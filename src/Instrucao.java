public class Instrucao {
    BlocoControleProcesso blocoControle;
    int quantum;
    int contador;
    private int tempo, instrucoesExecutadas;

    public Instrucao(BlocoControleProcesso blocoControle, int quantum){
        this.blocoControle = blocoControle;
        this.quantum = quantum;
    }

    //executa os comandos
    public void processaInstrucoes() throws Exception{

        System.out.println("\n-------->>\n\t EXEC. PROGRAMA: " + blocoControle.getNomePrograma());

        instrucoesExecutadas = 0;

        for(int i = 0; i < quantum; i++){
            //instrucao a ser executada
            String instrucao = blocoControle.getlistaIntrucoesPrograma().get(blocoControle.getContador());

            System.out.println("\t\tINSTRUCAO EXECUTADA: " + instrucao);
            blocoControle.incrementaContador();
            instrucoesExecutadas++;

            if (instrucao.startsWith("X"))
                this.instrucaoX(Integer.parseInt(instrucao.substring(instrucao.indexOf("=")+1, instrucao.length())));

            if (instrucao.startsWith("Y"))
                this.instrucaoY(Integer.parseInt(instrucao.substring(instrucao.indexOf("=")+1, instrucao.length())));

            if (instrucao.equals("E/S")){
                if(contador == 0){
                    this.tempo = i+1;
                    this.contador++;
                }

                chamadaAoSO();
            }

            if (instrucao.equals("COM"))
                instrucaoCOM();

            if(instrucao.equals("SAIDA")){
                if(contador == 0){
                    this.tempo = i+1;
                    this.contador++;
                    System.out.println("\nTERMINOU ->>>> " + "NOME: " + blocoControle.getNomePrograma() + " TEMPO: " + (blocoControle.getTempoEspera()+this.tempo)+" \n");
                }

                throw new Exception("SAIDA");
            }
        }

        if(contador == 0) this.tempo = quantum;

        this.contador = 0;
    }

    //1° atribuicao no registrador
    public void instrucaoX(int x){
        blocoControle.setRegistradorX(x);
    }

    public void instrucaoY(int y){
        blocoControle.setRegistradorY(y);
    }

    //2° chamada ao SO
    public void chamadaAoSO() throws Exception{
        blocoControle.setEstadoProcesso("Bloqueado");
        throw new Exception("E/S");
    }

    //3° executado pela maquina, podendo executar um comando comum e simples
    public void instrucaoCOM(){}

    public boolean terminou(){
        return blocoControle.getContador() >= blocoControle.getlistaIntrucoesPrograma().size();
    }

    public int buscaTempo(){
        return this.tempo;
    }

    public int buscaInstrucoesExecutadas(){
        return instrucoesExecutadas;
    }
}