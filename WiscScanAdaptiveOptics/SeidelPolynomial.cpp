
#include "StdAfx.h"
#include "SeidelPolynomial.h"
#include "SLMProject.h"

#include "Logger.h"
#include <sstream>

// Constructor.
SeidelPolynomial::SeidelPolynomial()
{
  resetCoefficients();
}


/**
 * Resets the SeidelPolynomial coefficients to default values.
 */
void SeidelPolynomial::resetCoefficients()
{
  setPiston(0.0);
  setSecondaryAstigmatismX(0.0);
  setSecondaryAstigmatismY(0.0);
  setPower(0.0);
  setAstigmatismX(0.0);
  setAstigmatismY(0.0);
  setComaX(0.0);
  setComaY(0.0);
  setSphericalAberration(0.0);
  setTrefoilX(0.0);
  setTrefoilY(0.0);
  setSecondaryComaX(0.0);
  setSecondaryComaY(0.0);
  setSecondarySphericalAberration(0.0);
  setTiltX(20.0);
  setTiltY(20.0);
  //setTiltX(0.0);
  //setTiltY(0.0);
}

/**
 * Round off to ensure dat is in range -256 to 256.
 * GH: (replacable with modulus opration?)
 */
double round256b(double dat)
{
  if (dat > 0) {
    while (dat >= 256){
      dat = dat - 256;
    }
  }
  else {
    while (dat <= -256) {
      dat = dat + 256;
    }
  }
  
  return dat;
}


/**
 * ???
 * Converts double-valued matrix to an unsigned 255 bit matrix, which is the input
 * for the Spatial light modulator.
 * N.B. A double value of 1 will be 255 in the SLM (the range is 0-255).
 */
void DtoIb(double *Dbuf, int length, unsigned char *phaseData)
{
  unsigned char temp;
  int i;
  
#ifdef DEBUG_OUTPUT
  int tp, tp2;
  FILE *pfold;
  pfold = fopen("c:/slmcontrol/dumpout/VC_output_beforeD2I.txt","wt");
  for (tp = 0; tp < 512; tp++) {
    fprintf(pfold, "\n");
    for (tp2 = 0; tp2 < 512; tp2++)
      fprintf(pfold, "%f, ", Dbuf[tp*512 + tp2]);
  }
  fclose(pfold);
#endif
  
  for (i = 0; i < SLMSIZE * SLMSIZE; i++) {
    Dbuf[i] = round256b(Dbuf[i] * 256);
    if (Dbuf[i] < 0) {
      Dbuf[i] = Dbuf[i] + 256;
    }
  } 
  
  // GH: Appears to be some sort of a rounding scheme.  Bit weird though.
  for (i = 0; i < length; i ++) {
    temp = unsigned char(*(Dbuf+i));
    if ((*(Dbuf+i) - temp) < 0.5)
      *(phaseData+i) = temp;
    else
      *(phaseData+i) = temp + 1;
  }
}


/**
 * Generate the buffer from the parameters.
 *
 * @param phaseData The output phase data image buffer.
 */
void SeidelPolynomial::generateImageBufferForSLM(unsigned char *phaseData)
{
  double x, y, radius;
  double divX, divY, XSquPlusYSqu, divXSqu, divYSqu, XPYSquSqu;
  double terms[16];
  double total;
  //std::ostringstream logSS;
  
  
  //  char msgbuf[1024];
  
  //int defocusbins, stigxbins, stigybins, comaxbins, comaybins, speribins;
  
  int ActSize, start, end;
  double *zern = new double[SLMSIZE*SLMSIZE];
  memset(zern, 0, sizeof(double)*SLMSIZE*SLMSIZE);
  
  
  ActSize = SLMSIZE;
  /* GH: FIX.  5/07/2008.  Should be 256, not 300.
  * radius = ActSize*300/512;
  */
  radius = ActSize*256/512;
  
  start = (SLMSIZE - ActSize)/2;
  end = start + ActSize;
  
  y = ActSize/2;
  
  for (int row = start; row < end; row++) {
    // Reset x.
    x = -(ActSize/2);
    
    for (int col = start; col < end; col++) {
      // Build some terms that are repeated through the equations.
      divX = x/radius;
      divY = y/radius;
      XSquPlusYSqu = divX*divX + divY*divY;
      XPYSquSqu = XSquPlusYSqu*XSquPlusYSqu;
      divXSqu = divX*divX;
      divYSqu = divY*divY;
      
      if ((divXSqu + divYSqu) <= 1) {     
        terms[0] = (getPiston());                            // Constant term / Piston
        terms[1] = (getTiltX()/2)*divX;                        // Tilt X
        terms[2] = (getTiltY()/2)*divY;                        // Tilt Y
        terms[3] = (getPower())*(XSquPlusYSqu);        // Defocus?
        
        // XXX/FIXME/VERIFY: AstigOne=AstigmatismX, and AstigTwo for Y?
        terms[4] = 0; //(getAstigmatismX()/2)*(divXSqu - divY*divY);
        terms[5] = 0; //(getAstigmatismY()/2)*(2*divX*divY);
        terms[6] = 0; //(getComaX()/2)*(3*divX*XSquPlusYSqu - 2*divX);
        terms[7] = 0; //(getComaY()/2)*(3*divY*XSquPlusYSqu - 2*divY);
        terms[8] = (getSphericalAberration())*(XPYSquSqu);
        terms[9] = 0; //(getTrefoilX()/2)*(divXSqu*divX - 3*divX*divYSqu);
        terms[10] = 0;// (getTrefoilY()/2)*(3*divXSqu*divY - divYSqu*divY);
        terms[11] = 0; //(getSecondaryAstigmatismX()/2)*(3*divYSqu - 3*divXSqu + 4*divXSqu*XSquPlusYSqu - 4*divYSqu*XSquPlusYSqu);
        terms[12] = 0; //(getSecondaryAstigmatismY()/2)*(8*divX*divY*XSquPlusYSqu - 6*divX*divY);
        terms[13] = 0; //(getSecondaryComaX()/2)*(3*divX - 12*divX*XSquPlusYSqu + 10*divX*XPYSquSqu);
        terms[14] = 0; //(getSecondaryComaY()/2)*(3*divY - 12*divY*XSquPlusYSqu + 10*divY*XPYSquSqu);
        terms[15] = 0; //(getSecondarySphericalAberration()/2)*(12*XSquPlusYSqu - 1 - 30*XPYSquSqu + 20*XSquPlusYSqu*XPYSquSqu);
        
        // Add the terms together.
        total = 0;
        for (int i = 0; i < 16; i++) {
          total += terms[i];
        }
      } else {
        total = 0;
      }
      
      zern[row*SLMSIZE+col] = total;
      total = 0;
      x++;
    }
    
    y--;
  }
  
  memset(phaseData, 0, sizeof(unsigned char)*SLMSIZE*SLMSIZE);
  DtoIb(zern, SLMSIZE*SLMSIZE, phaseData);
  delete zern;
  
  return;
}


void SeidelPolynomial::dumpString()
{
  std::ostringstream logSS;
  // Piston.
  logSS << "Piston: " <<  getPiston();
  LOGME( logSS.str() );
  logSS.str("");
  // Power
  logSS << "Power: " << getPower();
  LOGME( logSS.str() );
  logSS.str("");
  // Astigmatism X
  logSS << "AstigX: " << getAstigmatismX();
  LOGME( logSS.str() );
  logSS.str("");
  // Astigmatism Y
  logSS << "AstigY: " << getAstigmatismY();
  LOGME( logSS.str() );
  logSS.str("");
  // Coma X
  logSS << "ComaX: " << getComaX();
  LOGME( logSS.str() );
  logSS.str("");
  // Coma Y
  logSS << "ComaY: " << getComaY();
  LOGME( logSS.str() );
  logSS.str("");
  // Spherical ab.
  logSS << "SphericalAb: " << getSphericalAberration();
  LOGME( logSS.str() );
  logSS.str("");
  // Trefoil X
  logSS << "TrefoilX: " << getTrefoilX();
  LOGME( logSS.str() );
  logSS.str("");
  // Trefoil Y
  logSS << "TrefoilY: " << getTrefoilY();
  LOGME( logSS.str() );
  logSS.str("");
  // 2nd coma x
  logSS << "2nd ComaX: " << getSecondaryComaX();
  LOGME( logSS.str() );
  logSS.str("");
  // 2nd coma y
  logSS << "2nd ComaY: " << getSecondaryComaY();
  LOGME( logSS.str() );
  logSS.str("");
  // 2nd spherical ab.
  logSS << "2nd SpherAb: " << getSecondarySphericalAberration();
  LOGME( logSS.str() );
  logSS.str("");
  // 2nd astigm. x
  logSS << "2nd AstigX: " << getSecondaryAstigmatismX();
  LOGME( logSS.str() );
  logSS.str("");
  // 2nd astigm. y
  logSS << "2nd AstigY: " << getSecondaryAstigmatismY();
  LOGME( logSS.str() );
  logSS.str("");
  // tilt x
  logSS << "Tilt X: " << getTiltX();
  LOGME( logSS.str() );
  logSS.str("");
  // tilt y
  logSS << "Tilt Y: " << getTiltY();
  LOGME( logSS.str() );
}




// Getters and setters.

double SeidelPolynomial::getPiston()
{
  return piston;
}

void SeidelPolynomial::setPiston(double piston)
{
  this->piston = piston;
}

double SeidelPolynomial::getPower()
{
  return power;
}

void SeidelPolynomial::setPower(double power)
{
  this->power = power;
}

double SeidelPolynomial::getAstigmatismX()
{
  return astigmatismX;
}

void SeidelPolynomial::setAstigmatismX(double astigmatismX)
{
  this->astigmatismX = astigmatismX;
}

double SeidelPolynomial::getAstigmatismY()
{
  return astigmatismY;
}

void SeidelPolynomial::setAstigmatismY(double astigmatismY)
{
  this->astigmatismY = astigmatismY;
}

double SeidelPolynomial::getComaX()
{
  return comaX;
}

void SeidelPolynomial::setComaX(double comaX)
{
  this->comaX = comaX;
}

double SeidelPolynomial::getComaY()
{
  return comaY;
}

void SeidelPolynomial::setComaY(double comaY)
{
  this->comaY = comaY;
}

double SeidelPolynomial::getSphericalAberration()
{
  return sphericalAberration;
}

void SeidelPolynomial::setSphericalAberration(double sphericalAberration)
{
  this->sphericalAberration = sphericalAberration;
}

double SeidelPolynomial::getTrefoilX()
{
  return trefoilX;
}

void SeidelPolynomial::setTrefoilX(double trefoilX)
{
  this->trefoilX = trefoilX;
}

double SeidelPolynomial::getTrefoilY()
{
  return trefoilY;
}

void SeidelPolynomial::setTrefoilY(double trefoilY)
{
  this->trefoilY = trefoilY;
}

double SeidelPolynomial::getSecondaryComaX()
{
  return secondaryComaX;
}

void SeidelPolynomial::setSecondaryComaX(double secondaryComaX)
{
  this->secondaryComaX = secondaryComaX;
}

double SeidelPolynomial::getSecondaryComaY()
{
  return secondaryComaY;
}

void SeidelPolynomial::setSecondaryComaY(double secondaryComaY)
{
  this->secondaryComaY = secondaryComaY;
}

double SeidelPolynomial::getSecondarySphericalAberration()
{
  return secondarySphericalAberration;
}

void SeidelPolynomial::setSecondarySphericalAberration(double secondarySphericalAberration)
{
  this->secondarySphericalAberration = secondarySphericalAberration;
}

double SeidelPolynomial::getSecondaryAstigmatismX()
{
  return secondaryAstigmatismX;
}

void SeidelPolynomial::setSecondaryAstigmatismX(double secondaryAstigmatismX)
{
  this->secondaryAstigmatismX = secondaryAstigmatismX;
}

double SeidelPolynomial::getSecondaryAstigmatismY()
{
  return secondaryAstigmatismY;
}

void SeidelPolynomial::setSecondaryAstigmatismY(double secondaryAstigmatismY)
{
  this->secondaryAstigmatismY = secondaryAstigmatismY;
}

double SeidelPolynomial::getTiltX()
{
  return tiltX;
}

void SeidelPolynomial::setTiltX(double tiltX)
{
  this->tiltX = tiltX;
}

double SeidelPolynomial::getTiltY()
{
  return tiltY;
}

void SeidelPolynomial::setTiltY(double tiltY)
{
  this->tiltY = tiltY;
}


