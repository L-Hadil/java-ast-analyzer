package app;

import ui.CLI;
import ui.GUI;

public class Main {
    public static void main(String[] args) throws Exception {
    	 CLI.start();
       
      /*  javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                com.formdev.flatlaf.FlatLightLaf.setup();
            } catch (Exception e) {
                System.err.println("️ Thème FlatLaf non disponible, utilisation du thème par défaut.");
            }

            new GUI().setVisible(true);
        });*/
    }
}
