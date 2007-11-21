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
    bool InitSLM();
    void receiveData(unsigned char *Data);
    bool SendtoDlm(bool FrameNumchange);
    void CloseSLM();
    void OpenSLM();
    void GetFrameNum(int *frnum);
    void GetWH(int *Wid, int *Height);
    void readlut(unsigned char *LUTBuf, CString lutfilename);
};
