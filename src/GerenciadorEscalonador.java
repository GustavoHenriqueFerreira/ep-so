import java.util.List;
import java.util.ArrayList;

public class GerenciadorEscalonador {
    private int quantum;
    private int instrucoesExecutadas;
    private int trocas;
    private int totalProgramas;
    private Instrucao instrucao = null;
    private String log;

    //tipos de processos
    protected List<BlocoControleProcesso> listaDeProntos = new ArrayList<>();
    protected List<BlocoControleProcesso> listaDeBloqueados = new ArrayList<>();

    //tabela de processos tem ponteiro para o bloco de controle
    protected List<BlocoControleProcesso> tabelaDeProcessos = new ArrayList<>();

    // construtor
    public GerenciadorEscalonador(int quantum){
        this.quantum = quantum;
    }

    public GerenciadorEscalonador(String quantumFile){
        this.quantum = LeituraPrograma.getQuantumFile(quantumFile);
    }

    // metodos
    /**
     * Conjunto de métodos responsáveis por gerenciar os processos
     * e controlar a mudança de seus estados durante a execução.

     * O gerenciamento segue a política de escalonamento Round-Robin,
     * na qual o processador é compartilhado entre os processos de forma
     * cíclica, respeitando o valor de quantum definido.

     * Principais responsabilidades:
     * - Atualizar o estado do processo (Pronto, Executando, Bloqueado, etc.).
     * - Incrementar/decrementar o contador de instruções e o tempo de espera.
     * - Simular a execução das instruções de acordo com o quantum.
     * - Garantir a alternância justa entre os processos na tabela.
     */
    public void carregarProgramas(){
        tabelaDeProcessos = LeituraPrograma.listarProgramasLidos();
        totalProgramas = tabelaDeProcessos.size();

        //todos na lista de prontos
        for(BlocoControleProcesso processo : tabelaDeProcessos){

            listaDeProntos.add(processo);

            String log = "Carregando " + processo.getNomePrograma();
            Log.gravarLog(quantum, log);
        }
    }

    // Round Robin
    public void executarRoundRobin() {
        System.out.println("INICIANDO ROUND ROBIN - QUANTUM = " + quantum);

        // enquanto ainda houver processos prontos ou bloqueados, o escalonador continua ativo
        while (listaDeProntos.size() > 0 || listaDeBloqueados.size() > 0) {
            try {
                // caso tenha algum processo pronto para execução
                if (listaDeProntos.size() > 0) {
                    // seleciona o primeiro processo da fila de prontos
                    BlocoControleProcesso programa = listaDeProntos.get(0);

                    // cria uma nova "instrução" a ser executada pelo processo, limitada pelo quantum
                    instrucao = new Instrucao(programa, quantum);

                    // verifica se o processo já terminou antes de executar
                    if (!instrucao.terminou()) {
                        // log início da execução
                        log = "Executando " + programa.getNomePrograma();
                        Log.gravarLog(quantum, log);

                        // marca o processo como em execução
                        programa.setEstadoProcesso("Executando");

                        // executa as instruções do processo até atingir o quantum ou ocorrer E/S/SAÍDA
                        instrucao.processaInstrucoes();

                        // atualiza estatísticas globais
                        instrucoesExecutadas += instrucao.buscaInstrucoesExecutadas();
                        trocas++; // Cada chamada é considerada uma troca de contexto

                        // log de interrupção após término do quantum
                        log = "Interrompendo " + programa.getNomePrograma() + " após " + instrucao.buscaInstrucoesExecutadas() + " instruções";
                        Log.gravarLog(quantum, log);

                        // processo movido novamente como pronto
                        programa.setEstadoProcesso("Pronto");

                        // move o processo do início para o fim da fila (Round Robin)
                        listaDeProntos.add(listaDeProntos.remove(0));

                        // tempo de espera dos processos bloqueados atualiza
                        decrementaTempoEspera_Bloqueados();
                    }
                } else {
                    // caso não tenha nenhum processo pronto, apenas decrementa o tempo dos bloqueados
                    decrementaTempoEspera_Bloqueados();
                }
            } catch (Exception e) {
                // tratamento de eventos especiais durante a execução de um quantum

                // se o processo requisitou entrada/saída, ele é movido para a lista de bloqueados
                if (e.getMessage().equals("E/S"))
                    executaES();

                // se o processo terminou (SAIDA), ele é removido da fila de prontos e da tabela
                if (e.getMessage().equals("SAIDA"))
                    executarSaida();

                // atualiza o tempo de espera dos bloqueados em ambos os casos
                decrementaTempoEspera_Bloqueados();
            } finally {
                // ao final de cada iteração, imprime o estado atual das filas e da tabela
                imprimirListaDeProntos();
                imprimirListaDeBloqueados();
                imprimirTabelaProcessos();
            }
        }
    }

    private void executaES(){
        BlocoControleProcesso programaES = listaDeProntos.get(0);

        log = "Interrompendo " + programaES.getNomePrograma() + " após " + instrucao.buscaInstrucoesExecutadas() + " instruções";
        System.out.println(log);
        Log.gravarLog(quantum, log);

        log = "E/S iniciada em " + programaES.getNomePrograma();
        System.out.println(log);
        Log.gravarLog(quantum, log);

        instrucoesExecutadas += instrucao.buscaInstrucoesExecutadas();
        trocas++;

        programaES.setTempoEspera(2);
        programaES.setEstadoProcesso("Bloqueado");

        //remove da fila de Prontos e coloca no final da fila de Bloqueados
        listaDeBloqueados.add(listaDeProntos.remove(0));		
    }

    private void executarSaida(){
        BlocoControleProcesso programaSaida = listaDeProntos.remove(0);

        int posicao = tabelaDeProcessos.indexOf(programaSaida);
        tabelaDeProcessos.remove(posicao);

        System.out.println(programaSaida.getNomePrograma() + " REMOVIDO da lista de PRONTOS e da TABELA PROCESSOS!");

        instrucoesExecutadas += instrucao.buscaInstrucoesExecutadas();
        trocas++;

        log = programaSaida.getNomePrograma() + " terminado. X=" + programaSaida.getRegistradorX() + ". Y=" + programaSaida.getRegistradorY();
        Log.gravarLog(quantum, log);
    }

    //	processo que passe pelo estado executando, todos na fila de bloqueados tem seu tempo decrementado
    private void decrementaTempoEspera_Bloqueados(){
        for(BlocoControleProcesso processo : listaDeBloqueados)
            processo.decrementaTempoEspera();

        retirarZerosBloqueados();
    }

    //	quando o tempo de espera de algum processo bloqueado chegar a zero
    private void retirarZerosBloqueados(){
        for(BlocoControleProcesso processo : listaDeBloqueados){
            if (processo.getTempoEspera() == 0){
                //	recebe o status de pronto, remove da fila de bloqueados e inserido ao final da fila de processos prontos
                int posicao = listaDeBloqueados.indexOf(processo);
                listaDeBloqueados.get(posicao).setEstadoProcesso("Pronto");
                listaDeProntos.add(listaDeBloqueados.remove(posicao));
                retirarZerosBloqueados();
            }

            if (listaDeBloqueados.size() == 0) return;
        }
    }

    // tempo de média de troca, intrução e programas
    public double buscaMediaTrocasContexto(){
        System.out.println("MEDIA DE TROCAS:  " + (trocas/totalProgramas) + " / QTD DE TROCAS: " + trocas + " / QTD PROGRAMAS: " + totalProgramas);
        return ((double) trocas/totalProgramas);
    }

    public double buscaMediaInstrucoes(){
        System.out.println("MEDIA DE INSTRUCOES:  " + (instrucoesExecutadas/trocas) + " / QTD DE INSTRUCOES: " + instrucoesExecutadas + " / QTD TROCAS: " + trocas);
        return ((double) instrucoesExecutadas/trocas);
    }

    // imprimir
    public void imprimirTabelaProcessos(){
        System.out.println("\n\n---- TABELA DE PROCESSOS");
        for(BlocoControleProcesso processo : tabelaDeProcessos)
            System.out.print(processo);
    }

    public void imprimirListaDeProntos(){
        System.out.println("\n\n---- LISTA DE PRONTOS");
        for(BlocoControleProcesso processo : listaDeProntos)
            System.out.print(processo);
    }

    public void imprimirListaDeBloqueados(){
        System.out.println("\n---- LISTA DE BLOQUEADOS");
        for(BlocoControleProcesso processo : listaDeBloqueados)
            System.out.print(processo);
    }

    public void imprimirLogFinal(){
        String log;

        log = "MEDIA DE TROCAS: " + buscaMediaTrocasContexto();
        Log.gravarLog(quantum, log);

        log = "MEDIA DE INSTRUCOES: " + buscaMediaInstrucoes();
        Log.gravarLog(quantum, log);

        log = "QUANTUM: " + quantum;
        Log.gravarLog(quantum, log);
    }
}