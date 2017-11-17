package architecture.commons;

public class VersionDifference {
	private int fromVersion;
	private int toVersion;
	private double diffValue;
	
	public VersionDifference(int from, int to, double diff) {
		this.fromVersion = from;
		this.toVersion = to;
		this.diffValue = diff;
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
	
	public double getDiffValue() {
		return diffValue;
	}
	
	public void setDiffValue(double diffValue) {
		this.diffValue = diffValue;
	}
	
}
