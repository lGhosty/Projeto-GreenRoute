package controller;

import exception.ValidacaoException;
import model.Veiculo;
import model.VeiculoEletrico;
import model.VeiculoHibrido;
import repository.VeiculoRepository;

import java.util.ArrayList;

public class VeiculoController {

    private final VeiculoRepository repository;

    public VeiculoController(VeiculoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Veiculo veiculo) throws ValidacaoException {
        validar(veiculo);

        boolean salvou = repository.adicionar(veiculo);

        if (!salvou) {
            throw new ValidacaoException("Já existe um veículo cadastrado com esse ID.");
        }
    }

    public void atualizar(Veiculo veiculo) throws ValidacaoException {
        validar(veiculo);

        boolean atualizou = repository.atualizar(veiculo);

        if (!atualizou) {
            throw new ValidacaoException("Veículo não encontrado para atualização.");
        }
    }

    public void remover(int id) throws ValidacaoException {
        boolean removeu = repository.remover(id);

        if (!removeu) {
            throw new ValidacaoException("Veículo não encontrado para exclusão.");
        }
    }

    public Veiculo buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public ArrayList<Veiculo> listarTodos() {
        return repository.listarTodos();
    }

    public int gerarProximoId() {
        return repository.gerarProximoId();
    }

    private void validar(Veiculo veiculo) throws ValidacaoException {
        if (veiculo == null) {
            throw new ValidacaoException("Veículo inválido.");
        }

        if (veiculo.getId() <= 0) {
            throw new ValidacaoException("O ID do veículo deve ser maior que zero.");
        }

        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            throw new ValidacaoException("Informe o modelo do veículo.");
        }

        if (veiculo.getAutonomiaMaxima() <= 0) {
            throw new ValidacaoException("A autonomia máxima deve ser maior que zero.");
        }

        if (veiculo.getCargaBateriaAtual() < 0 || veiculo.getCargaBateriaAtual() > 100) {
            throw new ValidacaoException("A carga da bateria deve estar entre 0% e 100%.");
        }

        if (veiculo.getConsumoKwhPorKm() <= 0) {
            throw new ValidacaoException("O consumo em kWh/km deve ser maior que zero.");
        }

        if (veiculo.getTempoRecargaCompleta() <= 0) {
            throw new ValidacaoException("O tempo de recarga completa deve ser maior que zero.");
        }

        if (veiculo instanceof VeiculoEletrico) {
            validarEletrico((VeiculoEletrico) veiculo);
        }

        if (veiculo instanceof VeiculoHibrido) {
            validarHibrido((VeiculoHibrido) veiculo);
        }
    }

    private void validarEletrico(VeiculoEletrico veiculo) throws ValidacaoException {
        if (veiculo.getTipoConector() == null || veiculo.getTipoConector().trim().isEmpty()) {
            throw new ValidacaoException("Informe o tipo de conector do veículo elétrico.");
        }

        if (veiculo.getTempoRecargaRapida() <= 0) {
            throw new ValidacaoException("O tempo de recarga rápida deve ser maior que zero.");
        }
    }

    private void validarHibrido(VeiculoHibrido veiculo) throws ValidacaoException {
        if (veiculo.getCapacidadeTanqueCombustivel() <= 0) {
            throw new ValidacaoException("A capacidade do tanque deve ser maior que zero.");
        }

        if (veiculo.getConsumoCombustivel() <= 0) {
            throw new ValidacaoException("O consumo de combustível deve ser maior que zero.");
        }

        if (veiculo.getTipoCombustivel() == null || veiculo.getTipoCombustivel().trim().isEmpty()) {
            throw new ValidacaoException("Informe o tipo de combustível do veículo híbrido.");
        }
    }
}