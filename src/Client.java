import java.io.BufferedReader;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client extends Thread {

    private BufferedReader br;
    private PrintWriter pw;
    public int playerNumber;
    public volatile boolean runningGame;
    public Scanner scan;

    public Client(String hostname, int port, Scanner sc){

        Socket s = null;
        playerNumber = 0;
        runningGame = true;
        //Scanner scan = null;
        
        try {
            //System.out.println("Trying to connect to " + hostname + ":" + port);
            s = new Socket(hostname, port);			// exception may occur
            System.out.println("Connected to " + hostname + ":" + port);
            this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.pw = new PrintWriter(s.getOutputStream());
            this.start();
            //scan = new Scanner(System.in);
            scan = sc;
            while(true) {
                String line = scan.nextLine();
                pw.println(line);
                pw.flush();
            }
        } catch (IOException ioe) {

        } finally {
            if(scan != null) scan.close();
            try {
                if (s != null) s.close();
            } catch (IOException ioe){
                System.out.println("<Client terminates>");
            }
        }
    }

    public void run() {
        try {
            while(runningGame){
                String line = br.readLine();
                System.out.println(line);

                if(line == null || line.contains("winner") || line.contains("invalid game file")){
                    System.out.println("<Client terminates>");
                    System.in.close();
                    runningGame = false;
                    System.exit(0);
                    break;
                }
            }
        } catch( EOFException eof ) {
            System.out.println("Client closed the connection.");
            runningGame = false;
        }
        catch (IOException ioe) {
            //System.out.println("ioe reading from server: " + ioe.getMessage());
            System.out.println("ERROR: cannot connect you to server, game room is full.");
            runningGame = false;
        }
        return;
    }

    public static void main(String[] args){
        boolean readingInput = true;
        String hostname = "";
        int port = 0;

        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to 201 Crossword!");


        while(readingInput){
            if(hostname == ""){
                System.out.print("Enter the server hostname: ");
                hostname = sc.nextLine();
                //hostname = Util.readLine();
            } else if (port == 0){
                System.out.print("Enter the server port number: ");
                //port = Util.readNumber();
                String line = sc.nextLine();
                try{
                    port = Integer.parseInt(line);
                } catch (Exception e){
                    System.out.println("Cannot convert -- error: " + e.getMessage());
                }
            } else {
                readingInput = false;
            }
        }


        //Client pc = new Client("localhost", 3456);
        Client pc = new Client(hostname, port, sc);

    }
}
