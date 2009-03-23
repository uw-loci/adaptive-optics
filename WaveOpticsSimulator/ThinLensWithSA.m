%function [FO] = ThinLens(FI, f, SA, lambda) 
%Input field FI is multiplied by the phase profile of the
%thin lens, producing FO as the output.
%Lambda is the wavelength used.
%SA is the coefficient of the SA.
function [FO] = ThinLensWithSA(FI, f, SA, lambda) 

rho = sqrt((FI.SY).^2 + (FI.SX).^2);
%rhoScaled = rho/(0.5*FI.opticalWidth);
%theta = atan(SX./(SY+1e-12));
phi = -1/(2*f)*rho.^2;
phi = phi + SA.*rho.^4;

k=2*pi/lambda;
%Output
FO.SX=FI.SX;
FO.SY=FI.SY;
FO.actualWidth=FI.actualWidth;
FO.opticalWidth=FI.opticalWidth;
FO.E = FI.E .* exp(i*k*phi);

FO.opName='ThinLensWithSA';
FO.opParam=sprintf('f=%1.3fmm, SA=%1.3fwl, lambda=%dnm', f*1e3, SA/lambda, lambda*1e9);
