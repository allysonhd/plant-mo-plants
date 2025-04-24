package com.launchcode.dama_devs.models.data;

import com.launchcode.dama_devs.models.Garden;
import com.launchcode.dama_devs.models.PlantRating;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GardenRepository extends CrudRepository<Garden, Integer> {
    List<Garden> findByUser_UserId(Integer userId);
    Optional<Garden> findByIdAndUser_userId(Integer gardenId, Integer userId);
}
//@Repository
//public interface GardenRepository extends CrudRepository<Garden, Integer> {
//    List<Garden> findByUserId(Integer userId);
//}
