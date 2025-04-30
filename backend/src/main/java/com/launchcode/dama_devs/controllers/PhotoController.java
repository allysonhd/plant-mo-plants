package com.launchcode.dama_devs.controllers;

import com.launchcode.dama_devs.models.Garden;
import com.launchcode.dama_devs.models.Photo;
import com.launchcode.dama_devs.models.User;
import com.launchcode.dama_devs.models.data.GardenRepository;
import com.launchcode.dama_devs.services.PhotoService;
import com.launchcode.dama_devs.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Adjust for React
public class PhotoController {

    private final PhotoService photoService;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("File is too large! Please upload a smaller file.");
    }

//******************  this will only allow the gardens that do not have a photo in the drop down menu
//**** fetched on photo-upload

    @GetMapping("/gardens-without-photo/user")
    public ResponseEntity<List<Garden>> getGardensWithoutPhotoByUser( @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer userId = userDetails.getId();
        List<Garden> gardens = photoService.getGardensWithoutPhotosByUserId(userId);
        return ResponseEntity.ok(gardens);
    }

    @PostMapping("/upload/{gardenId}")
    public ResponseEntity<Photo> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @PathVariable("gardenId") Integer gardenId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        Integer userId = userDetails.getId();
        String photoName = file.getOriginalFilename();
        return ResponseEntity.ok(photoService.savePhoto(file, photoName, gardenId, userId));
    }
//******* Gets the photos by user to display in the dashboard

    @GetMapping("/user")
    public ResponseEntity<List<Photo>> getPhotosForCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer userId = userDetails.getId(); // pulled from your UserDetailsImpl
        List<Photo> photos = photoService.findPhotosByUser_UserId(userId);
        return ResponseEntity.ok(photos);
    }

//******* Allows the user to display their favorite garden photo in the dashboard as the featured photo

    @PutMapping("/featured/{photoId}")
    public ResponseEntity<?> setFeaturedPhoto(
            @PathVariable Integer photoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Integer userId = userDetails.getId();
        try {
            photoService.setFeaturedPhoto(photoId, userId);
            return ResponseEntity.ok("Photo set as featured.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    //****Keeping logic in case we change how or where we are updating/deleting photos in garden details page
//******** UPDATE
    @PutMapping("/update/{photoId}")
    public ResponseEntity<?> updatePhoto(
            @PathVariable Integer photoId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "photoName", required = false) String photoName,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Integer userId = userDetails.getId();
            Photo updatedPhoto = photoService.updatePhoto(photoId, file, photoName, userId);
            return ResponseEntity.ok(updatedPhoto);
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //********** DELETE
    @DeleteMapping("/delete/{photoId}")
    public ResponseEntity<?> deletePhoto(
            @PathVariable Integer photoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Integer userId = userDetails.getId();
            photoService.deletePhoto(photoId, userId);
            return ResponseEntity.ok("Photo deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/gardens/user")
    public ResponseEntity<List<Garden>> getGardensByUser( @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer userId = userDetails.getId();
        List<Garden> gardens = photoService.getGardensByUserId(userId);
        return ResponseEntity.ok(gardens);
    }

    @GetMapping("/garden/{gardenId}/user")
    public ResponseEntity<List<Photo>> getPhotosByGardenAndUser(
            @PathVariable Integer gardenId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer userId = userDetails.getId();
        return ResponseEntity.ok(photoService.findByGardenIdAndUser_UserId(gardenId, userId));
    }

}
