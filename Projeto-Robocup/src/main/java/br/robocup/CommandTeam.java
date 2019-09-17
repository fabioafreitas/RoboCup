package br.robocup;

import easy_soccer_lib.AbstractTeam;
import easy_soccer_lib.PlayerCommander;

public class CommandTeam extends AbstractTeam {
	public CommandTeam(String suffix) {
		super("Team"+suffix, 4, false);
	}

	@Override
	protected void launchPlayer(int ag, PlayerCommander comm) {
		System.out.println("Player lançado!");
		CommandPlayer p = new CommandPlayer(comm);
		p.start();
	}
}