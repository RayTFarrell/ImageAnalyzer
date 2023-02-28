package com.image_analyzer.controllers;

import com.image_analyzer.entities.ImageEntity;
import com.image_analyzer.exceptions.ImageProcessingException;
import com.image_analyzer.exceptions.InvalidParametersException;
import com.image_analyzer.repositories.ImagesRepository;
import com.image_analyzer.services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ImageService mockedImageService;
    @MockBean
    ImagesRepository mockedImagesRepository;
    private String validGetResponse = "[{\"id\":1,\"fileName\":\"test.jpg\",\"contentType\":\"jpg\",\"label\":\"test\",\"objects\":\"object, object 2, object 3\"}]";
    private String validPostResponse = "{\"id\":0,\"fileName\":null,\"contentType\":null,\"label\":null,\"objects\":null}";
    private List<ImageEntity> imageEntityList;

    @BeforeEach
    public void init(){
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setId(1);
        imageEntity.setLabel("test");
        imageEntity.setFileName("test.jpg");
        imageEntity.setContentType("jpg");
        imageEntity.setObjects("object, object 2, object 3");
        imageEntityList = new ArrayList<>();
        imageEntityList.add(imageEntity);
    }

    @Test
    void getImagesByObjectsTest() throws Exception {
        when(mockedImageService.findImagesWithOptionalParams(any())).thenReturn(imageEntityList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/images?objects=test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        assertEquals(validGetResponse, mvcResult.getResponse().getContentAsString());

        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/images")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        assertEquals(validGetResponse, mvcResult.getResponse().getContentAsString());
    }
    @Test
    void getImagesByObjectsExceptionTest() throws Exception {
        when(mockedImageService.findImagesWithOptionalParams(any())).thenThrow(ImageProcessingException.class);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/images?objects=test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is5xxServerError()).andReturn();

        assertEquals("{\"status\":500,\"message\":\"com.image_analyzer.exceptions.ImageProcessingException null\"}", mvcResult.getResponse().getContentAsString());

    }

    @Test
    void getImageTest() throws Exception {
        when(mockedImagesRepository.findAllById(any())).thenReturn(imageEntityList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/images/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        assertEquals(validGetResponse, mvcResult.getResponse().getContentAsString());
    }
    @Test
    void getImageExceptionTest() throws Exception {
        when(mockedImagesRepository.findAllById(any())).thenThrow(RuntimeException.class);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/images/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is5xxServerError()).andReturn();

        assertEquals("{\"status\":500,\"message\":\"java.lang.RuntimeException null\"}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    void postImageTest() throws Exception {
        when(mockedImageService.handleRequest(any(),any(),any(),any())).thenReturn(ImageEntity.builder().build());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/images")
                        .param("url", "url")
                        .param("file", "image.jpg")
                        .param("label", "label")
                        .param("objectDetectionEnabled", "true")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        assertEquals(validPostResponse, mvcResult.getResponse().getContentAsString());

    }
    @Test
    void postExceptionImageTest() throws Exception {
        when(mockedImageService.handleRequest(any(),any(),any(),any())).thenThrow(InvalidParametersException.class);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/images")
                        .param("label", "label")
                        .param("objectDetectionEnabled", "true")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print()).andExpect(status().is4xxClientError()).andReturn();

        assertEquals("{\"status\":400}", mvcResult.getResponse().getContentAsString());

    }
}