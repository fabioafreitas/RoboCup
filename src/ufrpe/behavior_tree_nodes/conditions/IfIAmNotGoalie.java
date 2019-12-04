package ufrpe.behavior_tree_nodes.conditions;

import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.BTStatus;

//Retorna sucesso se o jogador nao for o goleiro
public class IfIAmNotGoalie extends BTNode<BehaviorTreePlayer> {
    @Override
    public BTStatus tick(BehaviorTreePlayer agent) {
        return agent.getSelfPerc().getUniformNumber() != 1 ? BTStatus.SUCCESS: BTStatus.FAILURE;
    }
}
