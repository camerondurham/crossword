import java.util.Vector;

public class Tile {
    private int row, col;           // row and column number
    private boolean horiz;          // part of horizontal word, void if a start and end
    public String ltr;              // string of letter on square: space, letter, or empty
    private boolean start, end;     // is start letter or end letter
                                    //  intersection if  start && end
    private boolean canIntersect;
    private String verticalWord = "";
    private String horizontalWord = "";

    // Constructor
    public Tile(String ltr, int row, int col, boolean horiz, boolean start, boolean end, String verticalWord, String horizontalWord){
        this.row = row;
        this.col = col;
        this.ltr = ltr;
        this.horiz = horiz;
        this.start = start;
        this.end = end;
        this.canIntersect = true;
        this.verticalWord = verticalWord;
        this.horizontalWord = horizontalWord;

    }
    public Tile(Tile other){

        this.row = other.row;
        this.col = other.col;
        this.ltr = other.ltr;
        this.horiz = other.horiz;
        this.start = other.start;
        this.end = other.end;
        this.canIntersect = other.canIntersect;
        this.verticalWord = other.verticalWord;
        this.horizontalWord = other.horizontalWord;
    }

    // Getters
    public int getR(){
        return this.row;
    }

    public int getC(){
        return this.col;
    }

    public String getLtr(){
        if(ltr == " " || ltr == ""){
            // return "☐";
            return "_";
        }
        return ltr.toUpperCase();
    }

    public boolean isHoriz(){
        return this.horiz;
    }
    public boolean isStart(){
        return this.start;
    }
    public boolean isEnd(){
        return this.end;
    }
    public boolean isLetter(){
        return (this.ltr != null) && (this.ltr != "") && (this.ltr != " ");
    }

    public String getVerticalWord(){
        return this.verticalWord;
    }
    public String getHorizontalWord() {
        return this.horizontalWord;
    }

    // Setters

    public void setHoriz(boolean horiz){
        this.horiz = horiz;
    }
    public void makeEnd() {
        this.end = true;
    }
    public void makeStart(){
        this.start = true;
    }

    public void setLtr(String ltr){
        this.ltr = ltr;
    }
    public void setTileProps(Tile t){
        this.ltr = t.ltr;
        this.horiz = t.horiz;
        this.start = t.start;
        this.end = t.end;
        this.canIntersect = t.canIntersect;
        this.verticalWord = t.verticalWord;
        this.horizontalWord = t.horizontalWord;

    }

    public void setHorizontalWord(String s){
        this.horizontalWord = s;
    }
    public void setVerticalWord(String s){
        this.verticalWord = s;
    }

    // Compare functions

    public boolean compOverlap(Tile t){
        // System.out.printf("\n**************************COMPOVERLAP***************************\n");

        //System.out.printf("Comparing A %s \t TO B: %s", this.toString(), t.toString());
	if(t.isLetter() && !this.isLetter()){
		return true;
	}
        if(t.isLetter() && ( (t.horiz == this.horiz) || (this.ltr.compareToIgnoreCase(t.ltr) != 0))) {
	//	System.out.printf("\nRETURNING FALSE COMP OVERLAP \nt.horiz == this.horiz : %b  (unequal letters): %b\n",t.horiz == this.horiz, (this.ltr.compareToIgnoreCase(t.ltr) != 0 ));
            return false;
        }
        return true;
    }

    // set other word for overlapping tiles
    public void setOtherWord(Tile other){
        if(this.horiz){
            this.verticalWord = other.verticalWord;
        }
        else {
            this.horizontalWord = other.horizontalWord;
        }
    }

    public boolean isHorizontalStart(){
        return start && ltr.equalsIgnoreCase(horizontalWord.substring(0, 1)) ;
    }
    public boolean isVerticalStart(){
        return start && ltr.equalsIgnoreCase(verticalWord.substring(0,1)) ;
    }

    @Override
    public String toString(){
        return  String.format("[ ltr: %s, (%d,%d), horiz: %b, start: %b, end: %b]", this.ltr, this.row, this.col, this.horiz, this.start, this.end);
    }

    public boolean compOthers(Vector<Tile> others, boolean inIntersect){
        // System.out.printf("\n**************************COMPOTHERS***************************\n");
        for(Tile other : others){

            //System.out.printf("Comparing A \n %s inIntersect?: %b \nTO B: %s\n", this.toString(), inIntersect, other.toString());

            if((this.start && this.end) && other.isLetter() ){
                return false;
            }
            if( (this.start || this.end) && (other.start || other.end) && !inIntersect){
                return false;
            }
  	    if( (other.start || other.end) && this.isLetter() && !inIntersect){ // ADDED THIS
  		    return false;
  	    }

            if( (this.start || this.end) && this.isLetter() &&(!other.start && !other.end) && !inIntersect){ // ADDED THIS
        	    return false;
            }
            else if ((this.horiz == other.horiz) && this.isLetter()){
                return false;
            }
        }
        return true;
    }



}
