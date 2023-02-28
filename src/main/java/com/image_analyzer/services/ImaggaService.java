package com.image_analyzer.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringJoiner;

@Service
public class ImaggaService {
    @Value("${IMAGGA_KEY}")
    String imaggAPIKey;

    public String getImageObjectDataByURL(String imageUrl) throws IOException {

        String endpointUrl = "https://api.imagga.com/v2/tags";
        String url = endpointUrl + "?image_url=" + imageUrl;

        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", imaggAPIKey);

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String jsonResponse = connectionInput.readLine();

        connectionInput.close();

        return getListOfImageObjects(jsonResponse);
    }
    private String getListOfImageObjects(String response) {
        StringJoiner objectString = new StringJoiner(", ");
        JsonElement responseJSON = new JsonParser().parse(response).getAsJsonObject()
                .get("result").getAsJsonObject().get("tags").getAsJsonArray();
        responseJSON.getAsJsonArray().forEach(jsonElement -> {
            objectString.add(jsonElement.getAsJsonObject().get("tag").getAsJsonObject().get("en").getAsString());
        });
        return objectString.toString();
    }
    private String getImageIdByFile(MultipartFile inputFile) throws IOException {
        File imageProcessingFile = ResourceUtils.getFile("classpath:imageProcessingFile.tmp");
        inputFile.transferTo(imageProcessingFile);

        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "Image Upload";
        URL urlObject = new URL("https://api.imagga.com/v2/uploads");
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestProperty("Authorization", imaggAPIKey);
        connection.setUseCaches(false);
        connection.setDoOutput(true);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + boundary);

        DataOutputStream request = new DataOutputStream(connection.getOutputStream());

        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + imageProcessingFile.getName() + "\"" + crlf);
        request.writeBytes(crlf);

        InputStream inputStream = new FileInputStream(imageProcessingFile);
        int bytesRead;
        byte[] dataBuffer = new byte[1024];
        while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
            request.write(dataBuffer, 0, bytesRead);
        }

        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
        request.close();

        InputStream responseStream = new BufferedInputStream(connection.getInputStream());

        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

        String line = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ((line = responseStreamReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        responseStreamReader.close();

        String response = stringBuilder.toString();

        responseStream.close();
        connection.disconnect();

        return response;
    }

    public String getImageObjectsByFile(MultipartFile inputFile) throws IOException {
        JsonObject responseJSON = new JsonParser().parse(getImageIdByFile(inputFile)).getAsJsonObject();
        String imageId = responseJSON.get("result").getAsJsonObject().get("upload_id").getAsString();

        String endpointUrl = "https://api.imagga.com/v2/tags";
        String url = endpointUrl + "?image_upload_id=" + imageId;
        URL urlObject = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        connection.setRequestProperty("Authorization", imaggAPIKey);

        int responseCode = connection.getResponseCode();

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String jsonResponse = connectionInput.readLine();

        connectionInput.close();

        return getListOfImageObjects(jsonResponse);
    }
}
