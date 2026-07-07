package repository;

import model.Eletroposto;

import java.util.ArrayList;

public class EletropostoRepository {

    private final ArrayList<Eletroposto> eletropostos = new ArrayList<>();

    public boolean adicionar(Eletroposto eletroposto) {
        if (buscarPorId(eletroposto.getId()) != null) {
            return false;
        }

        eletropostos.add(eletroposto);
        return true;
    }

    public boolean atualizar(Eletroposto eletropostoAtualizado) {
        for (int i = 0; i < eletropostos.size(); i++) {
            Eletroposto eletroposto = eletropostos.get(i);

            if (eletroposto.getId() == eletropostoAtualizado.getId()) {
                eletropostos.set(i, eletropostoAtualizado);
                return true;
            }
        }

        return false;
    }

    public boolean remover(int id) {
        Eletroposto eletroposto = buscarPorId(id);

        if (eletroposto == null) {
            return false;
        }

        eletropostos.remove(eletroposto);
        return true;
    }

    public Eletroposto buscarPorId(int id) {
        for (Eletroposto eletroposto : eletropostos) {
            if (eletroposto.getId() == id) {
                return eletroposto;
            }
        }

        return null;
    }

    public ArrayList<Eletroposto> listarTodos() {
        return new ArrayList<>(eletropostos);
    }

    public ArrayList<Eletroposto> buscarPorCidadeId(int cidadeId) {
        ArrayList<Eletroposto> resultado = new ArrayList<>();

        for (Eletroposto eletroposto : eletropostos) {
            if (eletroposto.getCidadeId() == cidadeId) {
                resultado.add(eletroposto);
            }
        }

        return resultado;
    }

    public int gerarProximoId() {
        int maiorId = 0;

        for (Eletroposto eletroposto : eletropostos) {
            if (eletroposto.getId() > maiorId) {
                maiorId = eletroposto.getId();
            }
        }

        return maiorId + 1;
    }
}