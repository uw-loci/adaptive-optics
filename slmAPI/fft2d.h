typedef struct {
	double real,imag;
	}COMPLEX;

class fft2dTrans
{
private:
	COMPLEX **transdata;
	int width, heigth;
	void          Powerof2(int n,int *m, int*twopm);
	void          fft(int dir,int m,double *x,double *y);
    COMPLEX **    alloc_2d_complex(int n1, int n2);
	void          free_2d_complex(COMPLEX ** comimage);
public:
	fft2dTrans();
	~fft2dTrans();
	void          receivedata(double *data, int nx, int ny);
	void          fft2(int nx,int ny,int dir);
	void          getPhase(double *phasedata);
	void          getAmp(double *Ampdata);
};

