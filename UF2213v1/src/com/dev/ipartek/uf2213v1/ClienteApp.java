package com.dev.ipartek.uf2213v1;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.dev.ipartek.bibliotecas.UtilesDeConsola.*;

public class ClienteApp {

	private static final String URL = "jdbc:mysql://localhost:3306/manana_tienda";
	private static final String USER = "root";
	private static final String PASS = "root";
	
	private static final String SQL_CAMPOS = "dni,dni_diferencial,nombre,apellidos,fecha_nacimiento";
	private static final String SQL_SELECT_ALL = "SELECT id, " + SQL_CAMPOS + " FROM clientes ";
	private static final String SQL_SELECT_BY_ID = "SELECT id, " + SQL_CAMPOS + " FROM clientes c WHERE id=?";
	private static final String SQL_INSERT = "INSERT INTO clientes ( " + SQL_CAMPOS + ") VALUES(?,?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE clientes SET dni=?, dni_diferencial=?, nombre=?, apellidos=?, fecha_nacimiento=? WHERE id=?";
	private static final String SQL_DELETE = "DELETE FROM clientes WHERE id=?";
	private static final String SQL_BY_ID_CON_FACTURAS = """
			SELECT 
				c.id, c.dni, c.dni_diferencial, c.nombre, c.apellidos, c.fecha_nacimiento,
				f.numero, f.fecha
			 FROM clientes AS c
			 JOIN facturas AS f
			 ON f.clientes_id = c.id 
			 WHERE c.id = ?;
			""";
	private static final String SQL_BY_ID_CON_FACTURAS_CON_PRODUCTOS = """
		SELECT 
			c.id, c.dni, c.dni_diferencial, c.nombre, c.apellidos, c.fecha_nacimiento,
			f.numero, f.fecha, f.id,
            fp.cantidad,
            p.nombre, p.precio, p.id,
            p.precio * fp.cantidad AS total
            
		FROM clientes AS c
		JOIN facturas AS f
		ON f.clientes_id = c.id 
		JOIN facturas_has_productos AS fp
		ON fp.facturas_id = f.id
		JOIN productos AS p
		ON fp.productos_id = p.id 
		WHERE c.id = ?
		ORDER BY f.id,p.id;
		""";
	
	private static final int SALIR = 0;
	private static final int VER_TODOS = 1;
	private static final int BUSCAR_POR_ID = 2;
	private static final int INSERTAR = 3;
	private static final int MODIFICAR = 4;
	private static final int BORRAR = 5;
	private static final int BUSCAR_POR_ID_CON_FACTURAS = 6;
	private static final int BUSCAR_POR_ID_CON_FACTURAS_CON_PRODUCTOS = 7;
	
	private static Connection con;

	public static void main(String[] args) {

		try {
			con = DriverManager.getConnection(URL, USER, PASS);
			
			int opcion;
			do {
				mostrarMenu();
				opcion = pedirOpcion();
				ejecutarOpcion(opcion);
			}while(opcion != SALIR);
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la conexión");
			System.err.println(e.getMessage());
		}
	}
	
	private static void mostrarMenu() {
		
		System.out.println("""
				MENU
				----
				
				1. VER TODOS
				2. BUSCAR POR ID
				3. INSERTAR CLIENTE
				4. MODIFICAR CLIENTE
				5. BORRAR CLIENTE
				6. BUSCAR POR ID CON FACTURAS
				7. BUSCAR POR ID CON FACTURAS Y PRODUCTOS
				
				0. SALIR
				
				""");
	}

	private static int pedirOpcion() {
		return readInt("Introduce la opción elegida");
	}


	private static void ejecutarOpcion(int opcion) {
		
		switch(opcion) {
		
		case VER_TODOS:
			getAll();
			break;
		case BUSCAR_POR_ID:
			find();
			break;
		case INSERTAR:
			save();
			break;
		case MODIFICAR:
			update();
			break;
		case BORRAR:
			delete();
			break;
		case BUSCAR_POR_ID_CON_FACTURAS:
			findWithFacturas();
			break;
		case BUSCAR_POR_ID_CON_FACTURAS_CON_PRODUCTOS:
			findWithFacturasWithProductos();
			break;
		case SALIR:
			System.out.println("Nos vemos pronto");
			break;
		default:
			System.out.println("No conozco esa opción");
		}
	}

	private static void getAll() {
		
		try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL);
			ResultSet rs = pstmt.executeQuery()) {
			
			while(rs.next()) {
//			 System.out.printf("%s\t;%s\t;%s\t;%s\t;%s\t;%s%n",
			System.out.printf("%2s %s %3s %-15s %-30s %s\n",
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
	
	private static void find() {
		Long id = readLong("Introduce el id a buscar");
		getById(id);
	}
	
	private static void getById(Long id) {
		try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_BY_ID)) {
			pstmt.setLong(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if(rs.next()) {
					mostrarCliente(rs);
				}
			}
			
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la consulta getById");
			System.err.println(e.getMessage());
		}
	}

	private static void save() {
		String dni = readString("DNI");
		Integer dniDiferencial = readInt("DNI diferencial");
		String nombre = readString("Nombre");
		String apellidos = readString("Apellidos", OPCIONAL);
		LocalDate fechaNacimiento = readFecha("Fecha de nacimiento", OPCIONAL, LocalDate.of(1900,1,1), LocalDate.now().minusYears(18));
		
		save(dni, dniDiferencial, nombre, apellidos, fechaNacimiento);
	}
	
	private static void save(String dni, Integer dniDiferencial, String nombre, String apellidos, LocalDate fechaNacimiento) {
		try (PreparedStatement pstmt = con.prepareStatement(SQL_INSERT)) {
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
	
	private static void update() {
		Long id = readLong("Introduce el id del elemento a modificar");
		
		String dni = readString("DNI");
		Integer dniDiferencial = readInt("DNI diferencial", OPCIONAL);
		String nombre = readString("Nombre");
		String apellidos = readString("Apellidos", OPCIONAL);
		LocalDate fechaNacimiento = readFecha("Fecha de Nacimiento", OPCIONAL, LocalDate.of(1900, 1, 1), LocalDate.now().minusYears(18));
		
		update(id, dni, dniDiferencial, nombre, apellidos, fechaNacimiento);
	}
	
	private static void update(Long id, String dni, Integer dniDiferencial, String nombre, String apellidos, LocalDate fechaNacimiento) {
		try (PreparedStatement pstmt = con.prepareStatement(SQL_UPDATE)) {
			
			pstmt.setString(1, dni);
			pstmt.setInt(2, dniDiferencial);
			pstmt.setString(3, nombre);
			pstmt.setString(4, apellidos);
			pstmt.setDate(5, java.sql.Date.valueOf(fechaNacimiento));
			pstmt.setLong(6, id);
			
			int numeroRegistrosModificados = pstmt.executeUpdate();
			System.out.println("Número de Registros modificados: " + numeroRegistrosModificados);
			
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la consulta update()");
			System.err.println(e.getMessage());
		}
	}
	
	
	private static void delete() {
		long id = readLong("Introduce el id a borrar");
		delete(id);
	}
	
	private static void delete(Long id) {
		try (PreparedStatement pstmt = con.prepareStatement(SQL_DELETE)) {
			
			pstmt.setLong(1,id);
			
			int numeroRegistrosBorrados = pstmt.executeUpdate();
			System.out.println("Número de registros eliminados: " + numeroRegistrosBorrados);
			
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la consulta delete()");
			System.err.println(e.getMessage());
		}
	}
	
	private static void findWithFacturas() {
		long id = readLong("Introduce el ID del cliente a buscar");
		getByIdConFacturas(id);
	}
	
	private static void getByIdConFacturas(Long id) {
		boolean isClienteMostrado = false;
		
		try (PreparedStatement pstmt = con.prepareStatement(SQL_BY_ID_CON_FACTURAS)) {
			pstmt.setLong(1, id);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while(rs.next()) {
					if(!isClienteMostrado) {
						mostrarCliente(rs);
						
						isClienteMostrado = true;
					}
					mostrarFactura(rs);
				}
			}
			
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la consulta getByIdConFacturas()");
			System.err.println(e.getMessage());
		}
	}
	
	private static void findWithFacturasWithProductos() {
		long id = readLong("Introduce el ID del cliente a buscar");
		getByIdConFacturasConProductos(id);
	}
	
	private static void getByIdConFacturasConProductos(Long id) {
		boolean isClienteMostrado = false;
		Long idFacturaAnterior = null;
		
		try (PreparedStatement pstmt = con.prepareStatement(SQL_BY_ID_CON_FACTURAS_CON_PRODUCTOS)) {
			pstmt.setLong(1, id);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while(rs.next()) {
					if(!isClienteMostrado) {
						mostrarCliente(rs);
						
						isClienteMostrado = true;
					}
					
					Long idFacturaActual = Long.parseLong(rs.getString("f.id"));
					if(idFacturaActual != idFacturaAnterior ) {
						mostrarFactura(rs);
					}
					mostrarProducto(rs);
					
					idFacturaAnterior = idFacturaActual;
				}
			}
		} catch (SQLException e) {
			System.err.println("No se ha podido realizar la consulta getByIdConFacturasConProductos()");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void mostrarProducto(ResultSet rs) throws SQLException {
		System.out.printf("""
				
					PRODUCTO
					--------
					\tNombre:        %s
					\tCantidad:      %s
					\tPrecio:        %s\n
					\tTOTAL:%s\n\n""",
				rs.getString("p.nombre"),
				rs.getString("fp.cantidad"),
				rs.getString("p.precio"),
				rs.getString("total"));
	}

	private static void mostrarFactura(ResultSet rs) throws SQLException {
		System.out.printf(
				"""
				FACTURA
				-------
				Factura Número:      %s
				Fecha:               %s
				""",
				rs.getString("f.numero")
				,rs.getString("f.fecha"));
	}
	
	private static void mostrarCliente(ResultSet rs) throws SQLException {
		System.out.printf("""
				
				CLIENTE
				=========
				
				ID:                   %s
				DNI:                  %s
				DNI_DIFERENCIAL:      %s
				NOMBRE:               %s
				APELLIDOS:            %s
				FECHA DE NACIMIENTO:  %s
				
						
				""",
		rs.getString("c.id"),
		rs.getString("c.dni"),
		rs.getString("c.dni_diferencial"),
		rs.getString("c.nombre"),
		rs.getString("c.apellidos"),
		rs.getString("c.fecha_nacimiento"));
	}
	

}
