
/**
 * Logger.
 * This class handles all debug and data logging for the module.
 */

#define LOG_FILE    "AdaptiveOptics.log"



#include <iostream>
#include <sstream>

using namespace std;


#define LOGME(str1) Logger::Instance()->log( __FILE__, __LINE__, str1 );



class Logger
{
  public:
    static Logger *Instance();
    void log( string fileName, int lineNumber, string buffer );

  private:
    static Logger *pInstance;
};


