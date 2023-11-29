package sistemasdistribuidos.servidor;

public class Point {
    private int id;
    private String name;
    private String obs;

    public Point(int id, String name, String obs) {
        this.id = id;
        this.name = name;
        this.obs = obs;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getObs() {
        return obs;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    @Override
    public String toString() {
        return "Point [id=" + id + ", name=" + name + ", obs=" + obs + "]";
    }
}