import java.util.ArrayList;

class Point {

    // an List of double variables to represent the coordinate of the point in each dimension
    private ArrayList<Double> position;

    Point(){
        position = new ArrayList<>();
    }

    // constructor with an Array List as argument
    Point(ArrayList<Double> position){
        this.position = position;
    }

    Point(double... doubles){
        position = new ArrayList<>();
        for(double d : doubles){
            position.add(d);
        }
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

    String print(){
        String s = "";
        for (int i=0 ; i<position.size();i++){
            s = s + (" "+position.get(i));
        }
        return s;
    }
}
