import java.util.List;
import java.util.ArrayList;

public class GerenciadorEscalonador {
    //////////////////////////////////////////////
    //             ATRIBUTOS CLASSE             //
    //////////////////////////////////////////////
    private int quantum, instrucoesExecutadas, trocas, totalProgramas;
    private Instrucoes instrucao = null;
    private String log;

    //Instancia de List com objetos do tipo Processos
    protected List<BlocoDeControleDeProcessos> listaDeProntos = new ArrayList<>();
    protected List<BlocoDeControleDeProcessos> listaDeBloqueados = new ArrayList<>();

    //A tabela de processos possui um ponteiro para o BCP.
    protected List<BlocoDeControleDeProcessos> tabelaDeProcessos = new ArrayList<>();

    //////////////////////////////////////////////
    //            	 CONSTRUTORES 	            //
    //////////////////////////////////////////////
    public GerenciadorEscalonador(int quantum){
        this.quantum = quantum;
    }

    public GerenciadorEscalonador(String quantumFile){
        this.quantum = LeituraPrograma.getQuantumFile(quantumFile);
    }

    //////////////////////////////////////////////
    //             			MÉTODOS  			//
    //////////////////////////////////////////////

    /**
     * Metodos para gerenciar os processos e determinar seus estados
     * Usar o algoritmo de Round-Robin, dividir o processador de acordo
     * com a quantidade do Quantum.
     */
    public void carregandoProgramas(){
        tabelaDeProcessos = LeituraPrograma.programasLidos();
        totalProgramas = tabelaDeProcessos.size();

        //Inicializa todos na lista de Prontos
        for(BlocoDeControleDeProcessos processo : tabelaDeProcessos){

            listaDeProntos.add(processo);

            String log = "Carregando " + processo.getNomePrograma();
            Log.gravarArquivoLog(quantum, log);
        }
    }

    // Metodo principal do Escalonador Round Robin
    public void executaRoundRobin() {
        System.out.println("INICIANDO ROUND ROBIN - QUANTUM = " + quantum);

        // Enquanto ainda houver processos prontos ou bloqueados, o escalonador continua ativo
        while (listaDeProntos.size() > 0 || listaDeBloqueados.size() > 0) {
            try {
                // Caso exista algum processo pronto para execução
                if (listaDeProntos.size() > 0) {
                    // Seleciona o primeiro processo da fila de prontos
                    BlocoDeControleDeProcessos programa = listaDeProntos.get(0);

                    // Cria uma nova "instrução" a ser executada pelo processo, limitada pelo quantum
                    instrucao = new Instrucoes(programa, quantum);

                    // Verifica se o processo já terminou antes de executar
                    if (!instrucao.terminou()) {
                        // Log de início da execução
                        log = "Executando " + programa.getNomePrograma();
                        Log.gravarArquivoLog(quantum, log);

                        // Marca o processo como em execução
                        programa.setEstadoProcesso("Executando");

                        // Executa as instruções do processo até atingir o quantum ou ocorrer E/S/SAÍDA
                        instrucao.processaInstrucoes();

                        // Atualiza estatísticas globais
                        instrucoesExecutadas += instrucao.getInstrucoesExecutadas();
                        trocas++; // Cada chamada é considerada uma troca de contexto

                        // Log de interrupção após término do quantum
                        log = "Interrompendo " + programa.getNomePrograma() + " após " + instrucao.getInstrucoesExecutadas() + " instruções";
                        Log.gravarArquivoLog(quantum, log);

                        // Coloca o processo novamente como pronto
                        programa.setEstadoProcesso("Pronto");

                        // Move o processo do início para o fim da fila (Round Robin clássico)
                        listaDeProntos.add(listaDeProntos.remove(0));

                        // Atualiza o tempo de espera dos processos bloqueados
                        decrementaTempoEspera_Bloqueados();
                    }
                } else {
                    // Se não há nenhum processo pronto, apenas decrementa o tempo dos bloqueados
                    decrementaTempoEspera_Bloqueados();
                }
            } catch (Exception e) {
                // Tratamento de eventos especiais durante a execução de um quantum

                // Se o processo requisitou entrada/saída, ele é movido para a lista de bloqueados
                if (e.getMessage().equals("E/S"))
                    executaES();

                // Se o processo terminou (SAIDA), ele é removido da fila de prontos e da tabela
                if (e.getMessage().equals("SAIDA"))
                    executaSaida();

                // Atualiza o tempo de espera dos bloqueados em ambos os casos
                decrementaTempoEspera_Bloqueados();
            } finally {
                // Ao final de cada iteração, imprime o estado atual das filas e da tabela
                imprimeListaDeProntos();
                imprimeListaDeBloqueados();
                imprimeTabelaProcessos();
            }
        }
    }

    private void executaES(){
        BlocoDeControleDeProcessos programaES = listaDeProntos.get(0);

        log = "Interrompendo " + programaES.getNomePrograma() + " após " + instrucao.getInstrucoesExecutadas() + " instruções";
        System.out.println(log);
        Log.gravarArquivoLog(quantum, log);

        log = "E/S iniciada em " + programaES.getNomePrograma();
        System.out.println(log);
        Log.gravarArquivoLog(quantum, log);

        instrucoesExecutadas += instrucao.getInstrucoesExecutadas();
        trocas++;		//toda vez que chama o processaInstrucao e uma troca de contexto

        programaES.setTempoEspera(2);
        programaES.setEstadoProcesso("Bloqueado");

        listaDeBloqueados.add(listaDeProntos.remove(0));		//Remove da fila de Prontos e coloca no final da fila de Bloqueados
    }

    private void executaSaida(){
        BlocoDeControleDeProcessos programaSaida = listaDeProntos.remove(0);

        int posicao = tabelaDeProcessos.indexOf(programaSaida);
        tabelaDeProcessos.remove(posicao);

        System.out.println(programaSaida.getNomePrograma() + " REMOVIDO da lista de PRONTOS e da TABELA PROCESSOS!");

        instrucoesExecutadas += instrucao.getInstrucoesExecutadas();
        trocas++;

        log = programaSaida.getNomePrograma() + " terminado. X=" + programaSaida.getRegistradorX() + ". Y=" + programaSaida.getRegistradorY();
        Log.gravarArquivoLog(quantum, log);
    }

    //	3 (c) A cada processo que passe pelo estado executando,
    //	todos na fila de bloqueados tem seu tempo decrementado
    private void decrementaTempoEspera_Bloqueados(){
        for(BlocoDeControleDeProcessos processo : listaDeBloqueados)
            processo.decrementaTempoEspera();

        retiraZerados_Bloqueados();
    }

    //	3 (e) - Quando o tempo de espera de algum processo bloqueado chegar a zero
    private void retiraZerados_Bloqueados(){
        for(BlocoDeControleDeProcessos processo : listaDeBloqueados){
            if (processo.getTempoEspera() == 0){
                //	recebe o status de pronto, remove da fila de bloqueados e inserido ao final da fila de processos prontos

                int posicao = listaDeBloqueados.indexOf(processo);
                listaDeBloqueados.get(posicao).setEstadoProcesso("Pronto");
                listaDeProntos.add(listaDeBloqueados.remove(posicao));
                retiraZerados_Bloqueados();
            }

            if (listaDeBloqueados.size() == 0) return;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //            CALCULAR O TEMPO: MÉDIAS DE TROCAS, INSTRUCOES E PROGRAMAS           //
    /////////////////////////////////////////////////////////////////////////////////////
    public double getMediaTrocasContexto(){
        System.out.println("MEDIA DE TROCAS:  " + (trocas/totalProgramas) + " / QTD DE TROCAS: " + trocas + " / QTD PROGRAMAS: " + totalProgramas);
        return ((double) trocas/totalProgramas);
    }

    public double getMediaInstrucoes(){
        System.out.println("MEDIA DE INSTRUCOES:  " + (instrucoesExecutadas/trocas) + " / QTD DE INSTRUCOES: " + instrucoesExecutadas + " / QTD TROCAS: " + trocas);
        return ((double) instrucoesExecutadas/trocas);
    }

    //////////////////////////////////////////////
    //              IMPRESSÃO	                  //
    //////////////////////////////////////////////
    public void imprimeTabelaProcessos(){

        System.out.println("\n\n---- TABELA DE PROCESSOS");
        for(BlocoDeControleDeProcessos processo : tabelaDeProcessos)
            System.out.print(processo);

    }

    public void imprimeListaDeProntos(){

        System.out.println("\n\n---- LISTA DE PRONTOS");
        for(BlocoDeControleDeProcessos processo : listaDeProntos)
            System.out.print(processo);

    }

    public void imprimeListaDeBloqueados(){

        System.out.println("\n---- LISTA DE BLOQUEADOS");
        for(BlocoDeControleDeProcessos processo : listaDeBloqueados)
            System.out.print(processo);

    }

    //////////////////////////////////////////////
    //             			LOG                 //
    //////////////////////////////////////////////
    public void logFinal(){
        String log;

        log = "MEDIA DE TROCAS: " + getMediaTrocasContexto();
        Log.gravarArquivoLog(quantum, log);

        log = "MEDIA DE INSTRUCOES: " + getMediaInstrucoes();
        Log.gravarArquivoLog(quantum, log);

        log = "QUANTUM: " + quantum;
        Log.gravarArquivoLog(quantum, log);
    }
}