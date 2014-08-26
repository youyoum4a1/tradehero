#!/usr/bin/env python

# Note that this script can only run using python 2.x.x

# Steps for producing missing translation
# 1. From Android project folder, run `lint --check MissingTranslation .`
# 2. Save the output to lint_MissingTranslation.txt
# 3. Run this script
# 4. csvfile.csv will be the output to be used by translators

# Steps for import back to Android: in progress

import re
import csv

lint_missing_translation_file = 'lint_MissingTranslation.txt'
export_file = 'csvfile.csv'
lint_separator = '~~~~~~~~~~~~~~~~~'

regx_key = r"Error: \"([A-Za-z0-9_]+)\" is not translated"
regx_languages = r"translated in (.+) \[MissingTranslation\]"
regx_en_translation = r">([^<]+)<\/string>"

target_languages = ['"ko" (Korean)', '"zh" (Chinese)']

lint_file = open(lint_missing_translation_file, "r")
lint_report = lint_file.read()
lint_file.close()

blocks = lint_report.split(lint_separator)

with open(export_file, "wb") as csv_file:
    translation_writer = csv.writer(csv_file, delimiter=',', quoting=csv.QUOTE_MINIMAL)
    for block in blocks:
        print(block)
        m = re.search(regx_key, block)
        key = m.group(1) if m is not None else None

        m = re.search(regx_languages, block)
        languages = m.group(1) if m is not None else None

        m = re.search(regx_en_translation, block)
        en_translation = m.group(1) if m is not None else None

        if key is not None and languages is not None and en_translation is not None:
            rows = [key, en_translation]
            for target_language in target_languages:
                rows.append("" if target_language in languages else "[translated]")

            translation_writer.writerow(rows)
        else:
            print block