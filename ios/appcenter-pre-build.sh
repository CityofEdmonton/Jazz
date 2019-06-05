#!/usr/bin/env bash

# Example: Change bundle name of an iOS app for non-production
echo "Start changing ENV VAR"
awk -v group="\"$GROUP"\"";" -v license="\"$LICENCES"\"";" -v clientID="\"$SERVER_CLIENT_ID"\"";"\
 '{if ($3=="license") {$5=license; print} else if ($3=="group") {$5=group; print} else if ($3=="clientID") {$5=clientID; print} else {print}}' JazziOS/Config.swift.example.swift > JazziOS/Config.swift
echo "Done changing ENV VAR"
