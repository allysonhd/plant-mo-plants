package com.launchcode.dama_devs.controllers;

import com.launchcode.dama_devs.models.Plant;
import com.launchcode.dama_devs.models.data.PlantRepository;
import com.launchcode.dama_devs.services.PlantFilteringService;
import com.launchcode.dama_devs.services.UserDetailsImpl;
import com.launchcode.dama_devs.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/plant")
public class PlantController {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private PlantFilteringService plantFilteringService;

    //combined method calls in this controller because I wanted to try using the @RequestParam
    //if the request param is not null or empty (i.e. someone has chosen a type from the plantType filter, run the type filter method.
    //otherwise, get all matching garden plants.
    @GetMapping("/{gardenId}/search-plants")
    public ResponseEntity<List<Plant>> getMatchingGardenPlants(@PathVariable Integer gardenId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam(required = false) String selectedPlantType) {
        if (selectedPlantType != null && !selectedPlantType.isEmpty()) {
            List<Plant> matchingTypePlants = plantFilteringService.filterGardenPlantsByType(gardenId, userDetails.getId(), selectedPlantType);
            return ResponseEntity.status(HttpStatus.OK).body(matchingTypePlants);
        } else {
            List<Plant> matchingGardenPlants = plantFilteringService.filterPlantsByGardenFields(gardenId,userDetails.getId());
            return ResponseEntity.status(HttpStatus.OK).body(matchingGardenPlants);
        }
    }

    @GetMapping("/{plantId}")
    public ResponseEntity<Optional<Plant>> getPlant(@PathVariable Integer plantId) {
        Optional<Plant> plant = plantRepository.findById(plantId);
        return ResponseEntity.status(HttpStatus.OK).body(plant);
    }
}
