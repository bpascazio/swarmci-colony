package com.bytefly.swarm.cmd.util;

import java.util.Scanner;

public class SwarmUser {

	public String username = "";
	public String email = "";
	public String password = "";
	
	public SwarmUser() {
		username = "x";
		email = "x";
		password = "x";
	}
	
	public static SwarmUser getUserInfo() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Username/Email: ");
		String username = scanner.nextLine();
		System.out.print("Password: ");
		String password = scanner.nextLine();
		SwarmUser su = new SwarmUser();
		su.email = su.username = new String(username);
		su.password = new String (password);
		return su;
	}
	
}
