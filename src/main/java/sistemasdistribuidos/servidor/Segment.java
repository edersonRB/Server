package sistemasdistribuidos.servidor;

public class Segment {

    private int id;
    private Point origin;
    private Point destiny;
    private String direction;
    private int distance;
    private String obs;

    public Segment(int id, Point origin, Point destiny, String direction, int distance, String obs) {
        this.id = id;
        this.origin = origin;
        this.destiny = destiny;
        this.direction = direction;
        this.distance = distance;
        this.obs = obs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public Point getDestiny() {
        return destiny;
    }

    public void setDestiny(Point destiny) {
        this.destiny = destiny;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "id=" + id +
                ", origin=" + origin +
                ", destiny=" + destiny +
                ", direction='" + direction + '\'' +
                ", distance=" + distance +
                ", obs='" + obs + '\'' +
                '}';
    }
}
