/**
 * Utility function library for communicating with a CCD camera.
 * Migrated from the CMU CCD library to the NIIMAQdx library which has more options for faster image retrieval.
 * Gunnsteinn Hall, 2/28/2011.
 * LOCI.
 */

#include "stdafx.h"

#include <windows.h>
#include <NIIMAQdx.h>
#include <nivision.h>
#include <string>
#include <ctime>

IMAQdxSession session;
char note_buf[512];
unsigned long width=-1, height=-1;
unsigned long roi_x=-1, roi_y=-1;
Image *img = NULL;
ImageInfo imgInfo;

using namespace std;

/**
 * Initialize the camera.
 */
bool init_camera()
{
	const char *camName = "cam0";

	IMAQdxError status = IMAQdxOpenCamera(camName, IMAQdxCameraControlModeController, &session);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to open %s: %s", camName, errorMsg);
		return false;
	}

	char vendorName[IMAQDX_MAX_API_STRING_LENGTH], modelName[IMAQDX_MAX_API_STRING_LENGTH];
	width=-1, height=-1;
	IMAQdxGetAttribute(session, IMAQdxAttributeVendorName, IMAQdxValueTypeString, &vendorName);
	IMAQdxGetAttribute(session, IMAQdxAttributeModelName, IMAQdxValueTypeString, &modelName);
	IMAQdxGetAttribute(session,IMAQdxAttributeWidth, IMAQdxValueTypeU32, &width);
	IMAQdxGetAttribute(session,IMAQdxAttributeHeight, IMAQdxValueTypeU32, &height);

	sprintf(note_buf, "Vendor: %s\r\nModel: %s\r\nwidth: %d, height: %d",
		vendorName, modelName, width, height);
	
	/*
	status = IMAQdxConfigureAcquisition(session, 0, 1);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to open configure acquisition: %s", errorMsg);
		return false;
	}*/
	status = IMAQdxConfigureGrab(session);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to open configure grab: %s", errorMsg);
		return false;
	}

	img = imaqCreateImage(IMAQ_IMAGE_U8, 0);

	return true;
}

/**
 * Shut down, release memory and close the camera.
 *
 * @return 1 on success.
 */
int shutdown()
{
	IMAQdxStopAcquisition(session);
	imaqDispose(img);
	IMAQdxCloseCamera(session);
	return 1;
}

/**
 * Retrieve the note buffer, which typically contains an error message after an error is indicated.
 *
 * @return A copy of the note buffer.
 */
char *get_note()
{
	return strdup(note_buf);
}

/**
 * Capture a single frame.
 *
 * @return On success, the size of the captured frame.  On failure, -1.
 */
int capture_frame()
{
	/*
	IMAQdxError status = IMAQdxStartAcquisition(session);	
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to start acquisition: %s", errorMsg);
		return -1;
	}
	
	
	uInt32 bufNum;	
	status = IMAQdxGetImage(session, img, IMAQdxBufferNumberModeLast, -1, &bufNum);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to get image data: %s", errorMsg);
		return -1;
	}

	status = IMAQdxStopAcquisition(session);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to stop acquisition: %s", errorMsg);
		return -1;
	}*/

	uInt32 bufNum;	
	unsigned int waitForNextBuffer = 1; //use 1 to be 100% guaranteed.
	IMAQdxError status = IMAQdxGrab(session, img, waitForNextBuffer, &bufNum);

	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to grab image: %s", errorMsg);
		return -1;
	}
	
	imaqGetImageInfo(img, &imgInfo);
	int framelen = imgInfo.yRes * imgInfo.xRes;

	return framelen;
}

/**
 * Set the region of interest.
 * @param x The x offset of the upper left hand corner.
 * @param y The y offset of the upper left hand corner.
 * @param dx The width of the region.
 * @param dy The height of the region.
 *
 * @return On success, 1.  On failure, -1.
 */
int set_roi(int x, int y, int dx, int dy) {
	roi_x=x;
	roi_y=y;
	width=dx;
	height=dy;

	IMAQdxError status = IMAQdxUnconfigureAcquisition(session);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to unconfigure acquisition: %s", errorMsg);
		return -1;
	}
	

	status = IMAQdxSetAttribute(session,IMAQdxAttributeOffsetX, IMAQdxValueTypeU32, roi_x);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed set offset x: ", errorMsg);
		return -1;
	}

	status = IMAQdxSetAttribute(session,IMAQdxAttributeOffsetY, IMAQdxValueTypeU32, roi_y);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed set offset y: ", errorMsg);
		return -1;
	}

	status = IMAQdxSetAttribute(session,IMAQdxAttributeWidth, IMAQdxValueTypeU32, width);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed set width: ", errorMsg);
		return -1;
	}

	status = IMAQdxSetAttribute(session,IMAQdxAttributeHeight, IMAQdxValueTypeU32, height);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed set height: ", errorMsg);
		return -1;
	}

	/*
	status = IMAQdxConfigureAcquisition(session, 0, 1);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to reg configure acquisition: %s", errorMsg);
		return -1;
	}*/
	status = IMAQdxConfigureGrab(session);
	if (status != IMAQdxErrorSuccess) {
		char errorMsg[100];
		IMAQdxGetErrorString(status, errorMsg, 99);
		sprintf(note_buf, "Failed to open configure grab: %s", errorMsg);
		return false;
	}

	return 1;
}

/**
 * Return the frame value at location (x,y).
 *
 * @return The frame value at location (x,y).
 */
unsigned char get_frame_at_pos(int x, int y)
{
	unsigned char *pixel_address;

	pixel_address = (unsigned char *)imgInfo.imageStart + y*imgInfo.pixelsPerLine + x;
	int ch = *pixel_address;

	return ch;
}


/**
 * A simple test function to check if SWIG is working.
 *
 * @return 3.
 */
int test_me()
{
	return 3;
}

