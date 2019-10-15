package ufrpe;

import easy_soccer_lib.AbstractTeam;
import easy_soccer_lib.PlayerCommander;
import easy_soccer_lib.utils.Vector2D;

public class BehaviorTreeTeam extends AbstractTeam {
//	public BehaviorTreeTeam(String suffix) {
//		super(suffix, 8, false);
//	}
	public BehaviorTreeTeam(String suffix) {
		super(suffix, 1, false);
	}

	@Override
	protected void launchPlayer(int ag, PlayerCommander commander) {
		Vector2D posicaoInicial = null;

		switch (ag) {
			case 0: //GOLEIRO
				posicaoInicial = new Vector2D(-50.0d, 0.0d); break;
			case 1: //MEIA
				posicaoInicial = new Vector2D(-15.0d, 0.0d); break;
			case 2: //ZAGUEIRO_DIREITO
				posicaoInicial = new Vector2D(-40.0d, 16.0d); break;
			case 3: //ZAGUEIRO_ESQUERDO
				posicaoInicial = new Vector2D(-40.0d, -16.0d); break;
			case 4: //LATERAL_DIREITO
				posicaoInicial = new Vector2D(-20.0d, 20.0d); break;
			case 5: //LATERAL_ESQUERDO
				posicaoInicial = new Vector2D(-20.0d, -20.0d); break;
			case 6: //ATACANTE_DIREITO
				posicaoInicial = new Vector2D(-7.0d, 12.0d); break;
			case 7: //ATACANTE_ESQUERDO
				posicaoInicial = new Vector2D(-7.0d, -12.0d); break;
			default: break;
		}
		
		BehaviorTreePlayer jogador = new BehaviorTreePlayer(commander, posicaoInicial);
		jogador.start();
	}

}
