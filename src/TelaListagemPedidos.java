package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


public class TelaListagemPedidos extends JFrame {

    private JTable tabela;
    private DefaultTableModel model;
    private PedidoDAO dao = new PedidoDAO();
    private boolean carregando = false;

    private JComboBox<String> cbStatusFiltro;
    private JTextField tfEmpresaFiltro;

    private List<Pedido> cachePedidos;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy"); 

    public TelaListagemPedidos() {

        setTitle("Lista de Pedidos");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // =========================
        // MODEL
        // =========================
        model = new DefaultTableModel(
                new Object[]{
                        "ID", "Pedido Produção", "Pedido Compra", "Empresa",
                        "Previsão Início", "Prazo", "Prontos", "Total",
                        "Observações", "Status", "Data Entrega"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 9;
            }
        };

        tabela = new JTable(model);

        // =========================
        // CORES
        // =========================
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setForeground(Color.BLACK);

                try {
                    String status = String.valueOf(model.getValueAt(row, 9));

                    if ("PRONTO".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(180, 255, 180)); // VERDE CLARO PARA PRONTO
                    } else if ("ENTREGUE".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(180, 255, 255)); // AZUL CLARO PARA ENTREGUE
                    } else {
                        String dataStr = String.valueOf(model.getValueAt(row, 4));
                        Date previsaoDate = parseDate(dataStr);

                        if (previsaoDate != null) {
                            LocalDate hoje = LocalDate.now();
                            LocalDate previsao = previsaoDate.toLocalDate();

                            long dias = java.time.temporal.ChronoUnit.DAYS.between(hoje, previsao); 

                            if (dias < 0) {
                                c.setBackground(new Color(255, 180, 180)); // VERMELHO CLARO ATRASADO
                            } else if (dias <= 3) {
                                c.setBackground(new Color(255, 255, 180)); // AMARELO CLARO PRÓXIMO
                            } else {
                                c.setBackground(Color.WHITE); // SEM COR PARA O RESTANTE
                            }
                        } else {
                            c.setBackground(Color.WHITE); // SEM COR SE NÃO HOUVER DATA VÁLIDA
                        }
                    }
                } catch (Exception e) {
                    c.setBackground(Color.WHITE); // SEM COR EM CASO DE ERRO
                }
                 
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }

                return c;
            }
        });

        // =========================
        // FILTRO + BOTÕES
        // =========================
        JPanel painelFiltro = new JPanel();

        tfEmpresaFiltro = new JTextField(12);

        tfEmpresaFiltro.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        cbStatusFiltro = new JComboBox<>(new String[]{
                "TODOS", "PRONTO", "EM ANDAMENTO", "ENTREGUE"
        });

        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnImprimir = new JButton("Imprimir");
        JButton btnExportar = new JButton("Exportar CSV");
        JButton btnImportar = new JButton("Importar Excel");

        btnFiltrar.addActionListener(e -> filtrar());
        btnExcluir.addActionListener(e -> excluirPedido());
        btnImprimir.addActionListener(e -> imprimir());
        btnExportar.addActionListener(e -> exportarCSV());
        btnImportar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecionar arquivo Excel (.xlsx)");

            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                ImportPedidos importador = new ImportPedidos();
                importador.importar(file);

                carregar();

            }
        });

        painelFiltro.add(tfEmpresaFiltro);
        painelFiltro.add(cbStatusFiltro);
        painelFiltro.add(btnFiltrar);
        painelFiltro.add(btnExcluir);
        painelFiltro.add(btnImprimir);
        painelFiltro.add(btnExportar);
        painelFiltro.add(btnImportar);

        add(painelFiltro, BorderLayout.NORTH);

        // =========================
        // LISTENER
        // =========================
        model.addTableModelListener(e -> {

            if (carregando) return;
            if (e.getType() != TableModelEvent.UPDATE) return;

            int row = e.getFirstRow();
            int col = e.getColumn();

            if (row < 0 || col < 0) return;

            Object idObj = model.getValueAt(row, 0);
            if (idObj == null) return;

            int id = Integer.parseInt(idObj.toString());

            SwingUtilities.invokeLater(() -> {
                atualizarBanco(row, id);
            });

        });

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        carregar();

        setVisible(true);
    }

    // =========================
    // CARREGAR
    // =========================
    private void carregar() {
        try {
            carregando = true;
            cachePedidos = dao.listarTodos();
            preencherTabela(cachePedidos);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        } finally {
            carregando = false;
        }
    }

    // =========================
    // FILTRAR
    // =========================
    private void filtrar() {

        if (cachePedidos == null) return;

        String empresa = tfEmpresaFiltro.getText().trim().toLowerCase();
        String status = (String) cbStatusFiltro.getSelectedItem();

        List<Pedido> filtrados = cachePedidos.stream()
                .filter(p -> {
                    String busca = empresa;

                    if (busca.isEmpty()) return true;

                    return (p.getEmpresa() != null && p.getEmpresa().toLowerCase().contains(busca)) ||
                            (p.getPedidoDeProducao() != null && p.getPedidoDeProducao().toLowerCase().contains(busca)) ||
                            (p.getPedidoDeCompra() != null && p.getPedidoDeCompra().toLowerCase().contains(busca));
                })
                .filter(p -> status.equals("TODOS") ||
                        dao.calcularStatusDAO(p.getProntos(), p.getTotalDeItens(), p.getDataDeEntrega())
                                .equalsIgnoreCase(status))
                .toList();

        preencherTabela(filtrados);
    }

    // =========================
    private void preencherTabela(List<Pedido> lista) {

        carregando = true;

        model.setRowCount(0);

        for (Pedido p : lista) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getPedidoDeProducao(),
                    p.getPedidoDeCompra(),
                    p.getEmpresa(),
                    formatDate(p.getPrevisaoDeInicio()),
                    formatDate(p.getPrazo()),
                    p.getProntos(),
                    p.getTotalDeItens(),
                    p.getObservacoes(),
                    dao.calcularStatusDAO(p.getProntos(), p.getTotalDeItens(), p.getDataDeEntrega()),
                    formatDate(p.getDataDeEntrega())
            });
        }

        carregando = false;
    }

    // =========================
    private void atualizarBanco(int linha, int id) {

        try {

            int prontos = parseIntSafe(model.getValueAt(linha, 6));
            int total = parseIntSafe(model.getValueAt(linha, 7));
            String obs = safe(model.getValueAt(linha, 8));

            Object valor = model.getValueAt(linha, 10);
            Date dataDeEntrega = parseDate(String.valueOf(valor));

            if (dataDeEntrega != null){
                LocalDate hoje = LocalDate.now();

                if (dataDeEntrega.toLocalDate().isAfter(hoje)) {
                    JOptionPane.showMessageDialog(this,
                        "A data de entrega não pode ser maior que hoje");

                    model.setValueAt("", linha, 10);
                    return;
                }
            }

            carregando = true;

            model.setValueAt(dataDeEntrega, linha, 10);

            carregando = false;

            Pedido p = new Pedido();
            p.setId(id);
            p.setPedidoDeProducao(safe(model.getValueAt(linha, 1)));
            p.setPedidoDeCompra(safe(model.getValueAt(linha, 2)));
            p.setEmpresa(safe(model.getValueAt(linha, 3)));

            p.setPrevisaoDeInicio(parseDate(safe(model.getValueAt(linha, 4))));
            p.setPrazo(parseDate(safe(model.getValueAt(linha, 5))));

            p.setProntos(parseIntSafe(model.getValueAt(linha, 6)));
            p.setTotalDeItens(parseIntSafe(model.getValueAt(linha, 7)));

            p.setObservacoes(safe(model.getValueAt(linha, 8)));

            p.setDataDeEntrega(dataDeEntrega);

            dao.atualizarPedido(p);

            carregar();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar!" + ex.getMessage());
        }
    }

    // =========================
    private void excluirPedido() {

        int row = tabela.getSelectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja excluir este pedido?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.excluir(id);
                carregar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir!");
            }
        }
    }

    private void imprimir() {
        try {
            tabela.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao imprimir!");
        }
    }

    // =========================
    // EXPORTAR CSV (COM ESCOLHA)
    // =========================
    private void exportarCSV() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar CSV");
        fileChooser.setSelectedFile(new File("pedidos.csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File fileToSave = fileChooser.getSelectedFile();

            try (PrintWriter pw = new PrintWriter(new FileWriter(fileToSave))) {

                for (int i = 0; i < model.getRowCount(); i++) {

                    for (int j = 1; j < model.getColumnCount(); j++) {

                        String val = String.valueOf(model.getValueAt(i, j))
                                .replace(";", ",");

                        pw.print(val);

                        if (j < model.getColumnCount() - 1) pw.print(";");
                    }

                    pw.println();
                }

                JOptionPane.showMessageDialog(this, "Exportado com sucesso!");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar!");
            }
        }
    }

    // =========================
    // HELPERS
    // =========================
    private String formatDate(Object obj) {
        if (obj == null) return "";
        if (obj instanceof Date d) {
            return d.toLocalDate().format(fmt);
        }
        return obj.toString();
    }

    private int parseIntSafe(Object obj) {
        try {
            return Integer.parseInt(obj.toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String safe(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(dateStr, fmt);
            return Date.valueOf(localDate);
        } catch (Exception e) {
            return null;
        }
    }

}