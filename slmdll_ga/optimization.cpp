#include "stdafx.h"
#include "slmproject.h"
#include "optimization.h"





void exchange(double* a, double* b){
    double tem;
	tem = *a;
	*a = *b;
	*b = tem;
}



void exchange(char* a, char* b){
    char tem;
	tem = *a;
	*a = *b;
	*b = tem;
}


optim_ga::optim_ga(int num){
    order = num;

	realpara = new double[order];
	
    phasedata = new unsigned char[SLMSIZE*SLMSIZE];
    memset(phasedata, 0, sizeof(unsigned char)*SLMSIZE*SLMSIZE);

	char msgbuf[1024];


    for(int i=0; i < ParallelNum; i++){
		paracomax[i] = (rand() % 255) - 127;
		paracomay[i] = (rand() % 255) - 127;
	    parastigx[i] = (rand() % 255) - 127;
	    parastigy[i] = (rand() % 255) - 127;

	    paraspher[i] = (rand() % 255) - 127;
        para2ndspher[i] = (rand() % 255) - 127;

/*
		paracomax[i] = 0;
	    paracomay[i] = 0;
	    parastigx[i] = 0;
	    parastigy[i] = 0;


		
        paratrefoilx[i] = 0;
		paratrefoily[i] = 0;
		para2ndastigx[i] = 0;
		para2ndastigy[i] = 0;
		para2ndcomax[i] = 0;
		para2ndcomay[i] = 0;


/*
        paratrefoilx[i] = (rand() % 255) - 127;
		paratrefoily[i] = (rand() % 255) - 127;
		para2ndastigx[i] = (rand() % 255) - 127;
		para2ndastigy[i] = (rand() % 255) - 127;
		para2ndcomax[i] = (rand() % 255) - 127;
		para2ndcomay[i] = (rand() % 255) - 127;
*/

			
	sprintf(msgbuf, "**********init value: paracomax[%d] is %d, paracomay[%d] is %d, parastigx[%d] is %d, parastigy[%d] is %d,  paraspher[%d] is %d****************", i, paracomax[i], i, paracomay[i], i, parastigx[i], i,parastigy[i],i,paraspher[i]);
	OutputDebugString(msgbuf);

		
	}



	//slm control initialization
	slm = new SlmCom;
	slm->InitSlm();

	nfCount = 0;

	curpoint = 0;
	initflag = 0;
}

optim_ga::~optim_ga(){
	if(realpara != NULL)
        delete realpara;
	if(phasedata != NULL)
	    delete phasedata;
    if(slm != NULL)
		delete slm;
}


void optim_ga::exchpara(int i, int j){
        exchange(&paracomax[i], &paracomax[j]);
        exchange(&paracomay[i], &paracomay[j]);
        exchange(&parastigx[i], &parastigx[j]);
        exchange(&parastigy[i], &parastigy[j]);
        exchange(&paraspher[i], &paraspher[j]);


        exchange(&paratrefoilx[i], &paratrefoilx[j]);
        exchange(&paratrefoily[i], &paratrefoily[j]);
        exchange(&para2ndastigx[i], &para2ndastigx[j]);
        exchange(&para2ndastigy[i], &para2ndastigy[j]);
        exchange(&para2ndcomax[i], &para2ndcomax[j]);
        exchange(&para2ndcomay[i], &para2ndcomay[j]);
        exchange(&para2ndspher[i], &para2ndspher[j]);
}



void optim_ga::setpower(bool bPowerstatus){
	if(bPowerstatus == true){

		OutputDebugString("********open power*******");

	    slm->OpenSlm();
	}
	else{
		OutputDebugString("********close power*******");

	    //slm->CloseSlm();
	}
    return;
}


int optim_ga::optimization(double aver){
    
	char msgbuf[1024];

	sprintf(msgbuf, "***************just go in optimization function, current point is %d, aver is %f ****************", curpoint, aver);
	OutputDebugString(msgbuf);


    //the first value is invalid
	if(initflag == 0){
        curpoint = 0;
	    slmcon();
	    initflag = 1;
		return 1;
	}
    
/*
	if(curpoint == 0){
	    slmcon();
	    curpoint = 1;
		return 1;
	}
*/

	aver_inten[curpoint] = aver;
	
	if(curpoint >= ParallelNum-1){
		if(0 == stopfun()){
			//meet the stop condition
		    return 0;
		}
		else{
		    curpoint = 0;

			controlfun();
			slmcon();
			//pause several second and scan again.
        	return 1;
		}
	}
	else{
	    curpoint ++;
	    slmcon();
		//continue scan.
	    return 1;
	}
}


//return int: the return value refers the 
void optim_ga::controlfun(){
    //find the three best intensity
    char msgbuf[1024];
	
	find3best();

	sprintf(msgbuf, "***********aver_inten[0] is %f, aver_inten[1] is %f, aver_inten[2] is %f, aver_inten[3] is %f,aver_inten[4] is %f,aver_inten[5] is %f,aver_inten[6] is %f, aver_inten[7] is %f ****************", aver_inten[0], aver_inten[1], aver_inten[2],aver_inten[3],aver_inten[4],aver_inten[5],aver_inten[6],aver_inten[7]); 
	
	OutputDebugString(msgbuf);


	sprintf(msgbuf, "***********best_inten[0] is %f, best_inten[1] is %f, best_inten[2] is %f****************", best_inten[0], best_inten[1], best_inten[2]);

	OutputDebugString(msgbuf);




	if(best_inten[0] <= finalbestinten + DIFVALUE){
	    nfCount ++;
		if(best_inten[0] > finalbestinten){
			finalbestinten = best_inten[0];
			selefinalctpara();
		}
	}
	else{
	    finalbestinten = best_inten[0];
		nfCount = 0;
		selefinalctpara();
	}
	crossOverM();
}

//crossover and mutation operation
void optim_ga::crossOverM(){
    char msgbuf[1024];


	for(int i = 0; i < CrosNum; i++){
	    bestparacomax[i] = paracomax[i];
	    bestparacomay[i] = paracomay[i];
	    bestparastigx[i] = parastigx[i];
	    bestparastigy[i] = parastigy[i];
	    bestparaspher[i] = paraspher[i];


        bestparatrefoilx[i] = paratrefoilx[i];
		bestparatrefoily[i] = paratrefoily[i];
		bestpara2ndastigx[i] = para2ndastigx[i];
		bestpara2ndastigy[i] = para2ndastigy[i];
		bestpara2ndcomax[i] = para2ndcomax[i];
		bestpara2ndcomay[i] = para2ndcomay[i];
		best2ndparaspher[i] = para2ndspher[i];

	}

	sprintf(msgbuf, "\n***********bestparaspher[0] is %d, bestparaspher[1] is %d, bestparaspher[2] is %d***************\n", bestparaspher[0], bestparaspher[1], bestparaspher[2]); 
	OutputDebugString(msgbuf);

	sprintf(msgbuf, "\n***********best2ndparaspher[0] is %d, best2ndparaspher[1] is %d, best2ndparaspher[2] is %d***************\n", best2ndparaspher[0], best2ndparaspher[1], best2ndparaspher[2]); 
	OutputDebugString(msgbuf);



/*

	//mutation of the 2 best parameters
   	paracomax[0] = bestparacomax[0];
    paracomax[1] = (bestparacomax[0]&0xFC)|(~(bestparacomax[0]&0x03));
	//crossove of the 3 best parameters
    paracomax[2] = (bestparacomax[0]&0xF8)|(bestparacomax[1]&0x07);
	paracomax[3] = (bestparacomax[1]&0xF8)|(bestparacomax[0]&0x07);
    paracomax[4] = (bestparacomax[0]&0xF8)|(bestparacomax[2]&0x07);
	paracomax[5] = (bestparacomax[2]&0xF8)|(bestparacomax[0]&0x07);

	paracomax[6] = -bestparacomax[0];
	paracomax[7] = (bestparacomax[1]&0x7F)|(~(bestparacomax[1]&0x80));


	//mutation of the 2 best parameters
	paracomay[0] = bestparacomay[0];
    paracomay[1] = (bestparacomay[0]&0xFC)|(~(bestparacomay[0]&0x03));
	//crossove of the 3 best parameters
    paracomay[2] = (bestparacomay[0]&0xF8)|(bestparacomay[1]&0x07);
	paracomay[3] = (bestparacomay[1]&0xF8)|(bestparacomay[0]&0x07);
    paracomay[4] = (bestparacomay[0]&0xF8)|(bestparacomay[2]&0x07);
	paracomay[5] = (bestparacomay[2]&0xF8)|(bestparacomay[0]&0x07);

	paracomay[6] = -bestparacomay[0];
	paracomay[7] = (bestparacomay[1]&0x7F)|(~(bestparacomay[1]&0x80));

	//mutation of the 2 best parameters
	parastigx[0] = bestparastigx[0];
    parastigx[1] = (bestparastigx[0]&0xFC)|(~(bestparastigx[0]&0x03));
	//crossove of the 3 best parameters
    parastigx[2] = (bestparastigx[0]&0xF8)|(bestparastigx[1]&0x07);
	parastigx[3] = (bestparastigx[1]&0xF8)|(bestparastigx[0]&0x07);
    parastigx[4] = (bestparastigx[0]&0xF8)|(bestparastigx[2]&0x07);
	parastigx[5] = (bestparastigx[2]&0xF8)|(bestparastigx[0]&0x07);

	parastigx[6] = -bestparastigx[0];
	parastigx[7] = (bestparastigx[1]&0x7F)|(~(bestparastigx[1]&0x80));

	//mutation of the 2 best parameters
	parastigy[0] = bestparastigy[0];
    parastigy[1] = (bestparastigy[0]&0xFC)|(~(bestparastigy[0]&0x03));
	//crossove of the 3 best parameters
    parastigy[2] = (bestparastigy[0]&0xF8)|(bestparastigy[1]&0x07);
	parastigy[3] = (bestparastigy[1]&0xF8)|(bestparastigy[0]&0x07);
    parastigy[4] = (bestparastigy[0]&0xF8)|(bestparastigy[2]&0x07);
	parastigy[5] = (bestparastigy[2]&0xF8)|(bestparastigy[0]&0x07);

	parastigy[6] = -bestparastigy[0];
	parastigy[7] = (bestparastigy[1]&0x7F)|(~(bestparastigy[1]&0x80));
	

	//mutation of the 2 best parameters

	
	paraspher[0] = bestparaspher[0];
    paraspher[1] = (bestparaspher[0]&0xFC)|(~(bestparaspher[0]&0x03));
	//crossove of the 3 best parameters
    paraspher[2] = (bestparaspher[0]&0xF8)|(bestparaspher[1]&0x07);
	paraspher[3] = (bestparaspher[1]&0xF8)|(bestparaspher[0]&0x07);
    paraspher[4] = (bestparaspher[0]&0xF8)|(bestparaspher[2]&0x07);
	paraspher[5] = (bestparaspher[2]&0xF8)|(bestparaspher[0]&0x07);

	paraspher[6] = -bestparaspher[0];
	paraspher[7] = (bestparaspher[1]&0x7F)|(~(bestparaspher[1]&0x80));


    //////////////////////////////////////////////////////////////////////////////
    ////////////////second order////////////
	paratrefoilx[0] = bestparatrefoilx[0];
    paratrefoilx[1] = (bestparatrefoilx[0]&0xFC)|(~(bestparatrefoilx[0]&0x03));
	//crossove of the 3 best parameters
    paratrefoilx[2] = (bestparatrefoilx[0]&0xF8)|(bestparatrefoilx[1]&0x07);
	paratrefoilx[3] = (bestparatrefoilx[1]&0xF8)|(bestparatrefoilx[0]&0x07);
    paratrefoilx[4] = (bestparatrefoilx[0]&0xF8)|(bestparatrefoilx[2]&0x07);
	paratrefoilx[5] = (bestparatrefoilx[2]&0xF8)|(bestparatrefoilx[0]&0x07);
	paratrefoilx[6] = -bestparatrefoilx[0];
	paratrefoilx[7] = (bestparatrefoilx[1]&0x7F)|(~(bestparatrefoilx[1]&0x80));



	paratrefoily[0] = bestparatrefoily[0];
    paratrefoily[1] = (bestparatrefoily[0]&0xFC)|(~(bestparatrefoily[0]&0x03));
	//crossove of the 3 best parameters
    paratrefoily[2] = (bestparatrefoily[0]&0xF8)|(bestparatrefoily[1]&0x07);
	paratrefoily[3] = (bestparatrefoily[1]&0xF8)|(bestparatrefoily[0]&0x07);
    paratrefoily[4] = (bestparatrefoily[0]&0xF8)|(bestparatrefoily[2]&0x07);
	paratrefoily[5] = (bestparatrefoily[2]&0xF8)|(bestparatrefoily[0]&0x07);
	paratrefoily[6] = -bestparatrefoily[0];
	paratrefoily[7] = (bestparatrefoily[1]&0x7F)|(~(bestparatrefoily[1]&0x80));


	para2ndastigx[0] = bestpara2ndastigx[0];
    para2ndastigx[1] = (bestpara2ndastigx[0]&0xFC)|(~(bestpara2ndastigx[0]&0x03));
	//crossove of the 3 best parameters
    para2ndastigx[2] = (bestpara2ndastigx[0]&0xF8)|(bestpara2ndastigx[1]&0x07);
	para2ndastigx[3] = (bestpara2ndastigx[1]&0xF8)|(bestpara2ndastigx[0]&0x07);
    para2ndastigx[4] = (bestpara2ndastigx[0]&0xF8)|(bestpara2ndastigx[2]&0x07);
	para2ndastigx[5] = (bestpara2ndastigx[2]&0xF8)|(bestpara2ndastigx[0]&0x07);
	para2ndastigx[6] = -bestpara2ndastigx[0];
	para2ndastigx[7] = (bestpara2ndastigx[1]&0x7F)|(~(bestpara2ndastigx[1]&0x80));


	para2ndastigy[0] = bestpara2ndastigy[0];
    para2ndastigy[1] = (bestpara2ndastigy[0]&0xFC)|(~(bestpara2ndastigy[0]&0x03));
	//crossove of the 3 best parameters
    para2ndastigy[2] = (bestpara2ndastigy[0]&0xF8)|(bestpara2ndastigy[1]&0x07);
	para2ndastigy[3] = (bestpara2ndastigy[1]&0xF8)|(bestpara2ndastigy[0]&0x07);
    para2ndastigy[4] = (bestpara2ndastigy[0]&0xF8)|(bestpara2ndastigy[2]&0x07);
	para2ndastigy[5] = (bestpara2ndastigy[2]&0xF8)|(bestpara2ndastigy[0]&0x07);
	para2ndastigy[6] = -bestpara2ndastigy[0];
	para2ndastigy[7] = (bestpara2ndastigy[1]&0x7F)|(~(bestpara2ndastigy[1]&0x80));


	para2ndcomax[0] = bestpara2ndcomax[0];
    para2ndcomax[1] = (bestpara2ndcomax[0]&0xFC)|(~(bestpara2ndcomax[0]&0x03));
	//crossove of the 3 best parameters
    para2ndcomax[2] = (bestpara2ndcomax[0]&0xF8)|(bestpara2ndcomax[1]&0x07);
	para2ndcomax[3] = (bestpara2ndcomax[1]&0xF8)|(bestpara2ndcomax[0]&0x07);
    para2ndcomax[4] = (bestpara2ndcomax[0]&0xF8)|(bestpara2ndcomax[2]&0x07);
	para2ndcomax[5] = (bestpara2ndcomax[2]&0xF8)|(bestpara2ndcomax[0]&0x07);
	para2ndcomax[6] = -bestpara2ndcomax[0];
	para2ndcomax[7] = (bestpara2ndcomax[1]&0x7F)|(~(bestpara2ndcomax[1]&0x80));


	para2ndcomay[0] = bestpara2ndcomay[0];
    para2ndcomay[1] = (bestpara2ndcomay[0]&0xFC)|(~(bestpara2ndcomay[0]&0x03));
	//crossove of the 3 best parameters
    para2ndcomay[2] = (bestpara2ndcomay[0]&0xF8)|(bestpara2ndcomay[1]&0x07);
	para2ndcomay[3] = (bestpara2ndcomay[1]&0xF8)|(bestpara2ndcomay[0]&0x07);
    para2ndcomay[4] = (bestpara2ndcomay[0]&0xF8)|(bestpara2ndcomay[2]&0x07);
	para2ndcomay[5] = (bestpara2ndcomay[2]&0xF8)|(bestpara2ndcomay[0]&0x07);
	para2ndcomay[6] = -bestpara2ndcomay[0];
	para2ndcomay[7] = (bestpara2ndcomay[1]&0x7F)|(~(bestpara2ndcomay[1]&0x80));


	para2ndspher[0] = best2ndparaspher[0];
    para2ndspher[1] = (best2ndparaspher[0]&0xFC)|(~(best2ndparaspher[0]&0x03));
	//crossove of the 3 best parameters
    para2ndspher[2] = (best2ndparaspher[0]&0xF8)|(best2ndparaspher[1]&0x07);
	para2ndspher[3] = (best2ndparaspher[1]&0xF8)|(best2ndparaspher[0]&0x07);
    para2ndspher[4] = (best2ndparaspher[0]&0xF8)|(best2ndparaspher[2]&0x07);
	para2ndspher[5] = (best2ndparaspher[2]&0xF8)|(best2ndparaspher[0]&0x07);
	para2ndspher[6] = -best2ndparaspher[0];
	para2ndspher[7] = (best2ndparaspher[1]&0x7F)|(~(best2ndparaspher[1]&0x80));
*/

    crossfunc(bestparacomax, paracomax);
	crossfunc(bestparacomay, paracomay);
	crossfunc(bestparastigx, parastigx);
	crossfunc(bestparastigy, parastigy);
	crossfunc(bestparaspher, paraspher);

	crossfunc(bestparatrefoilx, paratrefoilx);
	crossfunc(bestparatrefoily, paratrefoily);
	crossfunc(bestpara2ndastigx, para2ndastigx);
	crossfunc(bestpara2ndastigy, para2ndastigy);
	crossfunc(bestpara2ndcomax, para2ndcomax);
	crossfunc(bestpara2ndcomay, para2ndcomay);
	crossfunc(best2ndparaspher, para2ndspher);


	for(i = 0; i < 8; i++){
	    sprintf(msgbuf, "\n***********paraspher[%d] is %d, para2ndspher[%d] is %d***************\n", i, paraspher[i], i, para2ndspher[i]); 
		OutputDebugString(msgbuf);
	}

}


void optim_ga::crossfunc(char* bestpara, char* para){
    *para = ((*bestpara) & 0xFE)|(~(*bestpara) & 0x01);
	*(para+1) = ((*bestpara) & 0xFC)|(~(*bestpara) & 0x03);
    *(para+2) = ((*bestpara) & 0xF8)|((*(bestpara+1)) & 0x07);
	*(para+3) = ((*(bestpara+1)) & 0xF8)|((*bestpara) & 0x07);
    *(para+4) = ((*bestpara) & 0xF8)|((*(bestpara+2)) & 0x07);
	*(para+5) = ((*(bestpara+2)) & 0xF8)|((*bestpara) & 0x07);
	*(para+6) = ((*(bestpara)) & 0x7F)|(~(*(bestpara)) & 0x80);
	*(para+7) = -(*bestpara);
    return;
}



void optim_ga::find3best(){
	intsort();
	for(int j = 0; j < 3; j++){
		best_inten[j] = aver_inten[j];
		for(int i = j+1; i < ParallelNum; i++){
            if(aver_inten[i] != aver_inten[j]){
                if(i != (j + 1)){
					aver_inten[j+1] = 0;
				    exchange(&aver_inten[i], &aver_inten[j+1]);
                    exchpara(i, j+1);
				}
				break;
			}
		}
	}
}



void optim_ga::intsort(){
	for(int j = 0; j < ParallelNum-1; j++){
	    for(int i = j+1; i < ParallelNum; i++){
            if(aver_inten[i] > aver_inten[j]){
				exchange(&aver_inten[i], &aver_inten[j]);
                exchpara(i, j);
			}
		}
	}
}



void optim_ga::slmcon(){
	selectpara();
	generate_buf();
    send_buf();
}

void optim_ga::selefinalctpara(){
	finalcomax = paracomax[0];
	finalcomay = paracomay[0];
	finalstigx = parastigx[0];
	finalstigy = parastigy[0];
	finalspher = paraspher[0];

    //////////////////////////////////////////////////////
	finaltrefx = paratrefoilx[0];
	finaltrefy = paratrefoily[0];
	fianlsndastx = para2ndastigx[0];
	finalsndasty = para2ndastigy[0];
	finalsndcomx = para2ndcomax[0];
	finalsndcomy = para2ndcomay[0];
	finalsndspher = para2ndspher[0];

}


void optim_ga::selectpara(){
	paracomax[ArrayNum-1] = paracomax[curpoint];
	paracomay[ArrayNum-1] = paracomay[curpoint];
	parastigx[ArrayNum-1] = parastigx[curpoint];
	parastigy[ArrayNum-1] = parastigy[curpoint];
	paraspher[ArrayNum-1] = paraspher[curpoint];


    ///////////////////////////////////////////////////
	paratrefoilx[ArrayNum-1] = paratrefoilx[curpoint];
	paratrefoily[ArrayNum-1] = paratrefoily[curpoint];
	para2ndastigx[ArrayNum-1] = para2ndastigx[curpoint];
	para2ndastigy[ArrayNum-1] = para2ndastigy[curpoint];
	para2ndcomax[ArrayNum-1] = para2ndcomax[curpoint];
	para2ndcomay[ArrayNum-1] = para2ndcomay[curpoint];

    para2ndspher[ArrayNum-1] = para2ndspher[curpoint];


}


//return 0: stop
//return 2: scan and close the shutter for 3 seconds and scan again.

int optim_ga::stopfun(){
    char msgbuf[1024];
	
	sprintf(msgbuf, "*********nfCount is %d",nfCount); 
	
	OutputDebugString(msgbuf);


	if(nfCount > MAXNUMNOTB){

		paracomax[ArrayNum-1] = finalcomax;
      	paracomay[ArrayNum-1] = finalcomay;
	    parastigx[ArrayNum-1] = finalstigx;
	    parastigy[ArrayNum-1] = finalstigy;
	    paraspher[ArrayNum-1] = finalspher;

		///////////////////////////////second order/////////////////////
	    paratrefoilx[ArrayNum-1] = finaltrefx;
   	    paratrefoily[ArrayNum-1] = finaltrefy;
	    para2ndastigx[ArrayNum-1] = fianlsndastx;
	    para2ndastigy[ArrayNum-1] = finalsndasty;
     	para2ndcomax[ArrayNum-1] = finalsndcomx;
    	para2ndcomay[ArrayNum-1] = finalsndcomy;

        para2ndspher[ArrayNum-1] = finalsndspher;



    	generate_buf();
        send_buf();


    	sprintf(msgbuf, "\n*********finalcomax is %d, finalcomay is %d, finalstigx is %d, finalstigy is %d, finalspher is %d, finalsndspher is %d", finalcomax, finalcomay,finalstigx,finalstigy,finalspher, finalsndspher); 
		OutputDebugString(msgbuf);
	    sprintf(msgbuf, "\n*********finaltrefx is %d, finaltrefy is %d, fianlsndastx is %d, finalsndasty is %d, finalsndcomx is %d, finalsndcomy is %d", finaltrefx, finaltrefy, fianlsndastx, finalsndasty, finalsndcomx, finalsndcomy);
		OutputDebugString(msgbuf);


        initflag = 0;
	    return 0;
	}
    return 1;
}







void optim_ga::DtoI(double * Dbuf, int length)
{
	unsigned char temp;
	int i;
#ifdef DEBUG_OUTPUT
    int tp, tp2;
    FILE *pfold;
	pfold = fopen("c:/slmcontrol/dumpout/VC_output_beforeD2I.txt","wt");
    for (tp = 0; tp < 512; tp ++)
	{
		fprintf(pfold, "\n");
		for (tp2 = 0; tp2 < 512; tp2++)
		fprintf(pfold, "%f, ", Dbuf[tp*512 + tp2]);
	}
	fclose(pfold);
#endif

	        
    for (i = 0; i < SLMSIZE*SLMSIZE; i++)
    {
        Dbuf[i] = round256(Dbuf[i]*256);
        if(Dbuf[i] < 0)
        {
            Dbuf[i] = Dbuf[i] + 256;
        }
    } 

	for (i = 0; i< length; i ++)
	{
		temp = unsigned char(*(Dbuf+i));
		if ((*(Dbuf+i) - temp) < 0.5)
            *(phasedata+i) = temp;
		else
            *(phasedata+i) = temp + 1;
	}
}


void optim_ga::send_buf(){

	bool framechange = 0;

#ifdef DEBUG_OUTPUT
    int tp, tp2;
    FILE *pfold;
	pfold = fopen("c:/slmcontrol/dumpout/VC_output_aftergen.txt","wt");
    for (tp = 0; tp < 512; tp ++)
	{
		fprintf(pfold, "\n");
		for (tp2 = 0; tp2 < 512; tp2++)
		fprintf(pfold, "%d, ", phasedata[tp*512 + tp2]);
	}
	fclose(pfold);
#endif

	slm->receiveData(phasedata);
	slm->SendtoDlm(framechange);
	return;
}







void optim_ga::generate_buf(){
    //
        double x, y, Radius;
        double divX, divY, XSquPlusYSqu, divXSqu, divYSqu, XPYSquSqu;
        double term1, term2, term3, term4, term5, term6, term7, term8, term9;

        double Piston, XTilt, YTilt, Power, AstigOne, AstigTwo, ComaX, ComaY, PrimarySpherical;
		double total;

		////////////////////second order////////////////////////
		double term10, term11, term12, term13, term14, term15, term16;
        double TrefoilX, TrefoilY, SecondaryAstigX, SecondaryAstigY, SecondaryComaX, SecondaryComaY, SecondarySpherical;


		char msgbuf[1024];
		
		//int defocusbins, stigxbins, stigybins, comaxbins, comaybins, speribins;
        
        int ActSize, start, end;
		double *zern = new double[SLMSIZE*SLMSIZE];
        memset(zern, 0, sizeof(double)*SLMSIZE*SLMSIZE);

        getpara();

        Piston = realpara[0]; XTilt = realpara[1]; YTilt = realpara[2]; Power = realpara[3];
        AstigOne = realpara[4]; AstigTwo = realpara[5]; ComaX = realpara[6]; ComaY = realpara[7];
        PrimarySpherical = realpara[8]; 
		
		/////////////////////////////second order///////////////
		TrefoilX = realpara[9];
		TrefoilY =  realpara[10];
		SecondaryAstigX =  realpara[11];
		SecondaryAstigY =  realpara[12];
		SecondaryComaX =  realpara[13];
		SecondaryComaY =  realpara[14];

		SecondarySpherical = realpara[15];


        

        sprintf(msgbuf, "*************Piston %f, XTilt is %f, YTilt is %f, Power is %f, SLMSIZE is %d\n************", Piston, XTilt, YTilt, Power, SLMSIZE);	

        OutputDebugString(msgbuf);		

        sprintf(msgbuf, "*************AstigOne %f, AstigTwo is %f, ComaX is %f, ComaY is %f, PrimarySpherical is %f, Power is %f\n************", AstigOne, AstigTwo, ComaX, ComaY, PrimarySpherical, Power);	

        OutputDebugString(msgbuf);		

        sprintf(msgbuf, "*************TrefoilX %f, TrefoilY is %f, SecondaryAstigX is %f, SecondaryAstigY is %f, SecondaryComaX is %f, SecondaryComaY is %f, SecondarySpherical is %f\n************", TrefoilX, TrefoilY, SecondaryAstigX, SecondaryAstigY, SecondaryComaX, SecondaryComaY, SecondarySpherical);	

        OutputDebugString(msgbuf);		


        ActSize = SLMSIZE;
        
        Radius = ActSize*300/512;
        
        start = (SLMSIZE - ActSize)/2;
        end = start + ActSize;
        
	    y = ActSize/2;
        
    	for(int row = start; row < end; row++){
		    //reset x
	        x = -(ActSize/2);
            
	        for(int col = start; col < end; col++){
		    //build some terms that are repeated through the equations
		        divX = x/Radius;
		        divY = y/Radius;
		        XSquPlusYSqu = divX*divX + divY*divY;
		        XPYSquSqu = XSquPlusYSqu*XSquPlusYSqu;
		        divXSqu = divX*divX;
		        divYSqu = divY*divY;
		        //figure out what each term in the equation is
		        term1 = (Piston/2);
		        term2 = (XTilt/2)*divX;
		        term3 = (YTilt/2)*divY;
		        term4 = (Power/2)*(2*XSquPlusYSqu - 1);
		        term5 = (AstigOne/2)*(divXSqu - divY*divY);
		        term6 = (AstigTwo/2)*(2*divX*divY);
		        term7 = (ComaX/2)*(3*divX*XSquPlusYSqu - 2*divX);
	        	term8 = (ComaY/2)*(3*divY*XSquPlusYSqu - 2*divY);
	        	term9 = (PrimarySpherical/2)*(1 - 6*XSquPlusYSqu + 6*XPYSquSqu);


				///////////////////////second order///////////////////////////////////
	        	term10 = (TrefoilX/2)*(divXSqu*divX - 3*divX*divYSqu);
		        term11 = (TrefoilY/2)*(3*divXSqu*divY - divYSqu*divY);
		        term12 = (SecondaryAstigX/2)*(3*divYSqu - 3*divXSqu + 4*divXSqu*XSquPlusYSqu - 4*divYSqu*XSquPlusYSqu);
		        term13 = (SecondaryAstigY/2)*(8*divX*divY*XSquPlusYSqu - 6*divX*divY);
		        term14 = (SecondaryComaX/2)*(3*divX - 12*divX*XSquPlusYSqu + 10*divX*XPYSquSqu);
		        term15 = (SecondaryComaY/2)*(3*divY - 12*divY*XSquPlusYSqu + 10*divY*XPYSquSqu);
				term16 = (SecondarySpherical/2)*(12*XSquPlusYSqu - 1 - 30*XPYSquSqu + 20*XSquPlusYSqu*XPYSquSqu);


                total = term1 + term2 + term3 + term4 + term5 + term6 + term7 + term8 + term9;

				///////////////second order//////////////
				total = total + term10 + term11 + term12 + term13 + term14 + term15 + term16;

	    	    zern[row*SLMSIZE+col] = (total);
			    total = 0;
                x++;
            }
            y--;
        }
        
        DtoI(zern, SLMSIZE*SLMSIZE);
        delete zern;
		return;
    }




	double optim_ga::round256(double dat){
		if (dat > 0) {
		    while(dat >= 256){
                dat = dat - 256;
			}
		}
		else{
			while(dat <= -256){
			    dat = dat + 256;
			}
		}
        return dat;
	}


    void optim_ga::getpara(){
        realpara[0] = 0;
		realpara[4] = parameterExchange(parastigx[ArrayNum-1]);
		realpara[5] = parameterExchange(parastigy[ArrayNum-1]);
		realpara[6] = parameterExchange(paracomax[ArrayNum-1]);
		realpara[7] = parameterExchange(paracomay[ArrayNum-1]);

        realpara[1] = INIT_SHIFTX+tilt_fix_func(realpara[6]);
        realpara[2] = INIT_SHIFTY+tilt_fix_func(realpara[7]);

        realpara[8] = parameterExchange(paraspher[ArrayNum-1]);

		realpara[3] = INIT_FOCUS + focus_fix_func(realpara[8], realpara[4], realpara[5]);

		////////////////////second order

		realpara[9] = parameterExchange(paratrefoilx[ArrayNum-1]);
		realpara[10] = parameterExchange(paratrefoily[ArrayNum-1]);
		realpara[11] = parameterExchange(para2ndastigx[ArrayNum-1]);
		realpara[12] = parameterExchange(para2ndastigy[ArrayNum-1]);
		realpara[13] = parameterExchange(para2ndcomax[ArrayNum-1]);
		realpara[14] = parameterExchange(para2ndcomay[ArrayNum-1]);

		realpara[15] = parameterExchange(para2ndspher[ArrayNum-1]);

	}


    double optim_ga::parameterExchange(char para){
	    return (para)*SCALEXCH;
	}


	double optim_ga::tilt_fix_func(double coma){
        return 2*coma;
	}


    double optim_ga::focus_fix_func(double spher, double astigx, double astigy){
        return (3*spher + sqrt(astigx*astigx + astigy*astigy)/2);
	}
