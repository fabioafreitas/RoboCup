package ufrpe.actions;

import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * Faz o jogador avancar na direcao a frente de sua homePositio, ou seja,
 * o jogador pode avancar de forma a tentar manter a formacao do time
 */
public class AdvanceWithoutBall extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        //TODO
        return null;
    }
}
