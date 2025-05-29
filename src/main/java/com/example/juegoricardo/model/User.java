package com.example.juegoricardo.model;

public class User {
	private String username;
	private String password;
	private String nombre;
	private String apellidoP;
	private String apellidoM;
	private String email;

	public User() {
	}

	public User(String nombre, String apellidoP, String apellidoM, String email, String username, String password) {
		this.nombre = nombre;
		this.apellidoP = apellidoP;
		this.apellidoM = apellidoM;
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidoP() {
		return apellidoP;
	}

	public String getApellidoM() {
		return apellidoM;
	}

	public String getEmail() {
		return email;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setApellidoP(String apellidoP) {
		this.apellidoP = apellidoP;
	}

	public void setApellidoM(String apellidoM) {
		this.apellidoM = apellidoM;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

