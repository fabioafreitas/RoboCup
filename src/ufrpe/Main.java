package ufrpe;

import exemplo.CommandTeam;

import java.net.UnknownHostException;


public class Main {
	public static void main(String[] args) throws UnknownHostException {
		BehaviorTreeTeam team1 = new BehaviorTreeTeam("Sport");
		BehaviorTreeTeam team2 = new BehaviorTreeTeam("Santa_Cruz");

		team1.launchTeamAndServer();
  		team2.launchTeam();

    }
	
}

