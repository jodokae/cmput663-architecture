package architecture.extraction.reconstruction;

import architecture.commons.ClassElement;
import architecture.commons.Graph;
import architecture.commons.Module;

public abstract class AbstractArchitectureReconstructor {
	
	public abstract Graph<Module> reconstruct(Graph<ClassElement> graph);
}
