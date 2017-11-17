package architecture.similarity;

import java.io.File;

public abstract class AbstractArchitectureSimilarityComputer {
	
	public abstract double computeSimilarity(File arcOne, File arcTwo);
	
	public abstract double getNormalizedDifference(double simValue);
	
	public double computeDifference(File arcOne, File arcTwo) {
		return getNormalizedDifference(computeSimilarity(arcOne, arcTwo));
	}
	
}
