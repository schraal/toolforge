@echo off
REM
REM Requires IzPack
REM %IZPACK_HOME% must point to the IzPack installation directory
REM

if "%IZPACK_HOME%"=="" goto no_izpack_home
if "%1"=="" goto usage

"%IZPACK_HOME%\bin\compile" karma-installer.xml -b . -o ..\target\karma-%1-INSTALLER
goto :EOF


:no_izpack_home
echo.	
echo Fatal error: 
echo.
echo IZPACK_HOME environment variable must be set to the IzPack installation directory.

:usage
echo.
echo Usage:
echo.
echo Usage   :    make-dist 'version'
echo Example :    make-dist 1-0-rc1 (results in karma-1-0-rc1-INSTALLER.jar)
echo.
echo The installer will be created in ..\target\karma-'version'-INSTALLER.jar
