import java.io.IOException;
import java.lang.IllegalStateException;
import java.util.*;
import java.io.File;

public class LeituraPrograma {
    //Metodo para ler os programas
    private static Scanner entrada;
    private static int quantum;

    //Método para abrir o arquivo arquivo.txt
    public static List<BlocoDeControleDeProcessos> programasLidos() {
        List<BlocoDeControleDeProcessos> listaDeProgramas = new ArrayList<>();

        File diretorio = new File("src/programas");

        for(File file : diretorio.listFiles()){
            try {
                // Carrega somente os programas
                if (!file.getName().equals("quantum.txt")){
                    entrada = new Scanner(file);

                    //Carregar todos os arquivos (e seus comandos e gerar um BCP/Arquivo)
                    BlocoDeControleDeProcessos blocoDoPrograma = new BlocoDeControleDeProcessos(lerDados(), Integer.parseInt(file.getName().replace(".txt", "")));
                    listaDeProgramas.add(blocoDoPrograma);
                    fecharArquivo();
                }
            }
            catch (IOException erroES) {
                System.err.println("Erro ao abrir o arquivo. Finalizando.");
                System.exit(1);//terminar o programa
            }
        }

        //Usando o collection sort para ordenar
        Collections.sort(listaDeProgramas);

        return listaDeProgramas;
    }

    //Metodo para ler os registros do arquivo
    public static List<String> lerDados() {
        List<String> listaDeComandos = new ArrayList<>();

        try {
            while (entrada.hasNext()) {//enquanto houver dados para ler, mostrar os registros
                String comando = entrada.nextLine();
                listaDeComandos.add(comando);
                //System.out.println(comando);
            }
        }
        catch (NoSuchElementException erroElemento) {
            System.err.println("Arquivo com problemas. Finalizando.");
            entrada.nextLine(); // Descartar a entrada para que o usuário possa tentar de novo
        }
        catch (IllegalStateException erroEstado) {
            System.err.println("Erro ao ler o arquivo. Finalizando.");
        }

        return listaDeComandos;
    }//fim do método lerDados

    public static int getQuantumFile(String fileName){
        try{
            File quantumFile = new File("src/programas/" + fileName);
            System.out.println(quantumFile);
            entrada = new Scanner(quantumFile);
            quantum = Integer.parseInt(lerDados().get(0));
            //System.out.println("QUANTUM LIDO = " + quantum);

            return quantum;
        }
        catch (Exception e){
            System.err.println("Erro ao abrir o quantum. Finalizando.");
            System.exit(1);//terminar o programa
            return 0;
        }
    }

    //Metodo para fechar o arquivo aberto
    public static void fecharArquivo() {
        if (entrada != null)
            entrada.close();
    }

    public static void apagarLogs(){
        File diretorio = new File(".");

        for(File file : diretorio.listFiles()){

            if (file.getName().startsWith("log") && file.getName().contains(".txt"))
                file.delete();
        }
    }
}