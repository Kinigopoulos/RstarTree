import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class osmReader {

    osmReader(String fileName){
        try {
            Scanner scanner = new Scanner(new File(fileName));
            int counter = 0;
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                System.out.println(data);
                counter++;
            }
            System.out.println(counter);
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " was not found...");
        }
    }

}
