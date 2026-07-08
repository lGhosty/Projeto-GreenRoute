package view;

import model.Cidade;
import model.Eletroposto;

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

public class EletropostoPanel extends JPanel {

    private final AppContext context;
    private final Runnable atualizarTelasCallback;

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtLocalizacao;
    private JComboBox<Cidade> comboCidade;
    private JTextField txtConectores;
    private JTextField txtPotencia;
    private JTextField txtPreco;
    private JTextField txtVagas;
    private JTextArea txtCadastroIA;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public EletropostoPanel(AppContext context, Runnable atualizarTelasCallback) {
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
                "Cadastre um eletroposto no shopping com conector CCS2, potência de 50 kW, " +
                        "preço de 2.20 por kWh e 2 vagas disponíveis."
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
        JPanel formulario = new JPanel(new GridLayout(4, 4, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados do Eletroposto"));

        txtId = new JTextField();
        txtNome = new JTextField();
        txtLocalizacao = new JTextField();
        comboCidade = new JComboBox<>();
        txtConectores = new JTextField();
        txtPotencia = new JTextField();
        txtPreco = new JTextField();
        txtVagas = new JTextField();

        SwingHelper.adicionarCampo(formulario, "ID", txtId);
        SwingHelper.adicionarCampo(formulario, "Nome", txtNome);
        SwingHelper.adicionarCampo(formulario, "Localização", txtLocalizacao);
        SwingHelper.adicionarCampo(formulario, "Cidade", comboCidade);

        SwingHelper.adicionarCampo(formulario, "Conectores", txtConectores);
        SwingHelper.adicionarCampo(formulario, "Potência kW", txtPotencia);
        SwingHelper.adicionarCampo(formulario, "Preço kWh", txtPreco);
        SwingHelper.adicionarCampo(formulario, "Vagas", txtVagas);

        return formulario;
    }

    private JPanel criarBotoes() {
        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnMaps = new JButton("Abrir localização no Maps");

        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnMaps.addActionListener(e -> abrirLocalizacaoNoMaps());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);
        botoes.add(btnMaps);

        return botoes;
    }

    private JScrollPane criarTabela() {
        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Localização", "Cidade ID", "Conectores", "Potência", "Preço", "Vagas"},
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
                    .extrairDadosCadastro(txtCadastroIA.getText(), "eletroposto");

            txtId.setText(String.valueOf(context.getEletropostoController().gerarProximoId()));
            txtNome.setText(dados.getOrDefault("nome", ""));
            txtLocalizacao.setText(dados.getOrDefault("localizacao", ""));
            txtConectores.setText(dados.getOrDefault("conectores", "CCS2"));
            txtPotencia.setText(dados.getOrDefault("potencia", "50"));
            txtPreco.setText(dados.getOrDefault("preco", "2.20"));
            txtVagas.setText(dados.getOrDefault("vagas", "2"));

            if (comboCidade.getItemCount() > 0) {
                comboCidade.setSelectedIndex(0);
            }

            SwingHelper.mostrarInfo(this, "Campos do eletroposto preenchidos pela IA. Revise antes de salvar.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, "Erro ao preencher eletroposto com IA: " + e.getMessage());
        }
    }

    private void salvar() {
        try {
            Eletroposto eletroposto = criarEletropostoDoFormulario();

            context.getEletropostoController().salvar(eletroposto);

            atualizarTelasCallback.run();
            limparFormulario();

            SwingHelper.mostrarInfo(this, "Eletroposto salvo com sucesso.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private void atualizar() {
        try {
            Eletroposto eletroposto = criarEletropostoDoFormulario();

            context.getEletropostoController().atualizar(eletroposto);

            atualizarTelasCallback.run();

            SwingHelper.mostrarInfo(this, "Eletroposto atualizado com sucesso.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private void excluir() {
        try {
            int id = SwingHelper.lerInteiro(txtId, "ID do eletroposto");

            context.getEletropostoController().remover(id);

            atualizarTelasCallback.run();
            limparFormulario();

            SwingHelper.mostrarInfo(this, "Eletroposto excluído com sucesso.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private Eletroposto criarEletropostoDoFormulario() throws Exception {
        Cidade cidade = (Cidade) comboCidade.getSelectedItem();

        if (cidade == null) {
            throw new Exception("Cadastre uma cidade antes de cadastrar um eletroposto.");
        }

        int id = SwingHelper.lerInteiro(txtId, "ID");
        String nome = txtNome.getText();
        String localizacao = txtLocalizacao.getText();
        String conectores = txtConectores.getText();
        double potencia = SwingHelper.lerDouble(txtPotencia, "potência");
        double preco = SwingHelper.lerDouble(txtPreco, "preço por kWh");
        int vagas = SwingHelper.lerInteiro(txtVagas, "vagas");

        return new Eletroposto(
                id,
                nome,
                localizacao,
                cidade.getId(),
                conectores,
                potencia,
                preco,
                vagas
        );
    }

    private void selecionarDaTabela() {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            return;
        }

        Object valorId = modeloTabela.getValueAt(linha, 0);
        int id = Integer.parseInt(valorId.toString());

        Eletroposto eletroposto = context.getEletropostoController().buscarPorId(id);

        if (eletroposto == null) {
            return;
        }

        txtId.setText(String.valueOf(eletroposto.getId()));
        txtNome.setText(eletroposto.getNome());
        txtLocalizacao.setText(eletroposto.getLocalizacao());
        SwingHelper.selecionarCidadeNoCombo(comboCidade, eletroposto.getCidadeId());
        txtConectores.setText(eletroposto.getTiposConectoresDisponiveis());
        txtPotencia.setText(String.valueOf(eletroposto.getPotenciaCargaKw()));
        txtPreco.setText(String.valueOf(eletroposto.getPrecoPorKwh()));
        txtVagas.setText(String.valueOf(eletroposto.getVagasDisponiveis()));
    }

    private void abrirLocalizacaoNoMaps() {
        try {
            String localizacao = txtLocalizacao.getText();

            if (localizacao == null || localizacao.trim().isEmpty()) {
                SwingHelper.mostrarErro(this, "Informe ou selecione uma localização.");
                return;
            }

            context.getGoogleMapsService().abrirLocalizacao(localizacao);
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, "Erro ao abrir localização no Maps: " + e.getMessage());
        }
    }

    public void atualizarTabela() {
        modeloTabela.setRowCount(0);

        for (Eletroposto eletroposto : context.getEletropostoController().listarTodos()) {
            modeloTabela.addRow(new Object[]{
                    eletroposto.getId(),
                    eletroposto.getNome(),
                    eletroposto.getLocalizacao(),
                    eletroposto.getCidadeId(),
                    eletroposto.getTiposConectoresDisponiveis(),
                    eletroposto.getPotenciaCargaKw(),
                    eletroposto.getPrecoPorKwh(),
                    eletroposto.getVagasDisponiveis()
            });
        }
    }

    public void atualizarComboCidades() {
        Cidade cidadeSelecionada = (Cidade) comboCidade.getSelectedItem();

        comboCidade.removeAllItems();

        for (Cidade cidade : context.getCidadeController().listarTodas()) {
            comboCidade.addItem(cidade);
        }

        if (cidadeSelecionada != null) {
            SwingHelper.selecionarCidadeNoCombo(comboCidade, cidadeSelecionada.getId());
        }
    }

    private void limparFormulario() {
        txtId.setText(String.valueOf(context.getEletropostoController().gerarProximoId()));
        txtNome.setText("");
        txtLocalizacao.setText("");

        if (comboCidade.getItemCount() > 0) {
            comboCidade.setSelectedIndex(0);
        }

        txtConectores.setText("");
        txtPotencia.setText("");
        txtPreco.setText("");
        txtVagas.setText("");

        if (tabela != null) {
            tabela.clearSelection();
        }
    }
}