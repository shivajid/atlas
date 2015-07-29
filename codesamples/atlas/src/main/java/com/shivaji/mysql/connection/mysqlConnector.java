package com.shivaji.mysql.connection;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import com.hortonworks.atlas.adapter.MySqlAdapter;

public class mysqlConnector {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub

		System.out.println("-------- MySQL JDBC Connection Testing ------------");
		
		MySqlAdapter ms = new MySqlAdapter(args[0], args[1], args[2], args[3]);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}
		System.out.println("MySQL JDBC Driver Registered!");
		Connection connection = null;
	 
		try {
			connection = DriverManager
			.getConnection("jdbc:mysql://172.24.0.207:3306/test","trucker1", "trucker");
			
			java.sql.DatabaseMetaData md = connection.getMetaData();
			
			//System.out.println(md.getCatalogTerm());
			java.sql.ResultSet res = md.getTables(null, null, null, new String[]{"TABLE"});
			//java.sql.ResultSet rs = md.getCatalogs();
			
	 
			while(res.next()){
				
				
				java.sql.ResultSetMetaData rsmd = res.getMetaData();
				
				
				 System.out.println(
				            "   "+res.getString("TABLE_CAT") 
				          
				           + ", "+res.getString("TABLE_NAME")
				           + ", "+res.getString("TABLE_TYPE")
				           + ", "+res.getString("REMARKS")); 
				
				 java.sql.ResultSet res1 = md.getColumns(null, null, res.getString("TABLE_NAME"), null);
				 
				 while(res1.next()){
					 System.out.println(
					            "   "+res1.getString("TABLE_NAME") 
					           + ", "+res1.getString("COLUMN_NAME")
					           + ", "+res1.getString("TYPE_NAME")
					           +  ", "+res1.getString("COLUMN_SIZE")
					           +  ", "+res1.getString("NULLABLE")
					           +  ", "+res1.getString("REMARKS"));
					         
					 
				 }
				 
				int i =1; 
				
				System.out.println(res.getNString(1));
				
				//System.out.println(rs.getNString(2));
			}
			
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
	 
		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		
		
	}

}
