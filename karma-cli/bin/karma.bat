@REM =================================================
@REM
@REM Windows batch file, so we support Windows as well
@REM
@REM Thanks to Age Mooy (amooy@wisdom.nl)
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
SET KARMA_MAIN_CLASS="nl.toolforge.karma.cli.CLI"
SET KARMA_CLASSPATH="%KARMA_HOME%\resources;%KARMA_HOME%\lib\ant-1.6.1.jar;%KARMA_HOME%\lib\ant-junit.jar;%KARMA_HOME%\lib\ant-launcher-1.6.1.jar;%KARMA_HOME%\lib\commons-beanutils-1.6.1.jar;%KARMA_HOME%\lib\commons-cli-1.0.jar;%KARMA_HOME%\lib\commons-collections-2.1.jar;%KARMA_HOME%\lib\commons-digester-1.5.jar;%KARMA_HOME%\lib\commons-io-20030203.000550.jar;%KARMA_HOME%\lib\commons-lang-1.0.1.jar;%KARMA_HOME%\lib\commons-logging-1.0.3.jar;%KARMA_HOME%\lib\cvslib-3.6.jar;%KARMA_HOME%\lib\junit-3.8.1.jar;%KARMA_HOME%\lib\karma-cli-1.0.jar;%KARMA_HOME%\lib\karma-core-1.0.jar;%KARMA_HOME%\lib\log4j-1.2.8.jar;%KARMA_HOME%\lib\maven-1.0.jar;%KARMA_HOME%\lib\toolforge-core-1.0.jar;%KARMA_HOME%\lib\xerces-2.4.0.jar;%KARMA_HOME%\lib\xercesImpl-2.4.0.jar;%KARMA_HOME%\lib\xmlParserAPIs-2.2.1.jar;%JAVA_HOME%\lib\tools.jar"
SET KARMA_LOGLEVEL=INFO

REM Run the Karma application
%KARMA_JAVACMD% -Dkarma.home="%KARMA_HOME%" -Dloglevel=%KARMA_LOGLEVEL% -cp %KARMA_CLASSPATH% %KARMA_MAIN_CLASS%
goto end

:end
REM unset local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT
:endNT
@endlocal
