package ufrpe.actions;

import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

public class ReturnToHomePosition extends BTNode<BehaviorTreePlayer> {

	@Override
	public BTStatus tick(BehaviorTreePlayer agent) {
		//se distancia maior que 50, voltar para posicao inicial
		Vector2D homePosition = agent.getHomePosition();

		//Se ja esta proximo da homePosition, retorna sucesso
		if(agent.isCloseTo(homePosition, 1.0d)) {
			return BTStatus.SUCCESS;
		}

		//Se esta virado para a homePosition, entao caminhe ate ela
		//senao, vire na direcao dela
		if (agent.isAlignedTo(homePosition)) {
			agent.getCommander().doDashBlocking(50.0d); //chega mais perto da bola
		} else {
			agent.getCommander().doTurnToPoint(homePosition);
		}

		return BTStatus.RUNNING;
	}

}
