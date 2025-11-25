package com.geohunt.backend.images;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    @Autowired
    ImageRepository imageRepository;

    // Save Image
    public Image saveImage(MultipartFile imageFile, String folder){

    }

    // Get Image Object
    public Image getImageObj(){

    }

    // Get Image byte[]
    public byte[] getImageBytes(){

    }

    // Delete Image
    public boolean deleteImage(){

    }

    // Update Image
    public Image updateImage(){

    }

    // Image Exists
    public boolean imageExists(){

    }

}
