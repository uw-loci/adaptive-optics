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

	theCamera.GetCameraName(model,sizeof(model));
	theCamera.GetCameraVendor(vendor,sizeof(vendor));
	theCamera.GetCameraUniqueID(&ID);
	sprintf(note_buf, "Vendor: %s\r\nModel: %s\r\nUniqueID: %08X%08X",
		vendor,model,ID.HighPart,ID.LowPart);

	return true;
}

char *get_note()
{
	return strdup(note_buf);
}

int capture_frame()
{
	if (theCamera.AcquireImage() != CAM_SUCCESS) {
		printf("Error in acquiring an image");
		return NULL;
	}

	unsigned long framelen = 0;
	frame_buffer = theCamera.GetRawData(&framelen);
	buffer_length = framelen;

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

