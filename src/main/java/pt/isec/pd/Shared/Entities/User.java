package pt.isec.pd.Shared.Entities;

import java.sql.Date;

public class User {
    private int id;
    private Date creation_Date;
    private String name;
    private String contact;
    private String email;
    private String password;

    public User(Date creation_Date, String name, String contact, String email, String password) {
        this.creation_Date = creation_Date;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.password = password;
    }

    public Date getCreation_Date() {
        return creation_Date;
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
        return password; // TODO usar o Hasher
    }

}
