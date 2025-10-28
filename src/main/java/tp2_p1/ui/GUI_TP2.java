package tp2_p1.ui;

import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.util.*;
import tp2_p1.analyzer.*;
import tp2_p1.spoon_version.*;

public class GUI_TP2 extends JFrame {
    private Path selectedPath;
    private final JComboBox<String> modeSelector;

    public GUI_TP2() {
        setTitle("Analyseur de Code Java – TP2 (JDT) et Version Spoon");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Analyseur de Code Java – TP2 & Version Spoon", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;

        JButton chooseBtn = new JButton("📂 Sélectionner un dossier source");
        JTextField pathField = new JTextField();
        pathField.setEditable(false);
        chooseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedPath = fc.getSelectedFile().toPath();
                pathField.setText(selectedPath.toString());
            }
        });
        center.add(chooseBtn, c);

        c.gridy++;
        center.add(pathField, c);

        // Sélecteur de mode (JDT / Spoon)
        c.gridy++;
        c.gridwidth = 1;
        modeSelector = new JComboBox<>(new String[]{"Version JDT (TP2)", "Version Spoon"});
        center.add(modeSelector, c);

        // Boutons de la GUI
        c.gridx = 1;
        JButton showGraphBtn = new JButton("Afficher Graphe d'appel");
        center.add(showGraphBtn, c);

        c.gridx = 0;
        c.gridy++;
        JButton couplingABBtn = new JButton("Calculer Couplage (A,B)");
        center.add(couplingABBtn, c);

        c.gridx = 1;
        JButton couplingBtn = new JButton("Générer Graphe de Couplage");
        center.add(couplingBtn, c);

        c.gridx = 0;
        c.gridy++;
        JButton clusterBtn = new JButton("Exécuter Clustering");
        center.add(clusterBtn, c);

        // Actions
        showGraphBtn.addActionListener(e -> runCallGraph());
        couplingABBtn.addActionListener(e -> calculateCouplingBetweenTwoClasses());
        couplingBtn.addActionListener(e -> runCouplingAnalysis());
        clusterBtn.addActionListener(e -> runClustering());

        main.add(center, BorderLayout.CENTER);
        add(main);
    }

    // ---------- 1. Graphe d'appel inter-classes ----------
    private void runCallGraph() {
        if (selectedPath == null) {
            JOptionPane.showMessageDialog(this, "Veuillez choisir un dossier source !");
            return;
        }
        try {
            boolean isSpoon = modeSelector.getSelectedIndex() == 1;
            Map<String, Set<String>> graph;
            if (isSpoon) {
                graph = SpoonCallGraphBuilder.buildCallGraph(selectedPath);
            } else {
                CouplingCalculator calc = new CouplingCalculator(selectedPath);
                graph = calc.getCallGraph();
            }
            

            JTextArea text = new JTextArea();
            graph.forEach((k, v) -> text.append(k + " -> " + v + "\n"));
            text.setEditable(false);
            JScrollPane scroll = new JScrollPane(text);
            scroll.setPreferredSize(new Dimension(700, 400));
            JOptionPane.showMessageDialog(this, scroll, "Graphe d'appel", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }


    // ---------- 2. Calculer Couplage entre deux classes ----------
    private void calculateCouplingBetweenTwoClasses() {
        if (selectedPath == null) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord choisir un dossier source !");
            return;
        }
        try {
            boolean isSpoon = modeSelector.getSelectedIndex() == 1;
            String classA = JOptionPane.showInputDialog(this, "Nom de la première classe (A) :");
            String classB = JOptionPane.showInputDialog(this, "Nom de la deuxième classe (B) :");
            if (classA == null || classB == null || classA.isEmpty() || classB.isEmpty()) return;

            float c;
            if (isSpoon) {
                SpoonCouplingCalculator calc = new SpoonCouplingCalculator(selectedPath);
                c = calc.coupling(classA, classB);
            } else {
                CouplingCalculator calc = new CouplingCalculator(selectedPath);
                c = calc.coupling(classA, classB);
            }

            JOptionPane.showMessageDialog(this, String.format("Couplage(%s,%s) = %.4f", classA, classB, c));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    // ---------- 3. Générer Graphe de Couplage ----------
    private void runCouplingAnalysis() {
        if (selectedPath == null) {
            JOptionPane.showMessageDialog(this, "Veuillez choisir un dossier source !");
            return;
        }

        try {
            boolean isSpoon = modeSelector.getSelectedIndex() == 1;
            if (isSpoon) {
                SpoonCouplingCalculator calc = new SpoonCouplingCalculator(selectedPath);
                Map<String, Map<String, Float>> m = calc.getCouplingMatrix();
                CouplingGraphExporter.exportToDot(m, Path.of("spoon_coupling_graph.dot"));
                CouplingGraphExporter.exportToPNG(Path.of("spoon_coupling_graph.dot"), Path.of("spoon_coupling_graph.png"));
                JOptionPane.showMessageDialog(this, "Graphe Spoon généré : spoon_coupling_graph.png");
                openImage(Path.of("spoon_coupling_graph.png"));
            } else {
                CouplingCalculator calc = new CouplingCalculator(selectedPath);
                Map<String, Map<String, Float>> m = calc.getCouplingMatrix();
                CouplingGraphExporter.exportToDot(m, Path.of("coupling_graph.dot"));
                CouplingGraphExporter.exportToPNG(Path.of("coupling_graph.dot"), Path.of("coupling_graph.png"));
                JOptionPane.showMessageDialog(this, "Graphe JDT généré : coupling_graph.png");
                openImage(Path.of("coupling_graph.png"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    // ---------- 4. Exécuter Clustering ----------
    private void runClustering() {
        if (selectedPath == null) {
            JOptionPane.showMessageDialog(this, "Veuillez choisir un dossier source !");
            return;
        }

        try {
            boolean isSpoon = modeSelector.getSelectedIndex() == 1;
            CouplingMatrix matrix;
            if (isSpoon) {
                SpoonCouplingCalculator calc = new SpoonCouplingCalculator(selectedPath);
                matrix = new CouplingMatrix(calc.getCouplingMatrix());
                HierarchicalClusterer clusterer = new HierarchicalClusterer(matrix);
                HierarchicalClusterer.Cluster root = clusterer.run();
                HierarchicalClusterer.exportToDot(root, Path.of("spoon_clusters.dot"));
                HierarchicalClusterer.exportToPNG(Path.of("spoon_clusters.dot"), Path.of("spoon_clusters.png"));
                JOptionPane.showMessageDialog(this, "Clustering Spoon généré : spoon_clusters.png");
                openImage(Path.of("spoon_clusters.png"));
            } else {
                CouplingCalculator calc = new CouplingCalculator(selectedPath);
                matrix = new CouplingMatrix(calc.getCouplingMatrix());
                HierarchicalClusterer clusterer = new HierarchicalClusterer(matrix);
                HierarchicalClusterer.Cluster root = clusterer.run();
                HierarchicalClusterer.exportToDot(root, Path.of("clusters.dot"));
                HierarchicalClusterer.exportToPNG(Path.of("clusters.dot"), Path.of("clusters.png"));
                JOptionPane.showMessageDialog(this, "Clustering JDT généré : clusters.png");
                openImage(Path.of("clusters.png"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    // ---------- Ouvre automatiquement les images ----------
    private void openImage(Path pngFile) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac")) {
                new ProcessBuilder("open", pngFile.toString()).start();
            } else if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "start", pngFile.toString()).start();
            } else {
                new ProcessBuilder("xdg-open", pngFile.toString()).start();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Impossible d'ouvrir l'image : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI_TP2().setVisible(true));
    }

}
