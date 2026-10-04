package fr.miage.gps.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Représente le réseau routier sous forme de graphe.
 * Utilise une liste d'adjacence : chaque ville est associée à la liste des routes qui en partent.
 */
public class Graphe {

    // Liste d'adjacence : clé = ville de départ, valeur = routes sortantes
    private final Map<Ville, List<Route>> adjacence;

    public Graphe() {
        this.adjacence = new HashMap<>();
    }

    /**
     * Ajoute une ville dans le graphe si elle n'est pas déjà présente.
     */
    public void ajouterVille(Ville ville) {
        if (ville == null) {
            throw new IllegalArgumentException("La ville ne peut pas être nulle.");
        }
        adjacence.putIfAbsent(ville, new ArrayList<>());
    }

    /**
     * Ajoute une route orientée (sens unique de source vers destination).
     */
    public void ajouterRoute(Route route) {
        if (route == null) {
            throw new IllegalArgumentException("La route ne peut pas être nulle.");
        }
        ajouterVille(route.getSource());
        ajouterVille(route.getDestination());
        adjacence.get(route.getSource()).add(route);
    }

    /**
     * Ajoute une route entre deux villes.
     * Par défaut pour une carte routière, la route est créée dans les deux sens (bidirectionnelle).
     */
    public void ajouterRoute(Ville source, Ville destination, double distance) {
        ajouterRoute(source, destination, distance, true);
    }

    /**
     * Ajoute une route en précisant si elle est à double sens ou à sens unique.
     */
    public void ajouterRoute(Ville source, Ville destination, double distance, boolean bidirectionnel) {
        ajouterRoute(new Route(source, destination, distance));
        if (bidirectionnel) {
            ajouterRoute(new Route(destination, source, distance));
        }
    }

    /**
     * Retourne la liste des routes qui partent d'une ville donnée.
     * Si la ville n'est pas dans le graphe, retourne une liste vide.
     */
    public List<Route> getRoutesVoisines(Ville ville) {
        return Collections.unmodifiableList(adjacence.getOrDefault(ville, Collections.emptyList()));
    }

    /**
     * Retourne l'ensemble de toutes les villes présentes dans le graphe.
     */
    public Set<Ville> getVilles() {
        return Collections.unmodifiableSet(adjacence.keySet());
    }

    /**
     * Recherche une ville par son nom (insensible à la casse).
     * Retourne la ville trouvée ou null si absente.
     */
    public Ville getVilleParNom(String nom) {
        if (nom == null) return null;
        for (Ville v : adjacence.keySet()) {
            if (v.getNom().equalsIgnoreCase(nom.trim())) {
                return v;
            }
        }
        return null;
    }

    /**
     * Vérifie si une ville existe dans le graphe.
     */
    public boolean contientVille(Ville ville) {
        return adjacence.containsKey(ville);
    }

    /**
     * Retourne le nombre total de sommets (villes).
     */
    public int getNombreVilles() {
        return adjacence.size();
    }

    /**
     * Retourne le nombre total d'arêtes (routes orientées).
     */
    public int getNombreRoutes() {
        int total = 0;
        for (List<Route> routes : adjacence.values()) {
            total += routes.size();
        }
        return total;
    }
}
