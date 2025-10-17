/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */
public class Unit {
    private int id;
    private String unitName;
    private String symbol;
    private String description;

    // Constructors aaaa
    public Unit() {}

    public Unit(int id, String unitName, String symbol, String description) {
        this.id = id;
        this.unitName = unitName;
        this.symbol = symbol;
        this.description = description;
    }

    public Unit(int id, String unitName) {
        this.id = id;
        this.unitName = unitName;
    }
    

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Optional: toString method for debugging
    @Override
    public String toString() {
        return "Unit{" +
                "id=" + id +
                ", unitName='" + unitName + '\'' +
                ", symbol='" + symbol + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
