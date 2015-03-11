#!/usr/bin/python

import subprocess, sys, os
# check args
hostname = sys.argv[1]
path=sys.argv[2]
codebase = "Server.jar"
os.chdir(path)

# All of our jars are in <packageroot>/bin
cmd = ["java", "-cp", "bin/JARS/*", "-Djava.rmi.server.hostname=" + hostname, "-Djava.rmi.server.codebase=file:" + codebase, "genetics.GeneticServer", hostname , "nohup"]
server = subprocess.Popen(cmd)
#server.communicate();
# keep writing Server output to log file.
#with open(hostname + ".log", "a+") as outFile:
#       while server.poll():
#               [out, err] = server.communicate();
#               if out:
#                       outFile.write(out)
#               if err:
#                       outfile.write(err)

#server.kill()

