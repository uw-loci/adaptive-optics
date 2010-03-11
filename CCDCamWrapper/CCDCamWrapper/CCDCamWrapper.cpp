/**
 * Utility function library for communicating with a CCD camera.
 * Gunnsteinn Hall, 2/19/2010.
 * LOCI.
 */

#include "stdafx.h"

#include <windows.h>
#include <1394Camera.h>
#include <string>

C1394Camera theCamera;
unsigned char *frame_buffer = NULL;
int buffer_length = 0;
char note_buf[512];

using namespace std;

bool init_camera()
{
	char vendor[256],model[256];
	LARGE_INTEGER ID;

	int ret = theCamera.RefreshCameraList();
	int type;
	if(ret >= 0)
	{
		sprintf(note_buf, "CheckLink: Found %d Camera%s\n",ret,ret == 1 ? "" : "s");
	} else {
		sprintf(note_buf, "CheckLink: Error %08x Refreshing Camera List", GetLastError());
		return false;
	}

	if(theCamera.GetNumberCameras() < 1) {
		sprintf(note_buf, "No cameras available\n");
		return false;
	}

	// Pick the first camera.
	theCamera.SelectCamera(0);

	// Initialize.
	bool reset = FALSE;
	if(theCamera.InitCamera(reset) != CAM_SUCCESS)
	{
		sprintf(note_buf, "Error initializing camera\n");
		return false;
	}

	unsigned long width = -1, height = -1;
	theCamera.GetVideoFrameDimensions(&width, &height);

	theCamera.GetCameraName(model,sizeof(model));
	theCamera.GetCameraVendor(vendor,sizeof(vendor));
	theCamera.GetCameraUniqueID(&ID);
	sprintf(note_buf, "Vendor: %s\r\nModel: %s\r\nUniqueID: %08X%08X; width: %d, height: %d",
		vendor,model,ID.HighPart,ID.LowPart, width, height);



	 

	return true;
}

char *get_note()
{
	return strdup(note_buf);
}

int capture_frame()
{
	int buffersAllocated = 1;
	int maxTimeout = 200; // ms.
	if (theCamera.StartImageAcquisitionEx(buffersAllocated, maxTimeout, ACQ_START_VIDEO_STREAM)) {
		sprintf(note_buf, "Error starting image acquisition");
		return -1;
	}

	int ret = theCamera.AcquireImageEx(TRUE, NULL);
	if (ret != CAM_SUCCESS) {
		sprintf(note_buf, "Error in acquiring an image, ret: %d", ret);
		return -1;
	}

	unsigned long framelen = 0;
	unsigned char *tmp_buf = theCamera.GetRawData(&framelen);
	if (framelen > 0) {
		// XXX: add a function to drop this memory, once used.
		frame_buffer = (unsigned char *)malloc(framelen);
		memcpy(frame_buffer, tmp_buf, framelen);
	} else {
		frame_buffer = NULL;
	}

	buffer_length = framelen;

	if (theCamera.StopImageAcquisition()) {
		sprintf(note_buf, "Problem Stopping Image Acquisition");
		return -1;
	}

	return buffer_length;
}

unsigned char get_frame_at_pos(int index)
{
	return frame_buffer[index];
}




int test_me()
{
	return 3;
}

