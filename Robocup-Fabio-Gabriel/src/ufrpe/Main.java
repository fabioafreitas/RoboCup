package ufrpe;

import java.net.UnknownHostException;


public class Main {

	public static void main(String[] args) throws UnknownHostException {
		BTreeTeam team1 = new BTreeTeam("A");
		BTreeTeam team2 = new BTreeTeam("B");
		
		team1.launchTeamAndServer();
		team2.launchTeam();
	}
	
}

