#!/bin/bash

jar=MJ.jar
refJar=MJRF.jar

myFileFolder=myFiles/
refFileFolder=refFiles/

for file in *.java;
do
	echo "-----------------------------------"
	echo "$file"
	java -jar $jar $file
done

for myFiles in *.java.ast.dot;
do
	echo "***********************************"
	echo "renaming an and moving $myFiles"
	mv $myFiles $myFileFolder
done

for file2 in *.java;
do
	echo "————————————————————————————————————"
	echo "$file2"
	java -jar $refJar $file2
done

for refFiles in *.java.ast.dot;
do
	echo "***********************************"
	echo "renaming an and moving $refFiles"
	mv $refFiles $refFileFolder
done

diff -r $myFileFolder $refFileFolder



