#!/usr/bin/python

import os, sys, subprocess
hostname=sys.argv[1]
cmd = ["java", "-cp", "bin/*", "-Djava.rmi.server.hostname=" + hostname, "cuneiform.Experiment"]
client = subprocess.Popen(cmd)
