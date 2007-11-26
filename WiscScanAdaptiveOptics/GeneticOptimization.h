
#include "ZernikePolynomial.h"
#include "SLMController.h"

#define POPULATION_SIZE   5
#define MAX_ITERATIONS    20

#define MAX_SPHERICAL_ABERRATION_MUTATION 1.0

class GeneticOptimization {
  public:
    // Constructor.
    GeneticOptimization();

    // Performs optimization.  Returns best result. 
    // void performOptimization();

    // Prepares the optimization (resets).
    void prepareOptimization();

    // Pass fitness and process.
    // iterateOnce
    void iterateOnce(double intensity);

    // Checks if the optimization is finished.
    bool isFinished();

  private:
    // The SLM Controller.
    SLMController *SLMInstance;

    // The population.
    ZernikePolynomial Population[POPULATION_SIZE];

    // The fitness of the population.
    double Fitness[POPULATION_SIZE];

    // Indicates whether or not the first iteration is done.
    bool firstIterationDone;

    // The number of population members evaluated.
    int evaluatedCount;

    // The current number of iterations done.
    int iterationCount;

    // Is finished?
    bool isDone;

    // Initializes the population (arrays of Zernike coefficients).
    void initializePopulation();

    // Returns the most fit member of the population.
    //int evaluatePopulation();

    // Finds best member of the population.
    int findBestMemberIndex();

    // evalate fitness of a single member.
    // double evaluateMemberFitness(int memberIndex);

    // Crossover involves mating the current population with the best member of the population.
    void crossoverPopulation(int bestMemberIndex);

    // Mutation is done after crossover, involves random perturbations of new population.
    void mutatePopulation();

    // Checks whether convergence has been reached, or not.
    bool isStopConditionSatisfied();

    // Fixes tilt for a given coma.
    // double tilt_fix_func(double coma);

    // Fixes focus, given spherical aberation and astigmatism parameters.
    // double focus_fix_func(double spher, double astigx, double astigy);
};

