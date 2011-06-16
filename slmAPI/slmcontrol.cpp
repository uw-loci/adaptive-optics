
#include "slmcontrol.h"


SlmCom::SlmCom()
{
	ImgWidth = SLMSIZE;
	ImgHeight = SLMSIZE;
}


bool SlmCom::InitSlm()
{
	CString BoardName = "512A_SLM";
	unsigned short LC_Type = 1;
	unsigned short TrueFrames = 3;
	bool VerifyHardware;
	CBNSFactory boardFactory;
	

	theBoard = boardFactory.BuildBoard(BoardName, LC_Type, TrueFrames, &VerifyHardware);

	//if Verify hardware is false, then the wrong board class was built for
	//the hardware in the machine. So we need to build a different board class
	//all the other variables passed to the function remain the same
	if(!VerifyHardware)
	{
		//deconstruct the old board class, it is important to clean up what was
		//wrongly built
		delete theBoard;

		//assign the new board name to the BoardName variable
		BoardName = "256A_SLM";

		//build the new board class
		theBoard = boardFactory.BuildBoard(BoardName, LC_Type, TrueFrames, &VerifyHardware);
	}	

	//initialize that this program is not going to use continuous
	//downloads. Instead it will sequence through a series of 
	//pre-loaded images. 
	bool ContinuousDownload = true;
	theBoard->SetDownloadMode(ContinuousDownload);

	unsigned short FrameRate = 1000;
	unsigned short LaserDuty = 50;
	unsigned short TrueLaserGain = 255;
	unsigned short InverseLaserGain = 255;
    theBoard->SetRunParameters(FrameRate, LaserDuty, TrueLaserGain, InverseLaserGain);

    bPowerOn = (bool)theBoard->GetPower();
	if(bPowerOn != true)
	{
		bPowerOn =  true;
	    theBoard->SetPower(bPowerOn);
	}

	//establish our image dimensions based on some Board Spec parameters
	ImgWidth = theBoard->BoardSpec()->FrameWidth;
	ImgHeight = theBoard->BoardSpec()->FrameHeight;

      /*
	memset(LUTBuf,0, 500*sizeof(unsigned char));
	readlut(LUTBuf, LUTFILE);
      */

	return true;

}


void SlmCom::readlut(unsigned char *LUT, CString lutfilename)
{
	FILE *stream;
	int i, seqnum, ReturnVal, tmpLUT;

	//the LUT file
	stream = fopen(lutfilename,"r");
	if (stream != NULL)
	{
		//read in all 256 values
		for (i=0; i<256; i++)
		{
		   ReturnVal=fscanf(stream, "%d %d", &seqnum, &tmpLUT); 
		   if (ReturnVal!=2 || seqnum!=i || tmpLUT<0 || tmpLUT>255)
		   {
			   	fclose(stream);
				printf("\nThere is error in lut file, while reading lut.\n");
				break;
		   }
		   LUT[i] = (unsigned char) tmpLUT;
		}
		fclose(stream);
		return;
	}

	//if there was an error reading in the LUT, then default to a linear LUT

    for (i=0; i<256; i++)
	// linear
		LUT[i]=i;
    //
	//	LUT[i]=0;
	printf("\nThere is error when open lut file, lut is set to linear.\n");	return;
}


void SlmCom::receiveData(double *Data)
{  
	ImageData = (unsigned char *) malloc(ImgWidth*ImgHeight);

	for (int i = 0; i< ImgWidth*ImgHeight; i++)
	{    
		ImageData[i] = (unsigned char)Data[i];
	}

	return;
}

void dumpCtrlOutput(const char *debugText)
{
#ifdef DEBUG_OUTPUT
    FILE *pfold;

	pfold = fopen("c:/Gunnsteinn/debug/slmcontrol.txt","a+");
    fprintf(pfold, "%s\n", debugText);
	fclose(pfold);
#endif
}

// Write data to the selected frame.
bool SlmCom::SendtoDlm(int frameNum)
{
	dumpCtrlOutput("1. Writing frame buffer");
	if (!theBoard->WriteFrameBuffer(frameNum, ImageData))
	{
		free(ImageData);
		return false;
	}
	dumpCtrlOutput("2. Writing debug java output");
	dumpCtrlOutput("3. Frame number increase?");


	dumpCtrlOutput("4. Freeeing data");

	free(ImageData);

	//dumpCtrlOutput("5. Selecting the frame");
	//theBoard->SelectImage(FrameNum);

	dumpCtrlOutput("5. Returning");


	return true;
}

void SlmCom::SelectFrame(int frameNum)
{
	theBoard->SelectImage(frameNum);
}


void SlmCom::CloseSlm()
{
	bPowerOn =  false;
	theBoard->SetPower(bPowerOn);
	delete theBoard;
	theBoard = NULL;
	return;
}

void SlmCom::OpenSlm()
{
	if (theBoard == NULL)
	{
	    InitSlm();
	}
	return;
}


/*void SlmCom::GetFramNum(int *frnum)
{
  *frnum = FrameNum;
  return;
}*/


void SlmCom::GetWH(int *Wid, int *Height)
{
  *Wid = ImgWidth;
	*Height = ImgHeight;
	return;
}