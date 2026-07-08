package view;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    private final AppContext context = new AppContext();

    private VeiculoPanel veiculoPanel;
    private CidadePanel cidadePanel;
    private EletropostoPanel eletropostoPanel;
    private PlanejadorIAPanel planejadorIAPanel;

    public MainFrame() {
        setTitle("GreenRoute - Módulo 2");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        criarAbas();

        context.carregarDadosExemplo();
        atualizarTelas();
    }

    private void criarAbas() {
        JTabbedPane abas = new JTabbedPane();

        veiculoPanel = new VeiculoPanel(context, this::atualizarTelas);
        cidadePanel = new CidadePanel(context, this::atualizarTelas);
        eletropostoPanel = new EletropostoPanel(context, this::atualizarTelas);
        planejadorIAPanel = new PlanejadorIAPanel(context);

        abas.addTab("Veículos", veiculoPanel);
        abas.addTab("Cidades", cidadePanel);
        abas.addTab("Eletropostos", eletropostoPanel);
        abas.addTab("Planejador IA", planejadorIAPanel);

        add(abas, BorderLayout.CENTER);
    }

    private void atualizarTelas() {
        veiculoPanel.atualizarTabela();
        cidadePanel.atualizarTabela();
        eletropostoPanel.atualizarTabela();
        eletropostoPanel.atualizarComboCidades();
        planejadorIAPanel.atualizarCombos();
    }
}