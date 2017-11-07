package architecture.similarity;

import architecture.commons.Graph;
import architecture.commons.Module;

public abstract class AbstractArchitectureSimilarityComputer {
	
	public abstract double computeSimilarity(Graph<Module> arcOne, Graph<Module> arcTwo);
	
}
