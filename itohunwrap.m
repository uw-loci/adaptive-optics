%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Function name : itohunwrap
% Version : a 
%
% Purpose : Function to perform One dimensional phase 
% unwrapper using Itoh's method 
% 
% References : Two-Dimensional phase unwrapping Theory, Algorithms 
% and Software. Dennis C. Ghiglia & Mark D. Pratt
% 
% File name : itohunwrap.m
% Author : B J Dew %
%
% Start date : 31st March 2004 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [unwrapped, wrap_diff] = itohunwrap(wrapped)

no_samples = length(wrapped);
unwrapped = wrapped*0.0;
wrap_diff = zeros(no_samples);
for i = 1:no_samples-1
  dumm = wrapped(i+1) - wrapped(i);
  wrap_diff(i) = atan2(sin(dumm),cos(dumm));
end
unwrapped(1) = wrapped(1);

for i = 2:no_samples
  unwrapped(i) = unwrapped(i-1) + wrap_diff(i-1);
end
