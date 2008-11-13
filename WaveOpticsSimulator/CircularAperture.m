%function [FO] = CircularAperture(FI, diameter) 
%Input field FI goes through an aperture of size diameter,
%the output (FO) is the input field with the appropriate
%parts zerod out by the aperture.
function [FO] = CircularAperture(FI, diameter) 

M=sqrt((FI.SX).^2 + (FI.SY).^2) <= (diameter/2);

FO.SX=FI.SX;
FO.SY=FI.SY;
FO.E=FI.E.*M;
FO.opticalWidth=diameter;
FO.actualWidth=FI.actualWidth;

FO.opName='CircularAperture';
FO.opParam=sprintf('diam=%1.3fmm', diameter*1e3);
