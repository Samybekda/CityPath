package fr.miage.gps;

import fr.miage.gps.algo.Dijkstra;
import fr.miage.gps.algo.ResultatTrajet;
import fr.miage.gps.io.CsvLoader;
import fr.miage.gps.model.Graphe;
import fr.miage.gps.model.Route;
import fr.miage.gps.model.Ville;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Application Console (Version 1) : Calculateur d'itinéraire GPS (L3 MIAGE).
 */
public class Main {

    public static void main(String[] args) {
        afficherBanniere();

        // 1. Chargement des données CSV
        Graphe graphe = chargerReseauRoutier();

        // 2. Affichage des villes disponibles
        afficherVillesDisponibles(graphe);

        // 3. Boucle interactive de recherche d'itinéraire
        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;

        System.out.println("Tapez 'quitter' ou 'exit' à tout moment pour sortir du programme.\n");

        while (continuer) {
            try {
                System.out.print("Ville de départ : ");
                if (!scanner.hasNextLine()) {
                    // Mode non-interactif / fin de flux : démonstration automatique
                    lancerDemonstrationAuto(graphe);
                    break;
                }
                String saisieDepart = scanner.nextLine().trim();
                if (saisieDepart.equalsIgnoreCase("quitter") || saisieDepart.equalsIgnoreCase("exit")) {
                    break;
                }
                if (saisieDepart.isEmpty()) {
                    continue;
                }

                Ville villeDepart = graphe.getVilleParNom(saisieDepart);
                if (villeDepart == null) {
                    System.out.println("❌ Ville introuvable : \"" + saisieDepart + "\". Veuillez choisir une ville de la liste ci-dessus.\n");
                    continue;
                }

                System.out.print("Ville d'arrivée : ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String saisieArrivee = scanner.nextLine().trim();
                if (saisieArrivee.equalsIgnoreCase("quitter") || saisieArrivee.equalsIgnoreCase("exit")) {
                    break;
                }
                if (saisieArrivee.isEmpty()) {
                    continue;
                }

                Ville villeArrivee = graphe.getVilleParNom(saisieArrivee);
                if (villeArrivee == null) {
                    System.out.println("❌ Ville introuvable : \"" + saisieArrivee + "\".\n");
                    continue;
                }

                // 4. Calcul avec Dijkstra
                calculerEtAfficherTrajet(graphe, villeDepart, villeArrivee);

            } catch (NoSuchElementException e) {
                break;
            }
        }

        System.out.println("\nMerci d'avoir utilisé le GPS Itinéraire MIAGE. À bientôt !");
    }

    private static void afficherBanniere() {
        System.out.println("=============================================================");
        System.out.println("        📍 CALCULATEUR D'ITINÉRAIRE GPS - L3 MIAGE");
        System.out.println("      Moteur de plus court chemin (Algorithme de Dijkstra)");
        System.out.println("=============================================================");
    }

    private static Graphe chargerReseauRoutier() {
        // Recherche du fichier routes.csv à la racine du projet ou dans data/
        Path[] cheminsPotentiels = new Path[]{
                Paths.get("data/routes.csv"),
                Paths.get("itineraire-gps/data/routes.csv"),
                Paths.get("../data/routes.csv")
        };

        for (Path chemin : cheminsPotentiels) {
            if (Files.exists(chemin)) {
                try {
                    Graphe g = CsvLoader.chargerGraphe(chemin, true);
                    System.out.println("✔ Données chargées avec succès depuis : " + chemin);
                    System.out.println("  • Villes enregistrées : " + g.getNombreVilles());
                    System.out.println("  • Routes actives      : " + g.getNombreRoutes() + "\n");
                    return g;
                } catch (IOException e) {
                    System.err.println("Erreur de lecture de " + chemin + " : " + e.getMessage());
                }
            }
        }

        System.out.println("⚠ Fichier data/routes.csv non trouvé, initialisation du graphe de secours par défaut...");
        Graphe fallback = new Graphe();
        Ville toulouse = new Ville("Toulouse");
        Ville bordeaux = new Ville("Bordeaux");
        Ville paris = new Ville("Paris");
        fallback.ajouterRoute(toulouse, bordeaux, 250);
        fallback.ajouterRoute(bordeaux, paris, 590);
        fallback.ajouterRoute(toulouse, paris, 680);
        return fallback;
    }

    private static void afficherVillesDisponibles(Graphe graphe) {
        List<String> nomsVilles = graphe.getVilles().stream()
                .map(Ville::getNom)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        System.out.println("Villes disponibles sur la carte :");
        System.out.println("---------------------------------");
        System.out.println(String.join("  |  ", nomsVilles));
        System.out.println("---------------------------------\n");
    }

    private static void calculerEtAfficherTrajet(Graphe graphe, Ville depart, Ville arrivee) {
        System.out.println("\nCalcul du plus court chemin en cours...");
        long debut = System.nanoTime();
        ResultatTrajet resultat = Dijkstra.trouverPlusCourtChemin(graphe, depart, arrivee);
        long dureeMicro = (System.nanoTime() - debut) / 1000;

        if (!resultat.isCheminTrouve()) {
            System.out.println("❌ Aucun chemin possible entre " + depart + " et " + arrivee + ".\n");
            return;
        }

        System.out.println("✔ Itinéraire optimal trouvé en " + dureeMicro + " µs !");
        System.out.println("-------------------------------------------------------------");
        System.out.println("🚩 Départ       : " + depart.getNom());
        System.out.println("🏁 Arrivée      : " + arrivee.getNom());
        System.out.println("📏 Distance tot : " + resultat.getDistanceTotale() + " km");
        System.out.println("🛣  Étapes (" + (resultat.getNombreEtapes() - 1) + " segments) :");

        List<Ville> etapes = resultat.getEtapes();
        for (int i = 0; i < etapes.size(); i++) {
            if (i == 0) {
                System.out.println("   1. [Départ]  " + etapes.get(i).getNom());
            } else {
                Ville prev = etapes.get(i - 1);
                Ville curr = etapes.get(i);
                // Retrouver la distance du segment
                double distSegment = 0.0;
                for (Route r : graphe.getRoutesVoisines(prev)) {
                    if (r.getDestination().equals(curr)) {
                        distSegment = r.getDistance();
                        break;
                    }
                }
                System.out.printf("   %d. -> %-12s (+%.1f km)%n", (i + 1), curr.getNom(), distSegment);
            }
        }
        System.out.println("-------------------------------------------------------------\n");
    }

    private static void lancerDemonstrationAuto(Graphe graphe) {
        System.out.println("\n[Mode démo automatique]");
        Ville toulouse = graphe.getVilleParNom("Toulouse");
        Ville lille = graphe.getVilleParNom("Lille");
        if (toulouse != null && lille != null) {
            calculerEtAfficherTrajet(graphe, toulouse, lille);
        }
    }
}
