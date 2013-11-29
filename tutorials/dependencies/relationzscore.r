# load data
depdata = read.csv("C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\dependencydone_copadded.csv")

depdata$positive = as.numeric(depdata$positive)
depdata$negative = as.numeric(depdata$negative)
depdata$functional = as.numeric(depdata$functional)
depdata$structural = as.numeric(depdata$structural)
depdata$descriptive = as.numeric(depdata$descriptive)
depdata$instruction = as.numeric(depdata$instruction)
depdata$interCLT = as.numeric(depdata$interCLT)
depdata$other = as.numeric(depdata$other)

library(sqldf)
relstat = sqldf("select relation, sum(positive) as npositive, count(*)  as nobs from depdata group by relation")

# Compute population statistic
prc.positive.total <- sum(relstat$npositive) / sum(relstat$nobs)

expected.positive <- relstat$nobs * prc.positive.total

delta.positive <- relstat$npositive - expected.positive

# The standard deviation of a binomial variable 
std.deviation <- sqrt(relstat$nobs * prc.positive.total * (1 - prc.positive.total))

relstat$z.score <- delta.positive / std.deviation

relstat$prc.positive = relstat$npositive/relstat$nobs

relstat$prc.negative = (relstat$nobs-relstat$npositive)/relstat$nobs


write.table(relstat, "C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\Probs\\relstat.csv", sep=",")
