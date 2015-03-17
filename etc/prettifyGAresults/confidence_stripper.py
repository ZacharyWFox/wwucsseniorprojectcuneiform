#Python script
#by Danielle Thurow
#strips extra info from status.txt
#leaving only the numbers and headers for each col


import os, sys, subprocess
filePath=sys.argv[1]

myfile = open(filePath, 'r')
outputFile = open("stripped_Confidences.txt", "w")


outputFile.write("Levenshtein NWAlgo Difference\n")

for line in myfile:
    line = line.replace("lev:", "")
    line = line.replace("NW:", "   ")
    line = line.replace("diff:", "   ")
    outputFile.write(line)


print("it is done")
