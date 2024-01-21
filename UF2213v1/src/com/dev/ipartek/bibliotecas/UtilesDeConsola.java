package com.dev.ipartek.bibliotecas;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class UtilesDeConsola {
	
	private static final Scanner sc =  new Scanner(System.in);
	
	public static String readString(String mensaje) {
		System.out.print(mensaje + ": ");
		return sc.nextLine();
	}
	
	public static Long readLong(String mensaje) {
		boolean hayError = true;
		long longDato = 0;
		
		do {
			try {
					
				String dato = readString(mensaje);
				
				if(dato.trim().length() == 0) {
					return null; 
				}
				
				longDato = Long.parseLong(dato);
				hayError = false;
			}catch(NumberFormatException e) {
				System.out.println("El número debe ser de la longitud correcta, ni menos que " + Long.MIN_VALUE + " ni más que " + Long.MAX_VALUE);
			}
		}while(hayError);
		
		return longDato;
	}
	

	public static Integer readInt(String mensaje) {
		return readInt(mensaje, false);
	}
	
	public static Integer readInt(String mensaje, boolean opcional) {
		boolean hayError = true;
		int intDato = 0;
		
		do {
			try {
				String dato = readString(mensaje);
			
				if(opcional && dato.trim().length() == 0) {
					return null;
				}
			
				intDato = Integer.parseInt(dato);
				hayError = false;
			}catch(NumberFormatException e) {
				System.out.println("El número debe ser un entero entre " + Integer.MIN_VALUE + " y " + Integer.MAX_VALUE);
			}
		}while(hayError);
		
		return intDato;
	}
	
	public static LocalDate readFecha(String mensaje) {
		boolean hayError = true;
		LocalDate fecha = null;
		
		do {
			try {
				String dato = readString(mensaje + " [AAAA-MM-DD]");
				
				if(dato.trim().length() == 0) {
					return null;
				}
				
				fecha = LocalDate.parse(dato);
				hayError = false;
			}catch(DateTimeParseException e) {
				System.out.println("La fecha debe ser válida");
			}
		}while(hayError);
		
		return fecha;
	}

}
