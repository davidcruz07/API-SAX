import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class cdClass {

    public static void main(String[] args) {
        try {
            File xmlFile = new File("cd_catalog.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList cdList = doc.getElementsByTagName("CD");

            ArrayList<Double> prices = new ArrayList<>();

            for (int i = 0; i < cdList.getLength(); i++) {
                Element cdElement = (Element) cdList.item(i);
                String priceStr = cdElement.getElementsByTagName("PRICE").item(0).getTextContent();
                double price = Double.parseDouble(priceStr);
                prices.add(price);
            }

            double mean = calculateMean(prices);
            double stdDev = calculateStdDev(prices, mean);

            System.out.println("Media de precios: " + mean);
            System.out.println("Desviación estándar: " + stdDev);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double calculateMean(ArrayList<Double> data) {
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.size();
    }

    private static double calculateStdDev(ArrayList<Double> data, double mean) {
        double sum = 0;
        for (double value : data) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / data.size());
    }
}

