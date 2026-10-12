package fr.miage.gps.io;

import fr.miage.gps.algo.Dijkstra;
import fr.miage.gps.algo.ResultatTrajet;
import fr.miage.gps.model.Graphe;
import fr.miage.gps.model.Ville;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvLoaderTest {

    @Test
    @DisplayName("Chargement réussi du fichier routes.csv officiel")
    void testChargementFichierOfficiel() throws IOException {
        Path path = Path.of("data/routes.csv");
        assertTrue(Files.exists(path), "Le fichier data/routes.csv doit exister");

        Graphe graphe = CsvLoader.chargerGraphe(path, true);

        assertNotNull(graphe);
        assertTrue(graphe.getNombreVilles() >= 8, "Le graphe doit contenir au moins 8 villes");
        assertTrue(graphe.contientVille(new Ville("Toulouse")));
        assertTrue(graphe.contientVille(new Ville("Paris")));

        // Calcul d'un itinéraire sur les données réelles du fichier
        ResultatTrajet trajet = Dijkstra.trouverPlusCourtChemin(graphe, "Toulouse", "Paris");
        assertTrue(trajet.isCheminTrouve());
        assertTrue(trajet.getDistanceTotale() <= 680.0);
    }

    @Test
    @DisplayName("Ignorer les commentaires et lignes vides dans le CSV")
    void testFichierAvecCommentairesEtLignesVides(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("test-routes.csv");
        String contenu = """
                # Ceci est un commentaire
                
                Lille,Paris,220
                # Autre commentaire
                Paris,Lyon,460
                
                """;
        Files.writeString(tempFile, contenu);

        Graphe graphe = CsvLoader.chargerGraphe(tempFile, true);

        assertEquals(3, graphe.getNombreVilles()); // Lille, Paris, Lyon
        assertEquals(4, graphe.getNombreRoutes()); // 2 routes bidirectionnelles = 4 arcs
    }

    @Test
    @DisplayName("Fichier inexistant lève une IOException")
    void testFichierInexistant() {
        assertThrows(IOException.class, () -> {
            CsvLoader.chargerGraphe("fichier_qui_n_existe_pas.csv");
        });
    }
}
