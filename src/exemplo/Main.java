package exemplo;

import java.net.UnknownHostException;

public class Main {
	public static void main(String[] args) throws UnknownHostException {
		CommandTeam teamA = new CommandTeam("RevoltzTeam");
		teamA.launchTeamAndServer();
	}
}