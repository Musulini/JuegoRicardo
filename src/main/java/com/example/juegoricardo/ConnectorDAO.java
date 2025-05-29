package com.example.juegoricardo;

import com.example.juegoricardo.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectorDAO {
	private final Connection connector;

	public ConnectorDAO(Connection connector) {
		this.connector = connector;
	}

	public void createUser(User user) {
		String query = "INSERT INTO user (nombre, apellidoPaterno, apellidoMaterno, email, username, password) " +
				"VALUES (?,?,?,?,?,?)";
		try (PreparedStatement ps = connector.prepareStatement(query)) {
			attributesSet(user, ps);
			ps.executeUpdate();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}
	public String[] getLoggedUser(String username) {
		String query = "SELECT username, password FROM user WHERE username = ?";
		String[] data = new String[2];
		try(PreparedStatement ps = connector.prepareStatement(query)){
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				data[0] = rs.getString("username");
				data[1] = rs.getString("password");
			}
			return data;
		}catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
		return null;
	}

	private void attributesSet(User user, PreparedStatement ps) throws SQLException {
		ps.setString(1, user.getNombre());
		ps.setString(2,user.getApellidoP());
		ps.setString(3,user.getApellidoM());
		ps.setString(4,user.getEmail());
		ps.setString(5, user.getUsername());
		ps.setString(6, user.getPassword());
	}
}
