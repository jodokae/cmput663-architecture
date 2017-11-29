package architecture.extraction.classes.husacct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import architecture.extraction.classes.AbstractClassGraphExtractor;
import husacct.analyse.AnalyseServiceImpl;
import husacct.analyse.IAnalyseService;
import husacct.common.dto.ProjectDTO;

public class HusacctGraphExtractor extends AbstractClassGraphExtractor {

	public static final String[] SOURCE_DIR = {"src", "main"};
	
	private boolean simpleWriter;
		
	public HusacctGraphExtractor(boolean simpleWriter) {
		this.simpleWriter = simpleWriter;
	}
	
	@Override
	public void extract(File projectFolder, File output) throws IOException {		
		Element res = extractGraph(projectFolder);
		
		HusaactGraphFileWriter writer = new HusaactGraphFileWriter(output, simpleWriter);
		writer.write(res);
	}
	
	
	private Element extractGraph(File root) {
		List<String> res = getSourceFolders(root);
		Element graph = extract(res);
		
		return graph;
	}
		
	private List<String> removeTestFolders(List<String> list) {
		List<String> withoutTest = new ArrayList<String>();
		
		for(String item : list) {
			if(!item.contains("test")) {
				withoutTest.add(item);
			}
		}
		
		return withoutTest;
	}
	
	private List<String> getSourceFolders(File root) {
		List<String> result = new ArrayList<String>();

		  for (File file : root.listFiles()) {
		    if (file.isDirectory()) {
		      if (file.getName().equals(SOURCE_DIR[1]) && 
		        file.getParentFile().getName().equals(SOURCE_DIR[0])) {
		    	  result.add(file.getPath());
		      } else {
		    	  result.addAll(getSourceFolders(file));
		      }
		    }
		  }

		  return removeTestFolders(result);
    }
	
	
	private Element extract(List<String> path) {
		IAnalyseService analyseService = new AnalyseServiceImpl();
	    
	    ProjectDTO project = new ProjectDTO("TestName", new ArrayList<String>(path), "Java", "1.0", "",
	        null);
	    
	    analyseService.analyseApplication(project);
	    
	    Element result = analyseService.exportAnalysisModel();
	    
	    return result;
	   
	}

}
