package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class PedidoDAO {

    // =========================
    // CONEXÃO
    // =========================
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado.", e);
        }
        String url = "jdbc:mysql://localhost:3306/PlanejamentoDePedidos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    // =========================
    // INSERT
    // =========================
    public void inserir(Pedido pedido) throws SQLException {

        String sql = "INSERT INTO pedidos "
                + "(pedido_de_producao, pedido_de_compra, empresa, previsao_de_inicio, "
                + "prazo, prontos, total_de_itens, observacoes, data_de_entrega, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, safe(pedido.getPedidoDeProducao()));
            ps.setString(2, safe(pedido.getPedidoDeCompra()));
            ps.setString(3, safe(pedido.getEmpresa()));
            ps.setDate(4, pedido.getPrevisaoDeInicio());
            ps.setDate(5, pedido.getPrazo());
            ps.setInt(6, pedido.getProntos());
            ps.setInt(7, pedido.getTotalDeItens());
            ps.setString(8, safe(pedido.getObservacoes()));

            if (pedido.getDataDeEntrega() != null) {
                ps.setDate(9, pedido.getDataDeEntrega());
            } else {
                ps.setNull(9, Types.DATE);
            }

            // STATUS AUTOMÁTICO
            ps.setString(10, calcularStatusDAO(
                    pedido.getProntos(),
                    pedido.getTotalDeItens(),
                    pedido.getDataDeEntrega()
            ));

            ps.executeUpdate();
            System.out.println("Pedido inserido com sucesso!" + pedido.getPedidoDeProducao());

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    pedido.setId(rs.getInt(1));
                }
            }
        }
    }

    // =========================
    // SELECT
    // =========================
    public List<Pedido> listarTodos() throws SQLException {

        List<Pedido> list = new ArrayList<>();

        String sql = "SELECT * FROM pedidos ORDER BY previsao_de_inicio ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Pedido p = new Pedido(
                        rs.getString("pedido_de_producao"),
                        rs.getString("pedido_de_compra"),
                        rs.getString("empresa"),
                        rs.getDate("previsao_de_inicio"),
                        rs.getDate("prazo"),
                        rs.getInt("prontos"),
                        rs.getInt("total_de_itens"),
                        rs.getString("observacoes"),
                        calcularStatusDAO(
                            rs.getInt("prontos"),
                            rs.getInt("total_de_itens"),
                            rs.getDate("data_de_entrega")
                        ),
                        rs.getDate("data_de_entrega")
                );

                p.setId(rs.getInt("id"));
                list.add(p);
            }
        }

        return list;
    }

    // =========================
    // UPDATE 
    // =========================
    public void atualizarPedido(Pedido p) throws SQLException {

        String sql = "UPDATE pedidos SET "
                + "pedido_de_producao = ?, "
                + "pedido_de_compra = ?, "
                + "empresa = ?, "
                + "previsao_de_inicio = ?, "
                + "prazo = ?, "
                + "prontos = ?, "
                + "total_de_itens = ?, "
                + "observacoes = ?, "
                + "status = ?, "
                + "data_de_entrega = ? "
                + "WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, safe(p.getPedidoDeProducao()));
            ps.setString(2, safe(p.getPedidoDeCompra()));
            ps.setString(3, safe(p.getEmpresa()));
            ps.setDate(4, p.getPrevisaoDeInicio());
            ps.setDate(5, p.getPrazo());
            ps.setInt(6, p.getProntos());
            ps.setInt(7, p.getTotalDeItens());
            ps.setString(8, safe(p.getObservacoes()));
            ps.setString(9, calcularStatusDAO(
                    p.getProntos(),
                    p.getTotalDeItens(),
                    p.getDataDeEntrega()
            ));

            if (p.getDataDeEntrega() != null) {
                ps.setDate(10, p.getDataDeEntrega());
            } else {
                ps.setNull(10, Types.DATE);
            }

            ps.setInt(11, p.getId());

            ps.executeUpdate();
        }
    }

    // =========================
    // DELETE
    // =========================
    public void excluir(int id) throws SQLException {

        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // =========================
    // FILTRO
    // =========================
    public List<Pedido> filtrar(String empresa, String status) throws SQLException {

        List<Pedido> list = new ArrayList<>();

        String sql = "SELECT * FROM pedidos WHERE 1=1";

        if (empresa != null && !empresa.trim().isEmpty()) {
            sql += " AND empresa LIKE ?";
        }

        if (status != null && !status.equals("TODOS")) {

            if (status.equals("PRONTO")) {
                sql += " AND prontos >= total_de_itens";

            } else if (status.equals("EM ANDAMENTO") || status.equals("EM PRODUÇÃO")) {
                sql += " AND prontos < total_de_itens";
            }

            else if (status.equals("ENTREGUE")) {
                sql += " AND data_de_entrega IS NOT NULL";
            }
        }

        sql += " ORDER BY prazo ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;

            if (empresa != null && !empresa.trim().isEmpty()) {
                ps.setString(i++, "%" + empresa + "%");
            }

            if (status != null
                    && !status.equals("TODOS")
                    && !status.equals("PRONTO")
                    && !status.equals("EM ANDAMENTO")
                    && !status.equals("EM PRODUÇÃO")) {

                ps.setString(i++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Pedido p = new Pedido(
                            rs.getString("pedido_de_producao"),
                            rs.getString("pedido_de_compra"),
                            rs.getString("empresa"),
                            rs.getDate("previsao_de_inicio"),
                            rs.getDate("prazo"),
                            rs.getInt("prontos"),
                            rs.getInt("total_de_itens"),
                            rs.getString("observacoes"),
                            rs.getString("status"),
                            rs.getDate("data_de_entrega")
                    );

                    p.setId(rs.getInt("id"));
                    list.add(p);
                }
            }
        }

        return list;
    }

    // =========================
    // HELPERS
    // =========================
    private String safe(String s) {
        return (s == null) ? "" : s;
    }

    public String calcularStatusDAO(int prontos, int total, Date dataDeEntrega) {
        if (dataDeEntrega != null) {
            return "ENTREGUE";
        } else if (prontos < total && total > 0) {
            return "EM ANDAMENTO";
        } else {
            return "PRONTO";
        }
    }

    public void limparTabela() throws Exception {
        String sql = "DELETE FROM pedidos";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}