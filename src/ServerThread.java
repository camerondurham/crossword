import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.io.IOException;

public class ServerThread extends Thread {
    private BufferedReader br;
    private PrintWriter pw;
    private ReentrantLock lock;
    private Condition condition;
    private Server s;
    private boolean firstPlayer;
    public PuzzleLoader pl;
    private String line;
    private int numPlayers = 0;
    public int index;
    public volatile boolean exit = false;
    public ServerThread(Socket socket, Server s, ReentrantLock lock, Condition condition, int index){

        // Set game server
        this.s = s;
        try {
            this.br = new BufferedReader(new InputStreamReader( socket.getInputStream()));
            this.pw = new PrintWriter(socket.getOutputStream());
            this.lock = lock;
            this.condition = condition;
            this.firstPlayer = (index == 0);
            this.index = index;
            if(this.firstPlayer){

                boolean readingData = true;
                boolean waitingToStart = true;
                System.out.println("Beginning session for first player" );

                while( waitingToStart && !exit ){

                    try {
                        if (numPlayers == 0) {
                            System.out.println("How many players in the game? " );
                            pw.println("How many players in the game? " );
                            pw.flush();
                            line = br.readLine();
                            //numPlayers = Util.readInt(line);
                           	numPlayers = Integer.parseInt(line);
                            s.setNumPlayers(numPlayers);
                            readingData = false;
                            pl = new PuzzleLoader();

                            // check if user has valid game file and set
                            pl.findValidFile();
                            readingData = false;
                            waitingToStart = false;

                        }
                    } catch (Exception e) {
                        sendMessage("That is not a valid input." + e.getMessage());
                    }
                }
            }
            this.start();

        }
        catch (IOException ioe){
            System.out.println("IOException in ServerThread ioe: " + ioe.getMessage());
        }

    }

    public void run() {
        try {
            lock.lock();
            if(this.firstPlayer && s.counter < numPlayers){

                System.out.println("Waiting for other clients to connect...");
                sendMessage("Waiting for other clients to connect...");
                while(s.counter < numPlayers){}
                if(s != null && s.puzzle != null){
                    sendMessage(s.puzzle.promptPlayerString());
                }

            }
            else if(!firstPlayer && s.gameLoaded == false){
                pl = new PuzzleLoader();
                pl.findValidFile(); // returns if valid file found
                condition.await();
            } else if(!this.firstPlayer) {
                condition.await();

            }

            while (!exit) {

                if(s.puzzle.remaining == 0){
                    s.endGame();
                    this.terminate();
                }


                sendMessage("\nWould you like to answer a question across (a) or down (d)? ");

                boolean getMoveType = true;
                boolean getMoveNumber  = false;
                boolean getAnswer  = false;
                boolean acrossChoice = false;
                boolean readInput = true;
                int numChoice = 1;
                String guess = "";


                while(readInput && !exit){
                    line = br.readLine();

                    if(line.equals("a")|| line.equals("d")){
                        acrossChoice = line.equals("a");
                        readInput = false;
                    } else {
                        sendMessage("\nThat is not a valid option.\nWould you like to answer a question across (a) or down (d)?  ");
                    }

                }

                readInput = true;
                sendMessage("\nWhich number?");

                while(readInput && !exit){
                    try {
                        line = br.readLine();
                        numChoice = Util.readInt(line);

                        if(s.puzzle.isValidChoice(numChoice, acrossChoice)){
                            getMoveNumber = false;
                            getAnswer = true;
                            readInput = false;
                        }
                        else {
                            sendMessage("That is not a valid choice.\nWhich number? ");
                        }
                    } catch(Exception e){
                        sendMessage("That is not a valid option\nWhich number? ");
                    }

                }

                readInput = true;
                String acrossString = (acrossChoice ? "across": "down");
                sendMessage("\nWhat is your guess for " + numChoice +" " + acrossString);

                while(readInput && !exit){

                    line = br.readLine();
                    guess = line;
                    if(guess == null ||guess.trim().length() == 0)
                    {
                        sendMessage("\nWhat is your guess for " + numChoice + " " + acrossString + "?");
                    } else {

                        getAnswer = false;
                        readInput = false;
                    }

                }

                Move m = new Move(index, numChoice, acrossChoice, guess);


                boolean correct = s.validateMove(m);
                s.broadcast("Player " + (index + 1)+ " guessed \"" + guess + "\" for " + numChoice +  " "  + acrossString + ".", this );

                if(correct){
                    sendMessage("That is correct!");
                    s.broadcast("That is correct.", this);
                    sendMessage(s.puzzle.toString() + "\n"+ s.puzzle.promptPlayerString());
                    s.broadcast("Player " + (index + 1) + "\'s turn.", this);
                } else {
                    s.broadcast("That is incorrect.", this);
                    sendMessage("That is incorrect!");
                    s.nextPlayerTurn();
                    condition.await();
                }



                if(s.puzzle.remaining == 0){
                    s.endGame();
                    this.terminate();
                }
            }

        } catch (IOException ioe){
            System.out.println("IOException in ServerThread ioe: " + ioe.getMessage());
        } catch (InterruptedException ie){
            System.out.println("InterruptedException in ServerThread ioe: " + ie.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void terminate(){
        this.exit = true;
    }

    public void sendMessage(String message) {
        pw.println(message);
        pw.flush();
    }
}
