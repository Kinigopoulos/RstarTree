import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class RStar {

    private ArrayList<Rectangle<?>> rectangles;
    private boolean mustSplit = false;

    RStar() {
        rectangles = new ArrayList<>();
    }

    RStar(Rectangle<?> root) {
        this();
        rectangles.add(root);
    }

    double Overlap(Rectangle<?> rectangle) {
        double area = rectangle.getArea();
        double totalOverlap = 0;
        ArrayList<Rectangle<?>> rectanglesToLook = new ArrayList<>();
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

    Rectangle<?> ChooseSubtree(Point point) {
        Rectangle<?> N = rectangles.get(0); //N is equal to the root.
        while (!N.pointsToLeafs()) {
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
                        mustSplit = copy.isFull();
                    } else if (copyOverlap == minOverlap) {
                        double area = copy.getArea();
                        if (bestArea > area) {
                            bestRectangle = i;
                            bestArea = area;
                            mustSplit = copy.isFull();
                        } else if (bestArea == area) {
                            if (children[bestRectangle].getArea() > children[i].getArea()) {
                                bestRectangle = i;
                                mustSplit = copy.isFull();
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
        }
        return N;
    }

    private void Split(Rectangle<?> rectangle) {
        int axis = ChooseSplitAxis(rectangle);
        int index = ChooseSplitIndex(rectangle, axis);
        Rectangle<?>[] children = (Rectangle<?>[]) rectangle.getEntries();
        Rectangle<?> other = new Rectangle(Arrays.copyOfRange(children, index, rectangle.getEntriesSize()), 0);
        rectangle.setEntriesSize(index - 1);
    }

    private int ChooseSplitAxis(Rectangle<?> rectangle) {
        final int M = rectangle.getEntriesSize();
        final int m = (Main.MINIMUM_ENTRIES_PERCENTAGE * M / 100);
        final int kMax = M - 2 * m + 1;
        int kEnd = m + kMax;

        Rectangle<?>[] children = new Rectangle[M];
        System.arraycopy(rectangle.getEntries(), 0, children, 0, children.length);

        double bestMargin = Double.MAX_VALUE;
        int bestAxis = 0;
        for (int axis = 0; axis < Main.DIMENSIONS; axis++) {
            double totalMargin = 0;
            for (int sortType = 0; sortType < 2; sortType++) {
                if (sortType == 0) {
                    int finalAxis = axis;
                    Arrays.sort(children, (a, b) -> Rectangle.Compare(a, b, finalAxis));
                } else {
                    Rectangle<?>[] reverse = new Rectangle[M];
                    for (int i = 0; i < M; i++) {
                        reverse[i] = children[M - i - 1];
                    }
                    children = reverse;
                }
                for (int k = m + 1; k <= kEnd; k++) {
                    totalMargin += CalculateMargin(Arrays.copyOfRange(children, 0, k));
                    totalMargin += CalculateMargin(Arrays.copyOfRange(children, k, children.length));
                }
            }

            if (totalMargin < bestMargin) {
                bestMargin = totalMargin;
                bestAxis = axis;
            }
        }
        return bestAxis;
    }

    private double CalculateMargin(Rectangle<?>[] rectangles) {
        Rectangle<?> temp = new Rectangle<>(rectangles, -1);
        return Rectangle.getMargin(temp.getMinValues(), temp.getMaxValues());
    }

    private int ChooseSplitIndex(Rectangle<?> rectangle, int axis) {
        double minOverlap = Double.MAX_VALUE;
        double minArea = Double.MAX_VALUE;
        int bestIndex = 0;
        final int M = rectangle.getEntriesSize();
        final int m = Main.MINIMUM_ENTRIES_PERCENTAGE * M / 100;
        final int kMax = (M - 2 * m + 1);
        Rectangle<?>[] children = new Rectangle[M];
        System.arraycopy(rectangle.getEntries(), 0, children, 0, children.length);
        Arrays.sort(children, (a, b) -> Rectangle.Compare(a, b, axis));

        for (int i = m + 1; i <= m + kMax; i++) {
            double currentOverlap = 0;

            Rectangle<?> split1 = new Rectangle<>(Arrays.copyOfRange(children, 0, i), -1);
            Rectangle<?> split2 = new Rectangle<>(Arrays.copyOfRange(children, i + 1, children.length), -1);
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
        Insert(point);
    }

    private void Insert(Point point) {
        Rectangle<?> bestRectangle = ChooseSubtree(point);

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

}

