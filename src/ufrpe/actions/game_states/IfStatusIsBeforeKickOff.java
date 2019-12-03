package ufrpe.actions.game_states;

import easy_soccer_lib.perception.PlayerPerception;
import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.EMatchState;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * Condicao, que retorna Sucess
 * se o estado da partida e PrepareToKick
 * e Failure se nao eh
 * */
public class IfStatusIsBeforeKickOff extends BTNode<BehaviorTreePlayer>{
	
	private PlayerPerception selfPerc;
	private final int GOLEIRO = 0;
	private final int MEIA = 2; 
	private final int ZAGUEIRO_DIREITO = 3;
	private final int ZAGUEIRO_ESQUERDO = 4;
	private final int LATERAL_DIREITO = 5;
	private final int LATERAL_ESQUERDO = 6;
	private final int ATACANTE_DIREITO = 7;
	private final int ATACANTE_ESQUERDO = 8;
	@Override
    public BTStatus tick(BehaviorTreePlayer agent) {
		if(agent.getMatchPerc().getState() == EMatchState.AFTER_GOAL_LEFT || agent.getMatchPerc().getState() == EMatchState.KICK_OFF_LEFT) { //por enquanto mudando a posicao utilizando essas duas transicoes de estados
			
			if(agent.getSelfPerc().getSide() == EFieldSide.LEFT) {
				agent.getCommander().doMoveBlocking(agent.getHomePosition().getX(), agent.getHomePosition().getY());
				System.out.println("AAAAAAAAAA"+agent.getHomePosition().toString());
				return BTStatus.SUCCESS;
			}
			else if(agent.getSelfPerc().getSide() == EFieldSide.RIGHT) {
				agent.getCommander().doMoveBlocking(-agent.getHomePosition().getX(), -agent.getHomePosition().getY());
				System.out.println("BBBBBBBBBB"+agent.getHomePosition().toString());
				return BTStatus.SUCCESS;
			}
		}
		else if(agent.getMatchPerc().getState() == EMatchState.AFTER_GOAL_RIGHT || agent.getMatchPerc().getState() == EMatchState.KICK_OFF_RIGHT) {

			if(agent.getSelfPerc().getSide() == EFieldSide.LEFT) {
				agent.getCommander().doMoveBlocking(agent.getHomePosition().getX(), agent.getHomePosition().getY());
				System.out.println(agent.getHomePosition().toString());
				return BTStatus.SUCCESS;
			}
			else if(agent.getSelfPerc().getSide() == EFieldSide.RIGHT) {
				agent.getCommander().doMoveBlocking(-agent.getHomePosition().getX(), -agent.getHomePosition().getY());
				System.out.println(agent.getHomePosition().toString());
				return BTStatus.SUCCESS;
			}
		}
//		if(agent.getSelfPerc().getUniformNumber() == GOLEIRO) {
//			agent.getSelfPerc().setDirection( new Vector2D(50.0d, 0.0d));
//		}
		return BTStatus.RUNNING;
        //TODO falta definir a acao a ser tomada
        //return agent.getMatchPerc().getState() == EMatchState.BEFORE_KICK_OFF ? BTStatus.SUCCESS : BTStatus.FAILURE;
    }

	
}
