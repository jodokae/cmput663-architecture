package architecture.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
		
		main.extractAndCompare(5);
		
		/*try {
			main.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
	
	private void extractAndCompare(int numberVersions) {
		List<Integer> builds = database.getBuildList();	
		Map<String, File> architectures = new HashMap<String, File>();
		Map<Integer, Double> diffValues = new HashMap<Integer, Double>();
		
		if(builds.size() < numberVersions) {
			System.out.println("Not enough Versions in Database");
		}
				
		for(int i = 0; i < numberVersions; i++) {
			String commit = database.getCommit(builds.get(i*10));
			try {
				architectures.put(commit, comToArc.downloadAndReconstruct(commit));
			} catch (IOException e) {
				System.err.println("Could't extract Version " + commit);
				e.printStackTrace();
			}
		}
		
		if(architectures.size() < numberVersions) {
			System.out.println("At least one version was not downloaded. Aborting comparison");
		}
		
		for(int i = 1; i < numberVersions; i++) {
			int first = builds.get((i-1)*10);
			int second = builds.get(i*10);
			
			String firstCommit = database.getCommit(first);
			String secondCommit = database.getCommit(second);
			
			double diff = simComp.computeDifference(architectures.get(firstCommit), architectures.get(secondCommit));
			
			diffValues.put(second, diff);
			
			System.out.println("Diff: " + first + " -> " + second + ": " + diff);
			
		}
		
	}
	
	
}
