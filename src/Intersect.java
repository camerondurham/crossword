public class Intersect{
    // intersection points
    public int rA, rB, cA, cB;

    // change shift left and right for shapes
    public int deltaRA, deltaCA, deltaRB, deltaCB;
    public int newR, newC;
    public int Brows, Bcols;
    public String ltr;

    // intersection region in A
    public int up, down, right, left;

    public Intersect(Shape A, Shape B, int rA, int cA, int rB, int cB){
        this.rA = rA;
        this.cA = cA;
        this.rB = rB;
        this.cB = cB;
        deltaCA = 0;
        deltaCB = 0;
        deltaRA = 0;
        deltaRB = 0;
	Brows = B.getR();
	Bcols = B.getC();
        this.ltr = A.getTile(rA, cA).ltr;
        // System.out.printf("\nrA = %d, cA = %d \t rB = %d, cB = %d\n",rA, cA, rB, cB);
        if(rB > rA){ // A shifts down
            newR = Math.max(rB - rA + A.getR(), B.getR());
            deltaRA = rB - rA;

        } else if (rB < rA) { // B shifts down
            newR = Math.max(rA - rB + B.getR(), A.getR());
            deltaRB = rA - rB;
        } else {
            newR = Math.max(A.getR(), B.getR());
        }

        if(cB > cA){ // A shifts right
            newC = Math.max(cB - cA + A.getC(), B.getC());
            deltaCA = cB - cA;

        } else if (cB < cA) { // B shifts down
            newC = Math.max(cA - cB + B.getC(), A.getC());
            deltaCB = cA - cB;
        } else {
            newC = Math.max(A.getC(), B.getC());
        }

        // System.out.printf("\nA: dR = %d, dC = %d\n B: dR = %d, dC = %d\nnewR = %d, newC = %d\n", deltaRA, deltaCA, deltaRB, deltaCB, newR, newC);

        if(deltaCB > 0){
            left = deltaCB - 1;
        } else {
            left = 0;
        }
        if(deltaRB > 0){
           up = deltaRB - 1; // changed these
        } else {
            up = 0;
        }
        down = deltaRB + Math.min(A.getR(), B.getR()  ) + 1;
        right = deltaCB +  Math.min(A.getC(), B.getC()  ) + 1;

        // System.out.printf("\nupper: %d, lower: %d\nleft: %d, right: %d\n", up, down, left, right);
    }


    // BACKUP
    // public Intersect(Shape A, Shape B, int rA, int cA, int rB, int cB){
    //     this.rA = rA;
    //     this.cA = cA;
    //     this.rB = rB;
    //     this.cB = cB;
    //     deltaCA = 0;
    //     deltaCB = 0;
    //     deltaRA = 0;
    //     deltaRB = 0;
	// Brows = B.getR();
	// Bcols = B.getC();
    //     this.ltr = A.getTile(rA, cA).ltr;
    //     // System.out.printf("\nrA = %d, cA = %d \t rB = %d, cB = %d\n",rA, cA, rB, cB);
    //     if(rB > rA){ // A shifts down
    //         newR = Math.max(rB - rA + A.getR(), B.getR());
    //         deltaRA = rB - rA;

    //     } else if (rB < rA) { // B shifts down
    //         newR = Math.max(rA - rB + B.getR(), A.getR());
    //         deltaRB = rA - rB;
    //     } else {
    //         newR = Math.max(A.getR(), B.getR());
    //     }

    //     if(cB > cA){ // A shifts right
    //         newC = Math.max(cB - cA + A.getC(), B.getC());
    //         deltaCA = cB - cA;

    //     } else if (cB < cA) { // B shifts down
    //         newC = Math.max(cA - cB + B.getC(), A.getC());
    //         deltaCB = cA - cB;
    //     } else {
    //         newC = Math.max(A.getC(), B.getC());
    //     }

    //     // System.out.printf("\nA: dR = %d, dC = %d\n B: dR = %d, dC = %d\nnewR = %d, newC = %d\n", deltaRA, deltaCA, deltaRB, deltaCB, newR, newC);

    //     if(deltaCB > 0){
    //         left = deltaCB;
    //     } else {
    //         left = 0;
    //     }
    //     if(deltaRB > 0){
    //        up = deltaRB;
    //     } else {
    //         up = 0;
    //     }
    //     down = deltaRB + Math.min(A.getR(), B.getR()  );
    //     right = deltaCB +  Math.min(A.getC(), B.getC()  );

    //     // System.out.printf("\nupper: %d, lower: %d\nleft: %d, right: %d\n", up, down, left, right);
    // }

    public boolean inShapeB(int i, int j){
        //System.out.printf("\nChecking if (%d,%d) -> (%d,%d) in B", i,j, i - deltaRB + deltaRA, j - deltaCB - deltaCA);
        if( i  - deltaRB  + deltaRA >= 0 && j  - deltaCB + deltaCA >= 0 ){

         //   String s =( (i - deltaRB  + deltaRA < Brows && j - deltaCB + deltaCA < Bcols)? " -- true" : " -- false inner");
         //   System.out.print(s);
            return (i - deltaRB  + deltaRA < Brows && j - deltaCB + deltaCA < Bcols);
        }
        //System.out.printf("  -- false outer");
        return false;
    }

    @Override
    public String toString(){
        return String.format("IX: ltr: %s, A(%d,%d), B(%d,%d)\nupper: %d, lower: %d\nleft: %d, right: %d\n(dRA, dCA) = (%d,%d), (dRB,dCB) = (%d,%d)", this.ltr, rA,cA,rB,cB,up,down,left,right, deltaRA, deltaCA, deltaRB, deltaCB);
    }

}
