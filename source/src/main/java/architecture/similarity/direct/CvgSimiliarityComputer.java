package architecture.similarity.direct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class CvgSimiliarityComputer extends DirectSimiliarityComputer {

	@Override
	public double computeSimilarity(File arcOne, File arcTwo) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getNormalizedDifference(double simValue) {
		// TODO Auto-generated method stub
		return 0;
	}
		
	public void py() {
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("import sys\nsys.path.append('target/classes/arcadepy')\nimport simevolanalyzer");
		
		interpreter.set("src", "/home/johannes/CMPUT663/cmput663-architecture/source/extracted/architectures/SonarSource/sonarqube/af6d4a423967e033cdf51e3762d0a4d14f659983/packageArc.rsf");
		interpreter.set("target", "/home/johannes/CMPUT663/cmput663-architecture/source/extracted/architectures/SonarSource/sonarqube/af6d4a423967e033cdf51e3762d0a4d14f659983/packageArc.rsf");
		
		
		interpreter.exec("(a,b) = simevolanalyzer.compareTwoVersions(src, target)");
		PyObject sourceCoverage = interpreter.get("a");
		PyObject targetCoverage = interpreter.get("b");
		
		
		System.out.println(sourceCoverage.toString());
		System.out.println(targetCoverage.toString());
		
		interpreter.close();
		
	}

}
