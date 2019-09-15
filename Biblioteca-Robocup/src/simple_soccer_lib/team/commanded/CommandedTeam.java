package simple_soccer_lib.team.commanded;

import simple_soccer_lib.AbstractTeam;
import simple_soccer_lib.PlayerCommander;

public class CommandedTeam extends AbstractTeam {

	public CommandedTeam() {
		super("COMM", 1, false);
	}

	@Override
	protected void launchPlayer(int ag, PlayerCommander commander) {
		System.out.println("Player lançado");
		CommandedPlayer p = new CommandedPlayer(commander);
		p.start();
	}

}
