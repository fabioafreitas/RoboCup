package simple_soccer_lib.team.commanded;

import java.net.UnknownHostException;


public class MainCommanded {

	public static void main(String[] args) throws UnknownHostException {
		CommandedTeam team1 = new CommandedTeam();
		team1.launchTeamAndServer();

	}
	
}

