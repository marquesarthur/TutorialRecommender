plot(precision_recall(:,1),precision_recall(:,2),'r');
hold on;
plot(precision_recall(:,1),precision_recall(:,3),'g');
title("Precision and Recall of IR system with various thresholds");
xlabel("Threshold");
ylabel("Precision-Recall");
legend("Precision", "Recall");
hold off;

print -djpg "precision_recall_vs_threshold.jpg";


plot(precision_recall(:,3),precision_recall(:,2),'r');
print -djpg "precision_recall.jpg";