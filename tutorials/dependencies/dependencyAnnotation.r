# load data
depdata = read.csv("C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\dependencydone_for_stat.csv")

depdata$positive = as.numeric(depdata$positive)
depdata$negative = as.numeric(depdata$negative)
depdata$functional = as.numeric(depdata$functional)
depdata$structural = as.numeric(depdata$structural)
depdata$descriptive = as.numeric(depdata$descriptive)
depdata$instruction = as.numeric(depdata$instruction)
depdata$interCLT = as.numeric(depdata$interCLT)
depdata$other = as.numeric(depdata$other)

library(sqldf)
depstat = sqldf("select governor, relation, dependant, sum(positive) as npositive, count(*)  as nobs from depdata group by governor, relation, dependant")

# Compute population statistic
prc.positive.total <- sum(depstat$npositive) / sum(depstat$nobs)

expected.positive <- depstat$nobs * prc.positive.total

delta.positive <- depstat$npositive - expected.positive

# The standard deviation of a binomial variable 
std.deviation <- sqrt(depstat$nobs * prc.positive.total * (1 - prc.positive.total))

depstat$z.score <- delta.positive / std.deviation

write.table(depstat, "C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\Probs\\depstat.csv", sep=",")

# DONE: Now let's test on actual data

par.size <- 5
paragraph <- sample(1:nrow(all.deps), par.size)

w <- sum(all.deps[paragraph, 'z.score']) / sqrt(par.size)