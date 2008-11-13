%function FO = PropagateFullHF(FI, z, lambda, width, M)
function FO = PropagateFullHF(FI, z, lambda, width, M)

%Camera defined:
cy=linspace(-width/2,width/2,M);
cx=cy;
[CX CY]=meshgrid(cx,cy);

E=zeros(M,M);
for lr = 1:M % Each camera screen row.
  strcat([num2str(lr) ' / ' num2str(M)]) 
  for lc = 1:M % Each camera screen column.
    %Distance from each source to point.
    E(lr,lc)=AddPointSources(z, CX(lr,lc), CY(lr,lc), FI.SX, FI.SY, FI.E, lambda);
  end
end


FO.SX=CX;
FO.SY=CY;
FO.E=E;
FO.actualWidth = width;
FO.opticalWidth = width;

FO.opName='PropagateFullHF';
FO.opParam=sprintf('z=%1.3fmm, lambda=%dnm, cameraWidth=%1.3fmm, cameraM=%d', ...
    z*1e3, lambda*1e9, width*1e3, M);
