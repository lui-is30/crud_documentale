package com.example.crud_documentale.repositories;

import com.example.crud_documentale.models.Documento;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
}
