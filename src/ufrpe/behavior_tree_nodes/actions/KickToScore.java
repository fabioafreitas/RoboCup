package ufrpe.behavior_tree_nodes.actions;

import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;

public class KickToScore extends BTNode<BehaviorTreePlayer> {

	@Override
	public BTStatus tick(BehaviorTreePlayer agent) {
		Vector2D ballPos = agent.getFieldPerc().getBall().getPosition();
		
		//condicao ruim extrema: longe demais da bola
		if (!agent.isCloseTo(ballPos, 3.0)) {
			return BTStatus.FAILURE;
		}
		

		if (agent.isAlignedTo(ballPos)) {
			if (agent.isCloseTo(ballPos, 1.0)) {
				//da um chute com forca maxima (100)
				agent.getCommander().doKickToPoint(100.0d, agent.getGoalPosition());
				return BTStatus.SUCCESS;
			} else {
				//corre com forca intermediaria (porque esta perto da bola)
				agent.getCommander().doDashBlocking(60.0d);
			}
		} else {
			agent.getCommander().doTurnToPoint(ballPos);
		}
		return BTStatus.RUNNING;
	}

}
