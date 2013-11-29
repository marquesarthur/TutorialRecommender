# check for invalid dependnacies
sqldf('select governor, relation, dependant, count(*) from depdata group by governor, relation,dependant having count(*)=1 ')

# count of negative and positive cases
sqldf("select positive, negative, count(*) from depdata group by positive, negative")

# number of unique relations
sqldf("select governor, relation, dependant from depdata group by governor, relation, dependant")

# counts for dependency classes
sqldf("select functional, structural, descriptive, instruction,interCLT, other, count(*) from depdata group by functional, structural, descriptive, instruction, interCLT, other")

# probability of each dependency
depProb = sqldf("select governor, relation, dependant, sum(positive)/count(*) as pprob, sum(negative)/count(*) as nprob, (sum(positive)*LOG(count(*)))/count(*) as pcprob, (sum(negative)*LOG(count(*)))/count(*) as ncprob  from depdata group by governor, relation, dependant")
write.table(depProb, "C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\Probs\\depProbabilities.csv", sep=",")

# probability of each dependency class
depClassProb = sqldf("select functional, structural, descriptive, instruction,interCLT, other, sum(positive)/count(*) as pprob, sum(negative)/count(*) as nprob, (sum(positive)*LOG(count(*)))/count(*) as pcprob, (sum(negative)*LOG(count(*)))/count(*) as ncprob  from depdata group by functional, structural, descriptive, instruction,interCLT, other")
write.table(depClassProb, "C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\Probs\\depClassProb.csv", sep=",")

# probability of relation
relProb = sqldf("select relation, sum(positive)/count(*) as pprob, sum(negative)/count(*) as nprob, (sum(positive)*LOG(count(*)))/count(*) as pcprob, (sum(negative)*LOG(count(*)))/count(*) as ncprob  from depdata group by relation")
write.table(relProb, "C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\Probs\\relProb.csv", sep=",")

# probability of governor
govProb = sqldf("select governor, sum(positive)/count(*) as pprob, sum(negative)/count(*) as nprob, (sum(positive)*LOG(count(*)))/count(*) as pcprob, (sum(negative)*LOG(count(*)))/count(*) as ncprob  from depdata group by governor")
write.table(govProb, "C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\Probs\\govProb.csv", sep=",")

# probability of dependant
dependantProb = sqldf("select dependant, sum(positive)/count(*) as pprob, sum(negative)/count(*) as nprob, (sum(positive)*LOG(count(*)))/count(*) as pcprob, (sum(negative)*LOG(count(*)))/count(*) as ncprob  from depdata group by dependant")
write.table(dependantProb, "C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\dependencies\\Probs\\dependantProb.csv", sep=",")
