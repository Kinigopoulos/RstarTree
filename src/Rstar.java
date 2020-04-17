import java.util.ArrayList;

class Rstar {

    static private ArrayList<Rectangle> rectangles = new ArrayList<>();

    Rstar(){


    }

    static void AddRectangle(Rectangle rectangle){
        int id = rectangle.getId();
        for(Rectangle r : rectangles){
            if(r.getId() == id){
                r.AddPoint(rectangle.getPoint(0));
                return;
            }
        }
        rectangles.add(rectangle);
    }

    static void print(){
        for(Rectangle rectangle : rectangles)
        System.out.println(rectangle.getName());
    }

}
