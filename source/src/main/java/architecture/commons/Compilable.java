package architecture.commons;

public class Compilable {
	private String commitID;
	private boolean compilable;
	
	public Compilable(String commitID, boolean isCompilable) {
		this.commitID = commitID;
		this.compilable = isCompilable;
	}
	
	public String getCommitID() {
		return commitID;
	}
	public void setCommitID(String commitID) {
		this.commitID = commitID;
	}
	public boolean isCompilable() {
		return compilable;
	}
	public void setCompilable(boolean compilable) {
		this.compilable = compilable;
	}
}
