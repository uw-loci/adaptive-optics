%function FO = PropagateFullHF(FI, z, lambda, width, M)
function FO = PropagateFullHF(FI, z, lambda, width, M)

%Camera defined:
cy=linspace(-width/2,width/2,M);
cx=cy;
[CX CY]=meshgrid(cx,cy);

k = 2*pi/lambda;

A=exp(i*k/(2*z)*(CX.^2+CY.^2));
B=fft2(FI.E.*exp(i*k/(2*z)*(FI.SX.^2+FI.SY.^2)))));
B=fftshift(B);
E=A.*B;

%E=zeros(M,M);
%for lr = 1:M % Each camera screen row.
%  disp(sprintf('%d / %d', lr, M));
%  for lc = 1:M % Each camera screen column.
    %Distance from each source to point.
    %E(lr,lc)=AddPointSources(z, CX(lr,lc), CY(lr,lc), FI.SX, FI.SY, FI.E, lambda);
%    x2=CX(lr,lc);
%    y2=CY(lr,lc);
    %Note that some constants have been removed.
    %See nice treatment in Encyclopedia of Optical Engineering p.359. (google books)
%    E(lr,lc)=sum(sum(exp(i*k/(2*z)*(x2^2+y2^2)).*CACHED));
%  end
%end

FO.SX=CX;
FO.SY=CY;
FO.E=E;
FO.actualWidth = width;
FO.opticalWidth = width;

FO.opName='PropagateFullHF';
FO.opParam=sprintf('z=%1.3fmm, lambda=%dnm, cameraWidth=%1.3fmm, cameraM=%d', ...
    z*1e3, lambda*1e9, width*1e3, M);
