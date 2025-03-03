package metodosPago;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import androidx.annotation.NonNull;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.MetodosDePagoActivity;
import co.com.celuweb.carterabaldomero.MultiplesFotosActivity;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Anticipo;
import dataobject.Bancos;
import dataobject.Cartera;
import dataobject.ClienteSincronizado;
import dataobject.CuentasBanco;
import dataobject.Facturas;
import dataobject.FormaPago;
import dataobject.Fotos;
import dataobject.Lenguaje;
import dataobject.Parcial;
import dataobject.PreciosFacturasParcial;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesCartera;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesFotos;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesParcial;
import utilidades.Utilidades;

public class MetodoDePagoTarjeta {

    public static Dialog dialogo;

    public static Vector<Bancos> listaParametrosBancosSpinner;
    public static Vector<CuentasBanco> listaParametrosCuentas;

    /**
     * VISTA DIALOGO TARJETA
     *
     * @param contexto
     * @param titulo
     * @param texto
     * @param onClickListenerAceptar
     * @param onClickListenerCancelar
     */
    public static void vistaDialogoTarjetaCredito(final Context contexto,
                                                  @NonNull String titulo, @NonNull String texto, View.
                                                          OnClickListener onClickListenerAceptar,
                                                  View.OnClickListener onClickListenerCancelar) {

        ImageView cancelarFormaPagoFE, guardarFormaPagoFE, guardarFormaPagoPendienteTarje;
        final TextView tvNROTarjeta, tvDescripcionTarjeta, tvFechaFragTarje;
        final TextView tvNombreTitular, tvFechaExpiracion, tvMonto, simboloTarjeta;
        final EditText tvValorFragEfec;
        final Cartera facturaCartera;
        final FormaPago formaPago;
        final Anticipo anticipo;
        List<Facturas> listaFacturas2;

        final List<Facturas> listsaFacturasParcialTotal2;
        List<Facturas> listaFacturas4;
        final List<Facturas> listsaFacturasParcialTotal;
        final List<Facturas> listsaFacturasParcialTotalPendientes;
        final ClienteSincronizado clienteSel;
        final Button tomarFoto;
        final Lenguaje lenguajeElegido;
        final TextView txtCompaReciboDinero, txtCompaReciboDinero1, txtCompaReciboDinero2, tituloFechaTarjeta, tituloMontoTarjeta, tituloNroTarjeta, tituloNombreTitularTarjeta, tituloCuentaDestinoTarjeta;


        dialogo = new Dialog(contexto);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.fragment_tarjeta_credito);

        Gson gson223 = new Gson();
        String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(contexto);
        lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);


        simboloTarjeta = dialogo.findViewById(R.id.simboloTarjeta);
        tvValorFragEfec = dialogo.findViewById(R.id.tvValorFragTarjeta);
        tvNROTarjeta = dialogo.findViewById(R.id.tvNROTarjeta);
        tvFechaFragTarje = dialogo.findViewById(R.id.tvFechaFragTarje);
        //   tvDescripcionTarjeta = dialogo.findViewById(R.id.tvDescripcionTarjeta);
        tvNombreTitular = dialogo.findViewById(R.id.tvNombreTitular);
        tvMonto = dialogo.findViewById(R.id.tvMonto);
        guardarFormaPagoFE = dialogo.findViewById(R.id.guardarFormaPagoFE);
        tomarFoto = dialogo.findViewById(R.id.tomarFoto);

        txtCompaReciboDinero = dialogo.findViewById(R.id.txtCompaReciboDinero);
        txtCompaReciboDinero1 = dialogo.findViewById(R.id.txtCompaReciboDinero1);
        txtCompaReciboDinero2 = dialogo.findViewById(R.id.txtCompaReciboDinero2);

        tituloFechaTarjeta = dialogo.findViewById(R.id.tituloFechaTarjeta);
        tituloMontoTarjeta = dialogo.findViewById(R.id.tituloMontoTarjeta);
        tituloNroTarjeta = dialogo.findViewById(R.id.tituloNroTarjeta);
        tituloNombreTitularTarjeta = dialogo.findViewById(R.id.tituloNombreTitularTarjeta);
        tituloCuentaDestinoTarjeta = dialogo.findViewById(R.id.tituloCuentaDestinoTarjeta);


        String tipoUsuario = "";
        tipoUsuario = DataBaseBO.cargarTipoUsuarioApp();

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                txtCompaReciboDinero.setText("Register payment method");
                txtCompaReciboDinero1.setText("Card");
                txtCompaReciboDinero2.setText("Amount to be collected: ");
                tituloFechaTarjeta.setText("Date:");
                tituloMontoTarjeta.setText("Amount:");
                tituloNroTarjeta.setText("Card Number");
                tituloNombreTitularTarjeta.setText("Name Cardholder:");
                tituloCuentaDestinoTarjeta.setText("Destination Account:");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {


            }
        }

        String empresa = "";
        String monedaTipo = "";
        String consecutivo = "";
        String consecutivofinal = "";
        int consecutivoInicial = 0;
        String consecutivoNegocio = "";
        String consecutivoVendedor = "";
        String codigoVendedor = "";

        double DiferenciaFormasPago;
        double DiferenciaFormasPagoE = 0;
        double DiferenciaFormasPagoPEN = 0;
        codigoVendedor = DataBaseBO.cargarCodigo();
        empresa = DataBaseBO.cargarEmpresa();
        monedaTipo = DataBaseBO.cargarMoneda();

        final Spinner spinnerCuentasBanco = dialogo.findViewById(R.id.spinnerCuentasBancoTarjeta);
        spinnerCuentasBanco.setVisibility(View.VISIBLE);

        String[] items2;
        Vector<String> listaItems = new Vector<String>();
        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                listaItems.addElement("Select");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                listaItems.addElement("Seleccione");

            }
        }
        listaParametrosCuentas = DataBaseBO.cargarCuentasBancosSolo(listaItems);

        if (listaItems.size() > 0) {
            items2 = new String[listaItems.size()];
            listaItems.copyInto(items2);

        } else {
            items2 = new String[]{};

            if (listaParametrosCuentas != null)
                listaParametrosCuentas.removeAllElements();
        }

        ArrayAdapter adapterCuentasBanco = new ArrayAdapter<>(contexto.getApplicationContext(), android.R.layout.simple_spinner_item, items2);
        adapterCuentasBanco.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuentasBanco.setAdapter(adapterCuentasBanco);
        adapterCuentasBanco.notifyDataSetChanged();
        String param = "";
        param = spinnerCuentasBanco.getSelectedItem().toString();

        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("AABR")) {
            simboloTarjeta.setText("$");
        }
        if (finalEmpresa.equals("ADHB")) {
            simboloTarjeta.setText("$");
        }
        if (finalEmpresa.equals("AGSC")) {
            simboloTarjeta.setText("$");
        }
        if (finalEmpresa.equals("AGGC")) {
            simboloTarjeta.setText("Q");
        }
        if (finalEmpresa.equals("AFPN")) {
            simboloTarjeta.setText("C$");
        }
        if (finalEmpresa.equals("AFPZ")) {
            simboloTarjeta.setText("₡");
        }
        if (finalEmpresa.equals("AGCO")) {
            simboloTarjeta.setText("$");
        }
        if (finalEmpresa.equals("AGAH")) {
            simboloTarjeta.setText("₡");
        }
        if (finalEmpresa.equals("AGDP")) {
            simboloTarjeta.setText("Q");
        }
        if (finalEmpresa.equals("AGUC")) {
            simboloTarjeta.setText("$");
        }

        final String CERO = "0";
        final String BARRA = "-";
        final Calendar c = Calendar.getInstance();

        final int mes = c.get(Calendar.MONTH);
        final int dia = c.get(Calendar.DAY_OF_MONTH);
        final int anio = c.get(Calendar.YEAR);

        tvFechaFragTarje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog recogerFecha = new DatePickerDialog(contexto, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;
                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                        //Muestro la fecha con el formato deseado
                        tvFechaFragTarje.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);


                    }

                }, anio, mes, dia);
                //Muestro el widget
                recogerFecha.show();
                recogerFecha.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
            }
        });


        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject222 = PreferencesCartera.obteneCarteraSeleccionada(contexto);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesFormaPago.obteneFacturaSeleccionada(contexto);
        formaPago = gson2.fromJson(stringJsonObject2, FormaPago.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject222, collectionType);

        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        final List<String> precios = new ArrayList<>();
        final List<String> valoresfacturas = new ArrayList<>();
        final List<String> preciosAcomparar = new ArrayList<>();
        final List<String> totalFacturas = new ArrayList<>();
        final List<String> preciosParcial = new ArrayList<>();
        final List<String> preciosAnticipo = new ArrayList<>();
        final List<String> documentt = new ArrayList<>();
        final List<String> preciosfacturasLogica = new ArrayList<>();
        final List<String> fechasDocumentos = new ArrayList<>();
        final List<Facturas> listaFacturas3 = new ArrayList<>();
        final List<String> preciosComparar = new ArrayList<>();
        final List<String> listaPrecios = new ArrayList<>();
        final List<String> vendedoresCartera = new ArrayList<>();
        final List<String> preciosListaTotal = new ArrayList<>();

        String claseDocument = "";
        String preciosFacturas = "";
        String documentoFinanciero = "";
        String fechasDocus = "";
        String nombreU = "";
        double precioTotal = 0;
        String document = "";
        String vendedorCartera = "";

        if (facCollection != null) {

            for (Cartera cartera1 : facCollection) {
                vendedorCartera = cartera1.getVendedor();
                vendedoresCartera.add(vendedorCartera);
                document = cartera1.getDocumento();
                precioTotal += cartera1.getSaldo();
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                claseDocument = cartera1.getConcepto();
                fechasDocus = cartera1.getFechaVencto();
                int Position = 2;
                claseDocument = claseDocument.substring(0, Position);
                fechasDocumentos.add(cartera1.getFechaVencto());
                claseDocumento.add(claseDocument);
                documentt.add(document);

                preciosFacturas = String.valueOf(cartera1.getSaldo());
                preciosListaTotal.add(preciosFacturas);
                precios.add(preciosFacturas);
                listaPrecios.add(preciosFacturas);

                documentosFinanciero.add(documentoFinanciero);

            }
        }

        double precioTo = Utilidades.formatearDecimales(precioTotal, 2);

        if (anticipo != null) {
            claseDocumento.add("DZ");
            documentosFinanciero.add(null);
        }


        String nroReciboFacTotalPar = clienteSel.consecutivo;

        // listsaFacturasParcialTotal = DataBaseBO.cargarFacParTotal(nroReciboFacTotalPar, documentosFinanciero);
        listsaFacturasParcialTotal = Utilidades.listaFacturasParcialTotal(contexto);


        for (Facturas fac : listsaFacturasParcialTotal) {
            String acert = "";
            acert = fac.idPago;
            double valorCom = 0;
            valorCom = fac.valor;
            valoresfacturas.add(String.valueOf(Utilidades.formatearDecimales(valorCom, 2)));

        }

        String nroRecibo = "";
        if (anticipo != null) {

            String parametroCme = "A";

            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);

        }

        DiferenciaFormasPagoE = Utilidades.totalFormasPago(contexto);

        DiferenciaFormasPago = (DiferenciaFormasPagoE);

        consecutivo = DataBaseBO.cargarConsecutivo();
        consecutivoNegocio = DataBaseBO.cargarNegocioConsecutivo();
        consecutivoVendedor = DataBaseBO.cargarVendedorConsecutivo();
        int contador = 1;
        int consec1 = Integer.parseInt(consecutivo);
        int vendedorsum = Integer.parseInt(consecutivoVendedor);

        consec1 = consec1 + contador;
        consecutivofinal = (clienteSel.consecutivo);
        consecutivoInicial = (clienteSel.consecutivoInicial);

        double valorfinal = 00;
        double comparar = 0, compararEscrito = 0;

        double salfoAFA = 0;
        double rfv = 0;

        double rfd = (Utilidades.formatearDecimales(Utilidades.totalDifereFAv(contexto.getApplicationContext()), 2));
        salfoAFA = rfd;
        double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsig(contexto.getApplicationContext()), 2));

        if (formaPago != null) {
            if (formaPago.valor > 0) {
                double rrr = (Utilidades.formatearDecimales(formaPago.valor - (precioTo), 2));

                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    if (precioTo + (-salfoAFA) - DiferenciaFormasPago == formaPago.valor - (DiferenciaFormasPago)) {

                        if (precioTo +  (-salfoAFA) - DiferenciaFormasPago +  (-salfoAFA) == formaPago.valor - DiferenciaFormasPago +  (-salfoAFA) ) {
                            if (DiferenciaFormasPago == sumaXValorConsignado) {
                                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                                String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago));
                                tvMonto.setText(resultados);
                                valorfinal = formaPago.valor - DiferenciaFormasPago ;
                                comparar = valorfinal;
                            }else{
                                if (salfoAFA < 0) {

                                    if (DiferenciaFormasPago == sumaXValorConsignado) {
                                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                                        String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago - (-salfoAFA)));
                                        tvMonto.setText(resultados);
                                        valorfinal = formaPago.valor - DiferenciaFormasPago - (-salfoAFA);
                                        comparar = valorfinal;
                                    }
                                    if (DiferenciaFormasPago < sumaXValorConsignado) {
                                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                                        String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago + (-salfoAFA)));
                                        tvMonto.setText(resultados);
                                        valorfinal = formaPago.valor - DiferenciaFormasPago + (-salfoAFA);
                                        comparar = valorfinal;
                                    }
                                    if (DiferenciaFormasPago > sumaXValorConsignado) {
                                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                                        String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago - (-salfoAFA)));
                                        tvMonto.setText(resultados);
                                        valorfinal = formaPago.valor - DiferenciaFormasPago - (-salfoAFA);
                                        comparar = valorfinal;
                                    }



                                }else {
                                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                                    String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago));
                                    tvMonto.setText(resultados);
                                    valorfinal = formaPago.valor - DiferenciaFormasPago;
                                    comparar = valorfinal;
                                }

                            }

                        }else{
                            if (salfoAFA < 0) {
                                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                                String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago - (-salfoAFA)));
                                tvMonto.setText(resultados);
                                valorfinal = formaPago.valor - DiferenciaFormasPago - (-salfoAFA);
                                comparar = valorfinal;
                            }else {
                                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                                String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago));
                                tvMonto.setText(resultados);
                                valorfinal = formaPago.valor - DiferenciaFormasPago;
                                comparar = valorfinal;
                            }

                        }

                    }else{

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                        if (DiferenciaFormasPago + rrr == sumaXValorConsignado) {
                            String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago - rrr));
                            tvMonto.setText(resultados);
                            valorfinal = formaPago.valor - DiferenciaFormasPago - rrr;
                            comparar = valorfinal;
                        }else{
                            String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago));
                            tvMonto.setText(resultados);
                            valorfinal = formaPago.valor - DiferenciaFormasPago;
                            comparar = valorfinal;

                        }
                    }




                } else {



                    if (precioTo + (-salfoAFA) - DiferenciaFormasPago == formaPago.valor - (DiferenciaFormasPago)) {

                        /**  NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                         String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago));
                         tvMonto.setText(resultados);
                         valorfinal = formaPago.valor - DiferenciaFormasPago;
                         comparar = valorfinal; **/


                        if (DiferenciaFormasPago == sumaXValorConsignado) {
                            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                            String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago));
                            tvMonto.setText(resultados);
                            valorfinal = formaPago.valor - DiferenciaFormasPago ;
                            comparar = valorfinal;
                        }else{
                            if (salfoAFA < 0) {

                                if (DiferenciaFormasPago == sumaXValorConsignado) {
                                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                                    String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago - (-salfoAFA)));
                                    tvMonto.setText(resultados);
                                    valorfinal = formaPago.valor - DiferenciaFormasPago - (-salfoAFA);
                                    comparar = valorfinal;
                                }
                                if (DiferenciaFormasPago < sumaXValorConsignado) {
                                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                                    String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago + (-salfoAFA)));
                                    tvMonto.setText(resultados);
                                    valorfinal = formaPago.valor - DiferenciaFormasPago + (-salfoAFA);
                                    comparar = valorfinal;
                                }
                                if (DiferenciaFormasPago > sumaXValorConsignado) {
                                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                                    String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago - (-salfoAFA)));
                                    tvMonto.setText(resultados);
                                    valorfinal = formaPago.valor - DiferenciaFormasPago - (-salfoAFA);
                                    comparar = valorfinal;
                                }



                            }else {
                                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                                String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago));
                                tvMonto.setText(resultados);
                                valorfinal = formaPago.valor - DiferenciaFormasPago;
                                comparar = valorfinal;
                            }

                        }



                    }else{

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));

                        if (DiferenciaFormasPago + rrr == sumaXValorConsignado) {
                            String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago - rrr));
                            tvMonto.setText(resultados);
                            valorfinal = formaPago.valor - DiferenciaFormasPago - rrr;
                            comparar = valorfinal;
                        }else{
                            String resultados = (formatoNumero.format(formaPago.valor - DiferenciaFormasPago));
                            tvMonto.setText(resultados);
                            valorfinal = formaPago.valor - DiferenciaFormasPago;
                            comparar = valorfinal;

                        }

                    }

                }


            } else if (formaPago.valor == 00) {

                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                    String resultados = (formatoNumero.format(precioTotal - (DiferenciaFormasPago)));
                    tvMonto.setText(resultados);
                    valorfinal = precioTotal;
                    comparar = valorfinal;
                } else {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));

                    String resultados = (formatoNumero.format(precioTotal - (DiferenciaFormasPago)));
                    tvMonto.setText(resultados);
                    valorfinal = precioTotal;
                    comparar = valorfinal;
                }

            }

        }

        if (formaPago == null) {

            if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                String resultados = (formatoNumero.format(anticipo.valor - (DiferenciaFormasPago)));
                tvMonto.setText(resultados);
                valorfinal = anticipo.valor;
                preciosAnticipo.add(String.valueOf(valorfinal));
                comparar = valorfinal;

            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));

                String resultados = (formatoNumero.format(anticipo.valor - (DiferenciaFormasPago)));
                tvMonto.setText(resultados);
                valorfinal = anticipo.valor;
                preciosAnticipo.add(String.valueOf(valorfinal));
                comparar = valorfinal;

            }

        }

        tvNombreTitular.setText("");
        tvNROTarjeta.setText("");


        final String sociedad = empresa;
        final String cod_cliente = clienteSel.getCodigo();
        String cod_Vendedor = "";
        if (tipoUsuario.equals("10")) {
            cod_Vendedor = vendedorCartera;
        } else {
            cod_Vendedor = codigoVendedor;
        }
        final String fecha_Documento = fechasDocus;
        final String fecha_Consignacion = tvFechaFragTarje.getText().toString().trim();
        final String moneda = monedaTipo;
        final String moneda_Consig = monedaTipo;
        final String NCF_Comprobante_fiscal = null;
        final String finalValor = String.valueOf(Double.parseDouble(String.valueOf(valorfinal)));
        final String docto_Financiero = "0";
        final String consecutivo1 = consecutivofinal;
        final int consecutivo2 = consecutivoInicial;
        final String via_Pago = "O";
        final String usuario = codigoVendedor;
        final String operacion_Cme = null;
        final int sincronizado = 0;
        int Position = 2;
        codigoVendedor = codigoVendedor.substring(0, Position);
        final String idPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss");
        final String finalParam = param;
        final double finalValorfinal = valorfinal;
        int numero = (int) (Math.random() * 1000) + 1;
        final String idFoto = idPago + "_ID_" + numero;
        final String nombreFoto = idPago + numero + ".jpg";
        final String idenFoto = codigoVendedor + Utilidades.fechaActual("HHmmss") + 1;

        if (tomarFoto.isClickable()) {
            tomarFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Fotos fotosestado = new Fotos();
                    fotosestado.estadoFoto = true;
                    fotosestado.idenFoto = idenFoto;

                    Gson gson = new Gson();
                    String jsonStringObject = gson.toJson(fotosestado);
                    PreferencesFotos.guardarFotoSeleccionada(contexto, jsonStringObject);

                    SharedPreferences sharedPreferences = contexto.getSharedPreferences("session", Context.MODE_PRIVATE);
                    boolean estado = true;
                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putBoolean("estado_MultiplesFotos", estado);
                    editor1.commit();
                    Intent login = new Intent(contexto.getApplicationContext(), MultiplesFotosActivity.class);
                    contexto.startActivity(login);
                    MetodoDePagoTarjeta.dialogo.onSaveInstanceState();


                }
            });
        }


        String doc = "";
        String docFac = "";
        Facturas fac3 = new Facturas();
        Facturas fac33 = new Facturas();

        final List<Facturas> listaDocuFinanFacturasHechas = new ArrayList<>();
        final List<Cartera> listaDocuFinanCarteraHechas = new ArrayList<>();
        final List<String> listaConsultaFac = new ArrayList<>();

        if (facCollection != null) {

            for (Cartera cartera1 : facCollection) {
                docFac = cartera1.getDocumentoFinanciero();
                listaDocuFinanCarteraHechas.add(cartera1);

            }

            for (Facturas fac : listsaFacturasParcialTotal) {
                fac3.documentoFinanciero = fac.documentoFinanciero;
                fac3=fac;

                doc = fac3.documento;
                listaDocuFinanFacturasHechas.add(fac);
                listaConsultaFac.add(doc);


                for (int i = 0; i < listaDocuFinanCarteraHechas.size(); i++) {
                    if (listaDocuFinanCarteraHechas.get(i).documentoFinanciero.equals(fac3.documentoFinanciero)){
                        listaDocuFinanCarteraHechas.remove(i);
                    }
                }

            }

            if (listaDocuFinanCarteraHechas.size()>0) {

                for (int i = 0; i < listaDocuFinanFacturasHechas.size(); i++) {


                    if (!listaDocuFinanCarteraHechas.get(i).documentoFinanciero.equals(listaDocuFinanFacturasHechas.get(i).documentoFinanciero)) {
                        fac33.valor = listaDocuFinanCarteraHechas.get(i).saldo;
                        fac33.documentoFinanciero = listaDocuFinanCarteraHechas.get(i).documentoFinanciero;


                        if (listsaFacturasParcialTotal.size() > 0) {


                            listsaFacturasParcialTotal.add(fac33);

                        }

                    }

                }
            }


        }

        Collections.sort(listsaFacturasParcialTotal, new Comparator<Facturas>() {
            @Override
            public int compare(Facturas obj1, Facturas obj2) {
                if (obj1.getDocumentoFinanciero().equals(obj2.getDocumentoFinanciero())){


                }

                return obj1.getDocumentoFinanciero().compareTo(obj2.getDocumentoFinanciero());
            }
        });

        if (listsaFacturasParcialTotal.size() > 0) {
            if (listaPrecios.size() > 0) {

                int cantidadFacturasPraciales;

                do {

                    cantidadFacturasPraciales = listsaFacturasParcialTotal.size();

                    for (int i = 0; i < listaPrecios.size(); i++) {


                        if (listaPrecios.get(i).equals(valoresfacturas.get(i))) {

                            if (listsaFacturasParcialTotal.size() > 0) {
                                listsaFacturasParcialTotal.remove(i);
                                listaPrecios.remove(i);
                                valoresfacturas.remove(i);
                                precios.remove(i);
                            }

                            documentosFinanciero.remove(i);
                            claseDocumento.remove(i);
                            fechasDocumentos.remove(i);
                        } else if (!listaPrecios.get(i).equals(valoresfacturas.get(i))) {

                            String valorFactu = "0";
                            valoresfacturas.add(valorFactu);


                        }

                    }
                }while (cantidadFacturasPraciales != listsaFacturasParcialTotal.size());
            }
        }

//        if (listsaFacturasParcialTotal.size() > 0) {
//            if (listaPrecios.size() > 0) {
//                for (int i = 0; i < listaPrecios.size(); i++) {
//
//
//                    if (listaPrecios.get(i).equals(valoresfacturas.get(i))) {
//
//                        if (listsaFacturasParcialTotal.size() > 0) {
//                            listsaFacturasParcialTotal.remove(i);
//                            listaPrecios.remove(i);
//                            valoresfacturas.remove(i);
//                            precios.remove(i);
//                        }
//
//                        documentosFinanciero.remove(i);
//                        claseDocumento.remove(i);
//                        fechasDocumentos.remove(i);
//                    } else if (!listaPrecios.get(i).equals(valoresfacturas.get(i))) {
//
//                        String valorFactu = "0";
//                        valoresfacturas.add(valorFactu);
//
//
//                    }
//
//                }
//            }
//        }
//
//        if (listsaFacturasParcialTotal.size() > 0) {
//            if (listaPrecios.size() > 0) {
//                for (int i = 0; i < listaPrecios.size(); i++) {
//
//
//                    if (listaPrecios.get(i).equals(valoresfacturas.get(i))) {
//
//                        if (listsaFacturasParcialTotal.size() > 0) {
//                            listsaFacturasParcialTotal.remove(i);
//                            listaPrecios.remove(i);
//                            valoresfacturas.remove(i);
//                            precios.remove(i);
//                        }
//
//                        documentosFinanciero.remove(i);
//                        claseDocumento.remove(i);
//                        fechasDocumentos.remove(i);
//                    } else if (!listaPrecios.get(i).equals(valoresfacturas.get(i))) {
//
//                        String valorFactu = "0";
//                        valoresfacturas.add(valorFactu);
//
//
//                    }
//
//                }
//            }
//        }
//
//        if (listsaFacturasParcialTotal.size() > 0) {
//            if (listaPrecios.size() > 0) {
//                for (int i = 0; i < listaPrecios.size(); i++) {
//
//
//                    if (listaPrecios.get(i).equals(valoresfacturas.get(i))) {
//
//                        if (listsaFacturasParcialTotal.size() > 0) {
//                            listsaFacturasParcialTotal.remove(i);
//                            listaPrecios.remove(i);
//                            valoresfacturas.remove(i);
//                            precios.remove(i);
//                        }
//
//                        documentosFinanciero.remove(i);
//                        claseDocumento.remove(i);
//                        fechasDocumentos.remove(i);
//                    } else if (!listaPrecios.get(i).equals(valoresfacturas.get(i))) {
//
//                        String valorFactu = "0";
//                        valoresfacturas.add(valorFactu);
//
//
//                    }
//
//                }
//            }
//        }


        Utilidades.formatearDecimales(salfoAFA, 2);

        /// decimales .., else ... else ,,.  5
        tvValorFragEfec.addTextChangedListener(new TextWatcher() {

            boolean condicion = false;
            String estadoTextoAnterior = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                    String input = s.toString();

                    if (!input.isEmpty()) {


                        input = input.replace(",", "").replace(".", "");
                        DecimalFormat formatoNumero = new DecimalFormat("###,###,###,##.#");

                        String newPrice = formatoNumero.format(Double.parseDouble(input));
                        newPrice = newPrice.replace(",", ".");
                        if (newPrice.length() > 3) {
                            newPrice = newPrice.substring(0, newPrice.length() - 3) + ',' + newPrice.substring(newPrice.length() - 2);
                        }

                        tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
                        tvValorFragEfec.setText(newPrice);
                        tvValorFragEfec.setSelection(newPrice.length()); //Move Cursor to end of String
                        tvValorFragEfec.addTextChangedListener(this);


                    }

                } else if (finalEmpresa.equals("AGCO")) {

                    String input = s.toString();

                    if (!input.isEmpty()) {


                        if (input.length() < 3) {
                            String newPrice2 = input;
                            tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
                            tvValorFragEfec.setText(newPrice2);
                            tvValorFragEfec.setSelection(newPrice2.length()); //Move Cursor to end of String
                            tvValorFragEfec.addTextChangedListener(this);

                        } else {

                            input = input.replace(".", "").replace(",", "");
                            DecimalFormat formatoNumero = new DecimalFormat("###,###,###,###");

                            String newPrice = formatoNumero.format(Double.parseDouble(input));

                            newPrice = newPrice.replace(",", ".");

                            tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
                            tvValorFragEfec.setText(newPrice);
                            tvValorFragEfec.setSelection(newPrice.length()); //Move Cursor to end of String
                            tvValorFragEfec.addTextChangedListener(this);

                        }
                    }

                } else {

                    String input = s.toString();

                    if (!input.isEmpty()) {


                        input = input.replace(",", "").replace(".", "");
                        DecimalFormat formatoNumero = new DecimalFormat("###,###,###,##.#");

                        String newPrice = formatoNumero.format(Double.parseDouble(input));
                        newPrice = newPrice.replace(".", ",");
                        if (newPrice.length() > 3) {
                            newPrice = newPrice.substring(0, newPrice.length() - 3) + '.' + newPrice.substring(newPrice.length() - 2);
                        }

                        tvValorFragEfec.removeTextChangedListener(this); //To Prevent from Infinite Loop
                        tvValorFragEfec.setText(newPrice);
                        tvValorFragEfec.setSelection(newPrice.length()); //Move Cursor to end of String
                        tvValorFragEfec.addTextChangedListener(this);


                    }

                }


            }


        });

        double finalComparar = Utilidades.formatearDecimales(comparar, 2);

        String finalCod_Vendedor = cod_Vendedor;
        String finalTipoUsuario = tipoUsuario;

        final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
        String consecId1 = "", numeroAnulacionId1 = "";
        String negocioId1 = "";
        String vendedorId1 = "";
        String consecutivoid = "";

        consecId1 = DataBaseBO.cargarConsecutivoId();
        negocioId1 = DataBaseBO.cargarNegocioConsecutivoId();
        vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId();

        int consec1Id = Integer.parseInt(consecId1);
        int vendedorsumId = Integer.parseInt(vendedorId1);
        int contadorId = 1;
        consec1Id = consec1Id + contadorId;
        numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

        DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon);
        consecutivoid = String.valueOf(negocioId1+ vendedorsumId+ consec1Id);


        String finalConsecutivoid = consecutivoid;

        guardarFormaPagoFE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                double totalesValoresParciales = 0;
                double nuevoValor = 0;
                double nuevoValor2 = 0;
                double nuevoValor3 = 0;
                double nuevoValor4 = 0;
                double nuevoValor5 = 0;
                double nuevoValor6 = 0;
                double nuevoValor7 = 0;
                double nuevoValor8 = 0;
                double nuevoValor9 = 0;
                double nuevoValor10 = 0;
                double nuevoValor11 = 0;
                double totalbotenido = 0;
                double resultadosValores = 0;

                String cuentasBanco = "";
                cuentasBanco = spinnerCuentasBanco.getSelectedItem().toString();
                String nombreTitular = tvNombreTitular.getText().toString();
                String nrotarjetaEfec = tvNROTarjeta.getText().toString();
                String descripc = "";
                String fotoID = "";
                String fecha = tvFechaFragTarje.getText().toString();
                double valor = 0;
                String input = tvValorFragEfec.getText().toString();

                if (facCollection != null) {
                    descripc = formaPago.getObservaciones();
                }

                if (facCollection == null) {

                    if (anticipo != null) {
                        if (anticipo.estado == true) {
                            descripc = anticipo.getObservaciones();

                        } else if (anticipo.estado == false) {
                            descripc = anticipo.getObservaciones();
                        }
                    }
                }

                if (fecha.equals("")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "The date field cannot be blank..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo de la fecha no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                        }
                    }
                }

                Gson gson1 = new Gson();
                String stringJsonObject1 = PreferencesFotos.obteneFotoSeleccionada(contexto);
                Fotos fotos = gson1.fromJson(stringJsonObject1, Fotos.class);


                double valorConvertido = 0;

                //Comprobación de entrada de textwacher para todas las formas de pago

                if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                    if (!input.isEmpty()) {
                        if (input.contains(".") && input.contains(",")) {

                            input = input.replace(".", "");
                            input = input.replace(",", ".");
                            valorConvertido = Double.parseDouble(input);
                            valor = Double.parseDouble(input);

                        } else if (input.contains(",")) {

                            input = input.replace(",", ".");
                            valorConvertido = Double.parseDouble(input);
                            valor = Double.parseDouble(input);

                        } else if (input.contains(".")) {

                            input = input.replace(".", "");
                            valorConvertido = Double.parseDouble(input);
                            valor = Double.parseDouble(input);

                        } else if (!input.contains(".") && !input.contains(",")) {
                            valorConvertido = Double.parseDouble(input);
                            valor = Double.parseDouble(input);
                        }
                    }

                } else if (finalEmpresa.equals("AGCO")) {

                    if (!input.isEmpty()) {

                        if (input.contains(".")) {

                            input = input.replace(".", "");
                            valorConvertido = Double.parseDouble(input);
                            valor = Double.parseDouble(input);

                        } else if (!input.contains(".") && !input.contains(",")) {
                            valorConvertido = Double.parseDouble(input);
                            valor = Double.parseDouble(input);
                        }
                    }

                } else {

                    if (!input.isEmpty()) {

                        if (input.contains(",")) {

                            input = input.replace(",", "");
                            valorConvertido = Double.parseDouble(input);
                            valor = Double.parseDouble(input);

                        } else if (input.contains(".")) {


                            valorConvertido = Double.parseDouble(input);
                            valor = Double.parseDouble(input);

                        } else if (!input.contains(".") && !input.contains(",")) {
                            valorConvertido = Double.parseDouble(input);
                            valor = Double.parseDouble(input);
                        }
                    }
                }

                if (fotos == null) {
                    if (tomarFoto.isClickable()) {

                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Toasty.warning(contexto, "Attaching images or taking photo collection is mandatory..", Toasty.LENGTH_LONG).show();


                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Toasty.warning(contexto, "El adjuntar imagenes o tomar foto del recaudo es obligatoria..", Toasty.LENGTH_LONG).show();

                            }
                        }
                    }
                }


                if (fotos != null) {
                    double valorFLEX = 0;
                    if (formaPago != null) {
                        valorFLEX = formaPago.getValor();
                        valorFLEX = Utilidades.formatearDecimales(valorFLEX, 2);
                    }


                    if (anticipo != null) {
                        valorFLEX = anticipo.getValor();
                        valorFLEX = Utilidades.formatearDecimales(valorFLEX, 2);
                    }
                    if (valorConvertido <= Utilidades.formatearDecimales(valorFLEX-sumaXValorConsignado,2) ) {

                        if (facCollection != null) {


                            //Actualizada total 28
                            if (listsaFacturasParcialTotal.size() == 0) {
                                try {
                                    if (formaPago.parcial == true) {

                                        //SUMAR SALDOS A FAVOR DEL CLIENTE
                                        Double valoreNegativos = 0.0;

                                        for (String p:precios) {
                                            if(Double.parseDouble(p) < 0)
                                                valoreNegativos += (Double.parseDouble(p) * -1);
                                        }

                                        for (int i = 0; i < precios.size(); i++) {

                                            PreciosFacturasParcial preciosFacturasParcial = new PreciosFacturasParcial();
                                            double valorobtenido = Double.parseDouble(precios.get(i));
                                            preciosFacturasParcial.valorobtenido = valorobtenido;
                                            preciosFacturasParcial.valor = valor;
                                            double valorObtenidoFac = 0;
                                            String totalesValoresLista = "";

                                            if(i == 0)
                                            {
                                                if (valorobtenido < 0) {

                                                    nuevoValor = valor;
                                                    preciosParcial.add(String.valueOf(valor));
                                                    preciosfacturasLogica.add(String.valueOf(0));

                                                } else if (valorobtenido > 0) {

                                                    if (valor < 0) {

                                                        nuevoValor = valor;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (valor == 0) {

                                                        nuevoValor = valor;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (valor < valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valor));

                                                    } else if (valor > valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenido));

                                                    } else if (valor == valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenido));

                                                    }

                                                }
                                            }
                                            else
                                            {
                                                if (valorobtenido < 0) {

                                                    preciosParcial.add(String.valueOf(valor));
                                                    preciosfacturasLogica.add(String.valueOf(0));

                                                } else if (valorobtenido > 0) {

                                                    if (nuevoValor < 0) {

                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (nuevoValor == 0) {

                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (nuevoValor < valorobtenido) {

                                                        if((nuevoValor + valoreNegativos) > valorobtenido)
                                                        {
                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
                                                            nuevoValor = totalbotenido;
                                                            preciosParcial.add(String.valueOf(valor));
                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
                                                        }
                                                        else
                                                        {
                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
                                                            preciosParcial.add(String.valueOf(valor));
                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor));
                                                            nuevoValor = totalbotenido;
                                                        }

                                                    } else if (nuevoValor > valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenido));


                                                    } else if (nuevoValor == valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenido));

                                                    }

                                                }
                                            }

//                                            switch (i) {
//
//                                                case 0:
//
//                                                    valorobtenido = Double.parseDouble(precios.get(i));
//
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor = valor;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (valor < 0) {
//
//                                                            nuevoValor = valor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (valor == 0) {
//
//                                                            nuevoValor = valor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (valor < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valor));
//
//                                                        } else if (valor > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (valor == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        }
//
//                                                    }
//
//                                                    break;
//
//                                                case 1:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor2 = nuevoValor;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor < 0) {
//
//                                                            nuevoValor2 = nuevoValor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor == 0) {
//
//                                                            nuevoValor2 = nuevoValor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor < valorobtenido) {
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor));
//
//
//                                                        } else if (nuevoValor > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                        } else if (nuevoValor == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 2:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor3 = nuevoValor2;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor2 < 0) {
//
//                                                            nuevoValor3 = nuevoValor2;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor2 == 0) {
//
//                                                            nuevoValor3 = nuevoValor2;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor2 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                        } else if (nuevoValor2 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor2 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 3:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor4 = nuevoValor3;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor3 < 0) {
//
//                                                            nuevoValor4 = nuevoValor3;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor3 == 0) {
//
//                                                            nuevoValor4 = nuevoValor3;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor3 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                        } else if (nuevoValor3 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor3 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 4:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor5 = nuevoValor4;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor4 < 0) {
//
//                                                            nuevoValor5 = nuevoValor4;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor4 == 0) {
//
//                                                            nuevoValor5 = nuevoValor4;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor4 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                        } else if (nuevoValor4 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor4 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 5:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor6 = nuevoValor5;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor5 < 0) {
//
//                                                            nuevoValor6 = nuevoValor5;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor5 == 0) {
//
//                                                            nuevoValor6 = nuevoValor5;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor5 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                        } else if (nuevoValor5 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor5 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 6:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor7 = nuevoValor6;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor6 < 0) {
//
//                                                            nuevoValor7 = nuevoValor6;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor6 == 0) {
//
//                                                            nuevoValor7 = nuevoValor6;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor6 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                        } else if (nuevoValor6 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor6 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 7:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor8 = nuevoValor7;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor7 < 0) {
//
//                                                            nuevoValor8 = nuevoValor7;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor7 == 0) {
//
//                                                            nuevoValor8 = nuevoValor7;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor7 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                        } else if (nuevoValor7 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor7 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 8:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor9 = nuevoValor8;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor8 < 0) {
//
//                                                            nuevoValor9 = nuevoValor8;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor8 == 0) {
//
//                                                            nuevoValor9 = nuevoValor8;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor8 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//
//                                                        } else if (nuevoValor8 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                        } else if (nuevoValor8 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//                                                        }
//
//
//                                                    }
//
//
//                                                    break;
//                                                case 9:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor10 = nuevoValor9;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor9 < 0) {
//
//                                                            nuevoValor10 = nuevoValor9;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor9 == 0) {
//
//                                                            nuevoValor10 = nuevoValor9;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor9 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//
//                                                        } else if (nuevoValor9 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                        } else if (nuevoValor9 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 10:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor11 = nuevoValor10;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor10 < 0) {
//
//                                                            nuevoValor11 = nuevoValor10;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor10 == 0) {
//
//                                                            nuevoValor11 = nuevoValor10;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor10 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                        } else if (nuevoValor10 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor10 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                        }
//
//                                                    }
//
//                                                    break;
//                                                default:
//                                                    break;
//                                            }

                                            PreciosFacturasParcial parcial = new PreciosFacturasParcial();
                                            parcial.valor = Double.parseDouble(preciosParcial.get(i));
                                            Gson gson33 = new Gson();
                                            String jsonStringObject = gson33.toJson(parcial);
                                            PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                        }

                                    } else if (formaPago.parcial == false) {

                                        for (int i = 0; i < precios.size(); i++) {


                                            PreciosFacturasParcial preciosFacturasParcial = new PreciosFacturasParcial();
                                            double valorobtenido = Double.parseDouble(precios.get(i));
                                            preciosFacturasParcial.valorobtenido = valorobtenido;
                                            preciosFacturasParcial.valor = valor;
                                            double valorObtenidoFac = 0;
                                            String totalesValoresLista = "";

                                            if(i == 0) {
                                                if (valorobtenido < 0) {

                                                    nuevoValor = valor;
                                                    preciosParcial.add(String.valueOf(valor));
                                                    preciosfacturasLogica.add(String.valueOf(0));

                                                } else if (valorobtenido > 0) {

                                                    if (valor < 0) {

                                                        nuevoValor = valor;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (valor == 0) {

                                                        nuevoValor = valor;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (valor < valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valor));

                                                    } else if (valor > valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenido));

                                                    } else if (valor == valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenido));

                                                    }

                                                }
                                            }
                                            else
                                            {
                                                if (valorobtenido < 0) {

                                                    preciosParcial.add(String.valueOf(valor));
                                                    preciosfacturasLogica.add(String.valueOf(0));

                                                } else if (valorobtenido > 0) {

                                                    if (nuevoValor < 0) {

                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (nuevoValor == 0) {

                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (nuevoValor < valorobtenido) {
                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(nuevoValor));
                                                        nuevoValor = totalbotenido;


                                                    } else if (nuevoValor > valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenido));


                                                    } else if (nuevoValor == valorobtenido) {

                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenido));

                                                    }

                                                }
                                            }

//                                            switch (i) {
//
//                                                case 0:
//
//                                                    valorobtenido = Double.parseDouble(precios.get(i));
//
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor = valor;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (valor < 0) {
//
//                                                            nuevoValor = valor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (valor == 0) {
//
//                                                            nuevoValor = valor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (valor < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valor));
//
//                                                        } else if (valor > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (valor == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        }
//
//                                                    }
//
//                                                    break;
//
//                                                case 1:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor2 = nuevoValor;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor < 0) {
//
//                                                            nuevoValor2 = nuevoValor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor == 0) {
//
//                                                            nuevoValor2 = nuevoValor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor < valorobtenido) {
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor));
//
//
//                                                        } else if (nuevoValor > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                        } else if (nuevoValor == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 2:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor3 = nuevoValor2;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor2 < 0) {
//
//                                                            nuevoValor3 = nuevoValor2;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor2 == 0) {
//
//                                                            nuevoValor3 = nuevoValor2;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor2 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                        } else if (nuevoValor2 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor2 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 3:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor4 = nuevoValor3;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor3 < 0) {
//
//                                                            nuevoValor4 = nuevoValor3;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor3 == 0) {
//
//                                                            nuevoValor4 = nuevoValor3;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor3 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                        } else if (nuevoValor3 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor3 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 4:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor5 = nuevoValor4;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor4 < 0) {
//
//                                                            nuevoValor5 = nuevoValor4;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor4 == 0) {
//
//                                                            nuevoValor5 = nuevoValor4;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor4 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                        } else if (nuevoValor4 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor4 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 5:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor6 = nuevoValor5;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor5 < 0) {
//
//                                                            nuevoValor6 = nuevoValor5;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor5 == 0) {
//
//                                                            nuevoValor6 = nuevoValor5;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor5 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                        } else if (nuevoValor5 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor5 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 6:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor7 = nuevoValor6;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor6 < 0) {
//
//                                                            nuevoValor7 = nuevoValor6;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor6 == 0) {
//
//                                                            nuevoValor7 = nuevoValor6;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor6 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                        } else if (nuevoValor6 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor6 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 7:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor8 = nuevoValor7;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor7 < 0) {
//
//                                                            nuevoValor8 = nuevoValor7;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor7 == 0) {
//
//                                                            nuevoValor8 = nuevoValor7;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor7 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                        } else if (nuevoValor7 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor7 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 8:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor9 = nuevoValor8;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor8 < 0) {
//
//                                                            nuevoValor9 = nuevoValor8;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor8 == 0) {
//
//                                                            nuevoValor9 = nuevoValor8;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor8 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//
//                                                        } else if (nuevoValor8 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                        } else if (nuevoValor8 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//                                                        }
//
//
//                                                    }
//
//
//                                                    break;
//                                                case 9:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor10 = nuevoValor9;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor9 < 0) {
//
//                                                            nuevoValor10 = nuevoValor9;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor9 == 0) {
//
//                                                            nuevoValor10 = nuevoValor9;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor9 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//
//                                                        } else if (nuevoValor9 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                        } else if (nuevoValor9 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 10:
//
//                                                    if (valorobtenido < 0) {
//
//                                                        nuevoValor11 = nuevoValor10;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenido > 0) {
//
//                                                        if (nuevoValor10 < 0) {
//
//                                                            nuevoValor11 = nuevoValor10;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor10 == 0) {
//
//                                                            nuevoValor11 = nuevoValor10;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor10 < valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                        } else if (nuevoValor10 > valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                        } else if (nuevoValor10 == valorobtenido) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                        }
//
//                                                    }
//
//                                                    break;
//                                                default:
//                                                    break;
//                                            }

                                            Gson gson33 = new Gson();
                                            String jsonStringObject = gson33.toJson(preciosParcial.get(i));
                                            PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                        }

                                    }

                                } catch (Exception exception) {
                                    System.out.println("Error en la forma de pago parcial " + exception);
                                }

                            }

                            if (listsaFacturasParcialTotal.size() > 0) {
                                try {
                                    if (formaPago.parcial == true) {

                                        for (int i = 0; i < precios.size(); i++) {


                                            PreciosFacturasParcial preciosFacturasParcial = new PreciosFacturasParcial();
                                            double valorobtenido = Double.parseDouble(precios.get(i));
                                            double valorobtenidoSegundo = 0;
                                            preciosFacturasParcial.valorobtenido = valorobtenido;
                                            preciosFacturasParcial.valor = valor;
                                            String acert = "";
                                            double valorTotalParcial = 0;
                                            double valorCom = 0;
                                            double valorObtenidoFac = 0;
                                            String totalesValoresLista = "";


                                            if(i == 0)
                                            {
                                                if (listsaFacturasParcialTotal != null) {

                                                    for (Facturas fac2 : listsaFacturasParcialTotal) {

                                                        valorObtenidoFac = fac2.getValor();
                                                        preciosAcomparar.add(String.valueOf(valorObtenidoFac));

                                                    }


                                                    for (int j = 0; j < preciosAcomparar.size(); j++) {
                                                        double valorLista = 0;

                                                        valorLista = Double.parseDouble(preciosAcomparar.get(j));

                                                        if (!DataBaseBO.ExisteDocumento(documentosFinanciero)) {
                                                            double valorLista2 = Double.parseDouble(precios.get(j));
                                                            totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2 - 0, 2));
                                                            totalFacturas.add(totalesValoresLista);
                                                        } else {
                                                            double valorLista2 = Double.parseDouble(precios.get(j));
                                                            if (valorLista2 == valorLista) {
                                                                totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2, 2));
                                                                totalFacturas.add(totalesValoresLista);
                                                            }else{
                                                                totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2 - valorLista, 2));
                                                                totalFacturas.add(totalesValoresLista);
                                                            }
                                                        }


                                                    }

                                                }

                                                valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));



                                                if (valorobtenidoSegundo < 0) {

                                                    nuevoValor = valor;
                                                    preciosParcial.add(String.valueOf(valor));
                                                    preciosfacturasLogica.add(String.valueOf(0));

                                                } else if (valorobtenidoSegundo > 0) {

                                                    if (valor < 0) {

                                                        nuevoValor = valor;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (valor == 0) {

                                                        nuevoValor = valor;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (valor < valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valor));

                                                    } else if (valor > valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));

                                                    } else if (valor == valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));

                                                    }

                                                }
                                            }
                                            else
                                            {
                                                valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));


                                                if (valorobtenidoSegundo < 0) {

                                                    preciosParcial.add(String.valueOf(valor));
                                                    preciosfacturasLogica.add(String.valueOf(0));

                                                } else if (valorobtenidoSegundo > 0) {

                                                    if (nuevoValor < 0) {

                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (nuevoValor == 0) {

                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (nuevoValor < valorobtenidoSegundo) {
                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(nuevoValor));
                                                        nuevoValor = totalbotenido;

                                                    } else if (nuevoValor > valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));


                                                    } else if (nuevoValor == valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));

                                                    }

                                                }
                                            }

//                                            switch (i) {
//
//                                                case 0:
//
//
//                                                    if (listsaFacturasParcialTotal != null) {
//
//                                                        for (Facturas fac2 : listsaFacturasParcialTotal) {
//
//                                                            valorObtenidoFac = fac2.getValor();
//                                                            preciosAcomparar.add(String.valueOf(valorObtenidoFac));
//
//                                                        }
//
//
//                                                        for (int j = 0; j < preciosAcomparar.size(); j++) {
//                                                            double valorLista = 0;
//
//                                                            valorLista = Double.parseDouble(preciosAcomparar.get(j));
//
//                                                            if (!DataBaseBO.ExisteDocumento(documentosFinanciero)) {
//                                                                double valorLista2 = Double.parseDouble(precios.get(j));
//                                                                totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2 - 0, 2));
//                                                                totalFacturas.add(totalesValoresLista);
//                                                            } else {
//                                                                double valorLista2 = Double.parseDouble(precios.get(j));
//                                                                if (valorLista2 == valorLista) {
//                                                                    totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2, 2));
//                                                                    totalFacturas.add(totalesValoresLista);
//                                                                }else{
//                                                                    totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2 - valorLista, 2));
//                                                                    totalFacturas.add(totalesValoresLista);
//                                                                }
//                                                            }
//
//
//                                                        }
//
//                                                    }
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//
//
//                                                    if (valorobtenidoSegundo < 0) {
//
//                                                        nuevoValor = valor;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenidoSegundo > 0) {
//
//                                                        if (valor < 0) {
//
//                                                            nuevoValor = valor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (valor == 0) {
//
//                                                            nuevoValor = valor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (valor < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valor));
//
//                                                        } else if (valor > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (valor == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        }
//
//                                                    }
//
//                                                    break;
//
//                                                case 1:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//
//                                                    if (valorobtenidoSegundo < 0) {
//
//                                                        nuevoValor2 = nuevoValor;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenidoSegundo > 0) {
//
//                                                        if (nuevoValor < 0) {
//
//                                                            nuevoValor2 = nuevoValor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor == 0) {
//
//                                                            nuevoValor2 = nuevoValor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor < valorobtenidoSegundo) {
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor));
//
//
//                                                        } else if (nuevoValor > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                        } else if (nuevoValor == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        }
//
//                                                    }
//
//                                                    break;
//                                                case 2:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor3 = nuevoValor2;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor2 < 0) {
//
//                                                            nuevoValor3 = nuevoValor2;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor2 == 0) {
//
//                                                            nuevoValor3 = nuevoValor2;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor2 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                        } else if (nuevoValor2 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor2 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                case 3:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor4 = nuevoValor3;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor3 < 0) {
//
//                                                            nuevoValor4 = nuevoValor3;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor3 == 0) {
//
//                                                            nuevoValor4 = nuevoValor3;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor3 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                        } else if (nuevoValor3 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor3 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                        }
//                                                    }
//
//                                                    break;
//                                                case 4:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor5 = nuevoValor4;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor4 < 0) {
//
//                                                            nuevoValor5 = nuevoValor4;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor4 == 0) {
//
//                                                            nuevoValor5 = nuevoValor4;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor4 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                        } else if (nuevoValor4 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor4 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                        }
//                                                    }
//
//                                                    break;
//                                                case 5:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor6 = nuevoValor5;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor5 < 0) {
//
//                                                            nuevoValor6 = nuevoValor5;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor5 == 0) {
//
//                                                            nuevoValor6 = nuevoValor5;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor5 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                        } else if (nuevoValor5 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor5 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                case 6:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor7 = nuevoValor6;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor6 < 0) {
//
//                                                            nuevoValor7 = nuevoValor6;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor6 == 0) {
//
//                                                            nuevoValor7 = nuevoValor6;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor6 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                        } else if (nuevoValor6 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor6 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                        }
//                                                    }
//
//                                                    break;
//                                                case 7:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor8 = nuevoValor7;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor7 < 0) {
//
//                                                            nuevoValor8 = nuevoValor7;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor7 == 0) {
//
//                                                            nuevoValor8 = nuevoValor7;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor7 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                        } else if (nuevoValor7 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor7 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                case 8:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor9 = nuevoValor8;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor8 < 0) {
//
//                                                            nuevoValor9 = nuevoValor8;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor8 == 0) {
//
//                                                            nuevoValor9 = nuevoValor8;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor8 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//
//                                                        } else if (nuevoValor8 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                        } else if (nuevoValor8 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 9:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor10 = nuevoValor9;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor9 < 0) {
//
//                                                            nuevoValor10 = nuevoValor9;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor9 == 0) {
//
//                                                            nuevoValor10 = nuevoValor9;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor9 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//
//                                                        } else if (nuevoValor9 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                        } else if (nuevoValor9 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                case 10:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor11 = nuevoValor10;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor10 < 0) {
//
//                                                            nuevoValor11 = nuevoValor10;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor10 == 0) {
//
//                                                            nuevoValor11 = nuevoValor10;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor10 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                        } else if (nuevoValor10 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor10 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                default:
//                                                    break;
//                                            }


                                            PreciosFacturasParcial parcial = new PreciosFacturasParcial();
                                            parcial.valor = Double.parseDouble(preciosParcial.get(i));
                                            Gson gson33 = new Gson();
                                            String jsonStringObject = gson33.toJson(parcial);
                                            PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                        }

                                    } else if (formaPago.parcial == false) {

                                        for (int i = 0; i < precios.size(); i++) {


                                            PreciosFacturasParcial preciosFacturasParcial = new PreciosFacturasParcial();
                                            double valorobtenido = 0;
                                            double valorobtenidoSegundo = 0;
                                            preciosFacturasParcial.valorobtenido = valorobtenido;
                                            preciosFacturasParcial.valor = valor;
                                            String acert = "";
                                            double valorTotalParcial = 0;
                                            double valorCom = 0;
                                            double valorObtenidoFac = 0;
                                            String totalesValoresLista = "";

                                            if(i == 0)
                                            {
                                                if (listsaFacturasParcialTotal != null) {

                                                    for (Facturas fac2 : listsaFacturasParcialTotal) {

                                                        valorObtenidoFac = fac2.getValor();
                                                        preciosAcomparar.add(String.valueOf(valorObtenidoFac));

                                                    }


                                                    for (int j = 0; j < preciosAcomparar.size(); j++) {
                                                        double valorLista = 0;

                                                        valorLista = Double.parseDouble(preciosAcomparar.get(j));


                                                        double valorLista2 = Double.parseDouble(precios.get(j));
                                                        totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2 - valorLista, 2));
                                                        totalFacturas.add(totalesValoresLista);


                                                    }

                                                }

                                                valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));



                                                if (valorobtenidoSegundo < 0) {

                                                    nuevoValor = valor;
                                                    preciosParcial.add(String.valueOf(valor));
                                                    preciosfacturasLogica.add(String.valueOf(0));

                                                } else if (valorobtenidoSegundo > 0) {

                                                    if (valor < 0) {

                                                        nuevoValor = valor;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (valor == 0) {

                                                        nuevoValor = valor;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (valor < valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valor));

                                                    } else if (valor > valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));

                                                    } else if (valor == valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));

                                                    }

                                                }
                                            }
                                            else
                                            {
                                                valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));


                                                if (valorobtenidoSegundo < 0) {

                                                    nuevoValor2 = nuevoValor;                                                    preciosParcial.add(String.valueOf(valor));
                                                    preciosfacturasLogica.add(String.valueOf(0));

                                                } else if (valorobtenidoSegundo > 0) {

                                                    if (nuevoValor < 0) {

                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (nuevoValor == 0) {

                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(0));

                                                    } else if (nuevoValor < valorobtenidoSegundo) {
                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(nuevoValor));
                                                        nuevoValor = totalbotenido;


                                                    } else if (nuevoValor > valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));


                                                    } else if (nuevoValor == valorobtenidoSegundo) {

                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                        nuevoValor = totalbotenido;
                                                        preciosParcial.add(String.valueOf(valor));
                                                        preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));

                                                    }

                                                }
                                            }


//                                            switch (i) {
//
//                                                case 0:
//
//
//                                                    if (listsaFacturasParcialTotal != null) {
//
//                                                        for (Facturas fac2 : listsaFacturasParcialTotal) {
//
//                                                            valorObtenidoFac = fac2.getValor();
//                                                            preciosAcomparar.add(String.valueOf(valorObtenidoFac));
//
//                                                        }
//
//
//                                                        for (int j = 0; j < preciosAcomparar.size(); j++) {
//                                                            double valorLista = 0;
//
//                                                            valorLista = Double.parseDouble(preciosAcomparar.get(j));
//
//
//                                                            double valorLista2 = Double.parseDouble(precios.get(j));
//                                                            totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2 - valorLista, 2));
//                                                            totalFacturas.add(totalesValoresLista);
//
//
//                                                        }
//
//                                                    }
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//
//
//                                                    if (valorobtenidoSegundo < 0) {
//
//                                                        nuevoValor = valor;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenidoSegundo > 0) {
//
//                                                        if (valor < 0) {
//
//                                                            nuevoValor = valor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (valor == 0) {
//
//                                                            nuevoValor = valor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (valor < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valor));
//
//                                                        } else if (valor > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (valor == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                            nuevoValor = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        }
//
//                                                    }
//
//                                                    break;
//
//                                                case 1:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//
//                                                    if (valorobtenidoSegundo < 0) {
//
//                                                        nuevoValor2 = nuevoValor;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//
//                                                    } else if (valorobtenidoSegundo > 0) {
//
//                                                        if (nuevoValor < 0) {
//
//                                                            nuevoValor2 = nuevoValor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor == 0) {
//
//                                                            nuevoValor2 = nuevoValor;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor < valorobtenidoSegundo) {
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor));
//
//
//                                                        } else if (nuevoValor > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                        } else if (nuevoValor == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                            nuevoValor2 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        }
//
//                                                    }
//
//                                                    break;
//                                                case 2:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor3 = nuevoValor2;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor2 < 0) {
//
//                                                            nuevoValor3 = nuevoValor2;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor2 == 0) {
//
//                                                            nuevoValor3 = nuevoValor2;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor2 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                        } else if (nuevoValor2 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor2 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                            nuevoValor3 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                case 3:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor4 = nuevoValor3;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor3 < 0) {
//
//                                                            nuevoValor4 = nuevoValor3;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor3 == 0) {
//
//                                                            nuevoValor4 = nuevoValor3;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor3 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                        } else if (nuevoValor3 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor3 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                            nuevoValor4 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                        }
//                                                    }
//
//                                                    break;
//                                                case 4:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor5 = nuevoValor4;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor4 < 0) {
//
//                                                            nuevoValor5 = nuevoValor4;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor4 == 0) {
//
//                                                            nuevoValor5 = nuevoValor4;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor4 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                        } else if (nuevoValor4 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor4 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                            nuevoValor5 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                        }
//                                                    }
//
//                                                    break;
//                                                case 5:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor6 = nuevoValor5;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor5 < 0) {
//
//                                                            nuevoValor6 = nuevoValor5;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor5 == 0) {
//
//                                                            nuevoValor6 = nuevoValor5;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor5 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                        } else if (nuevoValor5 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor5 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                            nuevoValor6 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                case 6:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor7 = nuevoValor6;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor6 < 0) {
//
//                                                            nuevoValor7 = nuevoValor6;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor6 == 0) {
//
//                                                            nuevoValor7 = nuevoValor6;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor6 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                        } else if (nuevoValor6 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor6 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                            nuevoValor7 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                        }
//                                                    }
//
//                                                    break;
//                                                case 7:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor8 = nuevoValor7;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor7 < 0) {
//
//                                                            nuevoValor8 = nuevoValor7;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor7 == 0) {
//
//                                                            nuevoValor8 = nuevoValor7;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor7 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                        } else if (nuevoValor7 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor7 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                            nuevoValor8 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                case 8:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor9 = nuevoValor8;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor8 < 0) {
//
//                                                            nuevoValor9 = nuevoValor8;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor8 == 0) {
//
//                                                            nuevoValor9 = nuevoValor8;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor8 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//
//                                                        } else if (nuevoValor8 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                        } else if (nuevoValor8 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                            nuevoValor9 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//                                                        }
//
//                                                    }
//
//
//                                                    break;
//                                                case 9:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor10 = nuevoValor9;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor9 < 0) {
//
//                                                            nuevoValor10 = nuevoValor9;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor9 == 0) {
//
//                                                            nuevoValor10 = nuevoValor9;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor9 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//
//                                                        } else if (nuevoValor9 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                        } else if (nuevoValor9 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                            nuevoValor10 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                case 10:
//
//                                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));
//
//                                                    if (valorobtenidoSegundo < 0) {
//                                                        nuevoValor11 = nuevoValor10;
//                                                        preciosParcial.add(String.valueOf(valor));
//                                                        preciosfacturasLogica.add(String.valueOf(0));
//                                                    } else if (valorobtenidoSegundo > 0) {
//                                                        if (nuevoValor10 < 0) {
//
//                                                            nuevoValor11 = nuevoValor10;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor10 == 0) {
//
//                                                            nuevoValor11 = nuevoValor10;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(0));
//
//                                                        } else if (nuevoValor10 < valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                        } else if (nuevoValor10 > valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                        } else if (nuevoValor10 == valorobtenidoSegundo) {
//
//                                                            totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                            nuevoValor11 = totalbotenido;
//                                                            preciosParcial.add(String.valueOf(valor));
//                                                            preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                        }
//                                                    }
//
//
//                                                    break;
//                                                default:
//                                                    break;
//                                            }

                                            Gson gson33 = new Gson();
                                            String jsonStringObject = gson33.toJson(preciosParcial.get(i));
                                            PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                        }

                                    }

                                } catch (Exception exception) {
                                    System.out.println("Error en la forma de pago parcial " + exception);
                                }
                            }


                        }

/// fac para anticipo 3
                        if (facCollection == null) {



                            if (valor > 0) {
                                if (valor < finalComparar || valor == finalComparar) {
                                    precios.add(input);

                                }
                            } else if (valor > finalComparar || valor <= 0) {

                                if (lenguajeElegido == null) {

                                } else if (lenguajeElegido != null) {
                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                        Toasty.warning(contexto, "The value entered is greater than the amount to be collected or is equal to 0..", Toasty.LENGTH_SHORT).show();

                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                        Toasty.warning(contexto, "El valor ingresado es mayor al monto a recaudar o es igual a 0..", Toasty.LENGTH_SHORT).show();

                                    }
                                }

                            }

                        }

                    }else if (valorConvertido > Utilidades.formatearDecimales(valorFLEX-sumaXValorConsignado,2)) {


                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Toasty.warning(contexto, "The value earned is greater than the amount to be collected..", Toasty.LENGTH_SHORT).show();

                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Toasty.warning(contexto, "El valor ingresado es mayor al monto a recaudar..", Toasty.LENGTH_SHORT).show();

                            }
                        }

                    }


                }

                if (spinnerCuentasBanco.getSelectedItem().toString().equals("Seleccione")
                        || spinnerCuentasBanco.getSelectedItem().toString().equals("Select")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "Destination account cannot be left without selection..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "La cuenta destino no puede quedar sin seleccion..", Toasty.LENGTH_SHORT).show();

                        }
                    }

                }

                if (nombreTitular.equals("")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "The field of the name of the holder cannot be blank..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo del nombre del titular no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                        }
                    }
                }

                if (nrotarjetaEfec.equals("")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "The card number field cannot be blank..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo del numero tarjeta no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                        }
                    }
                }

                if (descripc.equals("")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "The description field cannot be blank...", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo de descripcion no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                        }
                    }

                }


                if (fotos  != null) {
                    if (!nombreTitular.equals("") && !nrotarjetaEfec.equals("")
                            && !descripc.equals("") && valor != 0 && !fecha.equals("")
                            && !spinnerCuentasBanco.getSelectedItem().toString().equals("Seleccione")
                            || !spinnerCuentasBanco.getSelectedItem().toString().equals("Select") && fotos != null) {
                        double valorFLEX = 0;
                        if (formaPago != null) {
                            valorFLEX = formaPago.getValor();
                            valorFLEX = Utilidades.formatearDecimales(valorFLEX, 2);
                        }


                        if (anticipo != null) {
                            valorFLEX = anticipo.getValor();
                            valorFLEX = Utilidades.formatearDecimales(valorFLEX, 2);
                        }
                        if (valorConvertido <= Utilidades.formatearDecimales(valorFLEX-sumaXValorConsignado,2) ) {

                            if (facCollection == null) {

                                if (anticipo.estado == true) {

                                    final String operacion_Cme = "A";

                                    Parcial parcial = new Parcial();
                                    parcial.parcial = true;
                                    Gson gson33 = new Gson();
                                    String jsonStringObject = gson33.toJson(parcial);
                                    PreferencesParcial.guardarParcialSeleccionada(contexto.getApplicationContext(), jsonStringObject);


                                    if (DataBaseBO.guardarFormaPago(idPago, claseDocumento, sociedad,
                                            cod_cliente, finalCod_Vendedor,
                                            "0", fecha_Consignacion, fecha, preciosAnticipo,
                                            moneda, precios,
                                            valor,
                                            spinnerCuentasBanco.getSelectedItem().toString(), moneda_Consig, NCF_Comprobante_fiscal,
                                            documentosFinanciero, consecutivo1,
                                            descripc, via_Pago, usuario, operacion_Cme,
                                            sincronizado, "0", "0", nombreTitular, fotos.idenFoto, finalConsecutivoid, consecutivo2)) {

                                        if (lenguajeElegido == null) {

                                        } else if (lenguajeElegido != null) {
                                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                                Toasty.warning(contexto, "The log was stored correctly.", Toasty.LENGTH_SHORT).show();


                                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                            }
                                        }

                                    } else {
                                        if (lenguajeElegido == null) {

                                        } else if (lenguajeElegido != null) {
                                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                                Toasty.warning(contexto, "Could not store the log.", Toasty.LENGTH_SHORT).show();


                                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();

                                            }
                                        }
                                    }

                                }

                                if (anticipo.estado == false) {

                                    final String operacion_Cme = "X";

                                    Parcial parcial = new Parcial();
                                    parcial.parcial = true;
                                    Gson gson33 = new Gson();
                                    String jsonStringObject = gson33.toJson(parcial);
                                    PreferencesParcial.guardarParcialSeleccionada(contexto.getApplicationContext(), jsonStringObject);

                                    if (DataBaseBO.guardarFormaPago(idPago, claseDocumento, sociedad,
                                            cod_cliente, finalCod_Vendedor,
                                            "0", fecha_Consignacion, fecha, preciosAnticipo,
                                            moneda, precios,
                                            valor,
                                            spinnerCuentasBanco.getSelectedItem().toString(), moneda_Consig, NCF_Comprobante_fiscal,
                                            documentosFinanciero, consecutivo1,
                                            descripc, via_Pago, usuario, operacion_Cme,
                                            sincronizado, "0", "0", nombreTitular, fotos.idenFoto, finalConsecutivoid, consecutivo2)) {

                                        if (lenguajeElegido == null) {

                                        } else if (lenguajeElegido != null) {
                                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                                Toasty.warning(contexto, "The log was stored correctly.", Toasty.LENGTH_SHORT).show();


                                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                            }
                                        }

                                    } else {
                                        if (lenguajeElegido == null) {

                                        } else if (lenguajeElegido != null) {
                                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                                Toasty.warning(contexto, "Could not store the log.", Toasty.LENGTH_SHORT).show();


                                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();

                                            }
                                        }
                                    }

                                }

                                Intent login = new Intent(contexto.getApplicationContext(), MetodosDePagoActivity.class);
                                contexto.startActivity(login);
                                ((MetodosDePagoActivity) contexto).finish();
                                dialogo.dismiss();
                                dialogo.cancel();

                            }

                            if (facCollection != null) {

                                try {
                                    if (formaPago.parcial == true) {

                                        if (finalTipoUsuario.equals("10")) {
                                            if (DataBaseBO.guardarFormaPagParcialTipoCobrador(idPago, claseDocumento,
                                                    sociedad, cod_cliente, vendedoresCartera,
                                                    null, fechasDocumentos,
                                                    fecha, precios,
                                                    moneda, preciosfacturasLogica, preciosParcial, cuentasBanco,
                                                    moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                                    consecutivo1,
                                                    descripc, via_Pago, usuario, operacion_Cme,
                                                    sincronizado, "0", "0",
                                                    nombreTitular, fotos.idenFoto, finalConsecutivoid, formaPago != null ? formaPago.observacionesMotivo : "")) {

                                                if (lenguajeElegido == null) {

                                                } else if (lenguajeElegido != null) {
                                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                                        Toasty.warning(contexto, "The log was stored correctly.", Toasty.LENGTH_SHORT).show();


                                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                        Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                                    }
                                                }

                                            } else {
                                                if (lenguajeElegido == null) {

                                                } else if (lenguajeElegido != null) {
                                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                                        Toasty.warning(contexto, "Could not store the log.", Toasty.LENGTH_SHORT).show();


                                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                        Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();

                                                    }
                                                }
                                            }

                                        } else {

                                            if (formaPago.valor == DiferenciaFormasPago+valor) {


                                                double resultadoAfavor;
                                                double saldoAcomparar = 0;
                                                String favorSaldo = "";
                                                for (String salfoFavor : preciosListaTotal) {
                                                    saldoAcomparar += Double.parseDouble(salfoFavor);


                                                }

                                                if (DiferenciaFormasPago + valor < saldoAcomparar) {
                                                    resultadoAfavor = Utilidades.formatearDecimales(DiferenciaFormasPago + valor - formaPago.valor, 2);

                                                } else {
                                                    resultadoAfavor = Utilidades.formatearDecimales(DiferenciaFormasPago + valor - saldoAcomparar, 2) * -1;

                                                }

                                                if (DataBaseBO.guardarFormaPagParcial(idPago, claseDocumento, sociedad,
                                                        cod_cliente, finalCod_Vendedor,
                                                        "0", fechasDocumentos, fecha, precios,resultadoAfavor,
                                                        moneda, preciosfacturasLogica,
                                                        preciosParcial,
                                                        spinnerCuentasBanco.getSelectedItem().toString(), moneda_Consig, NCF_Comprobante_fiscal,
                                                        documentosFinanciero, consecutivo1,
                                                        descripc, via_Pago, usuario, operacion_Cme,
                                                        sincronizado, "0", "0", nombreTitular, fotos.idenFoto,finalConsecutivoid, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "")) {

                                                    if (lenguajeElegido == null) {

                                                    } else if (lenguajeElegido != null) {
                                                        if (lenguajeElegido.lenguaje.equals("USA")) {

                                                            Toasty.warning(contexto, "The log was stored correctly.", Toasty.LENGTH_SHORT).show();


                                                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                            Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                                        }
                                                    }

                                                } else {
                                                    if (lenguajeElegido == null) {

                                                    } else if (lenguajeElegido != null) {
                                                        if (lenguajeElegido.lenguaje.equals("USA")) {

                                                            Toasty.warning(contexto, "Could not store the log.", Toasty.LENGTH_SHORT).show();


                                                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                            Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                }

                                            }else{

                                                double resultadoAfavor;
                                                double saldoAcomparar = 0;
                                                double AfavorResultado = 0;
                                                String favorSaldo = "";
                                                for (String salfoFavor : preciosListaTotal) {
                                                    saldoAcomparar += Double.parseDouble(salfoFavor);

                                                }

                                                resultadoAfavor = Utilidades.formatearDecimales(DiferenciaFormasPago + valor - saldoAcomparar, 2) * -1;


                                                if (resultadoAfavor < 0) {
                                                    AfavorResultado = resultadoAfavor;
                                                } else {
                                                    AfavorResultado = 0;
                                                }

                                                if (DataBaseBO.guardarFormaPagParcial(idPago, claseDocumento, sociedad,
                                                        cod_cliente, finalCod_Vendedor,
                                                        "0", fechasDocumentos, fecha, precios,0,
                                                        moneda, preciosfacturasLogica,
                                                        preciosParcial,
                                                        spinnerCuentasBanco.getSelectedItem().toString(), moneda_Consig, NCF_Comprobante_fiscal,
                                                        documentosFinanciero, consecutivo1,
                                                        descripc, via_Pago, usuario, operacion_Cme,
                                                        sincronizado, "0", "0", nombreTitular, fotos.idenFoto,finalConsecutivoid, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "")) {

                                                    if (lenguajeElegido == null) {

                                                    } else if (lenguajeElegido != null) {
                                                        if (lenguajeElegido.lenguaje.equals("USA")) {

                                                            Toasty.warning(contexto, "The log was stored correctly.", Toasty.LENGTH_SHORT).show();


                                                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                            Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                                        }
                                                    }

                                                } else {
                                                    if (lenguajeElegido == null) {

                                                    } else if (lenguajeElegido != null) {
                                                        if (lenguajeElegido.lenguaje.equals("USA")) {

                                                            Toasty.warning(contexto, "Could not store the log.", Toasty.LENGTH_SHORT).show();


                                                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                            Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                }

                                            }




                                        }

                                    }

                                    if (formaPago.parcial == false) {


                                        if (finalTipoUsuario.equals("10")) {
                                            if (DataBaseBO.guardarFormaPagParcialTipoCobrador(idPago, claseDocumento,
                                                    sociedad, cod_cliente, vendedoresCartera,
                                                    null, fechasDocumentos,
                                                    fecha, precios,
                                                    moneda, preciosfacturasLogica, preciosParcial, cuentasBanco,
                                                    moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                                    consecutivo1,
                                                    descripc, via_Pago, usuario, operacion_Cme,
                                                    sincronizado, "0", "0",
                                                    nombreTitular, fotos.idenFoto,finalConsecutivoid, formaPago != null ? formaPago.observacionesMotivo : "")) {

                                                if (lenguajeElegido == null) {

                                                } else if (lenguajeElegido != null) {
                                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                                        Toasty.warning(contexto, "The log was stored correctly.", Toasty.LENGTH_SHORT).show();


                                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                        Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                                    }
                                                }

                                            } else {
                                                if (lenguajeElegido == null) {

                                                } else if (lenguajeElegido != null) {
                                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                                        Toasty.warning(contexto, "Could not store the log.", Toasty.LENGTH_SHORT).show();


                                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                        Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();

                                                    }
                                                }
                                            }

                                        } else {

                                            if (DataBaseBO.guardarFormaPagParcial(idPago, claseDocumento, sociedad,
                                                    cod_cliente, finalCod_Vendedor,
                                                    "0", fechasDocumentos, fecha, precios,0,
                                                    moneda, preciosfacturasLogica,
                                                    preciosParcial,
                                                    spinnerCuentasBanco.getSelectedItem().toString(), moneda_Consig, NCF_Comprobante_fiscal,
                                                    documentosFinanciero, consecutivo1,
                                                    descripc, via_Pago, usuario, operacion_Cme,
                                                    sincronizado, "0", "0", nombreTitular, fotos.idenFoto,finalConsecutivoid, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "")) {

                                                if (lenguajeElegido == null) {

                                                } else if (lenguajeElegido != null) {
                                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                                        Toasty.warning(contexto, "The log was stored correctly.", Toasty.LENGTH_SHORT).show();


                                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                        Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                                    }
                                                }

                                            } else {
                                                if (lenguajeElegido == null) {

                                                } else if (lenguajeElegido != null) {
                                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                                        Toasty.warning(contexto, "Could not store the log.", Toasty.LENGTH_SHORT).show();


                                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                                        Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();

                                                    }
                                                }
                                            }


                                        }


                                    }

                                } catch (Exception exception) {
                                    System.out.println("Error en la forma de pago parcial " + exception);
                                }

                            }

                            Intent login = new Intent(contexto.getApplicationContext(), MetodosDePagoActivity.class);
                            contexto.startActivity(login);
                            ((MetodosDePagoActivity) contexto).finish();
                            dialogo.dismiss();
                            dialogo.cancel();

                        } else if (valorConvertido > Utilidades.formatearDecimales(valorFLEX-sumaXValorConsignado,2)) {
                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Toasty.warning(contexto, "The value earned is greater than the amount to be collected..", Toasty.LENGTH_SHORT).show();

                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Toasty.warning(contexto, "El valor ingresado es mayor al monto a recaudar..", Toasty.LENGTH_SHORT).show();

                                }
                            }
                        }


                    }
                }


            }
        });


        cancelarFormaPagoFE = dialogo.findViewById(R.id.cancelarFormaPagoFE);
        cancelarFormaPagoFE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<String> documentoFacturas = new ArrayList<>();
                String nroRecibo = "";


                final List<String> fotosListaid = new ArrayList<>();
                String fotoID = "";


                Gson gsonFotos = new Gson();
                String stringJsonObjectFotos = PreferencesFotos.obteneFotoSeleccionada(contexto);
                Fotos fotos = gsonFotos.fromJson(stringJsonObjectFotos, Fotos.class);

                if (fotos == null) {
                    fotoID = null;
                }
                if (fotos != null) {
                    fotoID = fotos.idenFoto;
                    fotosListaid.add(fotos.idenFoto);
                }


                if (facCollection != null) {

                    nroRecibo = clienteSel.consecutivo;
                    documentoFacturas.add(nroRecibo);
                }

                if (anticipo != null) {

                    nroRecibo = clienteSel.consecutivo;
                    documentoFacturas.add(nroRecibo);
                }

                DataBaseBO.eliminarFotoIDFac(fotosListaid);
                DataBaseBO.eliminarFoto(documentoFacturas);

                dialogo.cancel();
            }
        });


        dialogo.setCancelable(false);
        dialogo.show();
    }
}
