%function [FO] = ZernikeDecompose(FI, diam, lambda) 
%Decomposes the input field into Zernike modes (coefficients)
%FI is the input field
%diam is the diameter of the interesting part of the field
%-if diam='auto' ZernikeDecompose will find the diameter corresponding to
% a 95% drop from the peak value
%lambda is the wavelength to be used.
function [FO] = ZernikeDecompose(FI, diam, lambda) 

k=2*pi/lambda;

ANG=unwrap2d(angle(FI.E));

if (diam == 'auto')
  %Auto select the diameter where it goes to 5% of the max.
  Irr=0.5*abs(FI.E).^2;
  cnt=sum(sum(Irr < max(Irr(:))*0.05));
  cnt2=size(Irr,1);
  diam=sqrt(4/pi*(cnt2^2 - cnt))*FI.actualWidth/cnt2;
  sprintf('diam is %1.3fmm\n', diam*1e3)
end


rho = sqrt((FI.SY).^2 + (FI.SX).^2);
rhoScaled = rho/(0.5*diam);

%Isolate the relevant part of the phase.
firstInd=find(FI.SX(1,:) <= -diam/2, 1, 'last'); 
lastInd=find(FI.SX(1,:) >= diam/2, 1, 'first'); 
MANG=ANG(firstInd:lastInd,firstInd:lastInd);

%Reproduce the area and convert to polar coordinates.
npix=size(MANG,1);
sx=linspace(-diam/2,diam/2,npix);
sy=linspace(-diam/2,diam/2,npix);
[SX SY] = meshgrid(sx,sy);
%Change into polar coordinates.
[TH R]=cart2pol(SX, SY);
RSC=R/(diam/2);

%Prepare the nList and mList:
%Basically a table for converting Zernike indices into
%n and m values for the Zernike function.
nList=[];
mList=[];
for pp=0:10
  nList=[nList pp*ones(1,pp+1)];
  if (rem(pp,2)==0)
    %even.
    A=[2:2:pp];
    B=[A;-A];
    C=[0 B(:)'];
  else
    %odd.
    A=[1:2:pp];
    B=[A;-A];
    C=B(:)';
  end
  mList=[mList C];
end
size(mList)

%Decompose into Zernike functions.
MpList=[];
for p=1:40
  ZW=ZernikePolynomial(nList(p), mList(p), RSC, TH);
  ZW=ZW.*(RSC<=1);
  Mp=1/(pi/4)*1/(npix^2).*sum(sum(MANG.*ZW));
  MpList=[MpList Mp];
end


%Output
%FO.SX=FI.SX;
%FO.SY=FI.SY;
%FO.actualWidth=FI.actualWidth;
%FO.opticalWidth=FI.opticalWidth;
%FO.E = FI.E .* exp(i*k*phi);

FO.opName='ZernikeDecompose';
FO.opParam=sprintf('diam=%1.3fmm, lambda=%1.3nm', diam*1e3, lambda*1e9);
FO.List=MpList;

