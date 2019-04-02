
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
		String s[] = new String[dados.size()];
		String s2[] = new String[dados.size()];
		String c[] = new String[dados.size()];
		double a = 0, x = 0, dist = 0;

		for (int i = 0; i < dados.size(); i++) {
			if(i == 0) {
				mensagem = ",";
				continue;
			}
			System.out.println("for cabecalho");
			s = dados.get(i -1).split(",");
			if( i != (dados.size() - 1)) {
				System.out.println(s[0]);
				mensagem = mensagem + s[0] +",";
			}else{
				System.out.println(s[0]);
				mensagem = mensagem + s[0];
			}
		}
		System.out.println(mensagem);
		output.println(mensagem);
		
		for (int i = 0; i < (dados.size()-1); i++) {
			System.out.println(dados.get(i));
			s = dados.get(i).split(",");

			
			
			s[2] = s[2].trim();
			if(s[2].contains("�")) {
				c = s[2].split("°");
			}else {
				c = s[2].split("�");
			}
			d1 = Double.parseDouble(c[0].trim());
			c[1] = c[1].trim();
			c = c[1].split("\'");
			d2 = Double.parseDouble(c[0].trim());
			c[1] = c[1].trim();
			//c[1].replace("', ' ');
			c = c[1].split("\"");
			d3 = Double.parseDouble(c[0].trim());	
			lon1 = d1 + d2 / 60 + d3 / 3600;
			
			if(c[1].contains("S")) {
				lat1 = lat1 * (-1);
			}
			if(c[1].contains("W")) {
				lon1 = lon1 * (-1);
			}
			
			
			lat1 = Math.toRadians(lat1);
			lon1 = Math.toRadians(lon1);
			
			
			//mensagem = mensagem + "," + lat1+","+lon1;
			
			for (int j = 0; j < (dados.size() - 1); j++) {
				a = 0;
				x = 0;
				dist = 0;
				d1 = 0;
				d2 = 0;
				d3 = 0;		
				if(i == j && j > 0) {
					mensagem = mensagem + "0,";
					continue;
				}
				if(j == 0 && i == j) {
					mensagem = s[0]+", 0,";
				}else if(j == 0) {
					s2 = dados.get(j).split(",");
					s2[1] = s2[1].trim();
					if(s[1].contains("�")) {
						c = s[1].split("°");
					}else {
						c = s[1].split("�");
					}
					d1 = Double.parseDouble(c[0].trim());
					c[1] = c[1].trim();
					c = c[1].split("\'");
					d2 = Double.parseDouble(c[0].trim());
					c[1] = c[1].trim();
					c = c[1].split("\"");
					d3 = Double.parseDouble(c[0].trim());	
					System.out.println("for i "+d3);
					lat2 = d1 + d2 / 60 + d3 / 3600;
	
					s2[2] = s2[2].trim();
					if(s[2].contains("�")) {
						c = s[2].split("°");
					}else {
						c = s[2].split("�");
					}
					d1 = Double.parseDouble(c[0].trim());
					c[1] = c[1].trim();
					c = c[1].split("\'");
					d2 = Double.parseDouble(c[0].trim());
					c[1] = c[1].trim();
					c = c[1].split("\"");
					d3 = Double.parseDouble(c[0].trim());	
					lon2 = d1 + d2 / 60 + d3 / 3600;
				
					if(c[1].contains("S")) {
						lat2 = lat2 * (-1);
					}
					if(c[1].contains("W")) {
						lon2 = lon2 * (-1);
					}
					
					lat2 = Math.toRadians(lat2);
					lon2 = Math.toRadians(lon2);
					
					
					a = Math.sin((lat2 - lat1)/2) * Math.sin((lat2 - lat1)/2) + 
							Math.cos(lat1) * Math.cos(lat2) * Math.sin((lon2 - lon1)/2) * Math.sin((lon2 - lon1)/2);
					
					x = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

					dist = 6371 * x;
					int u = (int) dist;
					mensagem = s[0]+","+ u +",";
				}else {
					s2 = dados.get(j).split(",");
					s2[1] = s2[1].trim();
					if(s[1].contains("�")) {
						c = s[1].split("°");
					}else {
						c = s[1].split("�");
					}
					d1 = Double.parseDouble(c[0].trim());
					c[1] = c[1].trim();
					c = c[1].split("\'");
					d2 = Double.parseDouble(c[0].trim());
					c[1] = c[1].trim();
					c = c[1].split("\"");
					d3 = Double.parseDouble(c[0].trim());	
					lat2 = d1 + d2 / 60 + d3 / 3600;
	
					s2[2] = s2[2].trim();
					if(s[2].contains("�")) {
						c = s[2].split("°");
					}else {
						c = s[2].split("�");
					}
					d1 = Double.parseDouble(c[0].trim());
					c[1] = c[1].trim();
					c = c[1].split("\'");
					d2 = Double.parseDouble(c[0].trim());
					c[1] = c[1].trim();
					c = c[1].split("\"");
					d3 = Double.parseDouble(c[0].trim());	
					lon2 = d1 + d2 / 60 + d3 / 3600;
				
					if(c[1].contains("S")) {
						lat2 = lat2 * (-1);
					}
					if(c[1].contains("W")) {
						lon2 = lon2 * (-1);
					}
					lat2 = Math.toRadians(lat2);
					lon2 = Math.toRadians(lon2);
					
					
					a = Math.sin((lat2 - lat1)/2) * Math.sin((lat2 - lat1)/2) + 
							Math.cos(lat1) * Math.cos(lat2) * Math.sin((lon2 - lon1)/2) * Math.sin((lon2 - lon1)/2);
					
					x = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

					dist = 6371 * x;
					int u = (int) dist;
					mensagem = mensagem + u +",";
				}
			}
			output.println(mensagem);
		}
	
		
	
		
		output.println("FIM");
		System.out.println("Conecao sera encerrada");
		
		close();
	}
	
	
	
	
}