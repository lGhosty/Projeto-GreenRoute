package view;

import controller.CidadeController;
import controller.EletropostoController;
import controller.RouteController;
import controller.VeiculoController;
import model.Cidade;
import model.Eletroposto;
import model.Veiculo;
import model.VeiculoEletrico;
import model.VeiculoHibrido;
import repository.CidadeRepository;
import repository.EletropostoRepository;
import repository.VeiculoRepository;
import service.GoogleMapsService;
import service.IAPlannerService;
import service.GeminiIAPlannerService;
import service.MockIAPlannerService;
import util.TextoIAUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;


public class MainFrame extends JFrame {

    private final CidadeRepository cidadeRepository = new CidadeRepository();
    private final VeiculoRepository veiculoRepository = new VeiculoRepository();
    private final EletropostoRepository eletropostoRepository = new EletropostoRepository();

    private final CidadeController cidadeController = new CidadeController(cidadeRepository);
    private final VeiculoController veiculoController = new VeiculoController(veiculoRepository);
    private final EletropostoController eletropostoController = new EletropostoController(eletropostoRepository, cidadeRepository);

    private final IAPlannerService iaPlannerService = criarServicoIA();

    private final RouteController routeController = new RouteController(
            veiculoRepository,
            cidadeRepository,
            eletropostoRepository,
            iaPlannerService
    );

    private final GoogleMapsService googleMapsService = new GoogleMapsService();

    private JTextField txtVeiculoId;
    private JTextField txtVeiculoModelo;
    private JComboBox<String> comboVeiculoTipo;
    private JTextField txtVeiculoAutonomia;
    private JTextField txtVeiculoBateria;
    private JTextField txtVeiculoConsumo;
    private JTextField txtVeiculoTempoCompleto;
    private JTextField txtVeiculoConector;
    private JTextField txtVeiculoTempoRapido;
    private JTextField txtVeiculoTanque;
    private JTextField txtVeiculoConsumoCombustivel;
    private JTextField txtVeiculoCombustivel;
    private JTextArea txtVeiculoIA;
    private JTable tabelaVeiculos;
    private DefaultTableModel modeloTabelaVeiculos;

    private JTextField txtCidadeId;
    private JTextField txtCidadeNome;
    private JTextField txtCidadeEstado;
    private JTextField txtCidadeDistancia;
    private JTextArea txtCidadeIA;
    private JTable tabelaCidades;
    private DefaultTableModel modeloTabelaCidades;

    private JTextField txtEletropostoId;
    private JTextField txtEletropostoNome;
    private JTextField txtEletropostoLocalizacao;
    private JComboBox<Cidade> comboEletropostoCidade;
    private JTextField txtEletropostoConectores;
    private JTextField txtEletropostoPotencia;
    private JTextField txtEletropostoPreco;
    private JTextField txtEletropostoVagas;
    private JTextArea txtEletropostoIA;
    private JTable tabelaEletropostos;
    private DefaultTableModel modeloTabelaEletropostos;

    private JComboBox<Veiculo> comboRotaVeiculo;
    private JComboBox<Cidade> comboRotaCidade;
    private JTextArea txtResultadoRota;

    private IAPlannerService criarServicoIA() {
        try {
            return new GeminiIAPlannerService();
        } catch (Exception e) {
            System.out.println("Gemini não inicializado. Usando IA simulada. Motivo: " + e.getMessage());
            return new MockIAPlannerService();
        }
    }

    public MainFrame() {
        setTitle("GreenRoute - Módulo 2");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane abas = new JTabbedPane();

        abas.addTab("Veículos", criarPainelVeiculos());
        abas.addTab("Cidades", criarPainelCidades());
        abas.addTab("Eletropostos", criarPainelEletropostos());
        abas.addTab("Planejador IA", criarPainelRotas());

        add(abas, BorderLayout.CENTER);

        carregarDadosExemplo();
        atualizarTelas();
    }

    private JPanel criarPainelVeiculos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtVeiculoIA = new JTextArea(3, 30);
        txtVeiculoIA.setLineWrap(true);
        txtVeiculoIA.setWrapStyleWord(true);
        txtVeiculoIA.setText("Cadastra o carro elétrico BYD Dolphin, autonomia de 400km, bateria em 85%, consumo de 0.15 kWh/km e leva 420 minutos para carregar totalmente. O conector dele é CCS2.");

        JButton btnPreencherIA = new JButton("Preencher com IA");
        btnPreencherIA.addActionListener(e -> preencherVeiculoComIA());

        JPanel painelIA = new JPanel(new BorderLayout(5, 5));
        painelIA.setBorder(BorderFactory.createTitledBorder("Cadastro Rápido por IA"));
        painelIA.add(new JScrollPane(txtVeiculoIA), BorderLayout.CENTER);
        painelIA.add(btnPreencherIA, BorderLayout.EAST);

        JPanel formulario = new JPanel(new GridLayout(6, 4, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados do Veículo"));

        txtVeiculoId = new JTextField();
        txtVeiculoModelo = new JTextField();
        comboVeiculoTipo = new JComboBox<>(new String[]{"Elétrico", "Híbrido"});
        txtVeiculoAutonomia = new JTextField();
        txtVeiculoBateria = new JTextField();
        txtVeiculoConsumo = new JTextField();
        txtVeiculoTempoCompleto = new JTextField();
        txtVeiculoConector = new JTextField();
        txtVeiculoTempoRapido = new JTextField();
        txtVeiculoTanque = new JTextField("45");
        txtVeiculoConsumoCombustivel = new JTextField("14");
        txtVeiculoCombustivel = new JTextField("Gasolina");

        adicionarCampo(formulario, "ID", txtVeiculoId);
        adicionarCampo(formulario, "Modelo", txtVeiculoModelo);
        adicionarCampo(formulario, "Tipo", comboVeiculoTipo);
        adicionarCampo(formulario, "Autonomia máxima", txtVeiculoAutonomia);
        adicionarCampo(formulario, "Bateria atual %", txtVeiculoBateria);
        adicionarCampo(formulario, "Consumo kWh/km", txtVeiculoConsumo);
        adicionarCampo(formulario, "Recarga completa min", txtVeiculoTempoCompleto);
        adicionarCampo(formulario, "Conector", txtVeiculoConector);
        adicionarCampo(formulario, "Recarga rápida min", txtVeiculoTempoRapido);
        adicionarCampo(formulario, "Tanque L", txtVeiculoTanque);
        adicionarCampo(formulario, "Consumo km/L", txtVeiculoConsumoCombustivel);
        adicionarCampo(formulario, "Combustível", txtVeiculoCombustivel);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");

        btnNovo.addActionListener(e -> limparFormularioVeiculo());
        btnSalvar.addActionListener(e -> salvarVeiculo());
        btnAtualizar.addActionListener(e -> atualizarVeiculo());
        btnExcluir.addActionListener(e -> excluirVeiculo());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);

        modeloTabelaVeiculos = new DefaultTableModel(
                new Object[]{"ID", "Modelo", "Tipo", "Autonomia", "Bateria", "Autonomia Atual", "Conector/Combustível"}, 0
        );

        tabelaVeiculos = new JTable(modeloTabelaVeiculos);
        tabelaVeiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVeiculos.getSelectionModel().addListSelectionListener(e -> selecionarVeiculoDaTabela());

        JPanel topo = new JPanel(new BorderLayout(5, 5));
        topo.add(painelIA, BorderLayout.NORTH);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaVeiculos), BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelCidades() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtCidadeIA = new JTextArea(3, 30);
        txtCidadeIA.setLineWrap(true);
        txtCidadeIA.setWrapStyleWord(true);
        txtCidadeIA.setText("Cadastre a cidade de Caruaru em PE, distância de 135 km da capital.");

        JButton btnPreencherIA = new JButton("Preencher com IA");
        btnPreencherIA.addActionListener(e -> preencherCidadeComIA());

        JPanel painelIA = new JPanel(new BorderLayout(5, 5));
        painelIA.setBorder(BorderFactory.createTitledBorder("Cadastro Rápido por IA"));
        painelIA.add(new JScrollPane(txtCidadeIA), BorderLayout.CENTER);
        painelIA.add(btnPreencherIA, BorderLayout.EAST);

        JPanel formulario = new JPanel(new GridLayout(2, 4, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados da Cidade"));

        txtCidadeId = new JTextField();
        txtCidadeNome = new JTextField();
        txtCidadeEstado = new JTextField();
        txtCidadeDistancia = new JTextField();

        adicionarCampo(formulario, "ID", txtCidadeId);
        adicionarCampo(formulario, "Nome", txtCidadeNome);
        adicionarCampo(formulario, "Estado UF", txtCidadeEstado);
        adicionarCampo(formulario, "Distância da capital", txtCidadeDistancia);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");

        btnNovo.addActionListener(e -> limparFormularioCidade());
        btnSalvar.addActionListener(e -> salvarCidade());
        btnAtualizar.addActionListener(e -> atualizarCidade());
        btnExcluir.addActionListener(e -> excluirCidade());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);

        modeloTabelaCidades = new DefaultTableModel(new Object[]{"ID", "Nome", "Estado", "Distância da capital"}, 0);

        tabelaCidades = new JTable(modeloTabelaCidades);
        tabelaCidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaCidades.getSelectionModel().addListSelectionListener(e -> selecionarCidadeDaTabela());

        JPanel topo = new JPanel(new BorderLayout(5, 5));
        topo.add(painelIA, BorderLayout.NORTH);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaCidades), BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelEletropostos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtEletropostoIA = new JTextArea(3, 30);
        txtEletropostoIA.setLineWrap(true);
        txtEletropostoIA.setWrapStyleWord(true);
        txtEletropostoIA.setText("Cadastre um eletroposto no shopping com conector CCS2, potência de 50 kW, preço de 2.20 por kWh e 2 vagas disponíveis.");

        JButton btnPreencherIA = new JButton("Preencher com IA");
        btnPreencherIA.addActionListener(e -> preencherEletropostoComIA());

        JPanel painelIA = new JPanel(new BorderLayout(5, 5));
        painelIA.setBorder(BorderFactory.createTitledBorder("Cadastro Rápido por IA"));
        painelIA.add(new JScrollPane(txtEletropostoIA), BorderLayout.CENTER);
        painelIA.add(btnPreencherIA, BorderLayout.EAST);

        JPanel formulario = new JPanel(new GridLayout(4, 4, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados do Eletroposto"));

        txtEletropostoId = new JTextField();
        txtEletropostoNome = new JTextField();
        txtEletropostoLocalizacao = new JTextField();
        comboEletropostoCidade = new JComboBox<>();
        txtEletropostoConectores = new JTextField();
        txtEletropostoPotencia = new JTextField();
        txtEletropostoPreco = new JTextField();
        txtEletropostoVagas = new JTextField();

        adicionarCampo(formulario, "ID", txtEletropostoId);
        adicionarCampo(formulario, "Nome", txtEletropostoNome);
        adicionarCampo(formulario, "Localização", txtEletropostoLocalizacao);
        adicionarCampo(formulario, "Cidade", comboEletropostoCidade);
        adicionarCampo(formulario, "Conectores", txtEletropostoConectores);
        adicionarCampo(formulario, "Potência kW", txtEletropostoPotencia);
        adicionarCampo(formulario, "Preço kWh", txtEletropostoPreco);
        adicionarCampo(formulario, "Vagas", txtEletropostoVagas);

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAbrirMaps = new JButton("Abrir localização no Maps");

        btnNovo.addActionListener(e -> limparFormularioEletroposto());
        btnSalvar.addActionListener(e -> salvarEletroposto());
        btnAtualizar.addActionListener(e -> atualizarEletroposto());
        btnExcluir.addActionListener(e -> excluirEletroposto());
        btnAbrirMaps.addActionListener(e -> abrirLocalizacaoEletropostoNoMaps());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);
        botoes.add(btnAbrirMaps);

        modeloTabelaEletropostos = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Localização", "Cidade ID", "Conectores", "Potência", "Preço", "Vagas"}, 0
        );

        tabelaEletropostos = new JTable(modeloTabelaEletropostos);
        tabelaEletropostos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaEletropostos.getSelectionModel().addListSelectionListener(e -> selecionarEletropostoDaTabela());

        JPanel topo = new JPanel(new BorderLayout(5, 5));
        topo.add(painelIA, BorderLayout.NORTH);
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaEletropostos), BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelRotas() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(2, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Planejador de Rotas Inteligente"));

        comboRotaVeiculo = new JComboBox<>();
        comboRotaCidade = new JComboBox<>();

        adicionarCampo(formulario, "Veículo", comboRotaVeiculo);
        adicionarCampo(formulario, "Cidade de destino", comboRotaCidade);

        JButton btnPlanejar = new JButton("Planejar com IA");
        JButton btnRotaMaps = new JButton("Abrir rota no Google Maps");
        JButton btnBuscarPostos = new JButton("Buscar eletropostos no Maps");

        btnPlanejar.addActionListener(e -> planejarRotaComIA());
        btnRotaMaps.addActionListener(e -> abrirRotaNoMaps());
        btnBuscarPostos.addActionListener(e -> buscarEletropostosNoMaps());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnPlanejar);
        botoes.add(btnRotaMaps);
        botoes.add(btnBuscarPostos);

        txtResultadoRota = new JTextArea();
        txtResultadoRota.setLineWrap(true);
        txtResultadoRota.setWrapStyleWord(true);
        txtResultadoRota.setEditable(false);

        JPanel topo = new JPanel(new BorderLayout(5, 5));
        topo.add(formulario, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(txtResultadoRota), BorderLayout.CENTER);

        return painel;
    }

    private void preencherVeiculoComIA() {
        try {
            Map<String, String> dados = iaPlannerService.extrairDadosCadastro(txtVeiculoIA.getText(), "veiculo");

            txtVeiculoId.setText(String.valueOf(veiculoController.gerarProximoId()));
            txtVeiculoModelo.setText(dados.getOrDefault("modelo", ""));
            comboVeiculoTipo.setSelectedItem(dados.getOrDefault("tipo", "Elétrico"));
            txtVeiculoAutonomia.setText(dados.getOrDefault("autonomia", ""));
            txtVeiculoBateria.setText(dados.getOrDefault("bateria", ""));
            txtVeiculoConsumo.setText(dados.getOrDefault("consumo", ""));
            txtVeiculoTempoCompleto.setText(dados.getOrDefault("tempoCompleto", ""));
            txtVeiculoConector.setText(dados.getOrDefault("conector", ""));
            txtVeiculoTempoRapido.setText(dados.getOrDefault("tempoRapido", "60"));
            txtVeiculoTanque.setText(dados.getOrDefault("capacidadeTanque", "45"));
            txtVeiculoConsumoCombustivel.setText(dados.getOrDefault("consumoCombustivel", "14"));
            txtVeiculoCombustivel.setText(dados.getOrDefault("tipoCombustivel", "Gasolina"));

            mostrarInfo("Campos do veículo preenchidos pela IA. Revise antes de salvar.");
        } catch (Exception e) {
            mostrarErro("Erro ao preencher veículo com IA: " + e.getMessage());
        }
    }

    private void preencherCidadeComIA() {
        try {
            Map<String, String> dados = iaPlannerService.extrairDadosCadastro(txtCidadeIA.getText(), "cidade");

            txtCidadeId.setText(String.valueOf(cidadeController.gerarProximoId()));
            txtCidadeNome.setText(dados.getOrDefault("nome", ""));
            txtCidadeEstado.setText(dados.getOrDefault("estado", "PE"));
            txtCidadeDistancia.setText(dados.getOrDefault("distancia", ""));

            mostrarInfo("Campos da cidade preenchidos pela IA. Revise antes de salvar.");
        } catch (Exception e) {
            mostrarErro("Erro ao preencher cidade com IA: " + e.getMessage());
        }
    }

    private void preencherEletropostoComIA() {
        try {
            Map<String, String> dados = iaPlannerService.extrairDadosCadastro(txtEletropostoIA.getText(), "eletroposto");

            txtEletropostoId.setText(String.valueOf(eletropostoController.gerarProximoId()));
            txtEletropostoNome.setText(dados.getOrDefault("nome", ""));
            txtEletropostoLocalizacao.setText(dados.getOrDefault("localizacao", ""));
            txtEletropostoConectores.setText(dados.getOrDefault("conectores", "CCS2"));
            txtEletropostoPotencia.setText(dados.getOrDefault("potencia", "50"));
            txtEletropostoPreco.setText(dados.getOrDefault("preco", "2.20"));
            txtEletropostoVagas.setText(dados.getOrDefault("vagas", "2"));

            if (comboEletropostoCidade.getItemCount() > 0) {
                comboEletropostoCidade.setSelectedIndex(0);
            }

            mostrarInfo("Campos do eletroposto preenchidos pela IA. Revise antes de salvar.");
        } catch (Exception e) {
            mostrarErro("Erro ao preencher eletroposto com IA: " + e.getMessage());
        }
    }

    private void salvarVeiculo() {
        try {
            veiculoController.salvar(criarVeiculoDoFormulario());
            atualizarTelas();
            limparFormularioVeiculo();
            mostrarInfo("Veículo salvo com sucesso.");
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void atualizarVeiculo() {
        try {
            veiculoController.atualizar(criarVeiculoDoFormulario());
            atualizarTelas();
            mostrarInfo("Veículo atualizado com sucesso.");
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void excluirVeiculo() {
        try {
            int id = lerInteiro(txtVeiculoId, "ID do veículo");
            veiculoController.remover(id);
            atualizarTelas();
            limparFormularioVeiculo();
            mostrarInfo("Veículo excluído com sucesso.");
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private Veiculo criarVeiculoDoFormulario() throws Exception {
        int id = lerInteiro(txtVeiculoId, "ID");
        String modelo = txtVeiculoModelo.getText();
        double autonomia = lerDouble(txtVeiculoAutonomia, "autonomia");
        double bateria = lerDouble(txtVeiculoBateria, "bateria");
        double consumo = lerDouble(txtVeiculoConsumo, "consumo kWh/km");
        int tempoCompleto = lerInteiro(txtVeiculoTempoCompleto, "tempo de recarga completa");
        String tipo = (String) comboVeiculoTipo.getSelectedItem();

        if ("Híbrido".equals(tipo)) {
            double tanque = lerDouble(txtVeiculoTanque, "tanque");
            double consumoCombustivel = lerDouble(txtVeiculoConsumoCombustivel, "consumo de combustível");
            String combustivel = txtVeiculoCombustivel.getText();

            return new VeiculoHibrido(
                    id, modelo, autonomia, bateria, consumo, tempoCompleto,
                    tanque, consumoCombustivel, combustivel
            );
        }

        String conector = txtVeiculoConector.getText();
        int tempoRapido = lerInteiro(txtVeiculoTempoRapido, "tempo de recarga rápida");

        return new VeiculoEletrico(
                id, modelo, autonomia, bateria, consumo, tempoCompleto,
                conector, tempoRapido
        );
    }

    private void salvarCidade() {
        try {
            cidadeController.salvar(criarCidadeDoFormulario());
            atualizarTelas();
            limparFormularioCidade();
            mostrarInfo("Cidade salva com sucesso.");
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void atualizarCidade() {
        try {
            cidadeController.atualizar(criarCidadeDoFormulario());
            atualizarTelas();
            mostrarInfo("Cidade atualizada com sucesso.");
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void excluirCidade() {
        try {
            int id = lerInteiro(txtCidadeId, "ID da cidade");
            cidadeController.remover(id);
            atualizarTelas();
            limparFormularioCidade();
            mostrarInfo("Cidade excluída com sucesso.");
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private Cidade criarCidadeDoFormulario() throws Exception {
        int id = lerInteiro(txtCidadeId, "ID");
        String nome = txtCidadeNome.getText();
        String estado = txtCidadeEstado.getText().trim().toUpperCase();
        double distancia = lerDouble(txtCidadeDistancia, "distância da capital");

        return new Cidade(id, nome, estado, distancia);
    }

    private void salvarEletroposto() {
        try {
            eletropostoController.salvar(criarEletropostoDoFormulario());
            atualizarTelas();
            limparFormularioEletroposto();
            mostrarInfo("Eletroposto salvo com sucesso.");
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void atualizarEletroposto() {
        try {
            eletropostoController.atualizar(criarEletropostoDoFormulario());
            atualizarTelas();
            mostrarInfo("Eletroposto atualizado com sucesso.");
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void excluirEletroposto() {
        try {
            int id = lerInteiro(txtEletropostoId, "ID do eletroposto");
            eletropostoController.remover(id);
            atualizarTelas();
            limparFormularioEletroposto();
            mostrarInfo("Eletroposto excluído com sucesso.");
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private Eletroposto criarEletropostoDoFormulario() throws Exception {
        Cidade cidade = (Cidade) comboEletropostoCidade.getSelectedItem();

        if (cidade == null) {
            throw new Exception("Cadastre uma cidade antes de cadastrar eletroposto.");
        }

        int id = lerInteiro(txtEletropostoId, "ID");
        String nome = txtEletropostoNome.getText();
        String localizacao = txtEletropostoLocalizacao.getText();
        String conectores = txtEletropostoConectores.getText();
        double potencia = lerDouble(txtEletropostoPotencia, "potência");
        double preco = lerDouble(txtEletropostoPreco, "preço");
        int vagas = lerInteiro(txtEletropostoVagas, "vagas");

        return new Eletroposto(id, nome, localizacao, cidade.getId(), conectores, potencia, preco, vagas);
    }

    private void planejarRotaComIA() {
        try {
            Veiculo veiculo = (Veiculo) comboRotaVeiculo.getSelectedItem();
            Cidade cidade = (Cidade) comboRotaCidade.getSelectedItem();

            if (veiculo == null || cidade == null) {
                mostrarErro("Cadastre e selecione um veículo e uma cidade.");
                return;
            }

            String resposta = routeController.planejarRotaComIA(veiculo.getId(), cidade.getId());

            txtResultadoRota.setText(resposta);
        } catch (Exception e) {
            mostrarErro(e.getMessage());
        }
    }

    private void abrirRotaNoMaps() {
        try {
            Cidade cidade = (Cidade) comboRotaCidade.getSelectedItem();

            if (cidade == null) {
                mostrarErro("Selecione uma cidade.");
                return;
            }

            googleMapsService.abrirRota("Recife PE", cidade.getNome() + " " + cidade.getEstado());
        } catch (Exception e) {
            mostrarErro("Erro ao abrir Google Maps: " + e.getMessage());
        }
    }

    private void buscarEletropostosNoMaps() {
        try {
            Cidade cidade = (Cidade) comboRotaCidade.getSelectedItem();

            if (cidade == null) {
                mostrarErro("Selecione uma cidade.");
                return;
            }

            googleMapsService.abrirPesquisaEletropostos(cidade.getNome(), cidade.getEstado());
        } catch (Exception e) {
            mostrarErro("Erro ao buscar eletropostos no Maps: " + e.getMessage());
        }
    }

    private void abrirLocalizacaoEletropostoNoMaps() {
        try {
            googleMapsService.abrirLocalizacao(txtEletropostoLocalizacao.getText());
        } catch (Exception e) {
            mostrarErro("Erro ao abrir localização: " + e.getMessage());
        }
    }

    private void selecionarVeiculoDaTabela() {
        int linha = tabelaVeiculos.getSelectedRow();

        if (linha < 0) {
            return;
        }

        int id = (int) modeloTabelaVeiculos.getValueAt(linha, 0);
        Veiculo veiculo = veiculoController.buscarPorId(id);

        if (veiculo == null) {
            return;
        }

        txtVeiculoId.setText(String.valueOf(veiculo.getId()));
        txtVeiculoModelo.setText(veiculo.getModelo());
        comboVeiculoTipo.setSelectedItem(veiculo.getTipo());
        txtVeiculoAutonomia.setText(String.valueOf(veiculo.getAutonomiaMaxima()));
        txtVeiculoBateria.setText(String.valueOf(veiculo.getCargaBateriaAtual()));
        txtVeiculoConsumo.setText(String.valueOf(veiculo.getConsumoKwhPorKm()));
        txtVeiculoTempoCompleto.setText(String.valueOf(veiculo.getTempoRecargaCompleta()));

        if (veiculo instanceof VeiculoEletrico) {
            VeiculoEletrico eletrico = (VeiculoEletrico) veiculo;

            txtVeiculoConector.setText(eletrico.getTipoConector());
            txtVeiculoTempoRapido.setText(String.valueOf(eletrico.getTempoRecargaRapida()));
        }

        if (veiculo instanceof VeiculoHibrido) {
            VeiculoHibrido hibrido = (VeiculoHibrido) veiculo;

            txtVeiculoTanque.setText(String.valueOf(hibrido.getCapacidadeTanqueCombustivel()));
            txtVeiculoConsumoCombustivel.setText(String.valueOf(hibrido.getConsumoCombustivel()));
            txtVeiculoCombustivel.setText(hibrido.getTipoCombustivel());
        }
    }

    private void selecionarCidadeDaTabela() {
        int linha = tabelaCidades.getSelectedRow();

        if (linha < 0) {
            return;
        }

        int id = (int) modeloTabelaCidades.getValueAt(linha, 0);
        Cidade cidade = cidadeController.buscarPorId(id);

        if (cidade == null) {
            return;
        }

        txtCidadeId.setText(String.valueOf(cidade.getId()));
        txtCidadeNome.setText(cidade.getNome());
        txtCidadeEstado.setText(cidade.getEstado());
        txtCidadeDistancia.setText(String.valueOf(cidade.getDistanciaDaCapital()));
    }

    private void selecionarEletropostoDaTabela() {
        int linha = tabelaEletropostos.getSelectedRow();

        if (linha < 0) {
            return;
        }

        int id = (int) modeloTabelaEletropostos.getValueAt(linha, 0);
        Eletroposto eletroposto = eletropostoController.buscarPorId(id);

        if (eletroposto == null) {
            return;
        }

        txtEletropostoId.setText(String.valueOf(eletroposto.getId()));
        txtEletropostoNome.setText(eletroposto.getNome());
        txtEletropostoLocalizacao.setText(eletroposto.getLocalizacao());
        selecionarCidadeNoCombo(comboEletropostoCidade, eletroposto.getCidadeId());
        txtEletropostoConectores.setText(eletroposto.getTiposConectoresDisponiveis());
        txtEletropostoPotencia.setText(String.valueOf(eletroposto.getPotenciaCargaKw()));
        txtEletropostoPreco.setText(String.valueOf(eletroposto.getPrecoPorKwh()));
        txtEletropostoVagas.setText(String.valueOf(eletroposto.getVagasDisponiveis()));
    }

    private void atualizarTelas() {
        atualizarTabelaVeiculos();
        atualizarTabelaCidades();
        atualizarTabelaEletropostos();
        atualizarCombos();
    }

    private void atualizarTabelaVeiculos() {
        modeloTabelaVeiculos.setRowCount(0);

        for (Veiculo veiculo : veiculoController.listarTodos()) {
            String detalhe = "";

            if (veiculo instanceof VeiculoEletrico) {
                detalhe = ((VeiculoEletrico) veiculo).getTipoConector();
            } else if (veiculo instanceof VeiculoHibrido) {
                detalhe = ((VeiculoHibrido) veiculo).getTipoCombustivel();
            }

            modeloTabelaVeiculos.addRow(new Object[]{
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

    private void atualizarTabelaCidades() {
        modeloTabelaCidades.setRowCount(0);

        for (Cidade cidade : cidadeController.listarTodas()) {
            modeloTabelaCidades.addRow(new Object[]{
                    cidade.getId(),
                    cidade.getNome(),
                    cidade.getEstado(),
                    cidade.getDistanciaDaCapital()
            });
        }
    }

    private void atualizarTabelaEletropostos() {
        modeloTabelaEletropostos.setRowCount(0);

        for (Eletroposto eletroposto : eletropostoController.listarTodos()) {
            modeloTabelaEletropostos.addRow(new Object[]{
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

    private void atualizarCombos() {
        Cidade cidadeEletropostoSelecionada = (Cidade) comboEletropostoCidade.getSelectedItem();
        Cidade cidadeRotaSelecionada = (Cidade) comboRotaCidade.getSelectedItem();
        Veiculo veiculoRotaSelecionado = (Veiculo) comboRotaVeiculo.getSelectedItem();

        comboEletropostoCidade.removeAllItems();
        comboRotaCidade.removeAllItems();
        comboRotaVeiculo.removeAllItems();

        for (Cidade cidade : cidadeController.listarTodas()) {
            comboEletropostoCidade.addItem(cidade);
            comboRotaCidade.addItem(cidade);
        }

        for (Veiculo veiculo : veiculoController.listarTodos()) {
            comboRotaVeiculo.addItem(veiculo);
        }

        if (cidadeEletropostoSelecionada != null) {
            selecionarCidadeNoCombo(comboEletropostoCidade, cidadeEletropostoSelecionada.getId());
        }

        if (cidadeRotaSelecionada != null) {
            selecionarCidadeNoCombo(comboRotaCidade, cidadeRotaSelecionada.getId());
        }

        if (veiculoRotaSelecionado != null) {
            selecionarVeiculoNoCombo(comboRotaVeiculo, veiculoRotaSelecionado.getId());
        }
    }

    private void selecionarCidadeNoCombo(JComboBox<Cidade> combo, int cidadeId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Cidade cidade = combo.getItemAt(i);

            if (cidade.getId() == cidadeId) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selecionarVeiculoNoCombo(JComboBox<Veiculo> combo, int veiculoId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Veiculo veiculo = combo.getItemAt(i);

            if (veiculo.getId() == veiculoId) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void limparFormularioVeiculo() {
        txtVeiculoId.setText(String.valueOf(veiculoController.gerarProximoId()));
        txtVeiculoModelo.setText("");
        comboVeiculoTipo.setSelectedItem("Elétrico");
        txtVeiculoAutonomia.setText("");
        txtVeiculoBateria.setText("");
        txtVeiculoConsumo.setText("");
        txtVeiculoTempoCompleto.setText("");
        txtVeiculoConector.setText("");
        txtVeiculoTempoRapido.setText("");
        txtVeiculoTanque.setText("45");
        txtVeiculoConsumoCombustivel.setText("14");
        txtVeiculoCombustivel.setText("Gasolina");
        tabelaVeiculos.clearSelection();
    }

    private void limparFormularioCidade() {
        txtCidadeId.setText(String.valueOf(cidadeController.gerarProximoId()));
        txtCidadeNome.setText("");
        txtCidadeEstado.setText("PE");
        txtCidadeDistancia.setText("");
        tabelaCidades.clearSelection();
    }

    private void limparFormularioEletroposto() {
        txtEletropostoId.setText(String.valueOf(eletropostoController.gerarProximoId()));
        txtEletropostoNome.setText("");
        txtEletropostoLocalizacao.setText("");

        if (comboEletropostoCidade.getItemCount() > 0) {
            comboEletropostoCidade.setSelectedIndex(0);
        }

        txtEletropostoConectores.setText("");
        txtEletropostoPotencia.setText("");
        txtEletropostoPreco.setText("");
        txtEletropostoVagas.setText("");
        tabelaEletropostos.clearSelection();
    }

    private void carregarDadosExemplo() {
        try {
            cidadeController.salvar(new Cidade(1, "Caruaru", "PE", 135));
            cidadeController.salvar(new Cidade(2, "Surubim", "PE", 120));

            veiculoController.salvar(new VeiculoEletrico(
                    1,
                    "BYD Dolphin",
                    400,
                    85,
                    0.15,
                    420,
                    "CCS2",
                    60
            ));

            veiculoController.salvar(new VeiculoHibrido(
                    2,
                    "Toyota Corolla Hybrid",
                    80,
                    60,
                    0.12,
                    240,
                    43,
                    18,
                    "Gasolina"
            ));

            eletropostoController.salvar(new Eletroposto(
                    1,
                    "Eletroposto Shopping",
                    "Shopping Difusora Caruaru PE",
                    1,
                    "CCS2, Tipo 2",
                    50,
                    2.20,
                    2
            ));
        } catch (Exception ignored) {
        }
    }

    private void adicionarCampo(JPanel painel, String label, JComponent componente) {
        painel.add(new JLabel(label));
        painel.add(componente);
    }

    private int lerInteiro(JTextField campo, String nomeCampo) throws Exception {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Informe um número inteiro válido para " + nomeCampo + ".");
        }
    }

    private double lerDouble(JTextField campo, String nomeCampo) throws Exception {
        try {
            return Double.parseDouble(campo.getText().replace(",", ".").trim());
        } catch (NumberFormatException e) {
            throw new Exception("Informe um número válido para " + nomeCampo + ".");
        }
    }

    private void mostrarInfo(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "GreenRoute", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}