package simple_soccer_lib.team.NoSwarm;
//
//	File:			NoSwarm.java



import java.io.*;
import java.net.*;
import java.util.StringTokenizer;


//***************************************************************************
//
//	This is main object class
//
//***************************************************************************
public class NoSwarm implements SendCommand
{
//===========================================================================
// Initialization member functions

	//---------------------------------------------------------------------------
	// The main appllication function.
	// Command line format:
	//
	// NoSwarm [-parameter value]
	//
	// Parameters:
	//
	//	host (default "localhost")
	//		The host name can either be a machine name, such as "java.sun.com" 
	//		or a string representing its IP address, such as "206.26.48.100."
	//
	//	port (default 6000)
	//		Port number for communication with server
	//
	//	team (default Kris)
	//		Team name. This name can not contain spaces.
	//
	//	
	//---------------------------------------------------------------------------
	// This constructor opens socket for  connection with server
	public NoSwarm(InetAddress host, int port, String team) 
		throws SocketException
	{
		m_socket = new DatagramSocket();
		m_host = host;
		m_port = port;
		m_team = team;
	}
																 
	//---------------------------------------------------------------------------
	// This destructor closes socket to server
	public void finalize()
	{
		m_socket.close();
	}
	
	public void run() throws IOException{
    	new Thread() {
    		public void run() {
    			try {
					mainLoop();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}.start();
    }



//===========================================================================
// Protected member functions

	//---------------------------------------------------------------------------
	// This is main loop for player
	protected void mainLoop() throws IOException
	{
		byte[] buffer = new byte[MSG_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);

		// first we need to initialize connection with server
		init();

		m_socket.receive(packet);
		parseInitCommand(new String(buffer,0));
		m_port = packet.getPort();

		// Now we should be connected to the server
		// and we know side, player number and play mode
		while( true )
			parseSensorInformation(receive());
	}


//===========================================================================
// Implementation of SendCommand Interface

	//---------------------------------------------------------------------------
	// This function sends move command to the server
	public void move(double x, double y)
	{
		send("(move " + Double.toString(x) + " " + Double.toString(y) + ")");
	}

	//---------------------------------------------------------------------------
	// This function sends turn command to the server
	public void turn(double moment)
	{
		send("(turn " + Double.toString(moment) + ")");
	}

	public void turn_neck(double moment)
	{
		send("(turn_neck " + Double.toString(moment) + ")");
	}

	//---------------------------------------------------------------------------
	// This function sends dash command to the server
	public void dash(double power)
	{
		send("(dash " + Double.toString(power) + ")");
	}

	//---------------------------------------------------------------------------
	// This function sends kick command to the server
	public void kick(double power, double direction)
	{
		send("(kick " + Double.toString(power) + " " + Double.toString(direction) + ")");
	}

	//---------------------------------------------------------------------------
	// This function sends say command to the server
	public void say(String message)
	{
		send("(say " + message + ")");
	}

	//---------------------------------------------------------------------------
	// This function sends chage_view command to the server
	public void changeView(String angle, String quality)
	{
		send("(change_view " + angle + " " + quality + ")");
	}

	//---------------------------------------------------------------------------
	// This function parses initial message from the server
	protected void parseInitCommand(String message)	throws IOException
	{
		StringTokenizer	tokenizer = new StringTokenizer(message,"() ");
		
		// We need init token
		if( tokenizer.nextToken().compareTo("init") != 0)
		{
			throw new IOException(message);
		}

		// initialize player's brain
		m_brain = new Brain(this, 
												m_team, 
												tokenizer.nextToken().charAt(0), 
												Integer.parseInt(tokenizer.nextToken()),
												tokenizer.nextToken());
	}



//===========================================================================
// Here comes collection of communication function
	//---------------------------------------------------------------------------
	// This function sends initialization command to the server
	private void init()
	{
                send("(init " + m_team + " (version 9))");
	}

	//---------------------------------------------------------------------------
	// This function parses sensor information
	private void parseSensorInformation(String message)
	{
		// First check kind of information		
		if( message.charAt(1) == 's' && message.charAt(3)=='e')
		{
			VisualInfo	info = new VisualInfo(message);
			info.parse();
			m_brain.see(info);
		}
		else if( message.charAt(1) == 'h' )
			parseHear(message);
	}


	//---------------------------------------------------------------------------
	// This function parses hear information
	private void parseHear(String message)
	{
		// get hear information
		StringTokenizer	tokenizer = new StringTokenizer(message,"() ");
		int	time;
		String sender;

		// skipp hear token
		tokenizer.nextToken();
		time = Integer.parseInt( tokenizer.nextToken() );
		sender = tokenizer.nextToken();
		if( sender.compareTo("referee") == 0 )
			m_brain.hear(time, tokenizer.nextToken());
		else if( sender.compareTo("self") != 0 )
			m_brain.hear(time, Integer.parseInt(sender), tokenizer.nextToken());
	}


	//---------------------------------------------------------------------------
	// This function sends via socket message to the server
	private void send(String message)
	{
		byte[] buffer = new byte[MSG_SIZE];
		message.getBytes(0, message.length(), buffer, 0);

		DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE, m_host, m_port);

		try{
			m_socket.send(packet);
		}catch(IOException e){
			System.err.println("socket sending error " + e);
		}
	}

	//---------------------------------------------------------------------------
	// This function waits for new message from server
	private String receive() 
	{
		byte[] buffer = new byte[MSG_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);
		try{
			m_socket.receive(packet);
		}catch(IOException e){
			System.err.println("socket receiving error " + e);
		}
		return new String(buffer,0);
	}

				
								 
//===========================================================================
// Private members
	// class memebers
	private DatagramSocket	m_socket;		// Socket to communicate with server
	private	InetAddress			m_host;			// Server address
	private int							m_port;			// server port
	private String					m_team;			// team name
	private SensorInput			m_brain;		// input for sensor information

	// constants
	private static final int	MSG_SIZE = 4096;	// Size of socket buffer

}
