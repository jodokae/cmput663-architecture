package architecture.similarity.direct;

import java.io.File;
import java.util.Map;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class CvgSimiliarityComputer extends DirectSimiliarityComputer {

	@Override
	public Map<String, Double> computeSimilarity(File arcOne, File arcTwo) {
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("import sys\nsys.path.append('arcadepy')\nimport simevolanalyzer");
		
		interpreter.set("src", arcOne.getAbsolutePath());
		interpreter.set("target", arcTwo.getAbsolutePath());
		
		
		interpreter.exec("(a,b) = simevolanalyzer.compareTwoVersions(src, target)");
		PyObject sourceCoverage = interpreter.get("a");
		PyObject targetCoverage = interpreter.get("b");
		
		metrics.put("cvgSource", sourceCoverage.asDouble());
		metrics.put("cvgTarget", targetCoverage.asDouble());
		
		interpreter.close();
		
		return metrics;
	}

	@Override
	public double getNormalizedDifference(double simValue) {
		return (1 - simValue);
	}

}
