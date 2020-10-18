public abstract class SpaceObject {

    public static int SortByMin(SpaceObject a, SpaceObject b, int axis) {
        double difference = a.getMinValue(axis) - b.getMinValue(axis);
        if (difference < 0) {
            return 1;
        } else if (difference > 0) {
            return -1;
        }
        return 0;
    }

    public static int SortByMax(SpaceObject a, SpaceObject b, int axis) {
        double difference = a.getMaxValue(axis) - b.getMaxValue(axis);
        if (difference < 0) {
            return 1;
        } else if (difference > 0) {
            return -1;
        }
        return 0;
    }

    abstract double[] getCenterPoint();
    abstract double getMinValue(int dimension);
    abstract double getMaxValue(int dimension);
    abstract long getId();
}
