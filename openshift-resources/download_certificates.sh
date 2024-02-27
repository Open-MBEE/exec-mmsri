#!/bin/bash

function try {
    $@
    return $?
}

function catch {
    echo "Error occurred: $1"
    # Handle error here or exit
    exit 1
}

# Run your commands within the try-catch block

# Wget commands
# try wget --no-check-certificate -r -np -nd -R "index.html*" /wcf/latest/crt/ -P /usr/local/share/ca-certificates/WCF || catch "wget command failed"
# try wget --no-check-certificate -r -np -nd -R "index.html*" /dod/latest/ -P /usr/local/share/ca-certificates/WCF || catch "wget command failed"

# Curl and unzip commands
try curl -L -o /usr/local/share/ca-certificates/WCF/crt.zip /wcf/latest/crt.zip || catch "curl command failed"
try unzip -o /usr/local/share/ca-certificates/WCF/crt.zip -d /usr/local/share/ca-certificates/WCF || catch "unzip command failed"
try curl -L -o /usr/local/share/ca-certificates/WCF/dod.zip /dod/latest/dod.zip || catch "curl command failed"
try unzip -o /usr/local/share/ca-certificates/WCF/dod.zip -d /usr/local/share/ca-certificates/WCF || catch "unzip failed"

echo "Commands executed successfully"

