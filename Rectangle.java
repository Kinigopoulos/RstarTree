import java.util.ArrayList;

class Rectangle {

    private int id;
    private String name;
    private ArrayList<Point> points;
    // the dimension in which the Rectangle exist
    private int dimension;

    Rectangle(){
        points = new ArrayList<>();
    }

    Rectangle(Point point, int id, String name){
        points = new ArrayList<>();
        points.add(point);
        this.id = id;
        this.name = name;
        // calculate the dimension based on the points
        dimension = points.get(0).getDimension();
    }

    Rectangle(ArrayList<Point> points){
        // calculate the dimension based on the points
        dimension = points.get(0).getDimension();
        this.points = points;
    }


    String getName(){
        return name;
    }

    int getId(){
        return id;
    }

    int getDimension(){return dimension;}

    Point getPoint(int n){
        return points.get(n);
    }
    ArrayList<Point> getPoints(){
        return points;
    }

    // function to calculate the Area of the Rectangle for all dimensions
    double getArea(){
        // for each dimension we count the sum of the differnces from the 1st point to the others
        double[] sum = new double[dimension];

        // we get the first point
        Point point = points.get(0);

        // for all the other points we sum the difference in the coordinates
        // for all the dimensions
        for (int i=1;i<points.size();i++){

            for ( int d = 0; d < dimension; d++){
                sum[d] += Math.abs(points.get(i).getPosition(d) - point.getPosition(d));
            }
        }

        // we divide each sum with the 2^dimension / 2
        for (int i=0;i<dimension;i++){
            sum[i] = sum[i]/(Math.pow(2,dimension)/2);
        }
        // we multiple all the sums together
        double s = 1;
        for (int i=0;i<dimension;i++){
            s *= sum[i];
        }
        return s;

    }


}
