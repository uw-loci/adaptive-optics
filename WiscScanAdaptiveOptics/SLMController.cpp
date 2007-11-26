


#include "StdAfx.h"
#include "SLMController.h"


SLMController::SLMController()
{
  FrameNum = 0;
  ImgWidth = SLMSIZE;
  ImgHeight = SLMSIZE;
}

/**
 * Initializes the SLM.
 * 
 * @return True if the initialization was successful.
 */
bool SLMController::initSLM()
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
  
  // Load a LUT file.
  memset(LUTBuf,0, 500*sizeof(unsigned char));
  readLUT(LUTBuf, LUTFILE);
  
  return true;
}


/**
 * Loads a LUT file (linear...) into the LUT buffer.
 * If the file is not found, the LUT is set to linear.
 *
 * @param LUT The buffer into which the LUT file is loaded into.
 * @param lutfilename The path to the LUT file to be loaded.
 */
void SLMController::readLUT(unsigned char *LUT, CString lutfilename)
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
    LUT[i] = i; // Linear.
  }

  printf("\nThere is error when open LUT file, LUT is set to linear.\n");	
  return;
}


/**
 * Prepares the data for transmission.
 * Converts the data from linear using the LUT buffer.
 * Ready to be sent by calling sendToSLM.
 *
 * @param Data The data that is to be converted. 
 */
void SLMController::receiveData(unsigned char *Data)
{
  
  ImageData = (unsigned char *) malloc(ImgWidth*ImgHeight);
  
  for(int i = 0; i< ImgWidth*ImgHeight; i++)
  {
    ImageData[i] = LUTBuf[(Data[i])%256];
//    ImageData[i] = Data[i];
  }
  
  return;
}

/**
 * Sends the current phase data to the SLM.
 *
 * @param FrameNumchange If true switches the frame number.
 * @return True on success, False otherwise.
 */
bool SLMController::sendToSLM(bool FrameNumchange)
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
    FrameNum++;
  }

  free(ImageData);
  theBoard->SelectImage(FrameNum);
  
  return true;
}

/**
 * Close down and uninitialize the SLM.
 */
void SLMController::closeSLM()
{
  bPowerOn =  false;
  theBoard->SetPower(bPowerOn);
  delete theBoard;
  theBoard = NULL;
  return;
}

/**
 * Initialize the SLM.
 */
void SLMController::openSLM()
{
  if (theBoard == NULL)
  {
    initSLM();
  }
  return;
}

/**
 * Returns the currently used frame number.
 *
 * @param frameNum The frame number is returned through this parameter.
 */
void SLMController::getFrameNumber(int *frameNum)
{
  *frameNum = this->FrameNum;
  return;
}


/**
 * Returns the image width and height.
 *
 * @param Wid The width is returned through this variable.
 * @param Height The height is returned through this variable.
 */
void SLMController::getWidthHeight(int *Wid, int *Height)
{
  *Wid = ImgWidth;
  *Height = ImgHeight;
  return;
}

