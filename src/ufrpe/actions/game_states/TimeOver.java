package ufrpe.actions.game_states;

import easy_soccer_lib.utils.EMatchState;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * Caso o jogo chege neste estado, então os jogadores não realizam mas nenhuma ação
 */
public class TimeOver extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        return null;
    }
}