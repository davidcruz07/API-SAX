import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class InterfazCD extends DefaultHandler {
    private static final Logger logger = Logger.getLogger(InterfazCD.class.getSimpleName());
    private static final List<String> nombresColumnas = new ArrayList<>();
    private final SAXParser saxParser;
    private String elementoActual;
    private final ArrayList<String> datosCelda = new ArrayList<>();
    private DefaultTableModel modeloTabla;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazCD app = new InterfazCD();
            app.crearInterfaz();
        });
    }

    public InterfazCD() {
        SAXParserFactory fabricaSaxParser = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = fabricaSaxParser.newSAXParser();
        } catch (SAXException | ParserConfigurationException e) {
            logger.severe(e.getMessage());
            System.exit(1);
        }
        saxParser = parser;
    }

    private void crearInterfaz() {
        JFrame marcoPrincipal = new JFrame("Visualizador de Archivos XML");
        marcoPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        marcoPrincipal.setSize(800, 600);
        marcoPrincipal.setLocationRelativeTo(null);

        JPanel panelTitulo = new JPanel(new BorderLayout());
        marcoPrincipal.add(panelTitulo, BorderLayout.NORTH);

        JLabel etiquetaTitulo = new JLabel("Visualizador de Archivos XML", SwingConstants.CENTER);
        etiquetaTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelTitulo.add(etiquetaTitulo, BorderLayout.CENTER);

        JButton botonCargar = new JButton("Cargar XML");
        panelTitulo.add(botonCargar, BorderLayout.WEST);

        JButton botonCerrar = new JButton("Cerrar");
        panelTitulo.add(botonCerrar, BorderLayout.EAST);

        JTable tablaDatos = new JTable();
        JScrollPane panelDesplazamiento = new JScrollPane(tablaDatos);
        marcoPrincipal.add(panelDesplazamiento, BorderLayout.CENTER);

        modeloTabla = new DefaultTableModel();
        tablaDatos.setModel(modeloTabla);

        botonCerrar.addActionListener(e -> System.exit(0));

        botonCargar.addActionListener(e -> {
            JFileChooser selectorArchivo = new JFileChooser();
            int resultado = selectorArchivo.showOpenDialog(null);

            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = selectorArchivo.getSelectedFile();

                analizarArchivoXml(archivoSeleccionado);
                mostrarTabla();
            }
        });

        panelTitulo.addMouseListener(new MouseAdapter() {
            private int posX, posY;

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                posX = e.getX();
                posY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent evt) {
                int x = evt.getXOnScreen();
                int y = evt.getYOnScreen();
                marcoPrincipal.setLocation(x - posX, y - posY);
            }
        });

        marcoPrincipal.setVisible(true);
    }

    private void analizarArchivoXml(File archivoXml) {
        try {
            saxParser.parse(archivoXml, this);
        } catch (IOException | SAXException e) {
            logger.severe(e.getMessage());
        }
    }

    @Override
    public void startElement(String uri, String nombreLocal, String nombreQName, Attributes atributos) {
        elementoActual = nombreQName;
    }

    @Override
    public void endElement(String uri, String nombreLocal, String nombreQName) {
        elementoActual = null;
    }

    @Override
    public void characters(char[] ch, int inicio, int longitud) {
        if (elementoActual != null) {
            String valor = new String(ch, inicio, longitud);
            if (!valor.isEmpty()) {
                datosCelda.add(valor);

                String nombreColumna = elementoActual.trim();
                if (!nombreColumna.isEmpty() && !nombresColumnas.contains(nombreColumna)) {
                    nombresColumnas.add(nombreColumna);
                }
            }
        }
    }

    private void mostrarTabla() {
        SwingUtilities.invokeLater(() -> {
            String[] columnas = nombresColumnas.toArray(new String[0]);
            int cantidadFilas = datosCelda.size() / nombresColumnas.size();
            int indiceDatos = 0;
            Object[][] datos = new Object[cantidadFilas][nombresColumnas.size()];

            for (int indiceFila = 0; indiceFila < cantidadFilas; indiceFila++) {
                for (int indiceColumna = 0; indiceColumna < nombresColumnas.size(); indiceColumna++) {
                    if (indiceDatos < datosCelda.size()) {
                        datos[indiceFila][indiceColumna] = datosCelda.get(indiceDatos);
                        indiceDatos++;
                    } else {
                        break;
                    }
                }
            }

            modeloTabla.setDataVector(datos, columnas);
        });
    }
}
