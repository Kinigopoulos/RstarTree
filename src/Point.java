import java.util.ArrayList;

class Point {

    private ArrayList<Double> position;

    Point(){
        position = new ArrayList<>();
    }

    Point(ArrayList<Double> position){
        this.position = position;
    }

    Point(double... doubles){
        position = new ArrayList<>();
        for(double d : doubles){
            position.add(d);
        }
    }

    ArrayList<Double> getPosition(){
        return position;
    }
}
