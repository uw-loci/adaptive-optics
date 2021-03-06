#include "stdafx.h"
#include "slmproject.h"
#include  "slmcontrol.h"

//classes


SlmCom::SlmCom()
{
	FrameNum = 0;
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

	memset(LUTBuf,0, 500*sizeof(unsigned char));
	readlut(LUTBuf, LUTFILE);


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
	//	LUT[i]=i;
    //
		LUT[i]=0;
	printf("\nThere is error when open lut file, lut is set to linear.\n");
	return;
}


void SlmCom::receiveData(unsigned char *Data)
{
#ifdef DEBUG_OUTPUT
    int tp, tp2;
    FILE *pfold;
	pfold = fopen("c:/slmcontrol/dumpout/VC_output0.txt","wt");
    for (tp = 0; tp < 512; tp ++)
	{
		fprintf(pfold, "\n");
		for (tp2 = 0; tp2 < 512; tp2++)
		fprintf(pfold, "%d, ", Data[tp*512 + tp2]);
	}
	fclose(pfold);
#endif

	ImageData = (unsigned char *) malloc(ImgWidth*ImgHeight);

	for(int i = 0; i< ImgWidth*ImgHeight; i++)
	{
		ImageData[i] = LUTBuf[(Data[i])%256];
	}
 

    return;
}

bool SlmCom::SendtoDlm(bool FrameNumchange)
{
	if (!theBoard->WriteFrameBuffer(FrameNum, ImageData))
	{
	    free(ImageData);
		return false;
	}


#ifdef DEBUG_OUTPUT
    int tp, tp2;
    FILE *pfold;
	pfold = fopen("c:/slmcontrol/dumpout/VC_output1.txt","wt");
    for (tp = 0; tp < 512; tp ++)
	{
		fprintf(pfold, "\n");
		for (tp2 = 0; tp2 < 512; tp2++)
		fprintf(pfold, "%d, ", ImageData[tp*512 + tp2]);
	}
	fclose(pfold);
#endif


    if (FrameNumchange == true)
	{
	    FrameNum ++;
	}
	free(ImageData);
	theBoard->SelectImage(FrameNum);

    return true;
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


void SlmCom::GetFramNum(int *frnum)
{
   *frnum = FrameNum;
   return;
}


void SlmCom::GetWH(int *Wid, int *Height)
{
    *Wid = ImgWidth;
	*Height = ImgHeight;
	return;
}