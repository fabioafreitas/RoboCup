package ufrpe.behavior_tree_nodes.game_states;

import easy_soccer_lib.utils.EMatchState;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * Condicao, que retorna Sucess
 * se o estado da partida e KickOff
 * e Failure se nao eh
 * */
public class KickOffRight extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        EMatchState state = agent.getMatchPerc().getState();
        if(state == EMatchState.KICK_OFF_RIGHT) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.FAILURE;
    }


}
