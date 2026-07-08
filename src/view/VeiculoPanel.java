package view;

import model.Veiculo;
import model.VeiculoEletrico;
import model.VeiculoHibrido;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Map;

public class VeiculoPanel extends JPanel {

    private final AppContext context;
    private final Runnable atualizarTelasCallback;

    private JTextField txtId;
    private JTextField txtModelo;
    private JComboBox<String> comboTipo;
    private JTextField txtAutonomia;
    private JTextField txtBateria;
    private JTextField txtConsumo;
    private JTextField txtTempoCompleto;

    private JTextField txtConector;
    private JTextField txtTempoRapido;

    private JTextField txtTanque;
    private JTextField txtConsumoCombustivel;
    private JTextField txtCombustivel;

    private JTextArea txtCadastroIA;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private JPanel painelEletrico;
    private JPanel painelHibrido;

    public VeiculoPanel(AppContext context, Runnable atualizarTelasCallback) {
        this.context = context;
        this.atualizarTelasCallback = atualizarTelasCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        criarComponentes();
    }

    private void criarComponentes() {
        JPanel painelTopo = new JPanel(new BorderLayout(8, 8));

        painelTopo.add(criarPainelIA(), BorderLayout.NORTH);
        painelTopo.add(criarFormulario(), BorderLayout.CENTER);
        painelTopo.add(criarBotoes(), BorderLayout.SOUTH);

        add(painelTopo, BorderLayout.NORTH);
        add(criarTabela(), BorderLayout.CENTER);
    }

    private JPanel criarPainelIA() {
        txtCadastroIA = new JTextArea(3, 30);
        txtCadastroIA.setLineWrap(true);
        txtCadastroIA.setWrapStyleWord(true);
        txtCadastroIA.setText(
                "Cadastra o carro elétrico BYD Dolphin, autonomia de 400km, bateria em 85%, " +
                        "consumo de 0.15 kWh/km e leva 420 minutos para carregar totalmente. O conector dele é CCS2."
        );

        JButton btnPreencherIA = new JButton("Preencher formulário com IA");
        btnPreencherIA.addActionListener(e -> preencherComIA());

        JPanel painelIA = new JPanel(new BorderLayout(5, 5));
        painelIA.setBorder(BorderFactory.createTitledBorder("Cadastro Rápido por IA"));
        painelIA.add(new JScrollPane(txtCadastroIA), BorderLayout.CENTER);
        painelIA.add(btnPreencherIA, BorderLayout.EAST);

        return painelIA;
    }

    private JPanel criarFormulario() {
        JPanel formularioPrincipal = new JPanel(new GridLayout(1, 3, 10, 10));
        formularioPrincipal.setBorder(BorderFactory.createTitledBorder("Formulário de Veículo"));

        JPanel painelGeral = criarPainelDadosGerais();
        painelEletrico = criarPainelDadosEletrico();
        painelHibrido = criarPainelDadosHibrido();

        formularioPrincipal.add(painelGeral);
        formularioPrincipal.add(painelEletrico);
        formularioPrincipal.add(painelHibrido);

        atualizarCamposPorTipo();

        return formularioPrincipal;
    }

    private JPanel criarPainelDadosGerais() {
        JPanel painel = new JPanel(new GridLayout(8, 2, 6, 6));
        painel.setBorder(BorderFactory.createTitledBorder("Dados Gerais"));

        txtId = new JTextField();
        txtModelo = new JTextField();
        comboTipo = new JComboBox<>(new String[]{"Elétrico", "Híbrido"});
        txtAutonomia = new JTextField();
        txtBateria = new JTextField();
        txtConsumo = new JTextField();
        txtTempoCompleto = new JTextField();

        comboTipo.addActionListener(e -> atualizarCamposPorTipo());

        SwingHelper.adicionarCampo(painel, "ID", txtId);
        SwingHelper.adicionarCampo(painel, "Modelo", txtModelo);
        SwingHelper.adicionarCampo(painel, "Tipo", comboTipo);
        SwingHelper.adicionarCampo(painel, "Autonomia máxima", txtAutonomia);
        SwingHelper.adicionarCampo(painel, "Bateria atual %", txtBateria);
        SwingHelper.adicionarCampo(painel, "Consumo kWh/km", txtConsumo);
        SwingHelper.adicionarCampo(painel, "Recarga completa min", txtTempoCompleto);

        return painel;
    }

    private JPanel criarPainelDadosEletrico() {
        JPanel painel = new JPanel(new GridLayout(4, 2, 6, 6));
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Elétrico"));

        txtConector = new JTextField();
        txtTempoRapido = new JTextField();

        SwingHelper.adicionarCampo(painel, "Conector", txtConector);
        SwingHelper.adicionarCampo(painel, "Recarga rápida min", txtTempoRapido);

        return painel;
    }

    private JPanel criarPainelDadosHibrido() {
        JPanel painel = new JPanel(new GridLayout(4, 2, 6, 6));
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Híbrido"));

        txtTanque = new JTextField();
        txtConsumoCombustivel = new JTextField();
        txtCombustivel = new JTextField();

        SwingHelper.adicionarCampo(painel, "Tanque L", txtTanque);
        SwingHelper.adicionarCampo(painel, "Consumo km/L", txtConsumoCombustivel);
        SwingHelper.adicionarCampo(painel, "Combustível", txtCombustivel);

        return painel;
    }

    private JPanel criarBotoes() {
        JButton btnNovo = new JButton("Novo cadastro");
        JButton btnSalvar = new JButton("Salvar veículo");
        JButton btnAtualizar = new JButton("Atualizar veículo");
        JButton btnExcluir = new JButton("Excluir veículo");

        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);

        return botoes;
    }

    private JScrollPane criarTabela() {
        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Modelo", "Tipo", "Autonomia", "Bateria", "Autonomia Atual", "Conector/Combustível"},
                0
        );

        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarDaTabela();
            }
        });

        return new JScrollPane(tabela);
    }

    private void preencherComIA() {
        try {
            Map<String, String> dados = context.getIaPlannerService()
                    .extrairDadosCadastro(txtCadastroIA.getText(), "veiculo");

            txtId.setText(String.valueOf(context.getVeiculoController().gerarProximoId()));
            txtModelo.setText(dados.getOrDefault("modelo", ""));
            comboTipo.setSelectedItem(dados.getOrDefault("tipo", "Elétrico"));
            txtAutonomia.setText(dados.getOrDefault("autonomia", ""));
            txtBateria.setText(dados.getOrDefault("bateria", ""));
            txtConsumo.setText(dados.getOrDefault("consumo", ""));
            txtTempoCompleto.setText(dados.getOrDefault("tempoCompleto", ""));

            txtConector.setText(dados.getOrDefault("conector", ""));
            txtTempoRapido.setText(dados.getOrDefault("tempoRapido", "60"));

            txtTanque.setText(dados.getOrDefault("capacidadeTanque", "45"));
            txtConsumoCombustivel.setText(dados.getOrDefault("consumoCombustivel", "14"));
            txtCombustivel.setText(dados.getOrDefault("tipoCombustivel", "Gasolina"));

            atualizarCamposPorTipo();

            SwingHelper.mostrarInfo(this, "Campos do veículo preenchidos pela IA. Revise antes de salvar.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, "Erro ao preencher veículo com IA: " + e.getMessage());
        }
    }

    private void salvar() {
        try {
            Veiculo veiculo = criarVeiculoDoFormulario();

            context.getVeiculoController().salvar(veiculo);

            atualizarTelasCallback.run();
            limparFormulario();

            SwingHelper.mostrarInfo(this, "Veículo salvo com sucesso.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private void atualizar() {
        try {
            Veiculo veiculo = criarVeiculoDoFormulario();

            context.getVeiculoController().atualizar(veiculo);

            atualizarTelasCallback.run();

            SwingHelper.mostrarInfo(this, "Veículo atualizado com sucesso.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private void excluir() {
        try {
            int id = SwingHelper.lerInteiro(txtId, "ID do veículo");

            context.getVeiculoController().remover(id);

            atualizarTelasCallback.run();
            limparFormulario();

            SwingHelper.mostrarInfo(this, "Veículo excluído com sucesso.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private Veiculo criarVeiculoDoFormulario() throws Exception {
        int id = SwingHelper.lerInteiro(txtId, "ID");
        String modelo = txtModelo.getText();
        String tipo = (String) comboTipo.getSelectedItem();

        double autonomia = SwingHelper.lerDouble(txtAutonomia, "autonomia máxima");
        double bateria = SwingHelper.lerDouble(txtBateria, "bateria atual");
        double consumo = SwingHelper.lerDouble(txtConsumo, "consumo kWh/km");
        int tempoCompleto = SwingHelper.lerInteiro(txtTempoCompleto, "tempo de recarga completa");

        if ("Híbrido".equals(tipo)) {
            double tanque = SwingHelper.lerDouble(txtTanque, "tanque");
            double consumoCombustivel = SwingHelper.lerDouble(txtConsumoCombustivel, "consumo de combustível");
            String combustivel = txtCombustivel.getText();

            return new VeiculoHibrido(
                    id,
                    modelo,
                    autonomia,
                    bateria,
                    consumo,
                    tempoCompleto,
                    tanque,
                    consumoCombustivel,
                    combustivel
            );
        }

        String conector = txtConector.getText();
        int tempoRapido = SwingHelper.lerInteiro(txtTempoRapido, "tempo de recarga rápida");

        return new VeiculoEletrico(
                id,
                modelo,
                autonomia,
                bateria,
                consumo,
                tempoCompleto,
                conector,
                tempoRapido
        );
    }

    private void selecionarDaTabela() {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            return;
        }

        Object valorId = modeloTabela.getValueAt(linha, 0);
        int id = Integer.parseInt(valorId.toString());

        Veiculo veiculo = context.getVeiculoController().buscarPorId(id);

        if (veiculo == null) {
            return;
        }

        txtId.setText(String.valueOf(veiculo.getId()));
        txtModelo.setText(veiculo.getModelo());
        comboTipo.setSelectedItem(veiculo.getTipo());
        txtAutonomia.setText(String.valueOf(veiculo.getAutonomiaMaxima()));
        txtBateria.setText(String.valueOf(veiculo.getCargaBateriaAtual()));
        txtConsumo.setText(String.valueOf(veiculo.getConsumoKwhPorKm()));
        txtTempoCompleto.setText(String.valueOf(veiculo.getTempoRecargaCompleta()));

        if (veiculo instanceof VeiculoEletrico eletrico) {
            txtConector.setText(eletrico.getTipoConector());
            txtTempoRapido.setText(String.valueOf(eletrico.getTempoRecargaRapida()));

            txtTanque.setText("");
            txtConsumoCombustivel.setText("");
            txtCombustivel.setText("");
        }

        if (veiculo instanceof VeiculoHibrido hibrido) {
            txtConector.setText("");
            txtTempoRapido.setText("");

            txtTanque.setText(String.valueOf(hibrido.getCapacidadeTanqueCombustivel()));
            txtConsumoCombustivel.setText(String.valueOf(hibrido.getConsumoCombustivel()));
            txtCombustivel.setText(hibrido.getTipoCombustivel());
        }

        atualizarCamposPorTipo();
    }

    public void atualizarTabela() {
        modeloTabela.setRowCount(0);

        for (Veiculo veiculo : context.getVeiculoController().listarTodos()) {
            String detalhe = "";

            if (veiculo instanceof VeiculoEletrico eletrico) {
                detalhe = eletrico.getTipoConector();
            }

            if (veiculo instanceof VeiculoHibrido hibrido) {
                detalhe = hibrido.getTipoCombustivel();
            }

            modeloTabela.addRow(new Object[]{
                    veiculo.getId(),
                    veiculo.getModelo(),
                    veiculo.getTipo(),
                    veiculo.getAutonomiaMaxima(),
                    veiculo.getCargaBateriaAtual(),
                    String.format("%.1f", veiculo.getAutonomiaAtual()),
                    detalhe
            });
        }
    }

    private void limparFormulario() {
        txtId.setText(String.valueOf(context.getVeiculoController().gerarProximoId()));
        txtModelo.setText("");
        comboTipo.setSelectedItem("Elétrico");
        txtAutonomia.setText("");
        txtBateria.setText("");
        txtConsumo.setText("");
        txtTempoCompleto.setText("");

        txtConector.setText("");
        txtTempoRapido.setText("");

        txtTanque.setText("");
        txtConsumoCombustivel.setText("");
        txtCombustivel.setText("");

        atualizarCamposPorTipo();

        if (tabela != null) {
            tabela.clearSelection();
        }
    }

    private void atualizarCamposPorTipo() {
        if (comboTipo == null ||
                txtConector == null ||
                txtTempoRapido == null ||
                txtTanque == null ||
                txtConsumoCombustivel == null ||
                txtCombustivel == null ||
                painelEletrico == null ||
                painelHibrido == null) {
            return;
        }

        String tipo = (String) comboTipo.getSelectedItem();

        boolean eletrico = "Elétrico".equals(tipo);
        boolean hibrido = "Híbrido".equals(tipo);

        txtConector.setEnabled(eletrico);
        txtTempoRapido.setEnabled(eletrico);
        painelEletrico.setEnabled(eletrico);

        txtTanque.setEnabled(hibrido);
        txtConsumoCombustivel.setEnabled(hibrido);
        txtCombustivel.setEnabled(hibrido);
        painelHibrido.setEnabled(hibrido);

        if (eletrico) {
            txtTanque.setText("");
            txtConsumoCombustivel.setText("");
            txtCombustivel.setText("");
        }

        if (hibrido) {
            txtConector.setText("");
            txtTempoRapido.setText("");
        }
    }
}