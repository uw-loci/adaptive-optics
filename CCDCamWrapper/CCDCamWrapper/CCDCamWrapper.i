/* ccdcam.i */
%module CCDCamWrapper
%{
#include <string>
/* Includes the header in the wrapper code */
extern bool init_camera();
extern char *get_note();
extern int capture_frame();
extern unsigned char get_frame_at_pos(int x, int y);
extern int test_me();
extern int set_roi(int x, int y, int dx, int dy);
extern int shutdown();
%}

extern bool init_camera();
extern char *get_note();
extern int capture_frame();
extern unsigned char get_frame_at_pos(int x, int y);
extern int test_me();
extern int set_roi(int x, int y, int dx, int dy);
extern int shutdown();

