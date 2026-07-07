package util;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextoIAUtils {

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        String textoLimpo = texto.trim().toLowerCase();

        textoLimpo = Normalizer.normalize(textoLimpo, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return textoLimpo;
    }

    public static boolean contemPalavra(String texto, String palavra) {
        String textoNormalizado = normalizar(texto);
        String palavraNormalizada = normalizar(palavra);

        return textoNormalizado.contains(palavraNormalizada);
    }

    public static String extrairPrimeiroNumero(String texto) {
        if (texto == null) {
            return "";
        }

        String textoAjustado = texto.replace(",", ".");

        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(textoAjustado);

        if (matcher.find()) {
            return matcher.group();
        }

        return "";
    }

    public static String extrairNumeroAntesDeKm(String texto) {
        String textoNormalizado = normalizar(texto).replace(",", ".");

        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*km");
        Matcher matcher = pattern.matcher(textoNormalizado);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    public static String extrairPercentual(String texto) {
        String textoNormalizado = normalizar(texto).replace(",", ".");

        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*%");
        Matcher matcher = pattern.matcher(textoNormalizado);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    public static String extrairNumeroAntesDeMinutos(String texto) {
        String textoNormalizado = normalizar(texto);

        Pattern pattern = Pattern.compile("(\\d+)\\s*(min|minuto|minutos)");
        Matcher matcher = pattern.matcher(textoNormalizado);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    public static String extrairConsumoKwhPorKm(String texto) {
        String textoNormalizado = normalizar(texto).replace(",", ".");

        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(kwh/km|kw/h|kwh)");
        Matcher matcher = pattern.matcher(textoNormalizado);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    public static String identificarConector(String texto) {
        String textoNormalizado = normalizar(texto);

        if (textoNormalizado.contains("ccs2") || textoNormalizado.contains("ccs 2")) {
            return "CCS2";
        }

        if (textoNormalizado.contains("tipo 2") || textoNormalizado.contains("type 2")) {
            return "Tipo 2";
        }

        if (textoNormalizado.contains("chademo")) {
            return "CHAdeMO";
        }

        return "";
    }

    public static String identificarTipoVeiculo(String texto) {
        String textoNormalizado = normalizar(texto);

        if (textoNormalizado.contains("hibrido")) {
            return "Híbrido";
        }

        if (textoNormalizado.contains("eletrico")) {
            return "Elétrico";
        }

        return "Elétrico";
    }

    public static String limparRespostaIA(String resposta) {
        if (resposta == null) {
            return "";
        }

        return resposta
                .replace("```json", "")
                .replace("```java", "")
                .replace("```", "")
                .trim();
    }

    public static double converterDoubleSeguro(String valor, double valorPadrao) {
        try {
            if (valor == null || valor.trim().isEmpty()) {
                return valorPadrao;
            }

            return Double.parseDouble(valor.replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return valorPadrao;
        }
    }

    public static int converterIntSeguro(String valor, int valorPadrao) {
        try {
            if (valor == null || valor.trim().isEmpty()) {
                return valorPadrao;
            }

            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return valorPadrao;
        }
    }

    public static String valorOuPadrao(String valor, String padrao) {
        if (valor == null || valor.trim().isEmpty()) {
            return padrao;
        }

        return valor.trim();
    }
}