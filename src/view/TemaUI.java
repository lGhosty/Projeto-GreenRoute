package view;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public class TemaUI {

    public static final Color VERDE_ESCURO = new Color(22, 101, 52);
    public static final Color VERDE_MEDIO = new Color(34, 197, 94);
    public static final Color VERDE_CLARO = new Color(220, 252, 231);
    public static final Color CINZA_FUNDO = new Color(245, 247, 250);
    public static final Color BRANCO = Color.WHITE;
    public static final Color TEXTO_ESCURO = new Color(31, 41, 55);

    private TemaUI() {
    }

    public static void aplicarTemaGlobal() {
        UIManager.put("Panel.background", CINZA_FUNDO);
        UIManager.put("TabbedPane.background", CINZA_FUNDO);
        UIManager.put("TabbedPane.selected", VERDE_CLARO);
        UIManager.put("Button.background", VERDE_ESCURO);
        UIManager.put("Button.foreground", BRANCO);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TextArea.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
    }

    public static void aplicarCard(JComponent componente) {
        componente.setBackground(BRANCO);
        componente.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
    }
}