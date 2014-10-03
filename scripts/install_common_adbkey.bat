@echo off
echo This script will overwrite your debug key in your home folder

set TARGET_FOLDER=%USERPROFILE%\.android\
set CURRENT_FOLDER=%~dp0

echo Copying key from "%CURRENT_FOLDER%" to "%TARGET_FOLDER%"
copy %CURRENT_FOLDER%\debugadbkey\* %TARGET_FOLDER%
