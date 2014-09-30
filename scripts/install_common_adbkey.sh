#!/bin/bash
echo This script will overwrite your debug key in your home folder

CURRENT_FOLDER=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
echo Copying key from $CURRENT_FOLDER/debugadbkey to $HOME/.android ...
cp $CURRENT_FOLDER/debugadbkey/* $HOME/.android/
echo Done.
