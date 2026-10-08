package fr.miage.gps.algo;

import fr.miage.gps.model.Graphe;
import fr.miage.gps.model.Route;
import fr.miage.gps.model.SommetDistance;
import fr.miage.gps.model.Ville;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Implémentation de l'algorithme de Dijkstra avec PriorityQueue.
 * Permet de déterminer le plus court chemin entre deux villes dans un graphe pondéré positif.
 */
public class Dijkstra {

    private Dijkstra() {
        // Classe utilitaire, constructeur privé
    }

    /**
     * Recherche le plus court chemin entre deux villes identifiées par leur nom.
     */
    public static ResultatTrajet trouverPlusCourtChemin(Graphe graphe, String nomDepart, String nomArrivee) {
        if (graphe == null || nomDepart == null || nomArrivee == null) {
            throw new IllegalArgumentException("Le graphe et les noms de villes ne peuvent pas être nuls.");
        }
        Ville depart = graphe.getVilleParNom(nomDepart);
        Ville arrivee = graphe.getVilleParNom(nomArrivee);

        if (depart == null || arrivee == null) {
            Ville dummyDepart = depart != null ? depart : new Ville(nomDepart);
            Ville dummyArrivee = arrivee != null ? arrivee : new Ville(nomArrivee);
            return ResultatTrajet.introuvable(dummyDepart, dummyArrivee);
        }

        return trouverPlusCourtChemin(graphe, depart, arrivee);
    }

    /**
     * Calcule le plus court chemin entre une ville de départ et une ville d'arrivée.
     */
    public static ResultatTrajet trouverPlusCourtChemin(Graphe graphe, Ville depart, Ville arrivee) {
        if (graphe == null || depart == null || arrivee == null) {
            throw new IllegalArgumentException("Les paramètres ne peuvent pas être nuls.");
        }

        if (!graphe.contientVille(depart) || !graphe.contientVille(arrivee)) {
            return ResultatTrajet.introuvable(depart, arrivee);
        }

        // Cas particulier : départ = arrivée
        if (depart.equals(arrivee)) {
            return ResultatTrajet.succes(depart, arrivee, List.of(depart), 0.0);
        }

        // 1. Structures de données pour Dijkstra
        Map<Ville, Double> distances = new HashMap<>();
        Map<Ville, Ville> predecesseurs = new HashMap<>();
        Set<Ville> visites = new HashSet<>();
        PriorityQueue<SommetDistance> filePriorite = new PriorityQueue<>();

        // 2. Initialisation : départ à distance 0
        distances.put(depart, 0.0);
        filePriorite.add(new SommetDistance(depart, 0.0));

        // 3. Boucle principale de l'algorithme
        while (!filePriorite.isEmpty()) {
            SommetDistance sommetCourant = filePriorite.poll();
            Ville villeCourante = sommetCourant.getVille();

            // Si déjà traitée, on passe
            if (visites.contains(villeCourante)) {
                continue;
            }
            visites.add(villeCourante);

            // Optimisation : si la destination est atteinte et extraite, le chemin minimal est garanti
            if (villeCourante.equals(arrivee)) {
                break;
            }

            // Examen des routes voisines
            for (Route route : graphe.getRoutesVoisines(villeCourante)) {
                Ville voisin = route.getDestination();

                if (visites.contains(voisin)) {
                    continue;
                }

                double distanceActuelleVoisin = distances.getOrDefault(voisin, Double.POSITIVE_INFINITY);
                double nouvelleDistance = distances.get(villeCourante) + route.getDistance();

                // Relâchement (mise à jour si meilleur chemin trouvé)
                if (nouvelleDistance < distanceActuelleVoisin) {
                    distances.put(voisin, nouvelleDistance);
                    predecesseurs.put(voisin, villeCourante);
                    filePriorite.add(new SommetDistance(voisin, nouvelleDistance));
                }
            }
        }

        // 4. Si la ville d'arrivée n'a pas été atteinte
        if (!distances.containsKey(arrivee)) {
            return ResultatTrajet.introuvable(depart, arrivee);
        }

        // 5. Reconstruction du chemin à rebours (de l'arrivée vers le départ)
        List<Ville> chemin = new ArrayList<>();
        Ville curseur = arrivee;
        while (curseur != null) {
            chemin.add(curseur);
            curseur = predecesseurs.get(curseur);
        }

        Collections.reverse(chemin);

        // Vérification de cohérence du chemin reconstruit
        if (chemin.isEmpty() || !chemin.get(0).equals(depart)) {
            return ResultatTrajet.introuvable(depart, arrivee);
        }

        return ResultatTrajet.succes(depart, arrivee, chemin, distances.get(arrivee));
    }
}
