package com.example.crud_documentale.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.example.crud_documentale.service.DocumentService;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.example.crud_documentale.models.Documento;
import com.example.crud_documentale.repositories.DocumentoRepository;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documenti")
@CrossOrigin(origins = "http://localhost:3000") // ⬅️ necessario per React
public class DocumentoController {

    private final DocumentoRepository documentoRepository;

    private final DocumentService documentService;

    public DocumentoController(DocumentService documentService,DocumentoRepository documentoRepository) {
        this.documentService = documentService;
        this.documentoRepository = documentoRepository;
    }
    
    @GetMapping
    public List<Documento> getAll() {
        return documentoRepository.findAll();
    }

    @PostMapping("/salva")
    public ResponseEntity<Documento> inserisciDocumento(@RequestParam("autore") String autore,@RequestParam("file")
    MultipartFile file) {
        try{
            Documento documento = new Documento(file.getOriginalFilename(),autore,file.getSize());
            Documento salva = documentService.salvaDocumento(documento, file.getBytes());
            return ResponseEntity.ok(salva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocumento(@PathVariable Long id) {
        return documentService.downloadDocumento(id);
    }
}