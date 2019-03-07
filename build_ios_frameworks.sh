#!/bin/bash

echo "> installing ios dependencies (carthage)"
carthage update --platform ios --project-directory `dirname "$0"`/ios
