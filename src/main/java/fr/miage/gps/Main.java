package fr.miage.gps;

import fr.miage.gps.model.Graphe;
import fr.miage.gps.model.Route;
import fr.miage.gps.model.SommetDistance;
import fr.miage.gps.model.Ville;

import java.util.PriorityQueue;

/**
 * Étape 1 : Démonstration du modèle de graphe routier (L3 MIAGE).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=============================================================");
        System.out.println("      📍 CityPath - Étape 1 : Modèle de Graphe (L3 MIAGE)");
        System.out.println("=============================================================");

        Graphe graphe = new Graphe();

        Ville toulouse = new Ville("Toulouse");
        Ville bordeaux = new Ville("Bordeaux");
        Ville paris = new Ville("Paris");

        graphe.ajouterRoute(toulouse, paris, 680);
        graphe.ajouterRoute(toulouse, bordeaux, 250);
        graphe.ajouterRoute(bordeaux, paris, 590);

        System.out.println("Nombre de villes : " + graphe.getNombreVilles());
        System.out.println("Nombre total de routes orientées : " + graphe.getNombreRoutes());

        System.out.println("\n--- Voisins de chaque ville ---");
        for (Ville ville : graphe.getVilles()) {
            System.out.println("Voisins de " + ville + " :");
            for (Route route : graphe.getRoutesVoisines(ville)) {
                System.out.println("  -> " + route.getDestination() + " (" + route.getDistance() + " km)");
            }
        }

        System.out.println("\n--- Préparation de la PriorityQueue pour Dijkstra ---");
        PriorityQueue<SommetDistance> filePriorite = new PriorityQueue<>();
        filePriorite.add(new SommetDistance(paris, 680));
        filePriorite.add(new SommetDistance(bordeaux, 250));

        SommetDistance plusProche = filePriorite.poll();
        System.out.println("Premier sommet extrait (distance minimale attendue : Bordeaux) :");
        System.out.println("  -> " + plusProche);
    }
}
