package architecture.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import architecture.commons.files.FileHandler;
import architecture.database.AbstractDatabase;
import architecture.extraction.AbstractArchitectureExtractor;
import architecture.similarity.AbstractArchitectureSimilarityComputer;

public class Main {
	private AbstractArchitectureExtractor extractor;
	private AbstractDatabase database;
	private AbstractArchitectureSimilarityComputer simComp;
	
	private String project = "SonarSource/sonarqube";
	
	static Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
		File log4jfile = new File("cfg/log4j.properties");
	    PropertyConfigurator.configure(log4jfile.getAbsolutePath());
		
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
		
		String commitOne = database.getCommit(builds.get(0));
		String commitTwo = database.getCommit(builds.get(1));
			
		String projectFolder = "autoDownloadProjects/";
		
		File archive = FileHandler.downloadCommit(project, commitOne, projectFolder);
		File versionOne = FileHandler.extract(archive, projectFolder);
		
		File archive2 = FileHandler.downloadCommit(project, commitTwo, projectFolder);
		File versionTwo = FileHandler.extract(archive2, projectFolder);
		
		String architectureFolder = FilenameUtils.normalize("architecture/" + project + "/" + commitOne);
		String architectureFolderTwo = FilenameUtils.normalize("architecture/" + project + "/" + commitTwo);
		
		
		File architectureOne = extractor.computeArchitecture(versionOne, architectureFolder);
		File architectureTwo = extractor.computeArchitecture(versionTwo, architectureFolderTwo);
			
		double sim = simComp.computeSimilarity(architectureOne, architectureTwo);
		double diff = simComp.getNormalizedDifference(sim);
		
		System.out.println("Diff between " + commitOne + " and " + commitTwo + ": " + diff);
		//System.out.println(database.getOutcome(builds.get(0)));
		//System.out.println(database.getOutcome(builds.get(1)));
		
		System.out.println("Finished");
		
		
	}
	
	
}
