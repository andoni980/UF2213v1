package com.dev.UF2213v1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ClienteApp {

	private static final String URL = "jdbc:mysql://localhost:3306/manana_tienda";
	private static final String USER = "root";
	private static final String PASS = "root";
	
	private static final String CAMPOS = "dni,dni_diferencial,nombre,apellidos,fecha_nacimiento";
	private static final String SELECT_ALL = "SELECT id, " + CAMPOS + " FROM clientes";
	private static final String SELECT_BY_ID = "SELECT " + CAMPOS + " FROM clientes WHERE id = ?";
	private static final String INSERT = "INSERT INTO clientes ( " + CAMPOS + ") VALUES(?,?,?,?,?)";
	
	private static Connection con;

	public static void main(String[] args) {

		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			
			getAll();
			getById(2L);
			save("45678765R", 2, "Iker", "Vargas", LocalDate.of(2013,02,15));
			getAll();
			
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la conexión");
			System.err.println(e.getMessage());
		}
	}
	
	private static void getAll() {
		
		try (PreparedStatement pstmt = con.prepareStatement(SELECT_ALL);
			ResultSet rs = pstmt.executeQuery()) {
			
			while(rs.next()) {
			 System.out.printf("%s\t;%s\t;%s\t;%s\t;%s\t;%s\n",
//			System.out.printf("%2s %s %3s %-10s %-20s %s\n",
					rs.getString("id"),
					rs.getString("dni"),
					rs.getString("dni_diferencial"),
					rs.getString("nombre"),
					rs.getString("apellidos"),
					rs.getString("fecha_nacimiento"));
			}
			
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la consulta getAll()");
			System.err.println(e.getMessage());
		}
	}
	
	private static void getById(Long id) {
		try (PreparedStatement pstmt = con.prepareStatement(SELECT_BY_ID)) {
			pstmt.setLong(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if(rs.next()) {
					System.out.printf("""
							ID:					%s
							DNI:				%s
							DNI_DIFERENCIAL:	%s
							NOMBRE:				%s
							APELLIDOS:			%s
							FECHA DE NACIMIENTO	%s	
							""",
					id,
					rs.getString("dni"),
					rs.getString("dni_diferencial"),
					rs.getString("nombre"),
					rs.getString("apellidos"),
					rs.getString("fecha_nacimiento"));
				}
			}
			
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la consulta getById");
			System.err.println(e.getMessage());
		}
	}
	
	private static void save(String dni, Integer dniDiferencial, String nombre, String apellidos, LocalDate fechaNacimiento) {
		try (PreparedStatement pstmt = con.prepareStatement(INSERT)) {
			pstmt.setString(1, dni);
			pstmt.setInt(2, dniDiferencial);
			pstmt.setString(3, nombre);
			pstmt.setString(4, apellidos);
			pstmt.setDate(5, java.sql.Date.valueOf(fechaNacimiento));
			
			int numeroRegistrosInsertados = pstmt.executeUpdate();
			System.out.println("Número de Registros insertados: " + numeroRegistrosInsertados);
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la consulta save()");
			System.err.println(e.getMessage());
		}
	}

}
