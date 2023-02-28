package com.image_analyzer.controllers;

import com.image_analyzer.entities.ImageEntity;
import com.image_analyzer.exceptions.ImageProcessingException;
import com.image_analyzer.exceptions.InvalidParametersException;
import com.image_analyzer.repositories.ImagesRepository;
import com.image_analyzer.services.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/images")
@Log4j2
public class Controller {

    @Autowired
    ImageService imageService;
    @Autowired
    ImagesRepository imagesRepository;

    @PostMapping(consumes = "multipart/form-data")
    public @ResponseBody ResponseEntity postImage(@RequestPart(required = false) String url,
                                                  @RequestPart(required = false) MultipartFile file,
                                                  @RequestPart(required = false) String label,
                                                  @RequestPart(required = false) String objectDetectionEnabled) throws IOException, InvalidParametersException {

        return new ResponseEntity<>(imageService.handleRequest(url, file, label, objectDetectionEnabled), HttpStatus.OK);

    }
    @GetMapping(value={"/{id}"})
    public @ResponseBody ResponseEntity getImage(@PathVariable(value="id") String id) throws ImageProcessingException {
        try {
            return new ResponseEntity<>(imagesRepository.findAllById(Collections.singleton(Integer.parseInt(id))), HttpStatus.OK);
        } catch (Exception e) {
            throw new ImageProcessingException(e.getClass().getName()  + " " + e.getMessage());
        }
    }
    @GetMapping()
    public @ResponseBody ResponseEntity getImages(@RequestParam(required = false) String objects) throws ImageProcessingException {
        try {
            List<ImageEntity> imageEntityList = imageService.findImagesWithOptionalParams(objects);
            return new ResponseEntity<>(imageEntityList, HttpStatus.OK);
        }
        catch (Exception e){
            throw new ImageProcessingException(e.getClass().getName()  + " " + e.getMessage());
        }
    }
}