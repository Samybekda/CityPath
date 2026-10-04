package fr.miage.gps.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class GrapheTest {

    private Graphe graphe;
    private Ville toulouse;
    private Ville bordeaux;
    private Ville paris;

    @BeforeEach
    void setUp() {
        graphe = new Graphe();
        toulouse = new Ville("Toulouse");
        bordeaux = new Ville("Bordeaux");
        paris = new Ville("Paris");
    }

    @Test
    @DisplayName("Deux objets Ville avec le même nom doivent être égaux (equals & hashCode)")
    void testEgaliteVilles() {
        Ville v1 = new Ville("Paris");
        Ville v2 = new Ville("paris");

        assertEquals(v1, v2, "Deux villes de même nom doivent être considérées égales");
        assertEquals(v1.hashCode(), v2.hashCode(), "Leurs hashCode doivent être identiques");
    }

    @Test
    @DisplayName("Ajout d'une ville dans le graphe")
    void testAjouterVille() {
        graphe.ajouterVille(toulouse);

        assertTrue(graphe.contientVille(toulouse));
        assertEquals(1, graphe.getNombreVilles());
        assertEquals(0, graphe.getRoutesVoisines(toulouse).size());
    }

    @Test
    @DisplayName("Ajout d'une route bidirectionnelle (double sens)")
    void testAjouterRouteBidirectionnelle() {
        graphe.ajouterRoute(toulouse, bordeaux, 250);

        assertEquals(2, graphe.getNombreVilles());
        assertEquals(2, graphe.getNombreRoutes()); // 1 aller + 1 retour

        List<Route> routesToulouse = graphe.getRoutesVoisines(toulouse);
        assertEquals(1, routesToulouse.size());
        assertEquals(bordeaux, routesToulouse.get(0).getDestination());
        assertEquals(250.0, routesToulouse.get(0).getDistance());

        List<Route> routesBordeaux = graphe.getRoutesVoisines(bordeaux);
        assertEquals(1, routesBordeaux.size());
        assertEquals(toulouse, routesBordeaux.get(0).getDestination());
        assertEquals(250.0, routesBordeaux.get(0).getDistance());
    }

    @Test
    @DisplayName("Ajout d'une route à sens unique")
    void testAjouterRouteSensUnique() {
        graphe.ajouterRoute(toulouse, paris, 680, false);

        assertEquals(1, graphe.getRoutesVoisines(toulouse).size());
        assertEquals(0, graphe.getRoutesVoisines(paris).size());
    }

    @Test
    @DisplayName("Recherche d'une ville par son nom")
    void testGetVilleParNom() {
        graphe.ajouterVille(toulouse);

        Ville trouvee = graphe.getVilleParNom("toulouse");
        assertNotNull(trouvee);
        assertEquals("Toulouse", trouvee.getNom());

        assertNull(graphe.getVilleParNom("Lyon"));
    }

    @Test
    @DisplayName("Vérification du fonctionnement de la PriorityQueue avec SommetDistance")
    void testPriorityQueueSommetDistance() {
        PriorityQueue<SommetDistance> queue = new PriorityQueue<>();

        queue.add(new SommetDistance(paris, 680));
        queue.add(new SommetDistance(toulouse, 100));
        queue.add(new SommetDistance(bordeaux, 250));

        // L'élément avec la plus petite distance doit sortir en premier
        SommetDistance premier = queue.poll();
        assertNotNull(premier);
        assertEquals(toulouse, premier.getVille());
        assertEquals(100.0, premier.getDistance());

        SommetDistance deuxieme = queue.poll();
        assertNotNull(deuxieme);
        assertEquals(bordeaux, deuxieme.getVille());
        assertEquals(250.0, deuxieme.getDistance());

        SommetDistance troisieme = queue.poll();
        assertNotNull(troisieme);
        assertEquals(paris, troisieme.getVille());
        assertEquals(680.0, troisieme.getDistance());
    }
}
