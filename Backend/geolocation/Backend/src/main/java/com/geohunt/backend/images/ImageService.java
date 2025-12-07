package com.geohunt.backend.images;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ImageService {

    // TODO: replace this before pushing to main. refers to the base directory where images are stored.
    private static String directory = "C:/Users/evan/2_jubair_5"; // local - evan

    @Autowired
    ImageRepository imageRepository;

    // Save Image
    public Image saveImage(MultipartFile imageFile, String folder){
        try{
            Image image = new Image();

            File destinationFile = new File(directory + File.separator + folder + File.separator + imageFile.getOriginalFilename()); // change filename to image id?
            if(destinationFile.exists()){
                // File already exists at a given location. throw exception?
                return null;
            }

            imageFile.transferTo(destinationFile);

            image.setAbsolutePath(destinationFile.getAbsolutePath());
            image.setLocalPath(folder + File.separator + imageFile.getOriginalFilename());
            imageRepository.save(image);

            return image;
        }
        catch (IOException e){
            return null;
        }
    }

    // Get Image Object
    public Image getImageObj(long id){
        if(imageRepository.findById(id).isPresent()){
            return imageRepository.findById(id).get();
        }
        else {
            return null;
        }
    }

    // Get Image byte[]
    public byte[] getImageBytes(long id) throws IOException{
        Image image = getImageObj(id);
        if(image == null){
            return null;
        }
        File imageFile = new File(image.getAbsolutePath());
        return Files.readAllBytes(imageFile.toPath());
    }

    // Delete Image
    public boolean deleteImage(long id){
        Image image = getImageObj(id);
        if(image == null){
            return false;
        }

        File imageFile = new File(image.getAbsolutePath());

        if(imageFile.delete()){
            imageRepository.delete(image);
            return true;
        }
        else{
            return false;
        }
    }

}
