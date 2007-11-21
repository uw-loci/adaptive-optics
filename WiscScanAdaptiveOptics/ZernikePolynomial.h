
/*
 * Represents a Zernike polynomial, with the following coefficients:
 * astigmatism, coma, spherical abberation, trefoil, secondary coma,
 * secondary spherical abberation.
 */
class ZernikePolynomial {
  private:
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
    double tiltX;
    double tiltY;

  public:
    // Constructor.
    ZernikePolynomial();

    // Reset Zernike coefficients.
    void resetCoefficients();

    // Getters and setters.
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

    double getTiltX();
    void setTiltX(double tiltX);

    double getTiltY();
    void setTiltY(double tiltY);
};

