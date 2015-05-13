from subprocess import call
import os.path
TASK = "assembleRelease"
COMMAND = "../../gradlew -PdistId=%s -PdistName=%s -PoutputRoot=%s %s"
OUTPUT_ROOT = "/Users/liangyx/Downloads/tmp/test"
FILE_TAPSTREAMTYPE = "src/main/java/com/tradehero/th/utils/metrics/tapstream/TapStreamType.java"
TAG_BEGIN = "BEGIN_ENUM"
TAG_END = "END_ENUM"

if not os.path.exists(FILE_TAPSTREAMTYPE):
    print "Check the file path of TapStreamType.java"
    exit()

with open(FILE_TAPSTREAMTYPE) as f:
    lines = f.readlines()
    isDefinedDist = False
    for line in lines:
        line = line.strip()
        if line.find(TAG_BEGIN) != -1:
            isDefinedDist = True
            continue
        if not isDefinedDist or not line:
            print "Invalid Line", line
            continue


        if line.find(TAG_END) != -1:
            break;

        index = line.find("(")
        if index != -1:
            dist_name = line[:index]
        line = line[index+1:]
        index = line.find(",")
        if index != -1:
            dist_id = line[:index]

        dist_id = dist_id.strip()
        dist_name = dist_name.strip()
        args = COMMAND % (dist_id, dist_name, OUTPUT_ROOT, TASK)
        print args
        call(args, shell=True)
        # Uncomment the follow code to build 4 APKs for testing.
        #if dist_id == "3":
        #    break;

