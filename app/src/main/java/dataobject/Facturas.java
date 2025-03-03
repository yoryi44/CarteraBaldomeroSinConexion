package dataobject;

import androidx.annotation.NonNull;

public class Facturas {

    public String codCliente;
    public String documento;
    public String consecutivo;
    public String tipoDocumento;
    public String formaPago;
    public double valor;
    public String valorUpdate;
    public String fecha;
    public String numRegFormaPago;
    public String Usuario;
    public String nroCheque;
    public String datafonof;
    public String idPago;
    public String referencia;
    public String observaciones;
    public String fechaConsignacion;
    public String cuentaBancaria;
    public String nroRecibo;
    public String viaPago;
    public Float valorDocumento;
    public Float valorPagado;
    public String banco;
    public String numeroCheque;
    public String nombrePropietario;
    public String idenFoto;
    public String documentoFinanciero;
    public String fechaRecibo;



    public Facturas() {
    }

    public Facturas(String codCliente, double valor, String idPago) {
        this.codCliente = codCliente;
        this.valor = valor;
        this.idPago = idPago;
    }

    public String getDocumentoFinanciero() {
        return documentoFinanciero;
    }

    public void setDocumentoFinanciero(String documentoFinanciero) {
        this.documentoFinanciero = documentoFinanciero;
    }


    public Float getValorPagado() {
        return valorPagado;
    }

    public void setValorPagado(Float valorPagado) {
        this.valorPagado = valorPagado;
    }

    public String getIdenFoto() {
        return idenFoto;
    }

    public void setIdenFoto(String idenFoto) {
        this.idenFoto = idenFoto;
    }

    public String getBanco() {
        return banco;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getNumeroCheque() {
        return numeroCheque;
    }

    public void setNumeroCheque(String numeroCheque) {
        this.numeroCheque = numeroCheque;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFechaConsignacion() {
        return fechaConsignacion;
    }

    public void setFechaConsignacion(String fechaConsignacion) {
        this.fechaConsignacion = fechaConsignacion;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNroRecibo() {
        return nroRecibo;
    }

    public void setNroRecibo(String nroRecibo) {
        this.nroRecibo = nroRecibo;
    }

    public String getViaPago() {
        return viaPago;
    }

    public void setViaPago(String viaPago) {
        this.viaPago = viaPago;
    }

    public Float getValorDocumento() {
        return valorDocumento;
    }

    public void setValorDocumento(Float valorDocumento) {
        this.valorDocumento = valorDocumento;
    }

    public Facturas(double valor) {
        this.valor = valor;
    }

    public String getValorUpdate() {
        return valorUpdate;
    }

    public void setValorUpdate(String valorUpdate) {
        this.valorUpdate = valorUpdate;
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNumRegFormaPago() {
        return numRegFormaPago;
    }

    public void setNumRegFormaPago(String numRegFormaPago) {
        this.numRegFormaPago = numRegFormaPago;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getNroCheque() {
        return nroCheque;
    }

    public void setNroCheque(String nroCheque) {
        this.nroCheque = nroCheque;
    }

    public String getDatafonof() {
        return datafonof;
    }

    public void setDatafonof(String datafonof) {
        this.datafonof = datafonof;
    }

    @NonNull
    @Override
    public String toString() {
        return documento;
    }
}


