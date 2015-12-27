package com.oguzcam.befrugal.enums;

import java.util.ArrayList;

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
    GR("Gram"),
    // liter
    LT("Liter");

    private String friendlyName;

    UnitType(String friendlyName){
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName(){
        return friendlyName;
    }

    public static UnitType fromString(String text) {
        if (text != null) {
            for (UnitType unit : UnitType.values()) {
                if (text.equalsIgnoreCase(unit.friendlyName)) {
                    return unit;
                }
            }
        }
        throw new IllegalArgumentException("No possible unit type for " + text);
    }

    public static ArrayList<String> getFriendlyNames() {
        ArrayList<String> friendlyNames = new ArrayList<>();
        for(UnitType value : UnitType.values()){
            friendlyNames.add(value.getFriendlyName());
        }
        return friendlyNames;
    }
}
