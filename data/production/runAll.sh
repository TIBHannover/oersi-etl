#!/bin/sh

for folder in data/production/*
  do command=(./gradlew run --args ''"$folder"' data/production/oersi.properties')
  "${command[@]}"
done