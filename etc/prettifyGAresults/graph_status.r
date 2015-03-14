##R code ##
##to make our results look pretty!##

data = read.table("stripped.txt", header=TRUE)
attach(data)
par(mfrow=c(2,2))
plot(TopFitness~Generation, main="Highest Fitness level for each Gen.", xlab="Generation No.", ylab="Top Fitness")
plot(FitChange~Generation, main="Change of Fitness for each Gen.", xlab="Generation No.", ylab="Change in Top Fitness")
plot(Time~Generation, main="Time to Complete each Generation", xlab="Generation No.", ylab="Time to complete (in sec)")

