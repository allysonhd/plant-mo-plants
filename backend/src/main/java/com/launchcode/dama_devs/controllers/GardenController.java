package com.launchcode.dama_devs.controllers;

import com.launchcode.dama_devs.models.User;
import com.launchcode.dama_devs.models.dto.CommentUserDTO;
import com.launchcode.dama_devs.models.dto.GardenDTO;
import com.launchcode.dama_devs.models.dto.GardenPlantDTO;
import com.launchcode.dama_devs.models.dto.PlantRatingDTO;
import com.launchcode.dama_devs.services.GardenPlantService;
import com.launchcode.dama_devs.services.UserDetailsImpl;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.launchcode.dama_devs.models.Garden;
import org.springframework.beans.factory.annotation.Autowired;
import com.launchcode.dama_devs.services.GardenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/garden")
public class GardenController {

    @Autowired
    private GardenPlantService gardenPlantService;

    @Autowired
    private GardenService gardenService;

    //Show all gardens in dashboard
    @GetMapping
    public Iterable<Garden> getGardensByUserId(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer userId = userDetails.getId();
        return gardenService.getGardensByUserId(userId);
    }

    @GetMapping("/user")
    public ResponseEntity<CommentUserDTO> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentUserDTO dto = new CommentUserDTO(userDetails.getId(), userDetails.getUsername());
        return ResponseEntity.ok(dto);
    }

    //Go to a specific garden
    @GetMapping("/garden-details/{gardenId}")
    public ResponseEntity<Garden> getGardenbyId(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Integer gardenId) {
        Integer userId = userDetails.getId();
        Optional<Garden> garden = gardenService.getGardenById(userId, gardenId);
        return garden.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/create")
    public ResponseEntity<Garden> createGarden(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody GardenDTO dto) {
        Integer userId = userDetails.getId();
        Garden newGarden = gardenService.saveGarden(userId, dto);
        System.out.println("User from authentication: " + userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(newGarden);
    }

    //Update an existing garden's fields with user edits
    @PutMapping("/garden-details/{gardenId}")
    public ResponseEntity<Garden> updateGarden(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Integer gardenId, @RequestBody GardenDTO dto) {
        Integer userId = userDetails.getId();
        Garden updatedGarden = gardenService.updateGarden(userId, gardenId, dto);
        return ResponseEntity.ok(updatedGarden);
    }

    //Delete an existing garden
    @DeleteMapping("/delete/{gardenId}")
    public ResponseEntity<Void> deleteGarden(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Integer gardenId) {
        Integer userId = userDetails.getId();
        Optional<Garden> gardenToDelete = gardenService.getGardenById(userId, gardenId);
        if (gardenToDelete.isPresent()) {
            gardenService.deleteGarden(userId, gardenId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{gardenId}/get-status/{plantId}")
    public ResponseEntity<GardenPlantDTO> getPlantInGardenStatus(@PathVariable Integer plantId, @PathVariable Integer gardenId) {
        GardenPlantDTO getStatus = gardenPlantService.getPlantInGardenStatus(plantId, gardenId);
                return ResponseEntity.status(HttpStatus.OK).body(getStatus);
    }

    @PostMapping("/{gardenId}/add-plant/{plantId}")
    public ResponseEntity<GardenPlantDTO> addPlantToGarden(@PathVariable Integer gardenId, @PathVariable Integer plantId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        GardenPlantDTO addPlantToGarden = gardenPlantService.addPlantToGarden(gardenId, plantId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(addPlantToGarden);
    }

    @DeleteMapping("/{gardenId}/remove-plant/{plantId}")
    public ResponseEntity<GardenPlantDTO> removePlantFromGarden(@PathVariable Integer gardenId, @PathVariable Integer plantId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        GardenPlantDTO removePlantFromGarden = gardenPlantService.removePlantFromGarden(gardenId, plantId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(removePlantFromGarden);
    }
}
