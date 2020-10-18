import java.util.Arrays;

class Rectangle<T> extends SpaceObject {

    private long id; //Id of the Rectangle.
    private T[] entries; //Points/Rectangles in space.
    private long[] entriesId;
    private int entriesSize;
    private long parentId = -1;
    private boolean isLeafContainer; //It's true when this object contains entries and not rectangles.

    private boolean loadedFromDisk;
    private double[] maxValues = new double[Main.DIMENSIONS];
    private double[] minValues = new double[Main.DIMENSIONS];
    private double area;

    @SuppressWarnings("unchecked")
    private Rectangle(T[] t, long id, boolean isLeafContainer) {
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
    Rectangle(Point[] points, long id) {
        this((T[]) points, id, true);
        isLeafContainer = true;
        ResizeBoundingBox();
    }

    @SuppressWarnings("unchecked")
    Rectangle(Rectangle<?>[] rectangles, long id) {
        this((T[]) rectangles, id, false);
        for (Rectangle<?> rectangle : rectangles) {
            //rectangle.parent = this;
            rectangle.parentId = this.id;
        }
        ResizeBoundingBox();
    }

    Rectangle(long[] entries, int entriesSize, long id, long parentId, boolean leafContainer, double[] min, double[] max, double area) {
        entriesId = entries;
        this.entriesSize = entriesSize;
        this.id = id;
        this.parentId = parentId;
        //if(id > 0) parent = FileReader.getRectangle(parentId);
        isLeafContainer = leafContainer;
        loadedFromDisk = true;
        minValues = min;
        maxValues = max;
        this.area = area;
    }

    Rectangle(T[] entries, int entriesSize, long id, long parentId, boolean isLeafContainer, double[] min, double[] max, double area) {
        this.entries = entries;
        this.entriesSize = entriesSize;
        this.id = id;
        this.parentId = parentId;
        //if(id > 0) parent = FileReader.getRectangle(parentId);
        this.isLeafContainer = isLeafContainer;
        loadedFromDisk = true;
        this.minValues = min;
        this.maxValues = max;
        this.area = area;
    }

    static Rectangle<?> CreateRectangle(SpaceObject[] objects, long id) {
        Rectangle<?> result;
        if (objects instanceof Rectangle[]) {
            result = new Rectangle<>((Rectangle<?>[]) objects, id);
        } else {
            result = new Rectangle<>((Point[]) objects, id);
        }
        return result;
    }

    void SaveRectangle() {
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
        System.out.print(tabs + "ID: " + id + " /  " + loadedFromDisk + " /  ");
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

        //stringBuilder.append(id == 0 ? "-1" : (parent == null ? parentId : parent.getId())).append('\n');
        stringBuilder.append(id == 0 ? "-1" : parentId).append('\n');

        stringBuilder.append(area).append('\n');
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            stringBuilder.append(minValues[i]).append('\n');
            stringBuilder.append(maxValues[i]).append('\n');
        }
        if (loadedFromDisk && !isLeafContainer) {
            int i = 0;
            while (i < Main.MAX_ENTRIES) {
                if (entriesId[i] == 0) {
                    break;
                }
                stringBuilder.append(entriesId[i]).append('\n');
                i++;
            }
            return stringBuilder.toString();
        }
        for (int i = 0; i < entriesSize; i++) {
            SpaceObject object = (SpaceObject) entries[i];
            stringBuilder.append(object.getId()).append('\n');
            if (isLeafContainer) {
                Point point = (Point) object;
                stringBuilder.append(point.getFileId()).append('\n');

                for (int j = 0; j < Main.DIMENSIONS; j++) {
                    stringBuilder.append(point.getPosition(j)).append('\n');
                }
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

    static void ResizeBoundingBoxAfterInsert(Rectangle<?> parent, Rectangle<?> child) {
        double[] min1 = parent.getMinValues();
        double[] min2 = child.getMinValues();
        double[] max1 = parent.getMaxValues();
        double[] max2 = child.getMaxValues();
        boolean affected = false;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            if (min1[i] > min2[i]) {
                min1[i] = min2[i];
                affected = true;
            }
            if (max1[i] < max2[i]) {
                max1[i] = max2[i];
                affected = true;
            }
        }

        if (!affected) {
            return;
        }

        parent.minValues = min1;
        parent.maxValues = max1;
        parent.area = getArea(min1, max1);

        FileReader.EditAreaOfIndex(parent.id, parent.area, parent.minValues, parent.maxValues);
    }

    //This methods adds a point to this object.
    @SuppressWarnings("unchecked")
    void AddPoint(Point entry) {
        if (entries == null) {
            entries = getEntries();
            loadedFromDisk = false;
        }
        entries[entriesSize] = (T) entry;
        entriesSize++;
        ResizeBoundingBox();
    }

    @SuppressWarnings("unchecked")
    void AddPoint(Rectangle<?> entry) {
        entry.parentId = this.id;
        if (entries == null) {
            entries = getEntries();
            loadedFromDisk = false;
        }
        entries[entriesSize] = (T) entry;
        entriesSize++;
        ResizeBoundingBox();
    }

    boolean isFull() {
        return entriesSize > Main.MAX_ENTRIES;
    }

    Rectangle<?> getParent() {
        if (id > 0) {
            return FileReader.getRectangle(parentId);
        }
        return null;
    }

    //Returns the id.
    long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
        if (!pointsToLeafs()) {
            Rectangle<?>[] children = (Rectangle<?>[]) this.getEntries();
            for (Rectangle<?> child : children) {
                child.parentId = this.id;
                child.SaveRectangle();
            }
        }
    }

    //Returns the entries.
    @SuppressWarnings("unchecked")
    T[] getEntries() {
        if (!loadedFromDisk) return Arrays.copyOfRange(entries, 0, entriesSize);


        if (isLeafContainer) {
            return Arrays.copyOfRange(entries, 0, entriesSize);
        } else {
            Rectangle<?>[] rectangles = new Rectangle[Main.MAX_ENTRIES + 1];
            for (int i = 0; i < entriesSize; i++) {
                rectangles[i] = FileReader.getRectangle(entriesId[i]);
            }

            entries = (T[]) rectangles;
        }
        return entries;
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
            //result[i] = maxValues[i] - minValues[i];
            result[i] = (maxValues[i] + minValues[i]) / 2;
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
            }
            if (newMaxValues[i] < rectangle.getMaxValue(i)) {
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

    double MinimumDistance(Point point) {
        double result = 0;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            double r;
            double p = point.getPosition(i);
            if (p < minValues[i]) {
                r = minValues[i];
            } else {
                r = Math.min(p, maxValues[i]);
            }
            result += Math.pow(p - r, 2);
        }
        return Math.sqrt(result);
    }

    double MinMaxDistance(Point point) {
        double result = Double.MAX_VALUE;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            double dist = point.getPosition(i);
            if (dist <= (minValues[i] + maxValues[i]) / 2) {
                dist -= minValues[i];
            } else {
                dist -= maxValues[i];
            }
            dist = Math.pow(dist, 2);
            double sum = 0;
            for (int j = 0; j < Main.DIMENSIONS; j++) {
                if (i != j) {
                    double p = point.getPosition(j);
                    if (p >= (minValues[j] + maxValues[j]) / 2) {
                        sum += Math.pow(p - minValues[i], 2);
                    } else {
                        sum += Math.pow(p - maxValues[i], 2);
                    }
                }
            }
            dist += sum;
            if (dist < result) {
                result = dist;
            }
        }
        return result;
    }

    //Given another rectangle, calculate the overlap value with this one.
    double OverlapCost(Rectangle<?> rectangle) {
        return OverlapCost(rectangle.getMinValues(), rectangle.getMaxValues());
    }

    double OverlapCost(double[] otherMinValues, double[] otherMaxValues) {
        if (Arrays.equals(otherMinValues, minValues) && Arrays.equals(otherMaxValues, maxValues)) {
            return area;
        }
        double overlap = 1;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            if (this.maxValues[i] >= otherMinValues[i] && this.maxValues[i] <= otherMaxValues[i]) {
                overlap *= this.maxValues[i] - Math.max(otherMinValues[i], this.minValues[i]);
            } else if (this.minValues[i] <= otherMaxValues[i] && this.minValues[i] >= otherMinValues[i]) {
                overlap *= Math.min(otherMaxValues[i], this.maxValues[i]) - this.minValues[i];
            } else if (this.maxValues[i] >= otherMaxValues[i] && this.minValues[i] <= otherMinValues[i]) {
                overlap *= otherMaxValues[i] - otherMinValues[i];
            } else if (this.maxValues[i] <= otherMaxValues[i] && this.minValues[i] >= otherMinValues[i]) {
                overlap *= this.maxValues[i] - this.minValues[i];
            } else {
                overlap = 0;
                break;
            }
        }
        return overlap;
    }

}