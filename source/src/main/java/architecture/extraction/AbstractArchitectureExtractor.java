package architecture.extraction;

import architecture.commons.ClassElement;
import architecture.commons.Graph;
import architecture.commons.Module;
import architecture.extraction.classes.AbstractClassGraphExtractor;
import architecture.extraction.reconstruction.AbstractArchitectureReconstructor;

public class ArchitectureExtractor {
	
	private AbstractClassGraphExtractor extractor;
	private AbstractArchitectureReconstructor reconstructor;
	
	public ArchitectureExtractor(
			AbstractClassGraphExtractor extractor, AbstractArchitectureReconstructor reconstructor) {
		
		this.extractor = extractor;
		this.reconstructor = reconstructor;
	}
	
	public Graph<Module> computeArchitecture(String path) {
		Graph<ClassElement> intermediateResult = extractor.extract(path);
		return reconstructor.reconstruct(intermediateResult);
	}

}
