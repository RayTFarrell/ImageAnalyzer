package com.image_analyzer.services;

import com.image_analyzer.entities.ImageEntity;
import com.image_analyzer.repositories.ImagesRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@Profile("!test")
public class ImageService {

    @Value("${IMAGGA_KEY}")
    String imaggAPIKey;
    @Autowired
    ImagesRepository imagesRepository;
    EntityManager entityManager;


    //    public ImageEntity processImageURL(ImageEntity imageEntity, String url, String objectDetectionEnabled) throws IOException {
//
//        if (objectDetectionEnabled != null && Boolean.parseBoolean(objectDetectionEnabled)) {
//            imageEntity.setMetaData(getImageMetaDataByURL(url));
//
//        }
//
//    }
    public ImageEntity processImageURL(String url,
                                    Optional<String> label,
                                    Optional<String> objectDetectionEnabled) throws IOException {

        ImageEntity imageEntity = new ImageEntity();

        if (!objectDetectionEnabled.isEmpty() && Boolean.parseBoolean(String.valueOf(objectDetectionEnabled))) {
            imageEntity.setObjects(getImageMetaDataByURL(url));
        }
        if (!label.isEmpty()){
            imageEntity.setLabel(label.get());
        }
        else {
            URL urlObject = new URL(url);
            imageEntity.setLabel(FilenameUtils.getName(urlObject.getPath()));
        }
        imagesRepository.save(imageEntity);
        return imageEntity;
    }

    public String getImageMetaDataByURL(String imageUrl) throws IOException {

        String endpointUrl = "https://api.imagga.com/v2/tags";
        String url = endpointUrl + "?image_url=" + imageUrl;

        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", imaggAPIKey);

        int responseCode = connection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String jsonResponse = connectionInput.readLine();

        connectionInput.close();

        return jsonResponse;
    }
}
