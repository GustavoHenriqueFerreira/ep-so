class Escalonador {
    public static void main(String[] args) {
        if(args.length == 0){
            executarEscalonador();
        }
        else{
            executarSimulador();
        }
    }

    private static void executarSimulador(){
        System.out.println("Iniciando simulacao...");

        Simulacao simula = new Simulacao();
        simula.simularEscalonador();
    }

    private static void executarEscalonador(){
        System.out.println("Iniciando escalonador...");
        LeituraPrograma.apagarLogs();

        GerenciadorEscalonador escalonador = new GerenciadorEscalonador("quantum.txt");

        escalonador.carregarProgramas();
        escalonador.executarRoundRobin();

        escalonador.imprimirLogFinal();
    }
}