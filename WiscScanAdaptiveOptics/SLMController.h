/**
 * SLMController.
 * This class represents the Boulder-Nonlinear Spatial Light Modulator (SLM), and handles
 * control of it and all communications with it.
 */

#include "slmproject.h"
#include "BNSBoard\BNSHardware.h"
#include "BNSBoard\BNSBoard.h"

class SLMController
{
  private:
    unsigned char *ImageData;
    unsigned char LUTBuf[500];
    int ImgWidth;
    int ImgHeight;
    unsigned short FrameNum;
    CBNSBoard *theBoard;
    bool bPowerOn;

  public:
    SLMController();
    bool initSLM();
    void receiveData(unsigned char *Data);
    bool sendToSLM(bool FrameNumchange);
    void closeSLM();
    void openSLM();
    void getFrameNumber(int *frnum);
    void getWidthHeight(int *Wid, int *Height);
    void readLUT(unsigned char *LUTBuf, CString lutfilename);
};
