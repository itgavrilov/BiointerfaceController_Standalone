package channel.seriesReducer;

/**
 * Represents a point on a plane. A point consists of 2 coordinates - x and y.
 */
public interface Point<X,Y> {

    X getX();
    
    Y getY();
}
