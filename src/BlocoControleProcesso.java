import java.util.*;

public class BlocoControleProcesso implements Comparable<BlocoControleProcesso>{
    private int contador;
    private int tempoEspera;
    private int nomeArquivo;
    private String nomePrograma, estadoProcesso;
    private int[] registradores = new int[2];
    private List<String> listaIntrucoesPrograma = new ArrayList<>();

    /*
     * Classe BlocoControleProcesso (BCP).
     *
     * Representa um processo na tabela de processos (Process Table) do sistema.
     * Cada BCP armazena informações essenciais para o gerenciamento do processo,
     * como o nome do programa, estado atual, registradores, contador de instruções,
     * tempo de espera e a lista de instruções associadas.
     *
     * A tabela de processos é composta por vários BCPs organizados em uma estrutura
     * de dados (por exemplo, um ArrayList), permitindo ao escalonador controlar
     * e manipular o ciclo de vida de cada processo.
     *
     * Em resumo:
     * - O BCP encapsula os dados e o contexto de um processo.
     * - A tabela de processos é a coleção desses BCPs, usada pelo escalonador.
     */
    public BlocoControleProcesso(){};

    public BlocoControleProcesso(List<String> listaIntrucoesPrograma, int nomeArquivo){
        this.listaIntrucoesPrograma = listaIntrucoesPrograma;
        this.nomePrograma = listaIntrucoesPrograma.remove(0);
        this.estadoProcesso = "Pronto";
        this.nomeArquivo = nomeArquivo;
    }

    @Override
    public int compareTo(BlocoControleProcesso bloco){
        return nomeArquivo - bloco.nomeArquivo;
    }

    @Override
    public String toString(){
        return nomePrograma + " - PC: [" + contador + "/" + listaIntrucoesPrograma.size() + "] - Tempo Espera: " + tempoEspera + " - Estado: " + estadoProcesso + "\n";
    }

    // getters e setters
    public int getRegistradorX(){
        return this.registradores[0];
    }

    public int getRegistradorY(){
        return this.registradores[1];
    }

    public void setRegistradorX(int x){
        this.registradores[0] = x;
    }

    public void setRegistradorY(int y){
        this.registradores[1] = y;
    }

    public List<String> getlistaIntrucoesPrograma(){
        return this.listaIntrucoesPrograma;
    }

    public int getContador(){
        return this.contador;
    }

    public String getNomePrograma(){
        return this.nomePrograma;
    }

    public int getTempoEspera(){
        return this.tempoEspera;
    }

    public void setTempoEspera(int tempo){
        this.tempoEspera += tempo;
    }

    public void decrementaTempoEspera(){
        this.tempoEspera--;
    }

    public void incrementaContador(){
        this.contador++;
    }

    public void setEstadoProcesso(String novoEstado){
        estadoProcesso = novoEstado;
    }
}