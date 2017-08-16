# CPPGAi
Java multithreaded asynchronous non-dominated sorting genetic algorithm with GUI (integer version)

Compile: JavaFX 8

By Scott Matsumura

Version 1.0

This genetic algorithm is intended to be easy to use and fast.  You can either use just the “GeneticAlgorithmInteger” class by creating your own class and “extend” it or use the GUI.  Extending the “GeneticAlgorithmInteger” class required one to @Override the “fitnessFunction”.  To reuse the GUI, rewrite the “fitnessFunction” located in the “MyGeneticAlgorithminteger” class and set your own default variables in the DefaultVariable constants class.

The genetic algorithm works by randomly selecting two genes.  If another thread is currently using one these genes, it selects another.  These two genes are crossovered twice to produce two new genes.  If one or both of these child genes have a better fitness than the parents, the parents are replaced by the child genes.

This technique has the benefit of being able to multithread asynchronously and maintain a diverse population.  If there is a population of 1000, it attempts to find nearly 1000 solutions.  The disadvantage of this technique is that it will get close to a solution quickly then slow down significantly.  The issue is that since each chromosome is finding its own solution, the genetic algorithm has difficulty finding the last fine-tuned gene combinations.

This could be solved by utilizing multiple populations in a coarse-grained parallel method and allow each population to converge on their own solution.  This would give many combinations of similar chromosomes to triangulate onto a solution.  Instead, I opted for a direct solution if not a pure one.  The optimization option is a steepest-hill-climb algorithm.  It takes a certain percentage of genes and uses brute-force to find the best fitness.
