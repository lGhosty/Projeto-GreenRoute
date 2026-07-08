package view;

import controller.CidadeController;
import controller.EletropostoController;
import controller.RouteController;
import controller.VeiculoController;
import model.Cidade;
import model.Eletroposto;
import model.VeiculoEletrico;
import model.VeiculoHibrido;
import repository.CidadeRepository;
import repository.EletropostoRepository;
import repository.VeiculoRepository;
import service.GeminiIAPlannerService;
import service.GoogleMapsService;
import service.IAPlannerService;
import service.IAPlannerServiceComFallback;
import service.MockIAPlannerService;

public class AppContext {

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

    private IAPlannerService criarServicoIA() {
        IAPlannerService mock = new MockIAPlannerService();

        try {
            IAPlannerService gemini = new GeminiIAPlannerService();
            return new IAPlannerServiceComFallback(gemini, mock);
        } catch (Exception e) {
            System.out.println("Gemini não inicializado. Usando IA simulada. Motivo: " + e.getMessage());
            return mock;
        }
    }

    public CidadeController getCidadeController() {
        return cidadeController;
    }

    public VeiculoController getVeiculoController() {
        return veiculoController;
    }

    public EletropostoController getEletropostoController() {
        return eletropostoController;
    }

    public RouteController getRouteController() {
        return routeController;
    }

    public IAPlannerService getIaPlannerService() {
        return iaPlannerService;
    }

    public GoogleMapsService getGoogleMapsService() {
        return googleMapsService;
    }

    public void carregarDadosExemplo() {
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
}