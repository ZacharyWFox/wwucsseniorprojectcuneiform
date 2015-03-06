#!/usr/bin/python

import subprocess, sys

# check args
hostname = sys.arg[0];
main = sys.arg[1];
codebase = bin/Server.jar
# copy over java binaries 

# ensure that we're in the binaries (somehow)

# start the registry
registry = subprocess.Popen("rmiregistry", stdout=subprocess.PIPE)
# start the GeneticServer process

# All of our jars are in <packageroot>/bin
cmd = ["java", "-cp", "bin/*", "-Djava.rmi.server.hostname=" + hostname, "-Djava.rmi.server.codebase=" + codebase, "genetics.GeneticServer"]
server = subprocess.Popen(cmd)

# keep writing Server output to log file.
with open("ServerOutput", "a") as outFile:
	while server.poll():
		[out, err] = server.communicate();
		if out:
			outFile.write(out)
		if err:
			outfile.write(err)

			
