
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
//import java.text.DecimalFormat;
import java.util.ArrayList;

public class Atendente implements Runnable{
	private Socket socket;
	private BufferedReader input;
	private PrintStream output;
	
	private boolean inicializado;
	private boolean executando;
	
	private Thread thread;
	
	
	public Atendente(Socket s){
		this.socket = s;
		
		this.executando = false;
		this.inicializado = false;
		
		this.open();
	}
	
	//abrir canais da conexao
	private void open(){
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintStream(socket.getOutputStream());
			inicializado = true;
		}catch (Exception e) {
			close();
			e.printStackTrace();
		}
	}
	
	// fechar canais de conexao
	private void close() {
		//fechar o canal de entrada de dados
		if (input != null) {
			try {
				input.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		//fechar o canal de saida de dados
		if (output != null) {
			try {
				output.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//fecha o socket 
		try {
			socket.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//reinicia os atributos
		input = null;
		output = null;
		socket = null;
		
		inicializado = false;
		executando = false;
		
		thread = null;
	}
	
	//inicializa as flags de controle e a thread
	public void start() {
		if(!inicializado || executando) {
			return;
		}
		
		executando = true;
		thread = new Thread(this);
		thread.start();
		
	}
	
	public void stop() throws Exception{
		executando = false;
		
		if(thread != null) {
			thread.join();
		}
	}
	
		
	@Override
	public void run(){
		ArrayList<String> dados = new ArrayList<String>();
		int z = 0;
		double lat1 = 0, lon1 = 0, lat2 = 0, lon2 = 0, d1 = 0, d2 = 0, d3 = 0;
	//	boolean processando = true;
		while(executando){
			try {
				socket.setSoTimeout(2500);
				String mensagem = input.readLine();
				
				dados.add(mensagem);
				System.out.println("Mensagem: "+ mensagem+"  \nPorta: "+socket.getPort());
	
				if("FIM".equals(mensagem)) {
					executando = false;
					break;
				}
				
				output.println("Dado: "+ z +"  RECEBIDO");
				z++;
			}catch (SocketTimeoutException e) {
				// faz nada
			}catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		
		
		String mensagem = "";
		String s[];
		int x = 0, result = 0;
		z = 0;
		for(int i = 0; i < dados.size()-1;i++){
			System.out.println("i: " + i);
			s = dados.get(i).split(",");
			System.out.println("primeiro: "+ s[0] + "  segundo: " + s[1]);
			
			z = Integer.parseInt(s[0]);
			x = Integer.parseInt(s[1]);

			System.out.println("z: "+ z + "  x: " + x);

			result = z;
			System.out.println("r: " + result);
			for(int g = 1; g < x; g++){
				result = result * z;
			}
			

			mensagem = ("Dado "+ i + " Calculo: " + result);
			System.out.println(mensagem);
			output.println(mensagem);
		}



		

		
		
		output.println("FIM");
		System.out.println("Conecao sera encerrada");
		
		close();
	}
	

	
}
