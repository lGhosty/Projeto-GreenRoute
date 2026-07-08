package view;

import model.Cidade;
import model.Veiculo;
import util.TextoIAUtils;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;

public class SwingHelper {

    private SwingHelper() {
    }

    public static void adicionarCampo(JPanel painel, String label, JComponent componente) {
        painel.add(new JLabel(label));
        painel.add(componente);
    }

    public static int lerInteiro(JTextField campo, String nomeCampo) throws Exception {
        try {
            String numeroExtraido = TextoIAUtils.extrairPrimeiroNumero(campo.getText());

            if (numeroExtraido == null || numeroExtraido.trim().isEmpty()) {
                throw new NumberFormatException();
            }

            return (int) Double.parseDouble(numeroExtraido);
        } catch (NumberFormatException e) {
            throw new Exception("Informe um número inteiro válido para " + nomeCampo + ".");
        }
    }

    public static double lerDouble(JTextField campo, String nomeCampo) throws Exception {
        try {
            String numeroExtraido = TextoIAUtils.extrairPrimeiroNumero(campo.getText());

            if (numeroExtraido == null || numeroExtraido.trim().isEmpty()) {
                throw new NumberFormatException();
            }

            return Double.parseDouble(numeroExtraido);
        } catch (NumberFormatException e) {
            throw new Exception("Informe um número válido para " + nomeCampo + ".");
        }
    }

    public static void mostrarInfo(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "GreenRoute", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarErro(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void selecionarCidadeNoCombo(JComboBox<Cidade> combo, int cidadeId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Cidade cidade = combo.getItemAt(i);

            if (cidade.getId() == cidadeId) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    public static void selecionarVeiculoNoCombo(JComboBox<Veiculo> combo, int veiculoId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Veiculo veiculo = combo.getItemAt(i);

            if (veiculo.getId() == veiculoId) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }
}