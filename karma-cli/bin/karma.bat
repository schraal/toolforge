@REM =================================================
@REM
@REM Windows batch file, so we support Windows as well
@REM
@REM Thanks to Age Mooy (amooy@wisdom.nl)
@REM Fixed by Chris Spelberg (c.spelberg@toolforge.nl)
@REM Enhanced by Hilbert Schraal (h.schraal@toolforge.nl)
@REM
@REM $Id$
@REM =================================================

@echo off

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM ========== START VALIDATION ==========
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto end

:OkJHome
if exist %JAVA_HOME%\nul goto chkMHome

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = %JAVA_HOME%
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto end

:chkMHome
if not "%KARMA_HOME%"=="" goto valMHome

echo.
echo ERROR: KARMA_HOME not found in your environment.
echo Please set the KARMA_HOME variable in your environment to match the
echo location of the Karma installation
echo.
goto end

:valMHome
if exist "%KARMA_HOME%\bin\karma.bat" goto init

echo.
echo ERROR: KARMA_HOME is set to an invalid directory.
echo KARMA_HOME = %KARMA_HOME%
echo Please set the KARMA_HOME variable in your environment to match the
echo location of the KArma installation
echo.
goto end
@REM ========== END VALIDATION ==========

:init
SET KARMA_JAVACMD="%JAVA_HOME%\bin\java.exe"
SET KARMA_LOGLEVEL=DEBUG
SET ANT_LOGLEVEL=INFO
SET KARMA_CLASSPATH="%KARMA_HOME%\resources;%KARMA_HOME%\lib\;%KARMA_HOME%\lib\plugins\;%JAVA_HOME%\lib\tools.jar"
SET KARMA_LAUNCHER_JAR=%KARMA_HOME%\lib\karma-launcher.jar

%KARMA_JAVACMD% -cp "%KARMA_LAUNCHER_JAR%" -DKARMA_CLASSPATH=%KARMA_CLASSPATH% -Dkarma.home="%KARMA_HOME%" -Dloglevel=%KARMA_LOGLEVEL% -Dantloglevel=%ANT_LOGLEVEL% nl.toolforge.karma.launcher.KarmaLauncher nl.toolforge.karma.cli.FireAway %*
goto end

:end
REM unset local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT
:endNT
@endlocal
