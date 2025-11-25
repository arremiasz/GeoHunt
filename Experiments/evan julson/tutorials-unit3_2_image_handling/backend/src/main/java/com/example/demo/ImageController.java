package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class ImageController {

    // replace this! careful with the operating system in use
    private static String directory = "C:/Users/evan/2_jubair_5";

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getImageById(@PathVariable int id) throws IOException {
        Image image = imageRepository.findById(id);
        File imageFile = new File(image.getFilePath());
        return Files.readAllBytes(imageFile.toPath());
    }

    @PostMapping("images")
    public String handleFileUpload(@RequestParam("image") MultipartFile imageFile)  {

        try {
            File destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
            if(destinationFile.exists()){
                System.out.println("file exists");
                return "Failed to upload file: file already exists";
            }

            imageFile.transferTo(destinationFile);  // save file to disk

            Image image = new Image();
            image.setFilePath(destinationFile.getAbsolutePath());
            imageRepository.save(image);

            return "File uploaded successfully: " + destinationFile.getAbsolutePath() + " id: " + image.getId();
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }

    @PutMapping("/images/{id}")
    public String updateImage(@RequestParam("image") MultipartFile imageFile, @PathVariable int id){
        try{
            Image image = imageRepository.findById(id);
            File destinationFile = new File(image.getFilePath());

            imageFile.transferTo(destinationFile);

            imageRepository.save(image);

            return "File updated successfully: " + destinationFile.getAbsolutePath() + " id: " + image.getId();
        }
        catch (IOException e){
            return "Failed to update file: " + e.getMessage();
        }
    }

    @DeleteMapping("/images/{id}")
    public String deleteImage(@PathVariable int id){
        Image image = imageRepository.findById(id);
        File imageFile = new File(image.getFilePath());

        if(imageFile.delete()){
            imageRepository.delete(image);
            return "File deleted successfully";
        }
        else {
            return "Failed to delete file";
        }
    }

}
