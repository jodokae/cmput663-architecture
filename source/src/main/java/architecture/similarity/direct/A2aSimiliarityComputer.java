package architecture.similarity.direct;

import java.io.File;

import edu.usc.softarch.arcade.metrics.BatchSystemEvo;

public class A2aSimiliarityComputer extends DirectSimiliarityComputer {

	public A2aSimiliarityComputer() {
		
	}
	
	@Override
	public double computeSimilarity(File arcOne, File arcTwo) {
		return BatchSystemEvo.computeSysEvo(arcOne, arcTwo);
	}

	@Override
	public double getNormalizedDifference(double simValue) {
		return 1 - (simValue / 100);
	}

}
