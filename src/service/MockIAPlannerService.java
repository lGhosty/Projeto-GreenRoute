package service;

import exception.AutonomiaInsuficienteException;
import exception.ConectorIncompativelException;
import model.Cidade;
import model.Eletroposto;
import model.Veiculo;
import model.VeiculoEletrico;
import model.VeiculoHibrido;
import util.TextoIAUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MockIAPlannerService implements IAPlannerService {

    @Override
    public Map<String, String> extrairDadosCadastro(String textoLivre, String entidade) {
        Map<String, String> dados = new HashMap<>();

        if (entidade == null) {
            return dados;
        }

        if (entidade.equalsIgnoreCase("veiculo")) {
            preencherDadosVeiculo(textoLivre, dados);
        } else if (entidade.equalsIgnoreCase("cidade")) {
            preencherDadosCidade(textoLivre, dados);
        } else if (entidade.equalsIgnoreCase("eletroposto")) {
            preencherDadosEletroposto(textoLivre, dados);
        }

        return dados;
    }

    @Override
    public String planejarRota(Veiculo veiculo, Cidade destino, ArrayList<Eletroposto> eletropostos)
            throws AutonomiaInsuficienteException, ConectorIncompativelException {

        double distancia = destino.getDistanciaDaCapital();
        double autonomiaAtual = veiculo.getAutonomiaAtual();

        if (distancia > autonomiaAtual && eletropostos.isEmpty()) {
            throw new AutonomiaInsuficienteException(
                    "A autonomia atual do veículo não cobre a distância até o destino e não há eletropostos cadastrados."
            );
        }

        Eletroposto postoCompativel = null;

        if (veiculo instanceof VeiculoEletrico) {
            VeiculoEletrico eletrico = (VeiculoEletrico) veiculo;

            for (Eletroposto posto : eletropostos) {
                if (eletrico.isCompativel(posto.getTiposConectoresDisponiveis())) {
                    postoCompativel = posto;
                    break;
                }
            }

            if (!eletropostos.isEmpty() && postoCompativel == null) {
                throw new ConectorIncompativelException(
                        "Há eletropostos cadastrados, mas nenhum possui conector compatível com o veículo."
                );
            }
        }

        StringBuilder resposta = new StringBuilder();

        resposta.append("PLANEJAMENTO INTELIGENTE GERADO PELA IA SIMULADA\n\n");

        resposta.append("Veículo selecionado: ").append(veiculo.getModelo()).append("\n");
        resposta.append("Tipo do veículo: ").append(veiculo.getTipo()).append("\n");
        resposta.append("Destino: ").append(destino.getNome()).append("/").append(destino.getEstado()).append("\n");
        resposta.append("Distância estimada: ").append(distancia).append(" km\n");
        resposta.append("Autonomia atual calculada: ")
                .append(String.format("%.1f", autonomiaAtual))
                .append(" km\n");
        resposta.append("Tempo de recarga completa: ")
                .append(veiculo.getTempoRecargaCompleta())
                .append(" minutos\n\n");

        resposta.append("Dados simulados externos:\n");
        resposta.append("- Clima: chuva leve em alguns trechos.\n");
        resposta.append("- Trânsito: moderado na saída da cidade.\n\n");

        if (distancia <= autonomiaAtual) {
            resposta.append("Análise da IA: a viagem pode ser feita com a carga atual.\n");
            resposta.append("Recomendação: mesmo assim, é indicado sair com boa margem de bateria.\n");
        } else {
            resposta.append("Análise da IA: a viagem exige planejamento de recarga.\n");

            if (postoCompativel != null) {
                resposta.append("Eletroposto recomendado: ").append(postoCompativel.getNome()).append("\n");
                resposta.append("Localização: ").append(postoCompativel.getLocalizacao()).append("\n");
                resposta.append("Conectores: ").append(postoCompativel.getTiposConectoresDisponiveis()).append("\n");
                resposta.append("Potência: ").append(postoCompativel.getPotenciaCargaKw()).append(" kW\n");
                resposta.append("Preço: R$ ")
                        .append(String.format("%.2f", postoCompativel.getPrecoPorKwh()))
                        .append(" por kWh\n");
                resposta.append("Vagas disponíveis: ").append(postoCompativel.getVagasDisponiveis()).append("\n");
            }
        }

        if (veiculo instanceof VeiculoHibrido) {
            VeiculoHibrido hibrido = (VeiculoHibrido) veiculo;

            resposta.append("\nComo o veículo é híbrido, a IA também considera a autonomia do motor a combustão.\n");
            resposta.append("Autonomia adicional estimada: ")
                    .append(String.format("%.1f", hibrido.getAutonomiaMotorCombustao()))
                    .append(" km\n");
        }

        resposta.append("\nConclusão: rota planejada com base nos dados cadastrados, autonomia restante, clima e trânsito simulados.");

        return resposta.toString();
    }

    private void preencherDadosVeiculo(String textoLivre, Map<String, String> dados) {
        String textoNormalizado = TextoIAUtils.normalizar(textoLivre);

        dados.put("tipo", TextoIAUtils.identificarTipoVeiculo(textoLivre));

        if (textoNormalizado.contains("byd dolphin")) {
            dados.put("modelo", "BYD Dolphin");
        } else if (textoNormalizado.contains("volvo ex30")) {
            dados.put("modelo", "Volvo EX30");
        } else if (textoNormalizado.contains("nissan leaf")) {
            dados.put("modelo", "Nissan Leaf");
        } else {
            dados.put("modelo", "Modelo identificado pela IA");
        }

        String autonomia = TextoIAUtils.extrairNumeroAntesDeKm(textoLivre);
        String bateria = TextoIAUtils.extrairPercentual(textoLivre);
        String consumo = TextoIAUtils.extrairConsumoKwhPorKm(textoLivre);
        String tempoCompleto = TextoIAUtils.extrairNumeroAntesDeMinutos(textoLivre);
        String conector = TextoIAUtils.identificarConector(textoLivre);

        dados.put("autonomia", TextoIAUtils.valorOuPadrao(autonomia, "400"));
        dados.put("bateria", TextoIAUtils.valorOuPadrao(bateria, "80"));
        dados.put("consumo", TextoIAUtils.valorOuPadrao(consumo, "0.15"));
        dados.put("tempoCompleto", TextoIAUtils.valorOuPadrao(tempoCompleto, "420"));
        dados.put("conector", TextoIAUtils.valorOuPadrao(conector, "CCS2"));
        dados.put("tempoRapido", "60");

        dados.put("capacidadeTanque", "45");
        dados.put("consumoCombustivel", "14");
        dados.put("tipoCombustivel", "Gasolina");
    }

    private void preencherDadosCidade(String textoLivre, Map<String, String> dados) {
        String textoNormalizado = TextoIAUtils.normalizar(textoLivre);

        if (textoNormalizado.contains("caruaru")) {
            dados.put("nome", "Caruaru");
        } else if (textoNormalizado.contains("recife")) {
            dados.put("nome", "Recife");
        } else if (textoNormalizado.contains("surubim")) {
            dados.put("nome", "Surubim");
        } else {
            dados.put("nome", "Cidade identificada pela IA");
        }

        if (textoNormalizado.contains("paraiba") || textoNormalizado.contains("pb")) {
            dados.put("estado", "PB");
        } else if (textoNormalizado.contains("alagoas") || textoNormalizado.contains("al")) {
            dados.put("estado", "AL");
        } else {
            dados.put("estado", "PE");
        }

        String distancia = TextoIAUtils.extrairNumeroAntesDeKm(textoLivre);

        dados.put("distancia", TextoIAUtils.valorOuPadrao(distancia, "100"));
    }

    private void preencherDadosEletroposto(String textoLivre, Map<String, String> dados) {
        String textoNormalizado = TextoIAUtils.normalizar(textoLivre);

        if (textoNormalizado.contains("shopping")) {
            dados.put("nome", "Eletroposto Shopping");
            dados.put("localizacao", "Shopping da cidade");
        } else if (textoNormalizado.contains("posto")) {
            dados.put("nome", "Eletroposto Posto Central");
            dados.put("localizacao", "Posto Central");
        } else {
            dados.put("nome", "Eletroposto identificado pela IA");
            dados.put("localizacao", "Localização identificada pela IA");
        }

        String conector = TextoIAUtils.identificarConector(textoLivre);
        String potencia = TextoIAUtils.extrairPrimeiroNumero(textoLivre);

        dados.put("cidadeId", "1");
        dados.put("conectores", TextoIAUtils.valorOuPadrao(conector, "CCS2"));
        dados.put("potencia", TextoIAUtils.valorOuPadrao(potencia, "50"));
        dados.put("preco", "2.20");
        dados.put("vagas", "2");
    }
}