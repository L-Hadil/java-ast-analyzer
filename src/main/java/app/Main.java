package app;

import ui.CLI;
import ui.GUI;
import javax.swing.SwingUtilities;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Analyseur de Code Java ===");
        System.out.println("Choisissez le mode d’exécution :");
        System.out.println("1. Interface Console (CLI)");
        System.out.println("2. Interface Graphique (GUI)");
        System.out.print("Votre choix : ");

        Scanner sc = new Scanner(System.in);
        String choix = sc.nextLine().trim();

        switch (choix) {
            case "1":
                CLI.start();
                break;

            case "2":
                SwingUtilities.invokeLater(() -> {
                    try {
                        com.formdev.flatlaf.FlatLightLaf.setup();
                    } catch (Exception e) {
                        System.err.println("Thème FlatLaf non disponible, utilisation du thème par défaut.");
                    }
                    new GUI().setVisible(true);
                });
                break;

            default:
                System.out.println("Choix invalide. Fin du programme.");
        }
    }
}
