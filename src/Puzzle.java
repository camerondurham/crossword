import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.Scanner;
import java.util.Map;

public class Puzzle {
	public Shape board;
	public HashMap<String,Crossword> acrossWords;
	public HashMap<String,Crossword> downWords;
	public Vector<Crossword> acrossMoves;
	public Vector<Crossword> downMoves;
    public int acrossGuessed;
    public int downGuessed;
    public int remaining;


	public Puzzle(Puzzle p){
		this.board = p.board;
		this.acrossWords = new HashMap<String,Crossword>();
		this.downWords = new HashMap<String,Crossword>();
		this.acrossMoves = new Vector<Crossword>();
		this.downMoves = new Vector<Crossword>();
        this.acrossGuessed = 0;
        this.downGuessed = 0;

		for(Map.Entry<String,Crossword> entry : acrossWords.entrySet()){
			this.acrossWords.put(entry.getKey(), entry.getValue());
		}

		for(Map.Entry<String,Crossword> entry : downWords.entrySet()){
			this.downWords.put(entry.getKey(), entry.getValue());
		}

        this.remaining = this.acrossWords.size() + this.downWords.size();


		/*System.out.println("*******************init puzzle***********************");
		System.out.println(this.acrossWords.toString() + this.downWords.toString());
		System.out.println(this.acrossWords.toString() + this.toString());
		System.out.println(this.acrossWords.toString() + this.downWords.toString());
        */


		for(Crossword w : p.acrossMoves){
			this.acrossMoves.add(w);
		}

		for(Crossword w : p.downMoves){
			this.downMoves.add(w);
		}


	}

	public Puzzle(Shape board, HashMap<String,Crossword> acrossWords, HashMap<String,Crossword> downWords){
		this.board = board;
		this.acrossWords = acrossWords;
		this.downWords = downWords;
        this.remaining = this.acrossWords.size() + this.downWords.size();
		this.acrossMoves = new Vector<Crossword>();
		this.downMoves = new Vector<Crossword>();
	}
	public Puzzle(PuzzleLoader pl){
		this.board = pl.getBoard();
		this.acrossWords = pl.getAcross();
		this.downWords = pl.getDown();

        this.remaining = this.acrossWords.size() + this.downWords.size();

        Comparator<Crossword> c = new Comparator<Crossword>() {
			public int compare(Crossword a, Crossword b){
				if(a.getNum() == b.getNum()){
					return 0;
				}
				else if(a.getNum() < b.getNum()){
					return -1;
				} else {
					return 1;
				}
			}
		};
		this.acrossMoves = new Vector<Crossword>();
		this.downMoves = new Vector<Crossword>();

		for(Crossword w : pl.getAcrossWords()){
			this.acrossMoves.add(w);
		}

		for(Crossword w : pl.getDownWords()){
			this.downMoves.add(w);
		}

		this.acrossMoves.sort(c);
		this.downMoves.sort(c);
	}

	// MOVE FUNCTIONS
	public boolean isGuessed(Tile t) {
		return ((acrossWords.containsKey(t.getHorizontalWord()) && acrossWords.get(t.getHorizontalWord()).getGuessed())
				|| (downWords.containsKey(t.getVerticalWord()) && downWords.get(t.getVerticalWord()).getGuessed()));
	}


	public Vector<Crossword> generateAcrossMoves(){
		Vector<Crossword> vec = new Vector<Crossword>();
		for(Crossword word : acrossMoves){
			if(word.getGuessed() == false){
				vec.add(word);
			}
		}
		return vec;
	}

	public Vector<Crossword> generateDownMoves(){
		Vector<Crossword> vec = new Vector<Crossword>();
		for(Crossword word : downMoves){
			if(word.getGuessed() == false){
				vec.add(word);
			}
		}
		return vec;
	}


	public String promptPlayerString() {
		String playerString = this.toString();
		if(acrossWords.size() > 0 && acrossGuessed < acrossWords.size()){
			playerString += "\nAcross\n";
			for(Crossword word : acrossMoves){
				if(word.getGuessed() == false){
					playerString += word.getNum() + "  " + word.getQuestion() + "\n";
				}
			}
		}
		if(downWords.size() > 0 && downGuessed < downWords.size()){
			playerString += "\nDown\n";
			for(Crossword word : downMoves){
				if(word.getGuessed() == false){
					playerString += word.getNum() + "  " + word.getQuestion() + "\n";
				}
			}
		}
		return playerString;
	}


	public boolean isValidChoice(int numChoice, boolean across){
		if(across){

			for(Crossword c : acrossMoves){
				if(numChoice == c.getNum() && c.getGuessed() == false){
					return true;
				}
			}


		} else {

			for(Crossword c : downMoves){
				if(numChoice == c.getNum() && c.getGuessed() == false){
					return true;
				}
			}

		}
		return false;
	}
	public boolean isValidChoice(int numChoice, Vector<Crossword> vec){

		for(Crossword c : vec){
			if(numChoice == c.getNum() && c.getGuessed() == false){
				return true;
			}
		}
		return false;
	}

	public boolean validateMove(Move m){

		return  makeMove(m.getAcross(), m.getWordNumber(), m.getGuess());

	}

	public boolean makeMove(boolean across, int num, String guess){
		if(across){
			for(Crossword word : acrossMoves){
				if(word.getNum() == num && word.getAnswer().equalsIgnoreCase(guess)){
					acrossWords.get(word.getAnswer()).setGuessed(true);
                    acrossGuessed++;
                    remaining--;
					acrossMoves.remove(word);
					return true;
				}
			}
			return false;
		}
		else {
			for(Crossword word : downMoves){
				if(word.getNum() == num && word.getAnswer().equalsIgnoreCase(guess)){
					downWords.get(word.getAnswer()).setGuessed(true);
                    downGuessed++;
                    remaining--;
					downMoves.remove(word);
					return true;
				}
			}
			return false;
		}
	}



	// BOARD PRINTING FUNCTIONS
	@Override
	public String toString() {
		String boardString = "\n";
		for (int i = 0; i < this.board.getR(); i++) {
			for (int j = 0; j < this.board.getC(); j++) {
				if (board.getTile(i, j).isLetter()) {
					if (acrossWords.containsKey(board.getTile(i, j).getHorizontalWord())
							&& acrossWords.get(board.getTile(i, j).getHorizontalWord()).startsMatch(i, j)) {
						boardString +=  acrossWords.get(board.getTile(i, j).getHorizontalWord()).getNum();
					} else if (downWords.containsKey(board.getTile(i, j).getVerticalWord())
							&& downWords.get(board.getTile(i, j).getVerticalWord()).startsMatch(i, j)) {
						boardString += downWords.get(board.getTile(i, j).getVerticalWord()).getNum();
					} else {
						boardString += " ";
					}
					if (isGuessed(board.getTile(i, j))) {
						boardString += board.getTile(i,j).ltr.toUpperCase() + " ";
					} else {
						boardString += "_  ";
					}
				} else {
					boardString += "   ";
				}
			}
			boardString+= "\n";
		}
		return boardString;
	}


	public void printBoard() {
		System.out.println();
		for (int i = 0; i < this.board.getR(); i++) {
			for (int j = 0; j < this.board.getC(); j++) {

				if (board.getTile(i, j).isLetter()) {
					if (acrossWords.containsKey(board.getTile(i, j).getHorizontalWord())
							&& acrossWords.get(board.getTile(i, j).getHorizontalWord()).startsMatch(i, j)) {
						System.out.printf("%d", acrossWords.get(board.getTile(i, j).getHorizontalWord()).getNum());
					} else if (downWords.containsKey(board.getTile(i, j).getVerticalWord())
							&& downWords.get(board.getTile(i, j).getVerticalWord()).startsMatch(i, j)) {
						System.out.printf("%d", downWords.get(board.getTile(i, j).getVerticalWord()).getNum());
					} else {
						System.out.printf(" ");
					}
					if (isGuessed(board.getTile(i, j))) {
						System.out.printf("%1s ", board.getTile(i, j).ltr);
					} else {
						System.err.print("_  ");
					}
				} else {
					System.out.printf("   ");
				}
			}
			System.out.println();
		}
	}


	public static void main(String args[]){
		Scanner sc = new Scanner(System.in);

		PuzzleLoader pl = new PuzzleLoader();
		pl.getFile("../gamedata/gamedata2.txt");
		pl.generateBoard();
		Puzzle pz = new Puzzle(pl);

		pz.printBoard();
		System.out.println(pz.promptPlayerString());
		Vector<Crossword> across = pz.generateAcrossMoves();
		Vector<Crossword> down = pz.generateDownMoves();

		String line;
		boolean getMoveType = true;
		boolean getMoveNumber  = false;
		boolean getAnswer  = false;
		boolean acrossChoice = false;
		int numChoice = 1;
		String guess = "";

		while(getMoveType || getMoveNumber || getAnswer){
			if(getMoveType){

				System.out.print("\nWould you like to answer a question across (a) or down (d)?");
				line = Util.readInput(sc);
				System.out.println("Read line: " + line);

				if(line.equals("a") || line.equals("d")){
					acrossChoice = line.equals("a");
					getMoveType = false;
					getMoveNumber = true;
				}
				else  {
					System.out.println("That is not a valid option.");
				}
			}
			else if (getMoveNumber) {

				System.out.print("\nWhich number? ");
				line = Util.readInput(sc);
				System.out.println("Read line: " + line);

				try {
					numChoice = Util.readInt(line);
					System.out.println("Read int: " + numChoice);
				} catch (Exception e){
					System.out.println("That is not a valid option.");
				}

				if(acrossChoice && pz.isValidChoice(numChoice, across)){
					getMoveNumber = false;
					getAnswer = true;

				} else if( !acrossChoice && pz.isValidChoice(numChoice, down)){
					getMoveNumber = false;
					getAnswer = true;
				}

			} else {
				String acrossString = (acrossChoice ? "across" : "down");
				System.out.printf("\nWhat is your guess for %d %s?  ",numChoice,acrossString);
				guess = Util.readInput(sc);
				System.out.println("Read line: " + guess);
				getAnswer = false;
			}

		}

		System.out.printf("\nRead in choices: across?: %b number?: %d answer?: %s\n",acrossChoice, numChoice, guess);
		Move m = new Move(0, numChoice, acrossChoice, guess);
		System.out.println("Valid Move? " + pz.validateMove(m) );
		pl.printBoard();
	}
}
