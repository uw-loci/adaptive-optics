@echo off
javap -s -private -classpath %~dps1 %~n1 > %~s2