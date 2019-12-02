package ufrpe.actions.conditions;

import java.util.List;

import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;
import easy_soccer_lib.perception.FieldPerception;
import easy_soccer_lib.perception.PlayerPerception;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;

public class IfClosestPlayerToBall extends BTNode<BehaviorTreePlayer> {

	@Override
	public BTStatus tick(BehaviorTreePlayer agent) {
		PlayerPerception selfPerc = agent.getSelfPerc();
		FieldPerception fieldPerc = agent.getFieldPerc();
		
		Vector2D ballPosition = fieldPerc.getBall().getPosition();
		List<PlayerPerception> myTeam = fieldPerc.getTeamPlayers(selfPerc.getSide());
		
		PlayerPerception closestPlayer = agent.getSelfPerc();
		double closestDistance = Double.MAX_VALUE;
		
		for (PlayerPerception player : myTeam) {
			double playerDistance = player.getPosition().distanceTo(ballPosition);
			if (playerDistance < closestDistance) {
				closestDistance = playerDistance;
				closestPlayer = player;
			}
		}
		
//		print(5000, "No ", selfPerc.getUniformNumber(), ", pos: ",
//				selfPerc.getPosition(),	", TEAM ", selfPerc.getSide(),
//				", CLOSEST? " + (closestPlayer.getUniformNumber() == selfPerc.getUniformNumber()));
		
		if (closestPlayer.getUniformNumber() == selfPerc.getUniformNumber()) {
			return BTStatus.SUCCESS;
		} else {
			return BTStatus.FAILURE;
		}
	}

}
