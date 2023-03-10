#!/bin/sh

for folder in data/production/*
  do echo "$folder";command=(./gradlew run --args ''"$folder"' data/production/oersi.properties')
  "${command[@]}"
done