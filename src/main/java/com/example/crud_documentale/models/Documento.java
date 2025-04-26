package com.example.crud_documentale.models;

import jakarta.persistence.*;

@Entity
@Table(name = "documenti")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titolo;
    private String autore;
    private Long dimensione;

    // Costruttori
    public Documento() {
    }

    public Documento(String titolo, String autore,Long dimensione) {
        this.titolo = titolo;
        this.autore = autore;
        this.dimensione = dimensione;
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public Long getDimensione(){
        return dimensione;
    }

    public void setDimensione(Long dimensione){
        this.dimensione = dimensione;
    }
}

