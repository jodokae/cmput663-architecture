package architecture.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import architecture.commons.files.FileHandler;
import architecture.database.AbstractDatabase;

public class Main {
	private AbstractDatabase database;
	
	//private CommitToArchitecture comToArc;
	private CompareAndSave compSave;
		
	private final static String PROJECT = "SonarSource/sonarqube";
	
	private final static String PROJECT_FOLDER = "extracted/projects/";
	private final static String ARC_FOLDER = "extracted/architectures/";
	private final static String DIFF_JSON = "extracted/versionDiff.json";
	
	static Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
		File log4jfile = new File("cfg/log4j.properties");
	    PropertyConfigurator.configure(log4jfile.getAbsolutePath());
		
	    log.info("Start Computation");
		Main main = new Main();
		main.init();
		
		
		//System.out.println(main.database.getBuildList().size()); //3043
		main.extractAndCompare(10);
		
		main.finish();
	}
	
	private void init() {		
		database = Factory.createDatabase(PROJECT);	
		
		//comToArc = Factory.createCommitToArchitecture(PROJECT_FOLDER, ARC_FOLDER, PROJECT);
		compSave = Factory.createCompareAndSave(DIFF_JSON);
		
	}
	
	private void finish() {
		try {
			log.info("Storing results");
			compSave.storeJSON();
		} catch (IOException e) {
			log.warn("Couldn't write Diff JSON");
		}
	}
	
	
	private void extractAndCompare(int numberVersions) {
		List<Integer> builds = database.getBuildList();	
		Map<String, Optional<File[]>> architectures = new ConcurrentHashMap<String, Optional<File[]>>();
		
		if(builds.size() < numberVersions) {
			log.warn("Not enough Versions in Database");
		}
		
		IntStream.range(0, numberVersions).parallel().forEach(i-> {
		//for(int i = 0; i < numberVersions; i++) {
			String commit = database.getCommit(builds.get(i));
			
			if((i == 0 && compSave.nextCalclulated(builds.get(i))) || 
					compSave.metricsCalculated(builds.get(i)) || 
					(i == numberVersions-1 && compSave.prevCalclulated(builds.get(i)))) {
				log.info("Metrics for " + commit + " already calculated, skipping reconstruct");
				// Empty is acceptable, because the value is never going to be used
				architectures.put(commit, Optional.empty());
				 //continue;
				return; //For Each only returns this iteration
			}
			
			try {
				CommitToArchitecture comToArc = Factory.createCommitToArchitecture(PROJECT_FOLDER, ARC_FOLDER, PROJECT);
				architectures.put(commit, Optional.of(comToArc.downloadAndReconstruct(commit, true)));
			} catch (IOException e) {
				System.err.println("Could't extract Version " + commit);
				e.printStackTrace();
			}
		//}
		});
		
		if(architectures.size() < numberVersions) {
			log.error("At least one version was not downloaded. Aborting comparison");
			return;
		}
		
		IntStream.range(1, numberVersions).parallel().forEach(i-> {
		//for(int i = 1; i < numberVersions; i++) {
			int first = builds.get((i-1));
			int second = builds.get(i);
			
			String firstCommit = database.getCommit(first);
			String secondCommit = database.getCommit(second);
			
			Optional<File[]> firstFiles = architectures.get(firstCommit);
			Optional<File[]> secondFiles = architectures.get(secondCommit);
			
			if(firstFiles.isPresent() && secondFiles.isPresent()) {
			
				ImmutablePair<Integer, File[]> arcOne = 
						new ImmutablePair<Integer, File[]>(first, architectures.get(firstCommit).get());
				ImmutablePair<Integer, File[]> arcTwo = 
						new ImmutablePair<Integer, File[]>(second, architectures.get(secondCommit).get());
							
				
				//Map<String, Map<String, Double>> metrics = compSave.compare(arcOne, arcTwo);
				compSave.compare(arcOne, arcTwo);
				
				if(((i == 0 && compSave.nextCalclulated(first)) || 
						compSave.metricsCalculated(first) || 
						(i == numberVersions-1 && compSave.prevCalclulated(first)))) {	
					try {
						FileHandler.remove(arcOne.getRight()[0].getParentFile());
					} catch (IOException e) {
						log.warn("Could not remove Architecture Files for "+ firstCommit);
						e.printStackTrace();
					} catch (NullPointerException e) {
						// Intended behaviour
					}
				}
				
				/*
				for(Entry<String, Map<String, Double>> metricEntry: metrics.entrySet()) {
					System.out.println(metricEntry.getKey());
					for(Entry<String, Double> diffEntry : metricEntry.getValue().entrySet()) {
						System.out.println("Diff: " + first + " -> " + second + ": " 
								+ diffEntry.getKey() + " " + diffEntry.getValue());				
					
					}
				}*/
			}
		//}
		});
		
		log.info("Done");
		
	}
	
	
}
