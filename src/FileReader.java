import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

class FileReader {

    static final String DATAFILE = "datafile";
    static final String INDEXFILE = "indexfile";

    static int[] GetDataProperties() {
        String fileName = DATAFILE + 0;
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

    static ArrayList<Point> GetPoints(int fileId) {
        String fileName = DATAFILE + fileId;
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
                    Point point = new Point(id, position, name, fileId);
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

    static Point GetPoint(long id, int fileId){
        ArrayList<Point> points = GetPoints(fileId);
        for(Point point : points){
            if(point.getId() == id){
                return point;
            }
        }
        return null;
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
                    if (i != Main.DIMENSIONS - 1) {
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
                    WriteFile(pageName + currentPage, content.toString());
                    nodesOfPage.add(nodes);
                    currentPage++;
                    content = new StringBuilder();
                    currentSize = 0;
                    nodes = 1;
                }
            }
            if (content.length() > 0) {
                WriteFile(pageName + currentPage, content.toString());
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

    private static void WriteFile(String filename, String data) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
            out.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void CreateIndexFile(Rectangle<?> rectangle) {
        WriteFile("indexFile" + rectangle.getId(), rectangle.getData());
    }

    public static Rectangle<?> getRectangle(long id){
        Rectangle<?> result = null;
        try {
            Scanner scanner = new Scanner(new File("indexFile" + id));
            String line = scanner.nextLine();
            boolean pointsToLeafs = line.equals("D");
            line = scanner.nextLine();
            long parent = Long.parseLong(line);
            double[] min = new double[Main.DIMENSIONS];
            double[] max = new double[Main.DIMENSIONS];
            line = scanner.nextLine();
            for(int i=0; i<Main.DIMENSIONS; i++){
                min[i] = Double.parseDouble(line);
                line = scanner.nextLine();
                max[i] = Double.parseDouble(line);
                line = scanner.nextLine();
            }
            long[] children = new long[Main.MAX_ENTRIES];
            int i = 0;
            while(scanner.hasNextLine()){
                children[i] = Long.parseLong(line);
                line = scanner.nextLine();
                i++;
            }

            result = new Rectangle<>(children, id, parent, pointsToLeafs, min, max);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
