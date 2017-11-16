package architecture.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


public class MySQLDatabase extends AbstractDatabase {
	
	private String url = "jdbc:mysql://localhost:3306/archi?useSSL=false&serverTimezone=UTC";
	private String username = "archi";
	private String password = "archi";
	
	private String query;
	
	public MySQLDatabase(String project) {
		commits = new HashMap<Integer, String>();
		statuses = new HashMap<Integer, String>();
		
		 query = 
					"select distinct tr_log_analyzer, gh_project_name, git_trigger_commit, tr_build_number, tr_status "
					+ "from travistorrent_8_2_2017 "
					+ "where gh_project_name = \""+ project +"\" "
					+ "&& tr_log_analyzer = \"java-maven\" "
					+ "order by tr_build_number ;";

		
		
		getData();
	}
	
	private void getData() {
		try (Connection connection = DriverManager.getConnection(url, username, password)) {
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				String commit = rs.getString("git_trigger_commit");
				int number = rs.getInt("tr_build_number");
				String status = rs.getString("tr_status");
				
				commits.put(number, commit);
				statuses.put(number, status);
				
			}
			
		} catch (SQLException e) {
		    throw new IllegalStateException("Cannot connect the database!", e);
		}
	}
	

}
