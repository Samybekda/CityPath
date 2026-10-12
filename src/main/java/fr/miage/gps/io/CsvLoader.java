package fr.miage.gps.io;

import fr.miage.gps.model.Graphe;
import fr.miage.gps.model.Ville;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Permet de charger un graphe routier à partir d'un fichier texte ou CSV.
 * Format attendu par ligne :
 * VilleDepart,VilleArrivee,Distance
 * Exemple : Toulouse,Paris,680
 */
public class CsvLoader {

    private CsvLoader() {
        // Classe utilitaire
    }

    /**
     * Charge les routes depuis un chemin de fichier et retourne un Graphe peuplé.
     */
    public static Graphe chargerGraphe(String cheminFichier) throws IOException {
        return chargerGraphe(Paths.get(cheminFichier), true);
    }

    /**
     * Charge les routes en spécifiant si elles sont bidirectionnelles ou non.
     */
    public static Graphe chargerGraphe(Path cheminFichier, boolean bidirectionnel) throws IOException {
        if (cheminFichier == null || !Files.exists(cheminFichier)) {
            throw new IOException("Fichier introuvable : " + cheminFichier);
        }

        Graphe graphe = new Graphe();

        try (BufferedReader reader = Files.newBufferedReader(cheminFichier, StandardCharsets.UTF_8)) {
            String ligne;
            int numeroLigne = 0;

            while ((ligne = reader.readLine()) != null) {
                numeroLigne++;
                String ligneNettoyee = ligne.trim();

                // Ignorer les lignes vides et les commentaires commençant par '#'
                if (ligneNettoyee.isEmpty() || ligneNettoyee.startsWith("#")) {
                    continue;
                }

                // Supporte la virgule ou le point-virgule comme séparateur
                String[] elements = ligneNettoyee.split("[,;]");
                if (elements.length < 3) {
                    System.err.println("Ligne " + numeroLigne + " ignorée (format invalide) : " + ligne);
                    continue;
                }

                String nomSource = elements[0].trim();
                String nomDestination = elements[1].trim();

                try {
                    double distance = Double.parseDouble(elements[2].trim());
                    Ville source = new Ville(nomSource);
                    Ville destination = new Ville(nomDestination);

                    graphe.ajouterRoute(source, destination, distance, bidirectionnel);
                } catch (NumberFormatException e) {
                    System.err.println("Ligne " + numeroLigne + " ignorée (distance invalide) : " + ligne);
                }
            }
        }

        return graphe;
    }
}
