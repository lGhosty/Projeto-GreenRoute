package controller;

import exception.ValidacaoException;
import model.Cidade;
import repository.CidadeRepository;

import java.util.ArrayList;

public class CidadeController {

    private final CidadeRepository repository;

    public CidadeController(CidadeRepository repository) {
        this.repository = repository;
    }

    public void salvar(Cidade cidade) throws ValidacaoException {
        validar(cidade);

        boolean salvou = repository.adicionar(cidade);

        if (!salvou) {
            throw new ValidacaoException("Já existe uma cidade cadastrada com esse ID.");
        }
    }

    public void atualizar(Cidade cidade) throws ValidacaoException {
        validar(cidade);

        boolean atualizou = repository.atualizar(cidade);

        if (!atualizou) {
            throw new ValidacaoException("Cidade não encontrada para atualização.");
        }
    }

    public void remover(int id) throws ValidacaoException {
        boolean removeu = repository.remover(id);

        if (!removeu) {
            throw new ValidacaoException("Cidade não encontrada para exclusão.");
        }
    }

    public Cidade buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public ArrayList<Cidade> listarTodas() {
        return repository.listarTodos();
    }

    public int gerarProximoId() {
        return repository.gerarProximoId();
    }

    private void validar(Cidade cidade) throws ValidacaoException {
        if (cidade == null) {
            throw new ValidacaoException("Cidade inválida.");
        }

        if (cidade.getId() <= 0) {
            throw new ValidacaoException("O ID da cidade deve ser maior que zero.");
        }

        if (cidade.getNome() == null || cidade.getNome().trim().isEmpty()) {
            throw new ValidacaoException("Informe o nome da cidade.");
        }

        if (cidade.getEstado() == null || cidade.getEstado().trim().isEmpty()) {
            throw new ValidacaoException("Informe o estado da cidade.");
        }

        if (cidade.getEstado().trim().length() != 2) {
            throw new ValidacaoException("O estado deve ser informado no formato UF. Exemplo: PE.");
        }

        if (cidade.getDistanciaDaCapital() < 0) {
            throw new ValidacaoException("A distância da capital não pode ser negativa.");
        }
    }
}