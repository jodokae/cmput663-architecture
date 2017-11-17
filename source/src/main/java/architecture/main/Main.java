package architecture.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import architecture.database.AbstractDatabase;
import architecture.similarity.AbstractArchitectureSimilarityComputer;

public class Main {
	private AbstractDatabase database;
	private AbstractArchitectureSimilarityComputer simComp;
	
	private CommitToArchitecture comToArc;
	
	private final static String PROJECT = "SonarSource/sonarqube";
	
	private final static String PROJECT_FOLDER = "extracted/projects/";
	private final static String ARC_FOLDER = "extracted/architectures/";
	
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
		database = Factory.createDatabase(PROJECT);	
		simComp = Factory.createSimilarityComputer();
		
		comToArc = new CommitToArchitecture(PROJECT_FOLDER, ARC_FOLDER, PROJECT);
		
	}
	
	public void run() throws IOException {
		System.out.println("Starting");
		
		List<Integer> builds = database.getBuildList();
		
		String commitOne = database.getCommit(builds.get(0));
		String commitTwo = database.getCommit(builds.get(1));
			
		System.out.println("Recover Arc 1");
		File architectureOne = comToArc.downloadAndReconstruct(commitOne);
		System.out.println("Recover Arc 2");
		File architectureTwo = comToArc.downloadAndReconstruct(commitTwo);
		
		System.out.println("Compute Diff");
		double diff = simComp.computeDifference(architectureOne, architectureTwo);
		
		System.out.println("Diff between " + commitOne + " and " + commitTwo + ": " + diff);
		
		System.out.println("Finished");
		
		
	}
	
	
}
