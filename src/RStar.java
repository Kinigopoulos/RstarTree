import java.util.ArrayList;
import java.util.Arrays;

class RStar {

    private ArrayList<Rectangle<?>> rectangles;
    private boolean isReinserting = false;
    private int leafLevel;

    RStar() {
        rectangles = new ArrayList<>();
        leafLevel = 0;
    }

    RStar(Point point) {
        this();
        rectangles.add(new Rectangle<>(new Point[]{point}, 0));
    }

    double Overlap(Rectangle<?> rectangle) {
        double area = rectangle.getArea();
        double totalOverlap = 0;
        ArrayList<Rectangle<?>> rectanglesToLook = new ArrayList<>();
        if(rectangles.get(0).pointsToLeafs()){
            return 0;
        }
        rectanglesToLook.add(rectangles.get(0)); //Add the root to begin looking for overlaps
        while (!rectanglesToLook.isEmpty()) {
            Rectangle<?>[] currentChildren = (Rectangle<?>[]) rectanglesToLook.get(0).getEntries();
            for (int i = 0; i < rectanglesToLook.get(0).getEntriesSize(); i++) {
                double overlap = rectangle.OverlapCost(currentChildren[i]);
                //If the overlap is the same as the area it means that the rectangle either includes the
                //rectangle which we calculate the overlap for or they are the same.
                if (overlap == area) {
                    if (!currentChildren[i].pointsToLeafs()) {
                        rectanglesToLook.add(currentChildren[i]);
                    }
                } else {
                    totalOverlap += overlap;
                }
            }
            rectanglesToLook.remove(0);
        }
        return totalOverlap;
    }

    Rectangle<?> ChooseSubtree(Rectangle<?> point, int level) {
        Rectangle<?> N = rectangles.get(0); //N is equal to the root.
        while (level > 0) {
            Rectangle<?>[] children = (Rectangle<?>[]) N.getEntries();
            int bestRectangle = 0; //Starting by assuming that the best rectangle is the first.
            double bestArea = children[0].AreaEnlargement(point);
            if (children[0].pointsToLeafs()) {
                double minOverlap = Double.MAX_VALUE;
                for (int i = 0; i < N.getEntriesSize(); i++) {
                    Rectangle<Point> copy = new Rectangle<>((Point[]) children[i].getEntries(), 0);
                    copy.AddPoint(point);
                    double copyOverlap = Overlap(copy);
                    System.out.println("Overlap: " + copyOverlap);
                    if (copyOverlap < minOverlap) {
                        bestRectangle = i;
                        bestArea = copy.getArea();
                        minOverlap = copyOverlap;
                    } else if (copyOverlap == minOverlap) {
                        double area = copy.getArea();
                        if (bestArea > area) {
                            bestRectangle = i;
                            bestArea = area;
                        } else if (bestArea == area) {
                            if (children[bestRectangle].getArea() > children[i].getArea()) {
                                bestRectangle = i;
                            }
                        }
                    }
                }
            } else {
                for (int i = 1; i < N.getEntriesSize(); i++) {
                    if (bestArea > children[i].AreaEnlargement(point)) {
                        bestRectangle = i;
                    } else if (bestArea == children[i].AreaEnlargement(point)) {
                        if (children[bestRectangle].getArea() > children[i].getArea()) {
                            bestRectangle = i;
                        }
                    }
                }
            }
            N = children[bestRectangle];
            level--;
        }
        return N;
    }

    private Rectangle<?> Split(Rectangle<?> rectangle) {
        int axis = ChooseSplitAxis(rectangle);
        int index = ChooseSplitIndex(rectangle, axis);
        SpaceObject[] children = (SpaceObject[]) rectangle.getEntries();
        Rectangle<?> other = Rectangle.CreateRectangle(Arrays.copyOfRange(children, index, rectangle.getEntriesSize()), 0);
        rectangle.setEntriesSize(index - 1);
        return other;
    }

    private int ChooseSplitAxis(Rectangle<?> rectangle) {
        final int M = rectangle.getEntriesSize();
        final int m = Main.MINIMUM_ENTRIES_PERCENTAGE * M / 100;
        final int kMax = M - 2 * m + 1;
        int kEnd = m + kMax;

        SpaceObject[] children;
        if (rectangle.pointsToLeafs()) {
            children = new Point[M];
        } else {
            children = new Rectangle[M];
        }
        System.arraycopy((SpaceObject[]) rectangle.getEntries(), 0, children, 0, M);

        double bestMargin = Double.MAX_VALUE;
        int bestAxis = 0;
        for (int axis = 0; axis < Main.DIMENSIONS; axis++) {
            double totalMargin = 0;
            for (int sortType = 0; sortType < 2; sortType++) {
                if (sortType == 0) {
                    int finalAxis = axis;
                    Arrays.sort(children, (a, b) -> SpaceObject.Compare(a, b, finalAxis));
                } else {
                    SpaceObject[] reverse = children.clone();
                    for (int i = 0; i < M; i++) {
                        reverse[i] = children[M - i - 1];
                    }
                    children = reverse;
                }
                for (int k = m + 1; k <= kEnd; k++) {
                    totalMargin += CalculateMargin(Arrays.copyOfRange(children, 0, k));
                    totalMargin += CalculateMargin(Arrays.copyOfRange(children, k, M));
                }
            }

            if (totalMargin < bestMargin) {
                bestMargin = totalMargin;
                bestAxis = axis;
            }
        }
        return bestAxis;
    }

    private double CalculateMargin(SpaceObject[] rectangles) {
        Rectangle<?> temp;
        if (rectangles instanceof Point[])
            temp = new Rectangle<>((Point[]) rectangles, -1);
        else {
            temp = new Rectangle<>((Rectangle<?>[]) rectangles, -1);
        }
        return Rectangle.getMargin(temp.getMinValues(), temp.getMaxValues());
    }

    private int ChooseSplitIndex(Rectangle<?> rectangle, int axis) {
        double minOverlap = Double.MAX_VALUE;
        double minArea = Double.MAX_VALUE;
        int bestIndex = 0;
        final int M = rectangle.getEntriesSize();
        final int m = Main.MINIMUM_ENTRIES_PERCENTAGE * M / 100;
        final int kMax = (M - 2 * m + 1);

        SpaceObject[] children;
        if (rectangle.pointsToLeafs()) {
            children = new Point[M];
        } else {
            children = new Rectangle[M];
        }
        System.arraycopy((SpaceObject[]) rectangle.getEntries(), 0, children, 0, M);

        Arrays.sort(children, (a, b) -> SpaceObject.Compare(a, b, axis));

        rectangle.setEntries(children);

        for (int i = m + 1; i <= m + kMax; i++) {
            double currentOverlap = 0;

            Rectangle<?> split1 = Rectangle.CreateRectangle((Arrays.copyOfRange(children, 0, i)), -1);
            Rectangle<?> split2 = Rectangle.CreateRectangle(Arrays.copyOfRange(children, i, children.length), -2);
            currentOverlap += Overlap(split1);
            currentOverlap += Overlap(split2);

            if (currentOverlap < minOverlap) {
                minOverlap = currentOverlap;
                minArea = split1.getArea() + split2.getArea();
                bestIndex = i;
            } else if (currentOverlap == minOverlap) {
                double currentArea = split1.getArea() + split2.getArea();
                if (currentArea < minArea) {
                    minOverlap = currentOverlap;
                    minArea = currentArea;
                    bestIndex = i;
                }
            }
        }

        return bestIndex;
    }

    void InsertData(Point point) {
        Insert(point, leafLevel);
    }

    private void Insert(Object Entry, int level) {
        final boolean isPoint = Entry instanceof Point;
        Rectangle<?> RectEntry;
        if (isPoint) {
            RectEntry = new Rectangle<Point>(new Point[]{(Point) Entry}, 0);
        } else {
            RectEntry = (Rectangle<?>) Entry;
        }
        Rectangle<?> N = ChooseSubtree(RectEntry, level);
        boolean mustSplit = N.isFull();
        if (!mustSplit) {
            if (Entry instanceof Point) {
                N.AddPoint((Point) Entry);
            } else {
                N.AddPoint((Rectangle<?>) Entry);
            }
        } else {
            Rectangle<?> result = OverflowTreatment(N, level);
            Rectangle<?> parent = N.getParent();
            if (result != null && parent != null) {
                parent.AddPoint(result);
                OverflowTreatment(N.getParent(), level - 1);
            } else if (result != null) {
                //TODO: create new root
                Rectangle<?> previousRoot = rectangles.get(0);
                Rectangle<?> newRoot = new Rectangle<>(new Rectangle<?>[]{previousRoot, result}, 0);
                rectangles.set(0, newRoot);
                leafLevel++;
            }
        }
        while (N != null) {
            N.ResizeBoundingBox();
            N = N.getParent();
        }
    }

    private Rectangle<?> OverflowTreatment(Rectangle<?> N, int level) {
        if (!isReinserting && N.getParent() != null) {
            Reinsert(N, level);
            isReinserting = true;
            return null;
        }
        return Split(N);
    }

    static class ReinsertStruct {
        Rectangle<?> child;
        double distance;

        ReinsertStruct(Rectangle<?> child, double distance) {
            this.child = child;
            this.distance = distance;
        }

        static int compareTo(ReinsertStruct a, ReinsertStruct b) {
            if (a.distance < b.distance) {
                return -1;
            } else if (a.distance > b.distance) {
                return 1;
            }
            return 0;
        }
    }

    private void Reinsert(Rectangle<?> N, int level) {
        final int length = N.getEntriesSize();
        final int p = 30 * Main.MAX_ENTRIES / 100;
        Rectangle<?>[] children = (Rectangle<?>[]) N.getEntries();
        double[] centerOfN = N.getCenterPoint();
        ReinsertStruct[] structs = new ReinsertStruct[length];
        for (int i = 0; i < length; i++) {
            double distance = DistanceOf(children[i].getCenterPoint(), centerOfN);
            structs[i] = new ReinsertStruct(children[i], distance);
        }
        Arrays.sort(structs, ReinsertStruct::compareTo);
        for (int i = 0; i < length - p; i++) {
            children[i] = structs[i].child;
        }
        N.setEntries(children);
        N.setEntriesSize(length - p);
        for (int i = length - p; i < length; i++) {
            Insert(structs[i].child, level);
        }
    }

    private static double DistanceOf(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            result += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(result);
    }

    void addRectangle(Rectangle<?> rectangle) {
        int id = rectangle.getId();
        for (Rectangle<?> r : rectangles) {
            if (r.getId() == id) {
                System.out.println("This id already exists. Aborting...");
                return;
            }
        }
        rectangles.add(rectangle);
    }


    void printAll() {
        print(rectangles.get(0));
    }

    private void print(Rectangle<?> rectangle) {
        rectangle.printData();
        if (!rectangle.pointsToLeafs()) {
            Rectangle<?>[] children = (Rectangle<?>[]) rectangle.getEntries();
            for (int i = 0; i < rectangle.getEntriesSize(); i++) {
                print(children[i]);
            }
        }
    }

}

