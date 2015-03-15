#Python script
#by Danielle Thurow
#strips extra info from status.txt
#leaving only the numbers and headers for each col


import os, sys, subprocess
filePath=sys.argv[1]

myfile = open(filePath, 'r')
outputFile = open("stripped.txt", "w")


outputFile.write("Generation    TopFitness    FitChange    Time\n")

for line in myfile:
    line = line.replace("Gen:", "")
    line = line.replace("topFit:", "   ")
    line = line.replace("fitChange:", "   ")
    line = line.replace("time:", "    ")
    outputFile.write(line)


print("it is done")




