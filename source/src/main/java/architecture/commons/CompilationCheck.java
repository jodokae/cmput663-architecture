package architecture.commons;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class CompilationCheck {
	
	private static int failReason = 0;
	
	public static int isCompilable(File root) {
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile( new File( root.getAbsolutePath() + "/pom.xml" ) );
		request.setGoals( Arrays.asList( "clean", "compile" ) );

		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(new File("apache-maven-3.2.5"));
		
		failReason = 0;
		invoker.setOutputHandler( new InvocationOutputHandler() {

        @Override
        public void consumeLine( String line ) {
        	//System.out.println(line);
        	if(line.startsWith("[ERROR]")) {
        		if(line.contains("dependencies")) {
        			System.out.println("Dep");
        			failReason = 1;
        		}
        		if(line.contains("Compilation failure")) {
        			System.out.println("Comp");
        			failReason = 2;
        		}
        	}
        }
    } );
		
		
		try {
			invoker.execute( request );
			return failReason;
		} catch (MavenInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 3;
	}

}
