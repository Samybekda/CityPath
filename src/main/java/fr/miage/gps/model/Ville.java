package fr.miage.gps.model;

import java.util.Objects;

/**
 * Représente un sommet (vertex) dans le graphe routier.
 * Une ville est identifiée par son nom.
 */
public class Ville {

    private final String nom;

    public Ville(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la ville ne peut pas être vide.");
        }
        this.nom = nom.trim();
    }

    public String getNom() {
        return nom;
    }

    /**
     * Deux villes sont considérées identiques si elles ont le même nom.
     * C'est indispensable pour que HashMap et HashSet fonctionnent correctement
     * avec nos objets Ville.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ville ville = (Ville) o;
        return nom.equalsIgnoreCase(ville.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom.toLowerCase());
    }

    @Override
    public String toString() {
        return nom;
    }
}
