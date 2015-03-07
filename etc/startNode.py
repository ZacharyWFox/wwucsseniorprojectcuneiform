#!/usr/bin/python

import subprocess, sys, os
os.chdir(os.path.expanduser("~"))
os.chdir("/home/foxz/workspace/Parser/bin/JARS")
# check args
hostname = sys.argv[1]
codebase = "Server.jar"
# copy over java binaries 

# ensure that we're in the binaries (somehow)

# start the registry
#registry = subprocess.Popen("rmiregistry", stdout=subprocess.PIPE)
# start the GeneticServer process

# All of our jars are in <packageroot>/bin
cmd = ["java", "-cp", "*", "-Djava.rmi.server.hostname=" + hostname, "-Djava.rmi.server.codebase=file:" + codebase, "genetics.GeneticServer"]
server = subprocess.Popen(cmd)

# keep writing Server output to log file.
#with open("ServerOutputregistry.log", "a+") as outFile:
#	while server.poll():
#		[out, err] = server.communicate();
#		if out:
#			outFile.write(out)
#		if err:
#			outfile.write(err)

#server.kill()
