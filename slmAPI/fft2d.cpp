/*-------------------------------------------------------------------------
   Perform a 2D FFT inplace given a complex 2D array
   The direction dir, 1 for forward, -1 for reverse
   The size of the array (nx,ny)
   Return false if there are memory problems or
      the dimensions are not powers of 2
*/
#include "slmproject.h"
#include "fft2d.h"
//#include "test.h"


fft2dTrans::fft2dTrans()
{
	width = SLMSIZE;
	heigth = SLMSIZE;
	transdata  = alloc_2d_complex(heigth, width);
	return;
}

fft2dTrans::~fft2dTrans()
{
    free_2d_complex(transdata);
	return;
}


void fft2dTrans::getPhase(double *phasedata)
{
	int rowNum,colNum;
	for (rowNum = 0; rowNum < heigth; rowNum ++) {
		for (colNum = 0; colNum < width; colNum++) {
			phasedata[rowNum*heigth + colNum] = atan2(transdata[rowNum][colNum].imag, transdata[rowNum][colNum].real);

		}
	}
#ifdef DEBUG_SIGN
//////////////////////////////////////////////////////////////////////
	debug_outdata_double(SLMSIZE, "dfftphase.txt", phasedata);
//////////////////////////////////////////////////////////////////////
#endif
    return;
}


void fft2dTrans::getAmp(double *Ampdata)
{
	int rowNum,colNum;
	for (rowNum = 0; rowNum < heigth; rowNum ++) {
		for (colNum = 0; colNum < width; colNum++) {
			Ampdata[rowNum*heigth + colNum] = sqrt(transdata[rowNum][colNum].imag * transdata[rowNum][colNum].imag \
				+ transdata[rowNum][colNum].real * transdata[rowNum][colNum].real);
		}
	}
    return;
}



void fft2dTrans::Powerof2(int n,int *m, int*twopm)
{
	if (n == 0)
	return;

    int i = 0;
    while(1)
	{
		if (n == 1)
			break;
		i ++; 
		n = n>>1;
 	}
	*m = i;
    *twopm = 1<<(i);
	return;
}


COMPLEX ** fft2dTrans::alloc_2d_complex(int n1, int n2)
{
    COMPLEX ** ii, *i;
    int j;
    
    ii =  (COMPLEX **) malloc (sizeof(COMPLEX *) * n1);

    i =  (COMPLEX *) malloc (sizeof(COMPLEX) * n1 * n2);

    ii[0] = i;
    for (j = 1; j < n1; j++) {
        ii[j] = ii[j - 1] + n2;
    }
    return ii;
}


void fft2dTrans::free_2d_complex(COMPLEX ** comimage)
{
    free(comimage[0]);
	free(comimage);
	return;
}



void fft2dTrans::receivedata(double *data, int wid, int hts)
{
	int i, j;
	
	width = wid;
	heigth = hts;

    for (j=0;j<heigth;j++) {
       for (i=0;i<width;i++) {
		   transdata[j][i].real = data[j*width+i];
		   transdata[j][i].imag = 0;
	   }
	}
	
#ifdef DEBUG_SIGN
////////////////////////////////////////////////////////////////////////
	debug_outdata_complex(SLMSIZE, "receivecomp.txt", transdata);
////////////////////////////////////////////////////////////////////////
#endif
    return;
}



void fft2dTrans::fft2(int nx,int ny,int dir)
{
   int i,j;
   int m, twopm;
   double *real,*imag;

   /* Transform the rows */
   real = (double *) malloc (nx * sizeof(double));
   imag = (double *) malloc (nx * sizeof(double));

   Powerof2(nx,&m,&twopm);
   if (twopm != nx)
   return;

   for (j=0;j<ny;j++) {
      for (i=0;i<nx;i++) {
         real[i] = transdata[i][j].real;
         imag[i] = transdata[i][j].imag;
      }
      fft(dir,m,real,imag);
      for (i=0;i<nx;i++) {
         transdata[i][j].real = real[i];
         transdata[i][j].imag = imag[i];
      }
   }

   free(real);
   free(imag);

   /* Transform the columns */
   real = (double*) malloc (ny * sizeof(double));
   imag = (double*) malloc (ny * sizeof(double));

   Powerof2(ny,&m,&twopm);
   if (twopm != ny)
   return;

   for (i=0;i<nx;i++) {
      for (j=0;j<ny;j++) {
         real[j] = transdata[i][j].real;
         imag[j] = transdata[i][j].imag;
      }
      fft(dir,m,real,imag);
      for (j=0;j<ny;j++) {
         transdata[i][j].real = real[j];
         transdata[i][j].imag = imag[j];
      }
   }
   free(real);
   free(imag);

#ifdef DEBUG_SIGN
////////////////////////////////////////////////////////////////////////
	debug_outdata_complex(SLMSIZE, "afterfftcomp.txt", transdata);
////////////////////////////////////////////////////////////////////////
#endif
   return;
}

/*-------------------------------------------------------------------------
   This computes an in-place complex-to-complex FFT
   x and y are the real and imaginary arrays of 2^m points.
   dir =  1 gives forward transform
   dir = -1 gives reverse transform

     Formula: forward
                  N-1
                  ---
              1   \          - j k 2 pi n / N
      X(n) = ---   >   x(k) e                    = forward transform
              N   /                                n=0..N-1
                  ---
                  k=0

      Formula: reverse
                  N-1
                  ---
                  \          j k 2 pi n / N
      X(n) =       >   x(k) e                    = forward transform
                  /                                n=0..N-1
                  ---
                  k=0
*/


void fft2dTrans::fft(int dir,int m,double *x,double *y)
{
   long nn,i,i1,j,k,i2,l,l1,l2;
   double c1,c2,tx,ty,t1,t2,u1,u2,z;

   /* Calculate the number of points */
   nn = 1;
   for (i=0;i<m;i++)
      nn *= 2;

   /* Do the bit reversal */
   i2 = nn >> 1;
   j = 0;
   for (i=0;i<nn-1;i++) {
      if (i < j) {
         tx = x[i];
         ty = y[i];
         x[i] = x[j];
         y[i] = y[j];
         x[j] = tx;
         y[j] = ty;
      }
      k = i2;
      while (k <= j) {
         j -= k;
         k >>= 1;
      }
      j += k;
   }

   /* Compute the FFT */
   c1 = -1.0;
   c2 = 0.0;
   l2 = 1;
   for (l=0;l<m;l++) {
      l1 = l2;
      l2 <<= 1;
      u1 = 1.0;
      u2 = 0.0;
      for (j=0;j<l1;j++) {
         for (i=j;i<nn;i+=l2) {
            i1 = i + l1;
            t1 = u1 * x[i1] - u2 * y[i1];
            t2 = u1 * y[i1] + u2 * x[i1];
            x[i1] = x[i] - t1;
            y[i1] = y[i] - t2;
            x[i] += t1;
            y[i] += t2;
         }
         z =  u1 * c1 - u2 * c2;
         u2 = u1 * c2 + u2 * c1;
         u1 = z;
      }
      c2 = sqrt((1.0 - c1) / 2.0);
      if (dir == 1)
         c2 = -c2;
      c1 = sqrt((1.0 + c1) / 2.0);
   }

   /* Scaling for forward transform */
   if (dir == 1) {
      for (i=0;i<nn;i++) {
         x[i] /= (double)nn;
         y[i] /= (double)nn;
      }
   }

   return;
}

