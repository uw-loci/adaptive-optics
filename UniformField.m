%function [SX SY Z] = UniformField(width, N) 
% Defines a unitary field (plane wave), of dimension width (meters)
% composed of NxN points.
function [F] = UniformField(width, N) 

%Source locations.
sy=linspace(-width/2,width/2,N);
sx=linspace(-width/2,width/2,N);
[SX SY]=meshgrid(sx,sy);
E=ones(N,N);

F.SX=SX;
F.SY=SY;
F.E=E;
F.opticalWidth=width;
F.actualWidth=width;

F.opName='UniformField';
F.opParam=sprintf('width=%1.3fmm', width*1e3);
