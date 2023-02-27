package com.image_analyzer.repositories;

import com.image_analyzer.entities.ImageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesRepository extends CrudRepository<ImageEntity, Integer> {
    List<ImageEntity> findAll();
    List<ImageEntity> findByObjectsContaining(String string);

}
