package com.sjapps.jsonlist.java;

import java.util.ArrayList;
import java.util.Objects;

public class ListItem {

    private String Name;
    private String Value;
    private boolean isArrayOfObjects;
    private boolean isArray;
    private boolean isObject;
    private boolean isSpace;
    private ArrayList<ListItem> Objects;
    private ArrayList<ArrayList<ListItem>> ListObjects;
    private int id = -1;

    public ListItem(){
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public boolean isArrayOfObjects() {
        return isArrayOfObjects;
    }

    public void setIsArrayOfObjects(boolean array) {
        isArrayOfObjects = array;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setIsArray(boolean array) {
        isArray = array;
    }

    public boolean isObject() {
        return isObject;
    }

    public void setIsObject(boolean object) {
        isObject = object;
    }

    public boolean isSpace() {
        return isSpace;
    }

    public void setIsSpace(boolean space) {
        isSpace = space;
    }

    public ArrayList<ListItem> getObjects() {
        return Objects;
    }

    public void setObjects(ArrayList<ListItem> objects) {
        Objects = objects;
    }

    public ArrayList<ArrayList<ListItem>> getListObjects() {
        return ListObjects;
    }

    public void setListObjects(ArrayList<ArrayList<ListItem>> listObjects) {
        ListObjects = listObjects;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "{" +
                "\"ID\":" + id +
                ",\"Name\":" +(Name!=null && !Name.startsWith("\"")?"\"":"") +  Name + (Name!=null && !Name.startsWith("\"")?"\"":"") +
                ", \"Value\":" + (Value!=null && !Value.startsWith("\"")?"\"":"") + Value + (Value!=null && !Value.startsWith("\"")?"\"":"") +
                ", \"isArrayOfObjects\":" + isArrayOfObjects +
                ", \"isArray\":" + isArray +
                ", \"isObject\":" + isObject +
                ", \"isSpace\":" + isSpace +
                ", \"Objects\":" + Objects +
                ", \"ListObjects\":" + ListObjects +
                '}';
    }

    public ListItem Space() {
        setIsSpace(true);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListItem)) return false;
        ListItem item = (ListItem) o;
        return isArrayOfObjects() == item.isArrayOfObjects() && isArray() == item.isArray() && isObject() == item.isObject() && isSpace() == item.isSpace() && java.util.Objects.equals(getName(), item.getName()) && java.util.Objects.equals(getValue(), item.getValue()) && java.util.Objects.equals(getObjects(), item.getObjects()) && java.util.Objects.equals(getListObjects(), item.getListObjects());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getName(), getValue(), isArrayOfObjects(), isArray(), isObject(), isSpace(), getObjects(), getListObjects());
    }
}
