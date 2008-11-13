
%%
% Slit.  Huygen approach.
%
% SI-units
clear all;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Define constants.

%Wavelength.
lambda=600e-9;
%Source parameters.
sourceWidth=1e-3; 
sourceN=401;
%Camera screen.
%cameraWidth=0.6e-3;
%cameraM=101;
cameraWidth=10e-6;
cameraM=201;
%Lens
focalLength=5e-3;
%Propagation distance
propDistance=(5.0)*1e-3;
%propDistance=(5.0+2.5)*1e-3;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Simulation.

F1=UniformField(sourceWidth, sourceN);
F2=CircularAperture(F1, sourceWidth);
F3=ThinLens(F2, focalLength, lambda);
F4=PropagateFullHF(F3, propDistance, lambda, cameraWidth, cameraM);
Fields={F1 F2 F3 F4};

%Information.
str=sprintf('Setup: (UniformField:F1) -> (CircularAperture:F2) -> (ThinLens:F3) -> (PropagateFullHF:F4)');
str=sprintf('%s\nPlane wave incident on thin lens (f) and propagates to a distance (z) away', ...
    str);
str=sprintf('%s\nSource: width/height=%1.3fmm, %dx%d sources -> interval between sources: %1.3f wavelengths', ...
    str, sourceWidth*1e3, sourceN, sourceN, sourceWidth/sourceN/lambda);
str=sprintf('%s\nCamera: width/height=%1.3fmm, %dx%d sources -> interval between sources: %1.3f wavelengths', ...
    str, cameraWidth*1e3, cameraM, cameraM, cameraWidth/cameraM/lambda);
str=sprintf('%s\nLens f=%1.3fmm, Distance z=%1.3fmm', ...
    str, focalLength*1e3, propDistance*1e3);
str=sprintf('%s\nLambda=%dnm', ...
    str, lambda*1e9);

%Store outputs.
WriteSimulationData('./Data/Lensf5mmd5mm_3.sdt', Fields, str);

%Outputs.
%PlotField(F1);
%PlotField(F2);
%PlotField(F3);
%PlotField(F4);

