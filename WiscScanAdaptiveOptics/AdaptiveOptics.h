/**
 * AdaptiveOptics.
 * This class is a middle layer between the WiscScanAdaptiveOptics DLL (which interfaces WiscScan),
 * and the optimization kernel.
 */

#include "GeneticOptimization.h"
#include "DefocusMeasurement.h"


class AdaptiveOptics
{
  private:
    GeneticOptimization *optimizer;
    DefocusMeasurement *measurand;

  public:
    AdaptiveOptics();  
    int processImage(double *buf, int width, int height, char mode);
    bool initializePhaseModulator(bool bPowerStatus);
    void closeDown();
    bool changeGain();
};

