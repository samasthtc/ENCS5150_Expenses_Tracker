package edu.birzeit.expensesTracker.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Expense implements Parcelable {

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    private int id;
    private int typeId;
    private double amount;
    private String note;
    private String date;
    private String typeName;


    public Expense(int id, int typeId, double amount, String note, String date, String typeName) {
        this.id = id;
        this.typeId = typeId;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.typeName = typeName;
    }

    protected Expense(Parcel in) {
        id = in.readInt();
        typeId = in.readInt();
        amount = in.readDouble();
        note = in.readString();
        date = in.readString();
        typeName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(typeId);
        dest.writeDouble(amount);
        dest.writeString(note);
        dest.writeString(date);
        dest.writeString(typeName);
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        if(typeName.isEmpty() || date.isEmpty())
            return note;
        return typeName + " — " + date.substring(11, 19);
    }
}

