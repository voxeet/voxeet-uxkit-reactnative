#!/bin/bash

ls -la .
path="`pwd`/ios"
echo $path
cd $path
carthage update --platform ios
