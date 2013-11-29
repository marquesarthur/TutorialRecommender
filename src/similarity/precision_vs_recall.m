plot(precision_recall(:,3),precision_recall(:,2),'r');
xlabel("Recall");
ylabel("Precision");
title("Precision vs. Recall");
print -djpg "precision_recall.jpg";