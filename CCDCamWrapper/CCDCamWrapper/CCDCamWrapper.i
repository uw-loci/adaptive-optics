/* ccdcam.i */
%module CCDCamWrapper
%{
#include <string>
/* Includes the header in the wrapper code */
extern bool init_camera(int driver);
extern char *get_note();
extern int capture_frame();
extern unsigned short get_frame_at_pos(int x, int y);
extern int test_me();
extern int set_roi(int x, int y, int dx, int dy);
extern int shutdown();
%}

extern bool init_camera(int driver);
extern char *get_note();
extern int capture_frame();
extern unsigned short get_frame_at_pos(int x, int y);
extern int test_me();
extern int set_roi(int x, int y, int dx, int dy);
extern int shutdown();

