package ufrpe.behavior_tree_nodes.game_states;

import easy_soccer_lib.utils.EMatchState;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * jogador mais próximo a bola chuta para outro jogador de seu time.
 *
 * este estado serve como um if, pois logo em seguida é chamado o pass ball
 */
public class IndirectFreeKickLeft extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        EMatchState state = agent.getMatchPerc().getState();
        if(state == EMatchState.INDIRECT_FREE_KICK_LEFT) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.FAILURE;
    }
}
