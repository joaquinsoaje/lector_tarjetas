package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.ArrayList;

public class BarcodeReaderApp {
    private JTextField barcodeField;
    private ArrayList<String> barcodeTableList = new ArrayList<>();
    private JFrame frame;
    private ImageIcon backgroundImage;

    public BarcodeReaderApp() {
        setCodes();
        frame = new JFrame("Lee el código en la tarjeta"); // Inicializar 'frame' aquí
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Obtén el tamaño de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();

        // Crea un JLayeredPane para manejar la imagen de fondo
        JLayeredPane layeredPane = new JLayeredPane();

        // Crea un JLabel con una imagen de fondo
        backgroundImage = new ImageIcon("atem_exodia.jpeg"); // Reemplaza con la ruta de tu imagen

        // Ajusta el tamaño de la imagen de fondo al tamaño de la pantalla
        backgroundImage.setImage(backgroundImage.getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_DEFAULT));
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, screenWidth, screenHeight);

        // Agrega el JLabel de fondo al JLayeredPane en el nivel de fondo
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // Agrega el JLabel de fondo al JFrame
        frame.setContentPane(backgroundLabel);

        barcodeField = new JTextField(10);
        barcodeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkLengthAndSubmit(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // No necesitamos hacer nada en la eliminación de texto
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Este método se llama para atributos de estilo, no es necesario hacer nada
            }
        });

        barcodeField.addActionListener(e -> {
            String barcode = barcodeField.getText();
            String mesa = findMesaForBarcode(barcode);
            if (mesa != null) {
                JLabel messageLabel = new JLabel("Mesa: " + mesa);
                messageLabel.setFont(new Font("Arial", Font.PLAIN, 80)); // Ajusta la fuente y el tamaño según tus preferencias
                JOptionPane.showMessageDialog(frame, messageLabel, "Mesa obtenida", JOptionPane.PLAIN_MESSAGE);
            } else {
                JLabel errorLabel = new JLabel("No se encontró mesa para codigo: " + barcode);
                errorLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Ajusta la fuente y el tamaño según tus preferencias

                // Mostrar el JOptionPane personalizado con un icono de error
                JOptionPane.showMessageDialog(frame, errorLabel, "Error", JOptionPane.ERROR_MESSAGE);
            }
            barcodeField.setText("");
        });

        frame.add(barcodeField);
        frame.setSize(screenWidth, screenHeight);
        // frame.pack();
        frame.setVisible(true);
    }

    private void checkLengthAndSubmit(DocumentEvent e) {
        if (barcodeField.getText().length() == 10) {
            SwingUtilities.invokeLater(() -> {
                ActionEvent enterEvent = new ActionEvent(barcodeField, ActionEvent.ACTION_PERFORMED, "\n");
                for (ActionListener listener : barcodeField.getActionListeners()) {
                    listener.actionPerformed(enterEvent);
                }
            });
        }
    }

    private void setCodes() {
        try (FileInputStream file = new FileInputStream("MESAS_2023.xlsx")) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell cellMesa = row.getCell(3);
                if (cellMesa != null && cellMesa.getCellType() == CellType.NUMERIC) {
                    Cell cellCodigo = row.getCell(1);
                    if (cellCodigo != null && cellCodigo.getCellType() == CellType.STRING) {
                        barcodeTableList.add(cellCodigo.getStringCellValue());
                        barcodeTableList.add(Integer.toString((int) cellMesa.getNumericCellValue()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error al leer el archivo Excel", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String findMesaForBarcode(String barcode) {
        for (int i = 0; i < barcodeTableList.size(); i += 2) {
            if (barcode.equals(barcodeTableList.get(i))) {
                return barcodeTableList.get(i + 1);
            }
        }
        return null;
    }


        public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BarcodeReaderApp();
            }
        });
    }
}
