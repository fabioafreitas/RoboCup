package ufrpe.actions.conditions;

import java.util.List;

import easy_soccer_lib.perception.FieldPerception;
import easy_soccer_lib.perception.PlayerPerception;
import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

import java.awt.*;

//TODO falta testar
public class IfPlayerIsSurrounded extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        Vector2D currentPos = agent.getSelfPerc().getPosition();
        Rectangle area = new Rectangle( (int) currentPos.getX()-5,
                                        (int) currentPos.getY()-5,
                                        10, 10);
        FieldPerception fieldPerc = agent.getFieldPerc();
        EFieldSide ladoOponente = (agent.getSelfPerc().getSide() == EFieldSide.RIGHT) ? EFieldSide.LEFT : EFieldSide.RIGHT;
        List<PlayerPerception> oponentes = fieldPerc.getTeamPlayers(ladoOponente);
        int numeroJogadoresProximos = 0;
        for (PlayerPerception player : oponentes ) {
            //Se há mais de 2 jogadores proximos do atual, então ele esta marcado
            if(numeroJogadoresProximos > 2) {
                return BTStatus.SUCCESS;
            }
            //Checa se o jogador opoente atual esta dentro do retangulo
            if(area.contains(player.getPosition().getX(), player.getPosition().getY())) {
                numeroJogadoresProximos++;
            }
        }
        return BTStatus.FAILURE;
    }
}
