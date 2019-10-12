package channel.seriesReducer;

import java.util.LinkedList;
import java.util.List;

public class SeriesReducer {

    /**
     * Reduces number of points in given series using Ramer-Douglas-Peucker algorithm.
     * 
     * @param points
     *          initial, ordered list of points (objects implementing the {@link Point} interface)
     * @param epsilon
     *          allowed margin of the resulting curve, has to be > 0
     */
    public static <X extends Number, Y extends  Number, P extends Point<X,Y>> LinkedList<P> reduce(LinkedList<P> points, double epsilon) {
        if (epsilon < 0) {
            throw new IllegalArgumentException("Epsilon cannot be less then 0.");
        } else if (points.size() < 2 ) {
            throw new IllegalArgumentException("Count of points cannot be less then 2.");
        }

        double furthestPointDistance = 0.0;
        int furthestPointIndex = 0;
        Line<X, Y, P> line = new Line<>(points.get(0), points.get(points.size() - 1));

        for (int i = 1; i < points.size() - 1; i++) {
            double distance = line.distance(points.get(i));
            if (distance > furthestPointDistance ) {
                furthestPointDistance = distance;
                furthestPointIndex = i;
            }
        }

        if (furthestPointDistance > epsilon) {
            LinkedList<P> result = reduce(new LinkedList<>(points.subList(0, furthestPointIndex+1)), epsilon);
            LinkedList<P> reduced = reduce(new LinkedList<>(points.subList(furthestPointIndex, points.size())), epsilon);
            result.addAll(reduced.subList(1, reduced.size()));
            return result;
        } else {
            return line.asLinkedList();
        }
    }
}
