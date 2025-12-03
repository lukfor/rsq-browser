#!/bin/bash

set -e

## ensure that you are in the folder with the collected chunks
## CAVE: delete or move chunks.txt (only chunklist) before executing this script.

## get merged text files (only lines with content)
awk -F',' 'FNR==1 && NR!=1 { next } $3 != 0 { print }' *.txt > merged.txt