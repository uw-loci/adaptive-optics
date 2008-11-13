%function [FO] = SeidelAberration(FI, deg, lambda) 
%Input field FI is multiplied by the phase profile of the
%aberration, producing FO as the output.
%Lambda is the wavelength used.
%Deg is the degree of the aberration, 4 for SA.
function [FO] = SeidelAberration(FI, deg, lambda) 

rho = sqrt((FI.SY).^2 + (FI.SX).^2);
%rhoScaled = rho/(0.5*FI.opticalWidth);
%theta = atan(SX./(SY+1e-12));
phi = -rho.^(deg);

k=2*pi/lambda;
%Output
FO.SX=FI.SX;
FO.SY=FI.SY;
FO.actualWidth=FI.actualWidth;
FO.opticalWidth=FI.opticalWidth;
FO.E = FI.E .* exp(i*k*phi);

FO.opName='SeidelAberration';
FO.opParam=sprintf('deg=%d, lambda=%dnm', deg, lambda*1e9);

