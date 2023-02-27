package com.image_analyzer.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.image_analyzer.exceptions.ControllerException;
import com.image_analyzer.repositories.ImagesRepository;
import com.image_analyzer.services.ImageService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                                                  @RequestPart(required = false) String objectDetectionEnabled) throws IOException {
        URL urlObject = new URL(url);
        System.out.println(FilenameUtils.getName(urlObject.getPath()));
        System.out.println(FilenameUtils.getName(String.valueOf(file)));
        System.out.println(url);
        System.out.println(label);
        System.out.println(objectDetectionEnabled);
        System.out.println(file);

        if (url != null && file == null) {
            imageService.processImageURL(url, label.describeConstable(), objectDetectionEnabled.describeConstable());
        } else {
//            imageService.processImageFile(imageEntityBuilder.imageEntityPK(imageEntityPK).name(FilenameUtils.getName(urlObject.getPath()))
//                    .label(label).build(), objectDetectionEnabled);
        }


        List<String> list = new ArrayList<>();
        list.add("test");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(list);

        return new ResponseEntity<>(json, HttpStatus.OK);

    }
    @GetMapping
    public @ResponseBody ResponseEntity getImages() throws ControllerException {
        return new ResponseEntity<>(imagesRepository.findAll(), HttpStatus.OK);

    }
    @GetMapping(value={"/{id}"})
    public @ResponseBody ResponseEntity getImage(@PathVariable(value="id") String id){
        return new ResponseEntity<>(imagesRepository.findAllById(Collections.singleton(Integer.parseInt(id))), HttpStatus.OK);
    }
//    @GetMapping(value = "images")
//    public @ResponseBody ResponseEntity getImageObjects(@RequestBody Map<String, String> formData){
//
//        return new ResponseEntity<>(json, HttpStatus.OK);
//
//    }
//}
}