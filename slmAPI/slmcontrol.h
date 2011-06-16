#include "slmproject.h"

#include "BNSBoard\BNSHardware.h"
#include "BNSBoard\BNSBoard.h"

class SlmCom
{
private:
	unsigned char *ImageData;
	unsigned char LUTBuf[500];
	int ImgWidth, ImgHeight;
	CBNSBoard*		theBoard;
	bool bPowerOn;
public:
	SlmCom();
	bool          InitSlm();

	// Gets data ready to be sent.
	void          receiveData(double *Data);

	// Write data to slm and change frame.
	bool          SendtoDlm(int frameNum);
	void          SelectFrame(int frameNum);
	void          CloseSlm();
	void          OpenSlm();
	//void          GetFramNum(int *frnum);
	void          GetWH(int *Wid, int *Height);
	void          readlut(unsigned char *LUTBuf, CString lutfilename);

};
