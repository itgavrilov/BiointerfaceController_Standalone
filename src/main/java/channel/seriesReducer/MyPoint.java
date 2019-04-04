package channel.seriesReducer;

public class MyPoint<X extends Number, Y extends  Number> implements Point {
    private X x;
    private Y y;

    public MyPoint(X  x, Y y) {
        this.x=x;
        this.y=y;
    }

    @Override
    public X getX() {
        return x;
    }

    @Override
    public Y getY() {
        return y;
    }

    public void setX(X x) {
        this.x = x;
    }

    public void setY(Y y) {
        this.y = y;
    }
}
