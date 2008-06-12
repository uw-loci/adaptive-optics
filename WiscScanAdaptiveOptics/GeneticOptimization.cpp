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
  Sleep(500); // Give it a bit to get started.
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
int *GeneticOptimization::findBestMemberIndices()
{
  int *bestIndices = new int[POPULATION_SIZE];

  for (int i = 0; i < POPULATION_SIZE; i++) {
    bestIndices[i] = i;
  }

  //LOGME("Looking for best member: ");
  bool isDone = false;
  while (!isDone) {
    isDone = true;
    for (int i = 0; i < POPULATION_SIZE-1; i++) {
      if (Fitness[bestIndices[i+1]] > Fitness[bestIndices[i]]) {
        int tmp = bestIndices[i];
        bestIndices[i] = bestIndices[i+1];
        bestIndices[i+1] = tmp;
        isDone = false;
      }  
    }
  }

  return bestIndices;
}



/**
 * Writes the optimization information to a Matlab compatible data file.
 *
 * @param bestMemberIndex The index of the best (most fit) member of the generation.
 */
void GeneticOptimization::writeOptimizationMatlabData(int bestMemberIndex)
{
  fstream file;

#define MATLAB_DATA_FILE    "C:/gunnsteinn/debug/Optimization.dat"

  if (iterationCount == 0) {
    file.open(MATLAB_DATA_FILE, ios::out);
    file << "%Generation|Fitness|TX|TY|Power|AX|AY|CX|CY|SA|2SA" << endl; 

    // Include the original fitness (without any correction) as the 0th generation.
    // To be true, the first member must have all-zero Zernike coefficients
    // (in the first generation).

    file << (0) << "\t" << Fitness[0] << "\t"
        << "0" << "\t"
        << "0" << "\t"
        << "0" << "\t"
        << "0" << "\t"
        << "0" << "\t"
        << "0" << "\t"
        << "0" << "\t"
        << "0" << "\t"
        << "0" << "\t"
        << endl;

  } else {
    file.open(MATLAB_DATA_FILE, ios::out|ios::app);
  }

  file << (iterationCount+1) << "\t" << Fitness[bestMemberIndex] << "\t"
      << Population[bestMemberIndex].getTiltX() << "\t"
      << Population[bestMemberIndex].getTiltY() << "\t"
      << Population[bestMemberIndex].getPower() << "\t"
      << Population[bestMemberIndex].getAstigmatismX() << "\t"
      << Population[bestMemberIndex].getAstigmatismY() << "\t"
      << Population[bestMemberIndex].getComaX() << "\t"
      << Population[bestMemberIndex].getComaY() << "\t"
      << Population[bestMemberIndex].getSphericalAberration() << "\t"
      << Population[bestMemberIndex].getSecondarySphericalAberration() << "\t"
      << endl;

  file.close();
}


/**
 * Run one iteration.
 *
 * @param intensity The intensity of the last image that was captured.
 */
void GeneticOptimization::iterateOnce(double intensity)
{
  std::ostringstream logSS;
  logSS << "Iteration count is: " << evaluatedCount 
//      << " Tref. Y: " << Population[evaluatedCount].getTrefoilY()
//      << " 2nd. Ast. X: " << Population[evaluatedCount].getSecondaryAstigmatismX()
        << " intensity now: " << intensity;
  LOGME( logSS.str() )

#if 0

  if (!firstIterationDone) {
    evaluatedCount = 0;
    firstIterationDone = true;
    //Population[evaluatedCount].setSphericalAberration(-1);
    //Population[evaluatedCount].setSecondarySphericalAberration(-1);
    //Population[evaluatedCount].setComaX(-2);
    //Population[evaluatedCount].setComaY(-2);
    //Population[evaluatedCount].setPiston(-10);
    //Population[evaluatedCount].setPower(-2);
    //Population[evaluatedCount].setAstigmatismX(-4);
    //Population[evaluatedCount].setAstigmatismY(-3);
    //Population[evaluatedCount].setTrefoilX(-3);
    //Population[evaluatedCount].setTrefoilY(-3);
    //Population[evaluatedCount].setSecondaryAstigmatismX(-2)
  } else {
    //Population[evaluatedCount].setSphericalAberration(Population[evaluatedCount].getSphericalAberration() + 0.05);
    //Population[evaluatedCount].setSecondarySphericalAberration(Population[evaluatedCount].getSecondarySphericalAberration() + 0.10);
    //Population[evaluatedCount].setComaX(Population[evaluatedCount].getComaX() + 0.10);
    //Population[evaluatedCount].setComaY(Population[evaluatedCount].getComaY() + 0.10);
    //Population[evaluatedCount].setPiston(Population[evaluatedCount].getPiston() + 0.5);
    //Population[evaluatedCount].setPower(Population[evaluatedCount].getPower() + 0.10);
    //Population[evaluatedCount].setAstigmatismX(Population[evaluatedCount].getAstigmatismX() + 0.10);
    //Population[evaluatedCount].setAstigmatismY(Population[evaluatedCount].getAstigmatismY() + 0.10);
    //Population[evaluatedCount].setTrefoilX(Population[evaluatedCount].getTrefoilX() + 0.10);
    //Population[evaluatedCount].setTrefoilY(Population[evaluatedCount].getTrefoilY() + 0.10);
    //Population[evaluatedCount].setSecondaryAstigmatismX(Population[evaluatedCount].getSecondaryAstigmatismX() + 0.10);
  }
  
  //if (Population[evaluatedCount].getSecondaryAstigmatismX() > 2)
  //{
//    isDone = true;
//  }

//logSS.str("");
//logSS << "Setting Spherical aberration to: " << Population[evaluatedCount].getSphericalAberration();
//logSS << "Setting 2nd Spherical aberration to: " << Population[evaluatedCount].getSecondarySphericalAberration();
//logSS << "Setting ComaX to: " << Population[evaluatedCount].getComaX();
//logSS << "Setting ComaY to: " << Population[evaluatedCount].getComaY();
//logSS << "Setting Piston to: " << Population[evaluatedCount].getPiston();
//logSS << "Setting Power to: " << Population[evaluatedCount].getPower();
//logSS << "Setting Ast. X to: " << Population[evaluatedCount].getAstigmatismX();
//logSS << "Setting Ast. Y to: " << Population[evaluatedCount].getAstigmatismY();
//logSS << "Setting Tref. X to: " << Population[evaluatedCount].getTrefoilX();


//LOGME( logSS.str() );
//Population[evaluatedCount].dumpString();

// Prepare the next image on the SLM. 
//unsigned char *phaseData = new unsigned char [SLMSIZE*SLMSIZE];
//Population[evaluatedCount].generateImageBufferForSLM(phaseData);
//SLMInstance->receiveData(phaseData);
//SLMInstance->sendToSLM(true);
//delete phaseData;

//Sleep(100); // Takes approx. 100 ms for SLM to "prepare".



//return;
#endif

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

    //Sleep(100); // Takes approx. 100 ms for SLM to "prepare".
    Sleep(150); // Seems to take a bit longer occasionally.
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
    int *bestMemberIndices = findBestMemberIndices();
    bestFitness[iterationCount] = Fitness[bestMemberIndices[0]];

    logSS.str(""); 
    logSS << "The best member index is: " << bestMemberIndices[0] << ", 2nd: " << bestMemberIndices[1];
    LOGME( logSS.str() );

    logSS.str(""); 
    logSS << "The best fitness is: " << bestFitness[iterationCount];
    LOGME( logSS.str() );

    writeOptimizationMatlabData(bestMemberIndices[0]);
    
    crossoverPopulation(bestMemberIndices);
    mutatePopulation(bestMemberIndices);

    logSS.str("");
    logSS << "Generation number: " << iterationCount;
    LOGME( logSS.str() );

    
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
  if (isDone) {
    SLMInstance->closeSLM();
  }
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
  // XXX/NOTE: the first polynomial is the zero one.
  for (i = 1; i < POPULATION_SIZE; i++) {
    Population[i].resetCoefficients();
    
    // XXX/FIXME: Only work with spherical aberration to begin with.
    /*Population[i].setAstigmatismX(0);
    Population[i].setAstigmatismY(0);
    Population[i].setComaX(0);
    Population[i].setComaY(0);*/
//    double mutationValue = (rand() % 1024)/1024.0 * MAX_SPHERICAL_ABERRATION_MUTATION;    
//    Population[i].setSphericalAberration(mutationValue);
    double sVal = ((rand() % 1024)/512.0 - 1) * 3;
    Population[i].setSphericalAberration(sVal);
    sVal = ((rand() % 1024)/512.0 - 1) * 3;
    Population[i].setSecondarySphericalAberration(sVal);
    sVal = ((rand() % 1024)/512.0 - 1) * 2;
    Population[i].setComaX(sVal);
    sVal = ((rand() % 1024)/512.0 - 1) * 2;
    Population[i].setComaY(sVal);
    Population[i].setPower(Population[i].focusCorrection());


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
void GeneticOptimization::crossoverPopulation(int *bestMemberIndices)
{
  ZernikePolynomial *bestMember = &Population[bestMemberIndices[0]];
  ZernikePolynomial *secondBestMember = &Population[bestMemberIndices[1]];
  ZernikePolynomial *aMember;
  
  for (int i = 0; i < POPULATION_SIZE; i++)
  {
    /*if (i == bestMemberIndices[0]) {
      continue; // Do not mutate the previous best member.
    }*/
    if (i == bestMemberIndices[0] || i == bestMemberIndices[1]) {
      continue; // Do not mutate the two previously best members.
    }

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
    aMember->setSphericalAberration((
      aMember->getSphericalAberration() 
      + 2*bestMember->getSphericalAberration()
      + secondBestMember->getSphericalAberration())/4);
    aMember->setSecondarySphericalAberration((
      aMember->getSecondarySphericalAberration() 
      + 2*bestMember->getSecondarySphericalAberration()
      + secondBestMember->getSecondarySphericalAberration())/4);
    aMember->setComaX((
      aMember->getComaX() 
      + 2*bestMember->getComaX()
      + secondBestMember->getComaX())/4);
    aMember->setComaY((
      aMember->getComaY() 
      + 2*bestMember->getComaY()
      + secondBestMember->getComaY())/4);
    aMember->setPower(aMember->focusCorrection());
  }
}

/**
 * Mutation is done after crossover, involves random perturbations of new population.
 *
 * @param bestIndex The index of the best member of the best polynomial coefficient set.
 */
void GeneticOptimization::mutatePopulation(int *bestIndices)
{
  ZernikePolynomial *aMember;
  
  for (int i = 0; i < POPULATION_SIZE; i++)
  {
    if (i == bestIndices[0] || i == bestIndices[1]) {
      continue; // Do not mutate the two previous best members.
    }

    aMember = &Population[i];
    /*    aMember->setAstigmatismX((aMember->getAstigmatismX() + bestMember->getAstigmatismX())/2);
    aMember->setAstigmatismY((aMember->getAstigmatismY() + bestMember->getAstigmatismY())/2);
    aMember->setDefocus((aMember->getDefocus() + bestMember->getDefocus())/2);
    aMember->setTrefoilX((aMember->getTrefoilX() + bestMember->getTrefoilX())/2);
    aMember->setTrefoilY((aMember->getTrefoilY() + bestMember->getTrefoilY())/2);*/
    /*aMember->setComaY((aMember->getComaY() + bestMember->getComaY())/2);*/
    // XXX/FIXME: Start with only 1 optimization parameter (spherical aberration).
    double mutationValue = ((rand() % 1024)/512.0 - 1)/4.0 * Population[bestIndices[0]].getSecondarySphericalAberration();
    aMember->setSecondarySphericalAberration(aMember->getSecondarySphericalAberration() + mutationValue);
    mutationValue = ((rand() % 1024)/512.0 - 1)/4.0 * Population[bestIndices[0]].getSphericalAberration();
    aMember->setSphericalAberration(aMember->getSphericalAberration() + mutationValue);
    mutationValue = ((rand() % 1024)/512.0 - 1)/4.0 * Population[bestIndices[0]].getComaX();
    aMember->setComaX(aMember->getComaX() + mutationValue);
    mutationValue = ((rand() % 1024)/512.0 - 1)/4.0 * Population[bestIndices[0]].getComaY();
    aMember->setComaY(aMember->getComaY() + mutationValue);

    aMember->setPower(aMember->focusCorrection());
  }
}

/**
 * Checks whether convergence has been reached, or not.
 *
 * @return True if the condition is satisfied.
 */
bool GeneticOptimization::isStopConditionSatisfied()
{
  /*double mean = 0, max;
  int i;*/

  if (iterationCount < 4) {
    return false;
  }

  double pIncrease = (bestFitness[iterationCount - 1] / bestFitness[iterationCount - 3]) - 1.0;
  std::ostringstream logSS;
  logSS << "pIncrease: " << pIncrease;
  LOGME( logSS.str() )
  logSS.str("");

  //if (pIncrease < 0.20) {
  return false; // XXX/FIXME: Remove
  if (pIncrease < 0.01) {
    return true;
  } else {
    return false;
  }


  
  // Calculate fitness mean.
  /*for (i = 0; i < POPULATION_SIZE; i++) {
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
  return standardDeviation/max < 0.01;*/
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
