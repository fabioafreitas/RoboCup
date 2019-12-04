package ufrpe.behavior_tree_nodes.game_states;

import easy_soccer_lib.utils.EMatchState;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * jogador mais proximo a bola a chuta em direção ao gol
 */
public class FreeKickFaultLeft extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        EMatchState state = agent.getMatchPerc().getState();
        if(state == EMatchState.FREE_KICK_FAULT_LEFT) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.FAILURE;
    }
}