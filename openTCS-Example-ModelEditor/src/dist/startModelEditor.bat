@echo off
rem
rem Start the openTCS Model Editor application.
rem

rem Set window title
title Model Editor (openTCS)

rem Don't export variables to the parent shell
setlocal

rem Set base directory names.
set OPENTCS_BASE=.
set OPENTCS_HOME=.
set OPENTCS_CONFIGDIR=%OPENTCS_HOME%\config
set OPENTCS_LIBDIR=%OPENTCS_BASE%\lib

rem Set the class path
set OPENTCS_CP=%OPENTCS_LIBDIR%\*;
set OPENTCS_CP=%OPENTCS_CP%;%OPENTCS_LIBDIR%\openTCS-extensions\*;

rem XXX Be a bit more clever to find out the name of the JVM runtime.
set JAVA=javaw

rem Start plant overview
start /b %JAVA% -enableassertions ^
    -Dopentcs.base="%OPENTCS_BASE%" ^
    -Dopentcs.home="%OPENTCS_HOME%" ^
    -Dopentcs.configuration.provider=gestalt ^
    -Dopentcs.configuration.reload.interval=10000 ^
    -Dopentcs.cfg4j.reload.interval=10000 ^
    -Djava.util.logging.config.file="%OPENTCS_CONFIGDIR%\logging.config" ^
    -Dsun.java2d.d3d=false ^
    -XX:-OmitStackTraceInFastThrow ^
    -classpath "%OPENTCS_CP%" ^
    -splash:bin/splash-image.gif ^
    org.opentcs.modeleditor.RunModelEditor
