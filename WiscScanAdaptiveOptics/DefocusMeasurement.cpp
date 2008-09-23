/**
 * The Defocus Measurement class. 
 */

#include "StdAfx.h"
#include "DefocusMeasurement.h"
#include "Logger.h"

#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <sstream>
#include <string>

/**
 * Constructor.
 */
DefocusMeasurement::DefocusMeasurement()
{
  // Prepare the random number generator.
  srand(time(0));

  firstIterationDone = false;

  // Prepare the optimization.
  prepareMeasurement();

  // Setup the SLM.
  SLMInstance = new SLMController;
  SLMInstance->initSLM();
  Sleep(500); // Give it a bit to get started.
}

/**
 * Close down (shut down the SLM).
 */
void DefocusMeasurement::closeDown()
{
  SLMInstance->closeSLM();
}


/**
 * Prepares the measurement (resets).
 * Neccessary because in the current model: WiscScan drives the evolution.
 */
void DefocusMeasurement::prepareMeasurement()
{
  isDone = false;
  As=0.0;
  Ad=0.50;
}

bool DefocusMeasurement::isFinished()
{
  return isDone;
}


/**
 * Generates a filename representing the current status of the
 * measurement.
 *
 * @return The file name.
 */
char *DefocusMeasurement::generateFileName()
{
  char fname[256] = "";
  sprintf(fname, "imgAs%.2fAd%.2f.dat", As, Ad);

  return strdup(fname);
}


double myAbs(double val)
{
  if (val < 0.0) {
    return -val;
  } else {
    return val;
  }
}

/**
 * Check if gain increase should be performed.
 *
 * @return True if gain should be increased, false otherwise.
 */
bool DefocusMeasurement::changeGain()
{
  if (Ad < 1.0) {
    return false;
  }


//  if ((myAbs(As-0.50) <= 0.05)|| (myAbs(As-0.70) <= 0.05) || (myAbs(As-1.00) <= 0.05) || (myAbs(As-1.20) <= 0.05)|| (myAbs(As-1.30) <= 0.05) || (myAbs(As-1.50) <= 0.05) || (myAbs(As-1.70) <= 0.05) || (myAbs(As-1.90) <= 0.05) || (myAbs(As-2.00) <= 0.05) || (myAbs(As-2.50) <= 0.05) || (myAbs(As-3.00) <= 0.05) || (myAbs(As-3.50) <= 0.05) || (myAbs(As-4.00) <= 0.05) || (myAbs(As-4.50) <= 0.05) || (myAbs(As-5.00) <= 0.05) ||
//    (myAbs(As-6.00) <= 0.05) || (myAbs(As-7.00) <= 0.05) || (myAbs(As-8.00) <= 0.05)) {
  if ((myAbs(As-0.20) <= 0.05)|| (myAbs(As-0.50) <= 0.05) || (myAbs(As-0.80) <= 0.05) || (myAbs(As-1.10) <= 0.05)|| (myAbs(As-1.30) <= 0.05) || (myAbs(As-1.50) <= 0.05) || (myAbs(As-1.70) <= 0.05) || (myAbs(As-1.90) <= 0.05) || (myAbs(As-2.00) <= 0.05)
    || (myAbs(As-0.40) <= 0.05)|| (myAbs(As-0.70) <= 0.05) || (myAbs(As-1.20) <= 0.05) 
    || (myAbs(As-2.20) <= 0.05) || (myAbs(As-2.50) <= 0.05) || (myAbs(As-3.00) <= 0.05) || (myAbs(As-3.50) <= 0.05) || (myAbs(As-4.00) <= 0.05) || (myAbs(As-4.50) <= 0.05) || (myAbs(As-5.00) <= 0.05) || (myAbs(As-5.50) <= 0.05)
    || (myAbs(As-6.00) <= 0.05) || (myAbs(As-6.50) <= 0.05) || (myAbs(As-7.00) <= 0.05) || (myAbs(As-7.50) <= 0.05) || (myAbs(As-8.00) <= 0.05) || (myAbs(As-8.50) <= 0.05) || (myAbs(As-9.00) <= 0.05)) {
    LOGME("increasing the gAin");
    return true;
  } else {
    return false;
  }
}


/**
 * Run one iteration.
 *
 * @param intensity The intensity of the last image that was captured.
 */
void DefocusMeasurement::iterateOnce(double intensity)
{
  std::ostringstream logSS;

  if (!firstIterationDone) {
    // The very first iteration.  Setup the SLM and return.
    firstIterationDone = true;

    // Prepare the next image on the SLM. 
    unsigned char *phaseData = new unsigned char [SLMSIZE*SLMSIZE];
    LOGME( "First iteration is now approximately done " )
    LOGME( "- Generating data for SLM " )
    SeidelSet.resetCoefficients();
    SeidelSet.setPower(Ad);
    SeidelSet.setSphericalAberration(As);
    SeidelSet.setSecondarySphericalAberration(0.0);
    SeidelSet.setTiltX(40);
    SeidelSet.setTiltY(40);
    SeidelSet.generateImageBufferForSLM(phaseData);
    LOGME( "- preparing data to be sent " )
    SLMInstance->receiveData(phaseData);
    LOGME( "- preparing sending... " )
    SLMInstance->sendToSLM(true);
    LOGME( "- done " )
    delete phaseData;

    Sleep(150); // Seems to take a bit longer occasionally.
    LOGME( "- SLM ready for action! " )
    return; // Return immediately.
  }

  //if ((-As-Ad+2) >= 1.5) {
  if (-Ad > 3.0) {
    As+=0.2;
//    As+=0.5;
    Ad=0.4;
    if (As > 11.50) {
      isDone = true;
    }
  } else {
    Ad -= 0.02;
//    Ad -= 0.5;
  }
//As=0.0;//XX
//Ad=80.0;//XXX

  if (!isDone) {
    // Prepare the next image on the SLM. 
    unsigned char *phaseData = new unsigned char [SLMSIZE*SLMSIZE];

    SeidelSet.resetCoefficients();
    SeidelSet.setPower(Ad);
    SeidelSet.setSphericalAberration(As);
    SeidelSet.setSecondarySphericalAberration(0.0);
    SeidelSet.setTiltX(40);
    SeidelSet.setTiltY(40);

    logSS.str("");
    logSS << "Sending SLM. As: " << As << " Ad: " << Ad << " using tilt " 
         << SeidelSet.getTiltX() << "/" << SeidelSet.getTiltY();
    LOGME( logSS.str() );
    
    SeidelSet.generateImageBufferForSLM(phaseData);
    SLMInstance->receiveData(phaseData);
    SLMInstance->sendToSLM(true);
    delete phaseData;

    Sleep(100); // Takes approx. 100 ms for SLM to "prepare".
    Sleep(1000);
  }
}

