package com.example.crud_documentale.service;

import com.example.crud_documentale.models.Documento;
import com.example.crud_documentale.repositories.DocumentoRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentoRepository documentoRepository;

    public DocumentService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public List<Documento> cercaPerTitolo(String titolo) {
        return documentoRepository.findByTitoloContainingIgnoreCase(titolo);
    }


    @Transactional
    public Documento salvaDocumento(Documento documento,byte[] file) throws Exception {
        Documento salva = documentoRepository.save(documento);

        String fileName = documento.getTitolo();
        String estensione = fileName.substring(fileName.lastIndexOf("."));

        try{
            Path filePath = Paths.get("C:/fileSystem",salva.getId()+estensione);
            Files.write(filePath, file);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante l'inserimento del documento",e);
        }
        return salva;
    }


    public ResponseEntity<Resource> downloadDocumento(Long id) {
        Documento documento = documentoRepository.findById(id).orElse(null);
        if (documento == null) {
            return ResponseEntity.notFound().build();
        }

        String titolo = documento.getTitolo();
        String estensione = titolo.substring(titolo.lastIndexOf("."));
        Path filePath = Paths.get("C:/fileSystem/" + documento.getId() + estensione);

        try {
            byte[] fileData = Files.readAllBytes(filePath);
            ByteArrayResource resource = new ByteArrayResource(fileData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + titolo + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileData.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
