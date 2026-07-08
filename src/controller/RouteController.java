package controller;

import exception.AutonomiaInsuficienteException;
import exception.ConectorIncompativelException;
import exception.ValidacaoException;
import model.Cidade;
import model.Eletroposto;
import model.Veiculo;
import repository.CidadeRepository;
import repository.EletropostoRepository;
import repository.VeiculoRepository;
import service.IAPlannerService;

import java.util.ArrayList;

public class RouteController {

    private final VeiculoRepository veiculoRepository;
    private final CidadeRepository cidadeRepository;
    private final EletropostoRepository eletropostoRepository;
    private final IAPlannerService iaPlannerService;

    public RouteController(VeiculoRepository veiculoRepository,
                           CidadeRepository cidadeRepository,
                           EletropostoRepository eletropostoRepository,
                           IAPlannerService iaPlannerService) {
        this.veiculoRepository = veiculoRepository;
        this.cidadeRepository = cidadeRepository;
        this.eletropostoRepository = eletropostoRepository;
        this.iaPlannerService = iaPlannerService;
    }

    public String planejarRotaComIA(int veiculoId, int cidadeId)
            throws ValidacaoException, AutonomiaInsuficienteException, ConectorIncompativelException {

        Veiculo veiculo = veiculoRepository.buscarPorId(veiculoId);

        if (veiculo == null) {
            throw new ValidacaoException("Selecione um veículo válido.");
        }

        Cidade cidade = cidadeRepository.buscarPorId(cidadeId);

        if (cidade == null) {
            throw new ValidacaoException("Selecione uma cidade válida.");
        }

        ArrayList<Eletroposto> postos = eletropostoRepository.buscarPorCidadeId(cidadeId);

        return iaPlannerService.planejarRota(veiculo, cidade, postos);
    }
}