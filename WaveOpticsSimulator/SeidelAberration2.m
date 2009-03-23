%function [FO] = SeidelAberration2(FI, diam, deg, coeff, lambda) 
%Input field FI is multiplied by the phase profile of the
%aberration, producing FO as the output.
%Lambda is the wavelength used.
%Deg is the degree of the aberration, 4 for SA.
%Coeff is the coefficient of the aberration.
function [FO] = SeidelAberration2(FI, diam, deg, coeff, lambda) 

rho = sqrt((FI.SY).^2 + (FI.SX).^2);
rhoScaled = rho/(0.5*diam);
%theta = atan(SX./(SY+1e-12));
phi = -coeff*rhoScaled.^(deg);

k=2*pi/lambda;
%Output
FO.SX=FI.SX;
FO.SY=FI.SY;
FO.actualWidth=FI.actualWidth;
FO.opticalWidth=FI.opticalWidth;
FO.E = FI.E .* exp(i*k*phi);

FO.opName='SeidelAberration2';
FO.opParam=sprintf('deg=%d, diam=%1.3fmm, coeff=%1.2f wl, lambda=%dnm', ...
    deg, diam*1e3, coeff/lambda, lambda*1e9);

