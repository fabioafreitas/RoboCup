package ufrpe;

import easy_soccer_lib.perception.MatchPerception;
import ufrpe.actions.*;
import ufrpe.actions.conditions.IfBallIsInBigArea;
import ufrpe.actions.conditions.IfBallIsWithAllies;
import ufrpe.actions.conditions.IfBallIsWithOpponent;
import ufrpe.actions.conditions.IfClosestPlayerToBall;
import ufrpe.actions.game_states.*;
import ufrpe.behavior_tree.BTNode;
import ufrpe.behavior_tree.Selector;
import ufrpe.behavior_tree.Sequence;
import easy_soccer_lib.PlayerCommander;
import easy_soccer_lib.perception.FieldPerception;
import easy_soccer_lib.perception.PlayerPerception;
import easy_soccer_lib.utils.EFieldSide;
import easy_soccer_lib.utils.Vector2D;

public class BehaviorTreePlayer extends Thread {
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
			case 1: //GOLEIRO
				btree = buildTree(Goleiro());
				break;
			case 2: //MEIA
				btree = buildTree(Meia());
				break;
			case 3: //ZAGUEIRO DIREITO
				btree = buildTree(ZagueiroDireito());
				break;
			case 4: //ZAGUEIRO ESQUERDO
				btree = buildTree(ZagueiroEsquerdo());
				break;
			case 5: //LATERAL DIREITO
				btree = buildTree(LateralDireito());
				break;
			case 6: //LATERAL ESQUERDO
				btree = buildTree(LateralEsquerdo());
				break;
			case 7: //ATACANTE DIREITO
				btree = buildTree(AtacanteDireito());
				break;
			case 8: //ATACANTE ESQUERDO
				btree = buildTree(AtacanteEsquerdo());
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

	private BTNode<BehaviorTreePlayer> buildTree(BTNode<BehaviorTreePlayer> playerBehaviorTree) {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ-STATES");

		/*Adicao dos comportamentos do jogador*/
		Sequence<BehaviorTreePlayer> playOn = new Sequence<BehaviorTreePlayer>("PLAY ON");
		playOn.add(new PlayOn());
		playOn.add(playerBehaviorTree);  //adicionar behavior tree padrao dos jogadores

		/*tratamento dos ESTADOS do jogo*/
		raiz.add(playOn);
		raiz.add(new BeforeKickOff_AfterGoal());
		raiz.add(new KickOff());
		raiz.add(new CornerKick());
		raiz.add(new FreeKick());
		raiz.add(new FreeKickFault());
		raiz.add(new GoalKick());
		raiz.add(new IndirectFreeKick());
		raiz.add(new KickIn());
		raiz.add(new KickOff());
		raiz.add(new Offside());
		raiz.add(new TimeOver());

		return raiz;
	}

	private BTNode<BehaviorTreePlayer> Goleiro() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> bolaNaArea = new Sequence<BehaviorTreePlayer>("Bola-Na-Area");
		bolaNaArea.add(new IfBallIsInBigArea());
		bolaNaArea.add(new GoGetBall());
		bolaNaArea.add(new PassBall());

		Sequence<BehaviorTreePlayer> bolaForaDaArea = new Sequence<BehaviorTreePlayer>("Bola-Fora-Da-Area");
		bolaForaDaArea.add(new MoveAccordingToBall());
		
		
//		raiz.add(beforeKickOff);
		BTNode<BehaviorTreePlayer> beforekickoff = new BeforeKickOff_AfterGoal();
		
		raiz.add(bolaNaArea);
		raiz.add(bolaForaDaArea);
		raiz.add(beforekickoff);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> Meia() {
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
		
		BTNode<BehaviorTreePlayer> beforekickoff = new BeforeKickOff_AfterGoal();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> LateralDireito() {
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
		
		BTNode<BehaviorTreePlayer> beforekickoff = new BeforeKickOff_AfterGoal();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
		return raiz;
}

	private BTNode<BehaviorTreePlayer> ZagueiroDireito() {
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
		
		BTNode<BehaviorTreePlayer> beforekickoff = new BeforeKickOff_AfterGoal();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
		return raiz;
	}


	private BTNode<BehaviorTreePlayer> AtacanteDireito() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> attackTree = new Sequence<BehaviorTreePlayer>("Avanca-para-Gol");
		attackTree.add(new IfClosestPlayerToBall());
		attackTree.add(new AdvanceWithBallToGoal());
		attackTree.add(new KickToScore());

		Sequence<BehaviorTreePlayer> deffensiveTree = new Sequence<BehaviorTreePlayer>("Rouba-Bola");
		deffensiveTree.add(new IfClosestPlayerToBall());
		deffensiveTree.add(new GoGetBall());

		BTNode<BehaviorTreePlayer> defaultTree = new ReturnToHomePosition();
		
		BTNode<BehaviorTreePlayer> beforekickoff = new BeforeKickOff_AfterGoal();
		
		raiz.add(attackTree);
		raiz.add(deffensiveTree);
		//raiz.add(defaultTree);
		raiz.add(beforekickoff);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> LateralEsquerdo() {
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
		
		BTNode<BehaviorTreePlayer> beforekickoff = new BeforeKickOff_AfterGoal();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> ZagueiroEsquerdo() {
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
		
		BTNode<BehaviorTreePlayer> beforekickoff = new BeforeKickOff_AfterGoal();
		
		raiz.add(tocarBola);
		raiz.add(defaultTreeAdvance);
		//raiz.add(defaultTreeRetreat);
		raiz.add(beforekickoff);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> AtacanteEsquerdo() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> attackTree = new Sequence<BehaviorTreePlayer>("Avanca-para-Gol");
		attackTree.add(new IfClosestPlayerToBall());
		attackTree.add(new AdvanceWithBallToGoal());
		attackTree.add(new KickToScore());

		Sequence<BehaviorTreePlayer> deffensiveTree = new Sequence<BehaviorTreePlayer>("Rouba-Bola");
		deffensiveTree.add(new IfClosestPlayerToBall());
		deffensiveTree.add(new GoGetBall());

		BTNode<BehaviorTreePlayer> defaultTree = new ReturnToHomePosition();
		
		BTNode<BehaviorTreePlayer> beforekickoff = new BeforeKickOff_AfterGoal();
		
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
