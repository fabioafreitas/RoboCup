package ufrpe.behavior_tree_nodes.game_states;

import easy_soccer_lib.utils.EMatchState;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

public class GoalKickLeft extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        EMatchState state = agent.getMatchPerc().getState();
        if(state == EMatchState.GOAL_KICK_LEFT) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.FAILURE;
    }
}
