package com.launchcode.dama_devs.services;

import com.launchcode.dama_devs.models.Plant;
import com.launchcode.dama_devs.models.data.PlantRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVFormat;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class CsvToDatabase {

    @Autowired
    PlantRepository plantRepository;

    CSVFormat format = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true).get();

    @Transactional
    public void insertRecords(String filePath) throws IOException {

        List<Plant> plantRecords = new ArrayList<>();

        try (FileReader fileReader = new FileReader(filePath);
             CSVParser csvParser = CSVParser.parse(fileReader, format)) {

            for (CSVRecord row : csvParser) {

                String commonName = row.get("commonName");
                String scientificName = row.get("scientificName");
                String plantImagePath = row.get("plantImagePath");
                String plantZone = row.get("plantZone");
                String plantCycle = row.get("plantCycle");
                String plantType = row.get("plantType");
                String plantLight = row.get("plantLight");
                String plantWater = row.get("plantWater");
                String plantSoil = row.get("plantSoil");
                String plantDescription = row.get("plantDescription");
                String plantHeight = row.get("plantHeight");
                String plantSpread = row.get("plantSpread");
                String colorOfInterest = row.get("colorOfInterest");
                String seasonOfInterest = row.get("seasonOfInterest");
                Boolean attractsBirds = Boolean.parseBoolean(row.get("attractsBirds"));
                Boolean attractsButterflies = Boolean.parseBoolean(row.get("attractsButterflies"));
                Boolean attractsPollinators = Boolean.parseBoolean(row.get("attractsPollinators"));
                Boolean isEdible = Boolean.parseBoolean(row.get("isEdible"));
                Boolean resistsDeer = Boolean.parseBoolean(row.get("resistsDeer"));
                Boolean toxicToAnimals = Boolean.parseBoolean(row.get("toxicToAnimals"));

                Plant plant = new Plant(
                        commonName,
                        scientificName,
                        plantImagePath,
                        plantZone,
                        plantCycle,
                        plantType,
                        plantLight,
                        plantWater,
                        plantSoil,
                        plantDescription,
                        plantHeight,
                        plantSpread,
                        colorOfInterest,
                        seasonOfInterest,
                        attractsBirds,
                        attractsButterflies,
                        attractsPollinators,
                        isEdible,
                        resistsDeer,
                        toxicToAnimals
                );
                plantRecords.add(plant);
            }
            if (plantRepository.count() < 90) {
                plantRepository.saveAll(plantRecords);
            }
        }
    }
}