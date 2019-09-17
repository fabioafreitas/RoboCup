package br.robocup;

import java.net.UnknownHostException;

import com.badlogic.gdx.ai.btree.*;

public class Main {
	public static void main(String[] args) throws UnknownHostException {
		CommandTeam teamA = new CommandTeam("A");
		CommandTeam teamB = new CommandTeam("B");
		
		teamA.launchTeamAndServer();
		teamB.launchTeam();
		
		BehaviorTree<Integer> teste;
	}
}