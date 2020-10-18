import java.util.Objects;

class Point extends SpaceObject {

    private double[] positions; //Array of doubles to represent the coordinate of the point in each dimension.
    private long id; //Id of the point.
    private int fileId;
    private String name = "";
    private boolean loadFromDatafile;

    /**
     * Maybe we want to add names here. Points will be the actual node itself.
     * Rectangles will be the area that'll contain all the nodes.
     */

    Point() {
        positions = new double[Main.DIMENSIONS];
    }

    Point(long id, double[] positions) {
        this.positions = positions;
        this.id = id;
    }

    Point(long id, double[] positions, int fileId) {
        this(id, positions);
        this.fileId = fileId;
        loadFromDatafile = true;
    }

    Point(long id, double[] positions, String name, int fileId) {
        this(id, positions);
        this.name = name;
        this.fileId = fileId;
    }

    public void LoadNameFromDataFile(){
        this.name = Objects.requireNonNull(FileReader.GetPoint(this.id, this.fileId)).name;
    }

    // return the coordinates of the Point
    double[] getPositions() {
        return positions;
    }

    // return position of Nth dimension.
    double getPosition(int n) {
        return positions[n];
    }

    double getMinValue(int dimension) {
        return positions[dimension];
    }

    double getMaxValue(int dimension) {
        return positions[dimension];
    }

    double[] getCenterPoint() {
        return positions;
    }

    long getId() {
        return id;
    }

    int getFileId() {
        return fileId;
    }

    double DistanceFrom(Point other) {
        double result = 0;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            result += Math.pow(positions[i] - other.positions[i], 2);
        }
        return result;
    }

    String getString() {
        String s = "";
        for (double p : positions) {
            s = s + p + "\t";
        }
        s += "ID: " + id;
        if(loadFromDatafile){
            name = Objects.requireNonNull(FileReader.GetPoint(id, fileId)).name;
        }
        s += "\t" + name;
        return s;
    }
}