%function FO = PropagateFresnel2(FI, z, lambda, width, M)
%Based on 2d convolution using the FFW implementation
%(Fastest filtering in the west).
function FO = PropagateFresnel2(FI, z, lambda, width, M)

%Camera defined:
cy=linspace(-width/2,width/2,M);
cx=cy;
[CX CY]=meshgrid(cx,cy);


%Free space propagation.
k=2*pi/lambda;
h=exp(-j*k*z).*j*k/(2*pi*z);
h=h*exp(-j*k*(CX.^2+CY.^2)/(2*z));
%FFW convolution
load fftexecutiontimes;
opt = detbestlength2(FFTrv,FFTiv,IFFTiv,size(FI.E),size(h),isreal(FI.E),isreal(h));
E  = fftolamopt2(h,FI.E,opt,'same');



FO.SX=CX;
FO.SY=CY;
FO.E=E;
FO.actualWidth = width;
FO.opticalWidth = width;

FO.opName='PropagateFresnel2';
FO.opParam=sprintf('z=%1.3fmm, lambda=%dnm, cameraWidth=%1.3fmm, cameraM=%d', ...
    z*1e3, lambda*1e9, width*1e3, M);
