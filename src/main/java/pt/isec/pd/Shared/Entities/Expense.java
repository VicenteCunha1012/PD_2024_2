package pt.isec.pd.Shared.Entities;

import java.sql.Date;

public class Expense {
    private int id;
    private Date creation_date;
    private String description;
    private Double value;
    private int paid_by;
    private int group_id;

    public Expense(Date creation_date, String description, Double value, int paid_by, int group_id) {
        this.creation_date = creation_date;
        this.description = description;
        this.value = value;
        this.paid_by = paid_by;
        this.group_id = group_id;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public String getDescription() {
        return description;
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
