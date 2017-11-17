package architecture.commons.files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import architecture.commons.VersionDifference;

public class DifferenceFileHandler {
	
	public List<VersionDifference> readJson(String path) throws IOException {
		Gson gson = new Gson();
		BufferedReader br = new BufferedReader(new FileReader(path));
		Type type = new TypeToken<List<VersionDifference>>(){}.getType();
		return gson.fromJson(br, type);
		
	}
	
	public void writeJson(String path, List<VersionDifference> diffList) throws IOException {
		PrintStream out = new PrintStream(path);;
		PrintWriter writer = new PrintWriter(out);
		
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();	    
	    Type type = new TypeToken<List<VersionDifference>>(){}.getType();

		writer.println(gson.toJson(diffList, type));
		writer.close();
	}
	
}
