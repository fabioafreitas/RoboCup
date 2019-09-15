package simple_soccer_lib;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import simple_soccer_lib.team.A1.A1;
import simple_soccer_lib.team.Krislet.Krislet;
import simple_soccer_lib.team.NoSwarm.NoSwarm;
import simple_soccer_lib.team.Tracker.Tracker;

/**
 * Esta classe facilita lançar um time de agentes, possivelmente junto com o servidor.
 * 
 * Para criar um time, basta implementar o metodo <b>launchPlayer()</b> para que, a cada 
 * chamada (na forma de callback), a sua classe instancie algum agente (com a classe que 
 * quiser) para atuar por meio do PlayerCommander dado como paaâmetro.
 * 
 * Em seguida, o seu time pode ser inicializado com facilidade usando os metodos 
 * <b>launchTeam()</b> ou <b>launchTeamAndServer()</b>
 * 
 * @author Pablo Sampaio
 */
public abstract class AbstractTeam {
	private String hostName;
	private int port;

	private String teamName;
	private int numPlayers;
	private boolean withGoalie;
	
	public AbstractTeam(String name, int players, String host, int port, boolean withGoalie) {
		this.hostName = host;
		this.port = port;
		this.teamName = name;
		this.numPlayers = players;
		this.withGoalie = withGoalie;
	}
	
	public AbstractTeam(String name, int players, boolean withGoalie) {
		this.hostName = "localhost";
		this.port = 6000;
		this.teamName = name;
		this.numPlayers = players;
		this.withGoalie = withGoalie;
	}

	/**
	 * Recebe o índice do agente. O índice zero é para o goleiro. Uma subclasse deve
	 * instanciar alguma classe para controlar o agente (provavelmente em uma thread) 
	 * por meio do PlayerCommander dado como parametro. 
	 */
	protected abstract void launchPlayer(int ag, PlayerCommander commander);
	

	public final void launchTeam(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PlayerCommander commander;
				
				System.out.println(" >> Iniciando o time...");
				for (int i = 0; i < AbstractTeam.this.numPlayers; i++) {
					try{
						if(i == 0){
							commander = new PlayerCommander(AbstractTeam.this.teamName, AbstractTeam.this.hostName, AbstractTeam.this.port, AbstractTeam.this.withGoalie);
						}else{
							commander = new PlayerCommander(teamName, hostName, port, false);
						}
						launchPlayer(i, commander);
					}catch(UnknownHostException uhe){
						System.err.println("Não foi possível conectar ao host: "+AbstractTeam.this.hostName);
						uhe.printStackTrace();
					}
					try {
						Thread.sleep(250);
					} catch (Exception e) {}
				}
			}
		}).start();
	}
	
	public final void launchTeamAndServer() throws UnknownHostException {
		launchServer();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		launchTeam();
	}
	
	public final void launchServer() {
		try {
			System.out.println(" >> Iniciando servidor...");
			
			Runtime r = Runtime.getRuntime();
			Process p = r.exec("cmd /c tools\\startServer.cmd");
			p.waitFor();
//			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			String line = "";
//			while ((line = b.readLine()) != null) {
//			  System.out.println(line);
//			  System.out.println(".");
//			}
//			b.close();

        } catch(Exception e) {
        	e.printStackTrace();
        	System.err.println("Não pode iniciar o servidor!");
        	return;
        }
	}
	
	public final void launchKrislet(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			for (int i = 0; i < this.numPlayers; i++) {
				Krislet krislet = new Krislet(InetAddress.getByName(hostName), port, "KRISLET");
				krislet.run();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public final void launchTracker(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			for (int i = 0; i < this.numPlayers; i++) {
				Tracker tracker = new Tracker(InetAddress.getByName(hostName), port, "TRACKER");
				tracker.run();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public final void launchNoSwarm(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			for (int i = 0; i < this.numPlayers; i++) {
				NoSwarm noSwarm = new NoSwarm(InetAddress.getByName(hostName), port, "NOSWARM");
				noSwarm.run();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public final void launchA1(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			for (int i = 0; i < this.numPlayers; i++) {
				A1 a1 = new A1(InetAddress.getByName(hostName), port, "A1");
				a1.run();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
