package dataobject;

public class FacturasRealizadas {

    public String numeroRecibo;
    public String codigoCliente;
    public String fechaCreacion;
    public String fechaCierre;
    public double montoPendientes;
    public String status;
    public String claseDocumento;
    public String sociedad;
    public String cod_vendedor;
    public String referencia;
    public double valorDocumento;
    public double valorConsignado;
    public String cuentaBancaria;
    public String monedaConsiganada;
    public String moneda;
    public String comprobanteFiscal;
    public String doctoFinanciero;
    public String observaciones;
    public String viaPago;
    public String denominacion;
    public String usuario;
    public String operacionCME;
    public String idPago;
    public String sincronizado;
    public String banco;
    public String numeroCheqe;
    public String numeroAnulacion;
    public String nombrePropietario;
    public String documentoFactura;
    public String codigoCausal;

    public FacturasRealizadas() {
    }

    public FacturasRealizadas(String numeroRecibo, String codigoCliente, String fechaCreacion, String fechaCierre, double montoPendientes, String status, String claseDocumento, String sociedad, String cod_vendedor, String referencia, double valorDocumento, double valorConsignado, String cuentaBancaria, String monedaConsiganada, String moneda, String comprobanteFiscal, String doctoFinanciero, String observaciones, String viaPago, String usuario, String operacionCME, String idPago, String sincronizado, String banco, String numeroCheqe, String nombrePropietario) {
        this.numeroRecibo = numeroRecibo;
        this.codigoCliente = codigoCliente;
        this.fechaCreacion = fechaCreacion;
        this.fechaCierre = fechaCierre;
        this.montoPendientes = montoPendientes;
        this.status = status;
        this.claseDocumento = claseDocumento;
        this.sociedad = sociedad;
        this.cod_vendedor = cod_vendedor;
        this.referencia = referencia;
        this.valorDocumento = valorDocumento;
        this.valorConsignado = valorConsignado;
        this.cuentaBancaria = cuentaBancaria;
        this.monedaConsiganada = monedaConsiganada;
        this.moneda = moneda;
        this.comprobanteFiscal = comprobanteFiscal;
        this.doctoFinanciero = doctoFinanciero;
        this.observaciones = observaciones;
        this.viaPago = viaPago;
        this.usuario = usuario;
        this.operacionCME = operacionCME;
        this.idPago = idPago;
        this.sincronizado = sincronizado;
        this.banco = banco;
        this.numeroCheqe = numeroCheqe;
        this.nombrePropietario = nombrePropietario;
    }

    public String getCodigoCausal() {
        return codigoCausal;
    }

    public void setCodigoCausal(String codigoCausal) {
        this.codigoCausal = codigoCausal;
    }

    public String getNumeroAnulacion() {
        return numeroAnulacion;
    }

    public void setNumeroAnulacion(String numeroAnulacion) {
        this.numeroAnulacion = numeroAnulacion;
    }

    public String getDocumentoFactura() {
        return documentoFactura;
    }

    public void setDocumentoFactura(String documentoFactura) {
        this.documentoFactura = documentoFactura;
    }

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public double getMontoPendientes() {
        return montoPendientes;
    }

    public void setMontoPendientes(double montoPendientes) {
        this.montoPendientes = montoPendientes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClaseDocumento() {
        return claseDocumento;
    }

    public void setClaseDocumento(String claseDocumento) {
        this.claseDocumento = claseDocumento;
    }

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

    public String getCod_vendedor() {
        return cod_vendedor;
    }

    public void setCod_vendedor(String cod_vendedor) {
        this.cod_vendedor = cod_vendedor;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public double getValorDocumento() {
        return valorDocumento;
    }

    public void setValorDocumento(double valorDocumento) {
        this.valorDocumento = valorDocumento;
    }

    public double getValorConsignado() {
        return valorConsignado;
    }

    public void setValorConsignado(double valorConsignado) {
        this.valorConsignado = valorConsignado;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public String getMonedaConsiganada() {
        return monedaConsiganada;
    }

    public void setMonedaConsiganada(String monedaConsiganada) {
        this.monedaConsiganada = monedaConsiganada;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getComprobanteFiscal() {
        return comprobanteFiscal;
    }

    public void setComprobanteFiscal(String comprobanteFiscal) {
        this.comprobanteFiscal = comprobanteFiscal;
    }

    public String getDoctoFinanciero() {
        return doctoFinanciero;
    }

    public void setDoctoFinanciero(String doctoFinanciero) {
        this.doctoFinanciero = doctoFinanciero;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getViaPago() {
        return viaPago;
    }

    public void setViaPago(String viaPago) {
        this.viaPago = viaPago;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getOperacionCME() {
        return operacionCME;
    }

    public void setOperacionCME(String operacionCME) {
        this.operacionCME = operacionCME;
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(String sincronizado) {
        this.sincronizado = sincronizado;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumeroCheqe() {
        return numeroCheqe;
    }

    public void setNumeroCheqe(String numeroCheqe) {
        this.numeroCheqe = numeroCheqe;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }
}
