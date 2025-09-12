import java.util.*;

public class BlocoDeControleDeProcessos implements Comparable<BlocoDeControleDeProcessos>{
    private int contadorPrograma, tempoEspera, nomeArquivo;
    private String nomePrograma, estadoProcesso;
    private int[] registradores = new int[2];
    private List<String> instrucoesDoPrograma = new ArrayList<>();

    /*
     * Gerencia a lista de processos que estao na tabela de processos.
     * O BCP vai ser uma classe possuindo informacoes dos programas.
     * E a tabela de processos sera um ArrayList guardando esses blocos.
     */
    public BlocoDeControleDeProcessos(){};

    public BlocoDeControleDeProcessos(List<String> instrucoesDoPrograma, int nomeArquivo){
        this.instrucoesDoPrograma = instrucoesDoPrograma;
        this.nomePrograma = instrucoesDoPrograma.remove(0);
        this.estadoProcesso = "Pronto";
        this.nomeArquivo = nomeArquivo;
    }

    public void imprimeListaInstrucoes(){
        for(String x : instrucoesDoPrograma){
            System.out.println(x);
        }
    }

    @Override
    public int compareTo(BlocoDeControleDeProcessos bloco){
        return nomeArquivo - bloco.nomeArquivo;
    }

    @Override
    public String toString(){
        return nomePrograma + " - PC: [" + contadorPrograma + "/" + instrucoesDoPrograma.size() + "] - Tempo Espera: " + tempoEspera + " - Estado: " + estadoProcesso + "\n";
    }

    //////////////////////////////////////////////
    //              GETs e SETs                 //
    //////////////////////////////////////////////

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

    public List<String> getInstrucoesDoPrograma(){
        return this.instrucoesDoPrograma;
    }

    public int getContador(){
        return this.contadorPrograma;
    }

    public void setContador(int valor){
        this.contadorPrograma = valor;
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
        this.contadorPrograma++;
    }

    public void setEstadoProcesso(String novoEstado){
        estadoProcesso = novoEstado;
    }

    public int getNomeArquivo(){
        return nomeArquivo;
    }
}