package pt.isec.pd.Shared.Entities;

import java.sql.Date;

public class User {
    private int id;
    private String name;
    private String contact;
    private String email;
    private String password;

    public User(String nome, String contacto, String email, String password) {
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

    public String getPassword() {
        return password;
    }

}
