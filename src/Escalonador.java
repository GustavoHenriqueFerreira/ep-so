class Escalonador {
    public static void main(String[] args) {
        if(args.length == 0){
            executaEscalonador();
        }
        else{
            executaSimulador();
        }
    }

    private static void executaSimulador(){
        System.out.println("Iniciando simulacao...");

        Simulacao simula = new Simulacao();
        simula.simulaEscalonador();
    }

    private static void executaEscalonador(){
        System.out.println("Iniciando escalonador...");
        LeituraPrograma.apagarLogs();

        GerenciadorEscalonador escalonador = new GerenciadorEscalonador("quantum.txt");

        escalonador.carregandoProgramas();
        escalonador.executaRoundRobin();

        escalonador.logFinal();
    }
}