#include "stdafx.h"
#include "Windows.h"
#include <iostream.h>
#include <fstream.h>
#include <math.h>
#include <memory.h>
#include <stdlib.h>
#include <stdio.h>


#define ZERNIKERAD 300
#define SLMSIZE  512
#define ZRN_MAX_LINE	300

#define pi 3.1415
#define LUTFILE ("C:/LUT_Files/test/slmnew71.lut")
//#define LUTFILE "linear.LUT"


//optimization 
//step size
#define STEPSIZE 0.1


#undef DEBUG_SIGN
//#define DEBUG_SIGN 1



#define DEBUG_OUTPUT