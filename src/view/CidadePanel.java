package view;

import model.Cidade;

import javax.swing.JButton;
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

public class CidadePanel extends JPanel {

    private final AppContext context;
    private final Runnable atualizarTelasCallback;

    private JTextField txtId;
    private JTextField txtNome;
    private JTextField txtEstado;
    private JTextField txtDistancia;
    private JTextArea txtCadastroIA;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public CidadePanel(AppContext context, Runnable atualizarTelasCallback) {
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
        txtCadastroIA.setText("Cadastre a cidade de Caruaru em PE, distância de 135 km da capital.");

        JButton btnPreencherIA = new JButton("Preencher com IA");
        btnPreencherIA.addActionListener(e -> preencherComIA());

        JPanel painelIA = new JPanel(new BorderLayout(5, 5));
        painelIA.setBorder(BorderFactory.createTitledBorder("Cadastro Rápido por IA"));
        painelIA.add(new JScrollPane(txtCadastroIA), BorderLayout.CENTER);
        painelIA.add(btnPreencherIA, BorderLayout.EAST);

        return painelIA;
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel(new GridLayout(2, 4, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados da Cidade"));

        txtId = new JTextField();
        txtNome = new JTextField();
        txtEstado = new JTextField();
        txtDistancia = new JTextField();

        SwingHelper.adicionarCampo(formulario, "ID", txtId);
        SwingHelper.adicionarCampo(formulario, "Nome", txtNome);
        SwingHelper.adicionarCampo(formulario, "Estado UF", txtEstado);
        SwingHelper.adicionarCampo(formulario, "Distância da capital", txtDistancia);

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
                new Object[]{"ID", "Nome", "Estado", "Distância da capital"},
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
                    .extrairDadosCadastro(txtCadastroIA.getText(), "cidade");

            txtId.setText(String.valueOf(context.getCidadeController().gerarProximoId()));
            txtNome.setText(dados.getOrDefault("nome", ""));
            txtEstado.setText(dados.getOrDefault("estado", "PE"));
            txtDistancia.setText(dados.getOrDefault("distancia", ""));

            SwingHelper.mostrarInfo(this, "Campos da cidade preenchidos pela IA. Revise antes de salvar.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, "Erro ao preencher cidade com IA: " + e.getMessage());
        }
    }

    private void salvar() {
        try {
            Cidade cidade = criarCidadeDoFormulario();

            context.getCidadeController().salvar(cidade);

            atualizarTelasCallback.run();
            limparFormulario();

            SwingHelper.mostrarInfo(this, "Cidade salva com sucesso.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private void atualizar() {
        try {
            Cidade cidade = criarCidadeDoFormulario();

            context.getCidadeController().atualizar(cidade);

            atualizarTelasCallback.run();

            SwingHelper.mostrarInfo(this, "Cidade atualizada com sucesso.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private void excluir() {
        try {
            int id = SwingHelper.lerInteiro(txtId, "ID da cidade");

            context.getCidadeController().remover(id);

            atualizarTelasCallback.run();
            limparFormulario();

            SwingHelper.mostrarInfo(this, "Cidade excluída com sucesso.");
        } catch (Exception e) {
            SwingHelper.mostrarErro(this, e.getMessage());
        }
    }

    private Cidade criarCidadeDoFormulario() throws Exception {
        int id = SwingHelper.lerInteiro(txtId, "ID");
        String nome = txtNome.getText();
        String estado = txtEstado.getText().trim().toUpperCase();
        double distancia = SwingHelper.lerDouble(txtDistancia, "distância da capital");

        return new Cidade(id, nome, estado, distancia);
    }

    private void selecionarDaTabela() {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            return;
        }

        Object valorId = modeloTabela.getValueAt(linha, 0);
        int id = Integer.parseInt(valorId.toString());

        Cidade cidade = context.getCidadeController().buscarPorId(id);

        if (cidade == null) {
            return;
        }

        txtId.setText(String.valueOf(cidade.getId()));
        txtNome.setText(cidade.getNome());
        txtEstado.setText(cidade.getEstado());
        txtDistancia.setText(String.valueOf(cidade.getDistanciaDaCapital()));
    }

    public void atualizarTabela() {
        modeloTabela.setRowCount(0);

        for (Cidade cidade : context.getCidadeController().listarTodas()) {
            modeloTabela.addRow(new Object[]{
                    cidade.getId(),
                    cidade.getNome(),
                    cidade.getEstado(),
                    cidade.getDistanciaDaCapital()
            });
        }
    }

    private void limparFormulario() {
        txtId.setText(String.valueOf(context.getCidadeController().gerarProximoId()));
        txtNome.setText("");
        txtEstado.setText("PE");
        txtDistancia.setText("");

        if (tabela != null) {
            tabela.clearSelection();
        }
    }
}