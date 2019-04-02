import java.net.*;
import java.util.*;
import java.io.*;


public class Cliente{

	public static void main(String []args){

		File arquivo;
		Scanner conteudo;
		String linhas[];
		int i = 0, count;
		
		try {
			
			//bloco de codigo para a leitura do arquivo de entrada
			arquivo = new File(args[2]);
			conteudo = new Scanner(arquivo);
			long tamanhoArquivo = arquivo.length();
			FileInputStream fs = new FileInputStream(arquivo);
			DataInputStream in = new DataInputStream(fs);
			LineNumberReader lineRead = new LineNumberReader(new InputStreamReader(in));
			lineRead.skip(tamanhoArquivo);
			count = lineRead.getLineNumber();
			linhas  = new String[count];
			System.out.println("Quantia de linhas: "+count);
			
			while(conteudo.hasNext()){
				String g = conteudo.nextLine();
				System.out.println("add a linha: "+ i + "  info: "+g);
				linhas[i] = g;
				i++;
			}
			
			//cria um arquivo novo com a saida
			FileWriter writer = new FileWriter("Saida.csv");
			
			
			System.out.println("Inicializando Cliente........");
		
			
			System.out.println("Inicializando conexao com Servidor........");
		
			//"localhost" , 48484
			int p = Integer.parseInt(args[1]);
			Socket socket = new Socket(args[0], p);

			
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();
			
			
			BufferedReader inn = new BufferedReader(new InputStreamReader(input));
			PrintStream out = new PrintStream(output);
			
			Scanner scanner = new Scanner(System.in);
			
			i = 0;
			while(i < count) {
				
				System.out.println("Enviando dados para processar");
				out.println(linhas[i]);
				i++;
				
				String mensagem = inn.readLine();
				System.out.println("Resposta servidor::: "+ mensagem);
			}

			out.println("FIM");
			boolean recebendo = true;
			
			while (recebendo) {
				String mensagem = inn.readLine();
				if("FIM".equals(mensagem)) {
					recebendo = false;
					break;
				}
				writer.append(mensagem);
				writer.append('\n');
			}
			
			System.out.println("Escrevendo arquivo de saida.....");
			writer.flush();
	        writer.close();
			
			
			System.out.println("Encerrando conexao.....");
			
			in.close();
			out.close();
			socket.close();
			
			
			
			
		
		}catch(Exception ex) {
			ex.printStackTrace();
			return;
		}

	}


}
