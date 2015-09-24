#!/usr/bin/python
# -*- coding: UTF-8 -*-
import os
import string

def process(line):
    channelName = line.split('(')[0]
    channelNum = line.split('(')[1].split(',')[0]
    print "Processing " + channelName + ' with id = ' + channelNum

    # Update the channel id
    os.system('sed -i .bk \'s/VERSION = 101/VERSION = ' + channelNum + '/\' src/main/java/com/tradehero/th/utils/Constants.java')

    # Do build
    os.system('../../gradlew assembleRelease')

    # Change back the channel id
    os.system('sed -i .bk \'s/VERSION = ' + channelNum + '/VERSION = 101/\' src/main/java/com/tradehero/th/utils/Constants.java')

    # Rename the APK packages
    os.rename('build/outputs/apk/tradehero-release-default.apk', 'build/outputs/apk/tradehero-' + channelName + '.apk')

    return


# Read in the channels file
fileName = 'src/main/java/com/tradehero/th/utils/metrics/tapstream/TapStreamType.java'
with open(fileName, 'r') as input:
    lines = input.readlines()
    lines = [line.strip() for line in lines]    ## Strip newlines
    lines = [line for line in lines if line]    ## Remove blank
    for line in lines:
        if ('MarketSegment.CHINA' in line):
            process(line)



