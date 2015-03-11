#!/usr/bin/python

import os, sys, subprocess
hostname=sys.argv[1]
debug = ""
if len(sys.argv) > 2:
	debug="debug"

os.chdir("/home/jungs3/wwucsseniorprojectcuneiform")
cmd = ["java", "-cp", "bin/JARS/*", "-Djava.rmi.server.hostname=" + hostname, "cuneiform.Experiment"]

if debug:
	cmd.append(debug)
	
client = subprocess.Popen(cmd)

