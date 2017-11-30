package architecture.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import architecture.database.AbstractDatabase;

public class Main {
	private AbstractDatabase database;
	
	private CommitToArchitecture comToArc;
	private CompareAndSave compSave;
	
	private final static String PROJECT = "SonarSource/sonarqube";
	
	private final static String PROJECT_FOLDER = "extracted/projects/";
	private final static String ARC_FOLDER = "extracted/architectures/";
	private final static String DIFF_JSON = "extracted/versionDiff.json";
	
	static Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
		File log4jfile = new File("cfg/log4j.properties");
	    PropertyConfigurator.configure(log4jfile.getAbsolutePath());
		
		Main main = new Main();
		main.init();
		
		
		//System.out.println(main.database.getBuildList().size()); //3043
		main.extractAndCompare(2);
		
		main.finish();
	}
	
	public void init() {		
		database = Factory.createDatabase(PROJECT);	
		
		comToArc = Factory.createCommitToArchitecture(PROJECT_FOLDER, ARC_FOLDER, PROJECT);
		compSave = Factory.createCompareAndSave(DIFF_JSON);
		
	}
	
	public void finish() {
		try {
			compSave.storeJSON();
		} catch (IOException e) {
			log.warn("Couldn't write Diff JSON");
		}
	}
	
	private void extractAndCompare(int numberVersions) {
		List<Integer> builds = database.getBuildList();	
		Map<String, File> architectures = new HashMap<String, File>();
		
		if(builds.size() < numberVersions) {
			System.out.println("Not enough Versions in Database");
		}
		
		// TODO ConcurrentModificationException
		//IntStream.range(0, numberVersions).parallel().forEach(i-> {
		for(int i = 0; i < numberVersions; i++) {
			String commit = database.getCommit(builds.get(i));
			try {
				architectures.put(commit, comToArc.downloadAndReconstruct(commit, true));
			} catch (IOException e) {
				System.err.println("Could't extract Version " + commit);
				e.printStackTrace();
			}
		}
		//});
		
		if(architectures.size() < numberVersions) {
			System.out.println("At least one version was not downloaded. Aborting comparison");
		}
		
		for(int i = 1; i < numberVersions; i++) {
			int first = builds.get((i-1));
			int second = builds.get(i);
			
			String firstCommit = database.getCommit(first);
			String secondCommit = database.getCommit(second);
			
			ImmutablePair<Integer, File> arcOne = 
					new ImmutablePair<Integer, File>(first, architectures.get(firstCommit));
			ImmutablePair<Integer, File> arcTwo = 
					new ImmutablePair<Integer, File>(second, architectures.get(secondCommit));
						
			
			Map<String, Double> diff = compSave.compare(arcOne, arcTwo);
			
			for(Entry<String, Double> diffEntry: diff.entrySet()) {
				System.out.println("Diff: " + first + " -> " + second + ": " 
						+ diffEntry.getKey() + " " + diffEntry.getValue());
			}
			
			
		}
		
	}
	
	
}
