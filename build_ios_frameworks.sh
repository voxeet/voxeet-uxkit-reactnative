#!/bin/bash

echo `pwd`
cd ./ios
carthage update --platform ios
