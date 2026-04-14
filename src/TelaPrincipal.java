package src;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {

        setTitle("Menu Principal");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton btnCadastro = new JButton("Cadastrar Pedido");
        JButton btnListagem = new JButton("Listar Pedidos");

        Dimension btnSize = new Dimension(180, 40);

        btnCadastro.setMaximumSize(btnSize);
        btnListagem.setMaximumSize(btnSize);

        btnCadastro.setPreferredSize(btnSize);
        btnListagem.setPreferredSize(btnSize);

        btnCadastro.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnListagem.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(btnCadastro);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnListagem);
        panel.add(Box.createVerticalGlue());

        add(panel);

        btnCadastro.addActionListener(e -> new TelaCadastroPedidos().setVisible(true));
        btnListagem.addActionListener(e -> new TelaListagemPedidos().setVisible(true));

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaPrincipal::new);
    }
}