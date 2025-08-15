public abstract class Person {
    private String id;
    private String name;
    private String email;
    private String phone;

    public Person(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public abstract String getRole();
    public abstract String[] getPermissions();

    // Getters & Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("ID: %s, Name: %s, Email: %s, Phone: %s", id, name, email, phone);
    }
}