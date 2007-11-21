
#include "StdAfx.h"
#include "SLMController.h"


SLMController::SLMController()
{
  FrameNum = 0;
  ImgWidth = SLMSIZE;
  ImgHeight = SLMSIZE;
}

bool SLMController::InitSLM()
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
  
  bPowerOn = theBoard->GetPower() == 1;
  if (bPowerOn != true)
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


void SLMController::readlut(unsigned char *LUT, CString lutfilename)
{
  FILE *stream;
  int i, seqnum, ReturnVal, tmpLUT;
  
  // The LUT file.
  stream = fopen(lutfilename,"r");
  if (stream != NULL)
  {
    // Read in all 256 values.
    for (i = 0; i < 256; i++)
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
  
  // If there was an error reading in the LUT, then default to a linear LUT.
  for (i = 0; i < 256; i++)
  {
    LUT[i]=i; // Linear.
  }
  //	LUT[i]=0;
  printf("\nThere is error when open lut file, lut is set to linear.\n");	return;
}


void SLMController::receiveData(unsigned char *Data)
{
  
  ImageData = (unsigned char *) malloc(ImgWidth*ImgHeight);
  
  for(int i = 0; i< ImgWidth*ImgHeight; i++)
  {
    /*     
    ImageData[i] = LUTBuf[(Data[i])%256];
    */
    ImageData[i] = Data[i];
  }
  
  return;
}

bool SLMController::SendtoDlm(bool FrameNumchange)
{
  if (!theBoard->WriteFrameBuffer(FrameNum, ImageData))
  {
    free(ImageData);
    return false;
  }
  
  
#ifdef DEBUG_OUTPUT
  int tp, tp2;
  FILE *pfold;
  pfold = fopen("c:/slmcontrol/dumpout/JAVA_output.txt","wt");
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

void SLMController::CloseSLM()
{
  bPowerOn =  false;
  theBoard->SetPower(bPowerOn);
  delete theBoard;
  theBoard = NULL;
  return;
}

void SLMController::OpenSLM()
{
  if (theBoard == NULL)
  {
    InitSLM();
  }
  return;
}


void SLMController::GetFrameNum(int *frameNum)
{
  *frameNum = this->FrameNum;
  return;
}


void SLMController::GetWH(int *Wid, int *Height)
{
  *Wid = ImgWidth;
  *Height = ImgHeight;
  return;
}

