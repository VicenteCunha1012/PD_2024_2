package pt.isec.pd.Shared.Entities;

import java.io.Serializable;
import java.sql.Date;

public class ListedGroup implements Serializable {
    private Integer id;
    private Date creation_date;
    private String name;
    private int owner_id;

    public ListedGroup(Integer id, Date creation_date, String name, Integer owner_id) {
        this.id = id;
        this.creation_date = creation_date;
        this.name = name;
        this.owner_id = owner_id;
    }

    public Integer getId() {return id;}

    public Date getCreation_date() {
        return creation_date;
    }

    public String getName() {
        return name;
    }

    public Integer getOwner_id() {
        return owner_id;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Nome: %s, Data de criação: %s, ID do Dono: %d", id, name, creation_date, owner_id);
    }
}
