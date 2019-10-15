package ufrpe.actions.game_states;

import easy_soccer_lib.utils.EMatchState;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * Condicao, que retorna Sucess
 * se o estado da partida e KickOff
 * e Failure se nao eh
 * */
public class KickOffLeft extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        //TODO falta definir a acao a ser tomada
        return agent.getMatchPerc().getState() == EMatchState.KICK_OFF_LEFT ? BTStatus.SUCCESS : BTStatus.FAILURE;
    }
}
