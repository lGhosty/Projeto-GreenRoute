package service;

import exception.AutonomiaInsuficienteException;
import exception.ConectorIncompativelException;
import model.Cidade;
import model.Eletroposto;
import model.Veiculo;

import java.util.ArrayList;
import java.util.Map;

public class IAPlannerServiceComFallback implements IAPlannerService {

    private final IAPlannerService servicoPrincipal;
    private final IAPlannerService servicoFallback;

    public IAPlannerServiceComFallback(IAPlannerService servicoPrincipal, IAPlannerService servicoFallback) {
        this.servicoPrincipal = servicoPrincipal;
        this.servicoFallback = servicoFallback;
    }

    @Override
    public Map<String, String> extrairDadosCadastro(String textoLivre, String entidade) {
        try {
            return servicoPrincipal.extrairDadosCadastro(textoLivre, entidade);
        } catch (RuntimeException e) {
            System.out.println("Falha ao usar Gemini no cadastro. Usando IA simulada. Motivo: " + e.getMessage());
            return servicoFallback.extrairDadosCadastro(textoLivre, entidade);
        }
    }

    @Override
    public String planejarRota(Veiculo veiculo, Cidade destino, ArrayList<Eletroposto> eletropostos)
            throws AutonomiaInsuficienteException, ConectorIncompativelException {
        try {
            return servicoPrincipal.planejarRota(veiculo, destino, eletropostos);
        } catch (AutonomiaInsuficienteException | ConectorIncompativelException e) {
            throw e;
        } catch (RuntimeException e) {
            System.out.println("Falha ao usar Gemini no planejamento. Usando IA simulada. Motivo: " + e.getMessage());
            return servicoFallback.planejarRota(veiculo, destino, eletropostos);
        }
    }
}