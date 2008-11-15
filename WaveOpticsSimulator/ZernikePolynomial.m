%function Z = ZernikePolynomial(n, m, p, th)
function Z = ZernikePolynomial(n, m, p, th)

dm0 = (m == 0);
Nnm = sqrt(2*(n+1)/(1 + dm0));
%disp(strcat(['Nnm = ' num2str(Nnm)]))

Rnm = zeros(size(th));
for s = [0:(n - abs(m))/2]
  num = (-1).^s.*factorial(n - s);
  den = factorial(s).*factorial(0.5*(n + abs(m))-s).*factorial(0.5*(n - abs(m))-s);
%  disp(strcat([num2str(num./den) 'p^' num2str(n - 2*s)]))
  Rnm = Rnm + num./den.*p.^(n - 2*s);
end

if m >= 0
    Z = Nnm .* Rnm .* cos(m*th);
else
    Z = -Nnm .* Rnm .* sin(m*th);
end
