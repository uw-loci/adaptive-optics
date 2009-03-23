%function [FO] = ZernikeAberration(diam, deg, coeff, N, lambda) 
%Diam is the diamater of the optical field.
%Lambda is the wavelength used.
%Deg are the degrees of the aberration, 4 for SA.
%Coeff are the corresponding coefficients of the aberrations.
%Lambda is the wavelength
%NxN is the grid area of the field.
function [FO] = ZernikeAberration(diam, deg, coeff, N, lambda) 

%Reproduce the area and convert to polar coordinates.
sx=linspace(-diam/2,diam/2,N);
sy=linspace(-diam/2,diam/2,N);
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


ZTOT=zeros(N,N);
for p=1:length(deg)
  m=deg(p);
  ZW=coeff(p)*ZernikePolynomial(nList(m), mList(m), RSC, TH);
  ZW=ZW.*(RSC<=1);
  ZTOT=ZTOT + ZW;
end


k=2*pi/lambda;

%Output
FO.SX=SX;
FO.SY=SY;
FO.actualWidth=diam;
FO.opticalWidth=diam;
FO.E = exp(i*k*ZTOT) .* (RSC <= 1);

FO.opName='ZernikeAberration';
desc=strcat([ 'degs=[' num2str(deg) '] coefs=[' num2str(coeff/lambda) '] wls']);
FO.opParam=sprintf('diam=%1.3fmm, %s, lambda:%dnm', diam*1e3, desc, lambda*1e9);



