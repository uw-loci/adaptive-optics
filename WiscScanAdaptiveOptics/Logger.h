
/**
 * Logger.
 * This class handles all debug and data logging for the module.
 */

#define LOG_FILE    "C:/gunnsteinn/debug/AdaptiveOptics.log"


#include <string>
using std::string;

#define LOGME(str1) Logger::Instance()->log( __FILE__, __LINE__, str1 );


class Logger
{
  public:
    static Logger *Instance();
    void log( string fileName, int lineNumber, string buffer );

  private:
    static Logger *pInstance;
};


