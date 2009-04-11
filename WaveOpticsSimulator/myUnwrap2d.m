function unwrappedImg = myUnwrap2d(wrappedImg)

[rs cs] = size(wrappedImg);
if (rs ~= cs)
  'only works for NxN images'
  return
end

unwrappedImg=zeros(rs,cs);
%unwrappedImg=wrappedImg;

angles = 360;
R=round(rs/2);

angleRes = 90 / ((rs-1)/2 + (cs-1)/2) * pi/180;

theta = 0;
while (theta < 2*pi)
  theta*180/pi


  p = 0;
  mat=[];
  while (p < R)
    % Location:
    % x = p cos(theta)
    % y = p sin(theta)
  
    m = round(R + p * cos(theta));
    n = round(R + p * sin(theta));
    if ((m > 0) && (m <= cs) && (n > 0) && (n <= rs))
      val = wrappedImg(m,n);
      mat=[mat val];
    end
    p = p + 1;
  end

  unwrapMat = unwrap(mat);

  p = 0;
  mat=[];
  while (p < R)
    % Location:
    % x = p cos(theta)
    % y = p sin(theta)
  
    m = round(R + p * cos(theta));
    n = round(R + p * sin(theta));
    if ((m > 0) && (m <= cs) && (n > 0) && (n <= rs))
      unwrappedImg(m,n) = unwrapMat(p+1);
    end
    p = p + 1;
  end



  theta = theta + angleRes;
end
