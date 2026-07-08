package view;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

public class MainFrame extends JFrame {

    private final AppContext context = new AppContext();

    private PainelVeiculos painelVeiculos;
    private PainelCidades painelCidades;
    private PainelEletropostos painelEletropostos;
    private PainelPlanejadorIA painelPlanejadorIA;

    public MainFrame() {
        TemaUI.aplicarTemaGlobal();

        setTitle("GreenRoute - Sistema Inteligente de Rotas Sustentáveis");
        setSize(1150, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(criarCabecalho(), BorderLayout.NORTH);
        criarAbas();

        context.carregarDadosExemplo();
        atualizarTelas();
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(TemaUI.VERDE_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel titulo = new JLabel("GreenRoute");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitulo = new JLabel("Sistema Inteligente de Rotas Sustentáveis com IA");
        subtitulo.setForeground(new Color(220, 252, 231));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        cabecalho.add(textos, BorderLayout.WEST);

        return cabecalho;
    }

    private void criarAbas() {
        JTabbedPane abas = new JTabbedPane();

        painelVeiculos = new PainelVeiculos(context, this::atualizarTelas);
        painelCidades = new PainelCidades(context, this::atualizarTelas);
        painelEletropostos = new PainelEletropostos(context, this::atualizarTelas);
        painelPlanejadorIA = new PainelPlanejadorIA(context);

        abas.addTab("Início", new PainelInicial());
        abas.addTab("Veículos", painelVeiculos);
        abas.addTab("Cidades", painelCidades);
        abas.addTab("Eletropostos", painelEletropostos);
        abas.addTab("Planejador IA", painelPlanejadorIA);

        add(abas, BorderLayout.CENTER);
    }

    private void atualizarTelas() {
        painelVeiculos.atualizarTabela();
        painelCidades.atualizarTabela();
        painelEletropostos.atualizarTabela();
        painelEletropostos.atualizarComboCidades();
        painelPlanejadorIA.atualizarCombos();
    }
}