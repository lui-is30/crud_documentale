package com.example.crud_documentale.service;

import com.example.crud_documentale.models.Documento;
import com.example.crud_documentale.repositories.DocumentoRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
public class DocumentService {

    private static final String DIRECTORY = "C:/fileSystem/";

    private final DocumentoRepository documentoRepository;

    public DocumentService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Documento salvaDocumento(Documento documento, byte[] fileBytes) throws IOException {
        Documento salvato = documentoRepository.save(documento);
        String titolo = documento.getTitolo();
        String estensione = titolo.substring(titolo.lastIndexOf("."));

        Path directoryPath = Paths.get(DIRECTORY);
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        Path filePath = directoryPath.resolve(salvato.getId() + estensione);
        Files.write(filePath, fileBytes, StandardOpenOption.CREATE_NEW);

        return salvato;
    }

    public List<Documento> cercaPerTitolo(String titolo) {
        return documentoRepository.findByTitoloContainingIgnoreCase(titolo);
    }

    public ResponseEntity<Resource> downloadDocumento(Long id) {
        Documento documento = documentoRepository.findById(id).orElse(null);
        if (documento == null) {
            return ResponseEntity.notFound().build();
        }

        String titolo = documento.getTitolo();
        String estensione = titolo.substring(titolo.lastIndexOf("."));
        Path filePath = Paths.get(DIRECTORY + documento.getId() + estensione);

        try {
            byte[] fileData = Files.readAllBytes(filePath);
            ByteArrayResource resource = new ByteArrayResource(fileData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + titolo + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileData.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

