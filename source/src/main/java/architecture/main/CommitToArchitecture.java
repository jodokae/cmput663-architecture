package architecture.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import architecture.commons.files.FileHandler;
import architecture.extraction.AbstractArchitectureExtractor;

public class CommitToArchitecture {
	private AbstractArchitectureExtractor extractor;
	
	private String project;
	private String downloadFolder;
	private String arcFolder;
	
	public CommitToArchitecture(String downloadFolder, String arcFolder, String project) {
		extractor = Factory.createExtractor();
		
		this.downloadFolder = downloadFolder;
		this.arcFolder = arcFolder;
		this.project = project;
	}
	
	public File downloadAndReconstruct(String commitID) throws IOException {
		String architectureFolder = FilenameUtils.normalize(arcFolder + "/" + project + "/" + commitID);
		
		File archive = FileHandler.downloadCommit(project, commitID, downloadFolder);
		File source = FileHandler.extract(archive, downloadFolder);		
		return extractor.computeArchitecture(source, architectureFolder);
		
	}
	
}
