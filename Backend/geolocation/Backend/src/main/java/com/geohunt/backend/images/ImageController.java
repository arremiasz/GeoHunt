package com.geohunt.backend.images;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ImageController {
    @Autowired
    private ImageService imageService;

    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImageById(@PathVariable long id) throws IOException{
        byte[] imageData = imageService.getImageBytes(id);
        if(imageData != null){
            return ResponseEntity.ok(imageData);
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/images")
    public ResponseEntity<String> handleFileUpload(@RequestParam("image") MultipartFile imageFile){
        Image image = imageService.saveImage(imageFile, "");
        if(image == null){
            return ResponseEntity.badRequest().build();
        }
        else {
            return ResponseEntity.ok("Image uploaded successfully: " + image.getId());
        }
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable long id){
        if(imageService.deleteImage(id)){
            return ResponseEntity.ok("File deleted successfully");
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
}
