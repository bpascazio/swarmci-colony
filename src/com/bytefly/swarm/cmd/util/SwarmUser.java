package com.bytefly.swarm.cmd.util;

import java.util.Scanner;

public class SwarmUser {

	public String username = "";
	public String email = "";
	public String password = "";
	
	public SwarmUser() {
		username = "bpascazio";
		email = "bob@bytefly.com";
		password = "password";
	}
	
	public static SwarmUser getUserInfo() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Username: ");
		String username = scanner.nextLine();
		System.out.print("Password: ");
		String password = scanner.nextLine();
		return new SwarmUser();
	}
	
}
