
#include "StdAfx.h"
#include "AdaptiveOptics.h"
#include "Logger.h"

#include <iostream>
#include <fstream>
#include <sstream>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

/**
 * Constructor.
 */
AdaptiveOptics::AdaptiveOptics()
{
  //optimizer = new GeneticOptimization();
  measurand = new DefocusMeasurement();
} 


/**
 * Initializes the phase modulator.
 *
 * @param bPowerStatus True if the SLM power is to be turned on.
 * @return True if successful.
 */
bool AdaptiveOptics::initializePhaseModulator(bool bPowerStatus)
{
  //send the power status to optimization module
  OutputDebugString("********set power*******");

  //optimizer->setPower(bPowerStatus);
  return true;
}


void dumpSumBuffer(int count, double *buf, int width, int height)
{
  ofstream file;
  char fileName[512];
  std::ostringstream fileNameSS;

  sprintf(fileName, "c:/gunnsteinn/debug/%d.dat", count);
  file.open( fileName );
  
  file << "% Width: " << width << " Height: " << height << endl;
  for (int i = 0; i < height; i++) {
    for ( int j = 0; j < width; j++) {
      file << buf[j + i * width] << endl; 
    }
  }

  file.close();
}

/**
 * Processes one round of imagery from WiscScan.
 *
 * @param buf The image buffer.
 * @param width The width of the image.
 * @param height The height of the image.
 * @param mode The mode. XXX/FIXME: remove?
 * @return An integer whose value has the following meanings:
 *    0: Stop the scan or stop the communication. 
 *    1: Continue scan. 
 *    x: Scan and close the shutter.
 */
int AdaptiveOptics::processImage(double *buf, int width, int height, char mode)
{
  // Compute the average intensity.
  static double sum = 0;
  char msgbuf[1024];
  double averageIntensity;
  static int flag = 0;
  static int count = 0;
 
  if (flag == 0) { // GH: skips the rest every other time? why?
    /* IF NOT USED: WE ONLY GET 1/2 OF THE IMAGE!
     * (or does it only appear so on the screen?)
     */
    flag = 1;  // Now: skip only 1st time (stability?)
    OutputDebugString("**********flag = 0 chang to 1 now, directly return ************");
    return 1;
  }
//  flag = 0;

  count ++;
  sprintf(msgbuf, "*************count is %d\n*************", count);
  OutputDebugString(msgbuf);
  OutputDebugString("**********flag = 1 chang to 0 now, optimization ************");

  /*
   * Calculate the average intensity.
   */
  const double THRESHOLD = 2.0;
  sum = 0;
  for (int i = 0; i < height; i++) {
    for (int j = 0; j < width; j++) {
      if (buf[j + i*width] >= THRESHOLD) {
        sum = sum + buf[j + i * width];
      }
    }
  }

  // Dump the image data to file.
  dumpSumBuffer(count, buf, width, height);

  averageIntensity = sum / (width*height*3);

  //std::ostringstream logSS;
  //logSS << "sum: " << sum << " avg. int: " << averageIntensity << " width: " << width << " height: " << height;
  //LOGME( logSS.str() )
  //logSS.str("");


  OutputDebugString("**********CallBack_Wiscan************");
  OutputDebugString("*********************");
  OutputDebugString("*****the buffer and average are ******");

  sprintf(msgbuf, "*************the average intensity is %f, width is %d, height is %d, mode is %d\n*************",
      averageIntensity, width, height, mode);
  OutputDebugString(msgbuf);

  // Send the data to optimization module.
  //optimizer->iterateOnce(averageIntensity);
  measurand->iterateOnce(averageIntensity);

  //if (optimizer->isFinished()) {
  if (measurand->isFinished()) {
    return 0; // Stop scanning.
  } else {
    return 1; // Continue scanning.
  }
}

