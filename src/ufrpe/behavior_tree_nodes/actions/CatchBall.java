package ufrpe.behavior_tree_nodes.actions;

import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

public class CatchBall extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        Vector2D ballPos = agent.getFieldPerc().getBall().getPosition();

        //corre atras da bola e da um pequeno toque
        if (agent.isAlignedTo(ballPos)) {
            agent.getCommander().doDashBlocking(85.0d);
        } else {
            agent.getCommander().doTurnToPoint(ballPos);
        }

        //condicao desejada: perto da bola (dist < 3)
        if (agent.isCloseTo(ballPos, 1.0)) {
            agent.getCommander().doCatchBlocking(0);
            return BTStatus.SUCCESS;
        }

        return BTStatus.RUNNING;
    }
}
