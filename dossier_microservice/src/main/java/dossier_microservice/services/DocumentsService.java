package dossier_microservice.services;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
public class DocumentsService {

    public void createDocuments(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }
}
