package com.launchcode.dama_devs.services;

import com.launchcode.dama_devs.models.Garden;
import com.launchcode.dama_devs.models.Plant;
import com.launchcode.dama_devs.models.data.GardenRepository;
import com.launchcode.dama_devs.models.data.PlantRepository;
import com.launchcode.dama_devs.models.dto.GardenPlantDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GardenPlantService {

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private PlantRepository plantRepository;

    //get status of plant in Garden
    public GardenPlantDTO getPlantInGardenStatus(Integer plantId, Integer gardenId) {
        Optional<Garden> gardenResult = gardenRepository.findById(gardenId);
        if (!gardenResult.isPresent()) {
            throw new IllegalArgumentException("Garden not found with ID" + gardenId);
        }
        Garden garden = gardenResult.get();

        Optional<Plant> plantResult = plantRepository.findById(plantId);
        if (!plantResult.isPresent()) {
            throw new IllegalArgumentException("Plant not found with ID" + plantId);
        }
        Plant plant = plantResult.get();
        GardenPlantDTO gardenPlantDTO = new GardenPlantDTO();
        gardenPlantDTO.setGarden(garden);
        if (garden.getPlants().contains(plant)) {
            gardenPlantDTO.setPlantInGarden(true);
        } else {
            gardenPlantDTO.setPlantInGarden(false);
        }
        return gardenPlantDTO;
    }

    //Add a plant to a garden. Create a DTO to send back with flag that plant was added
    public GardenPlantDTO addPlantToGarden(Integer gardenId, Integer plantId, Integer userId) {
        Optional<Garden> gardenResult = gardenRepository.findByIdAndUser_userId(gardenId, userId);
        if (!gardenResult.isPresent()) {
            throw new IllegalArgumentException("Garden not found with ID" + gardenId);
        }
        if (!gardenResult.get().getUser().getUserId().equals(userId)) {
            throw new SecurityException("User does not own this garden.");
        }

        Garden garden = gardenResult.get();

        Optional<Plant> plantResult = plantRepository.findById(plantId);
        if (!plantResult.isPresent()) {
            throw new IllegalArgumentException("Plant not found with ID" + plantId);
        }
        Plant plant = plantResult.get();

        if (!garden.getPlants().contains(plant)) {
            garden.addPlant(plant);
            gardenRepository.save(garden);
        }
        GardenPlantDTO gardenPlantDTO = new GardenPlantDTO();
        gardenPlantDTO.setGarden(garden);
        gardenPlantDTO.setPlantInGarden(true);

        return gardenPlantDTO;
    }

    //Remove a plant from a garden. send DTO back with flag that plant was removed.
    public GardenPlantDTO removePlantFromGarden(Integer gardenId, Integer plantId, Integer userId) {
        Optional<Garden> gardenResult = gardenRepository.findByIdAndUser_userId(gardenId, userId);
        if (!gardenResult.isPresent()) {
            throw new IllegalArgumentException("Garden not found with ID" + gardenId);
        }
        if (!gardenResult.get().getUser().getUserId().equals(userId)){
            throw new SecurityException("User does not own this garden.");
        }
        Garden garden = gardenResult.get();

        Optional<Plant> plantResult = plantRepository.findById(plantId);
        if (!plantResult.isPresent()) {
            throw new IllegalArgumentException("Plant not found with ID" + plantId);
        }
        Plant plant = plantResult.get();

        if (garden.getPlants().contains(plant)) {
            garden.removePlant(plant);
            gardenRepository.save(garden);
        }
        GardenPlantDTO gardenPlantDTO = new GardenPlantDTO();
        gardenPlantDTO.setGarden(garden);
        gardenPlantDTO.setPlant(plant);
        gardenPlantDTO.setPlantInGarden(false);

        return gardenPlantDTO;
    }
}
