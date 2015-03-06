import subprocess

# ensure that we're in the right directory (somehow)

# start the registry
registry = subprocess.Popen("rmiregistry", stdout=subprocess.PIPE);
# start the GeneticServer process

# keep writing Server output to log file.
