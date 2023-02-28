package com.image_analyzer.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "IMAGES")
public class ImageEntity implements Serializable {

    private static final long serialVersionUID = -1559835586880040424L;


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name="FILENAME")
    private String fileName;
    @Column(name="CONTENTTYPE")
    private String contentType;
    @Column(name="LABEL")
    private String label;
    @Column(name="OBJECTS")
    private String objects;


}
