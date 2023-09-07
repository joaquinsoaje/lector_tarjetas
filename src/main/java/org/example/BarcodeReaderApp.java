package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.util.ArrayList;

public class BarcodeReaderApp {
    private JTextField barcodeField;
    private ArrayList<String> barcodeTableList = new ArrayList<>();
    private JFrame frame;

    public BarcodeReaderApp() {
        setCodes();
        frame = new JFrame("Lee el código en la tarjeta"); // Inicializar 'frame' aquí
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        barcodeField = new JTextField();
        barcodeField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String barcode = barcodeField.getText();
                String mesa = findMesaForBarcode(barcode);
                if (mesa != null) {
                    JOptionPane.showMessageDialog(frame, "Mesa: " + mesa);
                } else {
                    JOptionPane.showMessageDialog(frame, "Código no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                }
                barcodeField.setText("");
            }
        });

        frame.add(barcodeField);
        frame.pack();
        frame.setVisible(true);
    }

    public void setCodes() {
        try (FileInputStream file = new FileInputStream("C:\\Users\\joaqu\\OneDrive\\Escritorio\\MESAS_2023.xlsx")) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell cellMesa = row.getCell(3);
                if (cellMesa != null && cellMesa.getCellType() == CellType.NUMERIC) {
                    Cell cellCodigo = row.getCell(1);
                    if (cellCodigo != null && cellCodigo.getCellType() == CellType.STRING) {
                        barcodeTableList.add(cellCodigo.getStringCellValue());
                        barcodeTableList.add(Integer.toString((int)cellMesa.getNumericCellValue()));
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
