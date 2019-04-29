import java.util.Collection;

public class PuzzleMaker {
    private PuzzleLoader pl;
    private Collection<Crossword> acrossWords;
    private Collection<Crossword> downWords;
    
    public static int firstCharMatch(String placed, String matching){
			int first_match = -1;
			for(int i = 0; i < matching.length(); i++){
				if( placed.contains(matching.subSequence(i, i+1))) {
					first_match = i;
					break;
				}
			}
			return first_match;
		}

    public PuzzleMaker(PuzzleLoader pl){
        this.pl = pl;
        this.acrossWords = pl.getAcrossWords();
        this.downWords = pl.getDownWords();
    }

    public static void main(String[] args){

    }

    
}