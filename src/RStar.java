import java.util.ArrayList;

/**
 * +++Algorithm ChooseSubtree+++
 * CSl    Set N to be the root
 * CS2    If N is a leaf,
 * return N
 * else
 * If the childPointers in N point to leaves
 * [determine the minimum overlap cost],
 * choose the entry in N whose rectangle needs least
 * overlap enlargement to include the new data
 * rectangle. Resolve ties by choosing the entry
 * whose rectangle needs least area enlargement,
 * then
 * the entry with the rectangle of smallest area
 * If the childPointers in N do not point to leaves
 * [determine the minimum area cost],
 * choose the entry in N whose rectangle needs least
 * area enlargement to include the new data
 * rectangle Resolve ties by choose the entry
 * with the rectangle of smallest area
 * end
 * CS3        Set N to be the childNode pointed to by the
 * childPointer of the chosen entry and repeat from CS2
 * <p>
 * (i) area-value area[bb(first group)] + area[bb(second group)]
 * (ii) margin-value margin[bb(first group)] + margin[bb(second group)]
 * (iii) overlap-value area[bb(first group) \intersection  bb(second group)]
 * Here bb denotes the bounding box of a set of rectangles
 * <p>
 * +++Algorithm Split+++
 * s1     Invoke ChooseSplitAxis to determine the axis,
 * perpendicular to which the split 1s performed.
 * s2     Invoke ChooseSplitAxis to determine the best
 * distribution mto two groups along that axis.
 * s3     Distribute the entries into two groups.
 * <p>
 * +++Algorithm ChooseSplitAxis+++
 * CSA1   For each axis
 * Sort the entries by the lower then by the upper
 * value of their rectangles and determine all
 * distributions as described above Compute S. the
 * sum of all margin-values of the different
 * distributions.
 * end
 * CSA2   Choose the axis with the minimum S as split axis.
 * <p>
 * +++Algorithm ChooseSplitIndex+++
 * CSI1   Along the chosen split axis, choose the
 * distribution with the minimum overlap-value
 * Resolve ties by choosing the distribution with
 * minimum area-value.
 * <p>
 * +++Algorithm InsertData+++
 * ID1    Invoke Insert starting with the leaf level as a
 * parameter, to Insert a new data rectangle.
 * <p>
 * +++Algorithm Insert+++
 * I1     Invoke ChooseSubtree, with the level as a parameter,
 * to find an appropriate node N, in which to place the
 * new entry E
 * I2     If N has less than M entries, accommodate E in N
 * If N has M entries,
 * invoke OverflowTreatment with the level of N
 * as a parameter [for reinsertion or split]
 * I3     If OverflowTreatment was called and a split was performed,
 * propagate OverflowTreatment upwards if necessary
 * If OverflowTreatment caused a split of the root,
 * create a new root
 * I4     Adjust all covering rectangles in the insertion path
 * such that they are minimum bounding boxes
 * enclosing then children rectangles
 * <p>
 * +++Algorithm OverflowTreatment+++
 * OT1    If the level is not the root level and this is the first
 * call of OverflowTreatment in the given level during the
 * Insertion of one data rectangle, then
 * invoke Reinsert
 * else
 * invoke Split
 * end
 * <p>
 * +++Algorithm Reinsert+++
 * RI1    For all M+1 entries of a node N, compute the distance
 * between the centers of their rectangles and the center
 * of the bounding rectangle of N
 * RI2    Sort the entries m decreasing order of their distances
 * computed in RI1
 * RI3    Remove the first p entries from N and adjust the
 * bounding rectangle of N
 * RI4    In the sort, defined in RI2, starting with the maximum
 * distance (= far reinsert) or minimum distance (= close
 * reinsert), invoke Insert to reinsert the entries
 **/


class RStar {

    //STATIC VARIABLES
    static final int MAX_ENTRIES = 3; //Defines that no more than the given number of rectangles or points can be contained in each node.
    static final int DIMENSIONS = 2; //Dimensions of RStar Tree.

    private ArrayList<Rectangle<?>> rectangles;

    RStar() {
        rectangles = new ArrayList<>();
    }

    RStar(Rectangle<?> root){
        this();
        rectangles.add(root);
    }



    @SuppressWarnings("unchecked")
    Rectangle<?> ChooseSubtree(Point point) {
        Rectangle<?> N = rectangles.get(0); //N is equal to the root.
        while (!N.pointsToLeafs()) {
            Rectangle<?>[] children = (Rectangle<?>[]) N.getEntries();
            int bestRectangle = 0; //Starting by assuming that the best rectangle is the first.
            double bestArea = children[0].AreaEnlargement(point);
            if (children[0].pointsToLeafs()) {
                double overlap = 0;
                for (int i = 0; i < N.getEntriesSize(); i++) {
                    Rectangle<Point> copy = new Rectangle<>((Point[]) children[i].getEntries(), 0);
                    if(!copy.AddPoint(point)){
                        //This condition adds the point to the copied rectangle
                        //and checks if it has space for it at the same time!
                        continue;
                    }

                    if( /* OVERLAPPING FUNCTION HERE */ false){

                    }
                    else if (bestArea > children[i].AreaEnlargement(point)) {
                        bestRectangle = i;
                    } else if (bestArea == children[i].AreaEnlargement(point)) {
                        if (children[bestRectangle].getArea() > children[i].getArea()) {
                            bestRectangle = i;
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