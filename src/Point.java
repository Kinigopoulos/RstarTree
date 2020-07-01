class Point {

    private double[] positions; //Array of doubles to represent the coordinate of the point in each dimension.
    private long id; //Id of the point.

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

    // return the coordinates of the Point
    double[] getPositions(){
        return positions;
    }

    // return position of Nth dimension.
    double getPosition(int n){
        return positions[n];
    }

    String getString(){
        String s = "";
        for (double p : positions){
            s = s + p + " ";
        }
        s = s +"\tID: "+id;
        return s;
    }
}