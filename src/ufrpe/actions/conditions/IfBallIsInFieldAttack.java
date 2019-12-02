package ufrpe.actions.conditions;

import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

import java.awt.*;

/**
 * Considerei o campo subdividido em 3 porções. A parte mais proxima do goleiro é
 * Campo de Defesa (FieldDefense) a porção do meio campo é o Campo do Meio (FieldMiddle)
 * e a porção que o time faz o gol é o Campo de Ataque (FieldAttack)
 *
 * Esta classe retorna sucesso caso o jogador atua esteja no FieldAttack
 */
//TODO falta testar
public class IfBallIsInFieldAttack extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        EFieldSide side = agent.getSelfPerc().getSide();
        Rectangle fieldAttack = (side == EFieldSide.RIGHT) ?
                new Rectangle(16,-34, 34, 68) :
                new Rectangle(-52,-34, 34, 68);
        Vector2D ballPos = agent.getFieldPerc().getBall().getPosition();
        if(fieldAttack.contains(ballPos.getX(), ballPos.getY())) {
            return BTStatus.SUCCESS;
        }
        return BTStatus.FAILURE;
    }
}
