package architecture.main;

import java.util.List;

import architecture.commons.Graph;
import architecture.commons.Module;
import architecture.database.AbstractDatabase;
import architecture.extraction.ArchitectureExtractor;
import architecture.similarity.AbstractArchitectureSimilarityComputer;

public class Main {
	private ArchitectureExtractor extractor;
	private AbstractDatabase database;
	private AbstractArchitectureSimilarityComputer simComp;
	
	public static void main(String[] args) {
		Main main = new Main();
		main.init();
		main.run();
	}
	
	public void init() {
		database = Factory.getDatabase();
		extractor = Factory.getExtractor();	
		simComp = Factory.getSimilarityComputer();
		
	}
	
	public void run() {
		List<Integer> builds = database.getBuildList();
		
		String pathOne = database.getCommit(builds.get(0));
		String pathTwo = database.getCommit(builds.get(1));
		
		Graph<Module> architectureOne = extractor.computeArchitecture(pathOne);
		Graph<Module> architectureTwo = extractor.computeArchitecture(pathTwo);
		
		double sim = simComp.computeSimilarity(architectureOne, architectureTwo);
		
		System.out.println(sim);
		
		
	}
	
}
