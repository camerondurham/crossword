import java.util.Vector;
import java.util.Scanner;
public class Util{

	public static void printShape(Vector<Vector<Tile>> shape){
		System.out.printf("    ");
		for(int i = 0; i < shape.get(0).size(); i++){
			System.out.printf("%3d", i);
		}
		System.out.printf("\n");

		for(int i = 0; i < shape.size(); i++){
			System.out.printf("%2d: ", i);
			for(int j = 0; j < shape.get(0).size(); j++){
				System.out.printf("%3s", shape.get(i).get(j).ltr);
			}
			System.out.printf("\n");
		}
	}

	public static Vector<Vector<Tile>> init2DTiles(int R, int C, String value) {
		Vector<Vector<Tile>> V = new Vector<Vector<Tile>>();
		for(int i = 0; i < R; i++){
			V.add(new Vector<Tile>());
			for(int j = 0; j < C; j++ ) {
				V.get(i).add(new Tile(value, i, j, false, false, false, "", ""));
			}
		}
		return V;
	}

	public static String readInput(Scanner sc){
		String line = null;

		while(sc.hasNext()){
			line  = sc.nextLine();
			if(line.isEmpty() || line.isBlank() ){
				continue;
			} else {
				break;
			}
		}
		return line;

	}

	public static String readLine(){
		Scanner sc = new Scanner(System.in);

		String line = null;

		while(sc.hasNext()){
			line  = sc.nextLine();
			if(line.isEmpty() || line.isBlank() ){
				continue;
			} else {
				break;
			}
		}
		sc.close();
		return line;
	}

	public static int readNumber() {

		String numToConvert = "";
		numToConvert = Util.readLine();
		int num = 0;

		try {
			num = Util.readInt(numToConvert);

		} catch (Exception e){
			System.out.println(e.getMessage());
		}
		return num;
	}


	public static float readFloat(String s) throws Exception {

		float f;
		try {
			f = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			throw new Exception("Unable to convert \'" + s + "\' to a float.\n");
		}

		return f;

	}

	public static int readInt(String s) throws Exception {

		int i;
		try {
			i = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new Exception("Unable to convert \'" + s + "\' to an integer.\n");
		}

		return i;
	}

	public static String readString(String s) throws Exception {

		if (!s.matches("[a-zA-Z ]+")) {
			throw new Exception("Invalid word.");
		}

		return s.trim();
	}



}

// Generics Example

// class Util<T> {
// public Vector<Vector<T>> init2D(int x, int y, T value) {
//     Vector<Vector<T>> V = new Vector<Vector<T>>();
//     for(int i = 0; i < y; i++){
//         V.add(new Vector<T>());
//         for(int j = 0; j < x; j++ ) {
//             V.get(i).add(value);
//         }
//     }
//     return V;
// }
