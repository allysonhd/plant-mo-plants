package com.launchcode.dama_devs.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@NotNull
public class Plant extends AbstractEntity {

    //declared fields with getters. Did not use setters bc plant data is immutable.
    private String commonName;
    private String scientificName;
    private String plantImagePath;
    private String plantZone;
    private String plantCycle;
    private String plantType;
    private String plantLight;
    private String plantWater;
    private String plantSoil;

    @Size(max=500)
    private String plantDescription;
    private String plantHeight;
    private String plantSpread;
    private String colorOfInterest;
    private String seasonOfInterest;
    private Boolean attractsBirds;
    private Boolean attractsButterflies;
    private Boolean attractsPollinators;
    private Boolean isEdible;
    private Boolean resistsDeer;
    private Boolean toxicToAnimals;

    //plant-garden relationship - creates a Join table in the database
    //JsonBackReference tells Jackson when serializing Plant, treat it like the parent and do no include the list of associated gardens
    @JsonBackReference
    @ManyToMany(mappedBy = "plants")
    private final List<Garden> gardens = new ArrayList<>();

    //plant-comment relationship
    @JsonManagedReference
    @OneToMany(mappedBy = "plant")
    private final List<Comment> comments = new ArrayList<>();

    //constructor
    public Plant(String commonName, String scientificName, String plantImagePath, String plantZone, String plantCycle, String plantType, String plantLight, String plantWater, String plantSoil, String plantDescription, String plantHeight, String plantSpread, String colorOfInterest, String seasonOfInterest, Boolean attractsBirds, Boolean attractsButterflies, Boolean attractsPollinators, Boolean isEdible, Boolean resistsDeer, Boolean toxicToAnimals) {

        this.commonName = commonName;
        this.scientificName = scientificName;
        this.plantImagePath = plantImagePath;
        this.plantZone = plantZone;
        this.plantCycle = plantCycle;
        this.plantType = plantType;
        this.plantLight = plantLight;
        this.plantWater = plantWater;
        this.plantSoil = plantSoil;
        this.plantDescription = plantDescription;
        this.plantHeight = plantHeight;
        this.plantSpread = plantSpread;
        this.colorOfInterest = colorOfInterest;
        this.seasonOfInterest = seasonOfInterest;
        this.attractsBirds = attractsBirds;
        this.attractsButterflies = attractsButterflies;
        this.attractsPollinators = attractsPollinators;
        this.isEdible = isEdible;
        this.resistsDeer = resistsDeer;
        this.toxicToAnimals = toxicToAnimals;
    }

    //toString
    @Override
    public String toString() {
        return "Plant{" +
                "commonName='" + commonName + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", plantImagePath='" + plantImagePath + '\'' +
                ", plantZone='" + plantZone + '\'' +
                ", plantCycle='" + plantCycle + '\'' +
                ", plantType='" + plantType + '\'' +
                ", plantLight='" + plantLight + '\'' +
                ", plantWater='" + plantWater + '\'' +
                ", plantSoil='" + plantSoil + '\'' +
                ", plantDescription='" + plantDescription + '\'' +
                ", plantHeight='" + plantHeight + '\'' +
                ", plantSpread='" + plantSpread + '\'' +
                ", colorOfInterest='" + colorOfInterest + '\'' +
                ", seasonOfInterest='" + seasonOfInterest + '\'' +
                ", attractsBirds=" + attractsBirds +
                ", attractsButterflies=" + attractsButterflies +
                ", attractsPollinators=" + attractsPollinators +
                ", isEdible=" + isEdible +
                ", resistsDeer=" + resistsDeer +
                ", toxicToAnimals=" + toxicToAnimals +
                '}';
    }
}
