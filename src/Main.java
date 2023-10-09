import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.ArrayList;

public class Main {
    private ArrayList<Double> prices = new ArrayList<>();

    public void parseXML(String xmlFilePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFilePath));

            NodeList cdList = document.getElementsByTagName("CD");

            for (int i = 0; i < cdList.getLength(); i++) {
                Element cd = (Element) cdList.item(i);
                double price = Double.parseDouble(cd.getElementsByTagName("PRICE").item(0).getTextContent());
                prices.add(price);
            }

            // Calcular y mostrar la media y la desviación estándar
            double media = calcularMedia();
            double desviacionEstandar = calcularDesviacionEstandar(media);
            System.out.println("Media de precios: " + media);
            System.out.println("Desviación estándar de precios: " + desviacionEstandar);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calcularMedia() {
        double suma = 0;
        for (Double price : prices) {
            suma += price;
        }
        return suma / prices.size();
    }

    private double calcularDesviacionEstandar(double media) {
        double sumaDiferenciasCuadradas = 0;
        for (Double price : prices) {
            double diferencia = price - media;
            sumaDiferenciasCuadradas += diferencia * diferencia;
        }
        double varianza = sumaDiferenciasCuadradas / prices.size();
        return Math.sqrt(varianza);
    }

    public static void main(String[] args) {
        Main cdStats = new Main();
        cdStats.parseXML("cd_catalog.xml");
    }
}
