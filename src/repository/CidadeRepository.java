package repository;

import model.Cidade;

import java.util.ArrayList;

public class CidadeRepository {

    private final ArrayList<Cidade> cidades = new ArrayList<>();

    public boolean adicionar(Cidade cidade) {
        if (buscarPorId(cidade.getId()) != null) {
            return false;
        }

        cidades.add(cidade);
        return true;
    }

    public boolean atualizar(Cidade cidadeAtualizada) {
        for (int i = 0; i < cidades.size(); i++) {
            Cidade cidade = cidades.get(i);

            if (cidade.getId() == cidadeAtualizada.getId()) {
                cidades.set(i, cidadeAtualizada);
                return true;
            }
        }

        return false;
    }

    public boolean remover(int id) {
        Cidade cidade = buscarPorId(id);

        if (cidade == null) {
            return false;
        }

        cidades.remove(cidade);
        return true;
    }

    public Cidade buscarPorId(int id) {
        for (Cidade cidade : cidades) {
            if (cidade.getId() == id) {
                return cidade;
            }
        }

        return null;
    }

    public ArrayList<Cidade> listarTodos() {
        return new ArrayList<>(cidades);
    }

    public int gerarProximoId() {
        int maiorId = 0;

        for (Cidade cidade : cidades) {
            if (cidade.getId() > maiorId) {
                maiorId = cidade.getId();
            }
        }

        return maiorId + 1;
    }
}