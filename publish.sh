echo Transpiling...
tsc || exit 1


echo Setting version in code...
VERSION=$(node -e "console.log(require('./package.json').version);")
echo Current version = $VERSION
sed -i '' "s/____REACT_NATIVE_VERSION____/$VERSION/g" android/src/main/java/com/voxeet/RNVoxeetConferenceKitModule.java

echo building and publishing...
npm publish || exit 1

echo Reverting modification in RNVoxeetConferenceKitModule.java...
git checkout android/src/main/java/com/voxeet/RNVoxeetConferenceKitModule.java

echo done
