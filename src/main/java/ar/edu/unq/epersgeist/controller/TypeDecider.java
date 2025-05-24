package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.modelo.enums.DegreeType;

public class TypeDecider {

    public DegreeType decide(String degrreType) {
        switch (degrreType) {
            case "OUTCOMMING": return DegreeType.OUTCOMMING;
            case "INCOMMING": return DegreeType.INCOMMING;
            case "ALL": return DegreeType.ALL;
        }
        return null;
    }
}
