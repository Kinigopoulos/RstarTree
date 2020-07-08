class Point extends SpaceObject{

    private double[] positions; //Array of doubles to represent the coordinate of the point in each dimension.
    private long id; //Id of the point.
    private int fileId;
    private String name = "";

    /**
     * Maybe we want to add names here. Points will be the actual node itself.
     * Rectangles will be the area that'll contain all the nodes.
     */

    Point(){
        positions = new double[Main.DIMENSIONS];
    }

    Point(long id, double[] positions){
        this.positions = positions;
        this.id=id;
    }

    Point(long id, double[] positions, String name, int fileId){
        this(id, positions);
        this.name = name;
        this.fileId = fileId;
    }

    // return the coordinates of the Point
    double[] getPositions(){
        return positions;
    }

    // return position of Nth dimension.
    double getPosition(int n){
        return positions[n];
    }

    double getMinValue(int dimension) {
        return positions[dimension];
    }

    double getMaxValue(int dimension) {
        return positions[dimension];
    }

    double[] getCenterPoint(){
        return positions;
    }

    long getId(){
        return id;
    }

    int getFileId(){
        return fileId;
    }

    String getString(){
        String s = "";
        for (double p : positions){
            s = s + p + "\t";
        }
        s += "ID: "+id;
        s += "\t" + name;
        return s;
    }
}