package architecture.similarity.pairwise;

import java.util.Map;

import architecture.commons.Graph;
import architecture.commons.Module;

public abstract class AbstractMetricExtractor {
	
	public abstract Map<String, Double> getMetrics(Graph<Module> arc);
}
