package src;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class TelaCadastroPedidos extends JFrame {

    private JTextField tfProducao = new JTextField();
    private JTextField tfCompra = new JTextField();
    private JTextField tfEmpresa = new JTextField();
    private JTextField tfPrevInicio = new JTextField();
    private JTextField tfPrazo = new JTextField();
    private JTextField tfProntos = new JTextField();
    private JTextField tfTotal = new JTextField();
    private JTextField tfObs = new JTextField();

    private PedidoDAO dao = new PedidoDAO();

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    public TelaCadastroPedidos() {

        setTitle("Cadastro de Pedido");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(10, 2, 5, 5));

        add(new JLabel("Produção")); add(tfProducao);
        add(new JLabel("Compra")); add(tfCompra);
        add(new JLabel("Empresa")); add(tfEmpresa);
        add(new JLabel("Previsão Início")); add(tfPrevInicio);
        add(new JLabel("Prazo")); add(tfPrazo);
        add(new JLabel("Prontos")); add(tfProntos);
        add(new JLabel("Total")); add(tfTotal);
        add(new JLabel("Obs")); add(tfObs);

        JButton btnSalvar = new JButton("Salvar");
        add(new JLabel());
        add(btnSalvar);

        btnSalvar.addActionListener(e -> salvar());

        setVisible(true);
    }

    private void salvar() {
        try {

            Date prev = parseDate(tfPrevInicio.getText());
            Date prazo = parseDate(tfPrazo.getText());

            int prontos = Integer.parseInt(tfProntos.getText());
            int total = Integer.parseInt(tfTotal.getText());

            String status = (prontos >= total && total > 0)
                    ? "PRONTO"
                    : "EM ANDAMENTO";

            Pedido p = new Pedido(
                    tfProducao.getText(),
                    tfCompra.getText(),
                    tfEmpresa.getText(),
                    prev,
                    prazo,
                    prontos,
                    total,
                    tfObs.getText(),
                    status,
                    null
            );

            dao.inserir(p);

            JOptionPane.showMessageDialog(this, "Pedido cadastrado com sucesso!");

            tfProducao.setText("");
            tfCompra.setText("");
            tfEmpresa.setText("");
            tfPrevInicio.setText("");
            tfPrazo.setText("");
            tfProntos.setText("");
            tfTotal.setText("");
            tfObs.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private Date parseDate(String text) throws Exception {
        if (text.trim().isEmpty()) return null;
        java.util.Date d = SDF.parse(text);
        return new Date(d.getTime());
    }
}