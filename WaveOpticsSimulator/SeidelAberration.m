%function [FO] = SeidelAberration(FI, deg, coeff, lambda) 
%Input field FI is multiplied by the phase profile of the
%aberration, producing FO as the output.
%Lambda is the wavelength used.
%Deg is the degree of the aberration, 4 for SA.
%Coeff is the coefficient of the aberration.
function [FO] = SeidelAberration(FI, deg, coeff, lambda) 

rho = sqrt((FI.SY).^2 + (FI.SX).^2);
%rhoScaled = rho/(0.5*FI.opticalWidth);
%theta = atan(SX./(SY+1e-12));
phi = -coeff*rho.^(deg);

k=2*pi/lambda;
%Output
FO.SX=FI.SX;
FO.SY=FI.SY;
FO.actualWidth=FI.actualWidth;
FO.opticalWidth=FI.opticalWidth;
FO.E = FI.E .* exp(i*k*phi);

FO.opName='SeidelAberration';
FO.opParam=sprintf('deg=%d, coeff=%1.2f wl, lambda=%dnm', ...
    deg, coeff/lambda, lambda*1e9);

