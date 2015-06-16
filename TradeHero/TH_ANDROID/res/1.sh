var="$1"
var="${var#"${var%%[![:space:]]*}"}"   # remove leading whitespace characters
var="${var%"${var##*[![:space:]]}"}"   # remove trailing whitespace characters

echo "===$var==="
find . -name $var
find . -name $var | xargs rm -fr
