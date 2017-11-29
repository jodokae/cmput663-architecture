package architecture.main;

import architecture.database.AbstractDatabase;
import architecture.database.MySQLDatabase;
import architecture.extraction.AbstractArchitectureExtractor;
import architecture.extraction.SplittedArchitectureExtractor;
import architecture.extraction.classes.AbstractClassGraphExtractor;
import architecture.extraction.classes.husacct.HusacctGraphExtractor;
import architecture.extraction.reconstruction.ACDCReconstructor;
import architecture.extraction.reconstruction.AbstractArchitectureReconstructor;
import architecture.extraction.reconstruction.pkg.PackageReconstructor;
import architecture.similarity.AbstractArchitectureSimilarityComputer;
import architecture.similarity.direct.A2aSimiliarityComputer;

public class Factory {
	
	public static AbstractArchitectureExtractor createExtractor() {
		AbstractClassGraphExtractor extractor = new HusacctGraphExtractor(false);
		AbstractArchitectureReconstructor reconstructor = new PackageReconstructor("packageArc");
		return new SplittedArchitectureExtractor(extractor, reconstructor, "complexClasses");
	}
	
	public static AbstractDatabase createDatabase(String projectName) {
		return new MySQLDatabase(projectName);
	}
	
	public static AbstractArchitectureSimilarityComputer createSimilarityComputer() {
		return new A2aSimiliarityComputer();
	}
	
	public static CommitToArchitecture createCommitToArchitecture(
			String downloadFolder, String arcFolder, String project) {
		return new CommitToArchitecture(downloadFolder, arcFolder, project);
	}
	
	public static CompareAndSave createCompareAndSave(String path) {
		return new CompareAndSave(path);
	}
}
