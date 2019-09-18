package ufrpe;

import java.net.UnknownHostException;

public class Main {
	public static void main(String[] args) throws UnknownHostException {
		CommandTeam teamA = new CommandTeam("A");
		CommandTeam teamB = new CommandTeam("B");
		
		teamA.launchTeamAndServer();
		teamB.launchTeam();
		
	}
}