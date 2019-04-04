package channel.seriesReducer;

import java.util.Arrays;
import java.util.List;

public class Line<X extends Number, Y extends  Number, P extends Point<X,Y>> {

    private P start;
    private P end;
    
    private double dx;
    private double dy;
    private double sxey;
    private double exsy;
    private double length;
    
    public Line(P start, P end) {
        this.start = start;
        this.end = end;

        dx = start.getX().doubleValue() - end.getX().doubleValue();
        dy = start.getY().doubleValue() - end.getY().doubleValue();

        sxey = start.getX().doubleValue() * end.getY().doubleValue();
        exsy = end.getX().doubleValue() * start.getY().doubleValue();

        length = Math.sqrt(dx*dx + dy*dy);

        if(length == 0) length = 1;
    }
    
    @SuppressWarnings("unchecked")
    public List<P> asList() {
        return Arrays.asList(start, end);
    }
    
    double distance(P p) {
        if(length > 0)
            return Math.abs(dy * p.getX().doubleValue() - dx * p.getY().doubleValue() + sxey - exsy) / length;
        else
            return 0;
    }
}


