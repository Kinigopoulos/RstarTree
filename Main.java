import java.util.ArrayList;

public class Main {

    public static void main(String args[]){
        //lat = y
        //lon = x
        ArrayList<Point> points = new ArrayList<>();
        ArrayList<Double> a1 = new ArrayList<>();
        a1.add(0.0);
        a1.add(0.0);
        a1.add(0.0);
        points.add(new Point(a1));
        a1 = new ArrayList<>();
        a1.add(1.0);
        a1.add(0.0);
        a1.add(0.0);
        points.add(new Point(a1));
        a1 = new ArrayList<>();
        a1.add(0.0);
        a1.add(1.0);
        a1.add(1.0);
        points.add(new Point(a1));
        a1 = new ArrayList<>();
        a1.add(1.0);
        a1.add(1.0);
        a1.add(0.0);
        points.add(new Point(a1));
        a1 = new ArrayList<>();
        a1.add(1.0);
        a1.add(1.0);
        a1.add(1.0);
        points.add(new Point(a1));
        a1 = new ArrayList<>();
        a1.add(1.0);
        a1.add(0.0);
        a1.add(1.0);
        points.add(new Point(a1));
        a1 = new ArrayList<>();
        a1.add(0.0);
        a1.add(0.0);
        a1.add(1.0);
        points.add(new Point(a1));
        a1 = new ArrayList<>();
        a1.add(0.0);
        a1.add(1.0);
        a1.add(0.0);
        points.add(new Point(a1));

        Rectangle r = new Rectangle(points);

        System.out.println("Dimension is "+r.getDimension());
        System.out.println("Area is "+r.getArea());


    }

}
