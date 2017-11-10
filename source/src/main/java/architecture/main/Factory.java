package architecture.main;

import architecture.database.AbstractDatabase;
import architecture.database.MySQLDatabase;
import architecture.extraction.ArchitectureExtractor;
import architecture.similarity.AbstractArchitectureSimilarityComputer;

public class Factory {
	
	public static ArchitectureExtractor createExtractor() {
		return null;
	}
	
	public static AbstractDatabase createDatabase() {
		return new MySQLDatabase();
	}
	
	public static AbstractArchitectureSimilarityComputer createSimilarityComputer() {
		return null;
	}
}
