import java.util.ArrayList;

public class Main {
    public static void main(String args[]){

        OsmReader osmReader = new OsmReader("amenity_points.csv");
        ArrayList<Point> points = osmReader.getPoints();

        ArrayList<Point> p1 = new ArrayList<>();
        // add the first 4 points to a rectangle
        for(int i=0;i<4;i++){
            p1.add(points.get(i));
            System.out.println(points.get(i).getString());
        }

        Rectangle r1 = new Rectangle(p1,0,"r1");
        System.out.println("Coordinates are ");
        ArrayList<Double> c = r1.getCoordinates();
        for(double c1 : c){
            System.out.println(c1);
        }
        System.out.println("Area is "+r1.getArea());

        Rstar rstar = new Rstar();
        //rstar.addRectangle(r1);

    }
}