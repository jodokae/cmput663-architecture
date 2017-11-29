package architecture.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import architecture.commons.VersionDifference;
import architecture.commons.files.DifferenceFileHandler;
import architecture.similarity.AbstractArchitectureSimilarityComputer;

public class CompareAndSave {
	
	private DifferenceFileHandler handler;
	private AbstractArchitectureSimilarityComputer simComp;
	
	private String path;
	private List<VersionDifference> list;
	
	static Logger log = Logger.getLogger(CompareAndSave.class);
		
	public CompareAndSave(String path) {
		handler = new DifferenceFileHandler();
		simComp = Factory.createSimilarityComputer();
		
		this.path = path;
		try {
			loadJSON();
		} catch (IOException e) {
			log.warn("Version Diff JSON could not be loaded");
			list = new ArrayList<VersionDifference>();
		}
	}
	
	public void loadJSON() throws IOException {
		list = handler.readJson(path);
	}
	
	public void storeJSON() throws IOException {
		handler.writeJson(path, list);
	}
	
	public Map<String, Double> compare(Pair<Integer, File> arcOne, Pair<Integer, File> arcTwo) {
		Optional<VersionDifference> vd = listContains(arcOne.getLeft(), arcTwo.getLeft());
		if(vd.isPresent()) {
			log.info("Diff between " + arcOne.getLeft() + " and " + arcTwo.getLeft() + " already computed");
			return vd.get().getDiffValue();
		}
		
		Map<String, Double> diff = simComp.computeDifference(arcOne.getRight(), arcTwo.getRight());
		list.add(new VersionDifference(arcOne.getLeft(), arcTwo.getLeft(), diff));
		return diff;
		
		
	}
	
	private Optional<VersionDifference> listContains(int from, int to) {
		for(VersionDifference vd : list) {
			if(vd.getFromVersion() == from && vd.getToVersion() == to) {
				return Optional.of(vd);
			}
		}
		return Optional.empty();
	}
	
}
