class Rectangle<T> {

    private int id; //Id of the Rectangle.
    private T[] entries; //Points/Rectangles in space.
    private int entriesSize;
    private Rectangle parent; //Reference to its parent. If it is null then it's the root.
    private boolean isLeafContainer; //It's true when this object contains entries and not rectangles.

    private double[] maxValues = new double[RStar.DIMENSIONS];
    private double[] minValues = new double[RStar.DIMENSIONS];

    @SuppressWarnings("unchecked")
    private Rectangle(T[] t, int id, boolean isLeafContainer) {
        if (isLeafContainer) {
            entries = (T[]) new Point[RStar.MAX_ENTRIES];
        } else {
            entries = (T[]) new Rectangle[RStar.MAX_ENTRIES];
        }
        if (t.length > RStar.MAX_ENTRIES) {
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
    Rectangle(Rectangle[] rectangles, int id) {
        this((T[]) rectangles, id, false);
        minValues = rectangles[0].getMinValues().clone();
        maxValues = rectangles[0].getMaxValues().clone();
        ResizeBoundingBox();
    }

    //Help Function, Prints minValues and maxValues.
    void printData() {
        for (double d : minValues) {
            System.out.print(d + " ");
        }
        System.out.println();
        for (double d : maxValues) {
            System.out.print(d + " ");
        }
        System.out.println();
    }

    //Finds lowest (and highest) values in each dimension.
    private void ResizeBoundingBox() {
        if (isLeafContainer) {
            Point[] entries = (Point[]) this.entries;
            for (int i=0; i<entriesSize; i++) {
                for (int j = 0; j < RStar.DIMENSIONS; j++) {
                    if (minValues[j] > entries[i].getPosition(j)) {
                        minValues[j] = entries[i].getPosition(j);
                    }
                    if (maxValues[j] < entries[i].getPosition(j)) {
                        maxValues[j] = entries[i].getPosition(j);
                    }
                }
            }
        } else {
            Rectangle[] entries = (Rectangle[]) this.entries;
            for (int i=0; i<entriesSize; i++) {
                double[] rectangleMinValues = entries[i].getMinValues();
                double[] rectangleMaxValues = entries[i].getMaxValues();
                for (int j = 0; j < RStar.DIMENSIONS; j++) {
                    if (minValues[j] > rectangleMinValues[j]) {
                        minValues[j] = rectangleMinValues[j];
                    }
                    if (maxValues[j] < rectangleMaxValues[j]) {
                        maxValues[j] = rectangleMaxValues[j];
                    }
                }
            }
        }
    }

    //This methods adds a point to this object. If there is no space left returns false.
    boolean AddPoint(T entry) {
        if (entriesSize == RStar.MAX_ENTRIES) {
            System.out.println("Cannot add more entries. Aborting...");
            return false;
        }
        entries[entriesSize] = entry;
        entriesSize++;
        ResizeBoundingBox();
        return true;
    }

    //Returns the id.
    int getId() {
        return id;
    }

    //Returns the entries.
    T[] getEntries() {
        return entries;
    }

    private double[] getMinValues() {
        return minValues;
    }

    private double[] getMaxValues() {
        return maxValues;
    }

    //Function to calculate the Area of the Rectangle.
    double getArea() {
        double result = 1;
        for (int i = 0; i < RStar.DIMENSIONS; i++) {
            result *= maxValues[i] - minValues[i];
        }
        return result;
    }


}