package service;

import exception.AutonomiaInsuficienteException;
import exception.ConectorIncompativelException;
import gemini.ConexaoGemini;
import model.Cidade;
import model.Eletroposto;
import model.Veiculo;
import model.VeiculoEletrico;
import util.TextoIAUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeminiIAPlannerService implements IAPlannerService {

    private final ConexaoGemini conexaoGemini;

    public GeminiIAPlannerService() {
        this.conexaoGemini = new ConexaoGemini();
    }

    @Override
    public Map<String, String> extrairDadosCadastro(String textoLivre, String entidade) {
        String prompt = montarPromptCadastro(textoLivre, entidade);

        String respostaIA = conexaoGemini.perguntar(prompt);

        return transformarRespostaEmMapa(respostaIA, textoLivre, entidade);
    }

    @Override
    public String planejarRota(Veiculo veiculo, Cidade destino, ArrayList<Eletroposto> eletropostos)
            throws AutonomiaInsuficienteException, ConectorIncompativelException {

        validarRotaAntesDaIA(veiculo, destino, eletropostos);

        String prompt = montarPromptRota(veiculo, destino, eletropostos);

        return conexaoGemini.perguntar(prompt);
    }

    private String montarPromptCadastro(String textoLivre, String entidade) {
        return """
                Você é uma IA integrada ao sistema GreenRoute.

                Sua função é extrair dados estruturados de um texto livre digitado pelo usuário.

                Entidade desejada: %s

                Texto digitado pelo usuário:
                "%s"

                Responda somente usando o formato chave=valor.
                Não use JSON.
                Não use Markdown.
                Não explique nada fora dos campos.

                Campos possíveis:

                Para veículo:
                modelo=
                tipo=
                autonomia=
                bateria=
                consumo=
                tempoCompleto=
                conector=
                tempoRapido=
                capacidadeTanque=
                consumoCombustivel=
                tipoCombustivel=

                Para cidade:
                nome=
                estado=
                distancia=

                Para eletroposto:
                nome=
                localizacao=
                cidadeId=
                conectores=
                potencia=
                preco=
                vagas=
                """.formatted(entidade, textoLivre);
    }

    private Map<String, String> transformarRespostaEmMapa(String respostaIA, String textoOriginal, String entidade) {
        Map<String, String> dados = new HashMap<>();

        String respostaLimpa = TextoIAUtils.limparRespostaIA(respostaIA);

        String[] linhas = respostaLimpa.split("\\n");

        for (String linha : linhas) {
            if (linha.contains("=")) {
                String[] partes = linha.split("=", 2);

                String chave = partes[0].trim();
                String valor = partes.length > 1 ? partes[1].trim() : "";

                if (!chave.isEmpty() && !valor.isEmpty()) {
                    dados.put(chave, valor);
                }
            }
        }

        completarComStringsLocais(dados, textoOriginal, entidade);

        return dados;
    }

    private void completarComStringsLocais(Map<String, String> dados, String textoOriginal, String entidade) {
        if (entidade == null) {
            return;
        }

        if (entidade.equalsIgnoreCase("veiculo")) {
            dados.putIfAbsent("tipo", TextoIAUtils.identificarTipoVeiculo(textoOriginal));
            dados.putIfAbsent("autonomia", TextoIAUtils.extrairNumeroAntesDeKm(textoOriginal));
            dados.putIfAbsent("bateria", TextoIAUtils.extrairPercentual(textoOriginal));
            dados.putIfAbsent("consumo", TextoIAUtils.extrairConsumoKwhPorKm(textoOriginal));
            dados.putIfAbsent("tempoCompleto", TextoIAUtils.extrairNumeroAntesDeMinutos(textoOriginal));
            dados.putIfAbsent("conector", TextoIAUtils.identificarConector(textoOriginal));

            dados.putIfAbsent("tempoRapido", "60");
            dados.putIfAbsent("capacidadeTanque", "45");
            dados.putIfAbsent("consumoCombustivel", "14");
            dados.putIfAbsent("tipoCombustivel", "Gasolina");
        }

        if (entidade.equalsIgnoreCase("cidade")) {
            dados.putIfAbsent("distancia", TextoIAUtils.extrairNumeroAntesDeKm(textoOriginal));
        }

        if (entidade.equalsIgnoreCase("eletroposto")) {
            dados.putIfAbsent("conectores", TextoIAUtils.identificarConector(textoOriginal));
            dados.putIfAbsent("potencia", TextoIAUtils.extrairPrimeiroNumero(textoOriginal));
            dados.putIfAbsent("cidadeId", "1");
            dados.putIfAbsent("preco", "2.20");
            dados.putIfAbsent("vagas", "2");
        }
    }

    private String montarPromptRota(Veiculo veiculo, Cidade destino, ArrayList<Eletroposto> eletropostos) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Você é o planejador inteligente de rotas do sistema GreenRoute.\n\n");

        prompt.append("Objetivo:\n");
        prompt.append("Planejar uma rota para veículo elétrico ou híbrido considerando autonomia, bateria, recarga, cidade de destino, eletropostos, clima e trânsito simulados.\n\n");

        prompt.append("Dados do veículo:\n");
        prompt.append("Modelo: ").append(veiculo.getModelo()).append("\n");
        prompt.append("Tipo: ").append(veiculo.getTipo()).append("\n");
        prompt.append("Autonomia máxima: ").append(veiculo.getAutonomiaMaxima()).append(" km\n");
        prompt.append("Bateria atual: ").append(veiculo.getCargaBateriaAtual()).append("%\n");
        prompt.append("Autonomia atual calculada: ").append(String.format("%.1f", veiculo.getAutonomiaAtual())).append(" km\n");
        prompt.append("Consumo: ").append(veiculo.getConsumoKwhPorKm()).append(" kWh/km\n");
        prompt.append("Tempo de recarga completa: ").append(veiculo.getTempoRecargaCompleta()).append(" minutos\n");

        if (veiculo instanceof VeiculoEletrico) {
            VeiculoEletrico eletrico = (VeiculoEletrico) veiculo;
            prompt.append("Conector do veículo: ").append(eletrico.getTipoConector()).append("\n");
        }

        prompt.append("\nDestino:\n");
        prompt.append("Cidade: ").append(destino.getNome()).append("/").append(destino.getEstado()).append("\n");
        prompt.append("Distância simulada a partir da capital: ").append(destino.getDistanciaDaCapital()).append(" km\n");

        prompt.append("\nDados externos simulados:\n");
        prompt.append("Clima: chuva leve em alguns trechos, exigindo direção mais cautelosa.\n");
        prompt.append("Trânsito: moderado na saída da cidade, podendo aumentar o tempo total da viagem.\n");

        prompt.append("\nEletropostos cadastrados para o destino:\n");

        if (eletropostos == null || eletropostos.isEmpty()) {
            prompt.append("Nenhum eletroposto cadastrado para essa cidade.\n");
        } else {
            for (Eletroposto posto : eletropostos) {
                prompt.append("- Nome: ").append(posto.getNome()).append("\n");
                prompt.append("  Localização: ").append(posto.getLocalizacao()).append("\n");
                prompt.append("  Conectores: ").append(posto.getTiposConectoresDisponiveis()).append("\n");
                prompt.append("  Potência: ").append(posto.getPotenciaCargaKw()).append(" kW\n");
                prompt.append("  Preço por kWh: R$ ").append(posto.getPrecoPorKwh()).append("\n");
                prompt.append("  Vagas disponíveis: ").append(posto.getVagasDisponiveis()).append("\n");
            }
        }

        prompt.append("""
                
                Gere uma resposta amigável para o usuário contendo:
                1. Se a viagem é possível.
                2. Se precisa recarregar.
                3. Qual eletroposto usar, se houver.
                4. Riscos por causa do clima e trânsito.
                5. Estimativa simples do tempo total.
                6. Recomendação final.

                Responda em português, de forma clara e organizada.
                """);

        return prompt.toString();
    }

    private void validarRotaAntesDaIA(Veiculo veiculo, Cidade destino, ArrayList<Eletroposto> eletropostos)
            throws AutonomiaInsuficienteException, ConectorIncompativelException {

        double distancia = destino.getDistanciaDaCapital();
        double autonomiaAtual = veiculo.getAutonomiaAtual();

        if (eletropostos == null) {
            eletropostos = new ArrayList<>();
        }

        if (distancia > autonomiaAtual && eletropostos.isEmpty()) {
            throw new AutonomiaInsuficienteException(
                    "A autonomia atual do veículo não cobre a distância até o destino e não há eletropostos cadastrados."
            );
        }

        if (veiculo instanceof VeiculoEletrico && !eletropostos.isEmpty()) {
            VeiculoEletrico eletrico = (VeiculoEletrico) veiculo;

            boolean encontrouCompativel = false;

            for (Eletroposto posto : eletropostos) {
                if (eletrico.isCompativel(posto.getTiposConectoresDisponiveis())) {
                    encontrouCompativel = true;
                    break;
                }
            }

            if (!encontrouCompativel) {
                throw new ConectorIncompativelException(
                        "Nenhum eletroposto cadastrado possui conector compatível com o veículo selecionado."
                );
            }
        }
    }
}