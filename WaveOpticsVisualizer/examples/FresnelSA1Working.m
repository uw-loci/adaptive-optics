%close all;
  %%
  % Slit.  Huygen approach.
  %
  % SI-units
  clear all;

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  %% Define constants.

  %Wavelength.
  lambda=1000e-9;
  %Source parameters.
  initialFieldWidth=20.0e-3; 
  sourceWidth=10.0e-3; 
  sourceN=1000;
  %Camera screen.
  cameraWidth=20.0e-3;
  cameraM=1000;
  % Aberration: 1.0 of SA.
  abDegs=[11];
  abCoef=[0.1]*lambda;
%  abCoef=[0.0]*lambda;
  %distances
  propDistance1=100e-2;   % 50cm 

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  %% Simulation.

  F1=UniformField(initialFieldWidth, sourceN);
  F2=CircularAperture(F1, sourceWidth);
  F3=ZernikeAberrationMix(F2, abDegs, abCoef, lambda);

  F4=PropagateFresnel2(F3, propDistance1, lambda, cameraWidth, cameraM);

  ZF3=ZernikeDecompose(F3,'auto',lambda)
  ZF4=ZernikeDecompose(F4,'auto',lambda)

%imagesc(abs(F4.E).^2);
figure;
PlotField(F3,sourceWidth)
figure;
PlotField(F4,cameraWidth)
figure;
stem(ZF3.List)
figure;
stem(ZF4.List)
figure;
mesh(F4.SX,F4.SY,angle(F4.E))



