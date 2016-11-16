# define installer name
Name "Novel Insight Scoring"
OutFile "Setup Novel Insight Scoring.exe"

# set desktop as install directory
InstallDir "$PROGRAMFILES64\Novel Insight Scoring"
 
# default section start
Section

MessageBox MB_YESNO "Do you want to install Novel Insight Scoring?" IDYES continue IDNO abort_installer
abort_installer:
Quit
continue:
 
# define output path
CreateDirectory "$PROGRAMFILES64\Novel Insight Scoring"
SetOutPath $INSTDIR
 
# specify file to go in output path
File *.dll
File ni-scoring.exe
File *.jar
File *.csv
File launch4j.*
File *.ico
File /r help
File /r ni-scoring_lib

CreateShortCut "$DESKTOP\NI Scoring.lnk" "$INSTDIR\ni-scoring.exe"

 
# define uninstaller name
WriteUninstaller $INSTDIR\uninstaller.exe
 
 
#-------
# default section end
SectionEnd
 
# create a section to define what the uninstaller does.
# the section will always be named "Uninstall"
Section "Uninstall"
 
# Always delete uninstaller first
Delete $INSTDIR\uninstaller.exe
 
# now delete installed files
Delete $INSTDIR\*.csv
Delete $INSTDIR\*.dll
Delete $INSTDIR\ni-scoring.exe
Delete $INSTDIR\*.jar
Delete $INSTDIR\launch4j.*
Delete $INSTDIR\*.ico
RMDir /r $INSTDIR\ni-scoring_lib
RMDir /r $INSTDIR\help
RMDir $INSTDIR
Delete "$DESKTOP\NI Scoring.lnk"
 
SectionEnd
