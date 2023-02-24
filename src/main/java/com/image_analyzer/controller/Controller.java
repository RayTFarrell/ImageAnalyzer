package com.image_analyzer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class Controller {

    @Value("${IMAGGA_KEY}")
    String imaggAPIKey;

    @PostMapping(value = "images")
    public @ResponseBody ResponseEntity postImage(@RequestBody Map<String, String> formData) throws JsonProcessingException {
        List<String> list = new ArrayList<>();
        list.add("test");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(list);

        return new ResponseEntity<>(json, HttpStatus.OK);

    }
    @GetMapping(value = "image")
    public @ResponseBody ResponseEntity getImages() throws IOException {

        log.info(imaggAPIKey);

        String endpoint_url = "https://api.imagga.com/v2/tags";
        String image_url = "https://imagga.com/static/images/tagging/wind-farm-538576_640.jpg";

        String url = endpoint_url + "?image_url=" + image_url;
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", imaggAPIKey);

        int responseCode = connection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String jsonResponse = connectionInput.readLine();

        connectionInput.close();

        System.out.println(jsonResponse);

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
