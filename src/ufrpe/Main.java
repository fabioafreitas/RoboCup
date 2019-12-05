package ufrpe;

import exemplo.CommandTeam;

import java.net.UnknownHostException;


public class Main {
	public static void main(String[] args) throws UnknownHostException {
		BehaviorTreeTeam team1 = new BehaviorTreeTeam("Dias_Toffoli");
		BehaviorTreeTeam team2 = new BehaviorTreeTeam("Gravida_Taubate");

		team1.launchTeamAndServer();
  		//team2.launchTeam();

    }
	
}

