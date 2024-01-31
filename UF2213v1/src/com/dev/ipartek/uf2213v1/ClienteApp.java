package com.dev.ipartek.uf2213v1;


import static com.dev.ipartek.bibliotecas.UtilesDeConsola.OPCIONAL;
import static com.dev.ipartek.bibliotecas.UtilesDeConsola.readFecha;
import static com.dev.ipartek.bibliotecas.UtilesDeConsola.readInt;
import static com.dev.ipartek.bibliotecas.UtilesDeConsola.readLong;
import static com.dev.ipartek.bibliotecas.UtilesDeConsola.readString;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ClienteApp {

	private static final String URL = "jdbc:mysql://localhost:3306/manana_tienda";
	private static final String USER = "root";
	private static final String PASS = System.getenv("MANANA_TIENDA_PASSWORD");
	
	private static final String SQL_SELECT_ALL = "Call clientes_select_all()";
	private static final String SQL_SELECT_BY_ID = "Call clientes_select_by_id(?)";
	private static final String SQL_INSERT = "Call clientes_save(?,?,?,?,?)";
	private static final String SQL_UPDATE = "Call clientes_update(?,?,?,?,?,?)";
	private static final String SQL_DELETE = "Call clientes_delete(?)";
	private static final String SQL_BY_ID_CON_FACTURAS = "Call clientes_by_id_facturas(?)";
	private static final String SQL_BY_ID_CON_FACTURAS_CON_PRODUCTOS = "Call clientes_by_id_facturas_productos(?)";
	
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
		
		try (CallableStatement cstmt = con.prepareCall(SQL_SELECT_ALL);
			ResultSet rs = cstmt.executeQuery()) {
			
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
		try (CallableStatement cstmt = con.prepareCall(SQL_SELECT_BY_ID)) {
			cstmt.setLong(1, id);
			try (ResultSet rs = cstmt.executeQuery()) {
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
		try (CallableStatement cstmt = con.prepareCall(SQL_INSERT)) {
			cstmt.setString(1, dni);
			cstmt.setInt(2, dniDiferencial);
			cstmt.setString(3, nombre);
			cstmt.setString(4, apellidos);
			cstmt.setDate(5, java.sql.Date.valueOf(fechaNacimiento));
			
			int numeroRegistrosInsertados = cstmt.executeUpdate();
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
		try (CallableStatement cstmt = con.prepareCall(SQL_UPDATE)) {
			
			cstmt.setString(1, dni);
			cstmt.setInt(2, dniDiferencial);
			cstmt.setString(3, nombre);
			cstmt.setString(4, apellidos);
			cstmt.setDate(5, java.sql.Date.valueOf(fechaNacimiento));
			cstmt.setLong(6, id);
			
			int numeroRegistrosModificados = cstmt.executeUpdate();
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
		try (CallableStatement cstmt = con.prepareCall(SQL_DELETE)) {
			
			cstmt.setLong(1,id);
			
			int numeroRegistrosBorrados = cstmt.executeUpdate();
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
		
		try (CallableStatement cstmt = con.prepareCall(SQL_BY_ID_CON_FACTURAS)) {
			cstmt.setLong(1, id);
			
			try (ResultSet rs = cstmt.executeQuery()) {
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
		
		try (CallableStatement cstmt = con.prepareCall(SQL_BY_ID_CON_FACTURAS_CON_PRODUCTOS)) {
			cstmt.setLong(1, id);
			
			try (ResultSet rs = cstmt.executeQuery()) {
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
