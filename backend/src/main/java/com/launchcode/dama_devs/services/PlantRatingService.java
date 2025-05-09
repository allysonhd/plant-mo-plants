package com.launchcode.dama_devs.services;

import com.launchcode.dama_devs.models.Plant;
import com.launchcode.dama_devs.models.PlantRating;
import com.launchcode.dama_devs.models.User;
import com.launchcode.dama_devs.models.data.PlantRatingRepository;
import com.launchcode.dama_devs.models.data.PlantRepository;
import com.launchcode.dama_devs.models.data.UserRepository;
import com.launchcode.dama_devs.models.dto.AverageRatingDTO;
import com.launchcode.dama_devs.models.dto.PlantRatingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlantRatingService {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlantRatingRepository plantRatingRepository;

    public PlantRatingDTO getRating(Integer userId, Integer plantId) {
        Optional<PlantRating> currentRating = plantRatingRepository.findByUser_userIdAndPlantId(userId, plantId);
        if (currentRating.isEmpty()) {
            PlantRatingDTO plantRatingDto = new PlantRatingDTO();
            plantRatingDto.setPlantRating(0);
            plantRatingDto.setUserId(userId);
            plantRatingDto.setPlantId(plantId);
            return plantRatingDto;
        } else {
            PlantRating rating = currentRating.get();
            PlantRatingDTO plantRatingDto = new PlantRatingDTO();
            plantRatingDto.setPlantRating(rating.getPlantRating());
            plantRatingDto.setUserId(userId);
            plantRatingDto.setPlantId(plantId);
            return plantRatingDto;
        }
    }

    public PlantRatingDTO createPlantRating(Integer userId, int plantRating, Integer plantId) {

        //get the user id
        Optional<User> userResult = userRepository.findById(userId);
        if (!userResult.isPresent()) {
            throw new IllegalArgumentException("User not found with ID:" + userId);
        }
        User user = userResult.get();

        //get the plant id
        Optional<Plant> plantResult = plantRepository.findById(plantId);
        if (!plantResult.isPresent()) {
            throw new IllegalArgumentException("Plant not found with ID:" + plantId);
        }
        Plant plant = plantResult.get();

        //check for a current rating otherwise it saves any rating changes as "new" ratings in the database
        Optional<PlantRating> currentRating = plantRatingRepository.findByUser_userIdAndPlantId(userId, plantId);
        if (currentRating.isPresent()) {
            PlantRating rating = currentRating.get();
            rating.setPlantRating(plantRating);
            plantRatingRepository.save(rating);

            //create a new plantRating DTO with updated rating
            PlantRatingDTO plantRatingDTO = new PlantRatingDTO();
            plantRatingDTO.setPlantRating(rating.getPlantRating());
            plantRatingDTO.setUserId(userId);
            plantRatingDTO.setPlantId(plantId);
            return plantRatingDTO;

        } else {
            //use constructor from model to create a new plant rating and save it to the repository
            PlantRating newRating = new PlantRating(plantRating, user, plant);
            plantRatingRepository.save(newRating);

            //create a new plantRating DTO
            PlantRatingDTO plantRatingDTO = new PlantRatingDTO();
            plantRatingDTO.setPlantRating(newRating.getPlantRating());
            plantRatingDTO.setUserId(userId);
            plantRatingDTO.setPlantId(plantId);
            return plantRatingDTO;
        }
    }

    public AverageRatingDTO calculateAverageRating(Integer plantId) {
        List<PlantRating> plantRatingList = plantRatingRepository.findAllByPlantId(plantId);

        //if there are no ratings for the plant yet, the average rating is set to zero
        if (plantRatingList.isEmpty()) {
            AverageRatingDTO averageRating = new AverageRatingDTO();
            averageRating.setAverageRating(0);
            averageRating.setPlantId(plantId);
            return averageRating;
        } else {
            int ratingTotal = 0;
            for (PlantRating rating : plantRatingList) {
                ratingTotal += rating.getPlantRating();
            }
                int result = Math.round(ratingTotal / plantRatingList.size());

                AverageRatingDTO averageRating = new AverageRatingDTO();
                averageRating.setAverageRating(result);
                averageRating.setPlantId(plantId);
                return averageRating;
            }
        }
    }