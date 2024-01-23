package com.dev.ipartek.bibliotecas;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class UtilesDeConsola {
	
	private static final Scanner sc =  new Scanner(System.in);
	
	public static final boolean OPCIONAL = true;
	public static final boolean OBLIGATORIO = false;
	
	public static String readString(String mensaje) {
		return readString(mensaje, OBLIGATORIO);
	}
	
	public static String readString(String mensaje, boolean opcional) {
		String texto;
		boolean repetir = true;
		
		do {
			System.out.println(mensaje + ": ");
			texto = sc.nextLine();
			
			if(!opcional && texto.trim().length() == 0) {
				System.out.println("Este dato es obligatorio");
			}else {
				repetir = false;
			}
		}while(repetir);
		
		return texto.trim().length() > 0 ? texto : null;
	}
	
	public static Long readLong(String mensaje) {
		return readLong(mensaje, OBLIGATORIO);
	}
	
	public static Long readLong(String mensaje, boolean opcional) {
		boolean hayError = true;
		long longDato = 0;
		
		do {
			try {
					
				String dato = readString(mensaje, opcional);
				
				if(dato == null) {
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
		return readInt(mensaje, OBLIGATORIO);
	}
	
	public static Integer readInt(String mensaje, boolean opcional) {
		return readInt(mensaje, opcional, null, null);
	}
	
	public static Integer readInt(String mensaje, boolean opcional, Integer minimo, Integer maximo) {
		boolean hayError = true;
		int intDato = 0;
		
		if(minimo == null) {
			minimo = Integer.MIN_VALUE;
		}
		
		if(maximo == null) {
			maximo = Integer.MAX_VALUE;
		}
		
		do {
			try {
				String dato = readString(mensaje, opcional);
			
				if(dato == null) {
					return null;
				}
			
				intDato = Integer.parseInt(dato);
				
				if(intDato < minimo || intDato > maximo) {
					System.out.println("El valor está fuera del rango permitido");
				}else {
					hayError = false;
				}
			}catch(NumberFormatException e) {
				System.out.println("El número debe ser un entero entre " + Integer.MIN_VALUE + " y " + Integer.MAX_VALUE);
			}
		}while(hayError);
		
		return intDato;
	}
	
	public static LocalDate readFecha(String mensaje) {
		return readFecha(mensaje, OBLIGATORIO);
	}
	
	public static LocalDate readFecha(String mensaje, boolean opcional) {
		return readFecha(mensaje,opcional, null, null);
	}
	
	public static LocalDate readFecha(String mensaje, boolean opcional, LocalDate minima, LocalDate maxima) {
		boolean hayError = true;
		LocalDate fecha = null;
		
		if(minima == null) {
			minima = LocalDate.MIN;
		}
		
		if(maxima == null) {
			maxima = LocalDate.MAX;
		}
		
		do {
			try {
				String dato = readString(mensaje + " [AAAA-MM-DD]", opcional);
				
				if(dato == null) {
					return null;
				}
				
				fecha = LocalDate.parse(dato);
				
				if(fecha.isBefore(minima) || fecha.isAfter(maxima)) {
					System.out.println("Fecha fuera de los límites");
				}else {
					hayError = false;
				}
				
			}catch(DateTimeParseException e) {
				System.out.println("La fecha debe ser válida");
			}
		}while(hayError);
		
		return fecha;
	}
	
	public static String readDni(String mensaje) {
		return readDni(mensaje, OBLIGATORIO);
	}
	
	public static String readDni(String mensaje, boolean opcional) {
		String texto;
		boolean repetir = true;
		
		do {
			texto = readString(mensaje, opcional);
			
			if(texto != null && !Dni.validarDni(texto)) {
				System.out.println("El DNI no es válido");
			}else {
				repetir = false;
			}
		}while(repetir);
		
		return texto == null ? null : texto.trim().toUpperCase();
	}
}
