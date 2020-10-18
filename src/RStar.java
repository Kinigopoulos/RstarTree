import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

class RStar {

    private Rectangle<?> Root;
    private HashSet<Integer> reinsertLevels;
    private int leafLevel;
    public static long nodes; //Used to track the amount of R containers
    private KnnPoint[] nearestKNN; //Used only in k-nn queries

    RStar() {
        Root = null;
        leafLevel = 0;
        nodes = 1;
        reinsertLevels = new HashSet<>();
    }

    void RANGE_QUERY(double[] minValues, double[] maxValues) {
        Point pointMin = new Point(-10, minValues);
        Point pointMax = new Point(-11, maxValues);
        Rectangle<?> query = new Rectangle<>(new Point[]{pointMax, pointMin}, -10);

        ArrayList<Rectangle<?>> rectanglesToLook = new ArrayList<>();
        rectanglesToLook.add(Root);
        while (!rectanglesToLook.isEmpty()) {
            Rectangle<?>[] children = (Rectangle<?>[]) rectanglesToLook.get(0).getEntries();
            int childrenSize = rectanglesToLook.get(0).getEntriesSize();
            if (children[0].pointsToLeafs()) {
                for (int i = 0; i < childrenSize; i++) {

                    Point[] points = (Point[]) children[i].getEntries();
                    for (Point point : points) {
                        if (Rectangle.ContainsPoint(point, minValues, maxValues)) {
                            System.out.println(point.getString());
                        }
                    }
                }
            } else {
                for (int i = 0; i < childrenSize; i++) {
                    if (children[i].OverlapCost(query) > 0) {
                        rectanglesToLook.add(children[i]);
                    }
                }
            }
            rectanglesToLook.remove(0);
        }
    }

    static class KnnPoint {
        Point point;
        double distance;

        KnnPoint(Point point, double distance) {
            this.point = point;
            this.distance = distance;
        }
    }

    static class ActiveBranchList {
        Rectangle<?> rectangle;
        double MinDist;
        //double MinMaxDist;
        //double DistanceFromCenter;

        ActiveBranchList(Rectangle<?> rectangle, Point query) {
            this.rectangle = rectangle;
            MinDist = rectangle.MinimumDistance(query);
            //MinMaxDist = rectangle.MinMaxDistance(query);
            //DistanceFromCenter = DistanceOf(rectangle.getCenterPoint(), query.getPositions());
        }

        static int compareTo(ActiveBranchList a, ActiveBranchList b) {
            if (a.MinDist < b.MinDist) {
                return -1;
            } else if (a.MinDist > b.MinDist) {
                return 1;
            }
            return 0;
        }
    }

    void K_NN_QUERY(int k, double[] position) {
        nearestKNN = new KnnPoint[k];
        for (int i = 0; i < k; i++) {
            nearestKNN[i] = new KnnPoint(null, Double.MAX_VALUE);
        }
        Point point = new Point(-20, position);
        FindNeighbours(k, Root, point);
        for (KnnPoint knnPoint : nearestKNN) {
            Point p = knnPoint.point;
            System.out.println(p.getString());
        }
        nearestKNN = null;
    }

    private void FindNeighbours(int k, Rectangle<?> rectangle, Point query) {
        if (rectangle.pointsToLeafs()) {
            Point[] points = (Point[]) rectangle.getEntries();
            for (Point point : points) {
                double distance = point.DistanceFrom(query);
                if (nearestKNN[k - 1].distance > distance) {
                    int j;
                    for (j = k - 2; j >= 0 && nearestKNN[j].distance > distance; j--) {
                        nearestKNN[j + 1] = nearestKNN[j];
                    }
                    nearestKNN[j + 1] = new KnnPoint(point, distance);
                }
            }
        } else {
            Rectangle<?>[] children = (Rectangle<?>[]) rectangle.getEntries();
            int size = rectangle.getEntriesSize();
            ActiveBranchList[] ABL = new ActiveBranchList[size];
            for (int i = 0; i < size; i++) {
                ABL[i] = new ActiveBranchList(children[i], query);
            }
            Arrays.sort(ABL, ActiveBranchList::compareTo);

            for (int i = 0; i < size; i++) {
                if (ABL[i].MinDist >= nearestKNN[k - 1].distance) {
                    break;
                }
                FindNeighbours(k, ABL[i].rectangle, query);
            }
        }
    }

    double Overlap(Rectangle<?> rectangle, Rectangle<?> enlargement, Rectangle<?>[] children, int size) {
        double overlap = 0;
        for (int i = 0; i < size; i++) {
            if (children[i] == rectangle) {
                continue;
            }
            overlap += children[i].OverlapCost(enlargement) - children[i].OverlapCost(rectangle);
        }
        return overlap;
    }

    Rectangle<?> ChooseSubtree(Rectangle<?> entry, int level) {
        Rectangle<?> N = Root; //N is equal to the root.
        while (level < leafLevel) {
            Rectangle<?>[] children = (Rectangle<?>[]) N.getEntries();
            int bestRectangle = 0; //Starting by assuming that the best rectangle is the first.
            double bestArea = children[0].AreaEnlargement(entry);
            if (children[0].pointsToLeafs()) {
                Point point = (Point) entry.getEntries()[0];
                double minOverlap = Double.MAX_VALUE;
                for (int i = 0; i < N.getEntriesSize(); i++) {
                    double overlap;
                    Rectangle<Point> copy = new Rectangle<>((Point[]) children[i].getEntries(), 0);
                    copy.AddPoint(point);
                    if (Rectangle.ContainsPoint(point, children[i].getMinValues(), children[i].getMaxValues())) {
                        overlap = 0;
                    } else {
                        overlap = Overlap(children[i], copy, children, N.getEntriesSize());
                    }

                    if (overlap < minOverlap) {
                        bestRectangle = i;
                        bestArea = copy.getArea();
                        minOverlap = overlap;
                    } else if (overlap == minOverlap) {
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
                    double area = children[i].AreaEnlargement(entry);
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

            N = children[bestRectangle];
            level++;
        }
        return N;
    }

    private Rectangle<?> Split(Rectangle<?> rectangle) {
        SpaceObject[] children = (SpaceObject[]) rectangle.getEntries();
        int childrenSize = rectangle.getEntriesSize();

        int axis = ChooseSplitAxis(children, childrenSize);
        int index = ChooseSplitIndex(rectangle, axis, children, childrenSize);

        Rectangle<?> other = Rectangle.CreateRectangle(Arrays.copyOfRange(children, index, childrenSize), nodes);
        nodes++;
        rectangle.setEntriesSize(index);
        rectangle.SaveRectangle();
        return other;
    }

    private int ChooseSplitAxis(SpaceObject[] children, int M) {
        final int m = Main.MINIMUM_ENTRIES_PERCENTAGE * M / 100;
        final int kMax = M - 2 * m + 1;
        int kEnd = m + kMax;

        double bestMargin = Double.MAX_VALUE;
        int bestAxis = 0;

        for (int axis = 0; axis < Main.DIMENSIONS; axis++) {
            double totalMargin = 0;
            for (int sortType = 0; sortType < 2; sortType++) {
                int finalAxis = axis;
                if (sortType == 0) {
                    Arrays.sort(children, (a, b) -> SpaceObject.SortByMin(a, b, finalAxis));
                } else {
                    Arrays.sort(children, (a, b) -> SpaceObject.SortByMax(a, b, finalAxis));
                }
                for (int k = m + 1; k < kEnd; k++) {
                    totalMargin += CalculateMargin(Arrays.copyOfRange(children, 0, k));
                    totalMargin += CalculateMargin(Arrays.copyOfRange(children, k + 1, M));
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
            temp = new Rectangle<>((Rectangle<?>[]) rectangles, -19);
        }
        return Rectangle.getMargin(temp.getMinValues(), temp.getMaxValues());
    }

    private int ChooseSplitIndex(Rectangle<?> rectangle, int axis, SpaceObject[] children, int M) {
        double minOverlap = Double.MAX_VALUE;
        double minArea = Double.MAX_VALUE;
        int bestIndex = 0;
        final int m = Main.MINIMUM_ENTRIES_PERCENTAGE * M / 100;
        final int kMax = (M - 2 * m + 1);

        Arrays.sort(children, (a, b) -> SpaceObject.SortByMin(a, b, axis));
        rectangle.setEntries(children);

        Rectangle<?> parent = rectangle.getParent();
        Rectangle<?>[] siblings = null;
        int siblingsSize = 0;
        if (parent != null) {
            siblings = (Rectangle<?>[]) parent.getEntries();
            siblingsSize = parent.getEntriesSize();
        }

        for (int i = m + 1; i < m + kMax; i++) {
            double currentOverlap;

            Rectangle<?> split1 = Rectangle.CreateRectangle((Arrays.copyOfRange(children, 0, i)), -3);
            Rectangle<?> split2 = Rectangle.CreateRectangle(Arrays.copyOfRange(children, i + 1, M), -4);

            currentOverlap = split1.OverlapCost(split2);
            if (parent != null) {
                for (int j = 0; j < siblingsSize; j++) {
                    if (siblings[j].getId() == rectangle.getId()) {
                        continue;
                    }
                    currentOverlap += split1.OverlapCost(siblings[j]);
                    currentOverlap += split2.OverlapCost(siblings[j]);
                }
            }

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
        System.out.println("============= INSERTING " + point.getId() + " =============");
        System.out.println("===========================================================");
        if (Root != null) {
            Insert(point, 0);
            reinsertLevels = new HashSet<>();
        } else {
            Root = new Rectangle<>(new Point[]{point}, 0);
            Root.SaveRectangle();
        }

        //Root.printData();
        Root = FileReader.getRectangle(0);
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

        if (isPoint) {
            N.AddPoint((Point) Entry);
        } else {
            N.AddPoint((Rectangle<?>) Entry);
            ((Rectangle<?>)Entry).SaveRectangle();
        }

        //Overflow check

        Rectangle<?> M = N;
        int levelCounter = 0;
        while (M.isFull()) {
            System.out.println("Overflow happened on " + M.getId());

            Rectangle<?> result = OverflowTreatment(M, levelCounter + level);
            levelCounter++;
            if (result == null) {
                //return;
                N = FileReader.getRectangle(N.getId());
                break;
            }

            Rectangle<?> parent = M.getParent();

            if (M.getId() > 0) {
                parent.AddPoint(result);
                result.setId(result.getId());
                result.SaveRectangle();
                parent.SaveRectangle();
            } else {
                ChangeRoot(result, M);
                break;
            }
            M = parent;
        }


        //Saving and/or fixing bounding boxes
        if (levelCounter == 0) {
            N.SaveRectangle();
        }
        M = N.getParent();
        while (M != null) {

            Rectangle.ResizeBoundingBoxAfterInsert(M, N);

            N = M;
            M = M.getParent();
        }
    }

    private void ChangeRoot(Rectangle<?> result, Rectangle<?> previousRoot) {
        System.out.println("***CHANGING ROOT***");
        previousRoot.setId(nodes);
        nodes++;
        Root = new Rectangle<>(new Rectangle<?>[]{previousRoot, result}, 0); //New Root

        Root.SaveRectangle();

        result.setId(result.getId());
        result.SaveRectangle();
        previousRoot.setId(previousRoot.getId());
        previousRoot.SaveRectangle();

        leafLevel++;
    }

    private Rectangle<?> OverflowTreatment(Rectangle<?> N, int level) {
        if (!reinsertLevels.contains(level) && N.getId() > 0) {
            reinsertLevels.add(level);
            Reinsert(N, level);
            return null;
        }
        return Split(N);
    }

    static class ReinsertStruct {
        SpaceObject child;
        double distance;

        ReinsertStruct(SpaceObject child, double distance) {
            this.child = child;
            this.distance = distance;
        }

        static int compareTo(ReinsertStruct a, ReinsertStruct b) {
            if (a.distance > b.distance) {
                return -1;
            } else if (a.distance < b.distance) {
                return 1;
            }
            return 0;
        }
    }

    private void Reinsert(Rectangle<?> N, int level) {
        final int length = N.getEntriesSize();
        final int p = 30 * Main.MAX_ENTRIES / 100;
        SpaceObject[] children = (SpaceObject[]) N.getEntries();
        double[] centerOfN = N.getCenterPoint();
        ReinsertStruct[] struct = new ReinsertStruct[length];
        for (int i = 0; i < length; i++) {
            double distance = DistanceOf(children[i].getCenterPoint(), centerOfN);
            struct[i] = new ReinsertStruct(children[i], distance);
        }
        Arrays.sort(struct, ReinsertStruct::compareTo);
        for (int i = 0; i < length - p; i++) {
            children[i] = struct[i].child;
        }
        N.setEntries(children);
        N.setEntriesSize(length - p);

        N.SaveRectangle();

        for (int i = length - p; i < length; i++) {
            System.out.println("Re-Inserting " + struct[i].child.getId());
            Insert(struct[i].child, level);
        }
    }

    private static double DistanceOf(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < Main.DIMENSIONS; i++) {
            result += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(result);
    }
}

