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
import ufrpe.actions.MoveAccordingToBall;
import ufrpe.actions.ReturnToHome;


public class BehaviorTreePlayer extends Thread {
	private final int GOLEIRO = 1;
	private final int MEIA = 2; 
	private final int ZAGUEIRO_DIREITO = 3;
	private final int ZAGUEIRO_ESQUERDO = 4;
	private final int LATERAL_DIREITO = 5;
	private final int LATERAL_ESQUERDO = 6;
	private final int ATACANTE_DIREITO = 7;
	private final int ATACANTE_ESQUERDO = 8;

	private final PlayerCommander commander;
	private PlayerPerception selfPerc;
	private FieldPerception  fieldPerc;

	private Vector2D homePosition;
	private Vector2D goalPosition;
	
	private BTNode<BehaviorTreePlayer> btree;
	
	
	public BehaviorTreePlayer(PlayerCommander player, Vector2D home) {
		commander = player;
		homePosition = home;
	}

	@Override
	public void run() {
		System.out.println(">> 1. Esperando percepcoes iniciais...");
		updatePerceptionsBlocking();

		System.out.println(">> 2. Movendo jogadores para posicao inicial...");
		commander.doMoveBlocking(homePosition.getX(), homePosition.getY());
		updatePerceptionsBlocking();

		if (selfPerc.getSide() == EFieldSide.LEFT) {
			goalPosition = new Vector2D(52.0d, 0);
		} else {
			goalPosition = new Vector2D(-52.0d, 0);
			homePosition.setX(- homePosition.getX()); //inverte, porque somente no move as coordenadas sao espelhadas independente de lado
			homePosition.setY(- homePosition.getY());
		}

		System.out.println(">> 3. Iniciando...");
		switch (selfPerc.getUniformNumber()) {
			case MEIA:
				btree = buildTree_Meia();
				break;
			case GOLEIRO:
				btree = buildTree_Goleiro();
				break;
			case ZAGUEIRO_DIREITO:
				btree = buildTree_ZagueiroDireito();
				break;
			case ZAGUEIRO_ESQUERDO:
				btree = buildTree_ZagueiroEsquerdo();
				break;
			case LATERAL_DIREITO:
				btree = buildTree_LateralDireito();
				break;
			case LATERAL_ESQUERDO:
				btree = buildTree_LateralEsquerdo();
				break;
			case ATACANTE_DIREITO:
				btree = buildTree_AtacanteDireito();
				break;
			case ATACANTE_ESQUERDO:
				btree = buildTree_AtacanteEsquerdo();
				break;
			default: break;
		}

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

	

	private BTNode<BehaviorTreePlayer> buildTree_Goleiro() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> attackTree = new Sequence<BehaviorTreePlayer>("Avanca-para-Gol");
		attackTree.add(new IfClosestPlayerToBall());
		attackTree.add(new AdvanceWithBallToGoal());
		attackTree.add(new KickToScore());

		Sequence<BehaviorTreePlayer> deffensiveTree = new Sequence<BehaviorTreePlayer>("Rouba-Bola");
		deffensiveTree.add(new IfClosestPlayerToBall());
		deffensiveTree.add(new GoGetBall());
		

//		Sequence<BehaviorTreePlayer> defaultTree = new Sequence<BehaviorTreePlayer>("Padrao");
//		defaultTree.add(new IfClosestPlayerToBall());
//		defaultTree.add(new GoGetBall());
		BTNode<BehaviorTreePlayer> defaultTree = new MoveAccordingToBall(); //TODO fica como EXERCICIO
		raiz.add(attackTree);
		raiz.add(deffensiveTree);
		raiz.add(defaultTree); //TODO

		return raiz;
	}

	private BTNode<BehaviorTreePlayer> buildTree_Meia() {
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

	private BTNode<BehaviorTreePlayer> buildTree_LateralDireito() {
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

	private BTNode<BehaviorTreePlayer> buildTree_ZagueiroDireito() {
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

	private BTNode<BehaviorTreePlayer> buildTree_AtacanteDireito() {
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

	private BTNode<BehaviorTreePlayer> buildTree_LateralEsquerdo() {
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

	private BTNode<BehaviorTreePlayer> buildTree_ZagueiroEsquerdo() {
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

	private BTNode<BehaviorTreePlayer> buildTree_AtacanteEsquerdo() {
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

	private void updatePerceptionsBlocking() {
		PlayerPerception newSelf = commander.perceiveSelfBlocking();
		FieldPerception newField = commander.perceiveFieldBlocking();

		if (newSelf != null) {
			this.selfPerc = newSelf;
		}
		if (newField != null) {
			this.fieldPerc = newField;
		}
	}

	private void updatePerceptions() {
		PlayerPerception newSelf = commander.perceiveSelf();
		FieldPerception newField = commander.perceiveField();

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
