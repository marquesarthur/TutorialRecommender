# load data
depdata = read.csv("C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\Probs\\jtStat.csv")

depdata$positive = as.numeric(depdata$positive)
depdata$negative = as.numeric(depdata$negative)
depdata$functional = as.numeric(depdata$functional)
depdata$structural = as.numeric(depdata$structural)
depdata$descriptive = as.numeric(depdata$descriptive)
depdata$instruction = as.numeric(depdata$instruction)
depdata$interCLT = as.numeric(depdata$interCLT)
depdata$other = as.numeric(depdata$other)

library(sqldf)
depstat = sqldf("select governor, relation, dependent, sum(positive) as npositive, count(*)  as nobs from depdata group by governor, relation, dependent")
# Compute population statistic
prc.positive.total <- sum(depstat$npositive) / sum(depstat$nobs)

expected.positive <- depstat$nobs * prc.positive.total

delta.positive <- depstat$npositive - expected.positive

# The standard deviation of a binomial variable 
std.deviation <- sqrt(depstat$nobs * prc.positive.total * (1 - prc.positive.total))

depstat$z.score <- delta.positive / std.deviation
