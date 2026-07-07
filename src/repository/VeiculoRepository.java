package repository;

import model.Veiculo;

import java.util.ArrayList;

public class VeiculoRepository {

    private final ArrayList<Veiculo> veiculos = new ArrayList<>();

    public boolean adicionar(Veiculo veiculo) {
        if (buscarPorId(veiculo.getId()) != null) {
            return false;
        }

        veiculos.add(veiculo);
        return true;
    }

    public boolean atualizar(Veiculo veiculoAtualizado) {
        for (int i = 0; i < veiculos.size(); i++) {
            Veiculo veiculo = veiculos.get(i);

            if (veiculo.getId() == veiculoAtualizado.getId()) {
                veiculos.set(i, veiculoAtualizado);
                return true;
            }
        }

        return false;
    }

    public boolean remover(int id) {
        Veiculo veiculo = buscarPorId(id);

        if (veiculo == null) {
            return false;
        }

        veiculos.remove(veiculo);
        return true;
    }

    public Veiculo buscarPorId(int id) {
        for (Veiculo veiculo : veiculos) {
            if (veiculo.getId() == id) {
                return veiculo;
            }
        }

        return null;
    }

    public ArrayList<Veiculo> listarTodos() {
        return new ArrayList<>(veiculos);
    }

    public int gerarProximoId() {
        int maiorId = 0;

        for (Veiculo veiculo : veiculos) {
            if (veiculo.getId() > maiorId) {
                maiorId = veiculo.getId();
            }
        }

        return maiorId + 1;
    }
}