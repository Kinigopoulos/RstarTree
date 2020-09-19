import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
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
        float startTime = System.nanoTime();
        WriteFile("indexFile" + rectangle.getId(), rectangle.getData());
        float endTime = System.nanoTime();
        //System.out.println("Wrote a file in: " + (endTime - startTime));
    }


    public static void CheckRStar(){
        ArrayList<Rectangle<?>> rectangles = new ArrayList<>();
        rectangles.add(getRectangle(0));

        boolean[] ids = new boolean[(int)RStar.nodes];
        ids[0] = true;


        while(!rectangles.isEmpty()){
            Rectangle<?> N = rectangles.get(0);
            rectangles.remove(0);
            if(N.pointsToLeafs()){
                continue;
            }
            Rectangle<?>[] children = (Rectangle<?>[]) N.getEntries();

            for(int i = 0; i < N.getEntriesSize(); i++){
                int id = (int)children[i].getId();
                if(ids[id]){
                    System.out.println("DOUBLE ID: " + id);
                }
                ids[id] = true;
                rectangles.add(children[i]);
            }


        }

        boolean correct = true;
        int i = 0;
        for(boolean id : ids){
            if(!id){
                correct = false;
                System.out.println("NON EXISTENT ID: " + i);
            }
            i++;
        }
        System.out.println("Rstar's correctness is " + correct);
    }

    static HashSet<Long> ids = new HashSet<>();
    public static void CheckForDuplicates(){

        int id = 0;
        ids = new HashSet<>();
        while (id < RStar.nodes){
            try{
                Scanner scanner = new Scanner(new File(INDEXFILE + id));
                if(scanner.nextLine().equals("D")){
                    //System.out.println("ID TO READ " + id);
                    Rectangle<?> rectangle = getRectangle(id);
                    Point[] points = (Point[]) rectangle.getEntries();

                    scanner.nextLine(); scanner.nextLine(); scanner.nextLine();
                    scanner.nextLine(); scanner.nextLine();

                    for(int i=0; i<rectangle.getEntriesSize(); i++){

                        if(ids.contains(points[i].getId())){
                            long ahaId = points[i].getId();
                            System.out.println(points[i].getId());

                        }else{
                            ids.add(points[i].getId());
                        }

                    }

                }
                id++;
            }catch (FileNotFoundException ignored){
                System.out.println("File not found");
                break;
            }
        }
    }

    public static void EditAreaOfIndex(long id, double area, double[] min, double[] max) {
        try{
            Scanner scanner = new Scanner(new File(INDEXFILE + id));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(scanner.nextLine()).append('\n');//D or R
            stringBuilder.append(scanner.nextLine()).append('\n');//Parent ID
            //Area
            scanner.nextLine();
            stringBuilder.append(area).append('\n');
            for(int i=0; i < Main.DIMENSIONS; i++){
                stringBuilder.append(min[i]).append('\n');
                scanner.nextLine();
                stringBuilder.append(max[i]).append('\n');
                scanner.nextLine();
            }
            while (scanner.hasNextLine()){
                stringBuilder.append(scanner.nextLine()).append('\n');
            }
            WriteFile(INDEXFILE + id, stringBuilder.toString());


        } catch (FileNotFoundException ignored){}
    }

    public static Rectangle<?> getRectangle(long id){
        float startTime = System.nanoTime();

        Rectangle<?> result = null;
        try {
            Scanner scanner = new Scanner(new File(INDEXFILE + id));
            String line = scanner.nextLine();
            boolean pointsToLeafs = line.equals("D");
            line = scanner.nextLine();
            long parent = Long.parseLong(line);
            line = scanner.nextLine();
            double area = Double.parseDouble(line);
            double[] min = new double[Main.DIMENSIONS];
            double[] max = new double[Main.DIMENSIONS];
            line = scanner.nextLine();
            for(int i=0; i<Main.DIMENSIONS; i++){
                min[i] = Double.parseDouble(line);
                line = scanner.nextLine();
                max[i] = Double.parseDouble(line);
                line = scanner.nextLine();
            }
            long[] children = new long[Main.MAX_ENTRIES + 1];
            int[] pageId = new int[Main.MAX_ENTRIES];
            int i = 0;
            while(scanner.hasNextLine()){
                children[i] = Long.parseLong(line);
                line = scanner.nextLine();
                if(pointsToLeafs){
                    pageId[i] = Integer.parseInt(line);
                    if(scanner.hasNextLine()){
                        line = scanner.nextLine();
                    }
                }
                i++;
            }
            if(pointsToLeafs) {
                Point[] points = new Point[Main.MAX_ENTRIES + 1];
                for (int j = 0; j < i; j++){
                    points[j] = GetPoint(children[j], pageId[j]);
                }
                result = new Rectangle<>(points, i, id, parent, true, min, max, area);
            }
            else {
                children[i] = Long.parseLong(line);
                i++;
                result = new Rectangle<>(children, i, id, parent, false, min, max, area);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find IndexFile" + id);
            e.printStackTrace();
        } catch (NoSuchElementException e){
            System.out.println("Failed to read IndexFile" + id);
            e.printStackTrace();
        }

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];//maybe this number needs to be corrected
        String methodName = e.getMethodName();

        float endTime = System.nanoTime();
        //System.out.println("Read a file in: " + (endTime - startTime) + "\nID: " + id + "\tMethod used: " + methodName);

        return result;
    }
}
