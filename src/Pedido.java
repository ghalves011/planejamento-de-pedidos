package src;

import java.sql.Date;

public class Pedido {

    private int id;
    private String pedidoDeProducao;
    private String pedidoDeCompra;
    private String empresa;
    private Date previsaoDeInicio;
    private Date prazo;
    private int prontos;
    private int totalDeItens;
    private String observacoes;
    private String status;
    private Date dataDeEntrega;

    // Construtor completo (SEM status)
    public Pedido(String pedidoDeProducao, String pedidoDeCompra, String empresa,
                  Date previsaoDeInicio, Date prazo,
                  int prontos, int totalDeItens,
                  String observacoes, String status, Date dataDeEntrega) {

        this.pedidoDeProducao = pedidoDeProducao;
        this.pedidoDeCompra = pedidoDeCompra;
        this.empresa = empresa;
        this.previsaoDeInicio = previsaoDeInicio;
        this.prazo = prazo;
        this.prontos = prontos;
        this.totalDeItens = totalDeItens;
        this.observacoes = observacoes;
        this.status = status;
        this.dataDeEntrega = dataDeEntrega;
    }

    // Construtor vazio
    public Pedido() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPedidoDeProducao() {
        return pedidoDeProducao;
    }

    public void setPedidoDeProducao(String pedidoDeProducao) {
        this.pedidoDeProducao = pedidoDeProducao;
    }

    public String getPedidoDeCompra() {
        return pedidoDeCompra;
    }

    public void setPedidoDeCompra(String pedidoDeCompra) {
        this.pedidoDeCompra = pedidoDeCompra;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Date getPrevisaoDeInicio() {
        return previsaoDeInicio;
    }

    public void setPrevisaoDeInicio(Date previsaoDeInicio) {
        this.previsaoDeInicio = previsaoDeInicio;
    }

    public Date getPrazo() {
        return prazo;
    }

    public void setPrazo(Date prazo) {
        this.prazo = prazo;
    }

    public int getProntos() {
        return prontos;
    }

    public void setProntos(int prontos) {
        this.prontos = prontos;
    }

    public int getTotalDeItens() {
        return totalDeItens;
    }

    public void setTotalDeItens(int totalDeItens) {
        this.totalDeItens = totalDeItens;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataDeEntrega() {
        return dataDeEntrega;
    }

    public void setDataDeEntrega(Date dataDeEntrega) {
        this.dataDeEntrega = dataDeEntrega;
    }
}