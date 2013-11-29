precision_recall <- read.csv("C:/Users/gaya/prog/workspace/TutorialRecommender/joda-prec-recall.csv")

pdf("C:/Users/gaya/Dropbox/project/thesis/pics/joda_similarity_r.pdf")

plot(precision_recall$threshold,precision_recall$precision, "l", col = "red", 
     cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5, ylab = "Precision-Recall", 
     xlab="Threshold");

lines(precision_recall$threshold,precision_recall$recall, "l", col = 'green');

legend("topright",legend=c("Precision","Recall"), col=c("red","green"),
       text.col=c("red","green"), lty=1,
       inset=0.01)
dev.off()

# precision-recall curve
pdf("C:/Users/gaya/Dropbox/project/thesis/pics/joda_prec-rec_r.pdf")
plot(precision_recall$recall,precision_recall$precision,  col = 'red', "o", 
     ylim = c(-0.1, 1.04), cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5, 
     ylab = "Precision", xlab="Recall");
text(precision_recall$recall,precision_recall$precision, precision_recall$threshold, cex = 0.8, pos = "3", offset = 0.2)
dev.off()

# math library
precision_recall <- read.csv("C:/Users/gaya/prog/workspace/TutorialRecommender/math-prec-recall.csv")
pdf("C:/Users/gaya/Dropbox/project/thesis/pics/math_similarity_r.pdf")
plot(precision_recall$threshold,precision_recall$precision, "l", col = "red", 
     cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5, ylab = "Precision-Recall", 
     xlab="Threshold");

lines(precision_recall$threshold,precision_recall$recall, "l", col = 'green');

legend("topright",legend=c("Precision","Recall"), col=c("red","green"),
       text.col=c("red","green"), lty=1,
       inset=0.01)

dev.off()

# precision-recall curve
pdf("C:/Users/gaya/Dropbox/project/thesis/pics/math_prec-rec_r.pdf")
plot(precision_recall$recall,precision_recall$precision,  col = 'red', "o", 
     ylim = c(-0.1, 1.04), cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5,
     ylab = "Precision", xlab="Recall");
text(precision_recall$recall,precision_recall$precision, precision_recall$threshold, cex = 0.8, pos = "3", offset = 0.2)
dev.off()


# java tutorials
precision_recall <- read.csv("C:/Users/gaya/prog/workspace/TutorialRecommender/col-prec-recall.csv")
pdf("C:/Users/gaya/Dropbox/project/thesis/pics/col_similarity_r.pdf")
plot(precision_recall$threshold,precision_recall$precision, "l", col = "red", 
     cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5, ylab = "Precision-Recall", 
     xlab="Threshold");

lines(precision_recall$threshold,precision_recall$recall, "l", col = 'green');

legend("topright",legend=c("Precision","Recall"), col=c("red","green"),
       text.col=c("red","green"), lty=1,
       inset=0.01)

dev.off()

# precision-recall curve
pdf("C:/Users/gaya/Dropbox/project/thesis/pics/col_prec-rec_r.pdf")
plot(precision_recall$recall,precision_recall$precision,  col = 'red', "o", 
     cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5, ylim = c(-0.1, 1.04), 
     ylab = "Precision", xlab="Recall");
text(precision_recall$recall,precision_recall$precision, precision_recall$threshold, cex = 0.8, pos = "3", offset = 0.2)
dev.off()

# java tutorials - Jankov
precision_recall <- read.csv("C:/Users/gaya/prog/workspace/TutorialRecommender/jcol-prec-recall.csv")
pdf("C:/Users/gaya/Dropbox/project/thesis/pics/jcol_similarity_r.pdf")
plot(precision_recall$threshold,precision_recall$precision, "l", col = "red", 
     cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5, ylab = "Precision-Recall", 
     xlab="Threshold");

lines(precision_recall$threshold,precision_recall$recall, "l", col = 'green');

legend("topright",legend=c("Precision","Recall"), col=c("red","green"),
       text.col=c("red","green"), lty=1,
       inset=0.01)

dev.off()

# precision-recall curve
pdf("C:/Users/gaya/Dropbox/project/thesis/pics/jcol_prec-rec_r.pdf")
plot(precision_recall$recall,precision_recall$precision,  col = 'red', "o", 
     cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5, ylim = c(-0.1, 1.04), 
     ylab = "Precision", xlab="Recall");
text(precision_recall$recall,precision_recall$precision, precision_recall$threshold, cex = 0.8, pos = "3", offset = 0.2)
dev.off()

# Smack
precision_recall <- read.csv("C:/Users/gaya/prog/workspace/TutorialRecommender/smack-prec-recall.csv")
pdf("C:/Users/gaya/Dropbox/project/thesis/pics/smack_similarity_r.pdf")
plot(precision_recall$threshold,precision_recall$precision, "l", col = "red", 
     cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5, ylab = "Precision-Recall", 
     xlab="Threshold", main="Precision and Recall curves with Verious thresholds\nSmack");

lines(precision_recall$threshold,precision_recall$recall, "l", col = 'green');

legend("topright",legend=c("Precision","Recall"), col=c("red","green"),
       text.col=c("red","green"), lty=1,
       inset=0.01)

dev.off()

# precision-recall curve
pdf("C:/Users/gaya/Dropbox/project/thesis/pics/smack_prec-rec_r.pdf")
plot(precision_recall$recall,precision_recall$precision,  col = 'red', "o", 
     cex.main = 1.7, cex.lab = 1.5, cex.axis = 1.5, ylim = c(-0.1, 1.04), 
     ylab = "Precision", xlab="Recall", main="Precision-Recall\nSmack");
text(precision_recall$recall,precision_recall$precision, precision_recall$threshold, cex = 0.8, pos = "3", offset = 0.2)
dev.off()
