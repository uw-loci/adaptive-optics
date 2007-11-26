#include "stdafx.h"
#include <iostream.h>
#include <fstream.h>
#include <math.h>
#include <memory.h>
#include <stdlib.h>
#include <stdio.h>
#include "cdib.h"

#define ZERNIKERAD 300
#define SLMSIZE  512
#define ZRN_MAX_LINE	300

#define pi 3.1415
#define LUTFILE "slm7041.LUT"
//#define LUTFILE "linear.lut"


//#define DEBUG_SIGN 1

#undef DEBUG_SIGN

#define DEBUG_OUTPUT