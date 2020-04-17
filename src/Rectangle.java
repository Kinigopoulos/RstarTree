import java.util.ArrayList;

class Rectangle {

    private int id;
    private String name;
    private ArrayList<Point> points;

    Rectangle(){
        points = new ArrayList<>();
    }

    Rectangle(Point point, int id, String name){
        points = new ArrayList<>();
        points.add(point);
        this.id = id;
        this.name = name;
    }

    Rectangle(ArrayList<Point> points){
        this.points = points;
    }

    void AddPoint(Point point){
        points.add(point);
    }

    String getName(){
        return name;
    }

    int getId(){
        return id;
    }

    Point getPoint(int pos){
        return points.get(pos);
    }
}
