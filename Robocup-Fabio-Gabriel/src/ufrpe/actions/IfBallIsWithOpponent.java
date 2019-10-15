package ufrpe.actions;

import easy_soccer_lib.perception.MatchPerception;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * Checa se a posse de bola está com time oponente
 *
 * se a bola esta mais proxima de um jogador de um jogador oponente
 */
public class IfBallIsWithOpponent extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        //TODO
        return null;
    }
}
