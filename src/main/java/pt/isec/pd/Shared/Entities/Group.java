package pt.isec.pd.Shared.Entities;

import java.sql.Date;

public class Group {
    private int id;
    private Date creation_date;
    private String name;
    private int owner_id;

    public Group(Date creation_date, String name, int owner_id) {
        this.creation_date = creation_date;
        this.name = name;
        this.owner_id = owner_id;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public String getName() {
        return name;
    }

    public int getOwner_id() {
        return owner_id;
    }
}
