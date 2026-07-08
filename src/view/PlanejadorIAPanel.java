package view;

import model.Cidade;
import model.Veiculo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class PlanejadorIAPanel extends JPanel {

    private final AppContext context;

    private JComboBox<Veiculo> comboVeiculo;
    private JComboBox<Cidade> comboCidade;
    private JTextArea txtResultado;

    public PlanejadorIAPanel(AppContext context) {
        this.context = context;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        criarComponentes();
    }

    private void criarComponentes() {
        JPanel painelTopo = new JPanel(new BorderLayout(5, 5));

        painelTopo.add(criarFormulario(), BorderLayout.CENTER);
        painelTopo.add(criarBotoes(), BorderLayout.SOUTH);

        txtResultado = new JTextArea();
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setEditable(false);

        add(painelTopo, BorderLayout.NORTH);
        add(new JScrollPane(txtResultado), BorderLayout.CENTER);
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel(new GridLayout(2, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Planejador de Rotas Inteligente"));

        comboVeiculo = new JComboBox<>();
        comboCidade = new JComboBox<>();

        SwingHelper.adicionarCampo(formulario, "Veículo", comboVeiculo);
        SwingHelper.adicionarCampo(formulario, "Cidade de destino", comboCidade);

        return formulario;
    }

    private JPanel criarBotoes() {
        JButton btnPlanejar = new JButton("Planejar com IA");
        JButton btnAbrirRotaMaps = new JButton("Abrir rota no Google Maps");
        JButton btnBuscarEletropostosMaps = new JButton("Buscar eletropostos no Maps");

        btnPlanejar.addActionListener(e -> planejarComIA());
        btnAbrirRotaMaps.addActionListener(e -> abrirRotaNoMaps());
        btnBuscarEletropostosMaps.addActionListener(e -> buscarEletropostosNoMaps());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnPlanejar);
        botoes.add(btnAbrirRotaMaps);
        botoes.add(btnBuscarEletropostosMaps);

        return botoes;
    }

    private void planejarComIA() {
        try {
            Veiculo veiculo = (Veiculo) comboVeiculo.getSelectedItem();
            Cidade cidade = (Cidade) comboCidade.getSelectedItem();

            if (veiculo == null || cidade == null) {
                SwingHelper.mostrarErro(this, "Cadastre e selecione um veículo e uma cidade.");
                return;
            }

            String resposta = context.getRouteController()
                    .planejarRotaComIA(veiculo.getId(), cidade.getId());

            txtResultado.setText(resposta);
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private void abrirRotaNoMaps() {
        try {
            Cidade cidade = (Cidade) comboCidade.getSelectedItem();

            if (cidade == null) {
                SwingHelper.mostrarErro(this, "Selecione uma cidade de destino.");
                return;
            }

            String origem = "Recife PE";
            String destino = cidade.getNome() + " " + cidade.getEstado();

            context.getGoogleMapsService().abrirRota(origem, destino);
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, "Erro ao abrir rota no Google Maps: " + e.getMessage());
        }
    }

    private void buscarEletropostosNoMaps() {
        try {
            Cidade cidade = (Cidade) comboCidade.getSelectedItem();

            if (cidade == null) {
                SwingHelper.mostrarErro(this, "Selecione uma cidade de destino.");
                return;
            }

            context.getGoogleMapsService()
                    .abrirPesquisaEletropostos(cidade.getNome(), cidade.getEstado());
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, "Erro ao buscar eletropostos no Maps: " + e.getMessage());
        }
    }

    public void atualizarCombos() {
        Veiculo veiculoSelecionado = (Veiculo) comboVeiculo.getSelectedItem();
        Cidade cidadeSelecionada = (Cidade) comboCidade.getSelectedItem();

        comboVeiculo.removeAllItems();
        comboCidade.removeAllItems();

        for (Veiculo veiculo : context.getVeiculoController().listarTodos()) {
            comboVeiculo.addItem(veiculo);
        }

        for (Cidade cidade : context.getCidadeController().listarTodas()) {
            comboCidade.addItem(cidade);
        }

        if (veiculoSelecionado != null) {
            SwingHelper.selecionarVeiculoNoCombo(comboVeiculo, veiculoSelecionado.getId());
        }

        if (cidadeSelecionada != null) {
            SwingHelper.selecionarCidadeNoCombo(comboCidade, cidadeSelecionada.getId());
        }
    }
}