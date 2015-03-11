#!/usr/bin/python

import os, sys, subprocess
hostname=sys.argv[1]

os.chdir("/home/jungs3/wwucsseniorprojectcuneiform")
cmd = ["java", "-cp", "bin/JARS/*", "-Djava.rmi.server.hostname=" + hostname, "cuneiform.Experiment", "nohup"]
client = subprocess.Popen(cmd)
with open("Expermient.log", "a+") as outfile:
	while client.poll():
		[out, err] = server.communicate()
		if out:
			outfile.write(out)
		if err:
			outfile.write(err)
