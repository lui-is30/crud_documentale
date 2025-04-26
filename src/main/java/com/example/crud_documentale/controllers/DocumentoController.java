package com.example.crud_documentale.controllers;

import java.util.List;

import com.example.crud_documentale.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}