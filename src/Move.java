public class Move {
	private int playerNumber;
	private int wordNumber;
	private boolean across;
	private String guess;

	public Move( int playerNumber, int wordNumber, boolean across, String guess){
		this.playerNumber = playerNumber;
		this.wordNumber = wordNumber;
		this.across = across;
		this.guess = guess;
	}

	public int getPlayerNumber() {
		return this.playerNumber;
	}

	public int getWordNumber() {
		return this.wordNumber;
	}

	public boolean getAcross() {
		return this.across;
	}
	public String getGuess() {
		return this.guess;
	}

}
