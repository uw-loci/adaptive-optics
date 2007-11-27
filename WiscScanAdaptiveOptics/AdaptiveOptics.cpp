
#include "StdAfx.h"
#include "AdaptiveOptics.h"

/**
 * Constructor.
 */
AdaptiveOptics::AdaptiveOptics()
{
  optimizer = new GeneticOptimization();
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
    flag = 1;
    OutputDebugString("**********flag = 0 chang to 1 now, directly return ************");
    return 1;
  }
    
  flag = 0;

  count ++;
  sprintf(msgbuf, "*************count is %d\n*************", count);
  OutputDebugString(msgbuf);
  OutputDebugString("**********flag = 1 chang to 0 now, optimization ************");

  /*
   * Calculate the average intensity.
   */
  for (int i = 0; i < height; i++) {
    for ( int j = 0; j < width; j++) {
      sum = sum + buf[j + i * width]; 
    }
  }

  averageIntensity = sum / (width*height*3);
  sum = 0;

  OutputDebugString("**********CallBack_Wiscan************");
  OutputDebugString("*********************");
  OutputDebugString("*****the buffer and average are ******");

  sprintf(msgbuf, "*************the average intensity is %f, width is %d, height is %d, mode is %d\n*************",
      averageIntensity, width, height, mode);
  OutputDebugString(msgbuf);
	
  // Send the data to optimization module.
  optimizer->iterateOnce(averageIntensity);

  if (optimizer->isFinished())
    return 0; // Stop scanning.
  else
    return 1; // Continue scanning.
}

