%function [FO] = ThinLens(FI, f, lambda) 
%Input field FI is multiplied by the phase profile of the
%thin lens, producing FO as the output.
%Lambda is the wavelength used.
function [FO] = ThinLens(FI, f, lambda) 

rho = sqrt((FI.SY).^2 + (FI.SX).^2);
%rhoScaled = rho/(0.5*FI.opticalWidth);
%theta = atan(SX./(SY+1e-12));
phi = -1/(2*f)*rho.^2;

k=2*pi/lambda;
%Output
FO.SX=FI.SX;
FO.SY=FI.SY;
FO.actualWidth=FI.actualWidth;
FO.opticalWidth=FI.opticalWidth;
FO.E = FI.E .* exp(i*k*phi);

FO.opName='ThinLens';
FO.opParam=sprintf('f=%1.3fmm, lambda=%dnm', f*1e3, lambda*1e9);
