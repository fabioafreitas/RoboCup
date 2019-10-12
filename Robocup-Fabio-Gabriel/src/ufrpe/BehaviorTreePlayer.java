package ufrpe;

import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.Selector;
import ufrpe.behavior_tree.Sequence;
import easy_soccer_lib.PlayerCommander;
import easy_soccer_lib.perception.FieldPerception;
import easy_soccer_lib.perception.PlayerPerception;
import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.Vector2D;
import ufrpe.actions.AdvanceWithBallToGoal;
import ufrpe.actions.GoGetBall;
import ufrpe.actions.IfClosestPlayerToBall;
import ufrpe.actions.KickToScore;


public class BehaviorTreePlayer extends Thread {
	private final int GOLEIRO = 0;
	private final int MEIA = 1;
	private final int ZAGUEIRO_DIREITO = 2;
	private final int ZAGUEIRO_ESQUERDO = 3;
	private final int LATERAL_DIREITO = 4;
	private final int LATERAL_ESQUERDO = 5;
	private final int ATACANTE_DIREITO = 6;
	private final int ATACANTE_ESQUERDO = 7;

	private final PlayerCommander commander;
	private PlayerPerception selfPerc;
	private FieldPerception  fieldPerc;

	private Vector2D homePosition;
	private Vector2D goalPosition;
	
	private BTNode<BehaviorTreePlayer> btree;
	
	
	public BehaviorTreePlayer(PlayerCommander player, Vector2D home) {
		commander = player;
		homePosition = home;
		btree = buildTree();
	}

	private BTNode<BehaviorTreePlayer> buildTree() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");
		
		Sequence<BehaviorTreePlayer> attackTree = new Sequence<BehaviorTreePlayer>("Avanca-para-Gol");
		attackTree.add(new IfClosestPlayerToBall());
		attackTree.add(new AdvanceWithBallToGoal());
		attackTree.add(new KickToScore());

		Sequence<BehaviorTreePlayer> deffensiveTree = new Sequence<BehaviorTreePlayer>("Rouba-Bola");
		deffensiveTree.add(new IfClosestPlayerToBall());
		deffensiveTree.add(new GoGetBall());
		
		//BTNode<BTPlayer> defaultTree = new ReturnToHome(); //TODO fica como EXERCICIO
		
		raiz.add(attackTree);
		raiz.add(deffensiveTree);
		//raiz.add(defaultTree); //TODO
		
		return raiz;
	}



	@Override
	public void run() {
		System.out.println(">> 1. Esperando percepcoes iniciais...");
		updatePerceptions();

		System.out.println(">> 2. Movendo jogadores para posicao inicial...");
		commander.doMoveBlocking(homePosition.getX(), homePosition.getY());
		updatePerceptions();

		if (selfPerc.getSide() == EFieldSide.LEFT) {
			goalPosition = new Vector2D(52.0d, 0);
		} else {
			goalPosition = new Vector2D(-52.0d, 0);
			homePosition.setX(- homePosition.getX()); //inverte, porque somente no move as coordenadas sao espelhadas independente de lado
			homePosition.setY(- homePosition.getY());
		}

		System.out.println(">> 3. Iniciando...");

		//TODO alterar
		switch (selfPerc.getUniformNumber()) {
			case MEIA:
				acaoMeia(); break;
			case GOLEIRO:
				acaoGoleiro(); break;
			case ZAGUEIRO_DIREITO:
				acaoZagueiroDireito(); break;
			case ZAGUEIRO_ESQUERDO:
				acaoZagueiroEsquerdo(); break;
			case LATERAL_DIREITO:
				acaoLateralDireito(); break;
			case LATERAL_ESQUERDO:
				acaoLateralEsquerdo(); break;
			case ATACANTE_DIREITO:
				acaoAtacanteDireito(); break;
			case ATACANTE_ESQUERDO:
				acaoAtacanteEsquerdo(); break;
			default: break;
		}

		//TODO mover este loop para dentro de cada ação dos jogadores
		while (commander.isActive()) {

			btree.tick(this);

			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			updatePerceptions(); //non-blocking
		}

		System.out.println(">> 4. Finalizado!");
	}

	private void updatePerceptions() {
		PlayerPerception newSelf = commander.perceiveSelfBlocking();
		FieldPerception newField = commander.perceiveFieldBlocking();

		if (newSelf != null) {
			this.selfPerc = newSelf;
		}
		if (newField != null) {
			this.fieldPerc = newField;
		}
	}
	
	/** Algumas funcoes auxiliares que mais de um tipo de no da arvore pode precisar **/

	public boolean closeTo(Vector2D pos) {
		return isCloseTo(pos, 1.5);
	}

	public boolean isCloseTo(Vector2D pos, double minDistance) {
		Vector2D myPos = selfPerc.getPosition();
		return pos.distanceTo(myPos) < minDistance;
	}

	public boolean isAlignedTo(Vector2D position) {
		return isAlignedTo(position, 12.0);
	}

	public boolean isAlignedTo(Vector2D position, double minAngle) {
		if (minAngle < 0) minAngle = -minAngle;
		
		Vector2D myPos = selfPerc.getPosition();
		
		if (position == null || myPos == null) {
			return false;			
		}
		
		double angle = selfPerc.getDirection().angleFrom(position.sub(myPos));
		return angle < minAngle && angle > -minAngle;
	}

	void acaoGoleiro() {
		//TODO colocar este metodo dentro de uma classe, como metodo estatico
	}

	void acaoMeia() {
		//TODO colocar este metodo dentro de uma classe, como metodo estatico

	}

	void acaoLateralDireito() {
		//TODO colocar este metodo dentro de uma classe, como metodo estatico

	}

	void acaoZagueiroDireito() {
		//TODO colocar este metodo dentro de uma classe, como metodo estatico

	}

	void acaoAtacanteDireito() {
		//TODO colocar este metodo dentro de uma classe, como metodo estatico

	}

	void acaoLateralEsquerdo() {
		//TODO colocar este metodo dentro de uma classe, como metodo estatico

	}

	void acaoZagueiroEsquerdo() {
		//TODO colocar este metodo dentro de uma classe, como metodo estatico

	}

	void acaoAtacanteEsquerdo() {
		//TODO colocar este metodo dentro de uma classe, como metodo estatico

	}

	public PlayerCommander getCommander() {
		return commander;
	}

	public PlayerPerception getSelfPerc() {
		return selfPerc;
	}

	public void setSelfPerc(PlayerPerception selfPerc) {
		this.selfPerc = selfPerc;
	}

	public FieldPerception getFieldPerc() {
		return fieldPerc;
	}

	public void setFieldPerc(FieldPerception fieldPerc) {
		this.fieldPerc = fieldPerc;
	}

	public Vector2D getHomePosition() {
		return homePosition;
	}

	public void setHomePosition(Vector2D homePosition) {
		this.homePosition = homePosition;
	}

	public Vector2D getGoalPosition() {
		return goalPosition;
	}

	public void setGoalPosition(Vector2D goalPosition) {
		this.goalPosition = goalPosition;
	}

	public BTNode<BehaviorTreePlayer> getBtree() {
		return btree;
	}

	public void setBtree(BTNode<BehaviorTreePlayer> btree) {
		this.btree = btree;
	}
}
