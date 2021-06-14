echo Transpiling...
tsc || exit 1


echo Setting version in code...
VERSION=$(node -e "console.log(require('./package.json').version);")
FILE=android/src/main/java/com/voxeet/RNVoxeetConferencekitModule.java
echo Current version = $VERSION
sed -i '' "s/____REACT_NATIVE_VERSION____/$VERSION/g" $FILE

echo building and publishing...
npm publish || exit 1

echo Reverting modification in $FILE...
git checkout $FILE

echo done
