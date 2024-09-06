package com.movie.movieApi.service;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class FileServiceImpl implements FileService{

    // Injection du chemin du répertoire des images à partir du fichier application.properties
    @Value("${movie.images.directory}")
    private String imageDirectory;

    // Chemin où les fichiers seront stockés
    private Path filePath;

    // Méthode exécutée après l'injection des dépendances pour initialiser le chemin
    @PostConstruct
    public void init() {
        // Initialisation du chemin avec le répertoire des images injecté
        filePath = Paths.get(imageDirectory);

        // Création du répertoire s'il n'existe pas
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath);
                System.out.println("Répertoire créé avec succès : " + imageDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la création du répertoire des images : " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile imageFile) throws IOException {
        // Vérification si le fichier est vide
        if (imageFile.isEmpty()) {
            throw new IOException("Le fichier est vide.");
        }

        // On récupère le nom du fichier
        String fileName = imageFile.getOriginalFilename();

        // Sauvegarde de l'image
        Files.copy(imageFile.getInputStream(), filePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    @Override
    public InputStream getResourceFile(String imageName) throws FileNotFoundException {
        String imagePath = getImagePath(imageName);
        return new FileInputStream(imagePath);
    }

    // Méthode pour obtenir le chemin complet d'une image
    public String getImagePath(String imageName) {
        return filePath.resolve(imageName).toString();
    }
}
