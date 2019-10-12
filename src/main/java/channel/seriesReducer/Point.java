package channel.seriesReducer;

/**
 * Represents a point on a plane. A point consists of 2 coordinates - x and y.
 */
public interface Point<X extends Number,Y extends Number> {
    X getX();
    Y getY();
    void setX(X x);
    void setY(Y y);
}
