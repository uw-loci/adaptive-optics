#!/bin/csh
#
set echo
#
cp bmp_io.H /$HOME/include
#
g++ -c -g bmp_io.C >& compiler.out
if ( $status != 0 ) then
  echo "Errors compiling bmp_io.C."
  exit
endif
rm compiler.out
#
mv bmp_io.o ~/lib/$ARCH/bmp_io.o
#
echo "A new version of bmp_io has been created."
