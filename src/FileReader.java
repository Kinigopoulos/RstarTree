import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class FileReader {

    static int[] GetDataProperties(String fileName) {
        int num;
        int[] size;
        try {
            Scanner scanner = new Scanner(new File(fileName));
            num = Integer.parseInt(scanner.nextLine());
            size = new int[num];
            int i = 0;
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                size[i] = Integer.parseInt(data);
                i++;
            }
        } catch (FileNotFoundException e) {
            size = new int[0];
            System.out.println(fileName + " was not found...");
        }
        return size;
    }

    static ArrayList<Point> GetPoints(String fileName) {
        ArrayList<Point> points = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(fileName));

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] words = data.split(Main.SEPARATOR);
                try {
                    double[] position = new double[Main.DIMENSIONS];
                    long id;
                    for (int i = 0; i < Main.DIMENSIONS; i++) {
                        position[i] = Double.parseDouble(words[Main.COLUMNS_OF_POSITION[i]]);
                    }
                    id = Long.parseLong(words[Main.COLUMN_OF_ID]);

                    Point point = new Point(id, position);
                    points.add(point);
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) { }
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println(fileName + " was not found...");
        }
        return points;
    }

}
