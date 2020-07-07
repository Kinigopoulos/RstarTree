import java.util.Arrays;

class Rectangle<T> extends SpaceObject {

    private long id; //Id of the Rectangle.
    private T[] entries; //Points/Rectangles in space.
    private long[] entriesId;
    private int entriesSize;
    private Rectangle<?> parent; //Reference to its parent. If it is null then it's the root.
    private long parentId = -1;
    private boolean isLeafContainer; //It's true when this object contains entries and not rectangles.

    private boolean loadedFromDisk = true;
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
        loadedFromDisk = false;
    }

    @SuppressWarnings("unchecked")
    Rectangle(Point[] points, int id) {
        this((T[]) points, id, true);
        isLeafContainer = true;
        ResizeBoundingBox();
    }

    @SuppressWarnings("unchecked")
    Rectangle(Rectangle<?>[] rectangles, int id) {
        this((T[]) rectangles, id, false);
        for (Rectangle<?> rectangle : rectangles) {
            rectangle.parent = this;
        }
        ResizeBoundingBox();
    }

    Rectangle(long[] entries, long id, long parent, boolean leafContainer, double[] min, double[] max, double area) {
        entriesId = entries;
        this.id = id;
        parentId = parent;
        isLeafContainer = leafContainer;
        minValues = min;
        maxValues = max;
        this.area = area;
    }

    Rectangle(T[] entries, long id, long parentId, boolean isLeafContainer, double[] min, double[] max, double area) {
        this.entries = entries;
        this.id = id;
        this.parentId = parentId;
        this.isLeafContainer = isLeafContainer;
        this.minValues = min;
        this.maxValues = max;
        this.area = area;
    }

    static Rectangle<?> CreateRectangle(SpaceObject[] objects, int id) {
        Rectangle<?> result;
        if (objects instanceof Rectangle[]) {
            result = new Rectangle<>((Rectangle<?>[]) objects, id);
        } else {
            result = new Rectangle<>((Point[]) objects, id);
        }
        return result;
    }

    void SaveRectangle() {
        if(id == -1){
            this.getData();
            System.out.println("-------------------------------------YOU MESSED UP WITH ID-------------------------------------");
            System.exit(0);

        }
        FileReader.CreateIndexFile(this);
    }

    //Help Function, Prints minValues and maxValues.
    void printData() {
        Rectangle<?> parent = this.getParent();

        System.out.print(parent == null ? "" : parent.getId());
        String tabs = "";
        while (parent != null) {
            tabs += '\t';
            parent = parent.getParent();
        }
        System.out.print(tabs + "ID: " + id + " /  ");
        for (double d : minValues) {
            System.out.print(d + " ");
        }
        System.out.print("/ ");
        for (double d : maxValues) {
            System.out.print(d + " ");
        }
        System.out.println("/ Area: " + area + ", size: " + entriesSize + ", parentId: " + parentId);
        if (pointsToLeafs()) {
            tabs += '\t';
            for (int i = 0; i < entriesSize; i++) {
                System.out.println(tabs + ((Point) entries[i]).getString());
            }
        }
    }

    String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(isLeafContainer ? "D" : "R"); //If it points to leafs then is labelled as Data holder.
        stringBuilder.append('\n');
        stringBuilder.append(parent == null ? "-1" : parent.getId()).append('\n');
        stringBuilder.append(area).append('\n');
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            stringBuilder.append(minValues[i]).append('\n');
            stringBuilder.append(maxValues[i]).append('\n');
        }
        for (int i = 0; i < entriesSize; i++) {
            SpaceObject object = (SpaceObject) entries[i];
            stringBuilder.append(object.getId()).append('\n');
            if (isLeafContainer) {
                Point point = (Point) object;
                stringBuilder.append(point.getFileId()).append('\n');
            }
        }
        return stringBuilder.toString();
    }

    //Finds lowest (and highest) values in each dimension.
    void ResizeBoundingBox() {
        if (isLeafContainer) {
            Point[] entries = (Point[]) this.getEntries();
            minValues = entries[0].getPositions().clone();
            maxValues = entries[0].getPositions().clone();

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
            Rectangle<?>[] entries = (Rectangle<?>[]) this.getEntries();
            minValues = entries[0].getMinValues().clone();
            maxValues = entries[0].getMaxValues().clone();

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

    static void ResizeBoundingBoxAfterInsert(Rectangle<?> parent, Rectangle<?> child){
        double[] min1 = parent.getMinValues();
        double[] min2 = child.getMinValues();
        double[] max1 = parent.getMaxValues();
        double[] max2 = child.getMaxValues();
        for(int i = 0; i < Main.DIMENSIONS; i++){
            if(min1[i] > min2[i]){
                min1[i] = min2[i];
            }
            if(max1[i] < max2[i]){
                max1[i] = max2[i];
            }
        }
        parent.SaveRectangle();
    }

    //This methods adds a point to this object.
    @SuppressWarnings("unchecked")
    void AddPoint(Point entry) {
        entries[entriesSize] = (T) entry;
        entriesSize++;
        ResizeBoundingBox();
    }

    @SuppressWarnings("unchecked")
    void AddPoint(Rectangle<?> entry) {
        entry.parent = this;
        entries[entriesSize] = (T) entry;
        entriesSize++;
        ResizeBoundingBox();
    }

    boolean isFull() {
        return entriesSize > Main.MAX_ENTRIES;
    }

    Rectangle<?> getParent() {
        if (loadedFromDisk) return parentId != -1 ? FileReader.getRectangle(parentId) : null;
        return parent;
    }

    //Returns the id.
    long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
        System.out.println("IS LOADED FROM DISK: " + loadedFromDisk + " and the id is: " + id);
        if(!pointsToLeafs()){
            Rectangle<?>[] children = (Rectangle<?>[])this.getEntries();
            for(Rectangle<?> child : children){
                child.parent = this;
                child.parentId = this.id;
                child.SaveRectangle();
            }
        }
    }

    //Returns the entries.
    @SuppressWarnings("unchecked")
    T[] getEntries() {
        if (!loadedFromDisk) return Arrays.copyOfRange(entries, 0, entriesSize);


        T[] result;
        if (isLeafContainer) {
            return Arrays.copyOfRange(entries, 0, entriesSize);
//            Point[] points = new Point[entriesSize];
//            for(int i=0; i<entriesSize; i++) {
//                points[i] = FileReader.GetPoint(entriesId[i], pagesId[i]);
//            }
//            result = (T[])points;
        } else {
            Rectangle<?>[] rectangles = new Rectangle[entriesSize];
            for (int i = 0; i < entriesSize; i++) {
                rectangles[i] = FileReader.getRectangle(entriesId[i]);
            }
            result = (T[]) rectangles;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    void setEntries(SpaceObject[] entries) {
        this.entries = (T[]) entries;
    }

    int getEntriesSize() {
        return entriesSize;
    }

    void setEntriesSize(int entriesSize) {
        this.entriesSize = entriesSize;
        ResizeBoundingBox();
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

    double[] getCenterPoint() {
        double[] result = new double[Main.DIMENSIONS];
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            result[i] = maxValues[i] - minValues[i];
        }
        return result;
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

    double AreaEnlargement(Rectangle<?> rectangle) {
        double[] newMinValues = minValues.clone();
        double[] newMaxValues = maxValues.clone();
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            if (newMinValues[i] > rectangle.getMinValue(i)) {
                newMinValues[i] = rectangle.getMinValue(i);
            } else if (newMaxValues[i] < rectangle.getMaxValue(i)) {
                newMaxValues[i] = rectangle.getMaxValue(i);
            }
        }
        return getArea(newMinValues, newMaxValues) - area;
    }

    static boolean ContainsPoint(Point point, double[] minValues, double[] maxValues) {
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            if (point.getPosition(i) < minValues[i] || point.getPosition(i) > maxValues[i]) {
                return false;
            }
        }
        return true;
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

}