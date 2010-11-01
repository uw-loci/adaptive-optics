/* ccdcam.i */
%module CCDCamWrapper
%{
#include <string>
/* Includes the header in the wrapper code */
extern bool init_camera();
extern char *get_note();
extern int capture_frame();
extern unsigned char get_frame_at_pos(int index);
extern int test_me();
%}

extern bool init_camera();
extern char *get_note();
extern int capture_frame();
extern unsigned char get_frame_at_pos(int index);
extern int test_me();
