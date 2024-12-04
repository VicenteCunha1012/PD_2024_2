package pt.isec.pd.Shared.Entities;

public class User {
    private String name;
    private String contact;
    private String email;
    private String password;

    public User(String name, String contact, String email, String password) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() { return password; }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
