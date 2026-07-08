package view;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class PainelInicial extends JPanel {

    public PainelInicial() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(criarTitulo(), BorderLayout.NORTH);
        add(criarCards(), BorderLayout.CENTER);
        add(criarDescricao(), BorderLayout.SOUTH);
    }

    private JLabel criarTitulo() {
        JLabel titulo = new JLabel("Bem-vindo ao GreenRoute");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TemaUI.VERDE_ESCURO);
        return titulo;
    }

    private JPanel criarCards() {
        JPanel cards = new JPanel(new GridLayout(2, 2, 15, 15));

        cards.add(criarCard(
                "CRUD em Interface Gráfica",
                "Cadastre, edite, liste e exclua veículos, cidades e eletropostos usando Java Swing."
        ));

        cards.add(criarCard(
                "ArrayList",
                "Os dados são armazenados em coleções dinâmicas, substituindo arrays tradicionais."
        ));

        cards.add(criarCard(
                "Cadastro Rápido por IA",
                "Digite um texto livre e a IA preenche automaticamente os campos do formulário."
        ));

        cards.add(criarCard(
                "Roteirização Inteligente",
                "A IA analisa autonomia, bateria, distância, eletropostos, clima e trânsito simulado."
        ));

        return cards;
    }

    private JPanel criarCard(String titulo, String descricao) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        TemaUI.aplicarCard(card);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitulo.setForeground(TemaUI.VERDE_ESCURO);

        JTextArea txtDescricao = new JTextArea(descricao);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setEditable(false);
        txtDescricao.setOpaque(false);
        txtDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescricao.setForeground(TemaUI.TEXTO_ESCURO);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(txtDescricao, BorderLayout.CENTER);

        return card;
    }

    private JTextArea criarDescricao() {
        JTextArea descricao = new JTextArea(
                "Sistema desenvolvido para o Módulo 2 do projeto GreenRoute, integrando GUI, ArrayList, " +
                        "tratamento de exceções, manipulação de strings, LLM e funcionalidades com Google Maps."
        );

        descricao.setLineWrap(true);
        descricao.setWrapStyleWord(true);
        descricao.setEditable(false);
        descricao.setOpaque(false);
        descricao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descricao.setForeground(TemaUI.TEXTO_ESCURO);

        return descricao;
    }
}