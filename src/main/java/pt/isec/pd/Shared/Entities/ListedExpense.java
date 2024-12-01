package pt.isec.pd.Shared.Entities;

import java.sql.Date;

public class ListedExpense {
    private int id;
    private Date creation_date;
    private String description;
    private Double value;
    private Boolean paid;
    private int paid_by;
    private int group_id;

    public ListedExpense(Integer id, Date creation_date,String description, Double value,Boolean paid, int paid_by, int group_id) {
        this.id = id;
        this.creation_date = creation_date;
        this.description = description;
        this.value = value;
        this.paid = paid;
        this.paid_by = paid_by;
        this.group_id = group_id;
    }


    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getPaid() {
        return paid;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public Double getValue() {
        return value;
    }

    public int getPaid_by() {
        return paid_by;
    }

    public int getGroup_id() {
        return group_id;
    }
}
