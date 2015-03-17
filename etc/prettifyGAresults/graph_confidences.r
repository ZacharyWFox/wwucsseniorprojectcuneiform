##by danielle Thurow
##show results all pretty like

data = read.table("stripped_confidences.txt", header=TRUE)
attach(data)
plot(Difference)
mean(Difference)

par(mfrow=c(1,2))
plot(Levenshtein)
plot(NWAlgo)

