// slmdll2.cpp : Defines the initialization routines for the DLL.
//

#include "stdafx.h"
#include "Int_wiscan.h"

#include<stdio.h>

extern "C" {
__declspec(dllexport) int int_wiscan (double *buf, int width, int height, char mode);

__declspec(dllexport) bool initsml(bool bPowerstatus);

__declspec(dllexport) void test(void);
}

void test(void)
{
	FILE* fp = fopen("c:\a.txt", "w");
	fprintf(fp, "hello");
	fclose(fp);
}


int int_wiscan(double *buf, int width, int height, char mode) {
	return m_pWiscan->CallBack_Wiscan(buf, width, height,mode) ;
}

bool initsml(bool bPowerstatus) {
    return m_pWiscan->init_PhaseModulator(bPowerstatus);
}


Int_wiscan::Int_wiscan() {
     opt = new optim_ga(OPTIMORDER);
} 


bool Int_wiscan::init_PhaseModulator(bool bPowerstatus){
	    //send the power status to optimization module
	OutputDebugString("********set power*******");

	opt->setpower(bPowerstatus);
	return true;
}


// return 0, stop the scan or stop the communication. 
// return 1, continue scan. 
// return x, scan and close the shutter.

int Int_wiscan::CallBack_Wiscan(double *buf, int width, int height, char mode){
	//compute the average intensity 
    static double sum = 0;
    char msgbuf[1024];
    double averinten;
	static int flag = 0;
	static int count = 0;
 
	if(flag == 0){
	    flag = 1;
		OutputDebugString("**********flag = 0 chang to 1 now, directly return ************");
		return 1;
	}
    
	flag = 0;

	count ++;
    sprintf(msgbuf, "*************count is %d\n*************", count);
	OutputDebugString(msgbuf);		

	OutputDebugString("**********flag = 1 chang to 0 now, optimization ************");

    for(int i = 0; i < height;i ++){
         for( int j = 0; j < width; j ++){
            sum = sum + buf[j + i * width]; 
         }
	}

    averinten = sum / (width*height*3);

    sum = 0;


	OutputDebugString("**********CallBack_Wiscan************");
	OutputDebugString("*********************");
	OutputDebugString("*****the buffer and average are ******");

    sprintf(msgbuf, "*************the average intensity is %f, width is %d, height is %d, mode is %d\n*************", averinten, width, height, mode);
	OutputDebugString(msgbuf);		

	
	//send the data to optimization module
	return opt->optimization(averinten);



}

