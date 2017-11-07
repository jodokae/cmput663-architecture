package architecture.database;

import java.util.List;

public abstract class AbstractDatabase {
	
	public abstract List<Integer> getBuildList();
	public abstract String getCommit(int build);
	public abstract String getOutcome(int build);
	
}
