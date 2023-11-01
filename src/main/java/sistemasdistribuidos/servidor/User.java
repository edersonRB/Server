package sistemasdistribuidos.servidor;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private boolean isAdmin;
    private String token;

    // Constructor with name
    public User(int id, String name, String email, String password, boolean isAdmin, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.token = token;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getType() {
        if(isAdmin())
            return "admin";
        return "user";
    }

    public String getToken() {
        return token;
    }

    // Setter methods
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", isAdmin=" + isAdmin + ", token=" + token + "]";
    }
}