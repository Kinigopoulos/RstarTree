import java.util.ArrayList;

class Point {

    // an List of double variables to represent the coordinate of the point in each dimension
    private ArrayList<Double> position;

    Point(){
        position = new ArrayList<>();
    }
    long id;

    // constructor with an Array List as argument
    Point(ArrayList<Double> position){
        this.position = position;
    }

    Point(long id, double... doubles){
        position = new ArrayList<>();
        for(double d : doubles){
            position.add(d);
        }
        this.id=id;
    }
    // return the coordinates of the Point
    ArrayList<Double> getPositions(){
        return position;
    }
    double getPosition(int n){
        return position.get(n);
    }

    // return the number of dimensions the Point has
    int getDimension(){
        return position.size();
    }

    String getString(){
        String s = "";
        for (double p : position){
            s = s + p+" ";
        }
        s = s +"ID : "+id;
        return s;
    }
}