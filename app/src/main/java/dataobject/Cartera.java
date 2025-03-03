package dataobject;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Vector;

import androidx.annotation.NonNull;

public class Cartera   {

    public String documento;
    public String codigo;
    public String consecutivo;
    public String condPago;
    public String concepto;
    public double saldo;
    public int dias;
    public String fechaVencto;
    public String fecha;
    public String nombre;
    public String descripcion;
    public int indicador;
    public String vendedor;
    public String estado;
    public boolean seleccionado;
    public boolean facturaSeleccionadaGestion;
    public String formaPago;
    public int idUnicoCartera;
    public String documentoFinanciero;

    private ArrayList<Cartera> carteraslista;
    public int Validado;
    public String codSap;
    public boolean modificado;
    public boolean sincronizado;
    public boolean registrado;
    public String observacion;


    public Cartera() {
    }

    public Cartera(String documento, String concepto, double saldo) {
        this.documento = documento;
        this.concepto = concepto;
        this.saldo = saldo;
    }

    public Cartera(String documento, String concepto, double saldo, int idUnicoCartera) {
        this.documento = documento;
        this.concepto = concepto;
        this.saldo = saldo;
        this.idUnicoCartera = idUnicoCartera;
    }

    public Cartera(ArrayList<Cartera> carteraslista, String documento, String concepto, double saldo, int idUnicoCartera) {
        this.documento = documento;
        this.concepto = concepto;
        this.saldo = saldo;
        this.carteraslista = carteraslista;
        this.idUnicoCartera = idUnicoCartera;
    }

    public String getDocumentoFinanciero() {
        return documentoFinanciero;
    }

    public void setDocumentoFinanciero(String documentoFinanciero) {
        this.documentoFinanciero = documentoFinanciero;
    }

    public int getIdUnicoCartera() {
        return idUnicoCartera;
    }

    public void setIdUnicoCartera(int idUnicoCartera) {
        this.idUnicoCartera = idUnicoCartera;
    }

    public ArrayList<Cartera> getCarteraslista() {
        return this.carteraslista;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCondPago() {
        return condPago;
    }

    public void setCondPago(String condPago) {
        this.condPago = condPago;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public String getFechaVencto() {
        return fechaVencto;
    }

    public void setFechaVencto(String fechaVencto) {
        this.fechaVencto = fechaVencto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIndicador() {
        return indicador;
    }

    public void setIndicador(int indicador) {
        this.indicador = indicador;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public boolean isFacturaSeleccionadaGestion() {
        return facturaSeleccionadaGestion;
    }

    public void setFacturaSeleccionadaGestion(boolean facturaSeleccionadaGestion) {
        this.facturaSeleccionadaGestion = facturaSeleccionadaGestion;
    }



    public int getValidado() {
        return Validado;
    }

    public void setValidado(int validado) {
        Validado = validado;
    }

    public String getCodSap() {
        return codSap;
    }

    public void setCodSap(String codSap) {
        this.codSap = codSap;
    }

    public boolean isModificado() {
        return modificado;
    }

    public void setModificado(boolean modificado) {
        this.modificado = modificado;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }

    public boolean isRegistrado() {
        return registrado;
    }

    public void setRegistrado(boolean registrado) {
        this.registrado = registrado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @NonNull
    @Override
    public String toString() {
        return documento;
    }
}
