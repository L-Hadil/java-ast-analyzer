package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;
import model.*;
import processor.*;

public class GUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Path selectedPath;

    public GUI() {
        setTitle("Analyseur de Code Java TP1-Partie2");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createHomePanel(), "home");
        mainPanel.add(createAnalysePanel(), "analyse");

        add(mainPanel);
        cardLayout.show(mainPanel, "home");
    }

    /** ----------- PAGE D'ACCUEIL ----------- **/
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 255));

        JLabel title = new JLabel(" Analyseur de Code Java", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        JTextArea info = new JTextArea(
                "Bienvenue dans l'application d'analyse statique  de code Java !\n\n"
              + "Fonctionnalités :\n"
              + "- Analyse Statistique (13 métriques principales)\n"
              + "- Graphe d'appel inter-classes (avec export en image PNG)\n\n"
              + "Guide d'utilisation :\n"
              + "1. Cliquez sur \"Commencer l'analyse\"\n"
              + "2. Sélectionnez le dossier contenant vos fichiers .java\n"
              + "3. Choisissez le type d'analyse à effectuer\n"
        );

        info.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        info.setEditable(false);
        info.setOpaque(false);
        info.setMargin(new Insets(20, 40, 20, 40));
        panel.add(info, BorderLayout.CENTER);

        JButton startBtn = new JButton(" Commencer l'analyse");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startBtn.setBackground(new Color(80, 150, 255));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> cardLayout.show(mainPanel, "analyse"));

        JPanel bottom = new JPanel();
        bottom.setBackground(panel.getBackground());
        bottom.add(startBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    /** ----------- PAGE D'ANALYSE ----------- **/
    private JPanel createAnalysePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(250, 250, 255));

        // Sélection du dossier
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lbl = new JLabel("📂 Dossier source : ");
        JTextField pathField = new JTextField(40);
        pathField.setEditable(false);
        JButton browseBtn = new JButton("Parcourir...");
        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedPath = fc.getSelectedFile().toPath();
                pathField.setText(selectedPath.toString());
            }
        });
        topPanel.add(lbl);
        topPanel.add(pathField);
        topPanel.add(browseBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        // Boutons d'analyse
        JPanel centerPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton statBtn = new JButton(" Analyse Statistique");
        JButton graphBtn = new JButton(" Graphe d'appel");

        statBtn.setBackground(new Color(50, 150, 250));
        graphBtn.setBackground(new Color(50, 200, 120));
        for (JButton b : new JButton[]{statBtn, graphBtn}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 16));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }
        centerPanel.add(statBtn);
        centerPanel.add(graphBtn);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Actions des boutons
        statBtn.addActionListener(e -> showMetricsChoice());
        graphBtn.addActionListener(e -> runGraphAnalysis());

        // Retour
        JButton backBtn = new JButton(" Retour à l'accueil");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "home"));
        JPanel south = new JPanel();
        south.add(backBtn);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    /** ----------- MÉTRIQUES : AFFICHAGE AVEC BOUTONS COLORÉS ----------- **/
    private void showMetricsChoice() {
        if (selectedPath == null) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord choisir un dossier source !");
            return;
        }

        String[] metricNames = {
            "1. Nombre de classes de l’application",
            "2. Nombre de lignes de code de l’application",
            "3. Nombre total de méthodes",
            "4. Nombre total de packages",
            "5. Moyenne de méthodes par classe",
            "6. Moyenne de lignes de code par méthode",
            "7. Moyenne d’attributs par classe",
            "8. 10% des classes avec le plus de méthodes",
            "9. 10% des classes avec le plus d’attributs",
            "10. Classes appartenant aux deux catégories précédentes",
            "11. Classes ayant plus de X méthodes",
            "12. 10% des méthodes les plus longues (par classe)",
            "13. Nombre maximal de paramètres parmi toutes les méthodes"
        };

        JDialog dialog = new JDialog(this, "Choisissez une métrique", true);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.getContentPane().setBackground(new Color(240, 250, 255));

        try {
            ProjectModel pm = ASTProcessor.process(selectedPath);
            Map<String, Object> stats = StatisticsCalculator.compute(pm, 5);

            for (int i = 0; i < metricNames.length; i++) {
                final int idx = i;
                JButton btn = new JButton(metricNames[i]);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                btn.setBackground(new Color(60 + (i * 10 % 120), 150, 250 - (i * 15 % 150)));
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);

                btn.addActionListener(ev -> {
                    try {
                        int X = 5;
                        if (idx == 10) { // métrique 11
                            String input = JOptionPane.showInputDialog(dialog,
                                    "Entrez la valeur de X :", "5");
                            if (input != null && !input.isEmpty())
                                X = Integer.parseInt(input);
                        }
                        Map<String, Object> newStats = StatisticsCalculator.compute(pm, X);
                        Object value = newStats.values().toArray()[idx];
                        JOptionPane.showMessageDialog(dialog, value.toString(),
                                "Résultat - " + metricNames[idx], JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Erreur : " + ex.getMessage());
                    }
                });

                dialog.add(btn);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }

        dialog.setVisible(true);
    }


    /** ----------- GRAPHE D'APPEL ----------- **/
    private void runGraphAnalysis() {
        if (selectedPath == null) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord choisir un dossier source !");
            return;
        }
        try {
            Map<String, Set<String>> graph = CallGraphBuilder.buildCallGraph(selectedPath);
            int choice = JOptionPane.showConfirmDialog(this,
                    "Souhaitez-vous exporter le graphe en image PNG ?",
                    "Graphe d'appel", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                ui.GraphVisualizer.exportGraphToPNG(graph, "call_graph");
                JOptionPane.showMessageDialog(this, "Graphe exporté : call_graph.png");
            } else {
                JTextArea text = new JTextArea();
                graph.forEach((k, v) -> text.append(k + " --> " + v + "\n"));
                text.setEditable(false);
                JScrollPane scroll = new JScrollPane(text);
                scroll.setPreferredSize(new Dimension(600, 400));
                JOptionPane.showMessageDialog(this, scroll,
                        "Graphe d'appel (texte)", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
            	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            } catch (Exception ignored) {}
            new GUI().setVisible(true);
        });
    }
}
