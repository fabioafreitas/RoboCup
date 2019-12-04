package ufrpe.behavior_tree_nodes.actions;

import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * Faz o jogador avancar na direcao a frente de sua homePositio, ou seja,
 * o jogador pode avancar de forma a tentar manter a formacao do time
 */
public class AdvanceAccordingToHomePosition extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        Vector2D homePosition = agent.getHomePosition();

        Vector2D fimDoCampo = (agent.getSelfPerc().getSide() == EFieldSide.LEFT) ?
                new Vector2D(52, homePosition.getY()) :
                new Vector2D(-52, homePosition.getY());;

        //Esta no fim do campo, nao pode mais avancar
        if(agent.isCloseTo(fimDoCampo, 3)) {
            return BTStatus.SUCCESS;
        }

        //Se esta virado para o outro lado do campo, avancar
        if(agent.isAlignedTo(fimDoCampo)) {
            agent.getCommander().doDashBlocking(100.0d);
        } else {
            agent.getCommander().doTurnToPoint(fimDoCampo);
        }
        return BTStatus.RUNNING;
    }
}
