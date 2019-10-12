package channel.seriesReducer;

import java.util.Arrays;
import java.util.LinkedList;

public class Line<X extends Number, Y extends  Number, P extends Point<X,Y>> {

    private P start;
    private P end;

    private double  dx = 0;
    private double  dy = 0;
    private double  sxey = 0;
    private double  exsy = 0;

    private double length = 0;
    
    public Line(P start, P end) {
        this.start = start;
        this.end = end;

        dx = start.getX().doubleValue() - end.getX().doubleValue();
        dy = start.getY().doubleValue() - end.getY().doubleValue();

        sxey = start.getX().intValue() * end.getY().doubleValue();
        exsy = end.getX().doubleValue()  * start.getY().doubleValue();

        length = Math.sqrt(dx*dx + dy*dy);

        if(length == 0) length = 1;
    }

    public LinkedList<P> asLinkedList() {
        return new LinkedList<>(Arrays.asList(start, end));
    }
    
    double distance(P p) {
        double tmp = dy * p.getX().doubleValue();
        tmp = tmp - dx * p.getY().doubleValue();
        tmp = tmp + sxey;
        tmp = tmp - exsy;

        tmp = Math.abs(tmp);

        if(length > 0)
            return tmp / length;
        else
            return 0;
    }
}


