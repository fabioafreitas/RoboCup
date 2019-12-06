package ufrpe;

import easy_soccer_lib.perception.MatchPerception;
import ufrpe.behavior_tree_nodes.actions.*;
import ufrpe.behavior_tree_nodes.conditions.*;
import ufrpe.behavior_tree_nodes.game_states.*;
import ufrpe.behavior_tree_nodes.game_states_movements.MovePlayerToHomePosition;
import ufrpe.behavior_tree_nodes.game_states_movements.MovePlayerToKickOff;
import ufrpe.behavior_tree_nodes.game_states_movements.MovePlayerToSmallArea;
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


		raiz.add(beforeKickOff_afterGoal());	//Completo
		raiz.add(freeKick());					//TODO ainda nao funciona mt bem
		raiz.add(kickOff());					//Completo
		raiz.add(offside()); 					//Completo
		raiz.add(cornerKick());					//TODO  escanteio (CornerKick)
		raiz.add(kickIn());						//TODO 	lateral (KickIn)
		//TODO 	GoalKick
		//TODO  IndirectFreeKick
		//TODO FreeKickFault
		raiz.add(playerBehaviorTree);

		return raiz;
	}

	private BTNode<BehaviorTreePlayer> playOn(BTNode<BehaviorTreePlayer> playerBehaviorTree) {
		Sequence<BehaviorTreePlayer> playOn = new Sequence<BehaviorTreePlayer>("PLAY ON");
		playOn.add(new PlayOn());
		playOn.add(playerBehaviorTree);  //adicionar behavior tree padrao dos jogadores
		return playOn;
	}

	private BTNode<BehaviorTreePlayer> beforeKickOff_afterGoal() {
		Selector<BehaviorTreePlayer> root = new Selector<BehaviorTreePlayer>("BEFORE KICK OFF / AFTER GOAL");
		Sequence<BehaviorTreePlayer> beforeKickOff = new Sequence<BehaviorTreePlayer>("BEFORE KICK OFF");
		Sequence<BehaviorTreePlayer> afterGoalLeft = new Sequence<BehaviorTreePlayer>("AFTER GOAL LEFT");
		Sequence<BehaviorTreePlayer> afterGoalRight = new Sequence<BehaviorTreePlayer>("AFTER GOAL RIGHT");

		root.add(beforeKickOff);
		root.add(afterGoalLeft);
		root.add(afterGoalRight);

		beforeKickOff.add(new BeforeKickOff());
		beforeKickOff.add(new MovePlayerToHomePosition());

		afterGoalLeft.add(new AfterGoalLeft());
		afterGoalLeft.add(new MovePlayerToHomePosition());

		afterGoalRight.add(new AfterGoalRight());
		afterGoalRight.add(new MovePlayerToHomePosition());

		return root;
	}

	private BTNode<BehaviorTreePlayer> freeKick() {
		Selector<BehaviorTreePlayer> root = new Selector<BehaviorTreePlayer>("FREE KICK");
		Sequence<BehaviorTreePlayer> freeKickLeft = new Sequence<BehaviorTreePlayer>("FREE KICK LEFT?");
		Sequence<BehaviorTreePlayer> freeKickRight = new Sequence<BehaviorTreePlayer>("FREE KICK RIGHT?");

		root.add(freeKickLeft);
		root.add(freeKickLeft);

		freeKickLeft.add(new FreeKickLeft());
		freeKickLeft.add(new GoGetBall());
		freeKickLeft.add(new KickToScore());

		freeKickRight.add(new FreeKickRight());
		freeKickRight.add(new GoGetBall());
		freeKickRight.add(new KickToScore());

		return root;
	}

	private BTNode<BehaviorTreePlayer> kickOff() {
		Selector<BehaviorTreePlayer> root = new Selector<BehaviorTreePlayer>("BEFORE KICK OFF / AFTER GOAL");
		Sequence<BehaviorTreePlayer> kickOffLeft = new Sequence<BehaviorTreePlayer>("KICK OFF LEFT");
		Sequence<BehaviorTreePlayer> kickOffRight = new Sequence<BehaviorTreePlayer>("KICK OFF RIGHT");

		root.add(kickOffLeft);
		root.add(kickOffRight);

		kickOffLeft.add(new KickOffLeft());
		kickOffLeft.add(new MovePlayerToKickOff());
		kickOffLeft.add(new IfClosestPlayerToBall());
		kickOffLeft.add(new GoGetBall());
		kickOffLeft.add(new PassBall());

		kickOffRight.add(new KickOffRight());
		kickOffRight.add((new MovePlayerToKickOff()));
		kickOffRight.add(new IfClosestPlayerToBall());
		kickOffRight.add(new GoGetBall());
		kickOffRight.add(new PassBall());

		return root;
	}
	//Impedimento
	private BTNode<BehaviorTreePlayer> offside() {
		Selector<BehaviorTreePlayer> offside = new Selector<BehaviorTreePlayer>("CHECANDO IMPEDIMENTO");
		Sequence<BehaviorTreePlayer> offsideLeft = new Sequence<BehaviorTreePlayer>("CHECANDO IMPEDIMENTO ESQUERDA");
		Sequence<BehaviorTreePlayer> offsideRight = new Sequence<BehaviorTreePlayer>("CHECANDO IMPEDIMENTO DIREITA");

		offside.add(offsideLeft);
		offside.add(offsideRight);

		//Se Esta impedido, recue
		offsideLeft.add(new OffsideLeft());
		offsideLeft.add(new IfLeftSide());
		offsideLeft.add(new IfIAmNotGoalie());
		offsideLeft.add(new RetreatAccordingToHomePosition());
		//Senao passe a bola
		offsideLeft.add(new IfClosestPlayerToBall());
		offsideLeft.add(new PassBall());

		//Se Esta impedido, recue
		offsideRight.add(new OffsideRight());
		offsideRight.add(new IfRightSide());
		offsideRight.add(new IfIAmNotGoalie());
		offsideRight.add(new RetreatAccordingToHomePosition());
		//Senao passe a bola
		offsideRight.add(new IfClosestPlayerToBall());
		offsideRight.add(new PassBall());

		return offside;
	}
	
	private BTNode<BehaviorTreePlayer> cornerKick(){ //TODO
		Selector<BehaviorTreePlayer> cornerKick = new Selector<BehaviorTreePlayer>("Escanteio");
		Sequence<BehaviorTreePlayer> cornerKickLeft = new Sequence<BehaviorTreePlayer>("Escanteio esquerda");
		Sequence<BehaviorTreePlayer> cornerKickRight = new Sequence<BehaviorTreePlayer>("Escanteio direita");
		cornerKick.add(cornerKickLeft);
		cornerKick.add(cornerKickRight);
		
		cornerKickLeft.add(new CornerKickLeft()); //52x,34y -34y 
		cornerKickLeft.add(new IfClosestPlayerToBall());
		cornerKickLeft.add(new MovePlayerToSmallArea());
		cornerKickLeft.add(new PassBall());
		
		cornerKickRight.add(new CornerKickRight()); //-52x,34y -34y 
		cornerKickRight.add(new IfClosestPlayerToBall());
		cornerKickRight.add(new MovePlayerToSmallArea());
		cornerKickRight.add(new PassBall());
		return cornerKick;
	}
	
	private BTNode<BehaviorTreePlayer> kickIn(){ //TODO
		Selector<BehaviorTreePlayer> kickIn = new Selector<BehaviorTreePlayer>("Lateral");
		Sequence<BehaviorTreePlayer> kickInLeft = new Sequence<BehaviorTreePlayer>("Lateral esquerda"); //esquerda cobrar
		Sequence<BehaviorTreePlayer> kickInRight = new Sequence<BehaviorTreePlayer>("Lateral direita"); //direita cobrar
		kickIn.add(kickInLeft);
		kickIn.add(kickInRight);
		
		kickInLeft.add(new KickInLeft()); //52x,34y -34y 
		kickInLeft.add(new PassBall());
		//kickInLeft.add(new MovePlayerToSmallArea());
		
		kickInRight.add(new KickInRight()); //-52x,34y -34y 
		kickInRight.add(new PassBall());
		//kickInRight.add(new MovePlayerToSmallArea());
		
		return kickIn;
	}
	
	private BTNode<BehaviorTreePlayer> Goleiro() { //Completo
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("GOLEIRO");

		Sequence<BehaviorTreePlayer> bolaNaPequenaArea = new Sequence<BehaviorTreePlayer>("BOLA ESTA NA PEQUENA AREA?");
		bolaNaPequenaArea.add(new IfBallIsInSmallArea());
		bolaNaPequenaArea.add(new CatchBall());
		bolaNaPequenaArea.add(new KickToScore());

		Sequence<BehaviorTreePlayer> bolaNaGrandeArea = new Sequence<BehaviorTreePlayer>("BOLA ESTA NA GRANDE AREA?");
		bolaNaGrandeArea.add(new IfBallIsInBigArea());
		bolaNaGrandeArea.add(new GoGetBall());
		bolaNaGrandeArea.add(new CatchBall());
		bolaNaGrandeArea.add(new KickToScore());

		raiz.add(bolaNaPequenaArea);
		raiz.add(bolaNaGrandeArea);
		raiz.add(new MoveAccordingToBall());
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> Meia() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");


		Sequence<BehaviorTreePlayer> ataque = new Sequence<BehaviorTreePlayer>(" ");
		//ataque.add(new IfClosestPlayerToBall());
		//ataque.add(new IfPlayerBelongsToTrioCloserToBall());
		ataque.add(new AdvanceWithBallToGoal());
		ataque.add(new KickToScore());

		Sequence<BehaviorTreePlayer> defender = new Sequence<BehaviorTreePlayer>(" ");
		defender.add(new IfClosestPlayerToBall());
		defender.add(new GoGetBall());


		raiz.add(ataque);
		raiz.add(defender);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> LateralDireito() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

//		Selector<BehaviorTreePlayer> regiaoDefesa = new Selector<BehaviorTreePlayer>("REGIAO DE DEFESA");
//
//		Sequence<BehaviorTreePlayer> a = new Sequence<BehaviorTreePlayer>(" ");
//		a.add(new IfPlayerIsInFieldDefense());
//		a.add(new IfBallIsInFieldDefense());
//		a.add(new GoGetBall());
//		a.add(new IfClosestPlayerToBall());
//		a.add(new PassBall());
//
//		Sequence<BehaviorTreePlayer> b = new Sequence<BehaviorTreePlayer>(" ");
//		b.add(new IfPlayerIsInFieldDefense());
//		b.add(new IfBallIsInFieldMiddle());
//		b.add(new AdvanceAccordingToHomePosition());
//
//		Sequence<BehaviorTreePlayer> c = new Sequence<BehaviorTreePlayer>(" ");
//		c.add(new IfPlayerIsInFieldDefense());
//		c.add(new IfBallIsInFieldAttack());
//		c.add(new ReturnToHomePosition());
//
//		regiaoDefesa.add(a);
//		regiaoDefesa.add(b);
//		regiaoDefesa.add(c);



		Selector<BehaviorTreePlayer> regiaoMeio = new Selector<BehaviorTreePlayer>("REGIAO DO MEIO");

		Sequence<BehaviorTreePlayer> d = new Sequence<BehaviorTreePlayer>(" ");
		d.add(new IfPlayerIsInFieldMiddle());
		d.add(new IfBallIsInFieldMiddle());
		d.add(new GoGetBall());
		d.add(new IfClosestPlayerToBall());
		d.add(new PassBall());

		Sequence<BehaviorTreePlayer> e = new Sequence<BehaviorTreePlayer>(" ");
		e.add(new IfPlayerIsInFieldMiddle());
		e.add(new IfBallIsInFieldDefense());
		e.add(new RetreatAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> f = new Sequence<BehaviorTreePlayer>(" ");
		f.add(new IfPlayerIsInFieldMiddle());
		f.add(new IfBallIsInFieldAttack());
		f.add(new RetreatAccordingToHomePosition());

		regiaoMeio.add(d);
		regiaoMeio.add(e);
		regiaoMeio.add(f);


		Selector<BehaviorTreePlayer> regiaoAtaque = new Selector<BehaviorTreePlayer>("REGIAO DO MEIO");

		Sequence<BehaviorTreePlayer> g = new Sequence<BehaviorTreePlayer>(" ");
		g.add(new IfPlayerIsInFieldAttack());
		g.add(new IfBallIsInFieldAttack());
		g.add(new IfClosestPlayerToBall());
		g.add(new KickToScore());

		Sequence<BehaviorTreePlayer> h = new Sequence<BehaviorTreePlayer>(" ");
		h.add(new IfPlayerIsInFieldAttack());
		h.add(new IfBallIsInFieldAttack());
		h.add(new GoGetBall());
		h.add(new PassBall());

		Sequence<BehaviorTreePlayer> i = new Sequence<BehaviorTreePlayer>(" ");
		i.add(new IfPlayerIsInFieldAttack());
		i.add(new IfBallIsInFieldMiddle());
		i.add(new RetreatAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> j = new Sequence<BehaviorTreePlayer>(" ");
		j.add(new IfPlayerIsInFieldAttack());
		j.add(new IfBallIsInFieldMiddle());
		j.add(new ReturnToHomePosition());

		regiaoAtaque.add(g);
		regiaoAtaque.add(h);
		regiaoAtaque.add(i);
		regiaoAtaque.add(j);


		raiz.add(regiaoMeio);
		raiz.add(regiaoAtaque);
//		raiz.add(regiaoDefesa);
		raiz.add(new ReturnToHomePosition());
		return raiz;
}

	private BTNode<BehaviorTreePlayer> ZagueiroDireito() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Selector<BehaviorTreePlayer> regiaoDefesa = new Selector<BehaviorTreePlayer>("REGIAO DE DEFESA");

		Sequence<BehaviorTreePlayer> a = new Sequence<BehaviorTreePlayer>(" ");
		a.add(new IfPlayerIsInFieldDefense());
		a.add(new IfBallIsInFieldDefense());
		a.add(new GoGetBall());
		a.add(new IfClosestPlayerToBall());
		a.add(new PassBall());

		Sequence<BehaviorTreePlayer> b = new Sequence<BehaviorTreePlayer>(" ");
		b.add(new IfPlayerIsInFieldDefense());
		b.add(new IfBallIsInFieldMiddle());
		b.add(new AdvanceAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> c = new Sequence<BehaviorTreePlayer>(" ");
		c.add(new IfPlayerIsInFieldDefense());
		c.add(new IfBallIsInFieldAttack());
		c.add(new ReturnToHomePosition());

		regiaoDefesa.add(a);
		regiaoDefesa.add(b);
		regiaoDefesa.add(c);

		Selector<BehaviorTreePlayer> regiaoMeio = new Selector<BehaviorTreePlayer>("REGIAO DO MEIO");

		Sequence<BehaviorTreePlayer> d = new Sequence<BehaviorTreePlayer>(" ");
		d.add(new IfPlayerIsInFieldMiddle());
		d.add(new IfBallIsInFieldMiddle());
		d.add(new GoGetBall());
		d.add(new IfClosestPlayerToBall());
		d.add(new PassBall());

		Sequence<BehaviorTreePlayer> e = new Sequence<BehaviorTreePlayer>(" ");
		e.add(new IfPlayerIsInFieldMiddle());
		e.add(new IfBallIsInFieldDefense());
		e.add(new RetreatAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> f = new Sequence<BehaviorTreePlayer>(" ");
		f.add(new IfPlayerIsInFieldMiddle());
		f.add(new IfBallIsInFieldAttack());
		f.add(new RetreatAccordingToHomePosition());

		regiaoMeio.add(d);
		regiaoMeio.add(e);
		regiaoMeio.add(f);

		raiz.add(regiaoDefesa);
		raiz.add(regiaoMeio);
		raiz.add(new ReturnToHomePosition());
		return raiz;
	}


	private BTNode<BehaviorTreePlayer> AtacanteDireito() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Sequence<BehaviorTreePlayer> ataque = new Sequence<BehaviorTreePlayer>(" ");
		//ataque.add(new IfClosestPlayerToBall());
		//ataque.add(new IfPlayerBelongsToTrioCloserToBall());
		ataque.add(new AdvanceWithBallToGoal());
		ataque.add(new KickToScore());

		Sequence<BehaviorTreePlayer> defender = new Sequence<BehaviorTreePlayer>(" ");
		defender.add(new IfClosestPlayerToBall());
		defender.add(new GoGetBall());

		raiz.add(ataque);
		raiz.add(defender);
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> LateralEsquerdo() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

//		Selector<BehaviorTreePlayer> regiaoDefesa = new Selector<BehaviorTreePlayer>("REGIAO DE DEFESA");
//
//		Sequence<BehaviorTreePlayer> a = new Sequence<BehaviorTreePlayer>(" ");
//		a.add(new IfPlayerIsInFieldDefense());
//		a.add(new IfBallIsInFieldDefense());
//		a.add(new GoGetBall());
//		a.add(new IfClosestPlayerToBall());
//		a.add(new PassBall());
//
//		Sequence<BehaviorTreePlayer> b = new Sequence<BehaviorTreePlayer>(" ");
//		b.add(new IfPlayerIsInFieldDefense());
//		b.add(new IfBallIsInFieldMiddle());
//		b.add(new AdvanceAccordingToHomePosition());
//
//		Sequence<BehaviorTreePlayer> c = new Sequence<BehaviorTreePlayer>(" ");
//		c.add(new IfPlayerIsInFieldDefense());
//		c.add(new IfBallIsInFieldAttack());
//		c.add(new ReturnToHomePosition());
//
//		regiaoDefesa.add(a);
//		regiaoDefesa.add(b);
//		regiaoDefesa.add(c);



		Selector<BehaviorTreePlayer> regiaoMeio = new Selector<BehaviorTreePlayer>("REGIAO DO MEIO");

		Sequence<BehaviorTreePlayer> d = new Sequence<BehaviorTreePlayer>(" ");
		d.add(new IfPlayerIsInFieldMiddle());
		d.add(new IfBallIsInFieldMiddle());
		d.add(new GoGetBall());
		d.add(new IfClosestPlayerToBall());
		d.add(new PassBall());

		Sequence<BehaviorTreePlayer> e = new Sequence<BehaviorTreePlayer>(" ");
		e.add(new IfPlayerIsInFieldMiddle());
		e.add(new IfBallIsInFieldDefense());
		e.add(new RetreatAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> f = new Sequence<BehaviorTreePlayer>(" ");
		f.add(new IfPlayerIsInFieldMiddle());
		f.add(new IfBallIsInFieldAttack());
		f.add(new RetreatAccordingToHomePosition());

		regiaoMeio.add(d);
		regiaoMeio.add(e);
		regiaoMeio.add(f);


		Selector<BehaviorTreePlayer> regiaoAtaque = new Selector<BehaviorTreePlayer>("REGIAO DO MEIO");

		Sequence<BehaviorTreePlayer> g = new Sequence<BehaviorTreePlayer>(" ");
		g.add(new IfPlayerIsInFieldAttack());
		g.add(new IfBallIsInFieldAttack());
		g.add(new IfClosestPlayerToBall());
		g.add(new KickToScore());

		Sequence<BehaviorTreePlayer> h = new Sequence<BehaviorTreePlayer>(" ");
		h.add(new IfPlayerIsInFieldAttack());
		h.add(new IfBallIsInFieldAttack());
		h.add(new GoGetBall());
		h.add(new PassBall());

		Sequence<BehaviorTreePlayer> i = new Sequence<BehaviorTreePlayer>(" ");
		i.add(new IfPlayerIsInFieldAttack());
		i.add(new IfBallIsInFieldMiddle());
		i.add(new RetreatAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> j = new Sequence<BehaviorTreePlayer>(" ");
		j.add(new IfPlayerIsInFieldAttack());
		j.add(new IfBallIsInFieldMiddle());
		j.add(new ReturnToHomePosition());

		regiaoAtaque.add(g);
		regiaoAtaque.add(h);
		regiaoAtaque.add(i);
		regiaoAtaque.add(j);

		raiz.add(regiaoMeio);
		raiz.add(regiaoAtaque);
//		raiz.add(regiaoDefesa);
		raiz.add(new ReturnToHomePosition());
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> ZagueiroEsquerdo() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");

		Selector<BehaviorTreePlayer> regiaoDefesa = new Selector<BehaviorTreePlayer>("REGIAO DE DEFESA");

		Sequence<BehaviorTreePlayer> a = new Sequence<BehaviorTreePlayer>(" ");
		a.add(new IfPlayerIsInFieldDefense());
		a.add(new IfBallIsInFieldDefense());
		a.add(new GoGetBall());
		a.add(new IfClosestPlayerToBall());
		a.add(new PassBall());

		Sequence<BehaviorTreePlayer> b = new Sequence<BehaviorTreePlayer>(" ");
		b.add(new IfPlayerIsInFieldDefense());
		b.add(new IfBallIsInFieldMiddle());
		b.add(new AdvanceAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> c = new Sequence<BehaviorTreePlayer>(" ");
		c.add(new IfPlayerIsInFieldDefense());
		c.add(new IfBallIsInFieldAttack());
		c.add(new ReturnToHomePosition());

		regiaoDefesa.add(a);
		regiaoDefesa.add(b);
		regiaoDefesa.add(c);

		Selector<BehaviorTreePlayer> regiaoMeio = new Selector<BehaviorTreePlayer>("REGIAO DO MEIO");

		Sequence<BehaviorTreePlayer> d = new Sequence<BehaviorTreePlayer>(" ");
		d.add(new IfPlayerIsInFieldMiddle());
		d.add(new IfBallIsInFieldMiddle());
		d.add(new GoGetBall());
		d.add(new IfClosestPlayerToBall());
		d.add(new PassBall());

		Sequence<BehaviorTreePlayer> e = new Sequence<BehaviorTreePlayer>(" ");
		e.add(new IfPlayerIsInFieldMiddle());
		e.add(new IfBallIsInFieldDefense());
		e.add(new RetreatAccordingToHomePosition());

		Sequence<BehaviorTreePlayer> f = new Sequence<BehaviorTreePlayer>(" ");
		f.add(new IfPlayerIsInFieldMiddle());
		f.add(new IfBallIsInFieldAttack());
		f.add(new RetreatAccordingToHomePosition());

		regiaoMeio.add(d);
		regiaoMeio.add(e);
		regiaoMeio.add(f);

		raiz.add(regiaoDefesa);
		raiz.add(regiaoMeio);
		raiz.add(new ReturnToHomePosition());
		return raiz;
	}

	private BTNode<BehaviorTreePlayer> AtacanteEsquerdo() {
		Selector<BehaviorTreePlayer> raiz = new Selector<BehaviorTreePlayer>("RAIZ");


		Sequence<BehaviorTreePlayer> ataque = new Sequence<BehaviorTreePlayer>(" ");
		//ataque.add(new IfClosestPlayerToBall());
		//ataque.add(new IfPlayerBelongsToTrioCloserToBall());
		ataque.add(new AdvanceWithBallToGoal());
		ataque.add(new KickToScore());

		Sequence<BehaviorTreePlayer> defender = new Sequence<BehaviorTreePlayer>(" ");
		defender.add(new IfClosestPlayerToBall());
		defender.add(new GoGetBall());

		raiz.add(ataque);
		raiz.add(defender);
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
