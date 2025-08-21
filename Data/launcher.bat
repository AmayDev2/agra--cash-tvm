@echo off
REM Usage: launcher.bat <dir> <oldFilename> <delaySec> <java-launch-command...>

SETLOCAL ENABLEDELAYEDEXPANSION
SET "DIR=%~1"
SET "OLD=%~2"
SET "DELAY=%~3"
SET "NEW_FILE_PATH=%~4"
SET "NEW_FILE_NAME=%~5"
SET "PID=%~6"



IF "%DIR%"=="" (
  ECHO Usage: %~nx0 dir oldFilename delaySec java-launch-command
  EXIT /B 1
)
PUSHD "%DIR%" 2>nul || (
  ECHO Directory not found: %DIR%
  EXIT /B 1
)

IF NOT EXIST "%OLD%" (
  ECHO File "%OLD%" not found in "%DIR%"
  POPD
  EXIT /B 1
)

ECHO Waiting %DELAY% seconds before renaming...
TIMEOUT /T %DELAY% /NOBREAK >nul

REM Extract components of OLD
FOR %%F IN ("%OLD%") DO (
  SET "BASE=%%~nF"
  SET "EXT=%%~xF"
)
SET "DEP=%BASE%_deprecated%EXT%"

REM If DEP already exists, delete it to allow proper rename
IF EXIST "%DEP%" (
  ECHO Deprecated file "%DEP%" exists — deleting it.
  DEL /F /Q "%DEP%"
)

REM Now safely rename OLD to DEP
IF EXIST "%OLD%" (
  ECHO Renaming "%OLD%" → "%DEP%"
  REN "%OLD%" "%DEP%" || (
    ECHO Rename failed for "%OLD%"
    POPD
    EXIT /B 1
  )
  ECHO Renamed "%OLD%" → "%DEP%"
) ELSE (
  ECHO Old file "%OLD%" not found — skipping.
)

ECHO Waiting %DELAY% seconds before renaming new file...
TIMEOUT /T %DELAY% /NOBREAK >nul

REM Split new file name into base & extension
FOR %%F IN ("%NEW_FILE_NAME%") DO (
  SET "NEW_BASE=%%~nF"
  SET "NEW_EXT=%%~xF"
)

ECHO Renaming new file "%NEW_FILE_NAME%" → "%OLD%"
REN "%NEW_FILE_NAME%" "%OLD%" || (
  ECHO Rename failed: could not rename "%NEW_FILE_NAME%" to "%OLD%"
  POPD
  EXIT /B 1
)

ECHO Renamed "%NEW_FILE_NAME%" → "%OLD%"



POPD
EXIT /B %EXIT_CODE%

