package architecture.extraction.classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.usc.softarch.arcade.facts.driver.JavaSourceToDepsBuilder;
import edu.usc.softarch.arcade.facts.driver.SourceToDepsBuilder;

public class ArcadeArchitectureExtractor extends AbstractClassGraphExtractor {
	
	//static Logger logger = Logger.getLogger(ArcadeACDCArchitectureExtractor.class);
	
	private String classesDir;
	
	@Override
	public void extract(File projectFolder, File output) throws IOException {
		
		System.out.println("Works on Jar Files - not implemented");
		
		/*if(!projectFolder.isDirectory()) {
			throw new IOException("Parameter is not a folder");
		}
				
		File input = new File("projects/");
		classesDir = "lib_struts";
		
		single(input, output);*/
		
	}
				
	private void single (File versionFolder, File outputDir) throws FileNotFoundException, IOException{
		// the revision number is really just the name of the subdirectory, for hadoop I actually name each subdirectory based on the revision number
		String revisionNumber = versionFolder.getName();
		
		// classesDir is the directory in each subdirectory of the dir directory that contains the compiled classes of the subdirectory
		String absoluteClassesDir = versionFolder.getAbsolutePath() + File.separatorChar + classesDir;
		File classesDirFile = new File(absoluteClassesDir);
		if (!classesDirFile.exists())
			return;
		
		System.out.println("C exists");
		
		// depsRsfFilename is the file name of the dependencies rsf file (one is created per subdirectory of dir)
		String depsRsfFilename = outputDir.getAbsolutePath() + File.separatorChar + revisionNumber + "_deps.rsf"; 
		String[] builderArgs = {absoluteClassesDir,depsRsfFilename};
		File depsRsfFile = new File(depsRsfFilename);
		if (!depsRsfFile.getParentFile().exists())
			depsRsfFile.getParentFile().mkdirs();
		
		
		SourceToDepsBuilder builder = new JavaSourceToDepsBuilder();
		
		builder.build(builderArgs);
		if (builder.getEdges().size() == 0) {
			return;
		}
		
		System.out.println(outputDir.getAbsolutePath());
		System.out.println(depsRsfFile.getAbsolutePath());
	}

}
