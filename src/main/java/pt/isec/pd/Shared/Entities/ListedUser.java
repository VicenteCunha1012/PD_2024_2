package pt.isec.pd.Shared.Entities;

import java.io.Serializable;
import java.sql.Date;

public class ListedUser implements Serializable {
    private Integer id;
    private Date creation_Date;
    private String name;
    private String contact;
    private String email;
    private String password;

    public ListedUser() {}

    public ListedUser(Integer id, Date creation_Date, String name, String contact, String email, String password) {
        this.id = id;
        this.creation_Date = creation_Date;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.password = password;
    }

    public Integer getId() {return id;}

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

    @Override
    public String toString() {
        return String.format("Nome: %s \t\t Contacto: %s\t\t Email: %s", name, contact, email);
    }
}
