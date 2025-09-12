public class Simulacao {
    public void simulaEscalonador(){
        LeituraPrograma.apagarLogs();

        //Tentar diferentes quantuns
        for(int i = 1; i <= 21; i++){
            System.out.println("\n\n\n********** INICIANDO SIMULACAO ************* QUANTUM = " + i);

            GerenciadorEscalonador escalonador = new GerenciadorEscalonador(i);

            escalonador.carregandoProgramas();
            escalonador.executaRoundRobin();

            escalonador.logFinal();

            System.out.println("********** FINALIZANDO SIMULACAO ************* QUANTUM = " + i);
        }
    }
}