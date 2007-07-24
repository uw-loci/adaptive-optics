#include "slmproject.h"

#include "BNSBoard\BNSHardware.h"
#include "BNSBoard\BNSBoard.h"

class SlmCom
{
private:
	unsigned char *ImageData;
	unsigned char LUTBuf[500];
	int ImgWidth, ImgHeight;
	unsigned short FrameNum;
	CBNSBoard*		theBoard;
	bool bPowerOn;
public:
	SlmCom();
	bool          InitSlm();
	void          receiveData(unsigned char *Data);
	bool          SendtoDlm(bool FrameNumchange);
	void          CloseSlm();
	void          OpenSlm();
	void          GetFramNum(int *frnum);
	void          GetWH(int *Wid, int *Height);
	void          readlut(unsigned char *LUTBuf, CString lutfilename);

};
