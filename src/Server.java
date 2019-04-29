import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;

public class Server {

    private Vector<ServerThread> serverThreads;
    private Vector<Lock> playerLocks;
    private Vector<Condition> playerConditions;

    public volatile int counter;
    private int index;
    public Puzzle puzzle;
    private boolean firstPlayer;
    private volatile int numPlayersLimit;
    public boolean gameLoaded;
    public boolean connectPlayers;
    public volatile boolean playingGame;
    public ArrayList<Integer> scoreboard;


    public Server(int port) {
        beginGame(port);
    }

    public void beginGame(int port ){

        ServerSocket ss = null;
        counter = 0;
        index = 0;
        gameLoaded = false;
        numPlayersLimit = 0;
        connectPlayers = true;
        playingGame = false;
        scoreboard = new ArrayList<Integer>();
        try {
            System.out.println("Listening on port " + port + ".");
            ss = new ServerSocket(port);

            // listen for player connections
            System.out.println("Waiting for players...");
            serverThreads  = new Vector<ServerThread>();
            playerLocks = new Vector<Lock>();
            playerConditions = new Vector<Condition>();
            puzzle = null;
            firstPlayer = true;

            while (connectPlayers) {

                // accept new socket connection
                Socket s = ss.accept();
                System.out.println("Connection from " + s.getInetAddress());

                firstPlayer = counter == 0;

                // initialize new player

                // create new locks
                ReentrantLock lock = new ReentrantLock();
                // create new condition
                Condition condition = lock.newCondition();
                // add new connection to players
                ServerThread st = new ServerThread(s, this, lock, condition, counter);

                while(this.numPlayersLimit == 0){} // wait for num players to be set
                System.out.println("Number of players: " + numPlayersLimit);
                // list number of players currently waiting on...
                if(this.numPlayersLimit > counter + 1) {
                    System.out.println("Waiting on player " + (counter + 2));
                }

                if(gameLoaded == false){
                    System.out.println("Reading random game file.");
                    st.pl = new PuzzleLoader();
                    st.pl.findValidFile();
                    if(st.pl.foundValidGameFile()){

                        gameLoaded = true;

                        // get puzzle from thread
                        st.pl.generateBoard();


                        this.puzzle = new Puzzle(st.pl);

                        if(this.puzzle != null) {
                            System.out.println("File read successfully.");
                            gameLoaded = true;
                            puzzle.printBoard();
                            this.broadcast(puzzle.toString(), null);
                            printMessage(puzzle.toString(), st);
                        }

                        else {
                            System.out.println("No file read successfully.");
                        }
                    }
                    else {
                        System.out.println("No file read successfully.");
                        if(this.numPlayersLimit == counter && !gameLoaded){
                            this.broadcast("Cannot begin game: invalid game file", null);
                            System.exit(1);
                            return;
                        }
                    }
                }

                playerLocks.add(lock);
                playerConditions.add(condition);
                serverThreads.add(st);
                this.scoreboard.add(0);
                counter++;
                System.out.println("Connected Players: " + counter);
                connectPlayers = (this.numPlayersLimit == 0 || this.numPlayersLimit > counter);
            }  // stop connecting clients and begin game

            if(this.numPlayersLimit == counter && !gameLoaded){
                this.broadcast("Cannot begin game: invalid game file", null);
                System.exit(1);
                return;
            }

            System.out.println("Game can now begin.");

            // IDK what to do here yet
            while(playingGame){
                // do gameplay stuff

            }


        } catch (IOException ioe ){
            System.out.println("ioe: " + ioe.getMessage());
        } catch (Exception e){

        }finally {
            try {
                if(ss != null)
                    ss.close();
            } catch (IOException ioe){
                System.out.println("Closing ioe: " + ioe.getMessage());
            }
        }

    }

    public void printMessage(String s, ServerThread st){
        st.sendMessage(s);
    }



    public void broadcast(String message, ServerThread currentST) {

        // broadcast updates to all clients playing the game
        if ( message != null && message.trim().length() > 0 ){
            System.out.println(message);
            for(ServerThread st : serverThreads){
                if ( st != currentST && st != null) {
                    st.sendMessage(message);
                }
            }
        }

    }


    public  void nextPlayerTurn() {
        //System.out.println("Sending game board.");
        //broadcast(puzzle.toString(), null);
        if(puzzle.remaining > 0){
            index = ( index + 1 ) % serverThreads.size();
            playerLocks.get(index).lock();
            playerConditions.get(index).signal();
            serverThreads.get(index).sendMessage(this.puzzle.promptPlayerString());
            playerLocks.get(index).unlock();
            System.out.println("Scores : " + scoreboardToString());
        }
        else {
        }
    }

    public void endGame(){
        System.out.println("The game has concluded.");
        System.out.println("Sending scores.");
        int winner = 0;
        for(int i  = 0;i < scoreboard.size(); i++){
            if(scoreboard.get(i) > scoreboard.get(winner)){
                winner = i;
            }
        }

        broadcast("Final Score" + scoreboardToString() + "\nPlayer " + (winner+1) + " is the winner.", null);
        System.out.println("Waiting for players...");
        beginGame(3456);
    }

    public boolean validateMove(Move m){
        boolean correct = this.puzzle.validateMove(m);
        if(correct){
            System.out.println("setting to new value");
            this.scoreboard.set(m.getPlayerNumber(), this.scoreboard.get(m.getPlayerNumber()) + 1);
        }
        return correct;
    }

    public String scoreboardToString(){
        String scores = "\n";
        for(int i  = 0; i < scoreboard.size(); i++){
            scores += "Player " + (i+1) + " - " + scoreboard.get(i) + " correct answers.\n";
        }
        return scores;
    }

    public void setNumPlayers(int n){
        this.numPlayersLimit = n;
    }

    public boolean numPlayersLimitSet(){
        return (this.numPlayersLimit == 0);
    }

    public static void main(String[] args) {
        Server server = new Server(3456);

    }
}
