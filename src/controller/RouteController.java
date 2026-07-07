package controller;

import exception.AutonomiaInsuficienteException;
import exception.ConectorIncompativelException;
import exception.ValidacaoException;
import model.Cidade;
import model.Eletroposto;
import model.Veiculo;
import model.VeiculoEletrico;
import model.VeiculoHibrido;
import repository.CidadeRepository;
import repository.EletropostoRepository;
import repository.VeiculoRepository;

import java.util.ArrayList;

public class RouteController {

    private final VeiculoRepository veiculoRepository;
    private final CidadeRepository cidadeRepository;
    private final EletropostoRepository eletropostoRepository;

    public RouteController(VeiculoRepository veiculoRepository,
                           CidadeRepository cidadeRepository,
                           EletropostoRepository eletropostoRepository) {
        this.veiculoRepository = veiculoRepository;
        this.cidadeRepository = cidadeRepository;
        this.eletropostoRepository = eletropostoRepository;
    }

    public String planejarRotaSimples(int veiculoId, int cidadeId)
            throws ValidacaoException, AutonomiaInsuficienteException, ConectorIncompativelException {

        Veiculo veiculo = veiculoRepository.buscarPorId(veiculoId);
        if (veiculo == null) {
            throw new ValidacaoException("Selecione um veículo válido.");
        }

        Cidade cidade = cidadeRepository.buscarPorId(cidadeId);
        if (cidade == null) {
            throw new ValidacaoException("Selecione uma cidade válida.");
        }

        double distancia = cidade.getDistanciaDaCapital();
        double autonomiaAtual = veiculo.getAutonomiaAtual();

        ArrayList<Eletroposto> postosDaCidade = eletropostoRepository.buscarPorCidadeId(cidadeId);

        if (distancia > autonomiaAtual && postosDaCidade.isEmpty()) {
            throw new AutonomiaInsuficienteException(
                    "A autonomia atual do veículo não é suficiente e não existem eletropostos cadastrados nessa cidade."
            );
        }

        Eletroposto postoCompativel = null;

        if (veiculo instanceof VeiculoEletrico) {
            VeiculoEletrico eletrico = (VeiculoEletrico) veiculo;

            for (Eletroposto posto : postosDaCidade) {
                if (eletrico.isCompativel(posto.getTiposConectoresDisponiveis())) {
                    postoCompativel = posto;
                    break;
                }
            }

            if (!postosDaCidade.isEmpty() && postoCompativel == null) {
                throw new ConectorIncompativelException(
                        "Existem eletropostos cadastrados, mas nenhum é compatível com o conector do veículo."
                );
            }
        }

        StringBuilder resultado = new StringBuilder();

        resultado.append("PLANEJAMENTO DE ROTA - GREENROUTE\n\n");
        resultado.append("Veículo: ").append(veiculo.getModelo()).append("\n");
        resultado.append("Tipo: ").append(veiculo.getTipo()).append("\n");
        resultado.append("Destino: ").append(cidade.getNome()).append("/").append(cidade.getEstado()).append("\n");
        resultado.append("Distância simulada: ").append(distancia).append(" km\n");
        resultado.append("Autonomia atual: ").append(String.format("%.1f", autonomiaAtual)).append(" km\n");
        resultado.append("Tempo de recarga completa: ").append(veiculo.getTempoRecargaCompleta()).append(" minutos\n\n");

        if (distancia <= autonomiaAtual) {
            resultado.append("Situação: a viagem pode ser realizada com a carga atual.\n");
        } else {
            resultado.append("Situação: será necessário planejar uma recarga durante ou ao final da viagem.\n");
        }

        if (postoCompativel != null) {
            resultado.append("Eletroposto recomendado: ").append(postoCompativel.getNome()).append("\n");
            resultado.append("Localização: ").append(postoCompativel.getLocalizacao()).append("\n");
            resultado.append("Conectores: ").append(postoCompativel.getTiposConectoresDisponiveis()).append("\n");
            resultado.append("Potência: ").append(postoCompativel.getPotenciaCargaKw()).append(" kW\n");
            resultado.append("Preço: R$ ").append(String.format("%.2f", postoCompativel.getPrecoPorKwh())).append("/kWh\n");
        } else {
            resultado.append("Eletroposto recomendado: sem parada obrigatória.\n");
        }

        if (veiculo instanceof VeiculoHibrido) {
            VeiculoHibrido hibrido = (VeiculoHibrido) veiculo;
            resultado.append("\nAutonomia adicional por combustão: ")
                    .append(String.format("%.1f", hibrido.getAutonomiaMotorCombustao()))
                    .append(" km\n");
        }

        resultado.append("\nObservação: esta é uma rota simples. Na próxima etapa, essa lógica será conectada à LLM.");

        return resultado.toString();
    }
}