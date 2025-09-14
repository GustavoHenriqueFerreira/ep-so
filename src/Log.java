import java.io.*;

public class Log{
    static void gravarLog(int quantum, String log){
        String numeroDeQuantum = "0" + Integer.toString(quantum);

        if (quantum >= 10) {
            numeroDeQuantum = Integer.toString(quantum);
        }

        String arquivo = "log" + numeroDeQuantum + ".txt";

        try{
            RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");

            Writer csv = new BufferedWriter(new FileWriter(arquivo, true));
            csv.append(log + "\n");
            csv.close();
        }
        catch (Exception e){
            System.out.println("Erro na Gravacao");
            e.printStackTrace();
        }
    }
}