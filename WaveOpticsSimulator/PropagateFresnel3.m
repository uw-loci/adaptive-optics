%function FO = PropagateFresnel3(FI, z, lambda, width, M)
%Based on 2d convolution using the FFW implementation
%(Fastest filtering in the west).
%Uses slightly more advanced impulse response function.
%(Not using approximations)
%-More general than fresnel actually.
function FO = PropagateFresnel3(FI, z, lambda, width, M)

%Camera defined:
cy=linspace(-width/2,width/2,M);
cx=cy;
[CX CY]=meshgrid(cx,cy);


%Free space propagation.
k=2*pi/lambda;
G=j*k*z*exp(-j*k*sqrt(CX.^2 + CY.^2 + z^2))./(2*pi*(CX.^2 + CY.^2 + z^2)) ...
  .* (1 + 1./(j*k*sqrt(CX.^2 + CY.^2 + z^2)));

%FFW convolution
load fftexecutiontimes;
opt = detbestlength2(FFTrv,FFTiv,IFFTiv,size(FI.E),size(G),isreal(FI.E),isreal(G));
E  = fftolamopt2(G,FI.E,opt,'same');



FO.SX=CX;
FO.SY=CY;
FO.E=E;
FO.actualWidth = width;
FO.opticalWidth = width;

FO.opName='PropagateFresnel2';
FO.opParam=sprintf('z=%1.3fmm, lambda=%dnm, cameraWidth=%1.3fmm, cameraM=%d', ...
    z*1e3, lambda*1e9, width*1e3, M);
