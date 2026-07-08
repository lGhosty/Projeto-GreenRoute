package view;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Font;

public class PainelInicial extends JPanel {

    public PainelInicial() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(criarTitulo(), BorderLayout.NORTH);
        add(criarTextoResumo(), BorderLayout.CENTER);
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("Resumo do Sistema");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        return titulo;
    }

    private JTextArea criarTextoResumo() {
        JTextArea texto = new JTextArea();

        texto.setText(
                "O GreenRoute é um sistema desenvolvido em Java para cadastro e gerenciamento " +
                        "de veículos, cidades e eletropostos.\n\n" +

                        "Nesta etapa, o sistema foi atualizado para utilizar interface gráfica com Java Swing. " +
                        "As operações de cadastro, listagem, edição e exclusão são feitas pelas abas superiores.\n\n" +

                        "Principais funcionalidades implementadas:\n\n" +

                        "- Cadastro de veículos elétricos e híbridos;\n" +
                        "- Cadastro de cidades;\n" +
                        "- Cadastro de eletropostos;\n" +
                        "- Uso de ArrayList para armazenar os dados;\n" +
                        "- Tratamento de erros com mensagens visuais;\n" +
                        "- Cadastro rápido com auxílio de IA;\n" +
                        "- Planejamento de rotas considerando veículo, cidade e eletropostos;\n" +
                        "- Integração com Google Maps para abrir rotas e buscar eletropostos.\n\n" +

                        "Para utilizar o sistema, selecione uma das abas acima e preencha os formulários."
        );

        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        texto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return texto;
    }
}