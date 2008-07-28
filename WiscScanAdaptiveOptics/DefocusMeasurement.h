
#include "ZernikePolynomial.h"
#include "SLMController.h"


class DefocusMeasurement {
  public:
    // Constructor.
    DefocusMeasurement();

    // Prepares the measurement (resets).
    void prepareMeasurement();

    // Pass fitness and process.
    void iterateOnce(double intensity);

    // Checks if the optimization is finished.
    bool isFinished();

  private:
    // The SLM Controller.
    SLMController *SLMInstance;

    // The population.
    SeidelPolynomial SeidelSet;

    // Seidel S.A, defocus.
    Double As, Ad;

    // Is finished?
    bool isDone;

    // Initializes the population (arrays of Zernike coefficients).
    void initializePopulation();

    // Returns the most fit member of the population.
    //int evaluatePopulation();

    // Finds best member of the population.
    int *findBestMemberIndices();

    // Checks whether convergence has been reached, or not.
    bool isStopConditionSatisfied();

    void debugPopulation();

    // Fixes tilt for a given coma.
    // double tilt_fix_func(double coma);

    // Fixes focus, given spherical aberation and astigmatism parameters.
    // double focus_fix_func(double spher, double astigx, double astigy);
};

