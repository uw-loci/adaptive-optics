#include <math.h>



double tiltx_fix_func(double comax){
    return 2*comax;
}


double tilty_fix_func(double comay){
    return 2*comay;
}


double focus_fix_func(double spher, double astigx, double astigy){
    return (3*spher + sqrt(astigx*astigx + astigy*astigy)/2);
}





//GA 

#define ParallelNum 8
#define ArrayNum (ParallelNum + 1)




class slm_ga{


    private :

        //parameter array, the last one save the best value
        char paracomax[ArrayNum];
        char paracomay[ArrayNum];
        char parastigx[ArrayNum];
        char parastigy[ArrayNum];
        char paraspher[ArrayNum];
        
        //best intensity value
        double bestinten;
        

        double tiltx_fix_func(double comax);
        double tilty_fix_func(double comay);
        double focus_fix_func(double spher, double astigx, double astigy);




};