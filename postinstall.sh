if [ ! -f "$1/ios/Podfile" ]; then
  echo "skipping Podfile, "
  exit 0
fi

(grep -v '#' $1/ios/Podfile | grep -q 'use_frameworks' && echo "use_frameworks already exists in Podfile") || sed -i -e '/platform\ \:ios/a\'$'\n''use_frameworks!\'$'\n' $1/ios/Podfile ; sed -i -e '/flipper/ s/^#*/#/' $1/ios/Podfile