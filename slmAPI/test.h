#include "slmcontrol.h"
#include "fft2d.h"

#ifdef DEBUG_SIGN
void debug_outdata(int size, CString filename, unsigned char *data);
void debug_outdata_double(int size, CString filename, double *data);
void debug_outdata_complex(int size, CString filename, COMPLEX **compdata);
#endif



class slmAPI
{
private:
	unsigned char *phasedata;
	//double *imagedata;
	fft2dTrans *fftcl;
	SlmCom *slm;
public:
	slmAPI();
	void sendData(double *Data);
	void DtoI(double * Dbuf, int length);
	void powerOn();
    void powerOff();
};
