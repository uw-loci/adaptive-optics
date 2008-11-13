%function Etot=AddPointSources(Z,XP,YP,SX,SY,SourceE, lambda)
%Sums up all the point sources from a source field distribution, to obtain the superposed
%electric field at a distance point.

function Etot=AddPointSources(Z,XP,YP,SX,SY,SourceE, lambda)

k=2*pi/lambda;
N=length(SX);

r=sqrt(Z^2 + (YP-SY).^2 + (XP-SX).^2);

et=exp(i*k*r(:)).*SourceE(:)./((r(:)));

Etot=sum(et)/(N*N);

