import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class OsmReader {

    private ArrayList<Point> points = new ArrayList<>();

    OsmReader(String fileName){
        try {
            Scanner scanner = new Scanner(new File(fileName));

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String words[] = data.split(",");

                //For .csv example
                if(words[0].equals("X")) continue;

                double x = Double.parseDouble(words[0]);
                double y = Double.parseDouble(words[1]);
                long id = Long.parseLong(words[2]);
                Point point = new Point(id, new double[]{x,y});

                points.add(point);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " was not found...");
        }
    }

    ArrayList<Point> getPoints(){
        return points;
    }

}
