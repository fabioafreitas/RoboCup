package ufrpe.actions.game_states;

import easy_soccer_lib.utils.EMatchState;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * Condicao, que retorna Sucess
 * se o estado da partida e PrepareToKick
 * e Failure se nao eh
 * */
public class PrepareToKick  extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        //TODO falta definir a acao a ser tomada
        return agent.getMatchPerc().getState() == EMatchState.BEFORE_KICK_OFF ? BTStatus.SUCCESS : BTStatus.FAILURE;
    }
}
