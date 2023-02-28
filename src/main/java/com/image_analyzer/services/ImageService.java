package com.image_analyzer.services;

import com.image_analyzer.entities.ImageEntity;
import com.image_analyzer.exceptions.ImageProcessingException;
import com.image_analyzer.exceptions.InvalidParametersException;
import com.image_analyzer.repositories.ImagesRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ImageService {

    @Autowired
    ImagesRepository imagesRepository;
    @Autowired
    ImaggaService imaggaService;

    public ImageEntity handleRequest(String url,
                                     MultipartFile file,
                                     String label,
                                     String objectDetectionEnabled) throws IOException, InvalidParametersException {
        if (url != null && file == null) {
            return processImageURL(url, label, objectDetectionEnabled);
        } else if (url == null && file != null) {
            return processImageFile(file, label, objectDetectionEnabled);
        }
        else{
            throw new InvalidParametersException("Invalid Request parameters: " + url + file +  label + objectDetectionEnabled);
        }
    }
    public ImageEntity processImageURL(String url,
                                    String label,
                                    String objectDetectionEnabled) throws IOException {

        ImageEntity imageEntity = new ImageEntity();
        URL urlObject = new URL(url);
        imageEntity.setFileName(FilenameUtils.getName(urlObject.getPath()));
        imageEntity.setContentType(FilenameUtils.getExtension(urlObject.getPath()));
        if (objectDetectionEnabled != null && Boolean.parseBoolean(objectDetectionEnabled)) {
            imageEntity.setObjects(imaggaService.getImageObjectDataByURL(url));
        }
        if (label != null){
            imageEntity.setLabel(label);
        }
        else {
            imageEntity.setLabel(FilenameUtils.getBaseName(urlObject.getPath()));
        }
        imagesRepository.save(imageEntity);
        return imageEntity;
    }
    public ImageEntity processImageFile(MultipartFile file,
                                        String label,
                                        String objectDetectionEnabled) throws IOException {

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setFileName(file.getOriginalFilename());
        imageEntity.setContentType(file.getContentType());
        if (objectDetectionEnabled != null && Boolean.parseBoolean(objectDetectionEnabled)) {
            imageEntity.setObjects(imaggaService.getImageObjectsByFile(file));
        }
        if (label != null){
            imageEntity.setLabel(label);
        }
        else {
            imageEntity.setLabel(FilenameUtils.getBaseName(file.getOriginalFilename()));
        }
        imagesRepository.save(imageEntity);
        return imageEntity;
    }
    public List<ImageEntity> findImagesWithOptionalParams(String objects) throws ImageProcessingException {
        if (objects == null) {
            return imagesRepository.findAll();
        }
        else {
            List<ImageEntity> imageEntityList = new ArrayList<>();
            List<String> objectsList = Stream.of(objects.split(",", -1))
                    .collect(Collectors.toList());
            try {
                objectsList.forEach(object -> {
                    imageEntityList.addAll(imagesRepository.findByObjectsContains(object));
                });
            }
            catch (Exception e){
                throw new ImageProcessingException(e.getClass().getName()  + " " + e.getMessage());
            }
            return imageEntityList;
        }
    }
}
