import java.util.Arrays;

class Rectangle<T> {

    private int id; //Id of the Rectangle.
    private T[] entries; //Points/Rectangles in space.
    private int entriesSize;
    private Rectangle<?> parent; //Reference to its parent. If it is null then it's the root.
    private boolean isLeafContainer; //It's true when this object contains entries and not rectangles.

    private double[] maxValues = new double[Main.DIMENSIONS];
    private double[] minValues = new double[Main.DIMENSIONS];
    private double area;

    @SuppressWarnings("unchecked")
    private Rectangle(T[] t, int id, boolean isLeafContainer) {
        if (isLeafContainer) {
            entries = (T[]) new Point[Main.MAX_ENTRIES + 1];
        } else {
            entries = (T[]) new Rectangle[Main.MAX_ENTRIES + 1];
        }
        if (t.length > Main.MAX_ENTRIES + 1) {
            System.out.println("Trying to allocate more entries than allowed. Aborting...");
            return;
        }
        System.arraycopy(t, 0, entries, 0, t.length);
        this.entriesSize = t.length;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    Rectangle(Point[] points, int id) {
        this((T[]) points, id, true);
        isLeafContainer = true;
        minValues = points[0].getPositions().clone();
        maxValues = points[0].getPositions().clone();
        ResizeBoundingBox();
    }

    @SuppressWarnings("unchecked")
    Rectangle(Rectangle<?>[] rectangles, int id) {
        this((T[]) rectangles, id, false);
        for (Rectangle<?> rectangle : rectangles) {
            rectangle.parent = this;
        }
        minValues = rectangles[0].getMinValues().clone();
        maxValues = rectangles[0].getMaxValues().clone();
        ResizeBoundingBox();
    }

    //Help Function, Prints minValues and maxValues.
    void printData() {
        System.out.println("--- ID: " + id + " ---");
        for (double d : minValues) {
            System.out.print(d + " ");
        }
        System.out.println();
        for (double d : maxValues) {
            System.out.print(d + " ");
        }
        System.out.println();
        System.out.println("Area: " + area);
    }

    //Finds lowest (and highest) values in each dimension.
    private void ResizeBoundingBox() {
        if (isLeafContainer) {
            Point[] entries = (Point[]) this.entries;
            for (int i = 0; i < entriesSize; i++) {
                for (int j = 0; j < Main.DIMENSIONS; j++) {
                    if (minValues[j] > entries[i].getPosition(j)) {
                        minValues[j] = entries[i].getPosition(j);
                    }
                    if (maxValues[j] < entries[i].getPosition(j)) {
                        maxValues[j] = entries[i].getPosition(j);
                    }
                }
            }
        } else {
            Rectangle<?>[] entries = (Rectangle<?>[]) this.entries;
            for (int i = 0; i < entriesSize; i++) {
                double[] rectangleMinValues = entries[i].getMinValues();
                double[] rectangleMaxValues = entries[i].getMaxValues();
                for (int j = 0; j < Main.DIMENSIONS; j++) {
                    if (minValues[j] > rectangleMinValues[j]) {
                        minValues[j] = rectangleMinValues[j];
                    }
                    if (maxValues[j] < rectangleMaxValues[j]) {
                        maxValues[j] = rectangleMaxValues[j];
                    }
                }
            }
        }
        area = getArea(minValues, maxValues);
    }

    //This methods adds a point to this object. If there is no space left returns false.
    void AddPoint(T entry) {
        entries[entriesSize] = entry;
        entriesSize++;
        ResizeBoundingBox();
    }

    boolean isFull(){
        return entriesSize != Main.MAX_ENTRIES + 1;
    }

    //Returns the id.
    int getId() {
        return id;
    }

    //Returns the entries.
    T[] getEntries() {
        return Arrays.copyOfRange(entries, 0, entriesSize);
    }

    int getEntriesSize() {
        return entriesSize;
    }

    void setEntriesSize(int entriesSize){
        this.entriesSize = entriesSize;
    }

    double[] getMinValues() {
        return minValues;
    }

    double[] getMaxValues() {
        return maxValues;
    }

    double getMinValue(int dimension) {
        return minValues[dimension];
    }

    double getMaxValue(int dimension) {
        return maxValues[dimension];
    }

    double getArea() {
        return area;
    }

    boolean pointsToLeafs() {
        return isLeafContainer;
    }

    //Function to calculate the Area of the Rectangle.
    static double getArea(double[] minValues, double[] maxValues) {
        double result = 1;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            result *= maxValues[i] - minValues[i];
        }
        return result;
    }

    //Function to calculate the margin of the Rectangle (known also as perimeter).
    static double getMargin(double[] minValues, double[] maxValues) {
        double result = 0;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            result += maxValues[i] - minValues[i];
        }
        return result;
    }

    //Function that returns the area if a given point will be added in the rectangle.
    double AreaEnlargement(Point point) {
        double[] newMinValues = minValues.clone();
        double[] newMaxValues = maxValues.clone();
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            if (newMinValues[i] > point.getPosition(i)) {
                newMinValues[i] = point.getPosition(i);
            } else if (newMaxValues[i] < point.getPosition(i)) {
                newMaxValues[i] = point.getPosition(i);
            }
        }
        return getArea(newMinValues, newMaxValues) - area;
    }

    //Given another rectangle, calculate the overlap value with this one.
    double OverlapCost(Rectangle<?> rectangle) {
        double[] otherMinValues = rectangle.getMinValues();
        double[] otherMaxValues = rectangle.getMaxValues();
        double overlap = 1;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            if (this.maxValues[i] > otherMinValues[i] && this.maxValues[i] < otherMaxValues[i]) {
                overlap *= this.maxValues[i] - Math.max(otherMinValues[i], this.minValues[i]);
            } else if (this.minValues[i] < otherMaxValues[i] && this.minValues[i] > otherMinValues[i]) {
                overlap *= Math.min(otherMaxValues[i], this.maxValues[i]) - this.minValues[i];
            } else if (this.maxValues[i] > otherMaxValues[i] && this.minValues[i] < otherMinValues[i]) {
                overlap *= otherMaxValues[i] - otherMinValues[i];
            } else if (this.maxValues[i] < otherMaxValues[i] && this.minValues[i] > otherMinValues[i]) {
                overlap *= this.maxValues[i] - this.minValues[i];
            } else {
                overlap = 0;
                break;
            }
        }
        return overlap;
    }

    public static int Compare(Rectangle<?> a, Rectangle<?> b, int axis) {
        double difference = a.getMinValue(axis) - b.getMinValue(axis);
        if (difference < 0) {
            return -1;
        } else if (difference > 0) {
            return 1;
        }
        difference = a.getMaxValue(axis) - b.getMaxValue(axis);
        if (difference < 0) {
            return -1;
        } else if (difference > 0) {
            return 1;
        }
        return 0;
    }
}