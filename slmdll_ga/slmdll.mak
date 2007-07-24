# Microsoft Developer Studio Generated NMAKE File, Based on slmdll.dsp
!IF "$(CFG)" == ""
CFG=slmdll - Win32 Debug
!MESSAGE No configuration specified. Defaulting to slmdll - Win32 Debug.
!ENDIF 

!IF "$(CFG)" != "slmdll - Win32 Release" && "$(CFG)" != "slmdll - Win32 Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "slmdll.mak" CFG="slmdll - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "slmdll - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "slmdll - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 

!IF  "$(CFG)" == "slmdll - Win32 Release"

OUTDIR=.\Release
INTDIR=.\Release
# Begin Custom Macros
OutDir=.\Release
# End Custom Macros

ALL : "$(OUTDIR)\slmdll.dll"


CLEAN :
	-@erase "$(INTDIR)\Int_wiscan.obj"
	-@erase "$(INTDIR)\optimization.obj"
	-@erase "$(INTDIR)\slmcontrol.obj"
	-@erase "$(INTDIR)\slmdll.obj"
	-@erase "$(INTDIR)\slmdll.pch"
	-@erase "$(INTDIR)\slmdll.res"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(OUTDIR)\slmdll.dll"
	-@erase "$(OUTDIR)\slmdll.exp"
	-@erase "$(OUTDIR)\slmdll.lib"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MD /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /D "_MBCS" /D "_AFXEXT" /Fp"$(INTDIR)\slmdll.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "NDEBUG" /mktyplib203 /win32 
RSC=rc.exe
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\slmdll.res" /d "NDEBUG" /d "_AFXDLL" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\slmdll.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=/nologo /subsystem:windows /dll /incremental:no /pdb:"$(OUTDIR)\slmdll.pdb" /machine:I386 /def:".\slmdll.def" /out:"$(OUTDIR)\slmdll.dll" /implib:"$(OUTDIR)\slmdll.lib" 
DEF_FILE= \
	".\slmdll.def"
LINK32_OBJS= \
	"$(INTDIR)\Int_wiscan.obj" \
	"$(INTDIR)\optimization.obj" \
	"$(INTDIR)\slmcontrol.obj" \
	"$(INTDIR)\slmdll.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\slmdll.res"

"$(OUTDIR)\slmdll.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

!ELSEIF  "$(CFG)" == "slmdll - Win32 Debug"

OUTDIR=.\Debug
INTDIR=.\Debug
# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

ALL : "$(OUTDIR)\slmdll.dll"


CLEAN :
	-@erase "$(INTDIR)\Int_wiscan.obj"
	-@erase "$(INTDIR)\optimization.obj"
	-@erase "$(INTDIR)\slmcontrol.obj"
	-@erase "$(INTDIR)\slmdll.obj"
	-@erase "$(INTDIR)\slmdll.pch"
	-@erase "$(INTDIR)\slmdll.res"
	-@erase "$(INTDIR)\StdAfx.obj"
	-@erase "$(INTDIR)\vc60.idb"
	-@erase "$(INTDIR)\vc60.pdb"
	-@erase "$(OUTDIR)\slmdll.dll"
	-@erase "$(OUTDIR)\slmdll.exp"
	-@erase "$(OUTDIR)\slmdll.ilk"
	-@erase "$(OUTDIR)\slmdll.lib"
	-@erase "$(OUTDIR)\slmdll.pdb"

"$(OUTDIR)" :
    if not exist "$(OUTDIR)/$(NULL)" mkdir "$(OUTDIR)"

CPP=cl.exe
CPP_PROJ=/nologo /MDd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /D "_MBCS" /D "_AFXEXT" /Fp"$(INTDIR)\slmdll.pch" /Yu"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

.c{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.obj::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.c{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cpp{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

.cxx{$(INTDIR)}.sbr::
   $(CPP) @<<
   $(CPP_PROJ) $< 
<<

MTL=midl.exe
MTL_PROJ=/nologo /D "_DEBUG" /mktyplib203 /win32 
RSC=rc.exe
RSC_PROJ=/l 0x409 /fo"$(INTDIR)\slmdll.res" /d "_DEBUG" /d "_AFXDLL" 
BSC32=bscmake.exe
BSC32_FLAGS=/nologo /o"$(OUTDIR)\slmdll.bsc" 
BSC32_SBRS= \
	
LINK32=link.exe
LINK32_FLAGS=Kernel32.lib BNSBoard\Debug\BNSBoard.lib /nologo /subsystem:windows /dll /incremental:yes /pdb:"$(OUTDIR)\slmdll.pdb" /debug /machine:I386 /def:".\slmdll.def" /out:"$(OUTDIR)\slmdll.dll" /implib:"$(OUTDIR)\slmdll.lib" /pdbtype:sept 
DEF_FILE= \
	".\slmdll.def"
LINK32_OBJS= \
	"$(INTDIR)\Int_wiscan.obj" \
	"$(INTDIR)\optimization.obj" \
	"$(INTDIR)\slmcontrol.obj" \
	"$(INTDIR)\slmdll.obj" \
	"$(INTDIR)\StdAfx.obj" \
	"$(INTDIR)\slmdll.res"

"$(OUTDIR)\slmdll.dll" : "$(OUTDIR)" $(DEF_FILE) $(LINK32_OBJS)
    $(LINK32) @<<
  $(LINK32_FLAGS) $(LINK32_OBJS)
<<

SOURCE="$(InputPath)"
DS_POSTBUILD_DEP=$(INTDIR)\postbld.dep

ALL : $(DS_POSTBUILD_DEP)

# Begin Custom Macros
OutDir=.\Debug
# End Custom Macros

$(DS_POSTBUILD_DEP) : "$(OUTDIR)\slmdll.dll"
   slmdll.bat
	echo Helper for Post-build step > "$(DS_POSTBUILD_DEP)"

!ENDIF 


!IF "$(NO_EXTERNAL_DEPS)" != "1"
!IF EXISTS("slmdll.dep")
!INCLUDE "slmdll.dep"
!ELSE 
!MESSAGE Warning: cannot find "slmdll.dep"
!ENDIF 
!ENDIF 


!IF "$(CFG)" == "slmdll - Win32 Release" || "$(CFG)" == "slmdll - Win32 Debug"
SOURCE=.\Int_wiscan.cpp

"$(INTDIR)\Int_wiscan.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\slmdll.pch"


SOURCE=.\optimization.cpp

"$(INTDIR)\optimization.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\slmdll.pch"


SOURCE=.\slmcontrol.cpp

"$(INTDIR)\slmcontrol.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\slmdll.pch"


SOURCE=.\slmdll.cpp

"$(INTDIR)\slmdll.obj" : $(SOURCE) "$(INTDIR)" "$(INTDIR)\slmdll.pch"


SOURCE=.\slmdll.rc

"$(INTDIR)\slmdll.res" : $(SOURCE) "$(INTDIR)"
	$(RSC) $(RSC_PROJ) $(SOURCE)


SOURCE=.\StdAfx.cpp

!IF  "$(CFG)" == "slmdll - Win32 Release"

CPP_SWITCHES=/nologo /MD /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /D "_MBCS" /D "_AFXEXT" /Fp"$(INTDIR)\slmdll.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\slmdll.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ELSEIF  "$(CFG)" == "slmdll - Win32 Debug"

CPP_SWITCHES=/nologo /MDd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_WINDLL" /D "_AFXDLL" /D "_MBCS" /D "_AFXEXT" /Fp"$(INTDIR)\slmdll.pch" /Yc"stdafx.h" /Fo"$(INTDIR)\\" /Fd"$(INTDIR)\\" /FD /GZ /c 

"$(INTDIR)\StdAfx.obj"	"$(INTDIR)\slmdll.pch" : $(SOURCE) "$(INTDIR)"
	$(CPP) @<<
  $(CPP_SWITCHES) $(SOURCE)
<<


!ENDIF 


!ENDIF 

