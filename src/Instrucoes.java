public class Instrucoes {
    BlocoDeControleDeProcessos bcp;
    int quantum;
    int cont;
    private int tempo, instrucoesExecutadas;

    public Instrucoes(BlocoDeControleDeProcessos bcp, int quantum){
        this.bcp = bcp;
        this.quantum = quantum;
    }

    //Compara as instrucoes e executa os comandos
    public void processaInstrucoes() throws Exception{

        System.out.println("\n-------->>\n\t EXEC. PROGRAMA: " + bcp.getNomePrograma());

        instrucoesExecutadas = 0;

        for(int i = 0; i < quantum; i++){
            //Obtem a instrucao a ser executada
            String instrucao = bcp.getInstrucoesDoPrograma().get(bcp.getContador());

            System.out.println("\t\tINSTRUCAO EXECUTADA: " + instrucao);
            bcp.incrementaContador();
            instrucoesExecutadas++;

            if (instrucao.startsWith("X"))
                this.instrucaoX(Integer.parseInt(instrucao.substring(instrucao.indexOf("=")+1, instrucao.length())));

            if (instrucao.startsWith("Y"))
                this.instrucaoY(Integer.parseInt(instrucao.substring(instrucao.indexOf("=")+1, instrucao.length())));

            if (instrucao.equals("E/S")){
                if(cont == 0){
                    this.tempo = i+1;
                    this.cont++;
                }

                chamadaAoSO();
            }

            if (instrucao.equals("COM")) instrucaoCOM();

            if(instrucao.equals("SAIDA")){
                if(cont == 0){
                    this.tempo = i+1;
                    this.cont++;
                    System.out.println("\nTERMINOU ->>>> " + "NOME: " + bcp.getNomePrograma() + " TEMPO: " + (bcp.getTempoEspera()+this.tempo)+" \n");
                }

                throw new Exception("SAIDA");
            }
        }

        if(cont == 0) this.tempo = quantum;

        this.cont = 0;
    }

    //1° Instrucao de atribuicao no registrador
    public void instrucaoX(int x){
        bcp.setRegistradorX(x);
    }

    public void instrucaoY(int y){
        bcp.setRegistradorY(y);
    }

    //2° Instrucao Chamada ao SO
    public void chamadaAoSO() throws Exception{
        bcp.setEstadoProcesso("Bloqueado");
        throw new Exception("E/S");
    }

    //3° Tarefa executada pela maquina
    //Pode executar um comando comum e simples
    public void instrucaoCOM(){}

    public boolean terminou(){
        return bcp.getContador() >= bcp.getInstrucoesDoPrograma().size();
    }

    public int getTempo(){
        return this.tempo;
    }

    public int getInstrucoesExecutadas(){
        return instrucoesExecutadas;
    }
}