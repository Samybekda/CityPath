package fr.miage.gps.algo;

import fr.miage.gps.model.Ville;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Représente le résultat du calcul d'un plus court chemin.
 */
public class ResultatTrajet {

    private final Ville depart;
    private final Ville arrivee;
    private final List<Ville> etapes;
    private final double distanceTotale;
    private final boolean cheminTrouve;

    private ResultatTrajet(Ville depart, Ville arrivee, List<Ville> etapes, double distanceTotale, boolean cheminTrouve) {
        this.depart = depart;
        this.arrivee = arrivee;
        this.etapes = etapes != null ? Collections.unmodifiableList(etapes) : Collections.emptyList();
        this.distanceTotale = distanceTotale;
        this.cheminTrouve = cheminTrouve;
    }

    public static ResultatTrajet succes(Ville depart, Ville arrivee, List<Ville> etapes, double distanceTotale) {
        return new ResultatTrajet(depart, arrivee, etapes, distanceTotale, true);
    }

    public static ResultatTrajet introuvable(Ville depart, Ville arrivee) {
        return new ResultatTrajet(depart, arrivee, Collections.emptyList(), Double.POSITIVE_INFINITY, false);
    }

    public Ville getDepart() {
        return depart;
    }

    public Ville getArrivee() {
        return arrivee;
    }

    public List<Ville> getEtapes() {
        return etapes;
    }

    public double getDistanceTotale() {
        return distanceTotale;
    }

    public boolean isCheminTrouve() {
        return cheminTrouve;
    }

    public int getNombreEtapes() {
        return etapes.size();
    }

    @Override
    public String toString() {
        if (!cheminTrouve) {
            return "Aucun chemin trouvé entre " + depart + " et " + arrivee + ".";
        }
        String chemin = etapes.stream()
                .map(Ville::getNom)
                .collect(Collectors.joining(" -> "));
        return chemin + " (" + distanceTotale + " km)";
    }
}
