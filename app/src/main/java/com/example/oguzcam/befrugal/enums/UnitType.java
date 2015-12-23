package com.example.oguzcam.befrugal.enums;

/**
 * Created by cam on 21.12.2015.
 */
public enum UnitType {
    // piece
    PC("Piece"),
    // pack
    PK("Pack"),
    // box
    BOX("Box"),
    // kilogram
    KG("Kg"),
    // gram
    GRAM("Gr"),
    // liter
    LT("Lt");

    private String friendlyName;

    UnitType(String friendlyName){
        this.friendlyName = friendlyName;
    }

    @Override public String toString(){
        return friendlyName;
    }
}
