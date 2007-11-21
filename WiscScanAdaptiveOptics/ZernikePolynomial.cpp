
#include "StdAfx.h"
#include "ZernikePolynomial.h"

// Constructor.
ZernikePolynomial::ZernikePolynomial()
{
  resetCoefficients();
}


/**
 * Resets the Zernike coefficients to default values.
 */
void ZernikePolynomial::resetCoefficients()
{
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
  setTiltX(40.0);
  setTiltY(40.0);
}

// Getters and setters.
double ZernikePolynomial::getAstigmatismX()
{
  return astigmatismX;
}

void ZernikePolynomial::setAstigmatismX(double astigmatismX)
{
  this->astigmatismX = astigmatismX;
}

double ZernikePolynomial::getAstigmatismY()
{
  return astigmatismY;
}

void ZernikePolynomial::setAstigmatismY(double astigmatismY)
{
  this->astigmatismY = astigmatismY;
}

double ZernikePolynomial::getComaX()
{
  return comaX;
}

void ZernikePolynomial::setComaX(double comaX)
{
  this->comaX = comaX;
}

double ZernikePolynomial::getComaY()
{
  return comaY;
}

void ZernikePolynomial::setComaY(double comaY)
{
  this->comaY = comaY;
}

double ZernikePolynomial::getSphericalAberration()
{
  return sphericalAberration;
}

void ZernikePolynomial::setSphericalAberration(double sphericalAberration)
{
  this->sphericalAberration = sphericalAberration;
}

double ZernikePolynomial::getTrefoilX()
{
  return trefoilX;
}

void ZernikePolynomial::setTrefoilX(double trefoilX)
{
  this->trefoilX = trefoilX;
}

double ZernikePolynomial::getTrefoilY()
{
  return trefoilY;
}

void ZernikePolynomial::setTrefoilY(double trefoilY)
{
  this->trefoilY = trefoilY;
}

double ZernikePolynomial::getSecondaryComaX()
{
  return secondaryComaX;
}

void ZernikePolynomial::setSecondaryComaX(double secondaryComaX)
{
  this->secondaryComaX = secondaryComaX;
}

double ZernikePolynomial::getSecondaryComaY()
{
  return secondaryComaY;
}

void ZernikePolynomial::setSecondaryComaY(double secondaryComaY)
{
  this->secondaryComaY = secondaryComaY;
}

double ZernikePolynomial::getSecondarySphericalAberration()
{
  return secondarySphericalAberration;
}

void ZernikePolynomial::setSecondarySphericalAberration(double secondarySphericalAberration)
{
  this->secondarySphericalAberration = secondarySphericalAberration;
}

double ZernikePolynomial::getTiltX()
{
  return tiltX;
}

void ZernikePolynomial::setTiltX(double tiltX)
{
  this->tiltX = tiltX;
}

double ZernikePolynomial::getTiltY()
{
  return tiltY;
}

void ZernikePolynomial::setTiltY(double tiltY)
{
  this->tiltY = tiltY;
}


