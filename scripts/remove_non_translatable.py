#!/usr/bin/env python

import re
import sys
from os import listdir
from os.path import isfile, join, basename, normpath

def usage():
	print """
	usage: remove_non_translatable <path/to/values-xxx/folder>
	"""


if len(sys.argv) <= 1:
	usage()
	exit(0)

mypath = sys.argv[1]
if not basename(normpath(mypath)).startswith("values-"):
	print "Target folder must start with \"values-\""
	exit(0)

files = [ f for f in listdir(mypath) if isfile(join(mypath, f)) and f.endswith(".xml") ]
for translateFile in files:
	print "Processing %s" % (translateFile)

	filePath = join(mypath, translateFile)
	
	sourceWithOutTranslatable = None
	with open(filePath, "r") as f:
		sourceWithOutTranslatable = "".join([line for line in f.readlines() if "translatable=\"false\"" not in line])
	
	if sourceWithOutTranslatable:
		with open(filePath, "w") as f:
			f.write(sourceWithOutTranslatable)

