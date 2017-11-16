package architecture.similarity.pairwise;

import java.io.File;
import java.util.Map;

import architecture.similarity.AbstractArchitectureSimilarityComputer;

public abstract class PairwiseSimilarityComputer extends AbstractArchitectureSimilarityComputer {
	
	private AbstractMetricExtractor metricExtractor;
	private AbstractComparator comparator;
	
	@Override
	public double computeSimilarity(File arcOne, File arcTwo) {
		Map<String, Double> m1 = metricExtractor.getMetrics(arcOne);
		Map<String, Double> m2 = metricExtractor.getMetrics(arcTwo);
		
		return comparator.compare(m1, m2);
	}
	
	
}
