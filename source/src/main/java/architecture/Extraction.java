package architecture;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.PropertyConfigurator;
import org.jdom2.Content;
import org.jdom2.Element;

import husacct.analyse.AnalyseServiceImpl;
import husacct.analyse.IAnalyseService;
import husacct.common.dto.ProjectDTO;

public class Extraction {
	
	public static final String ROOT = "projects";
	public static final String[] SOURCE_DIR = {"src", "main"};
	
	public static void main(String[] args) {
		Extraction extr = new Extraction();
		extr.run();
	}
	
	
	public void run() {
		URL propertiesFile = Class.class.getResource("/husacct/common/resources/log4j.properties");
	    PropertyConfigurator.configure(propertiesFile);
		
		
		List<File> commits = extractCommits();
		
		Map<String, Element> graphs = new HashMap<String, Element>();
		
		for(File commit: commits) {
			Element graph = extractGraph(commit);
			graphs.put(commit.getName(), graph);
			
			//printBasicInfo(graph);
		}
		
		for(Entry<String, Element> entry : graphs.entrySet()) {
			System.out.println(entry.getKey() + ": ");
			printBasicInfo(entry.getValue());
		}
		
	}
	
	private Element extractGraph(File root) {
		List<String> res = getSourceFolders(root);
		Element graph = extract(res);
		
		return graph;
	}
	
	private List<File> extractCommits() {
		File root = new File(ROOT);
		return Arrays.asList(root.listFiles());
		
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
	
	private void printBasicGraph(Element result) {
		List<Content> packages = ((Element) result.getContent(1)).getContent();
	    List<Content> classes = ((Element) result.getContent(2)).getContent();
	    List<Content> dependencies = ((Element) result.getContent(4)).getContent();
	    
	    for (Content c : packages) {
	    	Element cElement = (Element) c;
	        String simpleName = cElement.getContent().get(0).getValue();
	        String parentFullName = cElement.getContent().get(4).getValue();
	        
	        System.out.println(simpleName + " " + parentFullName);
	    }
	    
	    for (Content c : classes) {
	        Element cElement = (Element) c;
	        String simpleName = cElement.getContent().get(0).getValue();
	        String pkg = cElement.getContent().get(4).getValue();
	        
	        System.out.println(simpleName + " in: " + pkg);
	    }
	    
	    for (Content c : dependencies) {
	        Element cElement = (Element) c;
	        String fromType = cElement.getContent().get(0).getValue();
	        String toType = cElement.getContent().get(1).getValue();
	        String dependencyType = cElement.getContent().get(2).getValue();
	        
	        System.out.println(fromType + " -> " + toType + ". Type: " + dependencyType);
        }
    
	}
	
	private void printBasicInfo(Element result) {
		List<Content> packages = ((Element) result.getContent(1)).getContent();
	    List<Content> classes = ((Element) result.getContent(2)).getContent();
	    List<Content> dependencies = ((Element) result.getContent(4)).getContent();
	    
	    System.out.println("#Pkg: " + packages.size());
	    System.out.println("#Classes: " + classes.size());
	    System.out.println("#Depend: " + dependencies.size());
	}
	
}
