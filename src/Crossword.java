public class Crossword {
    private String answer;
    private boolean across;
    private String question;
    private int num;
    private int len;
    private boolean matched;
    private int row;
    private int col;
    private boolean guessed = false;
    public Crossword(String answer, boolean across, String question, int num){
        this.answer = answer;
        this.across = across;
        this.question = question;
        this.num = num;
        this.len = answer.length();
        this.matched = false;
        this.row = Integer.MAX_VALUE;
        this.col = Integer.MAX_VALUE;

    }

    public int getStartRow(){
        return row;
    }

    public int getStartCol(){
        return col;
    }

    public void updateStart(int row, int col){
        if(row < this.row){
            this.row = row;
        } 
        if(col < this.col){
            this.col = col;
        }
    }
    public boolean startsMatch(int i, int j){
        return row == i && col == j;
    }

    public int getLen(){
        return len;
    }
    public int getNum(){
        return num;
    }
    public String getAnswer(){
        return answer;
    }
    public boolean isAcross(){
        return across;
    }
    public boolean getAcross(){
        return across;
    }
    public String getQuestion(){
        return question;
    }
    public boolean getMatched() {
        return matched;
    }
    public void setMatched(boolean matched) {
        this.matched = matched;
    }
    public boolean getGuessed(){
        return this.guessed;
    }
    public void setGuessed(boolean guessed) {
        this.guessed = guessed;
    }

    @Override
    public String toString(){
        return "<" + num + ", " + answer + ", " + question +  " < " + row + "," + col + " > " + ">";
    }

}