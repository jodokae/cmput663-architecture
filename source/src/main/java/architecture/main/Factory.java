package architecture.main;

import architecture.database.AbstractDatabase;
import architecture.extraction.ArchitectureExtractor;
import architecture.similarity.AbstractArchitectureSimilarityComputer;

public class Factory {
	
	public static ArchitectureExtractor getExtractor() {
		return null;
	}
	
	public static AbstractDatabase getDatabase() {
		return null;
	}
	
	public static AbstractArchitectureSimilarityComputer getSimilarityComputer() {
		return null;
	}
}
