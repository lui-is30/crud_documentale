package com.example.crud_documentale.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crud_documentale.models.Documento;
import com.example.crud_documentale.repositories.DocumentoRepository;

@RestController
@RequestMapping("/api/documenti")
@CrossOrigin(origins = "http://localhost:3000") // ⬅️ necessario per React
public class DocumentoController {

    private final DocumentoRepository documentoRepository;

    public DocumentoController(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }
    
    @GetMapping
    public List<Documento> getAll() {
        return documentoRepository.findAll();
    }
}