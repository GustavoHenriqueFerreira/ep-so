import java.io.IOException;
import java.lang.IllegalStateException;
import java.util.*;
import java.io.File;

public class LeituraPrograma {
    //ler os programas
    private static Scanner entrada;
    private static int quantum;

    //abrir o arquivo arquivo.txt
    public static List<BlocoControleProcesso> listarProgramasLidos() {
        List<BlocoControleProcesso> listaDeProgramas = new ArrayList<>();

        File diretorio = new File("src/programas");

        for(File file : diretorio.listFiles()){
            try {
                // carrega somente os programas
                if (!file.getName().equals("quantum.txt")){
                    entrada = new Scanner(file);

                    //carregar todos os arquivos
                    BlocoControleProcesso blocoDoPrograma = new BlocoControleProcesso(lerDados(), Integer.parseInt(file.getName().replace(".txt", "")));
                    listaDeProgramas.add(blocoDoPrograma);
                    fecharArquivo();
                }
            }
            catch (IOException erroES) {
                System.err.println("Erro ao abrir o arquivo. Finalizando.");
                System.exit(1);//terminar o programa
            }
        }

        //sort para ordenar
        Collections.sort(listaDeProgramas);

        return listaDeProgramas;
    }

    //ler os registros do arquivo
    public static List<String> lerDados() {
        List<String> listaDeComandos = new ArrayList<>();

        try {
            while (entrada.hasNext()) {
                //enquanto houver dados para ler, mostrar os registros
                String comando = entrada.nextLine();
                listaDeComandos.add(comando);
                //System.out.println(comando);
            }
        }
        catch (NoSuchElementException erroElemento) {
            // descartar a entrada para que o usuário possa tentar de novo
            System.err.println("Arquivo com problemas. Finalizando.");
            entrada.nextLine();
        }
        catch (IllegalStateException erroEstado) {
            System.err.println("Erro ao ler o arquivo. Finalizando.");
        }

        return listaDeComandos;
    }

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
            //terminar o programa
            System.err.println("Erro ao abrir o quantum. Finalizando.");
            System.exit(1);
            return 0;
        }
    }

    //fechar o arquivo aberto
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