package com.example.juegoricardo;

public class UserScore {
	private final String username;
	private final int score;

	public UserScore(String username, int score) {
		this.username = username;
		this.score = score;
	}

	public String getUsername() {
		return username;
	}

	public int getScore() {
		return score;
	}
}
