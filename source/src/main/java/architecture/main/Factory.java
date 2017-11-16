package architecture.main;

import architecture.database.AbstractDatabase;
import architecture.database.MySQLDatabase;
import architecture.extraction.AbstractArchitectureExtractor;
import architecture.extraction.SplittedArchitectureExtractor;
import architecture.extraction.classes.AbstractClassGraphExtractor;
import architecture.extraction.classes.HusacctGraphExtractor;
import architecture.extraction.reconstruction.ACDCReconstructor;
import architecture.extraction.reconstruction.AbstractArchitectureReconstructor;
import architecture.similarity.AbstractArchitectureSimilarityComputer;

public class Factory {
	
	public static AbstractArchitectureExtractor createExtractor() {
		AbstractClassGraphExtractor extractor = new HusacctGraphExtractor();
		AbstractArchitectureReconstructor reconstructor = new ACDCReconstructor();
		return new SplittedArchitectureExtractor(extractor, reconstructor);
	}
	
	public static AbstractDatabase createDatabase(String projectName) {
		return new MySQLDatabase(projectName);
	}
	
	public static AbstractArchitectureSimilarityComputer createSimilarityComputer() {
		return null;
	}
}
