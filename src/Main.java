import java.io.File;
import java.util.ArrayList;

public class Main {

    /////STATIC VARIABLES\\\\\
    //R* TREE VARIABLES
    // Defines that no more than the given number of rectangles or points can be contained in each node.
    public static final int MAX_ENTRIES = 3;
    // Defines the percentage of minimum entries. Ideal value: 40% according to the paper.
    public static final int MINIMUM_ENTRIES_PERCENTAGE = 40;
    // Dimensions of the database and/or RStar Tree.
    public static final int DIMENSIONS = 2;

    //WRITING DATAFILE VARIABLES
    // Defines the csv file which will saved in datafiles
    public static final String FILENAME = "amenity_points.csv";
    // Defines if the program should ignore the first line because is a header and doesn't contain information.
    public static final boolean HAS_HEADER = true;
    // Defines maximum size (in bytes) for each datafile
    public static final int MAXIMUM_SIZE = 500;
    // Defines the character that splits data into columns
    public static final String SEPARATOR = ",";
    // Defines the columns of data that indicate position. Ex.: {0, 1} indicates that x is in 0th column, y is in 1st.
    public static final int[] COLUMNS_OF_POSITION = {0, 1};
    // Defines the column of data that indicate the id.
    public static final int COLUMN_OF_ID = 2;
    // Defines the column of data that indicate the name.
    public static final int COLUMN_OF_NAME = 4;

    public static void main(String[] args) {

        //OsmReader osmReader = new OsmReader("amenity_points.csv");
        //ArrayList<Point> points = osmReader.getEntries();

        FileReader.CreateDatafiles();
        SerialSearch.RANGE_QUERY(new double[]{26.25, 40.00}, new double[]{28.00, 41.5});
        //SerialSearch.K_NN_QUERY(2, new double[]{26.52, 41.5});

        /**
         This is some test points to build R Star.
         */
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(1, new double[]{5, 6}));
        points.add(new Point(2, new double[]{12, 3}));
        points.add(new Point(3, new double[]{14, 8}));
        points.add(new Point(4, new double[]{2, 9}));
        points.add(new Point(5, new double[]{8, 13}));
        points.add(new Point(6, new double[]{10, 6}));
        points.add(new Point(7, new double[]{1, 1}));
        points.add(new Point(8, new double[]{17, 16}));



        //Rectangle with points 0, 1, 2.
        Rectangle<Point> A = new Rectangle<>(new Point[]{points.get(0), points.get(6)}, 1);
        //Rectangle with points 3, 4, 5.
        Rectangle<Point> B = new Rectangle<>(new Point[]{points.get(2), points.get(5)}, 2);

        Rectangle<Rectangle<Object>> C = new Rectangle<>(new Rectangle[]{A, B}, 3);

        //A.printData();
        //B.printData();
        //C.printData();

        RStar rStar = new RStar(C);
        rStar.addRectangle(B);
        rStar.addRectangle(A);

        //rStar.ChooseSubtree(points.get(3)).printData();

        Point P1 = new Point(1, new double[]{1, 1});
        Point P2 = new Point(2, new double[]{8, 13});
        Point P3 = new Point(3, new double[]{-1, 0});
        Point P4 = new Point(4, new double[]{7, 12});

        Rectangle<Point> T1 = new Rectangle<>(new Point[]{P1, P2}, 20);
        Rectangle<Point> T2 = new Rectangle<>(new Point[]{P3, P4}, 21);
        //System.out.println(T1.OverlapCost(T2));


    }
}