package ufrpe.actions;

import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

/**
 * Se o jogador atual estiver virado para o próprio gol, então
 * ele faz gira 45 graus e chuta
 * senao, ele apenas chuta a bola
 */
//TODO falta testar
public class Drible  extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        Vector2D ballPos = agent.getFieldPerc().getBall().getPosition();

        //condicao desejada: perto da bola (dist < 3)
        if (!agent.isCloseTo(ballPos, 1.0)) {
            return BTStatus.RUNNING;
        }

        //corre atras da bola e da um pequeno toque
        if (true) { //TODO falta terminar esta classe, este if é onde faço as checagens descritas no comentario acima da classe
            agent.getCommander().doDashBlocking(100.0d);
        } else {
            agent.getCommander().doTurnToPoint(ballPos);
        }

        return BTStatus.RUNNING;
    }
}
