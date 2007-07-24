#include "slmcontrol.h"


/*
#define INTENBUFFSIZE 20
#define SUMSZIE 5
#define SUMARRAYSIZE (INTENBUFFSIZE - AVERSZIE)
*/

//3 point fast search 

const double INITSTEP = 1;
const double MINSTEP = 0.05;


//GA 

const int ParallelNum = 8;
const int ArrayNum = (ParallelNum+1);
const int CrosNum  = ((ParallelNum-2)/2);
const int MutationNum = 2;

const double INIT_SHIFTX = 40;
const double INIT_SHIFTY = 40;
const double INIT_FOCUS = 0;

const double MAXNUMNOTB = 3;
//change the char to double
//256 - 51.2, 0 -- 255 ---->-25.6--- 25.4
const double SCALEXCH = 0.05;

const double DIFVALUE = 0;





class SlmCom;



class optim_ga{


    private :

        //parameter array, the last one save the best value
        char paracomax[ArrayNum], bestparacomax[CrosNum];
        char paracomay[ArrayNum], bestparacomay[CrosNum];
        char parastigx[ArrayNum], bestparastigx[CrosNum];
        char parastigy[ArrayNum], bestparastigy[CrosNum];
        char paraspher[ArrayNum], bestparaspher[CrosNum];


		//second order
		char para2ndspher[ArrayNum], best2ndparaspher[CrosNum];

        char paratrefoilx[ArrayNum], bestparatrefoilx[CrosNum];
		char paratrefoily[ArrayNum], bestparatrefoily[CrosNum];
		char para2ndastigx[ArrayNum], bestpara2ndastigx[CrosNum];
		char para2ndastigy[ArrayNum], bestpara2ndastigy[CrosNum];
		char para2ndcomax[ArrayNum], bestpara2ndcomax[CrosNum];
		char para2ndcomay[ArrayNum], bestpara2ndcomay[CrosNum];


        char finalcomax, finalcomay, finalstigx, finalstigy, finalspher, finalsndspher;
        
		char finaltrefx, finaltrefy, fianlsndastx, finalsndasty, finalsndcomx, finalsndcomy;


		//order number 
        int order;
		//the double type parameters;
		double *realpara;
		//best intensity value
        double finalbestinten;
		double aver_inten[ParallelNum];
		double best_inten[CrosNum];

		unsigned int curpoint; //indicate which of the intense is
        unsigned char *phasedata;
        char initflag;

        SlmCom *slm;
        
		int nfCount;

        double tilt_fix_func(double coma);
        double focus_fix_func(double spher, double astigx, double astigy);

		//private function for data type translation
        void DtoI(double * Dbuf, int length);

        void generate_buf();
        void send_buf();

    	double round256(double dat);
        void crossOverM();
		void crossfunc(char* bestpara, char* para);
	    void controlfun();
		int stopfun();
		void slmcon();
        void selectpara();
		void selefinalctpara();
		double parameterExchange(char para);
        void find3best();
		void intsort();
		void getpara();
        void exchpara(int i, int j);

    public:

		optim_ga(int num);
		~optim_ga();
		int optimization(double aver);
		void setpower(bool bPowerstatus);

};

