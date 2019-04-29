import java.util.HashMap;
import java.util.Vector;

public class Shape {
    private Vector<Vector<Tile>> shape;
    private HashMap<String, Vector<Tile>> tiles;
    private int R;
    private int C;



    // Constructor
    public Shape(Shape other) {
        this.shape = new Vector<Vector<Tile>>(other.shape);
        this.tiles = new HashMap<String, Vector<Tile>>(other.tiles);

        // for (int i = 0; i < other.shape.size(); i++) {
        //     this.shape.add(new Vector<Tile>());
        //     for (int j = 0; j < other.shape.get(0).size(); j++) {
        //         this.shape.get(i).add(new Tile(other.shape.get(i).get(j)));
        //     }
        // }

        // for (Map.Entry<String, Vector<Tile>> entry : other.tiles.entrySet()) {
        //     Vector<Tile> vec = new Vector<Tile>();
        //     for (int i = 0; i < entry.getValue().size(); i++) {
        //         vec.add(new Tile(entry.getValue().elementAt(i)));
        //     }
        //     this.tiles.put(entry.getKey(), vec);
        // }

        this.R = other.R;
        this.C = other.C;
    }

    public Shape(Crossword hor, Crossword ver) {
        this.C = hor.getLen();
        this.R = ver.getLen();
        this.tiles = new HashMap<String, Vector<Tile>>();
        shape = new Vector<Vector<Tile>>();

        for (int i = 0; i < R; i++) {
            shape.add(new Vector<Tile>());
            for (int j = 0; j < C; j++) {
                Tile t = null;
                // add horizontal word in row 0
                if (i == 0) {
                    t = new Tile(String.valueOf(hor.getAnswer().charAt(j)), i, j, true, j == 0, j == C - 1, "", hor.getAnswer());
                    if(j == 0) t.setVerticalWord(ver.getAnswer());
                    shape.get(i).add(t);
                }
                // add vertical word in column 0
                else if (j == 0) {
                    t = new Tile(String.valueOf(ver.getAnswer().charAt(i)), i, j, false, i == 0, i == R - 1, ver.getAnswer(), "");
                    if( i == 0) t.setHorizontalWord(hor.getAnswer());
                    shape.get(i).add(t);
                } else {
                    // add blank tile
                    shape.get(i).add(new Tile(" ", i, j, false, false, false, "", ""));
                }
                // add tile character to map
                if (t != null) {
                    addTile(t);
                }
            }
        }
    }

    public Shape(Crossword c) {
        if (c.isAcross()) {
            this.C = c.getLen();
            this.R = 1;
        } else {
            this.C = 1;
            this.R = c.getLen();
        }

        this.tiles = new HashMap<String, Vector<Tile>>();
        shape = new Vector<Vector<Tile>>();

        for (int i = 0; i < R; i++) {
            shape.add(new Vector<Tile>());
            for (int j = 0; j < C; j++) {
                Tile t = null;
                // add horizontal word in row 0
                if (i == 0) {
                    t = new Tile(String.valueOf(c.getAnswer().charAt(j)), i, j, c.isAcross(), j == 0, j == C - 1, "", c.getAnswer());
                    if(j == 0) t.setVerticalWord(c.getAnswer());
                    shape.get(i).add(t);
                }
                // add vertical word in column 0
                else if (j == 0) {
                    t = new Tile(String.valueOf(c.getAnswer().charAt(i)), i, j, c.isAcross(), i == 0, i == R - 1, c.getAnswer(), "");
                    if( i == 0) t.setHorizontalWord(c.getAnswer());
                    shape.get(i).add(t);
                } else {
                    // add blank tile
                    shape.get(i).add(new Tile(" ", i, j, false, false, false, "", ""));
                }
                // add tile character to map
                if (t != null) {
                    addTile(t);
                }
            }
        }
    }
    // End Constructors

    // Begin utility functions
    public void addTile(Tile t) {
        if (this.tiles.containsKey(t.ltr)) {
            this.tiles.get(t.ltr).add(t);
        } else {
            Vector<Tile> v = new Vector<Tile>();
            v.add(t);
            this.tiles.put(t.ltr, v);
        }
    }

    public void printShape() {
        System.out.printf("    ");
        for (int i = 0; i < C; i++) {
            System.out.printf("%3d", i);
        }
        System.out.printf("\n");

        for (int i = 0; i < R; i++) {
            System.out.printf("%2d: ", i);
            for (int j = 0; j < C; j++) {
                System.out.printf("%3s", shape.get(i).get(j).getLtr());
            }
            System.out.printf("\n");
        }
    }

    public Vector<Intersect> findIntersects(Shape B) {
        Vector<Intersect> points = new Vector<Intersect>();

        for (String letter : this.tiles.keySet()) {
            if (B.tiles.containsKey(letter)) {
                Vector<Tile> Apoints = this.tiles.get(letter);
                Vector<Tile> Bpoints = B.tiles.get(letter);
                for (Tile atile : Apoints) {
                    for (Tile btile : Bpoints) {
                        points.add(new Intersect(this, B, atile.getR(), atile.getC(), btile.getR(), btile.getC()));
                    }
                }
            }
        }
        return points;
    }
    public boolean compare(Shape other, Intersect poi) {
        // System.out.println("\n******************STARTING
        // COMPARE***********************\nComparing with POI: " + poi.toString() + "\n
        // STARTING ROW " + Math.max(poi.up - 1, 0) + "\t COLUMN: " + Math.max(poi.left
        // - 1, 0));

        int startRow = Math.max(poi.up - 1, 0);
        int startCol = Math.max(poi.left - 1, 0);
        int endRow = Math.min(poi.down, this.shape.size());
        int endCol = Math.min(poi.right, this.shape.get(0).size());
	//  int endRow  = this.shape.size();
	//  int endCol = this.shape.get(0).size();

        if(poi.right == this.shape.get(0).size() - 1){
            endCol = this.shape.get(0).size();
        }
        if(poi.down == this.shape.size() - 1){
            endRow = this.shape.size();
        }
        // System.out.println("\nStarting at row " + startRow + " to " + endRow);
        // System.out.println("\nStarting at column " + startCol + " to " + endCol);
        for (int i = startRow; i < endRow; i++) {
            for (int j = startCol; j < endCol ; j++) {
                // System.out.printf("\nGetting tile in A index: (%d,%d)\n", i,j);
                if (poi.inShapeB(i, j) && (this.shape.get(i).get(j).compOverlap(
                        other.getTile(i - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA)) == false)) {
                    //System.out.println("\nInvalid overlap\n");
                    return false;
                }
                if (this.shape.get(i).get(j).isLetter() == false) {
                    // System.out.println("Don't care about spaces!!!!");
                    continue;
                }
                Vector<Tile> neighbors = new Vector<Tile>();

                // add neighbors for horizontal tiles
                if (this.shape.get(i).get(j).isHoriz()) {

                    if (poi.inShapeB(i - 1, j)) { // add above
                        neighbors.add(other.getTile(i - 1 - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA));
                    }

                    if (poi.inShapeB(i + 1, j)) { // add below
                        neighbors.add(other.getTile(i + 1 - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA));
                    }
                } else {

                    if (poi.inShapeB(i, j + 1)) { // add right
                        neighbors.add(other.getTile(i - poi.deltaRB + poi.deltaRA, j + 1 - poi.deltaCB + poi.deltaCA));
                    }

                    if (poi.inShapeB(i, j - 1)) { // add left
                        neighbors.add(other.getTile(i - poi.deltaRB + poi.deltaRA, j - 1 - poi.deltaCB + poi.deltaCA));
                    }
                }

                if (this.shape.get(i).get(j).isStart() && this.shape.get(i).get(j).isHoriz()) {
                    if (poi.inShapeB(i, j - 1)) { // add left
                        neighbors.add(other.getTile(i - poi.deltaRB + poi.deltaRA, j - 1 - poi.deltaCB + poi.deltaCA));
                    }
                } else if (this.shape.get(i).get(j).isStart()) {

                    if (poi.inShapeB(i - 1, j)) { // add above
                        neighbors.add(other.getTile(i - 1 - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA));
                    }

                }
                if (this.shape.get(i).get(j).isEnd() && this.shape.get(i).get(j).isHoriz()) {

                    if (poi.inShapeB(i, j + 1)) { // add right
                        neighbors.add(other.getTile(i - poi.deltaRB + poi.deltaRA, j + 1 - poi.deltaCB + poi.deltaCA));
                    }
                } else if (this.shape.get(i).get(j).isEnd()) {

                    if (poi.inShapeB(i + 1, j)) { // add below
                        neighbors.add(other.getTile(i + 1 - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA));
                    }
                }
                // finish adding neighbors
                   //System.out.println("Neighbors of : " + this.shape.get(i).get(j).toString() + " " + neighbors.size());
                boolean inIntersect = poi.inShapeB(i, j);
                if (this.shape.get(i).get(j).compOthers(neighbors, inIntersect) == false) {
                    //System.out.printf("\nReturning: Invalid Neighbors COMP\n");
                    return false;
                } else {
                    //System.out.println("\nValid Neightbors\n");
                }
            }
        }
        return true;
    }

    // public boolean compareOrig(Shape other, Intersect poi) {
    //     // System.out.println("\n******************STARTING
    //     // COMPARE***********************\nComparing with POI: " + poi.toString() + "\n
    //     // STARTING ROW " + Math.max(poi.up - 1, 0) + "\t COLUMN: " + Math.max(poi.left
    //     // - 1, 0));

    //     int startRow = Math.max(poi.up - 1, 0);
    //     int startCol = Math.max(poi.left - 1, 0);
    //     for (int i = startRow; i < Math.min(poi.down, this.shape.size()); i++) {
    //         for (int j = startCol; j < Math.min(poi.right, this.shape.get(0).size()); j++) {
    //             // System.out.printf("\nGetting tile in A index: (%d,%d)\n", i,j);
    //             if (poi.inShapeB(i, j) && (this.shape.get(i).get(j).compOverlap(
    //                     other.getTile(i - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA)) == false)) {
    //                 System.out.println("\nInvalid overlap\n");
    //                 return false;
    //             }
    //             if (this.shape.get(i).get(j).isLetter() == false) {
    //                 // System.out.println("Don't care about spaces!!!!");
    //                 continue;
    //             }
    //             Vector<Tile> neighbors = new Vector<Tile>();

    //             // add neighbors for horizontal tiles
    //             if (this.shape.get(i).get(j).isHoriz()) {

    //                 if (poi.inShapeB(i - 1, j)) { // add above
    //                     neighbors.add(other.getTile(i - 1 - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA));
    //                 }

    //                 if (poi.inShapeB(i + 1, j)) { // add below
    //                     neighbors.add(other.getTile(i + 1 - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA));
    //                 }
    //             } else {

    //                 if (poi.inShapeB(i, j + 1)) { // add right
    //                     neighbors.add(other.getTile(i - poi.deltaRB + poi.deltaRA, j + 1 - poi.deltaCB + poi.deltaCA));
    //                 }

    //                 if (poi.inShapeB(i, j - 1)) { // add left
    //                     neighbors.add(other.getTile(i - poi.deltaRB + poi.deltaRA, j - 1 - poi.deltaCB + poi.deltaCA));
    //                 }
    //             }

    //             if (this.shape.get(i).get(j).isStart() && this.shape.get(i).get(j).isHoriz()) {
    //                 if (poi.inShapeB(i, j - 1)) { // add left
    //                     neighbors.add(other.getTile(i - poi.deltaRB + poi.deltaRA, j - 1 - poi.deltaCB + poi.deltaCA));
    //                 }
    //             } else if (this.shape.get(i).get(j).isStart()) {

    //                 if (poi.inShapeB(i - 1, j)) { // add above
    //                     neighbors.add(other.getTile(i - 1 - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA));
    //                 }

    //             }
    //             if (this.shape.get(i).get(j).isEnd() && this.shape.get(i).get(j).isHoriz()) {

    //                 if (poi.inShapeB(i, j + 1)) { // add right
    //                     neighbors.add(other.getTile(i - poi.deltaRB + poi.deltaRA, j + 1 - poi.deltaCB + poi.deltaCA));
    //                 }
    //             } else if (this.shape.get(i).get(j).isEnd()) {

    //                 if (poi.inShapeB(i + 1, j)) { // add below
    //                     neighbors.add(other.getTile(i + 1 - poi.deltaRB + poi.deltaRA, j - poi.deltaCB + poi.deltaCA));
    //                 }
    //             }
    //             // finish adding neighbors
    //             // System.out.println("Neighbors of : " + this.shape.get(i).get(j).toString() +
    //             // " " + neighbors.size());
    //             if (this.shape.get(i).get(j).compOthers(neighbors, poi.inShapeB(i, j)) == false) {
    //                 System.out.printf("\nReturning: Invalid Neighbors COMP\n");
    //                 return false;
    //             } else {
    //                 System.out.println("\nValid Neightbors\n");
    //             }
    //         }
    //     }
    //     return true;
    // }

    /*
     * public void printShape(Vector<Vector<Tile>> shape){
     * System.out.printf("    "); for(int i = 0; i < shape.get(0).size(); i++){
     * System.out.printf("%3d", i); } System.out.printf("\n");
     *
     * for(int i = 0; i < shape.size(); i++){ System.out.printf("%2d: ", i); for(int
     * j = 0; j < shape.get(0).size(); j++){ System.out.printf("%3s",
     * shape.get(i).get(j).ltr); } System.out.printf("\n"); } }
     */

    // public Vector<Vector<Tile>> merge(Shape B, Intersect poi){
    // int newC = poi.newC, newR = poi.newR;

    // Vector<Vector<Tile>> newShape = Util.init2DTiles(newR, newC, "");
    // for(int i = 0; i < R; i++){
    // for(int j = 0; j < C; j++){
    // newShape.get(i + poi.deltaRA).get(j +
    // poi.deltaCA).setLtr(shape.get(i).get(j).ltr);
    // }
    // }
    // // Util.printShape(newShape);

    // // for(int i = poi.up; i < poi.down; i++){
    // // for(int j = poi.left; j < poi.right; j++){
    // // // get overlapping tile with shape A
    // // Tile inB = B.shape.get(i - poi.deltaRB).get(j - poi.deltaCB);
    // // Tile inA = newShape.get(i).get(j);
    // // System.out.printf("SHAPE-A: %5s (%2d,%2d) SHAPE-B: %5s (%2d,%2d)\n",
    // inA.ltr, inA.getR(), inA.getC(), inB.ltr, inB.getR(), inB.getC());
    // // }
    // // }

    // for(int i = 0; i < B.R ; i++){
    // for(int j = 0; j < B.C; j++){
    // if(B.getTile(i, j).isLetter()){
    // newShape.get(i + poi.deltaRB).get(j +
    // poi.deltaCB).setTileProps(B.getTile(i,j));
    // }
    // }
    // }

    // Util.printShape(newShape);
    // return newShape;

    // }

    // input shape to merge and points of intersection
    // assumes valid intersection
    public void merge(Shape B, Intersect poi) {
        int newC = poi.newC, newR = poi.newR;
        Vector<Vector<Tile>> newShape = Util.init2DTiles(newR, newC, "");

        // fill in current shape
        for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
                newShape.get(i + poi.deltaRA).get(j + poi.deltaCA).setLtr(shape.get(i).get(j).ltr);
            }
        }

        // fill in from overlap in shape B
        for (int i = 0; i < B.R; i++) {
            for (int j = 0; j < B.C; j++) {
                if (newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).isLetter() && B.getTile(i, j).isLetter()) {
                    newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).makeStart();
                    newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).makeEnd();
                } else {
                    newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).setTileProps(B.getTile(i, j));
                }
            }
        }

        // Util.printShape(newShape);
        // reset shape parameters
        this.shape = newShape;
        this.R = newR;
        this.C = newC;
    }

    public boolean canMerge(Shape B) {
        Vector<Intersect> points = this.findIntersects(B);
        if (points.isEmpty())
            return false;
        for (Intersect p : points) {
            if (compare(B, p)) {
                return true;
            }
        }
        return false;

    }

    // assumes valid merged shape
    // forms and returns merged shapes at point of intersection poi
    public Shape returnMerged(Shape B, Intersect poi) {
        int newC = poi.newC, newR = poi.newR;

        Vector<Vector<Tile>> newShape = Util.init2DTiles(newR, newC, "");
        Shape returnShape = new Shape(this);
        returnShape.tiles.clear();

        // fill in current shape
        for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
                newShape.get(i + poi.deltaRA).get(j + poi.deltaCA).setTileProps(shape.get(i).get(j));
            }
        }

        // fill in from overlap in shape B
        for (int i = 0; i < B.R; i++) {
            for (int j = 0; j < B.C; j++) {
                if (newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).isLetter() && B.getTile(i, j).isLetter()) {
                    newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).makeStart();
                    newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).makeEnd();
                    newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).setOtherWord(B.getTile(i, j));

                } else if (B.getTile(i, j).isLetter()) {
                    newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).setTileProps(B.getTile(i, j));
                }
                // else {
                // newShape.get(i + poi.deltaRB).get(j + poi.deltaCB).setTileProps(B.getTile(i,
                // j));
                // }
            }
        }

        // reset tile set
        for (int i = 0; i < newR; i++) {
            for (int j = 0; j < newC; j++) {
                returnShape.addTile(newShape.get(i).get(j));
            }
        }

        // Util.printShape(newShape);
        // reset shape parameters
        returnShape.setShape(newShape);
        returnShape.R = newR;
        returnShape.C = newC;
        return returnShape;
    }

    // End utility functions

    // Begin Getters
    public int getR() {
        return this.R;
    }

    public int getC() {
        return this.C;
    }

    public Tile getTile(int r, int c) {
        return this.shape.get(r).get(c);
    }
    // End Getters

    // Begin Setters
    public void setShape(Vector<Vector<Tile>> s) {
        this.shape = s;
    }

    // End Setters
  
}
