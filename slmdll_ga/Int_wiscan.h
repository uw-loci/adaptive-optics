#include "optimization.h"


#define OPTIMORDER 16




//Int_wiscan m_class;

extern class Int_wiscan* m_pWiscan;



class optim_ga;


class Int_wiscan
{
private:
    optim_ga *opt; 
public:
    Int_wiscan();  
//CallBack_Wiscan, interface function for wiscan to transfer the intensity to optimation module.
//mode = 4, defocus, 9 = sphereical........
//in the first step, we just use defocus. mode = 4
// for buf, double * or char * is better ???????. if char is enough, buf is a little bit small.
// return: 0 - stop scan, 1 - scan, any other is error.

    int CallBack_Wiscan (double *buf, int width, int height, char mode);


//init_PhaseModulator, initial the phase modulator, reset after complete the optimaztion and close the //phasemodulator after experiment.
//bPowerstatus, 0 close the power of phase modulator, 1, keep or turn on the phase modulator
//return, false means error, ture means correct

    bool init_PhaseModulator(bool bPowerstatus);
};

