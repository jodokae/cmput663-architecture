package architecture.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import architecture.commons.files.FileHandler;
import architecture.database.AbstractDatabase;
import architecture.extraction.AbstractArchitectureExtractor;
import architecture.similarity.AbstractArchitectureSimilarityComputer;

public class Main {
	private AbstractArchitectureExtractor extractor;
	private AbstractDatabase database;
	private AbstractArchitectureSimilarityComputer simComp;
	
	private String project = "SonarSource/sonarqube";
	
	public static void main(String[] args) {
		Main main = new Main();
		main.init();
		try {
			main.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void init() {
		database = Factory.createDatabase(project);
		extractor = Factory.createExtractor();	
		simComp = Factory.createSimilarityComputer();
		
	}
	
	public void run() throws IOException {
		List<Integer> builds = database.getBuildList();
		
		String pathOne = database.getCommit(builds.get(0));
		String pathTwo = database.getCommit(builds.get(1));
			
		String projectFolder = "autoDownloadProjects/";
		
		File archive = FileHandler.downloadCommit(project, pathOne, projectFolder);
		File versionOne = FileHandler.extract(archive, projectFolder);
		
		File archive2 = FileHandler.downloadCommit(project, pathTwo, projectFolder);
		File versionTwo = FileHandler.extract(archive2, projectFolder);
		
		String architectureFolder = FilenameUtils.normalize("architecture/" + project + "/" + pathOne);
		String architectureFolderTwo = FilenameUtils.normalize("architecture/" + project + "/" + pathTwo);
		
		
		File architectureOne = extractor.computeArchitecture(versionOne, architectureFolder);
		File architectureTwo = extractor.computeArchitecture(versionTwo, architectureFolderTwo);
			
		
		double sim = simComp.computeSimilarity(architectureOne, architectureTwo);
		
		System.out.println(sim);
		
		System.out.println("Finished");
		
		
	}
	
	
}
