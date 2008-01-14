/**
 * The Genetic Optimization class.
 * Uses a genetic algorithm to optimize the image output from the microscope.
 * The functionality is driven by WiscScanAdaptiveOptics, through the iterateOnce() method.
 */

#include "StdAfx.h"
#include "GeneticOptimization.h"
#include "Logger.h"

#include <stdlib.h>
#include <time.h>
#include <math.h>
#include <sstream>

/**
 * Constructor.
 */
GeneticOptimization::GeneticOptimization()
{
  // Prepare the random number generator.
  srand(time(0));

  // Prepare the optimization.
  prepareOptimization();

  // Setup the SLM.
  SLMInstance = new SLMController;
  SLMInstance->initSLM();
}

/**
 * Prepares the optimization (resets).
 * Neccessary because in the current model: WiscScan drives the evolution.
 */
void GeneticOptimization::prepareOptimization()
{
  iterationCount = 0;
  evaluatedCount = 0;
  firstIterationDone = false;
  isDone = false;
  initializePopulation();
}

/**
 * Finds the best member of the population.
 *
 * @return The index of the best member.
 */
int GeneticOptimization::findBestMemberIndex()
{
  int bestIndex;
  double bestValue;
  LOGME("Looking for best member: ");
  for (int i = 0; i < POPULATION_SIZE; i++) {
	std::ostringstream logSS;
	logSS << "- i: " << i << " avg. intensity: " << Fitness[i];
	LOGME( logSS.str() )

    if (i == 0 || Fitness[i] > bestValue) {
      bestValue = Fitness[i];
      bestIndex = i;
    }
  }

  return bestIndex;
}

/**
 * Run one iteration.
 *
 * @param intensity The intensity of the last image that was captured.
 */
void GeneticOptimization::iterateOnce(double intensity)
{
  std::ostringstream logSS;
  logSS << "Iteration count is: " << evaluatedCount << " intensity now: " << intensity;
  LOGME( logSS.str() )


  if (!firstIterationDone) {
    evaluatedCount = 0;
    firstIterationDone = true;
    //Population[evaluatedCount].setSphericalAberration(-1);
	//Population[evaluatedCount].setSecondarySphericalAberration(-1);
	Population[evaluatedCount].setComaX(-1);
  } else {
    //Population[evaluatedCount].setSecondarySphericalAberration(Population[evaluatedCount].getSecondarySphericalAberration() + 0.5);
	Population[evaluatedCount].setComaX(Population[evaluatedCount].getComaX() + 0.2);
  }
  
  if (Population[evaluatedCount].getComaX() > 1)
  {
    isDone = true;
  }

logSS.str("");
logSS << "Setting ComaX to: " << Population[evaluatedCount].getComaX();

LOGME( logSS.str() );

// Prepare the next image on the SLM. 
unsigned char *phaseData = new unsigned char [SLMSIZE*SLMSIZE];
Population[evaluatedCount].generateImageBufferForSLM(phaseData);
SLMInstance->receiveData(phaseData);
SLMInstance->sendToSLM(true);
delete phaseData;

Sleep(100); // Takes approx. 100 ms for SLM to "prepare".



return;

  if (!firstIterationDone) {
    // The very first iteration.  Setup the SLM and return.
    firstIterationDone = true;
    evaluatedCount = 0;

    // Prepare the next image on the SLM. 
    unsigned char *phaseData = new unsigned char [SLMSIZE*SLMSIZE];
    LOGME( "First iteration is now approximately done " )
    LOGME( "- Generating data for SLM " )
    Population[evaluatedCount].generateImageBufferForSLM(phaseData);
	LOGME( "- preparing data to be sent " )
    SLMInstance->receiveData(phaseData);
	LOGME( "- preparing sending... " )
    SLMInstance->sendToSLM(true);
	LOGME( "- done " )
    delete phaseData;

    Sleep(100); // Takes approx. 100 ms for SLM to "prepare".
	LOGME( "- SLM ready for action! " )
    return; // Return immediately.
  }

  Fitness[evaluatedCount] = intensity;
  evaluatedCount++;
  
  if (evaluatedCount == POPULATION_SIZE) {
    // Evaluation of population is done.
    LOGME( "The last evaluation for this iteration is done." );
	LOGME( "The old population:" );
	// Output info on the population for debugging.
	debugPopulation();
    int bestMemberIndex = findBestMemberIndex();
    logSS.str(""); 
    logSS << "The best member index is: " << bestMemberIndex;
    LOGME( logSS.str() )
    crossoverPopulation(bestMemberIndex);
    mutatePopulation(bestMemberIndex);
    
    iterationCount++;
	evaluatedCount = 0;
    if (isStopConditionSatisfied() || iterationCount == MAX_ITERATIONS) {
      isDone = true;
    }

	LOGME( "-------------------------------------------------------" )

  }

  if (!isDone) {
    // Prepare the next image on the SLM. 
    unsigned char *phaseData = new unsigned char [SLMSIZE*SLMSIZE];
    
    Population[evaluatedCount].generateImageBufferForSLM(phaseData);
    SLMInstance->receiveData(phaseData);
    SLMInstance->sendToSLM(true);
    delete phaseData;

    Sleep(100); // Takes approx. 100 ms for SLM to "prepare".
  }
}

/**
 * Checks if the optimization is finished.
 *
 * @return True if the optimization is finished, false otherwise.
 */
bool GeneticOptimization::isFinished()
{
  return isDone;
}

/*
 * Performs optimization.  Returns best result. 
 * Scans repeatedly until a stop condition is met, then evaluates the status and
 * calculates new coefficients.  Then scans again.  This repeated until the stop
 * condition has been met.
 *
 * @param aver Average picture intensity.
 */
/*
void GeneticOptimization::performOptimization()
{
int iterationCount = 0;
int bestMemberIndex;

  while (!isStopConditionSatisfied() && iterationCount < MAX_ITERATIONS) {
  bestMemberIndex = evaluatePopulation();
  crossoverPopulation(bestMemberIndex);
  mutatePopulation();
  
    iterationCount++;
    }
    
      // Result=? XXX/FIXME
      }
*/

/*
double GeneticOptimization::evaluateMemberFitness(int memberIndex)
{
return 1.0; // XXX/FIXME.
}
*/

/**
 * Initializes the population (arrays of Zernike coefficients).
 */
void GeneticOptimization::initializePopulation()
{
  int i;
  
  // First member should be reset (0) - for debug?
  Population[0].resetCoefficients();

  // Rest should be random on appropriate intervals.
  for (i = 1; i < POPULATION_SIZE; i++) {
    Population[i].resetCoefficients();
    
    // XXX/FIXME: Only work with spherical aberration to begin with.
    /*Population[i].setAstigmatismX(0);
    Population[i].setAstigmatismY(0);
    Population[i].setComaX(0);
    Population[i].setComaY(0);*/
//    double mutationValue = (rand() % 1024)/1024.0 * MAX_SPHERICAL_ABERRATION_MUTATION;    
//    Population[i].setSphericalAberration(mutationValue);
    Population[i].setSphericalAberration(50); 
	//Population[i].setSecondarySphericalAberration(0);
    /*Population[i].setTrefoilX(0);
    Population[i].setTrefoilY(0);
    Population[i].setSecondaryComaX(0);
    Population[i].setSecondaryComaY(0);
    Population[i].setSecondarySphericalAberration(0);*/
  }
  
  
}

// Returns the index of the most fit member of the population.
//int GeneticOptimization::evaluatePopulation()
//{
/*int bestMemberIndex = -1;
double bestIntensity = 0.0;
double memberIntensity;

  for (int i = 0; i < POPULATION_SIZE; i++)
  {
  memberIntensity = evaluateMemberFitness(i);
  if ( (bestMemberIndex == -1) || (memberIntensity > bestIntensity) ) {
  bestIntensity = memberIntensity;
  bestMemberIndex = i;
  }
}*/
//  return 0; // XXX/FIXME
//}

/**
 * Crossover involves mating the current population with the best member of the population.
 *
 * @param bestMemberIndex The index of the best member, in the population vector.
 */
void GeneticOptimization::crossoverPopulation(int bestMemberIndex)
{
  ZernikePolynomial *bestMember = &Population[bestMemberIndex];
  ZernikePolynomial *aMember;
  
  for (int i = 0; i < POPULATION_SIZE; i++)
  {
    aMember = &Population[i];
    
    // New population has coefficients which are the average of the current population's
    // polynomials and the best (most fit) one's.
    /*    aMember->setAstigmatismX((aMember->getAstigmatismX() + bestMember->getAstigmatismX())/2);
    aMember->setAstigmatismY((aMember->getAstigmatismY() + bestMember->getAstigmatismY())/2);
    aMember->setDefocus((aMember->getDefocus() + bestMember->getDefocus())/2);
    aMember->setTrefoilX((aMember->getTrefoilX() + bestMember->getTrefoilX())/2);
    aMember->setTrefoilY((aMember->getTrefoilY() + bestMember->getTrefoilY())/2);
    aMember->setComaX((aMember->getComaX() + bestMember->getComaX())/2);
    aMember->setComaY((aMember->getComaY() + bestMember->getComaY())/2);*/
    // XXX/FIXME: Start with only 1 optimization parameter (spherical aberration).
    aMember->setSphericalAberration((aMember->getSphericalAberration() + bestMember->getSphericalAberration())/2);
  }
}

/**
 * Mutation is done after crossover, involves random perturbations of new population.
 *
 * @param bestIndex The index of the best member of the best polynomial coefficient set.
 */
void GeneticOptimization::mutatePopulation(int bestIndex)
{
  ZernikePolynomial *aMember;
  
  for (int i = 0; i < POPULATION_SIZE; i++)
  {
	if (i == bestIndex) {
      continue; // Do not mutilate the previous best member.
	}

    aMember = &Population[i];
    /*    aMember->setAstigmatismX((aMember->getAstigmatismX() + bestMember->getAstigmatismX())/2);
    aMember->setAstigmatismY((aMember->getAstigmatismY() + bestMember->getAstigmatismY())/2);
    aMember->setDefocus((aMember->getDefocus() + bestMember->getDefocus())/2);
    aMember->setTrefoilX((aMember->getTrefoilX() + bestMember->getTrefoilX())/2);
    aMember->setTrefoilY((aMember->getTrefoilY() + bestMember->getTrefoilY())/2);
    aMember->setComaX((aMember->getComaX() + bestMember->getComaX())/2);
    aMember->setComaY((aMember->getComaY() + bestMember->getComaY())/2);*/
    // XXX/FIXME: Start with only 1 optimization parameter (spherical aberration).
    double mutationValue = (rand() % 1024)/1024.0/10.0 * Population[bestIndex].getSphericalAberration();
    aMember->setSphericalAberration(aMember->getSphericalAberration() + mutationValue);
  }
}

/**
 * Checks whether convergence has been reached, or not.
 *
 * @return True if the condition is satisfied.
 */
bool GeneticOptimization::isStopConditionSatisfied()
{
  double mean = 0, max;
  int i;
  
  // Calculate fitness mean.
  for (i = 0; i < POPULATION_SIZE; i++) {
    mean += Fitness[i];
    
    if (i == 0 || Fitness[i] > max) {
      max = Fitness[i];
    }
  }
  
  mean /= POPULATION_SIZE;
  
  // Calculate standard deviation of fitness.
  double variance = 0, standardDeviation = 0;
  for (i = 0; i < POPULATION_SIZE; i++) {
    variance += (Fitness[i] - mean)*(Fitness[i] - mean);
  }
  variance /= POPULATION_SIZE - 1;
  
  standardDeviation = sqrt(variance);

  std::ostringstream logSS;
  logSS << "var: " << variance << " " << "stddev: " << standardDeviation;
  LOGME( logSS.str() )
  logSS.str("");
  logSS << "stdDev/max: " << (standardDeviation/max);
  LOGME( logSS.str() )
  
  // Check condition.
  return standardDeviation/max < 0.05;
}



void GeneticOptimization::debugPopulation()
{
  for (int i = 0; i < POPULATION_SIZE; i++) {
    std::ostringstream logSS;
	logSS.str("");
    logSS << "Population member: " << i;
	LOGME( logSS.str() );
	Population[i].dumpString();
  }
}
