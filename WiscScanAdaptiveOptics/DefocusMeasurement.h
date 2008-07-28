
#include "SeidelPolynomial.h"
#include "SLMController.h"


class DefocusMeasurement {
  public:
    // Constructor.
    DefocusMeasurement();

    // Prepares the measurement (resets).
    void prepareMeasurement();

    // Pass fitness and process.
    void iterateOnce(double intensity);

    // Generate filename for output image.
    char *generateFileName();

    // Checks if the optimization is finished.
    bool isFinished();

  private:
    // The SLM Controller.
    SLMController *SLMInstance;

    // The population.
    SeidelPolynomial SeidelSet;

    // Seidel S.A, defocus.
    double As, Ad;

    // First iteration done?
    bool firstIterationDone;

    // Is finished?
    bool isDone;
};

