import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class osmReader {

    osmReader(String fileName){
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String words[] = data.split(" ");

                //For .txt example
                if(words[0].equals("id")) continue;
                int id = Integer.parseInt(words[0]);
                String name = words[1];
                double x = Double.parseDouble(words[2]);
                double y = Double.parseDouble(words[3]);
                Point point = new Point(x, y);
                Rectangle rectangle = new Rectangle(point, id, name);
                Rstar.AddRectangle(rectangle);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " was not found...");
        }
    }

}
