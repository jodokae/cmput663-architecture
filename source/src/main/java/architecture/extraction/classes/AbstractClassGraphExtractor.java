package architecture.extraction.classes;

import architecture.commons.ClassElement;
import architecture.commons.Graph;

public abstract class AbstractClassGraphExtractor {
	
	public abstract Graph<ClassElement> extract(String path);
}
