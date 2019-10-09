package ufrpe;

import easy_soccer_lib.AbstractTeam;
import easy_soccer_lib.PlayerCommander;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.actions.BTreePlayer;


/**
 * Time simples, demonstrado em sala.
 */
public class BTreeTeam extends AbstractTeam {

	public BTreeTeam(String suffix) {
		super("Macrom"+ suffix, 2, true);
	}

	@Override
	protected void launchPlayer(int ag, PlayerCommander commander) {
		double x, y;

		switch (ag) {
		case 0:
			x = -50.0d;
			y = -0.0d;
			break;
		case 1:
			x = -30.0d;
			y = 0.0d;
			break;
		case 2:
			x = -30.0d;
			y = -25.0d;
			break;

		default:
			x = -37.0d;
			y = 0;
		}
		
		BTreePlayer pl = new BTreePlayer(commander, new Vector2D(x, y));
		pl.start();
	}

}
