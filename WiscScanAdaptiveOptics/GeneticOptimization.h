
#include "ZernikePolynomial.h"
#include "SLMController.h"

#define POPULATION_SIZE   15
#define MAX_ITERATIONS    60

#define MAX_SPHERICAL_ABERRATION_MUTATION 20.0

class GeneticOptimization {
  public:
    // Constructor.
    GeneticOptimization();

    // Performs optimization.  Returns best result. 
    // void performOptimization();

    // Prepares the optimization (resets).
    void prepareOptimization();

    // Close down (shut down).
    void closeDown();

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

    // The best fitnesses of the population.
    double bestFitness[MAX_ITERATIONS];

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
    int *findBestMemberIndices();

    // Writes the optimization information to a Matlab compatible data file.
    void writeOptimizationMatlabData(int bestMemberIndex);

    // evalate fitness of a single member.
    // double evaluateMemberFitness(int memberIndex);

    // Crossover involves mating the current population with the best member of the population.
    void crossoverPopulation(int *bestMemberIndices);

    // Mutation is done after crossover, involves random perturbations of new population.
    void mutatePopulation(int *bestIndices);

    // Checks whether convergence has been reached, or not.
    bool isStopConditionSatisfied();

	  void debugPopulation();

    // Fixes tilt for a given coma.
    // double tilt_fix_func(double coma);

    // Fixes focus, given spherical aberation and astigmatism parameters.
    // double focus_fix_func(double spher, double astigx, double astigy);
};

