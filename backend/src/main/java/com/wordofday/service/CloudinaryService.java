package com.wordofday.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder}")
    private String folder;

    /**
     * Upload image bytes to Cloudinary
     * @param imageBytes The image data
     * @param fileName Public ID for the image (without extension)
     * @return The public URL of the uploaded image
     */
    public String uploadImage(byte[] imageBytes, String fileName) throws IOException {
        try {
            log.info("Uploading image to Cloudinary: {}", fileName);

            Map<?, ?> uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                    "public_id", folder + "/" + fileName,
                    "folder", folder,
                    "resource_type", "image"
            ));

            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("Image uploaded successfully: {}", secureUrl);

            return secureUrl;

        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete image from Cloudinary
     * @param publicId The public ID of the image (e.g., "lithuanian-words/word_123")
     */
    public void deleteImage(String publicId) {
        try {
            log.info("Deleting image from Cloudinary: {}", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted successfully");
        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: {}", e.getMessage(), e);
        }
    }

    /**
     * Extract public ID from Cloudinary URL
     * @param imageUrl Full Cloudinary URL
     * @return Public ID (e.g., "lithuanian-words/word_123")
     */
    public String extractPublicId(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return null;
        }

        // Extract public ID from URL like:
        // https://res.cloudinary.com/demo/image/upload/v1234567890/lithuanian-words/word_123.jpg
        String[] parts = imageUrl.split("/upload/");
        if (parts.length < 2) return null;

        String afterUpload = parts[1];
        // Remove version number (v1234567890/)
        afterUpload = afterUpload.replaceFirst("v\\d+/", "");
        // Remove file extension
        afterUpload = afterUpload.replaceFirst("\\.[^.]+$", "");

        return afterUpload;
    }
}