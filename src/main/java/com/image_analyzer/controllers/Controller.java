package com.image_analyzer.controllers;

import com.image_analyzer.entities.ImageEntity;
import com.image_analyzer.exceptions.ControllerException;
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

@RestController
@RequestMapping("/images")
@Log4j2
@Profile("!test")
public class Controller {

    @Autowired
    ImageService imageService;
    @Autowired
    ImagesRepository imagesRepository;

    @PostMapping(consumes = "multipart/form-data")
    public @ResponseBody ResponseEntity postImage(@RequestPart(required = false) String url,
                                                  @RequestPart(required = false) MultipartFile file,
                                                  @RequestPart(required = false) String label,
                                                  @RequestPart(required = false) String objectDetectionEnabled) throws IOException, ControllerException {

        ImageEntity imageEntity;
        if (url != null && file == null) {
            imageEntity = imageService.processImageURL(url, label, objectDetectionEnabled);
        } else {
            imageEntity = imageService.processImageFile(file, label, objectDetectionEnabled);
        }

        return new ResponseEntity<>(imageEntity, HttpStatus.OK);

    }
    @GetMapping(value={"/{id}"})
    public @ResponseBody ResponseEntity getImage(@PathVariable(value="id") String id) throws ControllerException{
        return new ResponseEntity<>(imagesRepository.findAllById(Collections.singleton(Integer.parseInt(id))), HttpStatus.OK);
    }
    @GetMapping()
    public @ResponseBody ResponseEntity getImages(@RequestParam(required = false) String objects) throws ControllerException {
        return imageService.findImagesWithOptionalParams(objects);
    }
}