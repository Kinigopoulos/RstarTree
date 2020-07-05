import java.util.ArrayList;

class SerialSearch {

    private static boolean ContainsPoint(Point point, double[] minValues, double[] maxValues) {
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            if (point.getPosition(i) < minValues[i] || point.getPosition(i) > maxValues[i]) {
                return false;
            }
        }
        return true;
    }

    public static void RANGE_QUERY(double[] minValues, double[] maxValues) {
        int[] pointsSize = FileReader.GetDataProperties();
        for (int i = 0; i < pointsSize.length; i++) {
            ArrayList<Point> points = FileReader.GetPoints(i + 1);
            for (Point point : points) {
                if (ContainsPoint(point, minValues, maxValues)) {
                    System.out.println(point.getString());
                }
            }
        }
    }

    public static void K_NN_QUERY(int k, double[] position) {
        Point[] nearest = new Point[k];
        double[] distances = new double[k];
        for(int i = 0; i < k; i++){
            distances[i] = Double.MAX_VALUE;
        }
        int[] pointsSize = FileReader.GetDataProperties();
        for (int i = 0; i < pointsSize.length; i++) {
            ArrayList<Point> points = FileReader.GetPoints(i + 1);
            for (Point point : points) {
                double distance = 0;
                for (int j = 0; j < position.length; j++) {
                    distance += Math.pow(point.getPosition(j) - position[j], 2);
                }
                distance = Math.sqrt(distance);
                if(distances[k - 1] > distance){
                    int j;
                    for(j = k - 2; j >= 0 && distances[j] > distance; j--){
                        distances[j + 1] = distances[j];
                        nearest[j + 1] = nearest[j];
                    }
                    distances[j + 1] = distance;
                    nearest[j + 1] = point;
                }
            }
        }

        for(int i = 0; i < k; i++){
            System.out.println(nearest[i].getString());
        }
    }

}
