# Read-Cluster-based-Classification

##INTRODUCTION
A cluster-based sequence classification algorithm RCTC (Read Cluster-based Taxonomy Classification) is proposed to solve the problem of low classification accuracy caused by the short length and high diversity of sequences. The method combines the unsupervised learning process of the metagenomic sequence clustering algorithm SpaRC (Spark Read Clustering) proposed by our team, which clusters sequences of the same genome into a cluster. The sequences within the clusters are classified using Kraken2 and Clark and then uniformly reclassified. The experimental results show that RCTC can effectively improve the classification performance compared to Kraken2 and Clark tools that directly classify short sequences. In addition, re-clustering based on TaxID significantly improves the clustering performance, and the size of the cluster is increased by an order of magnitude.


## steps:

1: Run sequence clustering algorithm: SpaRC 
SpaRC link:https://bitbucket.org/LizhenShi/sparc/src/master/

2: Run KRAKEN2 OR CLARK(Other better classification tools can also replace it); 


3: Merge the dataframe of clustered reads and classified reads.


4: Reclassify reads in cluster uniformly.



