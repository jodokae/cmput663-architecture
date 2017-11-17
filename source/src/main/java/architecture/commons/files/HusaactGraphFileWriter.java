package architecture.commons.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Element;

public class HusaactGraphFileWriter {

	PrintStream out;
	PrintWriter writer;

	public HusaactGraphFileWriter(File output) throws FileNotFoundException {
		out = new PrintStream(output);
		writer = new PrintWriter(out);
	}

	public File write(Element input) {

		List<Content> packages = ((Element) input.getContent(1)).getContent();
		List<Content> classes = ((Element) input.getContent(2)).getContent();
		List<Content> dependencies = ((Element) input.getContent(4)).getContent();

		handlePackages(packages);
		handleClasses(classes);
		handleDependencies(dependencies);

		writer.close();

		return null;

	}

	private void appendLine(String from, String to) {
		if(!from.isEmpty() && !to.isEmpty() &&
				!from.contains("xLibraries") && !to.contains("xLibraries")) {
			writer.println("depends " + from + " " + to);
		}

	}

	private void handlePackages(List<Content> packages) {
		for (Content c : packages) {
			Element cElement = (Element) c;
			String fullPkgName = cElement.getContent().get(1).getValue();
			String parentFullName = cElement.getContent().get(4).getValue();
			
			appendLine(fullPkgName, parentFullName);
		}
	}

	private void handleClasses(List<Content> classes) {
		for (Content c : classes) {
			Element cElement = (Element) c;
			String simpleName = cElement.getContent().get(0).getValue();
			String pkg = cElement.getContent().get(4).getValue();

			String fullName = pkg + "." + simpleName;

			appendLine(fullName, pkg);
		}
	}

	private void handleDependencies(List<Content> dependencies) {
		for (Content c : dependencies) {
			Element cElement = (Element) c;
			String fromType = cElement.getContent().get(0).getValue();
			String toType = cElement.getContent().get(1).getValue();
			
			appendLine(fromType, toType);
		}
	}

}
