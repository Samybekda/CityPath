package fr.miage.gps.algo;

import fr.miage.gps.model.Graphe;
import fr.miage.gps.model.Ville;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {

    private Graphe graphe;
    private Ville toulouse;
    private Ville bordeaux;
    private Ville paris;
    private Ville lyon;
    private Ville marseille;
    private Ville corse;

    @BeforeEach
    void setUp() {
        graphe = new Graphe();
        toulouse = new Ville("Toulouse");
        bordeaux = new Ville("Bordeaux");
        paris = new Ville("Paris");
        lyon = new Ville("Lyon");
        marseille = new Ville("Marseille");
        corse = new Ville("Ajaccio"); // Ville isolée

        // Réseau de test :
        // Toulouse - Paris direct : 700 km
        // Toulouse - Bordeaux : 250 km
        // Bordeaux - Paris : 350 km  => via Bordeaux : 600 km (< 700 km direct !)
        // Toulouse - Lyon : 540 km
        // Lyon - Paris : 460 km
        // Marseille - Lyon : 315 km
        graphe.ajouterRoute(toulouse, paris, 700);
        graphe.ajouterRoute(toulouse, bordeaux, 250);
        graphe.ajouterRoute(bordeaux, paris, 350);
        graphe.ajouterRoute(toulouse, lyon, 540);
        graphe.ajouterRoute(lyon, paris, 460);
        graphe.ajouterRoute(marseille, lyon, 315);

        // Ajout de la ville isolée
        graphe.ajouterVille(corse);
    }

    @Test
    @DisplayName("Dijkstra choisit le chemin indirect quand il est plus court que le chemin direct")
    void testCheminIndirectPlusCourt() {
        // Direct = 700 km, mais via Bordeaux = 250 + 350 = 600 km
        ResultatTrajet resultat = Dijkstra.trouverPlusCourtChemin(graphe, toulouse, paris);

        assertTrue(resultat.isCheminTrouve());
        assertEquals(600.0, resultat.getDistanceTotale());
        assertEquals(List.of(toulouse, bordeaux, paris), resultat.getEtapes());
    }

    @Test
    @DisplayName("Cas où le départ est identique à l'arrivée (distance = 0)")
    void testDepartEgalArrivee() {
        ResultatTrajet resultat = Dijkstra.trouverPlusCourtChemin(graphe, toulouse, toulouse);

        assertTrue(resultat.isCheminTrouve());
        assertEquals(0.0, resultat.getDistanceTotale());
        assertEquals(List.of(toulouse), resultat.getEtapes());
    }

    @Test
    @DisplayName("Aucun chemin possible vers une ville isolée")
    void testVilleIsoleeNonAtteignable() {
        ResultatTrajet resultat = Dijkstra.trouverPlusCourtChemin(graphe, toulouse, corse);

        assertFalse(resultat.isCheminTrouve());
        assertEquals(Double.POSITIVE_INFINITY, resultat.getDistanceTotale());
        assertTrue(resultat.getEtapes().isEmpty());
    }

    @Test
    @DisplayName("Recherche avec des noms de ville sous forme de String")
    void testRechercheParNomString() {
        ResultatTrajet resultat = Dijkstra.trouverPlusCourtChemin(graphe, "Toulouse", "Paris");

        assertTrue(resultat.isCheminTrouve());
        assertEquals(600.0, resultat.getDistanceTotale());
        assertEquals("Toulouse -> Bordeaux -> Paris (600.0 km)", resultat.toString());
    }

    @Test
    @DisplayName("Ville inexistante dans le graphe")
    void testVilleInexistante() {
        ResultatTrajet resultat = Dijkstra.trouverPlusCourtChemin(graphe, "Toulouse", "Inconnue");

        assertFalse(resultat.isCheminTrouve());
    }

    @Test
    @DisplayName("Chemin multi-étapes optimal (Marseille -> Paris via Lyon)")
    void testMultiEtapesMarseilleParis() {
        // Marseille -> Lyon (315) -> Paris (460) = 775 km
        ResultatTrajet resultat = Dijkstra.trouverPlusCourtChemin(graphe, marseille, paris);

        assertTrue(resultat.isCheminTrouve());
        assertEquals(775.0, resultat.getDistanceTotale());
        assertEquals(List.of(marseille, lyon, paris), resultat.getEtapes());
    }
}
