package architecture.extraction;

import java.io.File;
import java.io.IOException;

public abstract class AbstractArchitectureExtractor {

	public abstract File computeArchitecture(File projectFolder, String outputDir) throws IOException;

}
