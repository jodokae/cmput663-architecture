package architecture.extraction;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;

import architecture.extraction.classes.AbstractClassGraphExtractor;
import architecture.extraction.reconstruction.AbstractArchitectureReconstructor;

public class MultiSplittedArchitectureExtractor extends SplittedArchitectureExtractor {

	private AbstractArchitectureReconstructor[] reconstructors;
	
	public MultiSplittedArchitectureExtractor(
			AbstractClassGraphExtractor extractor,
			AbstractArchitectureReconstructor[] reconstructors, String intermediateFileName) {
		super(extractor, null, intermediateFileName);	
		this.reconstructors = reconstructors;
		if(reconstructors.length == 0) {
			System.err.println("No reconstructor give");
			return;
		} else {
			reconstructor = reconstructors[0];
		}
	}
	
	@Override
	public File computeArchitecture(File projectFolder, String outputDir) throws IOException {
		new File(outputDir).mkdirs();
				
		File classStructure = computeClasses(projectFolder, outputDir);
		
		File[] res = new File[reconstructors.length];
		
		for(int i = 0; i < reconstructors.length; i++) {
			res[i] = computeArc(projectFolder, outputDir, classStructure, reconstructors[i]);
		}
		
		return res[0].getParentFile();
	}
	
	@Override
	public Optional<File> isComputed(String outputDir) {
		Optional<File> res = super.isComputed(outputDir);
		if(res.isPresent()) {
			for(AbstractArchitectureReconstructor recon : reconstructors) {
				String outputName = outputDir + "/" + recon.getName() + ".rsf";
				File output = new File(FilenameUtils.normalize(outputName));
				
				if(!output.exists()) {
					return Optional.empty();
				}
			}
			return Optional.of(res.get().getParentFile());
		}
		return res;
		
	}
	
}
