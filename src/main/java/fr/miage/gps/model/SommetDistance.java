package fr.miage.gps.model;

/**
 * Prépare l'utilisation de la PriorityQueue pour Dijkstra.
 * 
 * Cette classe associe une ville à une distance provisoire depuis le départ.
 * Elle implémente Comparable afin que la PriorityQueue puisse extraire en priorité
 * le sommet ayant la plus petite distance (distance minimale).
 */
public class SommetDistance implements Comparable<SommetDistance> {

    private final Ville ville;
    private final double distance;

    public SommetDistance(Ville ville, double distance) {
        if (ville == null) {
            throw new IllegalArgumentException("La ville ne peut pas être nulle.");
        }
        this.ville = ville;
        this.distance = distance;
    }

    public Ville getVille() {
        return ville;
    }

    public double getDistance() {
        return distance;
    }

    /**
     * Permet à la PriorityQueue de trier automatiquement par distance croissante.
     */
    @Override
    public int compareTo(SommetDistance autre) {
        return Double.compare(this.distance, autre.distance);
    }

    @Override
    public String toString() {
        return ville.getNom() + " (dist: " + distance + " km)";
    }
}
