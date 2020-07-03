import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

class FileReader {

    static int[] GetDataProperties() {
        String fileName = "datafile0";
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
                String[] words = data.split("\t");
                try {
                    double[] position = new double[Main.DIMENSIONS];
                    long id = Long.parseLong(words[0]);
                    for (int i = 0; i < Main.DIMENSIONS; i++) {
                        position[i] = Double.parseDouble(words[i + 1]);
                    }
                    String name = "";
                    if (words.length > 3) {
                        name = words[3];
                    }
                    Point point = new Point(id, position, name);
                    points.add(point);
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                }
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println(fileName + " was not found...");
        }
        return points;
    }

    static void CreateDatafiles() {
        try {
            Scanner scanner = new Scanner(new File(Main.FILENAME));

            final String pageName = "datafile";
            int currentPage = 1;

            ArrayList<Integer> nodesOfPage = new ArrayList<>();

            String line = scanner.nextLine();
            if (Main.HAS_HEADER && scanner.hasNextLine()) {
                line = scanner.nextLine();
            }

            StringBuilder content = new StringBuilder();
            StringBuilder nodeData = new StringBuilder();
            int nodes = 0;
            int currentSize = 0;
            while (scanner.hasNextLine()) {

                String[] words = line.split(Main.SEPARATOR);
                nodeData.append(words[Main.COLUMN_OF_ID]).append('\t');
                for (int i = 0; i < Main.DIMENSIONS; i++) {
                    nodeData.append(words[Main.COLUMNS_OF_POSITION[i]]);
                    if(i != Main.DIMENSIONS - 1){
                        nodeData.append('\t');
                    }
                }
                if (words.length > Main.COLUMN_OF_NAME) {
                    if (!words[Main.COLUMN_OF_NAME].equals("")) {
                        nodeData.append('\t').append(words[Main.COLUMN_OF_NAME]);
                    }
                }
                nodeData.append('\n');
                line = scanner.nextLine();

                int nodeSizeOnDisk = nodeData.toString().getBytes(StandardCharsets.UTF_8).length;
                if (currentSize + nodeSizeOnDisk < Main.MAXIMUM_SIZE) {
                    content.append(nodeData);
                    nodes++;
                    currentSize += nodeSizeOnDisk;
                    nodeData = new StringBuilder();
                } else {
                    WriteDatafile(pageName + currentPage, content.toString());
                    nodesOfPage.add(nodes);
                    currentPage++;
                    content = new StringBuilder();
                    currentSize = 0;
                    nodes = 1;
                }
            }
            if(content.length() > 0){
                WriteDatafile(pageName + currentPage, content.toString());
                nodesOfPage.add(nodes);
            }
            try (BufferedWriter out = new BufferedWriter(new FileWriter(pageName + 0))) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(nodesOfPage.size());
                stringBuilder.append('\n');
                for (int n : nodesOfPage) {
                    stringBuilder.append(n).append('\n');
                }
                out.write(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println(Main.FILENAME + " not found...");
        }
    }

    private static void WriteDatafile(String filename, String data){
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
            out.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
