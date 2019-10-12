package channel.seriesReducer;

import java.util.Objects;

public class MyPoint<X extends Number, Y extends  Number> implements Point{
    private X x;
    private Y y;

    public MyPoint(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public X getX() {
        return x;
    }

    @Override
    public Y getY() {
        return y;
    }

    @Override
    public void setX(Number o) {
        this.x = (X)o;
    }

    @Override
    public void setY(Number o) {
        this.y = (Y)o;
    }

    @Override
    public String toString() {
        return "MyPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyPoint<?, ?> myPoint = (MyPoint<?, ?>) o;
        return Objects.equals(x, myPoint.x) &&
                Objects.equals(y, myPoint.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
