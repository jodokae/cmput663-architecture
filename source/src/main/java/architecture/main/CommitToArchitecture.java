package architecture.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import architecture.commons.Compilable;
import architecture.commons.CompilationCheck;
import architecture.commons.files.FileHandler;
import architecture.commons.files.JSONFileHandler;
import architecture.extraction.AbstractArchitectureExtractor;

public class CommitToArchitecture {
	private AbstractArchitectureExtractor extractor;
	
	private CompilableList compilableList;
		
	private String project;
	private String downloadFolder;
	private String arcFolder;
	
	static Logger log = Logger.getLogger(CommitToArchitecture.class);
	
	public CommitToArchitecture(String downloadFolder, String arcFolder, CompilableList compList, String project) {
		extractor = Factory.createExtractor();
		
		this.compilableList = compList;
		
		this.downloadFolder = downloadFolder;
		this.arcFolder = arcFolder;
		this.project = project;
	}
	
	public File[] downloadAndReconstruct(String commitID, boolean removeArchive) throws IOException {
		String architectureFolder = FilenameUtils.normalize(arcFolder + "/" + project + "/" + commitID);
		
		if(extractor.isComputed(architectureFolder).isPresent()) {
			log.info("Architecture already calculated");
			return extractor.isComputed(architectureFolder).get();
		}
		
		if(new File(architectureFolder).exists() && new File(architectureFolder).list().length != 0) {
			
		}
		
		File archive = FileHandler.downloadCommit(project, commitID, downloadFolder);
		File source = FileHandler.extract(archive, downloadFolder);
		
		if(!compilableList.contains(commitID)) {
			compilableList.add(new Compilable(commitID, CompilationCheck.isCompilable(source)));
			System.out.println("Compute comp for" + commitID);
		}

		File[] archictecture = extractor.computeArchitecture(source, architectureFolder);
		
		if(removeArchive) {
			FileHandler.remove(archive);
			FileHandler.remove(source);
			FileHandler.remove(source.getParentFile());
		}
		
		return archictecture;
		
	}
	
}
