set.seed(0)

# Let's generate some data
generate.deps <- function(n) {
  nobs <- round(runif(n, 4, 20))
  return(data.frame(nobs=nobs,
                    npositive=round(runif(n) * nobs)))
}

all.deps <- generate.deps(30)

# Now let's compute the z.scores for each dependency

# Compute population statistic
prc.positive.total <- sum(all.deps$npositive) / sum(all.deps$nobs)

expected.positive <- all.deps$nobs * prc.positive.total

delta.positive <- all.deps$npositive - expected.positive

# The standard deviation of a binomial variable 
std.deviation <- sqrt(all.deps$nobs * prc.positive.total * (1 - prc.positive.total))

all.deps$z.score <- delta.positive / std.deviation

# DONE: Now let's test on actual data

par.size <- 5
paragraph <- sample(1:nrow(all.deps), par.size)

w <- sum(all.deps[paragraph, 'z.score']) / sqrt(par.size)