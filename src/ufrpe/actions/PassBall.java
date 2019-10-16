package ufrpe.actions;

import easy_soccer_lib.perception.PlayerPerception;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

import java.util.Comparator;
import java.util.List;

/**
 * tenta passar para o jogador mais proximo e menos marcado
 */
public class PassBall extends BTNode<BehaviorTreePlayer> {
//ordeno jogadores aliados de acordo com a distancia
    //checo qual o com menos marcacao e chuto para ele
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        Vector2D ballPosition = agent.getFieldPerc().getBall().getPosition();
        List<PlayerPerception> myTeam = agent.getFieldPerc().getTeamPlayers(agent.getSelfPerc().getSide());

        //removendo o this.agent da lista
        PlayerPerception thisPlayer = null;
        for(int i = 0 ; i < myTeam.size() ; i++) {
            if(myTeam.get(i).getUniformNumber() == agent.getSelfPerc().getUniformNumber()) {
                thisPlayer = myTeam.get(i);
                myTeam.remove(myTeam.get(i));
            }
        }

        //ordenando jogadores pela distancia com o jogador atual
        /*TODO depois fzer um mecanismo de detectar o
           quanto o jogador esta marcado, para evitar perder a bola*/
//        myTeam.sort(new Comparator<PlayerPerception>() {
//            @Override
//            public int compare(PlayerPerception o1, PlayerPerception o2) {
//                double distanceA = o1.getPosition().distanceTo(agent.getSelfPerc().getPosition());
//                double distanceB = o2.getPosition().distanceTo(agent.getSelfPerc().getPosition());
//                return (int) (distanceA - distanceB);
//            }
//        });

        PlayerPerception closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        //buscando jogador mais proximo
        for (PlayerPerception player : myTeam) {
            double playerDistance = player.getPosition().distanceTo(ballPosition);
            if (playerDistance < closestDistance) {
                closestDistance = playerDistance;
                closestPlayer = player;
            }
        }

        agent.getCommander().doKickToDirection(65, closestPlayer.getPosition());

        return BTStatus.RUNNING;
    }
}
