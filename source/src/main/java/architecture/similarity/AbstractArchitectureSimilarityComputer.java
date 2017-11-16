package architecture.similarity;

import java.io.File;

public abstract class AbstractArchitectureSimilarityComputer {
	
	public abstract double computeSimilarity(File arcOne, File arcTwo);
	
}
