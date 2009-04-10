
function PlotField(F,diam)

Irr=0.5*abs(F.E).^2;
%%ANG=unwrap2d(angle(F.E));
ANG=myUnwrap2d(angle(F.E));
ANG=ANG .* ((F.SX.^2 + F.SY.^2) < diam^2/4);
N=length(F.SX);

%%
TL1=strcat(['Field, width=' num2str(F.actualWidth*1e3) 'mm, N=' num2str(length(F.SX))]);
ind1=round((N-1)/2+1);
ind2=round((N-1)/2+1);
TL2=strcat(['I(center) =' num2str(Irr(ind1,ind2)) ' AOC=' num2str(sum(sum(Irr))/N/N)]);

x=F.SX(1,:);
y=F.SY(:,1);

subplot(2,2,1);
imagesc(x*1e3,y*1e3,Irr);
title({TL1;TL2});
colorbar;

subplot(2,2,2);
mesh(F.SX*1e3, F.SY*1e3,Irr/max(Irr(:)));

subplot(2,2,3);
ind=round((N-1)/2+1);
imagesc(x*1e3,y*1e3,ANG-ANG(ind,ind));
colorbar;

subplot(2,2,4);
ind=round((N-1)/2+1);
mesh(F.SX*1000,F.SY*1000,ANG-ANG(ind,ind));

