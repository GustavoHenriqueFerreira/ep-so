import java.io.*;

public class Log{

    //////////////////////////////////
    //		   	GRAVAÇÃO	            //
    //////////////////////////////////
    static void gravarArquivoLog(int quantum, String log){
        String numQuantum = quantum < 10 ? "0" + quantum : Integer.toString(quantum);
        String arquivoLog = "log" + numQuantum + ".txt";

        try{
            RandomAccessFile arq = new RandomAccessFile(arquivoLog, "rw");

            Writer csv = new BufferedWriter(new FileWriter(arquivoLog, true));
            csv.append(log + "\n");
            csv.close();
        }
        catch (Exception e){
            System.out.println("!!! ERRO NA GRAVACAO DO LOG !!!");
            e.printStackTrace();
        }
    }
}