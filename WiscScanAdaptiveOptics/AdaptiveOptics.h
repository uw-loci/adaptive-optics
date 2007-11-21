
#include "GeneticOptimization.h"

class AdaptiveOptics
{
  private:
    GeneticOptimization *optimizer;
  public:
    AdaptiveOptics();  
    int processImage(double *buf, int width, int height, char mode);
    bool initializePhaseModulator(bool bPowerStatus);
};

