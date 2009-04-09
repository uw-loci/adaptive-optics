
  %%
  % Slit.  Huygen approach.
  %
  % SI-units
  clear all;

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  %% Define constants.

  %Wavelength.
  lambda=800e-9;
  %Source parameters.
  sourceWidth=1e-3; 
  sourceN=401;
  %Camera screen.
  %cameraWidth=0.6e-3;
  %cameraM=101;
  %Lens
  focalLength=5e-3;
  propDistance2=(5.0)*1e-3;
  cameraWidth=10e-6;
  cameraM=201;
  %distances
  propDistance1=0.50*focalLength;
  propDistance2=1.00*focalLength;
  propDistance3=1.50*focalLength;
  propDistance4=2.00*focalLength;
  propDistance5=3.00*focalLength;

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  %% Simulation.

  F1=UniformField(sourceWidth, sourceN);
  F2=CircularAperture(F1, sourceWidth);
  F3=ThinLens(F2, focalLength, lambda);

 F7=PropagateFresnel(F3, propDistance4, lambda, sourceWidth, sourceN);


 ZF3=ZernikeDecompose(F3, 'auto', lambda);
 ZF7=ZernikeDecompose(F7, 'auto', lambda);

  F1=SetFieldName(F1,'F1 - Uniform');
  F2=SetFieldName(F2,'F2 - Circular Apt.');
  F3=SetFieldName(F3,'F3 - Thin Lens');
  ZF3=SetFieldName(ZF3,'F3 - Zernike Modes');
  F7=SetFieldName(F7,'F7 - Prop 2.0f');
  ZF7=SetFieldName(ZF7,'F7 - Zernike Modes');



  Fields={F1 F2 F3 ZF3 F7 ZF7};


  %Information.
  str=sprintf('Source: width/height=%1.3fmm, %dx%d sources -> interval between sources: %1.3f wavelengths', ...
      sourceWidth*1e3, sourceN, sourceN, sourceWidth/sourceN/lambda);
  str=sprintf('%s\nCamera: width/height=%1.3fmm, %dx%d sources -> interval between sources: %1.3f wavelengths', ...
      str, cameraWidth*1e3, cameraM, cameraM, cameraWidth/cameraM/lambda);
  str=sprintf('%s\nLens f=%1.3fmm, Distance z=%1.3fmm', ...
      str, focalLength*1e3, propDistance2*1e3);
  str=sprintf('%s\nLambda=%dnm', ...
    str, lambda*1e9);
  str=sprintf('%s\nIntention to investigate the traversal of the wave aberration function numerically for a thin lens', str)

%Store outputs.
WriteSimulationData('./test1.sdt', Fields, str);

%Outputs.
%PlotField(F1);
%PlotField(F2);
%PlotField(F3);
%PlotField(F4);

