#include "slmproject.h"
#include "com_slmcontrol_slmAPI.h"

#include "test.h"
slmAPI *Java_com_slmcontrol_slmAPI = new slmAPI;

JNIEXPORT void JNICALL Java_com_slmcontrol_slmAPI_sendData
  (JNIEnv *env, jobject obj, jdoubleArray dArray, jchar frame)
{
	jdouble* da = (jdouble *) malloc(SLMSIZE*SLMSIZE*sizeof(jdouble)); 
	env->GetDoubleArrayRegion(dArray, 0, SLMSIZE*SLMSIZE, da);
	if (frame == 64)
		Java_com_slmcontrol_slmAPI->powerOn();
	else if (frame == 65)
		Java_com_slmcontrol_slmAPI->powerOff();
	else
        Java_com_slmcontrol_slmAPI->sendData(da);
}