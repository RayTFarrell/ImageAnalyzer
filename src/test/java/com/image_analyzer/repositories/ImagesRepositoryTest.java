package com.image_analyzer.repositories;

import com.image_analyzer.entities.ImageEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ImagesRepositoryTest {

    @Autowired
    ImagesRepository imagesRepository;

    @Test
    void findByIDTest() {
        ImageEntity imageEntity = ImageEntity.builder().id(0)
                .fileName("test.jpg")
                .contentType("jpg")
                .label("test_label")
                .objects("{test: field}").build();
        Optional<ImageEntity> imageEntityResult = imagesRepository.findById(imageEntity.getId());

        assertEquals(imageEntityResult.get().getId(), imageEntity.getId());
        assertEquals(imageEntityResult.get().getFileName(), imageEntity.getFileName());
        assertEquals(imageEntityResult.get().getContentType(), imageEntity.getContentType());
        assertEquals(imageEntityResult.get().getLabel(), imageEntity.getLabel());
        assertEquals(imageEntityResult.get().getObjects(), imageEntity.getObjects());
    }
    @Test
    void findAllTest() {
        List<ImageEntity> imageEntitySet = imagesRepository.findAll();
        assertEquals(5, imageEntitySet.size());
    }
    @Test
    void findAllById() {
        Optional<ImageEntity> imageEntity = imagesRepository.findById(1);
        assertEquals(1, imageEntity.get().getId());
        assertEquals("test.png", imageEntity.get().getFileName());
        assertEquals("png", imageEntity.get().getContentType());
        assertEquals("test_label2", imageEntity.get().getLabel());
        assertEquals("{test: field}", imageEntity.get().getObjects());
    }
    @Test
    void findAllByObjectsTest() {
        List<ImageEntity> imageEntitySet = imagesRepository.findByObjectsContains("dog");
        assertEquals(2, imageEntitySet.size());
    }

}