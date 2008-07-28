
/**
 * Represents a Seidel polynomial, with the following coefficients:
 * astigmatism, coma, spherical abberation, trefoil, secondary coma,
 * secondary spherical abberation.
 * XXX/FIXME: Update coefficient list.
 */

class SeidelPolynomial {
  private:
    double piston;
    double power;
    double astigmatismX;
    double astigmatismY;
    double comaX;
    double comaY;
    double sphericalAberration;
    double trefoilX;
    double trefoilY;
    double secondaryComaX;
    double secondaryComaY;
    double secondarySphericalAberration;
    double secondaryAstigmatismX;
    double secondaryAstigmatismY;
    double tiltX;
    double tiltY;

  public:
    // Constructor.
    SeidelPolynomial();

    // Reset Seidel coefficients.
    void resetCoefficients();

    // Generate image buffer for the SLM.
    void generateImageBufferForSLM(unsigned char *phaseData);

    // Generate a string with information on the object.
    void dumpString();

    // Getters and setters.
    double getPiston();
    void setPiston(double piston);

    double getAstigmatismX();
    void setAstigmatismX(double astigmatismX);

    double getAstigmatismY();
    void setAstigmatismY(double astigmatismY);
    
    double getComaX();
    void setComaX(double comaX);

    double getComaY();
    void setComaY(double comaY);

    double getSphericalAberration();
    void setSphericalAberration(double sphericalAberration);

    double getTrefoilX();
    void setTrefoilX(double trefoilX);

    double getTrefoilY();
    void setTrefoilY(double trefoilY);

    double getSecondaryComaX();
    void setSecondaryComaX(double secondaryComaX);

    double getSecondaryComaY();
    void setSecondaryComaY(double secondaryComaY);

    double getSecondarySphericalAberration();
    void setSecondarySphericalAberration(double secondarySphericalAberration);

    double getSecondaryAstigmatismX();
    void setSecondaryAstigmatismX(double secondaryAstigmatismX);

    double getSecondaryAstigmatismY();
    void setSecondaryAstigmatismY(double secondaryAstigmatismY);

    double getTiltX();
    void setTiltX(double tiltX);

    double getTiltY();
    void setTiltY(double tiltY);

    double getPower();
    void setPower(double power);
};

