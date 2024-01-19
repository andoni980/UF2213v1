package com.dev.UF2213v1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteApp {

	private static final String URL = "jdbc:mysql://localhost:3306/manana_tienda";
	private static final String USER = "root";
	private static final String PASS = "root";
	private static final String SELECT_ALL = "SELECT * FROM clientes";
	
	private static Connection con;

	public static void main(String[] args) {

		try {
			con = DriverManager.getConnection(URL, USER, PASS);
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la conexión");
			System.err.println(e.getMessage());
		}
	}
	
	private static void getAll() {
		
		try (PreparedStatement pstmt = con.prepareStatement(SELECT_ALL);
			ResultSet rs = pstmt.executeQuery()) {
			
			System.out.println();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
