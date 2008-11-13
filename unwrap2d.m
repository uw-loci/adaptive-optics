% M file driving routine for the Itoh unwrapping algorithm
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%
% %
% M-file properties %
% %
% Summary : The driving routine to drive the Itoh %
% unwrapper to give a 2-D unwrapper %
% %
% File name : drvitohunwrap.m %
% Function name : drvitohunwrap %
% Author : B J Dew %
% Creation date : Thr July 15th 2004 %
% %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%
function unwrappedImage = unwrap2d(w_arr)

u1_arr = w_arr*0.0;
dim = size(w_arr);
for i = 1 : dim(1)
  row = w_arr(:,i);
  row = itohunwrap(row);
  u1_arr(:,i) = row;
end

for i = 1 : dim(2)
  col = u1_arr(i,:);
  diff0 = col*0.0;
  diff1 = col*0.0;
  dumm = col*0.0;
  col = itohunwrap(col);
  u1_arr(i,:) = col;
end
unwrappedImage = u1_arr;
