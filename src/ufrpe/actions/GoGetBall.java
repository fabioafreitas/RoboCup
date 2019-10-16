package ufrpe.actions;

import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;


public class GoGetBall extends BTNode<BehaviorTreePlayer> {

	@Override
	public BTStatus tick(BehaviorTreePlayer agent) {
		Vector2D ballPos = agent.getFieldPerc().getBall().getPosition();
		
		//condicao desejada: perto da bola (dist < 3) 
		if (agent.isCloseTo(ballPos, 1.0)) {
			print("PERTO!");
			return BTStatus.SUCCESS;
		}

		//corre atras da bola e da um pequeno toque
		if (agent.isAlignedTo(ballPos)) {
			agent.getCommander().doDashBlocking(100.0d);
		} else {
			agent.getCommander().doTurnToPoint(ballPos);
		}
		
		return BTStatus.RUNNING;
	}
	
}
