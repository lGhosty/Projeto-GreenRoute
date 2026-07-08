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

    public VeiculoPanel(AppContext context, Runnable atualizarTelasCallback) {
        this.context = context;
        this.atualizarTelasCallback = atualizarTelasCallback;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        criarComponentes();
    }

    private void criarComponentes() {
        JPanel painelTopo = new JPanel(new BorderLayout(5, 5));

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

        JButton btnPreencherIA = new JButton("Preencher com IA");
        btnPreencherIA.addActionListener(e -> preencherComIA());

        JPanel painelIA = new JPanel(new BorderLayout(5, 5));
        painelIA.setBorder(BorderFactory.createTitledBorder("Cadastro Rápido por IA"));
        painelIA.add(new JScrollPane(txtCadastroIA), BorderLayout.CENTER);
        painelIA.add(btnPreencherIA, BorderLayout.EAST);

        return painelIA;
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel(new GridLayout(6, 4, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados do Veículo"));

        txtId = new JTextField();
        txtModelo = new JTextField();
        comboTipo = new JComboBox<>(new String[]{"Elétrico", "Híbrido"});
        txtAutonomia = new JTextField();
        txtBateria = new JTextField();
        txtConsumo = new JTextField();
        txtTempoCompleto = new JTextField();
        txtConector = new JTextField();
        txtTempoRapido = new JTextField();
        txtTanque = new JTextField("45");
        txtConsumoCombustivel = new JTextField("14");
        txtCombustivel = new JTextField("Gasolina");

        SwingHelper.adicionarCampo(formulario, "ID", txtId);
        SwingHelper.adicionarCampo(formulario, "Modelo", txtModelo);
        SwingHelper.adicionarCampo(formulario, "Tipo", comboTipo);
        SwingHelper.adicionarCampo(formulario, "Autonomia máxima", txtAutonomia);

        SwingHelper.adicionarCampo(formulario, "Bateria atual %", txtBateria);
        SwingHelper.adicionarCampo(formulario, "Consumo kWh/km", txtConsumo);
        SwingHelper.adicionarCampo(formulario, "Recarga completa min", txtTempoCompleto);
        SwingHelper.adicionarCampo(formulario, "Conector", txtConector);

        SwingHelper.adicionarCampo(formulario, "Recarga rápida min", txtTempoRapido);
        SwingHelper.adicionarCampo(formulario, "Tanque L", txtTanque);
        SwingHelper.adicionarCampo(formulario, "Consumo km/L", txtConsumoCombustivel);
        SwingHelper.adicionarCampo(formulario, "Combustível", txtCombustivel);

        return formulario;
    }

    private JPanel criarBotoes() {
        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");

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
            txtTanque.setText("45");
            txtConsumoCombustivel.setText("14");
            txtCombustivel.setText("Gasolina");
        }

        if (veiculo instanceof VeiculoHibrido hibrido) {
            txtConector.setText("");
            txtTempoRapido.setText("60");
            txtTanque.setText(String.valueOf(hibrido.getCapacidadeTanqueCombustivel()));
            txtConsumoCombustivel.setText(String.valueOf(hibrido.getConsumoCombustivel()));
            txtCombustivel.setText(hibrido.getTipoCombustivel());
        }
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
        txtTanque.setText("45");
        txtConsumoCombustivel.setText("14");
        txtCombustivel.setText("Gasolina");

        if (tabela != null) {
            tabela.clearSelection();
        }
    }
}