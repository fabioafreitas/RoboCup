package ufrpe;

import easy_soccer_lib.perception.MatchPerception;
import ufrpe.actions.*;
import ufrpe.actions.conditions.IfBallIsInBigArea;
import ufrpe.actions.conditions.IfBallIsWithAllies;
import ufrpe.actions.conditions.IfBallIsWithOpponent;
import ufrpe.actions.conditions.IfClosestPlayerToBall;
import ufrpe.actions.game_states.IfStatusIsBeforeKickOff;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.Selector;
import ufrpe.behavior_tree.Sequence;
import easy_soccer_lib.PlayerCommander;
import easy_soccer_lib.perception.FieldPerception;
import easy_soccer_lib.perception.PlayerPerception;
import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.EMatchState;
import easy_soccer_lib.utils.Vector2D;


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
	private MatchPerception  matchPerc;



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
//				switch(matchPerc.getState()) {
//				case BEFORE_KICK_OFF:
//					new IfStatusIsBeforeKickOff().tick(this);
//					break;
//				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			/*switch(matchPerc.getState()){
			case BEFORE_KICK_OFF:
				selfPerc.setDirection(new Vector2D(-50.0d, 0.0d));
				break;
			case TIME_OVER:
				break;
			case PLAY_ON:
				break;
			case KICK_OFF_LEFT:
				selfPerc.setDirection(new Vector2D(-50.0d, 0.0d));
				break;
			case KICK_OFF_RIGHT:
				break;
			case KICK_IN_LEFT:
				break;
			case KICK_IN_RIGHT:
				break;
			case FREE_KICK_LEFT:
				break;
			case FREE_KICK_RIGHT:
				break;
			case CORNER_KICK_LEFT:
				break;
			case CORNER_KICK_RIGHT:
				break;
			case GOAL_KICK_LEFT:
				break;
			case GOAL_KICK_RIGHT:
				break;
			case AFTER_GOAL_LEFT:
				break;
			case AFTER_GOAL_RIGHT:
				break;
			case DROP_BALL:
				break;
			case OFFSIDE_LEFT:
				break;
			case OFFSIDE_RIGHT :
				break;
			case MAX:
				break;
			case BACK_PASS_LEFT: // Falta: recuo para o goleiro do time left
				break;
			case BACK_PASS_RIGHT: // Falta: recuo para o goleiro do time right
				break;
			case FREE_KICK_FAULT_LEFT:
				break;
			case FREE_KICK_FAULT_RIGHT:
				break;
			case INDIRECT_FREE_KICK_LEFT: // Cobran�a indireta para o time left (2 toques)
				break;
			case INDIRECT_FREE_KICK_RIGHT: // Cobran�a indireta para o time right (2 toques)
				break;
		}*/
			updatePerceptions(); //non-blocking
		}

		System.out.println(">> 4. Finalizado!");
	}

	private BTNode<BehaviorTreePlayer> buildTree_Goleiro() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> bolaNaArea = new Sequence<BehaviorTreePlayer>("Bola-Na-Area");
		bolaNaArea.add(new IfBallIsInBigArea());
		bolaNaArea.add(new GoGetBall());
		bolaNaArea.add(new PassBall());

		Sequence<BehaviorTreePlayer> bolaForaDaArea = new Sequence<BehaviorTreePlayer>("Bola-Fora-Da-Area");
		bolaForaDaArea.add(new MoveAccordingToBall());
		
		
//		raiz.add(beforeKickOff);
		BTNode<BehaviorTreePlayer> beforekickoff = new IfStatusIsBeforeKickOff();
		
		raiz.add(bolaNaArea);
		raiz.add(bolaForaDaArea);
		raiz.add(beforekickoff);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> buildTree_Meia() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> tocarBola = new Sequence<BehaviorTreePlayer>("Bola-Na-Area");
		tocarBola.add(new IfClosestPlayerToBall());
		tocarBola.add(new GoGetBall());
		tocarBola.add(new PassBall());

		Sequence<BehaviorTreePlayer> defaultTreeAdvance = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeAdvance.add(new IfBallIsWithAllies());
		defaultTreeAdvance.add(new AdvanceAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> defaultTreeRetreat = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeRetreat.add(new IfBallIsWithOpponent());
		defaultTreeRetreat.add(new RetreatAccordingToHomePosition());
		
		BTNode<BehaviorTreePlayer> beforekickoff = new IfStatusIsBeforeKickOff();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> buildTree_LateralDireito() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> tocarBola = new Sequence<BehaviorTreePlayer>("Bola-Na-Area");
		tocarBola.add(new IfClosestPlayerToBall());
		tocarBola.add(new GoGetBall());
		tocarBola.add(new PassBall());

		Sequence<BehaviorTreePlayer> defaultTreeAdvance = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeAdvance.add(new IfBallIsWithAllies());
		defaultTreeAdvance.add(new AdvanceAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> defaultTreeRetreat = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeRetreat.add(new IfBallIsWithOpponent());
		defaultTreeRetreat.add(new RetreatAccordingToHomePosition());
		
		BTNode<BehaviorTreePlayer> beforekickoff = new IfStatusIsBeforeKickOff();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
		return raiz;
}

	private BTNode<BehaviorTreePlayer> buildTree_ZagueiroDireito() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> tocarBola = new Sequence<BehaviorTreePlayer>("Bola-Na-Area");
		tocarBola.add(new IfClosestPlayerToBall());
		tocarBola.add(new GoGetBall());
		tocarBola.add(new PassBall());

		Sequence<BehaviorTreePlayer> defaultTreeAdvance = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeAdvance.add(new IfBallIsWithAllies());
		defaultTreeAdvance.add(new AdvanceAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> defaultTreeRetreat = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeRetreat.add(new IfBallIsWithOpponent());
		defaultTreeRetreat.add(new RetreatAccordingToHomePosition());
		
		BTNode<BehaviorTreePlayer> beforekickoff = new IfStatusIsBeforeKickOff();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
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

		BTNode<BehaviorTreePlayer> defaultTree = new ReturnToHomePosition();
		
		BTNode<BehaviorTreePlayer> beforekickoff = new IfStatusIsBeforeKickOff();
		
		raiz.add(attackTree);
		raiz.add(deffensiveTree);
		//raiz.add(defaultTree);
		raiz.add(beforekickoff);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> buildTree_LateralEsquerdo() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> tocarBola = new Sequence<BehaviorTreePlayer>("Bola-Na-Area");
		tocarBola.add(new IfClosestPlayerToBall());
		tocarBola.add(new GoGetBall());
		tocarBola.add(new PassBall());

		Sequence<BehaviorTreePlayer> defaultTreeAdvance = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeAdvance.add(new IfBallIsWithAllies());
		defaultTreeAdvance.add(new AdvanceAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> defaultTreeRetreat = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeRetreat.add(new IfBallIsWithOpponent());
		defaultTreeRetreat.add(new RetreatAccordingToHomePosition());
		
		BTNode<BehaviorTreePlayer> beforekickoff = new IfStatusIsBeforeKickOff();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> buildTree_ZagueiroEsquerdo() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> tocarBola = new Sequence<BehaviorTreePlayer>("Bola-Na-Area");
		tocarBola.add(new IfClosestPlayerToBall());
		tocarBola.add(new GoGetBall());
		tocarBola.add(new PassBall());

		Sequence<BehaviorTreePlayer> defaultTreeAdvance = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeAdvance.add(new IfBallIsWithAllies());
		defaultTreeAdvance.add(new AdvanceAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> defaultTreeRetreat = new Sequence<BehaviorTreePlayer>("Default-Tree");
		defaultTreeRetreat.add(new IfBallIsWithOpponent());
		defaultTreeRetreat.add(new RetreatAccordingToHomePosition());
		
		BTNode<BehaviorTreePlayer> beforekickoff = new IfStatusIsBeforeKickOff();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
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

		BTNode<BehaviorTreePlayer> defaultTree = new ReturnToHomePosition();
		
		BTNode<BehaviorTreePlayer> beforekickoff = new IfStatusIsBeforeKickOff();
		
		raiz.add(attackTree);
		raiz.add(deffensiveTree);
		//raiz.add(defaultTree);
		raiz.add(beforekickoff);
		return raiz;
	}

	private void updatePerceptionsBlocking() {
		PlayerPerception newSelf = commander.perceiveSelfBlocking();
		FieldPerception newField = commander.perceiveFieldBlocking();
		MatchPerception newMatch = commander.perceiveMatchBlocking();
		if (newSelf != null) {
			this.selfPerc = newSelf;
		}
		if (newField != null) {
			this.fieldPerc = newField;
		}
		if (newMatch != null) {
			this.matchPerc = newMatch;
		}
	}

	private void updatePerceptions() {
		PlayerPerception newSelf = commander.perceiveSelf();
		FieldPerception newField = commander.perceiveField();
		MatchPerception newMatch = commander.perceiveMatch();

		if (newSelf != null) {
			this.selfPerc = newSelf;
		}
		if (newField != null) {
			this.fieldPerc = newField;
		}
		if (newMatch != null) {
			this.matchPerc = newMatch;
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

	public FieldPerception getFieldPerc() {
		return fieldPerc;
	}

	public MatchPerception getMatchPerc() {
		return matchPerc;
	}

	public Vector2D getHomePosition() {
		return homePosition;
	}

	public Vector2D getGoalPosition() {
		return goalPosition;
	}

	public BTNode<BehaviorTreePlayer> getBtree() {
		return btree;
	}
}
