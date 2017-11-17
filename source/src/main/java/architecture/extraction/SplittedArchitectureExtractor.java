package architecture.extraction; 

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import architecture.extraction.classes.AbstractClassGraphExtractor;
import architecture.extraction.reconstruction.AbstractArchitectureReconstructor;

public class SplittedArchitectureExtractor extends AbstractArchitectureExtractor {
	private AbstractClassGraphExtractor extractor;
	private AbstractArchitectureReconstructor reconstructor;
	
	static Logger log = Logger.getLogger(SplittedArchitectureExtractor.class);
	
	public SplittedArchitectureExtractor(
			AbstractClassGraphExtractor extractor, AbstractArchitectureReconstructor reconstructor) {
		
		this.extractor = extractor;
		this.reconstructor = reconstructor;
	}

	@Override
	public File computeArchitecture(File projectFolder, String outputDir) throws IOException {
		new File(outputDir).mkdirs();
		
		String classStructureName = outputDir + "/classes.rsf";
		File classStructure = new File(FilenameUtils.normalize(classStructureName));
				
		String outputName = outputDir + "/architecture.rsf";
		File output = new File(FilenameUtils.normalize(outputName));
		
		if(!classStructure.exists()) {
			extractor.extract(projectFolder, classStructure);
		} else {
			log.info("Class Structure " + projectFolder.getName() + " already extracted");
		}
		
		if(!output.exists()) {
			reconstructor.reconstruct(classStructure, output);	
		} else {
			log.info("Architecture Structure " + projectFolder.getName() + " already extracted");
		}
		
		return output;
	}
}
