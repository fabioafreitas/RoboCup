package ufrpe.behavior_tree_nodes.conditions;

import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

import java.awt.*;

/**
 * Checa se a bola está dentro da grande area
 *
 *
 *
 *
 */
public class IfBallIsInSmallArea extends BTNode<BehaviorTreePlayer> {

    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        EFieldSide side = agent.getSelfPerc().getSide();
        Rectangle area = (side == EFieldSide.LEFT) ?
                new Rectangle(-52, -9, 6, 18) :
                new Rectangle(46, -9, 6, 18);
        Vector2D ballPos = agent.getFieldPerc().getBall().getPosition();

        if(area.contains(ballPos.getX(), ballPos.getY())) {
            return BTStatus.SUCCESS;
        }

        return BTStatus.FAILURE;
    }
}
