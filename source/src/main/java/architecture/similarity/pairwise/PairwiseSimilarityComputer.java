package architecture.similarity.pairwise;

import java.util.Map;

import architecture.commons.Graph;
import architecture.commons.Module;
import architecture.similarity.AbstractArchitectureSimilarityComputer;

public abstract class PairwiseSimilarityComputer extends AbstractArchitectureSimilarityComputer {
	
	private AbstractMetricExtractor metricExtractor;
	private AbstractComparator comparator;
	
	public double computeSimilarity(Graph<Module> arcOne, Graph<Module> arcTwo) {
		Map<String, Double> m1 = metricExtractor.getMetrics(arcOne);
		Map<String, Double> m2 = metricExtractor.getMetrics(arcTwo);
		
		return comparator.compare(m1, m2);
	}
	
	
}