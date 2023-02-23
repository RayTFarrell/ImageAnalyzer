package com.image_analyzer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    @PostMapping(value = "images")
    public @ResponseBody ResponseEntity postImage(@RequestBody Map<String, String> formData) throws JsonProcessingException {
        List<String> list = new ArrayList<>();
        list.add("test");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(list);

        return new ResponseEntity<>(json, HttpStatus.OK);

    }
    @GetMapping(value = "image")
    public @ResponseBody ResponseEntity getImages(){

        return new ResponseEntity<>("hi", HttpStatus.OK);

    }
//    @GetMapping(value = "images")
//    public @ResponseBody ResponseEntity getImageObjects(@RequestBody Map<String, String> formData){
//
//        return new ResponseEntity<>(json, HttpStatus.OK);
//
//    }
//    @GetMapping(value = "images")
//    public @ResponseBody ResponseEntity getImage(@RequestBody Map<String, String> formData){
//
//        return new ResponseEntity<>(json, HttpStatus.OK);
//
//    }


}
