package fr.miage.gps.model;

import java.util.Objects;

/**
 * Représente une arête pondérée (edge) dans le graphe.
 * Relie une ville source à une ville destination avec une distance en kilomètres.
 */
public class Route {

    private final Ville source;
    private final Ville destination;
    private final double distance;

    public Route(Ville source, Ville destination, double distance) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("La source et la destination ne peuvent pas être nulles.");
        }
        if (distance < 0) {
            throw new IllegalArgumentException("La distance d'une route ne peut pas être négative.");
        }
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }

    public Ville getSource() {
        return source;
    }

    public Ville getDestination() {
        return destination;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Double.compare(route.distance, distance) == 0 &&
               Objects.equals(source, route.source) &&
               Objects.equals(destination, route.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination, distance);
    }

    @Override
    public String toString() {
        return source + " -> " + destination + " (" + distance + " km)";
    }
}
