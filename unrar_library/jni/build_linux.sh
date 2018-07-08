#!/bin/bash

clear
NDK=~/Library/Android/sdk/ndk-bundle

$NDK/ndk-build clean
$NDK/ndk-build -j8 NDK_DEBUG=0 -B
