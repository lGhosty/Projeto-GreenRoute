package controller;

import exception.ValidacaoException;
import model.Eletroposto;
import repository.CidadeRepository;
import repository.EletropostoRepository;

import java.util.ArrayList;

public class EletropostoController {

    private final EletropostoRepository repository;
    private final CidadeRepository cidadeRepository;

    public EletropostoController(EletropostoRepository repository, CidadeRepository cidadeRepository) {
        this.repository = repository;
        this.cidadeRepository = cidadeRepository;
    }

    public void salvar(Eletroposto eletroposto) throws ValidacaoException {
        validar(eletroposto);

        boolean salvou = repository.adicionar(eletroposto);

        if (!salvou) {
            throw new ValidacaoException("Já existe um eletroposto cadastrado com esse ID.");
        }
    }

    public void atualizar(Eletroposto eletroposto) throws ValidacaoException {
        validar(eletroposto);

        boolean atualizou = repository.atualizar(eletroposto);

        if (!atualizou) {
            throw new ValidacaoException("Eletroposto não encontrado para atualização.");
        }
    }

    public void remover(int id) throws ValidacaoException {
        boolean removeu = repository.remover(id);

        if (!removeu) {
            throw new ValidacaoException("Eletroposto não encontrado para exclusão.");
        }
    }

    public Eletroposto buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public ArrayList<Eletroposto> listarTodos() {
        return repository.listarTodos();
    }

    public ArrayList<Eletroposto> buscarPorCidadeId(int cidadeId) {
        return repository.buscarPorCidadeId(cidadeId);
    }

    public int gerarProximoId() {
        return repository.gerarProximoId();
    }

    private void validar(Eletroposto eletroposto) throws ValidacaoException {
        if (eletroposto == null) {
            throw new ValidacaoException("Eletroposto inválido.");
        }

        if (eletroposto.getId() <= 0) {
            throw new ValidacaoException("O ID do eletroposto deve ser maior que zero.");
        }

        if (eletroposto.getNome() == null || eletroposto.getNome().trim().isEmpty()) {
            throw new ValidacaoException("Informe o nome do eletroposto.");
        }

        if (eletroposto.getLocalizacao() == null || eletroposto.getLocalizacao().trim().isEmpty()) {
            throw new ValidacaoException("Informe a localização do eletroposto.");
        }

        if (cidadeRepository.buscarPorId(eletroposto.getCidadeId()) == null) {
            throw new ValidacaoException("Cidade não encontrada. Cadastre a cidade antes de cadastrar o eletroposto.");
        }

        if (eletroposto.getTiposConectoresDisponiveis() == null ||
                eletroposto.getTiposConectoresDisponiveis().trim().isEmpty()) {
            throw new ValidacaoException("Informe os conectores disponíveis no eletroposto.");
        }

        if (eletroposto.getPotenciaCargaKw() <= 0) {
            throw new ValidacaoException("A potência de carga deve ser maior que zero.");
        }

        if (eletroposto.getPrecoPorKwh() < 0) {
            throw new ValidacaoException("O preço por kWh não pode ser negativo.");
        }

        if (eletroposto.getVagasDisponiveis() < 0) {
            throw new ValidacaoException("A quantidade de vagas não pode ser negativa.");
        }
    }
}