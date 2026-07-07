package service;

import exception.AutonomiaInsuficienteException;
import exception.ConectorIncompativelException;
import model.Cidade;
import model.Eletroposto;
import model.Veiculo;

import java.util.ArrayList;
import java.util.Map;

public interface IAPlannerService {

    Map<String, String> extrairDadosCadastro(String textoLivre, String entidade);

    String planejarRota(Veiculo veiculo, Cidade destino, ArrayList<Eletroposto> eletropostos)
            throws AutonomiaInsuficienteException, ConectorIncompativelException;
}