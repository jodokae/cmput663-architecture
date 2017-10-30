package architecture;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.jdom2.Content;
import org.jdom2.Element;
import org.junit.Test;

import husacct.analyse.AnalyseServiceImpl;
import husacct.analyse.IAnalyseService;
import husacct.common.dto.ProjectDTO;

public class Extraction {
	
	public static final String FILES = "testproject/";
	
	@Test
	public void headlessTest() {
		
		List<String> path = Arrays.asList(FILES.split(";"));
		test(path);
		
	}
	
	private void test(List<String> path) {
		URL propertiesFile = Class.class.getResource("/husacct/common/resources/log4j.properties");
	    PropertyConfigurator.configure(propertiesFile);
		
		IAnalyseService analyseService = new AnalyseServiceImpl();
	    
	    
	    ProjectDTO project = new ProjectDTO("TestName", new ArrayList<String>(path), "Java", "1.0", "",
	        null);
	    
	    analyseService.analyseApplication(project);
	    
	    Element result = analyseService.exportAnalysisModel();
	    
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
	
}
