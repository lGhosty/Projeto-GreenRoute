package service;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleMapsService {

    public void abrirRota(String origem, String destino) throws Exception {
        String origemFormatada = URLEncoder.encode(origem, StandardCharsets.UTF_8);
        String destinoFormatado = URLEncoder.encode(destino, StandardCharsets.UTF_8);

        String url = "https://www.google.com/maps/dir/?api=1"
                + "&origin=" + origemFormatada
                + "&destination=" + destinoFormatado
                + "&travelmode=driving";

        abrirNoNavegador(url);
    }

    public void abrirPesquisaEletropostos(String cidade, String estado) throws Exception {
        String busca = "eletropostos em " + cidade + " " + estado;
        String buscaFormatada = URLEncoder.encode(busca, StandardCharsets.UTF_8);

        String url = "https://www.google.com/maps/search/?api=1&query=" + buscaFormatada;

        abrirNoNavegador(url);
    }

    public void abrirLocalizacao(String localizacao) throws Exception {
        String localizacaoFormatada = URLEncoder.encode(localizacao, StandardCharsets.UTF_8);

        String url = "https://www.google.com/maps/search/?api=1&query=" + localizacaoFormatada;

        abrirNoNavegador(url);
    }

    private void abrirNoNavegador(String url) throws Exception {
        if (!Desktop.isDesktopSupported()) {
            throw new UnsupportedOperationException("Não foi possível abrir o navegador neste sistema.");
        }

        Desktop.getDesktop().browse(new URI(url));
    }
}