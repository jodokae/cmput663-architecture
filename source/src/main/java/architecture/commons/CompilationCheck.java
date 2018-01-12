package architecture.commons;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class CompilationCheck {
	
	public static boolean isCompilable(File root) {
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile( new File( root.getAbsolutePath() + "/pom.xml" ) );
		request.setGoals( Arrays.asList( "clean", "compile" ) );

		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(new File("apache-maven-3.2.5"));
		try {
			InvocationResult result = invoker.execute( request );
			return result.getExitCode() == 0;
		} catch (MavenInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
