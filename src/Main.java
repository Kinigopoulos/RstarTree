
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    ///// STATIC VARIABLES \\\\\
    //=== R* TREE VARIABLES ===
    // Defines that no more than the given number of rectangles or points can be contained in each node.
    public static final int MAX_ENTRIES = 10;
    // Defines the percentage of minimum entries. Ideal value: 40% according to the paper.
    public static final int MINIMUM_ENTRIES_PERCENTAGE = 40;
    // Dimensions of the database and/or RStar Tree.
    public static final int DIMENSIONS = 2;

    //=== WRITING DATAFILE VARIABLES ===
    // Defines the csv file which will saved in datafiles
    public static final String FILENAME = "amenity_points.csv";
    // Defines if the program should ignore the first line because is a header and doesn't contain information.
    public static final boolean HAS_HEADER = true;
    // Defines maximum size (in bytes) for each datafile
    public static final int MAXIMUM_SIZE = 32000;
    // Defines the character that splits data into columns
    public static final String SEPARATOR = ",";
    // Defines the columns of data that indicate position. Ex.: {0, 1} indicates that x is in 0th column, y is in 1st.
    public static final int[] COLUMNS_OF_POSITION = {0, 1};
    // Defines the column of data that indicate the id.
    public static final int COLUMN_OF_ID = 2;
    // Defines the column of data that indicate the name.
    public static final int COLUMN_OF_NAME = 4;



    static final String[] NAME_OF_DIMENSIONS = {"X", "Y", "Z", "W"};

    public static void main(String[] args) {

        //Creates datafile files
        FileReader.CreateDatafiles();

        //Initialize R* with datafiles
        RStar rStar = new RStar();
        int[] dataSizes = FileReader.GetDataProperties();
        double TIME = System.nanoTime();
        for(int i = 0; i < dataSizes.length; i++){
            ArrayList<Point> points = FileReader.GetPoints(i + 1);
            for(Point point : points){
                rStar.InsertData(point);
            }
        }
        System.out.println("STRUCTURED IN: " + (System.nanoTime() - TIME));
        FileReader.CheckRStar();

        System.out.println("\n\nR* Tree Implementation");
        String choice = "";
        Scanner scanner = new Scanner(System.in);
        while(!choice.equals("0")){
            System.out.println("Type the number of the corresponding command to run it");
            System.out.println("===================");
            System.out.println("0. Exit");
            System.out.println("1. R* Range Query");
            System.out.println("2. R* K-nn Query");
            System.out.println("3. Serial Search Range Query");
            System.out.println("4. Serial Search K-nn Query");
            System.out.println("5. Insert Data to R*");

            choice = scanner.nextLine();
            long startTime = -1;
            long endTime = 0;
            if(choice.equals("1")){
                System.out.println("Minimum position");
                double[] min = readPoint();
                System.out.println("Maximum position");
                double[] max = readPoint();
                startTime = System.nanoTime();
                rStar.RANGE_QUERY(min, max);
                endTime = System.nanoTime();
            }else if(choice.equals("2")){
                System.out.println("Point's Position");
                double[] position = readPoint();
                System.out.println("Amount of neighbours (k > 0)");
                int k = Integer.parseInt(scanner.nextLine());
                startTime = System.nanoTime();
                rStar.K_NN_QUERY(k, position);
                endTime = System.nanoTime();
            }else if(choice.equals("3")){
                System.out.println("Minimum position");
                double[] min = readPoint();
                System.out.println("Maximum position");
                double[] max = readPoint();
                startTime = System.nanoTime();
                SerialSearch.RANGE_QUERY(min, max);
                endTime = System.nanoTime();
            }else if(choice.equals("4")){
                System.out.println("Point's Position");
                double[] position = readPoint();
                System.out.println("Amount of neighbours (k > 0)");
                int k = Integer.parseInt(scanner.nextLine());
                startTime = System.nanoTime();
                SerialSearch.K_NN_QUERY(k, position);
                endTime = System.nanoTime();
            }
            else if(choice.equals("5")){
                System.out.println("Point's Position");
                double[] position = readPoint();
                System.out.println("The id of the point");
                int id = Integer.parseInt(scanner.nextLine());
                startTime = System.nanoTime();
                Point point = new Point(id, position);
                rStar.InsertData(point);
                endTime = System.nanoTime();
            }
            if(startTime != -1) System.out.println("\nExecuted in " + (double)((endTime - startTime) / 1_000_000) + " milliseconds");
            System.out.println("===================\n");
        }

    }

    static double[] readPoint(){
        Scanner scanner = new Scanner(System.in);
        double[] result = new double[DIMENSIONS];
        for(int i=0; i<DIMENSIONS; i++){
            if(i < NAME_OF_DIMENSIONS.length) System.out.print(NAME_OF_DIMENSIONS[i] + ": ");
            else System.out.print((i+1)+"th dimension: ");

            result[i] = Double.parseDouble(scanner.nextLine());
        }
        return result;
    }
}