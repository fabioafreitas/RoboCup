package exemplo;

import easy_soccer_lib.AbstractTeam;
import easy_soccer_lib.PlayerCommander;

public class CommandTeam extends AbstractTeam {
	public CommandTeam(String nomeDoTime) {
		super(nomeDoTime, 4, false);
	}

	@Override
	protected void launchPlayer(int ag, PlayerCommander comm) {
		System.out.println("Player lan�ado!");
		CommandPlayer p = new CommandPlayer(comm);
		p.start();
	}
}