package architecture.commons;

import java.util.Map;

public class VersionDifference {
	private int fromVersion;
	private int toVersion;
	private Map<String, Double> diffValues;
	
	public VersionDifference(int from, int to, Map<String, Double> diffValues) {
		this.fromVersion = from;
		this.toVersion = to;
		this.diffValues = diffValues;
	}
	
	public int getFromVersion() {
		return fromVersion;
	}
	
	public void setFromVersion(int fromVersion) {
		this.fromVersion = fromVersion;
	}
	
	public int getToVersion() {
		return toVersion;
	}
	
	public void setToVersion(int toVersion) {
		this.toVersion = toVersion;
	}
	
	public Map<String, Double> getDiffValue() {
		return diffValues;
	}
	
	public void setDiffValue(Map<String, Double> diffValues) {
		this.diffValues = diffValues;
	}
	
}
