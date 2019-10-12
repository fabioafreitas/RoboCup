package ufrpe.players;

import ufrpe.BehaviorTreePlayer;
import ufrpe.behavior_tree.BTNode;

//TODO
public interface Comportamento {
    public BTNode<BehaviorTreePlayer> buildTree();
}
