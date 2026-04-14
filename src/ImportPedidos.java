package src;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportPedidos {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    public void importar(File file) {

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
        ) {

            Sheet sheet = workbook.getSheetAt(0);

            PedidoDAO dao = new PedidoDAO();

            dao.limparTabela(); // 🔥 limpa tabela antes de importar

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                // 🔥 ignora linha vazia
                if (row == null || isRowEmpty(row)) continue;

                Pedido p = new Pedido();
                p.setPedidoDeProducao(getString(row.getCell(0)));
                p.setPedidoDeCompra(getString(row.getCell(1)));
                p.setEmpresa(getString(row.getCell(2)));
                p.setPrevisaoDeInicio(getDate(row.getCell(3)));
                p.setPrazo(getDate(row.getCell(4)));
                p.setProntos(getInt(row.getCell(5)));
                p.setTotalDeItens(getInt(row.getCell(6)));
                p.setObservacoes(getString(row.getCell(7)));

                dao.inserir(p);
            }

            System.out.println("✔ Importação concluída com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erro na importação: " + e.getMessage());
        }
    }

    // =========================
    // STRING 
    // =========================
    private static String getString(Cell cell) {
        if (cell == null) return null;

        try {
            DataFormatter formatter = new DataFormatter();
            return formatter.formatCellValue(cell).trim();
        } catch (Exception e) {
            return null;
        }
    }

    // =========================
    // INT
    // =========================
    private static int getInt(Cell cell) {
        if (cell == null) return 0;

        try {
            switch (cell.getCellType()) {

                case NUMERIC:
                    return (int) cell.getNumericCellValue();

                case STRING:
                    return Integer.parseInt(cell.getStringCellValue().trim());

                default:
                    return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    // =========================
    // DATA
    // =========================
    private static java.sql.Date getDate(Cell cell) {
        if (cell == null) return null;

        try {

            if (DateUtil.isCellDateFormatted(cell)) {
                return new java.sql.Date(cell.getDateCellValue().getTime());
            }

            if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                if (value.isEmpty()) return null;

                Date date = SDF.parse(value);
                return new java.sql.Date(date.getTime());
            }        

        } catch (Exception e) {
            System.out.println("Erro na data: " + cell);
        }

        return null;
    }

    // =========================
    // VERIFICA LINHA VAZIA
    // =========================
    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);

            if (cell != null && cell.getCellType() != CellType.BLANK) {

                if (cell.getCellType() == CellType.STRING &&
                        !cell.getStringCellValue().trim().isEmpty()) {
                    return false;
                }

                if (cell.getCellType() == CellType.NUMERIC) {
                    return false;
                }

                if (cell.getCellType() == CellType.FORMULA) {
                    return false;
                }
            }
        }

        return true;
    }
}