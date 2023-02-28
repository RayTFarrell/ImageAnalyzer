package com.image_analyzer.services;

import com.image_analyzer.entities.ImageEntity;
import com.image_analyzer.repositories.ImagesRepository;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    @Mock
    @Autowired
    ImaggaService mockedImaggaService;
    @InjectMocks
    @Autowired
    ImageService imageService;
    @Autowired
    ImagesRepository imagesRepository;
    String imaggaURLResponse = "{\"result\":{\"tags\":[{\"confidence\":100,\"tag\":{\"en\":\"turbine\"}},{\"confidence\":64.8014373779297,\"tag\":{\"en\":\"wind\"}},{\"confidence\":63.3033409118652,\"tag\":{\"en\":\"generator\"}},";

    @Test
    @DisplayName("When an image is processed containing a url, label, and object detection as true, save it into H2 with provided labels and response from Imagga.")
    void processImageURLWithLabelAndObjectDetectionTest() throws IOException {
        when(mockedImaggaService.getImageObjectDataByURL(anyString())).thenReturn(imaggaURLResponse);

        ImageEntity imageEntityResult = imageService.processImageURL("https://www.test.com/test.bmp", "test_label", "true");
        Optional<ImageEntity> imageEntityRepositoryResult = imagesRepository.findById(imageEntityResult.getId());

        assertEquals(imageEntityResult.getId(), imageEntityRepositoryResult.get().getId());
        assertEquals("test.bmp", imageEntityRepositoryResult.get().getFileName());
        assertEquals("bmp", imageEntityRepositoryResult.get().getContentType());
        assertEquals("test_label", imageEntityRepositoryResult.get().getLabel());
        assertEquals(imaggaURLResponse, imageEntityRepositoryResult.get().getObjects());
    }
    @Test
    @DisplayName("When an image is processed containing a url and object detection as false, save it into H2 with generated labels and no response from Imagga.")
    void processImageURLWithNoLabelAndNoObjectDetectionTest() throws IOException {
        ImageEntity imageEntityResult = imageService.processImageURL("https://www.test.com/test-image.png", null, "false");
        Optional<ImageEntity> imageEntityRepositoryResult = imagesRepository.findById(imageEntityResult.getId());

        assertEquals(imageEntityResult.getId(), imageEntityRepositoryResult.get().getId());
        assertEquals("test-image.png", imageEntityRepositoryResult.get().getFileName());
        assertEquals("png", imageEntityRepositoryResult.get().getContentType());
        assertEquals("test-image", imageEntityRepositoryResult.get().getLabel());
        assertEquals(null, imageEntityRepositoryResult.get().getObjects());
    }
    @Test
    @DisplayName("When an image is processed containing a file and object detection as true, save it into H2 with provided label and response from Imagga.")
    void processImageFileWithLabelAndObjectDetectionTest() throws IOException {
        when(mockedImaggaService.getImageObjectsByFile(any())).thenReturn(imaggaURLResponse);
        File file = ResourceUtils.getFile("classpath:pitbull.jpeg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "image/jpeg", IOUtils.toByteArray(input));

        ImageEntity imageEntityResult = imageService.processImageFile(multipartFile, "pitbullLabel", "true");
        Optional<ImageEntity> imageEntityRepositoryResult = imagesRepository.findById(imageEntityResult.getId());

        assertEquals(imageEntityResult.getId(), imageEntityRepositoryResult.get().getId());
        assertEquals("pitbull.jpeg", imageEntityRepositoryResult.get().getFileName());
        assertEquals("image/jpeg", imageEntityRepositoryResult.get().getContentType());
        assertEquals("pitbullLabel", imageEntityRepositoryResult.get().getLabel());
        assertEquals(imaggaURLResponse, imageEntityRepositoryResult.get().getObjects());
    }
    @Test
    @DisplayName("When an image is processed containing a file and object detection as false, save it into H2 with generated labels and no response from Imagga.")
    void processImageFileWithNoLabelAndNoObjectDetectionTest() throws IOException {
        File file = ResourceUtils.getFile("classpath:pitbull.jpeg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "image/jpeg", IOUtils.toByteArray(input));

        ImageEntity imageEntityResult = imageService.processImageFile(multipartFile, null, "false");
        Optional<ImageEntity> imageEntityRepositoryResult = imagesRepository.findById(imageEntityResult.getId());

        assertEquals(imageEntityResult.getId(), imageEntityRepositoryResult.get().getId());
        assertEquals("pitbull.jpeg", imageEntityRepositoryResult.get().getFileName());
        assertEquals("image/jpeg", imageEntityRepositoryResult.get().getContentType());
        assertEquals("pitbull", imageEntityRepositoryResult.get().getLabel());
        assertEquals(null, imageEntityRepositoryResult.get().getObjects());
    }
}