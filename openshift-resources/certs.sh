#!/bin/sh
for FILE in /usr/local/share/ca-certificates/WCF/*;
do keytool -import -trustcacerts -cacerts -storepass changeit -noprompt -alias $FILE -file $FILE
done