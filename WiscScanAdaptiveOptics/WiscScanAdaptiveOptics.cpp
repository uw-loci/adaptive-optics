/**
 * WiscScanAdaptiveOptics.cpp: 
 * Defines the initialization routines for the DLL.
 * Also defines the main interface routines called by WiscScan.
 */

#include "StdAfx.h"


#include <afxdllx.h>

//#define new DEBUG_NEW

// The following symbol used to force inclusion of this module for _USRDLL
#ifdef _X86_
extern "C" { int _afxForceUSRDLL; }
#else
extern "C" { int __afxForceUSRDLL; }
#endif

static AFX_EXTENSION_MODULE WiscScanAdaptiveOpticsDLL = { NULL, NULL };

#include "AdaptiveOptics.h"
static AdaptiveOptics *AdaptiveOpticsFrontend;

#include "Logger.h"




/**
 * Initializes the library.
 */
extern "C"
BOOL WINAPI DllMain(HINSTANCE hInstance, DWORD dwReason, LPVOID /*lpReserved*/)
{
	if (dwReason == DLL_PROCESS_ATTACH)
	{

    AdaptiveOpticsFrontend = new AdaptiveOptics;

	TRACE0("WISCSCANADAPTIVEOPTICS.DLL Initializing!\n");
		
		// Extension DLL one-time initialization
    if (!AfxInitExtensionModule(WiscScanAdaptiveOpticsDLL, hInstance)) {
			return FALSE;
    }
	LOGME("Adaptive Optics Starting up!");

		
	}
	else if (dwReason == DLL_PROCESS_DETACH)
	{
		TRACE0("WISCSCANADAPTIVEOPTICS.DLL Terminating!\n");
		// Terminate the library before destructors are called
		AfxTermExtensionModule(WiscScanAdaptiveOpticsDLL);
		
    if (AdaptiveOpticsFrontend) {
			delete AdaptiveOpticsFrontend;
    }

	}

	return TRUE;
}

/* Redundant. */
// XXX/FIXME: Remove.
extern "C" __declspec(dllexport) void test(void)
{
  return;
}

/**
 * Initializes the SLM (spatial light modulator).
 */
extern "C" __declspec(dllexport) bool initsml(bool bPowerStatus) 
{
  LOGME("initsml called");
  return AdaptiveOpticsFrontend->initializePhaseModulator(bPowerStatus);
}

/**
 * Passes one round of imagery from WiscScan.
 */
extern "C" __declspec(dllexport) int int_wiscan(double *buf, int width, int height, char mode) 
{
//  LOGME("int_wiscan called");
  return AdaptiveOpticsFrontend->processImage(buf, width, height, mode);
}


