#!/usr/bin/python

# RunDistributed.py
# Run a command on many machines with the same user credentials
# Requires package 'paramiko' https://github.com/paramiko/paramiko

import sys, paramiko
from getpass import getpass

if len(sys.argv) < 4:
    print "Usage: python RunDistributed.py username filename(containing list of addresses) command"
    exit()

userName = sys.argv[1]
fileName = sys.argv[2]
command = sys.argv[3]

# Get addresses from file
with open(fileName) as f:
    addresses = f.readlines()

# Set up our ssh client to auto add unknown hosts
ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

# Get ssh password from user
password = getpass("Password for user " + userName + ": ")

for addr in addresses:
    addr = addr.rstrip()
    ssh.connect(addr, username=userName, password=password)
    ssh_stdin, ssh_stdout, ssh_stderr = ssh.exec_command(command)
