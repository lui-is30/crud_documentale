package com.example.crud_documentale.service;

import com.example.crud_documentale.models.Documento;
import com.example.crud_documentale.repositories.DocumentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DocumentService {

    private final DocumentoRepository documentoRepository;

    public DocumentService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
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

}
