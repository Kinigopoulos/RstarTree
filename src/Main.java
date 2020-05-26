import java.util.ArrayList;

public class Main {
    public static void main(String args[]) {

        //OsmReader osmReader = new OsmReader("amenity_points.csv");
        //ArrayList<Point> points = osmReader.getEntries();

        /**
         This is some test points to build R Star.
         */
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(1, new double[]{5, 6}));
        points.add(new Point(2, new double[]{12, 3}));
        points.add(new Point(3, new double[]{14, 8}));
        points.add(new Point(4, new double[]{2, 9}));
        points.add(new Point(5, new double[]{8, 13}));
        points.add(new Point(6, new double[]{10, 6}));
        points.add(new Point(7, new double[]{1, 1}));
        points.add(new Point(8, new double[]{17, 16}));

        /**
         * New Approach, please read before you start changing stuff.
         * It seems that nodes in OSM are just points and nothing more.
         * Let's say that Root bouncing box contains Rectangle R1, R2 and
         * R1 contains Point P1, P2, P3.
         * Class Rectangle will try to handle both bouncing boxes and points
         * by using generics (called templates in C++).
         * Not tested yet, but I hope it works.
         * So in such way on our example, the structure will be as follows:
         * Root will have an ArrayList<T> where T is Rectangle.
         * R1 will have an ArrayList<T> where T is Point.
         * P1, P2, P3 will be Point class and will have their unique id and names.
         */

        //Rectangle with points 0, 1, 2.
        Rectangle<Point> A = new Rectangle<>(new Point[]{points.get(0), points.get(6)}, 1);
        //Rectangle with points 3, 4, 5.
        Rectangle<Point> B = new Rectangle<>(new Point[]{points.get(2), points.get(5)}, 2);

        Rectangle<Rectangle<Object>> C = new Rectangle<>(new Rectangle[]{A, B}, 3);

        A.printData();
        B.printData();
        C.printData();

        RStar rStar = new RStar(C);
        rStar.addRectangle(B);
        rStar.addRectangle(A);

        rStar.ChooseSubtree(points.get(3)).printData();

        Point P1 = new Point(1, new double[]{1, 1});
        Point P2 = new Point(2, new double[]{8, 13});
        Point P3 = new Point(3, new double[]{2, 14});
        Point P4 = new Point(4, new double[]{7, 13});

        Rectangle<Point> T1 = new Rectangle<>(new Point[]{P1, P2}, 20);
        Rectangle<Point> T2 = new Rectangle<>(new Point[]{P3, P4}, 21);
        System.out.println(T1.OverlapCost(T2));


    }
}