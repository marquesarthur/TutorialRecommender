kappa = read.csv("C:\\Users\\gaya\\prog\\workspace\\TutorialRecommender\\tutorials\\annotation\\kappa_scores.txt")

scores = t(cbind(kappa$kappa, kappa$kappa.max))

par(xpd=T, mar=par()$mar+c(0,0,0,13))

barplot(scores, col=heat.colors(2), names.arg=kappa$tutorial, beside = TRUE, cex.names=0.8, ylim = c(0,1)) 

# Place the legend at (6,30) using heat colors
legend(19, 1, c("kappa", "kappa.max"), cex=0.8, fill=heat.colors(2));

# Restore default clipping rect
par(mar=c(5, 4, 4, 2) + 0.1)