package ufrpe.behavior_tree_nodes.game_states_movements;

import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

public class MovePlayerToSmallArea extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        Vector2D ballPos = agent.getFieldPerc().getBall().getPosition();
        
        if(agent.getSelfPerc().getSide() == EFieldSide.LEFT) {
        	if(agent.getSelfPerc().getUniformNumber() == 6) {
        		
        		agent.getCommander().doMoveBlocking(ballPos.getX()-15,ballPos.getY()+10);
        	}
        	if(agent.getSelfPerc().getUniformNumber() == 5) {
        		agent.getCommander().doMoveBlocking(ballPos.getX()-30,ballPos.getY()+20);
        	}
        	 return BTStatus.SUCCESS;
        }
        else if(agent.getSelfPerc().getSide() == EFieldSide.RIGHT) {
        	if(agent.getSelfPerc().getUniformNumber() == 6) {
        		
        		agent.getCommander().doMoveBlocking(ballPos.getX()-15,ballPos.getY()+10);
        	}
        	if(agent.getSelfPerc().getUniformNumber() == 5) {
        		agent.getCommander().doMoveBlocking(ballPos.getX()-30,ballPos.getY()+20);
        	}
        	 return BTStatus.SUCCESS;
        }
      
        return BTStatus.RUNNING;
    }
}
