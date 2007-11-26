
#include "StdAfx.h"
#include "Logger.h"

#include <iostream>
#include <fstream>

using namespace std;


Logger *Logger::pInstance = 0; // initialize pointer

/**
 * Return the single instance of the class.
 *
 * @return The single instance of Logger.
 */
Logger *Logger::Instance () 
{
  if (pInstance == 0)  // is it the first call?
  {  
    pInstance = new Logger; // create sole instance
  }

  return pInstance; // address of sole instance
}


/**
 * Write to the log file.
 *
 * @param fileName The file name of source file. (__FILE__).
 * @param lineNumber The location of the log call. (__LINE__).
 * @param buffer The data (string stream) to be appended to the log file.
 */
void Logger::log( string fileName, int lineNumber, string buffer )
{
  fstream file;

  file.open(LOG_FILE, ios::out|ios::app);
  file <<  fileName << ":" << lineNumber << " " << buffer << endl;
	file.close();
}
