import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;


public class PuzzleLoader {
    private Collection<Crossword> acrossWords;
    private Collection<Crossword> downWords;
    private HashMap<String, Crossword> AcrossWords;
    private HashMap<String, Crossword> DownWords;
    private Vector<Shape> shapes;
    private String gameDataPath;
    private boolean foundGameFile;
    private Shape board;

    public PuzzleLoader() {
        acrossWords = new HashSet<Crossword>();
        downWords = new HashSet<Crossword>();
        AcrossWords = new HashMap<String, Crossword>();
        DownWords = new HashMap<String, Crossword>();
        gameDataPath = "../gamedata/";
        foundGameFile = false;
        shapes = new Vector<Shape>();
    }

    public Collection<Crossword> getAcrossWords() {
        return this.acrossWords;
    }

    public Collection<Crossword> getDownWords() {
        return this.downWords;
    }

    public Shape getBoard() {
        return this.board;
    }

    public HashMap<String, Crossword> getAcross() {
        return this.AcrossWords;
    }

    public HashMap<String, Crossword> getDown() {
        return this.DownWords;
    }

    public boolean findValidFile() {
        File dir = new File(this.gameDataPath);
        LinkedList<File> fileList = null;
        if (dir != null) {
            File[] files = dir.listFiles();
            fileList = new LinkedList<File>(Arrays.asList(files));
            Collections.shuffle(fileList, new Random());
        }
        File gameFile = null;

        while (!fileList.isEmpty() && !foundGameFile) {
            gameFile = fileList.getFirst();
            foundGameFile = this.getFile(gameFile);
            fileList.removeFirst();
        }
        if (foundGameFile) {
            System.out.println("Found valid file: " + gameFile.getName());
        } else {
            System.out.println("Cannot find valid file");
        }

        return foundGameFile;
    }

    public boolean getFoundGameFile() {
        return this.foundGameFile;
    }

    public boolean foundValidGameFile() {
        return foundGameFile;
    }

    public BufferedReader openFile(String filename) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
        return br;
    }

    public BufferedReader openFile(File f) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
        return br;
    }

    public void closeFile(BufferedReader br) {
        try {
            if (br != null) {
                br.close();
            }
        } catch (IOException ioe) {
            System.out.println(" error closing file: " + ioe.getMessage());
        }
    }

    public boolean isNumeric(String str) {
        try {
            int num = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean searchMatch(String str, boolean across) {
        if (across) {
            for (Crossword c : downWords) {
                if (c.getAnswer().charAt(0) == str.charAt(0))
                    return true;
            }
        } else {
            for (Crossword c : acrossWords) {
                if (c.getAnswer().charAt(0) == str.charAt(0))
                    return true;
            }
        }
        return false;
    }

    public boolean getFile(File file) {
        BufferedReader br = openFile(file);
        boolean valid = parseFile(br);
        closeFile(br);
        return valid;
    }

    public boolean getFile(String file) {
        BufferedReader br = openFile(file);
        boolean valid = parseFile(br);
        closeFile(br);
        return valid;
    }

    public boolean verify(Crossword c, boolean across) {
        if (across) {
            for (Crossword cc : downWords) {
                if (cc.getNum() == c.getNum() && cc.getAnswer().charAt(0) != c.getAnswer().charAt(0)) {
                    return false;
                }
            }
        } else {
            for (Crossword cc : acrossWords) {
                if (cc.getNum() == c.getNum() && cc.getAnswer().charAt(0) != c.getAnswer().charAt(0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean parseFile(BufferedReader br) {
        boolean ACROSS = false;
        boolean DOWN = false;
        boolean horiz = false;
        Crossword c = null;
        try {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(" Read line : " + line);
                if (ACROSS && line.compareToIgnoreCase("ACROSS") == 0) {
                    return false;
                } else if (DOWN && line.compareToIgnoreCase("DOWN") == 0) {
                    return false;
                } else if (line.trim().compareToIgnoreCase("ACROSS") == 0) {
                    ACROSS = true;
                    horiz = true;
                } else if (line.trim().compareToIgnoreCase("DOWN") == 0) {
                    DOWN = true;
                    horiz = false;
                } else {
                    c = parseLine(line, horiz);
                    if (c != null && ACROSS && !DOWN && verify(c, ACROSS)) {
                        acrossWords.add(c);
                        AcrossWords.put(c.getAnswer(), c);
                    } else if (c != null && DOWN && verify(c, !DOWN)) {
                        downWords.add(c);
                        DownWords.put(c.getAnswer(), c);
                    } else {
                        acrossWords.clear();
                        downWords.clear();
                        AcrossWords.clear();
                        DownWords.clear();
                        return false;
                    }
                }

            }
        } catch (IOException ioe) {
            System.out.println("ioe error: " + ioe.getMessage());
            return false;
        }
        return true;
    }

    public Crossword parseLine(String line, boolean across) {
        Crossword c = null;
        String[] lineArr = line.split("\\|");
        if (lineArr != null && lineArr.length == 3) {
            int num = -1;
            String answer = null, question = null;

            if (isNumeric(lineArr[0])) {
                num = Integer.parseInt(lineArr[0]);
            }
            if (!lineArr[1].contains(" ")) {
                answer = lineArr[1];
            }
            question = lineArr[2];
            if (num > 0 && answer != null && question != null) {
                c = new Crossword(answer, across, question, num);
            }
        }
        return c;
    }

    public Puzzle generateBoard() {
        if (acrossWords.isEmpty() || downWords.isEmpty()) {
            return null;
        }
        this.board = null;

        for(int i = 0; i < 50 && board == null; i++){

            for (Crossword across : acrossWords) {
                for (Crossword down : downWords) {
                    if (across.getNum() == down.getNum()) {
                        shapes.add(new Shape(across, down));
                        down.setMatched(true);
                        across.setMatched(true);
                    }
                }
                if (!across.getMatched()) {
                    shapes.add(new Shape(across));
                }
            }

            for (Crossword down : downWords) {
                if (!down.getMatched()) {
                    shapes.add(new Shape(down));
                }
            }
            //System.out.println("\nSHAPES VEC\n");
            //for (Shape s : shapes) {
            //    s.printShape();
            //}
            this.board = mergeHelper(shapes, 0);
            if(this.board == null){
                shapes.clear();
            }
            /*        if (this.board != null) {

                      System.out.println( "\n##########################################\n\t\tFINAL BOARD\n#####################################");
            //this.board.printShape();
            } else {

            System.out.println( "\n##########################################\n\t\tFINAL BOARD\n#####################################");
            System.out.println( "\n##########################################\n\t\tINVALID!!!!!!!!!!!!!\n#####################################");
            }
            */
        }

        this.getStartCoordinates();

        return new Puzzle(board, this.AcrossWords, this.DownWords);
    }

    public static Shape mergeHelper(Vector<Shape> vec, int count) {
        //       System.out.printf("Recursive Call #%d\n", count);
        if (vec.size() == 1) {
            return vec.elementAt(0);
        }

        // System.out.printf("***************** Size of vec: %d********************\n",
        // vec.size());
        // for (int i = 0; i < vec.size(); i++) {
        // System.out.printf("Shape #%d: \n", i);
        // vec.elementAt(i).printShape();
        // }

        Shape A = new Shape(vec.remove(0));
        //	System.out.println("Shape A first letter is: " + A.getTile(0,0).getHorizontalWord() + " , " + A.getTile(0,0).getVerticalWord() + "\n");

        for (int i = 0; i < vec.size(); ++i) {
            //            System.out.println("Size of vector is: " + vec.size() + "\n");
            Shape B = new Shape(vec.get(i));

            Vector<Intersect> points = A.findIntersects(B);

            //  System.out.println("Comparing shape A:\n");
            //  A.printShape();

            //  System.out.println("And shape B:");
            //  B.printShape();
            // for(Intersect poi : points)
            // System.out.printf("intersection point: %s\n",poi.toString() );
            // System.out.printf("------------------- %d INTERSCTIONS
            // ------------------\n\n", points.size());
            for (Intersect poi : points) {
                //              System.out.printf( "\n\n&&&&&&&&&&&&&&&&&&&&&&&&&& AT COUNT %d Trying intersection point:&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& %s\n\n ", count, poi.toString());
                //              System.out.println("Shape A:");
                //              A.printShape();
                //              System.out.println("Shape B:");
                //              B.printShape();

                // System.out.println("other shapes");

                // for (Shape s : vec) {
                // s.printShape();
                // }
                if (A.compare(B, poi)) {

                    vec.removeElementAt(i);
                    Shape merged = new Shape(A.returnMerged(B, poi));
                    vec.insertElementAt(merged, 0);
                    Vector<Shape> clone = deepCopy(vec);
                    Shape ret = mergeHelper(clone, count + 1);

                    if (ret != null) {

                        //	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@Merge at point SUCCEEDED@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        return ret;
                    }

                    vec.remove(0);

                    //           System.out.println("<<<<<<<<<<<<<<<<ADDING BACK SHAPE VECTOR INDEX   " + i + "   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    if (i < vec.size()) {
                        vec.insertElementAt(B, i);
                    } else {
                        vec.add(B);
                    }

                } else {
                    //            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@Merge at point FAILED@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                }

            }
        }

        // vec.add(A);

        return null;
    }

    public boolean makeGameRandomFile() {
        if (this.findValidFile()) {
            this.generateBoard();
            return true;
        }
        return false;
    }

    public void makeGameFile(String filename) {
        if (this.getFile(this.gameDataPath + filename)) {
            this.generateBoard();
        }
    }

    public static Vector<Shape> deepCopy(Vector<Shape> v1) {
        Vector<Shape> v2 = new Vector<Shape>();
        for (Shape s : v1) {
            v2.add(new Shape(s));
        }
        return v2;
    }

    public boolean isGuessed(Tile t) {
        return ((AcrossWords.containsKey(t.getHorizontalWord()) && AcrossWords.get(t.getHorizontalWord()).getGuessed())
                || (DownWords.containsKey(t.getVerticalWord()) && DownWords.get(t.getVerticalWord()).getGuessed()));
    }

    public void printBoard() {
        System.out.println();
        if(this.board == null){System.out.println("Cannot create valid board!"); return;}
        for (int i = 0; i < this.board.getR(); i++) {
            // System.out.println("Row " + i);
            for (int j = 0; j < this.board.getC(); j++) {

                // System.out.println("Checking tile horizontal word: " + board.getTile(i,
                // j).getHorizontalWord() + " vertical word " +
                // board.getTile(i,j).getVerticalWord() + "\n");
                if (board.getTile(i, j).isLetter()) {
                    // System.out.println("Contains Vertical word ? " +
                    // DownWords.containsKey(board.getTile(i, j).getVerticalWord()));
                    // System.out.println("Contains Horizontal word ? " +
                    // AcrossWords.containsKey(board.getTile(i, j).getHorizontalWord()));
                    if (AcrossWords.containsKey(board.getTile(i, j).getHorizontalWord())
                            && AcrossWords.get(board.getTile(i, j).getHorizontalWord()).startsMatch(i, j)) {
                        System.out.printf("%d", AcrossWords.get(board.getTile(i, j).getHorizontalWord()).getNum());
                    } else if (DownWords.containsKey(board.getTile(i, j).getVerticalWord())
                            && DownWords.get(board.getTile(i, j).getVerticalWord()).startsMatch(i, j)) {
                        System.out.printf("%d", DownWords.get(board.getTile(i, j).getVerticalWord()).getNum());
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

    public void getStartCoordinates() {
        if(this.board == null){System.out.println("Cannot create valid board!"); return;}
        for (int i = 0; i < this.board.getR(); i++) {
            for (int j = 0; j < this.board.getC(); j++) {
                // if tile begins word and is horizontal
                Tile tile = board.getTile(i, j);
                if (tile.isHorizontalStart() && AcrossWords.containsKey(tile.getHorizontalWord())) {
                    //    System.out.println("Updating horizontal word: " + tile.getHorizontalWord());
                    AcrossWords.get(tile.getHorizontalWord()).updateStart(i, j);
                }

                // if tile begins word and vertical
                if (tile.isVerticalStart() && DownWords.containsKey(tile.getVerticalWord())) {
                    //   System.out.println("Updating vertical word: " + tile.getVerticalWord());
                    DownWords.get(board.getTile(i, j).getVerticalWord()).updateStart(i, j);
                }
            }
        }
        //System.out.println("\n\nAcross Words:");
        //for (Crossword c : AcrossWords.values()) {
        //    System.out.println("\n" + c.toString());
        //}
        //System.out.println("\n\nDown Words:");
        //for (Crossword c : DownWords.values()) {
        //    System.out.println("\n" + c.toString());
        //}
    }

    public static void main(String[] args) {
        System.out.println("My Puzzle Loader");
        // boolean valid = PL.getFile("../gamedata/gamedata1.txt");

        for(int i = 0; i < 10; i++){
            PuzzleLoader PL2 = new PuzzleLoader();
            PL2.getFile("../gamedata/4.txt");
            //PL2.findValidFile();
            PL2.generateBoard();
            PL2.printBoard();
        }

        //      System.out.println("ACROSS");
        //      for (Crossword c : PL2.acrossWords) {
        //          System.out.println(c.getAnswer());
        //      }
        //      System.out.println("DOWN");
        //      for (Crossword c : PL2.downWords) {
        //          System.out.println(c.getAnswer());
        //      }

        //Puzzle pz = PL2.generateBoard();
        // PL2.getStartCoordinates();

        // PL2.AcrossWords.get("telephone").setGuessed(true);
        // PL2.printBoard();
        // if(pz != null){
        // System.out.print(pz.toString());
        // }

        //Puzzle p = new Puzzle(PL2);
        //System.out.println(p.promptPlayerString());



        // Shape s1 = new Shape(new Crossword("TROJANS", true, "", 1), new
        // Crossword("TRAVELERS", false, "", 1));
        // Shape s2 = new Shape(new Crossword("CSCI", true, "", 2));
        // Shape s3 = new Shape(new Crossword("DODGERS", true, "", 2));
        // Shape s4 = new Shape(new Crossword("GOLD", false, "", 2));
        // Shape s5 = new Shape(new Crossword("MARSHALL", false, "", 2));

        // Shape S[] = {s1,s2,s3,s4,s5};
        // Vector<Shape> vec = new Vector<Shape>(Arrays.asList(S));
        // Shape f = mergeHelper(vec);
        // f.printShape();
    }

}
