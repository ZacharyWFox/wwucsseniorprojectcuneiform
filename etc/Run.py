#!/usr/bin/python

# RunDistributed.py
# Run a command on many machines with the same user credentials
# Requires package 'paramiko' https://github.com/paramiko/paramiko

import sys, os, subprocess
#from getpass import getpass

#if len(sys.argv) < 4:
#    print "Usage: python RunDistributed.py username filename(containing list of addresses) command"
#    exit()

#userName = sys.argv[1]
fileName = sys.argv[1]
#command = sys.argv[3]
#pycmd = "python /home/jungs3/wwucsseniorprojectcuneiform/etc/startNode.py " 
home="/home/jungs3/wwucsseniorprojectcuneiform/"
# Get addresses from file
with open(fileName) as f:
    addresses = f.readlines()

# Set up our ssh client to auto add unknown hosts
#ssh = paramiko.SSHClient()
#ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

# Get ssh password from user
#password = getpass("Password for user " + userName + ": ")

for addr in addresses:
    addr = addr.rstrip()
    command =  "python " + home + "etc/startNode.py " + addr + " " + home
    cmd = ["python", home + "etc/startNode.py", addr, home + "bin/jars"]
    rockscmd = "rocks run host " + addr + " command=\"" + command + "\""
    bigcmd = ["rocks", "run", "host", addr, "command=\"" + command + "\""]
    print addr
    server = subprocess.Popen(bigcmd);
    #ssh.connect(addr, username=userName, password=password)
    #ssh_stdin, ssh_stdout, ssh_stderr = ssh.exec_command(command)
#TODO start client
