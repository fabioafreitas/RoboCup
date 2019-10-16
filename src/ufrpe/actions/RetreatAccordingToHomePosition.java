package ufrpe.actions;

import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

public class RetreatAccordingToHomePosition extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        Vector2D homePosition = agent.getHomePosition();

        Vector2D inicioDoCampo = (agent.getSelfPerc().getSide() == EFieldSide.LEFT) ?
                new Vector2D(-52, homePosition.getY()) :
                new Vector2D(52, homePosition.getY());;

        //Esta no fim do campo, nao pode mais avancar
        if(agent.isCloseTo(inicioDoCampo, 3)) {
            return BTStatus.SUCCESS;
        }

        //Se esta virado para o outro lado do campo, avancar
        if(agent.isAlignedTo(inicioDoCampo)) {
            agent.getCommander().doDashBlocking(50.0d);
        } else {
            agent.getCommander().doTurnToPoint(inicioDoCampo);
        }
        return BTStatus.RUNNING;
    }
}
