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
#include <SpotCam.h>
#include <string>
#include <ctime>

IMAQdxSession session;
char note_buf[512];
unsigned long width=-1, height=-1;
unsigned long roi_x=-1, roi_y=-1;
Image *img = NULL;
ImageInfo imgInfo;

unsigned short *spotPixelData = NULL;
int spotImageWidth = -1;

int driver_type;
const int DRIVER_TYPE_IMAQDX = 0;
const int DRIVER_TYPE_SPOTDLL = 1;

using namespace std;

/*
 * Currently we support cameras that are supported by 1) IMAQdx, 2) Spot DLL (Diagnostic Inc CCDs).
 * The user needs to specify which one to use, otherwise should be more or less transparent to the user.
 */

bool imaqdx_init_camera() {
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

bool spotdll_init_camera() {
	SPOT_DEVICE_STRUCT spotDevices[SPOT_MAX_DEVICES];
	int numDevices;
	int retVal;
	int selCameraIndex;


	// Find SPOT Devices.
	retVal = SpotFindDevices(spotDevices, &numDevices);
	if (retVal != SPOT_SUCCESS) {
		return false;
	}

	printf("Number of cameras: %d\n", numDevices);
	printf("SPOT CAMERAS:\n");
	for (int i = 0; i < numDevices; i++) {
		printf("%d: %s\n", i, spotDevices[i].szDescription);
	}

	selCameraIndex=0;

	// Tell the driver to use the first camera (easily changed if need to support more than one SPOT camera)
	retVal = SpotSetValue(SPOT_DRIVERDEVICENUMBER, &selCameraIndex);
	if (retVal != SPOT_SUCCESS) {
		printf("Failure in setting device number\n");
	}

	// Initialize the driver.
	retVal = SpotInit();
	if (retVal != SPOT_SUCCESS) {
		printf("Failure to initialize, retVal: %d\n", retVal);
		return false;
	}
	printf("Success in initializing\n");

	// Set exposure, and gain.
	int expTimeMS = 100;
	int gainValue = 16;

	// Disable autoexposure.
	BOOL spAutoExpose=false;
	retVal = SpotSetValue(SPOT_AUTOEXPOSE, &spAutoExpose);
	if (retVal != SPOT_SUCCESS) {
		printf("Failure to set auto expose, retVal: %d\n", retVal);
		return false;
	}

	// Calculate exposure parameter.
	long spExpTimePerIncNS;
	SpotGetValue(SPOT_EXPOSUREINCREMENT, &spExpTimePerIncNS);

	SPOT_EXPOSURE_STRUCT2 spExposure;
    memset(&spExposure, 0, sizeof(spExposure));
    spExposure.nGain = (short)gainValue;     // Set the gain.
	printf("Setting Gain: %d, Exp: %d ms\n", gainValue, expTimeMS);
	
	spExposure.dwExpDur = expTimeMS * (1000000 / spExpTimePerIncNS);
	retVal = SpotSetValue(SPOT_EXPOSURE2, &spExposure);
	if (retVal != SPOT_SUCCESS) {
		printf("Failure to initialize, retVal: %d\n", retVal);
		return false;
	}

	// Setting orientation: top to bottom.
	short spImageOrientation = 1; // Top-first.  (-1: bottom first)
	retVal = SpotSetValue(SPOT_IMAGEORIENTATION, &spImageOrientation);
	if (retVal != SPOT_SUCCESS) {
		printf("Failure to set image orientation, retVal: %d\n", retVal);
		return false;
	}

	return true;
}

/**
 * Initialize the camera.
 */
bool init_camera(int driver)
{
	driver_type = driver;
	switch (driver_type) {
		case DRIVER_TYPE_IMAQDX:
			return imaqdx_init_camera();
		case DRIVER_TYPE_SPOTDLL:
			return spotdll_init_camera();
		default:
			return false;
	}
}

int imaqdx_shutdown()
{
	IMAQdxStopAcquisition(session);
	imaqDispose(img);
	IMAQdxCloseCamera(session);
	return 1;
}

int spotdll_shutdown()
{
	printf("spotdll_shutdown\n");
	int retVal = SpotExit();
	return (retVal == SPOT_SUCCESS ? 1 : 0);
}

/**
 * Shut down, release memory and close the camera.
 *
 * @return 1 on success.
 */
int shutdown()
{
	switch (driver_type) {
		case DRIVER_TYPE_IMAQDX:
			return imaqdx_shutdown();
		case DRIVER_TYPE_SPOTDLL:
			return spotdll_shutdown();
		default:
			return -1;
	}
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


int imaqdx_capture_frame()
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

int spotdll_capture_frame()
{
	int retVal;
	short asTemp[2];

	retVal = SpotGetValue(SPOT_ACQUIREDIMAGESIZE, asTemp);
	if (retVal != SPOT_SUCCESS) {
		printf("Failure, retval: %d\n", retVal);
		return -1;
	}

	int imageWidth = asTemp[0];
	int imageHeight = asTemp[1];
	spotImageWidth = imageWidth;

	short bitDepth;
	bitDepth = 14;
	retVal = SpotSetValue(SPOT_BITDEPTH, &bitDepth);
	if (retVal != SPOT_SUCCESS) {
		printf("Failure, retval: %d\n", retVal);
		return -1;
	}

	retVal = SpotGetValue(SPOT_BITDEPTH, &bitDepth);
	if (retVal != SPOT_SUCCESS) {
		printf("Failure, retval: %d\n", retVal);
		return -1;
	}


	int bufferSize = imageWidth * imageHeight * sizeof(unsigned short);
	spotPixelData = (unsigned short *)malloc(bufferSize);

	printf("Calling Spot Get Image\n"); fflush(stdout);
	retVal = SpotGetImage(0, FALSE, 0, spotPixelData, NULL, NULL, NULL);
	printf("SpotGetImage: %d\n", retVal); fflush(stdout);


	return (retVal == SPOT_SUCCESS ? (imageWidth*imageHeight) : -1);
}

/**
 * Capture a single frame.
 *
 * @return On success, the size of the captured frame.  On failure, -1.
 */
int capture_frame()
{
	switch (driver_type) {
		case DRIVER_TYPE_IMAQDX:
			return imaqdx_shutdown();
		case DRIVER_TYPE_SPOTDLL:
			return spotdll_capture_frame();
		default:
			return -1;
	}
}

int imaqdx_set_roi(int x, int y, int dx, int dy)
{
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
		return -1;
	}

	return 1;
}

int spotdll_set_roi(int x, int y, int dx, int dy)
{
	RECT stRect;
	
	stRect.left = x; // Use a sub-area of the image sensor
	stRect.top = y;
	stRect.right = x+dx;
	stRect.bottom = y+dy;

	printf("SPOTDLL roi left: %d, top: %d, right: %d, bottom: %d\n", stRect.left, stRect.top, stRect.right, stRect.bottom);
	fflush(stdout);

	int retVal = SpotSetValue(SPOT_IMAGERECT, &stRect);
	if (retVal != SPOT_SUCCESS) {
		printf("Failure, setting ROI retval: %d\n", retVal); fflush(stdout);
		return -1;
	}
	
	return (retVal == SPOT_SUCCESS ? 1 : -1);
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

	switch (driver_type) {
		case DRIVER_TYPE_IMAQDX:
			return imaqdx_set_roi(x, y, dx, dy);
		case DRIVER_TYPE_SPOTDLL:
			return spotdll_set_roi(x,y,dx,dy);
		default:
			return -1;
	}
}

unsigned short imaqdx_get_frame_at_pos(int x, int y)
{
	unsigned char *pixel_address;

	pixel_address = (unsigned char *)imgInfo.imageStart + y*imgInfo.pixelsPerLine + x;
	int ch = *pixel_address;

	return ch;
}

unsigned short spotdll_get_frame_at_pos(int x, int y)
{
	unsigned short *pixel_address;
	if (spotPixelData == NULL)
		return 0;

	pixel_address = (unsigned short *)(spotPixelData + y*spotImageWidth + x);
	return *pixel_address;
}


/**
 * Return the frame value at location (x,y).
 *
 * @return The frame value at location (x,y).
 */
unsigned short get_frame_at_pos(int x, int y)
{
	switch (driver_type) {
		case DRIVER_TYPE_IMAQDX:
			return imaqdx_get_frame_at_pos(x, y);
		case DRIVER_TYPE_SPOTDLL:
			return spotdll_get_frame_at_pos(x, y);
		default:
			return -1;
	}

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

