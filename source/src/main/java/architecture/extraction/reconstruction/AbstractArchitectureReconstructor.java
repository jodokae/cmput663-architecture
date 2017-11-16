package architecture.extraction.reconstruction;

import java.io.File;

public abstract class AbstractArchitectureReconstructor {
	
	public abstract void reconstruct(File graph, File output);
}
