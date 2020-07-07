import java.io.File;
import java.util.ArrayList;

public class Main {

    /////STATIC VARIABLES\\\\\
    //R* TREE VARIABLES
    // Defines that no more than the given number of rectangles or points can be contained in each node.
    public static final int MAX_ENTRIES = 10;
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

        //Creates datafile files
        FileReader.CreateDatafiles();


        SerialSearch.RANGE_QUERY(new double[]{26.25, 40.00}, new double[]{28.00, 41.5});
        //SerialSearch.K_NN_QUERY(2, new double[]{26.52, 41.5});


        RStar rStar = new RStar();
        int[] dataSizes = FileReader.GetDataProperties();
        for(int i = 0; i < dataSizes.length; i++){
            ArrayList<Point> points = FileReader.GetPoints(i);
            for(Point point : points){
                rStar.InsertData(point);
            }
        }

        rStar.RANGE_QUERY(new double[]{26.25, 40.00}, new double[]{28.00, 41.5});

        /**
         This is some test points to build R Star.
         */

        /*
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(1, new double[]{5, 6}));
        points.add(new Point(2, new double[]{12, 3}));
        points.add(new Point(3, new double[]{14, 8}));
        points.add(new Point(4, new double[]{2, 9}));
        points.add(new Point(5, new double[]{8, 13}));
        points.add(new Point(6, new double[]{10, 6}));
        points.add(new Point(7, new double[]{1, 1}));
        points.add(new Point(8, new double[]{17, 16}));
        points.add(new Point(9, new double[]{4, 4}));
        points.add(new Point(10, new double[]{7, 2}));
        points.add(new Point(11, new double[]{2, 19}));
        points.add(new Point(12, new double[]{8, 7}));
        points.add(new Point(13, new double[]{7, 8}));
        points.add(new Point(14, new double[]{2, 2}));
        points.add(new Point(15, new double[]{3, 3}));
        points.add(new Point(16, new double[]{12, 15}));
        points.add(new Point(17, new double[]{1, 0}));


        RStar rStar = new RStar(points.get(0));
        rStar.InsertData(points.get(1));
        rStar.InsertData(points.get(2));
        rStar.InsertData(points.get(3));
        rStar.InsertData(points.get(4));
        rStar.InsertData(points.get(5));
        rStar.InsertData(points.get(6));
        rStar.InsertData(points.get(7));
        rStar.InsertData(points.get(8));
        rStar.InsertData(points.get(9));
        rStar.InsertData(points.get(10));
        rStar.InsertData(points.get(11));
        rStar.InsertData(points.get(12));
        rStar.InsertData(points.get(13));
        rStar.InsertData(points.get(14));
        rStar.InsertData(points.get(15));
        rStar.InsertData(points.get(16));
        rStar.printAll();
        */


    }
}