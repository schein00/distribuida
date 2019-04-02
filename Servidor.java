

import 	java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;


public class Servidor implements Runnable{
	
	private ServerSocket server;
	private boolean inicializado;
	private boolean executando;
	
	
	ArrayList<Atendente> atendentes = new ArrayList<Atendente>();
	
	private Thread thread;
	
	
	public Servidor(int porta) {
		inicializado = false;
		executando   = false;
		
		init(porta);
	}
	
	private void init(int porta){
		try{
			server = new ServerSocket(porta);
			inicializado = true;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	private void close() {
		for(Atendente atendente : atendentes) {
			try {
				atendente.stop();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
		
		try {
			server.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		// reinicia os dados
		server = null;
		inicializado = false;
		executando = false;
		thread = null;
	}
	
	public void start() {
		if(!inicializado || executando) {
			return;
		}
		
		executando = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() throws Exception{
		try {
			executando = false;
			if(thread != null) {
				thread.join();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void run() {
		System.out.println("Aguardando conecao....");
		while(executando) {
			try {
				server.setSoTimeout(2500);
				
				Socket socket = server.accept();
				
				System.out.println("Conexao estabelecida....");
				
				Atendente atendente = new Atendente(socket);
				atendente.start();
				
				atendentes.add(atendente);
				
			}catch (SocketTimeoutException e) {
				// faz nada
			}catch (Exception e) {
				e.printStackTrace();
				break;
			}	
		}
		close();
	}
	
	
	//inicializa o servidor, criado a thread principail, que fica "ouvindo" a porta 48484
	
	public static void main(String []args) throws Exception{
		try {
			Servidor servidor = new Servidor(48488);
			servidor.start();

			System.out.println("Para finalizar o servidor presione ENTER");
			new Scanner(System.in).nextLine();
			
			System.out.println("Encerrando servidor......");
			servidor.close();	
			
		}catch(Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
	
}