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
import sharedpreferences.PreferencesCarteraFactura;
import sharedpreferences.PreferencesClienteSeleccionado;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesFotos;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesParcial;
import utilidades.Utilidades;

public class MetodoDePagoTarjetaUpdate {

    public static Dialog dialogo;
    public static Vector<Bancos> listaParametrosBancosSpinner;
    public static Vector<CuentasBanco> listaParametrosCuentas;

    /**
     * VISTA DIALOGO UPDATE TARJETA
     *
     * @param contexto
     * @param titulo
     * @param texto
     * @param onClickListenerAceptar
     * @param onClickListenerCancelar
     */
    public static void vistaDialogoUpdateTarjeta(final Context contexto, @NonNull String titulo, @NonNull String texto, View.OnClickListener onClickListenerAceptar,
                                                 View.OnClickListener onClickListenerCancelar) {

        ImageView cancelarFormaPagoFE, guardarFormaPagoFE, guardarFormaPagoPendienteTarje;
        final TextView tvNROTarjeta, tvDescripcionTarjeta, tvFechaFragTarje;
        final TextView tvNombreTitular, tvFechaExpiracion, tvMonto, simboloTarjeta;
        final EditText tvValorFragEfec;
        final Button tomarFoto;
        final Cartera facturaCartera;
        final FormaPago formaPago;
        final Anticipo anticipo;
        final ClienteSincronizado clienteSel;
        List<Facturas> listaFacturas2;
        List<Facturas> listaFacturas4;
        List<Facturas> cargarFacturas;
        final List<Facturas> listsaFacturasParcialTotal;
        final List<Facturas> listaFacturasTarjeta;
        final List<Facturas> listsaFacturasParcialTotalPendientes;
        final List<Facturas> listsaFacturasParcialTotal2;
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
        //  tvDescripcionTarjeta = dialogo.findViewById(R.id.tvDescripcionTarjeta);
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
        tipoUsuario = DataBaseBO.cargarTipoUsuarioApp(contexto);

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
        int consecutivoInicial = 0;
        String consecutivofinal = "";
        String consecutivoNegocio = "";
        String consecutivoVendedor = "";
        String codigoVendedor = "";
        double DiferenciaFormasPago;
        codigoVendedor = DataBaseBO.cargarCodigo(contexto);
        empresa = DataBaseBO.cargarEmpresa(contexto);
        monedaTipo = DataBaseBO.cargarMoneda(contexto);

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
        listaParametrosCuentas = DataBaseBO.cargarCuentasBancosSolo(listaItems, contexto);

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
                    //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
                    /**
                     *También puede cargar los valores que usted desee
                     */
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

        Gson gsonFacturas = new Gson();
        List<Facturas> facturaS = new ArrayList<>();
        Type collectionTypeFacturas = new TypeToken<Collection<Facturas>>() {
        }.getType();

        String stringJsonObjectFacturas = PreferencesCarteraFactura.obteneFacturaSeleccionada(contexto);
        Collection<Facturas> facCollectionFacturas = gsonFacturas.fromJson(stringJsonObjectFacturas, collectionTypeFacturas);

        final List<String> idesPago = new ArrayList<>();
        final List<String> idesPagoTarjeta = new ArrayList<>();
        String idsPagos = "",idFotos = "";

        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject222, collectionType);

        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        final List<String> documentosFinancieroAnticipo = new ArrayList<>();
        final List<String> precios = new ArrayList<>();
        final List<String> fechaConsignacionesAntiPend = new ArrayList<>();
        final List<String> valoresfacturas = new ArrayList<>();
        final List<String> valoresfacturasEfectivo = new ArrayList<>();
        final List<String> valoresfacturasTransfe = new ArrayList<>();
        final List<String> valoresfacturasCheque = new ArrayList<>();
        final List<String> valoresfacturasBitcoin = new ArrayList<>();
        final List<String> preciosAcomparar = new ArrayList<>();//////
        final List<String> totalFacturas = new ArrayList<>();
        final List<String> totalFacturasTransfe = new ArrayList<>();
        final List<String> totalFacturasCheque = new ArrayList<>();
        final List<String> totalFacturasTarjeta = new ArrayList<>();
        final List<String> totalFacturasEfectivo = new ArrayList<>();
        final List<String> totalFacturasBitcoin = new ArrayList<>();
        final List<String> preciosParcial = new ArrayList<>();
        final List<String> preciosParcialEfectivo = new ArrayList<>();
        final List<String> preciosParcialTrasnf = new ArrayList<>();
        final List<String> preciosParcialCheque = new ArrayList<>();
        final List<String> preciosParcialTarjeta = new ArrayList<>();
        final List<String> preciosParcialBitcoin = new ArrayList<>();
        final List<String> preciosAnticipo = new ArrayList<>();
        final List<String> documentt = new ArrayList<>();
        final List<String> preciosfacturasLogica = new ArrayList<>();
        final List<String> preciosfacturasLogicaEfectivo = new ArrayList<>();
        final List<String> preciosfacturasLogicaTarjeta = new ArrayList<>();
        final List<String> preciosfacturasLogicaTransfe = new ArrayList<>();
        final List<String> preciosfacturasLogicaCheque = new ArrayList<>();
        final List<String> preciosfacturasLogicaBitcoin = new ArrayList<>();
        final List<String> fechasDocumentos = new ArrayList<>();
        final List<Facturas> listaFacturas3 = new ArrayList<>();
        final List<String> preciosComparar = new ArrayList<>();
        final List<String> listaPrecios = new ArrayList<>();
        final List<String> preciosListaTotal = new ArrayList<>();

        String claseDocument = "";
        String preciosFacturas = "";
        String documentoFinanciero = "";
        String fechasDocus = "";
        String nombreU = "";
        double precioTotal = 0;
        String document = "";
        double valorSetText = 0;
        String vendedorCartera = "";

        if (facCollection != null) {

            for (Cartera cartera1 : facCollection) {
                vendedorCartera = cartera1.getVendedor();
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
                precios.add(preciosFacturas);
                listaPrecios.add(preciosFacturas);
                preciosListaTotal.add(preciosFacturas);
                documentosFinanciero.add(documentoFinanciero);

            }


        }

        double precioTo = Utilidades.formatearDecimales(precioTotal, 2);

        for (Facturas facturasids : facCollectionFacturas) {
            idsPagos = facturasids.getIdPago();
            idesPago.add(idsPagos);
            valorSetText = (facturasids.valor);
            idFotos = facturasids.idenFoto;

        }

        if (anticipo != null) {
            if (anticipo.estado == true) {
                claseDocumento.add("DZ");
                documentoFinanciero = null;
                documentosFinanciero.add(null);
            } else if (anticipo.estado == false) {
                claseDocumento.add("DZ");
                documentoFinanciero = null;
                documentosFinanciero.add(null);
            }

        }

        consecutivo = DataBaseBO.cargarConsecutivoUpdate(idsPagos, contexto);

        double countEfectivo = 0;
        double countCheque = 0;
        double countTarjeta = 0;
        double countTrasnferencia = 0;

        double DiferenciaFormasPagoE = 0;
        double DiferenciaFormasPagoPEN = 0;
        double valorConsignadoEfectivo = 0;
        double valorConsignadoCheque = 0;
        double valorConsignadoTarjeta = 0;
        double valorConsignadoTransferencia = 0;
        double valorConsignadoBitcoin = 0;

        String nroRecibo = "";

        if (anticipo != null) {

            String parametroCme = "A";

            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);

        }


        String nroReciboFacTotalPar = clienteSel.consecutivo;
        // listsaFacturasParcialTotal = DataBaseBO.cargarFacParTotal(nroReciboFacTotalPar, documentosFinanciero);
        listsaFacturasParcialTotal = Utilidades.listaFacturasParcialTotal(contexto);
        listaFacturasTarjeta = Utilidades.listaFacturasMetodoTarjeta(contexto, idesPago);


        for (Facturas fac : listsaFacturasParcialTotal) {
            String acert = "";
            acert = fac.idPago;
            double valorCom = 0;
            valorCom = fac.valor;
            valoresfacturas.add(String.valueOf(Utilidades.formatearDecimales(valorCom, 2)));

        }

        DiferenciaFormasPagoE = Utilidades.totalFormasPago(contexto);

        countEfectivo = Utilidades.CountMetodoPagoEfec(contexto);
        countCheque = Utilidades.CountMetodoPagoCheq(contexto);
        countTarjeta = Utilidades.CountMetodoPagoTarjeta(contexto);
        countTrasnferencia = Utilidades.CountMetodoPagoTransF(contexto);

        valorConsignadoEfectivo = Utilidades.totalFormasPagoEfec(contexto);
        valorConsignadoTransferencia = Utilidades.totalFormasPagoTranF(contexto);
        valorConsignadoCheque = Utilidades.totalFormasPagoCheq(contexto);
        valorConsignadoBitcoin = Utilidades.totalFormasPagoBit(contexto);
        valorConsignadoTarjeta = Utilidades.totalFormasPagoTarje(contexto);

        DiferenciaFormasPago = (DiferenciaFormasPagoE);

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

            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));

                String resultados = (formatoNumero.format(anticipo.valor - (DiferenciaFormasPago)));
                tvMonto.setText(resultados);
                valorfinal = anticipo.valor;
                preciosAnticipo.add(String.valueOf(valorfinal));

            }

        }

        tvNombreTitular.setText("");
        tvNROTarjeta.setText("");

        int contador = 1;
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
        final String docto_Financiero = "0";
        final String consecutivo1 = consecutivo;
        final int consecutivo2 = clienteSel.consecutivoInicial;
        final String via_Pago = "O";
        final String usuario = codigoVendedor;
        final String operacion_Cme = null;
        final double finalValor = valorfinal;
        final int sincronizado = 0;
        int Position = 2;
        codigoVendedor = codigoVendedor.substring(0, Position);
        final String finalParam = param;
        final double finalValorfinal = valorfinal;
        final String finalIdsPagos = idsPagos;
        final String finalIdsFotos = idFotos;
        final String finalDocumentoFinanciero = documentoFinanciero;
        int numero = (int) (Math.random() * 1000) + 1;
        final String idFoto = idsPagos + "_ID_" + numero;
        final String nombreFoto = idsPagos + numero + ".jpg";
        final String idenFoto = codigoVendedor + Utilidades.fechaActual("HHmmss") + 1;
        final String idPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss");
        final String idEfectivoPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + 4;
        final String idChequePago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + 1;
        final String idTarjetaPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + 2;
        final String idTransferenciaPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + 3;


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
                    MetodoDePagoTarjetaUpdate.dialogo.onSaveInstanceState();


                }
            });
        }


        cargarFacturas = DataBaseBO.cargarFacturasParametroIDS(idesPago, contexto);

        if (cargarFacturas.size() > 0) {
            for (Facturas facturas1 : cargarFacturas) {
                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                    tvValorFragEfec.setText(formatoNumero.format(valorSetText));

                } else {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                    tvValorFragEfec.setText(formatoNumero.format(valorSetText));

                }

                tvFechaFragTarje.setText(facturas1.getFechaConsignacion());
                tvNombreTitular.setText(facturas1.getNombrePropietario());

            }
        }


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

        double finalValorConsignadoEfect = valorConsignadoEfectivo;
        double finalValorConsignadoCheque = valorConsignadoCheque;
        double finalValorConsignadoTransferencia = valorConsignadoTransferencia;
        double finalValorConsignadoBitcoin = valorConsignadoBitcoin;
        double finalValorConsignadoTarjeta = valorConsignadoTarjeta;

        String finalIdsPagos1 = idsPagos;
        double finalCountTrasnferencia = countTrasnferencia;
        double finalCountTarjeta = countTarjeta;

        double finalCountEfectivo = countEfectivo;
        double finalCountCheque = countCheque;

        String finalCod_Vendedor = cod_Vendedor;


        String vendedor = "";

        vendedor = DataBaseBO.cargarVendedorConsecutivo(contexto);

        DataBaseBO.eliminarConsecutivoId(vendedor, contexto);

        final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
        String consecId1 = "", numeroAnulacionId1 = "";
        String negocioId1 = "";
        String vendedorId1 = "";

        String consecutivoid = "";
        String consecutivoidEfectivo2 = "";
        String consecutivoidCheque = "";
        String consecutivoidTarjeta = "";
        String consecutivoidTransferencia = "";

        consecId1 = DataBaseBO.cargarConsecutivoId(contexto);
        negocioId1 = DataBaseBO.cargarNegocioConsecutivoId(contexto);
        vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId(contexto);

        int consec1Id = Integer.parseInt(consecId1);
        int consec1IdEfec = Integer.parseInt(consecId1) + 1;
        int consec1IdCheq = Integer.parseInt(consecId1) + 2;
        int consec1IdTarjeta = Integer.parseInt(consecId1) + 3;
        int consec1IdTransferencia = Integer.parseInt(consecId1) + 4;

        int vendedorsumId = Integer.parseInt(vendedorId1);
        int contadorId = 1;
        consec1Id = consec1Id + contadorId;
        numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

        DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon, contexto);

        consecutivoid = String.valueOf(negocioId1 + vendedorsumId + consec1Id);
        consecutivoidEfectivo2 = String.valueOf(negocioId1 + vendedorsumId + consec1IdEfec);
        consecutivoidCheque = String.valueOf(negocioId1 + vendedorsumId + consec1IdCheq);
        consecutivoidTarjeta = String.valueOf(negocioId1 + vendedorsumId + consec1IdTarjeta);
        consecutivoidTransferencia = String.valueOf(negocioId1 + vendedorsumId + consec1IdTransferencia);

        String finalConsecutivoid = consecutivoid;
        String finalConsecutivoidCheque = consecutivoidCheque;
        String finalConsecutivoidEfectivo = consecutivoidEfectivo2;
        String finalConsecutivoidTransferencia = consecutivoidTransferencia;
        String finalConsecutivoidTarjeta = consecutivoidTarjeta;
        int finalconsec1Id = consec1Id;

        Utilidades.formatearDecimales(salfoAFA, 2);

        double finalValorfinal1 = Utilidades.formatearDecimales(valorfinal, 2);

        String finalCodigoVendedor = codigoVendedor;
        guardarFormaPagoFE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

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
                String inputEfect = "";
                String inputTransf = "";
                String inputCheque = "";
                String inputTarjeta = "";
                String cuentasBanco = "";
                String spinBanco = "";
                double valorEfec = finalValorConsignadoEfect;
                double valorCheque = finalValorConsignadoCheque;
                double valorTransf = finalValorConsignadoTransferencia;
                double valorBit = finalValorConsignadoBitcoin;
                double valorTarj = finalValorConsignadoTarjeta;
                String inputBitcoin = "";
                double totalesValoresParciales = 0;
                double resultadosValores = 0;

                String nombreTitular = tvNombreTitular.getText().toString();
                String nrotarjetaEfec = tvNROTarjeta.getText().toString();
                String descripc = "";
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

                if (valor == 0) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "The amount field cannot be blank...", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo del monto no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

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
                                        valorobtenido = Double.parseDouble(precios.get(i));

                                        if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                                                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                                            if (!input.isEmpty()) {
                                                if (input.contains(".") && input.contains(",")) {

                                                    input = input.replace(".", "");
                                                    input = input.replace(",", ".");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(",")) {

                                                    input = input.replace(",", ".");
                                                    valor = Double.parseDouble(input);
                                                } else if (input.contains(".")) {

                                                    input = input.replace(".", "");
                                                    valor = Double.parseDouble(input);

                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }

                                        } else if (finalEmpresa.equals("AGCO")) {

                                            if (!input.isEmpty()) {

                                                if (input.contains(".")) {

                                                    input = input.replace(".", "");
                                                    valor = Double.parseDouble(input);


                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }

                                        } else {

                                            if (!input.isEmpty()) {

                                                if (input.contains(",")) {

                                                    input = input.replace(",", "");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(".")) {


                                                    valor = Double.parseDouble(input);


                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }
                                        }

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
                                        valorobtenido = Double.parseDouble(precios.get(i));

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

//                                    switch (i) {
//
//                                        case 0:
//
//                                            valorobtenido = Double.parseDouble(precios.get(i));
//
//                                            if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
//                                                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {
//
//                                                if (!input.isEmpty()) {
//                                                    if (input.contains(".") && input.contains(",")) {
//
//                                                        input = input.replace(".", "");
//                                                        input = input.replace(",", ".");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(",")) {
//
//                                                        input = input.replace(",", ".");
//                                                        valor = Double.parseDouble(input);
//                                                    } else if (input.contains(".")) {
//
//                                                        input = input.replace(".", "");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//
//                                            } else if (finalEmpresa.equals("AGCO")) {
//
//                                                if (!input.isEmpty()) {
//
//                                                    if (input.contains(".")) {
//
//                                                        input = input.replace(".", "");
//                                                        valor = Double.parseDouble(input);
//
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//
//                                            } else {
//
//                                                if (!input.isEmpty()) {
//
//                                                    if (input.contains(",")) {
//
//                                                        input = input.replace(",", "");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(".")) {
//
//
//                                                        valor = Double.parseDouble(input);
//
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//                                            }
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor = valor;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (valor < 0) {
//
//                                                    nuevoValor = valor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (valor == 0) {
//
//                                                    nuevoValor = valor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (valor < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valor));
//
//                                                } else if (valor > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (valor == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                }
//
//                                            }
//
//                                            break;
//
//                                        case 1:
//
//                                            valorobtenido = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor2 = nuevoValor;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor < 0) {
//
//                                                    nuevoValor2 = nuevoValor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor == 0) {
//
//                                                    nuevoValor2 = nuevoValor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor < valorobtenido) {
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor));
//
//
//                                                } else if (nuevoValor > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                } else if (nuevoValor == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 2:
//
//                                            valorobtenido = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor3 = nuevoValor2;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor2 < 0) {
//
//                                                    nuevoValor3 = nuevoValor2;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor2 == 0) {
//
//                                                    nuevoValor3 = nuevoValor2;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor2 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                } else if (nuevoValor2 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor2 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 3:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor4 = nuevoValor3;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor3 < 0) {
//
//                                                    nuevoValor4 = nuevoValor3;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor3 == 0) {
//
//                                                    nuevoValor4 = nuevoValor3;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor3 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                } else if (nuevoValor3 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor3 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 4:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor5 = nuevoValor4;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor4 < 0) {
//
//                                                    nuevoValor5 = nuevoValor4;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor4 == 0) {
//
//                                                    nuevoValor5 = nuevoValor4;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor4 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                } else if (nuevoValor4 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor4 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 5:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor6 = nuevoValor5;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor5 < 0) {
//
//                                                    nuevoValor6 = nuevoValor5;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor5 == 0) {
//
//                                                    nuevoValor6 = nuevoValor5;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor5 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                } else if (nuevoValor5 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor5 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 6:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor7 = nuevoValor6;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor6 < 0) {
//
//                                                    nuevoValor7 = nuevoValor6;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor6 == 0) {
//
//                                                    nuevoValor7 = nuevoValor6;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor6 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                } else if (nuevoValor6 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor6 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 7:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor8 = nuevoValor7;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor7 < 0) {
//
//                                                    nuevoValor8 = nuevoValor7;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor7 == 0) {
//
//                                                    nuevoValor8 = nuevoValor7;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor7 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                } else if (nuevoValor7 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor7 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 8:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor9 = nuevoValor8;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor8 < 0) {
//
//                                                    nuevoValor9 = nuevoValor8;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor8 == 0) {
//
//                                                    nuevoValor9 = nuevoValor8;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor8 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//
//                                                } else if (nuevoValor8 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                } else if (nuevoValor8 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//                                                }
//
//
//                                            }
//
//
//                                            break;
//                                        case 9:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor10 = nuevoValor9;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor9 < 0) {
//
//                                                    nuevoValor10 = nuevoValor9;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor9 == 0) {
//
//                                                    nuevoValor10 = nuevoValor9;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor9 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//
//                                                } else if (nuevoValor9 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                } else if (nuevoValor9 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 10:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor11 = nuevoValor10;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor10 < 0) {
//
//                                                    nuevoValor11 = nuevoValor10;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor10 == 0) {
//
//                                                    nuevoValor11 = nuevoValor10;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor10 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                } else if (nuevoValor10 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor10 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                }
//
//                                            }
//
//                                            break;
//                                        default:
//                                            break;
//                                    }

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

                                    if(i == 0)
                                    {
                                        valorobtenido = Double.parseDouble(precios.get(i));

                                        if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                                                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                                            if (!input.isEmpty()) {
                                                if (input.contains(".") && input.contains(",")) {

                                                    input = input.replace(".", "");
                                                    input = input.replace(",", ".");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(",")) {

                                                    input = input.replace(",", ".");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(".")) {


                                                    valor = Double.parseDouble(input);

                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }

                                        } else if (finalEmpresa.equals("AGCO")) {

                                            if (!input.isEmpty()) {

                                                if (input.contains(".")) {

                                                    input = input.replace(".", "");
                                                    valor = Double.parseDouble(input);


                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }

                                        } else {

                                            if (!input.isEmpty()) {

                                                if (input.contains(",")) {

                                                    input = input.replace(",", "");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(".")) {

                                                    valor = Double.parseDouble(input);


                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }
                                        }

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

//                                    switch (i) {
//
//                                        case 0:
//
//                                            valorobtenido = Double.parseDouble(precios.get(i));
//
//                                            if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
//                                                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {
//
//                                                if (!input.isEmpty()) {
//                                                    if (input.contains(".") && input.contains(",")) {
//
//                                                        input = input.replace(".", "");
//                                                        input = input.replace(",", ".");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(",")) {
//
//                                                        input = input.replace(",", ".");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(".")) {
//
//
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//
//                                            } else if (finalEmpresa.equals("AGCO")) {
//
//                                                if (!input.isEmpty()) {
//
//                                                    if (input.contains(".")) {
//
//                                                        input = input.replace(".", "");
//                                                        valor = Double.parseDouble(input);
//
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//
//                                            } else {
//
//                                                if (!input.isEmpty()) {
//
//                                                    if (input.contains(",")) {
//
//                                                        input = input.replace(",", "");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(".")) {
//
//                                                        valor = Double.parseDouble(input);
//
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//                                            }
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor = valor;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (valor < 0) {
//
//                                                    nuevoValor = valor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (valor == 0) {
//
//                                                    nuevoValor = valor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (valor < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valor));
//
//                                                } else if (valor > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (valor == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenido, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                }
//
//                                            }
//
//                                            break;
//
//                                        case 1:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor2 = nuevoValor;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor < 0) {
//
//                                                    nuevoValor2 = nuevoValor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor == 0) {
//
//                                                    nuevoValor2 = nuevoValor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor < valorobtenido) {
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor));
//
//
//                                                } else if (nuevoValor > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                } else if (nuevoValor == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenido, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 2:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor3 = nuevoValor2;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor2 < 0) {
//
//                                                    nuevoValor3 = nuevoValor2;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor2 == 0) {
//
//                                                    nuevoValor3 = nuevoValor2;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor2 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                } else if (nuevoValor2 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor2 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenido, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 3:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor4 = nuevoValor3;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor3 < 0) {
//
//                                                    nuevoValor4 = nuevoValor3;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor3 == 0) {
//
//                                                    nuevoValor4 = nuevoValor3;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor3 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                } else if (nuevoValor3 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor3 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenido, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 4:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor5 = nuevoValor4;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor4 < 0) {
//
//                                                    nuevoValor5 = nuevoValor4;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor4 == 0) {
//
//                                                    nuevoValor5 = nuevoValor4;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor4 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                } else if (nuevoValor4 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor4 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenido, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 5:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor6 = nuevoValor5;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor5 < 0) {
//
//                                                    nuevoValor6 = nuevoValor5;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor5 == 0) {
//
//                                                    nuevoValor6 = nuevoValor5;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor5 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                } else if (nuevoValor5 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor5 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenido, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 6:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor7 = nuevoValor6;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor6 < 0) {
//
//                                                    nuevoValor7 = nuevoValor6;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor6 == 0) {
//
//                                                    nuevoValor7 = nuevoValor6;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor6 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                } else if (nuevoValor6 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor6 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenido, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 7:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor8 = nuevoValor7;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor7 < 0) {
//
//                                                    nuevoValor8 = nuevoValor7;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor7 == 0) {
//
//                                                    nuevoValor8 = nuevoValor7;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor7 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                } else if (nuevoValor7 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor7 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenido, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 8:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor9 = nuevoValor8;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor8 < 0) {
//
//                                                    nuevoValor9 = nuevoValor8;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor8 == 0) {
//
//                                                    nuevoValor9 = nuevoValor8;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor8 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//
//                                                } else if (nuevoValor8 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                } else if (nuevoValor8 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenido, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//                                                }
//
//
//                                            }
//
//
//                                            break;
//                                        case 9:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor10 = nuevoValor9;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor9 < 0) {
//
//                                                    nuevoValor10 = nuevoValor9;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor9 == 0) {
//
//                                                    nuevoValor10 = nuevoValor9;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor9 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//
//                                                } else if (nuevoValor9 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//
//                                                } else if (nuevoValor9 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenido, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 10:
//
//                                            if (valorobtenido < 0) {
//
//                                                nuevoValor11 = nuevoValor10;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenido > 0) {
//
//                                                if (nuevoValor10 < 0) {
//
//                                                    nuevoValor11 = nuevoValor10;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor10 == 0) {
//
//                                                    nuevoValor11 = nuevoValor10;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor10 < valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                } else if (nuevoValor10 > valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenido));
//
//                                                } else if (nuevoValor10 == valorobtenido) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenido, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                }
//
//                                            }
//
//                                            break;
//                                        default:
//                                            break;
//                                    }

                                    Gson gson33 = new Gson();
                                    String jsonStringObject = gson33.toJson(preciosParcial.get(i));
                                    PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                }

                            }

                        } catch (Exception exception) {
                            System.out.println("Error en la forma de pago parcial " + exception);
                        }

                    }


                    ///METODOS DE PAGO
//Tarjeta
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
                                        valorobtenidoSegundo = Double.parseDouble(precios.get(i));


                                        if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                                                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                                            if (!input.isEmpty()) {
                                                if (input.contains(".") && input.contains(",")) {

                                                    input = input.replace(".", "");
                                                    input = input.replace(",", ".");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(",")) {

                                                    input = input.replace(",", ".");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(".")) {


                                                    valor = Double.parseDouble(input);

                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }

                                        } else if (finalEmpresa.equals("AGCO")) {

                                            if (!input.isEmpty()) {

                                                if (input.contains(".")) {

                                                    input = input.replace(".", "");
                                                    valor = Double.parseDouble(input);


                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }

                                        } else {

                                            if (!input.isEmpty()) {

                                                if (input.contains(",")) {

                                                    input = input.replace(",", "");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(".")) {

                                                    valor = Double.parseDouble(input);


                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }
                                        }

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
                                        valorobtenidoSegundo = Double.parseDouble(precios.get(i));


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

//                                    switch (i) {
//
//                                        case 0:
//
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//
//                                            if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
//                                                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {
//
//                                                if (!input.isEmpty()) {
//                                                    if (input.contains(".") && input.contains(",")) {
//
//                                                        input = input.replace(".", "");
//                                                        input = input.replace(",", ".");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(",")) {
//
//                                                        input = input.replace(",", ".");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(".")) {
//
//
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//
//                                            } else if (finalEmpresa.equals("AGCO")) {
//
//                                                if (!input.isEmpty()) {
//
//                                                    if (input.contains(".")) {
//
//                                                        input = input.replace(".", "");
//                                                        valor = Double.parseDouble(input);
//
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//
//                                            } else {
//
//                                                if (!input.isEmpty()) {
//
//                                                    if (input.contains(",")) {
//
//                                                        input = input.replace(",", "");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(".")) {
//
//                                                        valor = Double.parseDouble(input);
//
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//                                            }
//
//                                            if (valorobtenidoSegundo < 0) {
//
//                                                nuevoValor = valor;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                if (valor < 0) {
//
//                                                    nuevoValor = valor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (valor == 0) {
//
//                                                    nuevoValor = valor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (valor < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valor));
//
//                                                } else if (valor > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (valor == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                }
//
//                                            }
//
//                                            break;
//
//                                        case 1:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//
//                                            if (valorobtenidoSegundo < 0) {
//
//                                                nuevoValor2 = nuevoValor;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                if (nuevoValor < 0) {
//
//                                                    nuevoValor2 = nuevoValor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor == 0) {
//
//                                                    nuevoValor2 = nuevoValor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor < valorobtenidoSegundo) {
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor));
//
//
//                                                } else if (nuevoValor > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                } else if (nuevoValor == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                }
//
//                                            }
//
//                                            break;
//                                        case 2:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor3 = nuevoValor2;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor2 < 0) {
//
//                                                    nuevoValor3 = nuevoValor2;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor2 == 0) {
//
//                                                    nuevoValor3 = nuevoValor2;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor2 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                } else if (nuevoValor2 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor2 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        case 3:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor4 = nuevoValor3;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor3 < 0) {
//
//                                                    nuevoValor4 = nuevoValor3;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor3 == 0) {
//
//                                                    nuevoValor4 = nuevoValor3;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor3 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                } else if (nuevoValor3 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor3 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                }
//                                            }
//
//                                            break;
//                                        case 4:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor5 = nuevoValor4;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor4 < 0) {
//
//                                                    nuevoValor5 = nuevoValor4;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor4 == 0) {
//
//                                                    nuevoValor5 = nuevoValor4;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor4 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                } else if (nuevoValor4 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor4 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                }
//                                            }
//
//                                            break;
//                                        case 5:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor6 = nuevoValor5;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor5 < 0) {
//
//                                                    nuevoValor6 = nuevoValor5;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor5 == 0) {
//
//                                                    nuevoValor6 = nuevoValor5;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor5 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                } else if (nuevoValor5 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor5 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        case 6:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor7 = nuevoValor6;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor6 < 0) {
//
//                                                    nuevoValor7 = nuevoValor6;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor6 == 0) {
//
//                                                    nuevoValor7 = nuevoValor6;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor6 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                } else if (nuevoValor6 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor6 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                }
//                                            }
//
//                                            break;
//                                        case 7:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor8 = nuevoValor7;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor7 < 0) {
//
//                                                    nuevoValor8 = nuevoValor7;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor7 == 0) {
//
//                                                    nuevoValor8 = nuevoValor7;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor7 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                } else if (nuevoValor7 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor7 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        case 8:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor9 = nuevoValor8;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor8 < 0) {
//
//                                                    nuevoValor9 = nuevoValor8;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor8 == 0) {
//
//                                                    nuevoValor9 = nuevoValor8;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor8 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//
//                                                } else if (nuevoValor8 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                } else if (nuevoValor8 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 9:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor10 = nuevoValor9;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor9 < 0) {
//
//                                                    nuevoValor10 = nuevoValor9;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor9 == 0) {
//
//                                                    nuevoValor10 = nuevoValor9;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor9 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//
//                                                } else if (nuevoValor9 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                } else if (nuevoValor9 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        case 10:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor11 = nuevoValor10;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor10 < 0) {
//
//                                                    nuevoValor11 = nuevoValor10;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor10 == 0) {
//
//                                                    nuevoValor11 = nuevoValor10;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor10 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                } else if (nuevoValor10 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor10 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        default:
//                                            break;
//                                    }


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
                                        valorobtenidoSegundo = Double.parseDouble(precios.get(i));


                                        if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                                                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                                            if (!input.isEmpty()) {
                                                if (input.contains(".") && input.contains(",")) {

                                                    input = input.replace(".", "");
                                                    input = input.replace(",", ".");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(",")) {

                                                    input = input.replace(",", ".");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(".")) {


                                                    valor = Double.parseDouble(input);

                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }

                                        } else if (finalEmpresa.equals("AGCO")) {

                                            if (!input.isEmpty()) {

                                                if (input.contains(".")) {

                                                    input = input.replace(".", "");
                                                    valor = Double.parseDouble(input);


                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }

                                        } else {

                                            if (!input.isEmpty()) {

                                                if (input.contains(",")) {

                                                    input = input.replace(",", "");
                                                    valor = Double.parseDouble(input);

                                                } else if (input.contains(".")) {

                                                    valor = Double.parseDouble(input);


                                                } else if (!input.contains(".") && !input.contains(",")) {
                                                    valor = Double.parseDouble(input);
                                                }
                                            }
                                        }

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
                                        valorobtenidoSegundo = Double.parseDouble(precios.get(i));


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

//                                    switch (i) {
//
//                                        case 0:
//
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//
//                                            if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
//                                                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {
//
//                                                if (!input.isEmpty()) {
//                                                    if (input.contains(".") && input.contains(",")) {
//
//                                                        input = input.replace(".", "");
//                                                        input = input.replace(",", ".");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(",")) {
//
//                                                        input = input.replace(",", ".");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(".")) {
//
//
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//
//                                            } else if (finalEmpresa.equals("AGCO")) {
//
//                                                if (!input.isEmpty()) {
//
//                                                    if (input.contains(".")) {
//
//                                                        input = input.replace(".", "");
//                                                        valor = Double.parseDouble(input);
//
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//
//                                            } else {
//
//                                                if (!input.isEmpty()) {
//
//                                                    if (input.contains(",")) {
//
//                                                        input = input.replace(",", "");
//                                                        valor = Double.parseDouble(input);
//
//                                                    } else if (input.contains(".")) {
//
//                                                        valor = Double.parseDouble(input);
//
//
//                                                    } else if (!input.contains(".") && !input.contains(",")) {
//                                                        valor = Double.parseDouble(input);
//                                                    }
//                                                }
//                                            }
//
//                                            if (valorobtenidoSegundo < 0) {
//
//                                                nuevoValor = valor;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                if (valor < 0) {
//
//                                                    nuevoValor = valor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (valor == 0) {
//
//                                                    nuevoValor = valor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (valor < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valor));
//
//                                                } else if (valor > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (valor == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(valor - valorobtenidoSegundo, 2);
//                                                    nuevoValor = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                }
//
//                                            }
//
//                                            break;
//
//                                        case 1:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//
//                                            if (valorobtenidoSegundo < 0) {
//
//                                                nuevoValor2 = nuevoValor;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//
//                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                if (nuevoValor < 0) {
//
//                                                    nuevoValor2 = nuevoValor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor == 0) {
//
//                                                    nuevoValor2 = nuevoValor;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor < valorobtenidoSegundo) {
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor));
//
//
//                                                } else if (nuevoValor > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                } else if (nuevoValor == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                    nuevoValor2 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                }
//
//                                            }
//
//                                            break;
//                                        case 2:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor3 = nuevoValor2;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor2 < 0) {
//
//                                                    nuevoValor3 = nuevoValor2;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor2 == 0) {
//
//                                                    nuevoValor3 = nuevoValor2;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor2 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                } else if (nuevoValor2 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor2 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                    nuevoValor3 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor2));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        case 3:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor4 = nuevoValor3;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor3 < 0) {
//
//                                                    nuevoValor4 = nuevoValor3;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor3 == 0) {
//
//                                                    nuevoValor4 = nuevoValor3;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor3 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                } else if (nuevoValor3 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor3 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                    nuevoValor4 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor3));
//
//                                                }
//                                            }
//
//                                            break;
//                                        case 4:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor5 = nuevoValor4;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor4 < 0) {
//
//                                                    nuevoValor5 = nuevoValor4;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor4 == 0) {
//
//                                                    nuevoValor5 = nuevoValor4;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor4 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                } else if (nuevoValor4 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor4 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                    nuevoValor5 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor4));
//
//                                                }
//                                            }
//
//                                            break;
//                                        case 5:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor6 = nuevoValor5;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor5 < 0) {
//
//                                                    nuevoValor6 = nuevoValor5;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor5 == 0) {
//
//                                                    nuevoValor6 = nuevoValor5;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor5 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                } else if (nuevoValor5 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor5 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                    nuevoValor6 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor5));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        case 6:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor7 = nuevoValor6;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor6 < 0) {
//
//                                                    nuevoValor7 = nuevoValor6;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor6 == 0) {
//
//                                                    nuevoValor7 = nuevoValor6;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor6 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                } else if (nuevoValor6 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor6 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                    nuevoValor7 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor6));
//
//                                                }
//                                            }
//
//                                            break;
//                                        case 7:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor8 = nuevoValor7;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor7 < 0) {
//
//                                                    nuevoValor8 = nuevoValor7;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor7 == 0) {
//
//                                                    nuevoValor8 = nuevoValor7;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor7 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                } else if (nuevoValor7 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor7 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                    nuevoValor8 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor7));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        case 8:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor9 = nuevoValor8;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor8 < 0) {
//
//                                                    nuevoValor9 = nuevoValor8;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor8 == 0) {
//
//                                                    nuevoValor9 = nuevoValor8;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor8 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//
//                                                } else if (nuevoValor8 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                } else if (nuevoValor8 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                    nuevoValor9 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor8));
//
//                                                }
//
//                                            }
//
//
//                                            break;
//                                        case 9:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor10 = nuevoValor9;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor9 < 0) {
//
//                                                    nuevoValor10 = nuevoValor9;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor9 == 0) {
//
//                                                    nuevoValor10 = nuevoValor9;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor9 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//
//                                                } else if (nuevoValor9 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                } else if (nuevoValor9 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                    nuevoValor10 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor9));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        case 10:
//
//                                            valorobtenidoSegundo = Double.parseDouble(precios.get(i));
//
//                                            if (valorobtenidoSegundo < 0) {
//                                                nuevoValor11 = nuevoValor10;
//                                                preciosParcial.add(String.valueOf(valor));
//                                                preciosfacturasLogica.add(String.valueOf(0));
//                                            } else if (valorobtenidoSegundo > 0) {
//                                                if (nuevoValor10 < 0) {
//
//                                                    nuevoValor11 = nuevoValor10;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor10 == 0) {
//
//                                                    nuevoValor11 = nuevoValor10;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(0));
//
//                                                } else if (nuevoValor10 < valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                } else if (nuevoValor10 > valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(valorobtenidoSegundo));
//
//                                                } else if (nuevoValor10 == valorobtenidoSegundo) {
//
//                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                    nuevoValor11 = totalbotenido;
//                                                    preciosParcial.add(String.valueOf(valor));
//                                                    preciosfacturasLogica.add(String.valueOf(nuevoValor10));
//
//                                                }
//                                            }
//
//
//                                            break;
//                                        default:
//                                            break;
//                                    }


                                    PreciosFacturasParcial parcial = new PreciosFacturasParcial();
                                    parcial.valor = Double.parseDouble(preciosParcial.get(i));
                                    Gson gson33 = new Gson();
                                    String jsonStringObject = gson33.toJson(parcial);
                                    PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                }

                            }

                        } catch (Exception exception) {
                            System.out.println("Error en la forma de pago parcial " + exception);
                        }
                    }
/// fac para anticipo 3
                    if (facCollection == null) {

                        if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                            if (!input.isEmpty()) {
                                if (input.contains(".") && input.contains(",")) {

                                    input = input.replace(".", "");
                                    input = input.replace(",", ".");
                                    valor = Double.parseDouble(input);

                                } else if (input.contains(",")) {

                                    input = input.replace(",", ".");
                                    valor = Double.parseDouble(input);

                                } else if (input.contains(".")) {

                                    input = input.replace(".", "");
                                    valor = Double.parseDouble(input);

                                } else if (!input.contains(".") && !input.contains(",")) {
                                    valor = Double.parseDouble(input);
                                }
                            }

                        } else if (finalEmpresa.equals("AGCO")) {

                            if (!input.isEmpty()) {

                                if (input.contains(".")) {

                                    input = input.replace(".", "");
                                    valor = Double.parseDouble(input);


                                } else if (!input.contains(".") && !input.contains(",")) {
                                    valor = Double.parseDouble(input);
                                }
                            }

                        } else {

                            if (!input.isEmpty()) {

                                if (input.contains(",")) {

                                    input = input.replace(",", "");
                                    valor = Double.parseDouble(input);

                                } else if (input.contains(".")) {


                                    valor = Double.parseDouble(input);


                                } else if (!input.contains(".") && !input.contains(",")) {
                                    valor = Double.parseDouble(input);
                                }
                            }
                        }

                        if (input.isEmpty()) {
                            if (lenguajeElegido == null) {

                            } else if (lenguajeElegido != null) {
                                if (lenguajeElegido.lenguaje.equals("USA")) {

                                    Toasty.warning(contexto, "The amount field cannot be blank...", Toasty.LENGTH_SHORT).show();

                                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                    Toasty.warning(contexto, "El campo del monto no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                                }
                            }

                        }

                        precios.add(input);

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



                if (fotos != null){
                    if (!nombreTitular.equals("") && !nrotarjetaEfec.equals("")
                            && !descripc.equals("") && valor != 0 && !fecha.equals("")
                            && !spinnerCuentasBanco.getSelectedItem().toString().equals("Seleccione")
                            || !spinnerCuentasBanco.getSelectedItem().toString().equals("Select") && fotos != null) {

                        if (facCollection == null) {

                            if (anticipo.estado == true) {
                                final String operacion_Cme = "A";

                                Parcial parcial = new Parcial();
                                parcial.parcial = true;
                                Gson gson33 = new Gson();
                                String jsonStringObject = gson33.toJson(parcial);
                                PreferencesParcial.guardarParcialSeleccionada(contexto.getApplicationContext(), jsonStringObject);

                                if (DataBaseBO.updateFormaPagoParcial(finalIdsPagos, claseDocumento, sociedad,
                                        cod_cliente,
                                        finalCod_Vendedor,
                                        "0", fecha_Consignacion, fecha, precios,
                                        moneda, precios,
                                        precios, spinnerCuentasBanco.getSelectedItem().toString(),
                                        moneda_Consig, NCF_Comprobante_fiscal,
                                        finalDocumentoFinanciero, consecutivo1,
                                        descripc, via_Pago, usuario, operacion_Cme, sincronizado, "0",
                                        "0", nombreTitular, fotos.idenFoto, finalConsecutivoid, contexto)) {


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

                                if (DataBaseBO.updateFormaPagoParcial(finalIdsPagos, claseDocumento, sociedad,
                                        cod_cliente,
                                        finalCod_Vendedor,
                                        "0", fecha_Consignacion, fecha, precios,
                                        moneda, precios,
                                        precios, spinnerCuentasBanco.getSelectedItem().toString(),
                                        moneda_Consig, NCF_Comprobante_fiscal,
                                        finalDocumentoFinanciero, consecutivo1,
                                        descripc, via_Pago, usuario, operacion_Cme, sincronizado, "0",
                                        "0", nombreTitular, fotos.idenFoto, finalConsecutivoid, contexto)) {


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

                        if (facCollection != null) {

                            try {

                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo(contexto);
                                DataBaseBO.eliminarConsecutivoId(vendedor, contexto);

                                if (DataBaseBO.updateFormaPago(finalIdsPagos, claseDocumento, sociedad,
                                        cod_cliente,
                                        finalCod_Vendedor,
                                        "0", fechasDocumentos, fecha, precios,
                                        moneda, preciosfacturasLogica,
                                        preciosParcial, spinnerCuentasBanco.getSelectedItem().toString(),
                                        moneda_Consig, NCF_Comprobante_fiscal,
                                        documentosFinanciero, consecutivo1,
                                        descripc, via_Pago, usuario, operacion_Cme, sincronizado, "0",
                                        "0", nombreTitular, fotos.idenFoto, finalConsecutivoid, contexto)) {

                                    DataBaseBO.eliminarRecaudos(finalIdsPagos, contexto);

                                    double DiferenciaFormasPagoUpdate = Utilidades.totalFormasPago(contexto);

                                    double resultadoAfavor;
                                    double saldoAcomparar = 0;
                                    String favorSaldo = "";
                                    for (String salfoFavor : preciosListaTotal) {
                                        saldoAcomparar += Double.parseDouble(salfoFavor);
                                    }

                                    if (DiferenciaFormasPagoUpdate + valorConvertido == formaPago.valor) {
                                        if (DiferenciaFormasPagoUpdate + valorConvertido < saldoAcomparar) {
                                            resultadoAfavor = Utilidades.formatearDecimales(DiferenciaFormasPagoUpdate + valorConvertido - formaPago.valor, 2);

                                        } else {
                                            resultadoAfavor = Utilidades.formatearDecimales(DiferenciaFormasPagoUpdate + valorConvertido - saldoAcomparar, 2) * -1;

                                        }
                                    }else {
                                        resultadoAfavor=0;
                                    }


                                    DataBaseBO.guardarFormaPagParcial(idPago, claseDocumento, sociedad,
                                            cod_cliente,
                                            finalCod_Vendedor,
                                            "0", fechasDocumentos, fecha, precios, resultadoAfavor,
                                            moneda, preciosfacturasLogica,
                                            preciosParcial, spinnerCuentasBanco.getSelectedItem().toString(),
                                            moneda_Consig, NCF_Comprobante_fiscal,
                                            documentosFinanciero, consecutivo1,
                                            descripc, via_Pago, usuario, operacion_Cme, sincronizado, "0",
                                            "0", nombreTitular, fotos.idenFoto, finalConsecutivoid, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto);


                                    if (finalCountTarjeta >= 2) {

                                        final List<Facturas> listsaFacturasParcialTotalTarjeta;
                                        final List<Facturas> listsaIDTarjeta;
                                        final List<String> listaFacHchasUnaPorUna = new ArrayList<>();

                                        double valorComTarjeta = 0;
                                        String idPagoTarjeta = "";
                                        String nombrePropietarioTarjeta = "";
                                        String referenciaTarjeta = "";
                                        String cuentaBancariaTarjeta = "";
                                        String fotoTarjeta = "";

                                        listaFacHchasUnaPorUna.add(idPago);
                                        listsaFacturasParcialTotalTarjeta = Utilidades.listaFacturasParcialTotalTarjeta(contexto);
                                        listsaIDTarjeta = Utilidades.listaFacturasIDTTarjeta(contexto);

                                        for (Facturas facturasids : listaFacturasTarjeta) {
                                            idPagoTarjeta = facturasids.getIdPago();
                                            if (finalIdsPagos1 != idPagoTarjeta) {
                                                idesPagoTarjeta.add(idPagoTarjeta);
                                                cuentaBancariaTarjeta = facturasids.cuentaBancaria;
                                                nombrePropietarioTarjeta = facturasids.nombrePropietario;
                                                referenciaTarjeta = facturasids.referencia;
                                            }
                                        }

                                        for (Facturas fac : listsaIDTarjeta) {
                                            referenciaTarjeta = fac.referencia;
                                            cuentaBancariaTarjeta = fac.cuentaBancaria;
                                            idPagoTarjeta = fac.idPago;
                                            nombrePropietarioTarjeta = fac.nombrePropietario;
                                            fotoTarjeta = fac.idenFoto;

                                        }



                                        for (int k = 0; k < idesPagoTarjeta.size(); k++) {

                                            double valorPorid = Utilidades.totalFormasPagoEfecCantidadFacturasHechas(contexto, idesPagoTarjeta.get(k), listaFacHchasUnaPorUna);
                                            inputTarjeta = String.valueOf(valorPorid);

                                            final List<Facturas> listsaFacturasParcialTotalHechas = Utilidades.listaFacturasParcialTotalHechas(contexto, listaFacHchasUnaPorUna);


                                            if (listsaFacturasParcialTotal.size() > 0) {
                                                try {


                                                    for (int i = 0; i < precios.size(); i++) {

                                                        final List<String> valoresFacReales = new ArrayList<>();

                                                        String valoris = String.valueOf(listsaFacturasParcialTotalHechas.get(k).valor);
                                                        String valorRes = String.valueOf(precios.get(i));
                                                        valoresFacReales.add(valoris);

                                                        if (valoris.equals(valorRes)) {

                                                            valoresFacReales.remove(i);
                                                        }
                                                        if (precios.size() == listsaFacturasParcialTotalHechas.size()) {
                                                            valoresFacReales.clear();
                                                        }

                                                        PreciosFacturasParcial preciosFacturasParcial = new PreciosFacturasParcial();
                                                        double valorobtenido = Double.parseDouble(precios.get(i));
                                                        double valorobtenidoSegundo = 0;
                                                        preciosFacturasParcial.valorobtenido = valorobtenido;
                                                        preciosFacturasParcial.valor = valor;
                                                        String acert = "";
                                                        double valorTotalParcial = 0;

                                                        double valorObtenidoFac = 0;
                                                        String totalesValoresLista = "";


                                                        if(i == 0)
                                                        {
                                                            if (listsaFacturasParcialTotalHechas.size() > 0) {

                                                                if (preciosfacturasLogica.size() > 0) {

                                                                    for (int j = 0; j < preciosfacturasLogica.size(); j++) {

                                                                        if (precios.get(j).equals(preciosfacturasLogica.get(j))) {

                                                                            if (listsaFacturasParcialTotalHechas.size() > 0) {

                                                                                listsaFacturasParcialTotalHechas.remove(j);
                                                                                preciosfacturasLogica.remove(j);
                                                                                precios.remove(j);

                                                                            }

                                                                            documentosFinanciero.remove(j);
                                                                            claseDocumento.remove(j);
                                                                            fechasDocumentos.remove(j);

                                                                        }
                                                                    }
                                                                }
                                                            }


                                                            for (int j = 0; j < preciosfacturasLogica.size(); j++) {

                                                                double valorLista = 0;
                                                                double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
                                                                double valorLista3 = Double.parseDouble(precios.get(j));
                                                                double valorLista4 = valorLista2 + valorLista;

                                                                if (valoresFacReales.size() > 0) {

                                                                    double valorvaloresFacReales = Double.parseDouble(valoresFacReales.get(j));

                                                                    if (valorvaloresFacReales > valorLista2) {
                                                                        totalFacturasTarjeta.clear();
                                                                        preciosParcialTarjeta.clear();
                                                                        preciosfacturasLogicaTarjeta.clear();
                                                                        totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista3 - valorvaloresFacReales, 2));
                                                                        totalFacturasTarjeta.add(totalesValoresLista);
                                                                    }

                                                                } else {

                                                                    totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista3 - valorLista2, 2));
                                                                    totalFacturasTarjeta.add(totalesValoresLista);
                                                                }

                                                            }

                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));


                                                            if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                                                                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                                                                if (!inputTarjeta.isEmpty()) {
                                                                    if (inputTarjeta.contains(".") && inputTarjeta.contains(",")) {

                                                                        inputTarjeta = inputTarjeta.replace(".", "");
                                                                        inputTarjeta = inputTarjeta.replace(",", ".");
                                                                        valorTarj = Double.parseDouble(inputTarjeta);

                                                                    } else if (inputTarjeta.contains(",")) {

                                                                        inputTarjeta = inputTarjeta.replace(",", ".");
                                                                        valorTarj = Double.parseDouble(inputTarjeta);

                                                                    } else if (inputTarjeta.contains(".")) {


                                                                        valorTarj = Double.parseDouble(inputTarjeta);

                                                                    } else if (!inputTarjeta.contains(".") && !inputTarjeta.contains(",")) {
                                                                        valorTarj = Double.parseDouble(inputTarjeta);
                                                                    }
                                                                }

                                                            } else if (finalEmpresa.equals("AGCO")) {

                                                                if (!inputTarjeta.isEmpty()) {

                                                                    if (inputTarjeta.contains(".")) {

                                                                        inputTarjeta = inputTarjeta.replace(".", "");
                                                                        valorTarj = Double.parseDouble(inputTarjeta);


                                                                    } else if (!inputTarjeta.contains(".") && !inputTarjeta.contains(",")) {
                                                                        valorTarj = Double.parseDouble(inputTarjeta);
                                                                    }
                                                                }

                                                            } else {

                                                                if (!inputTarjeta.isEmpty()) {

                                                                    if (inputTarjeta.contains(",")) {

                                                                        inputTarjeta = inputTarjeta.replace(",", "");
                                                                        valorTarj = Double.parseDouble(inputTarjeta);

                                                                    } else if (inputTarjeta.contains(".")) {

                                                                        valorTarj = Double.parseDouble(inputTarjeta);


                                                                    } else if (!inputTarjeta.contains(".") && !inputTarjeta.contains(",")) {
                                                                        valorTarj = Double.parseDouble(inputTarjeta);
                                                                    }
                                                                }
                                                            }

                                                            if (valorobtenidoSegundo < 0) {

                                                                nuevoValor = valorTarj;
                                                                preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                            } else if (valorobtenidoSegundo > 0) {

                                                                if (valorTarj < 0) {

                                                                    nuevoValor = valorTarj;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                                } else if (valorTarj == 0) {

                                                                    nuevoValor = valorTarj;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                                } else if (valorTarj < valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(valorTarj - valorobtenidoSegundo, 2);
                                                                    nuevoValor = totalbotenido;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(valorTarj));

                                                                } else if (valorTarj > valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(valorTarj - valorobtenidoSegundo, 2);
                                                                    nuevoValor = totalbotenido;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));

                                                                } else if (valorTarj == valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(valorTarj - valorobtenidoSegundo, 2);
                                                                    nuevoValor = totalbotenido;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));

                                                                }

                                                            } else if (valorobtenidoSegundo == 0) {

                                                                nuevoValor = valorTarj;
                                                                preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                            }
                                                        }
                                                        else
                                                        {
                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));


                                                            if (valorobtenidoSegundo < 0) {

                                                                nuevoValor2 = nuevoValor;
                                                                preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                            } else if (valorobtenidoSegundo > 0) {

                                                                if (nuevoValor < 0) {

                                                                    nuevoValor2 = nuevoValor;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                                } else if (nuevoValor == 0) {

                                                                    nuevoValor2 = nuevoValor;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                                } else if (nuevoValor < valorobtenidoSegundo) {
                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                    nuevoValor2 = totalbotenido;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor));


                                                                } else if (nuevoValor > valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                    nuevoValor2 = totalbotenido;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));


                                                                } else if (nuevoValor == valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                    nuevoValor2 = totalbotenido;
                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));

                                                                }

                                                            } else if (valorobtenidoSegundo == 0) {

                                                                nuevoValor2 = nuevoValor;
                                                                preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                            }
                                                        }

//                                                        switch (i) {
//
//                                                            case 0:
//
//
//                                                                if (listsaFacturasParcialTotalHechas.size() > 0) {
//
//                                                                    if (preciosfacturasLogica.size() > 0) {
//
//                                                                        for (int j = 0; j < preciosfacturasLogica.size(); j++) {
//
//                                                                            if (precios.get(j).equals(preciosfacturasLogica.get(j))) {
//
//                                                                                if (listsaFacturasParcialTotalHechas.size() > 0) {
//
//                                                                                    listsaFacturasParcialTotalHechas.remove(j);
//                                                                                    preciosfacturasLogica.remove(j);
//                                                                                    precios.remove(j);
//
//                                                                                }
//
//                                                                                documentosFinanciero.remove(j);
//                                                                                claseDocumento.remove(j);
//                                                                                fechasDocumentos.remove(j);
//
//                                                                            }
//                                                                        }
//                                                                    }
//                                                                }
//
//
//                                                                for (int j = 0; j < preciosfacturasLogica.size(); j++) {
//
//                                                                    double valorLista = 0;
//                                                                    double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
//                                                                    double valorLista3 = Double.parseDouble(precios.get(j));
//                                                                    double valorLista4 = valorLista2 + valorLista;
//
//                                                                    if (valoresFacReales.size() > 0) {
//
//                                                                        double valorvaloresFacReales = Double.parseDouble(valoresFacReales.get(j));
//
//                                                                        if (valorvaloresFacReales > valorLista2) {
//                                                                            totalFacturasTarjeta.clear();
//                                                                            preciosParcialTarjeta.clear();
//                                                                            preciosfacturasLogicaTarjeta.clear();
//                                                                            totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista3 - valorvaloresFacReales, 2));
//                                                                            totalFacturasTarjeta.add(totalesValoresLista);
//                                                                        }
//
//                                                                    } else {
//
//                                                                        totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista3 - valorLista2, 2));
//                                                                        totalFacturasTarjeta.add(totalesValoresLista);
//                                                                    }
//
//                                                                }
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//
//                                                                if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
//                                                                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {
//
//                                                                    if (!inputTarjeta.isEmpty()) {
//                                                                        if (inputTarjeta.contains(".") && inputTarjeta.contains(",")) {
//
//                                                                            inputTarjeta = inputTarjeta.replace(".", "");
//                                                                            inputTarjeta = inputTarjeta.replace(",", ".");
//                                                                            valorTarj = Double.parseDouble(inputTarjeta);
//
//                                                                        } else if (inputTarjeta.contains(",")) {
//
//                                                                            inputTarjeta = inputTarjeta.replace(",", ".");
//                                                                            valorTarj = Double.parseDouble(inputTarjeta);
//
//                                                                        } else if (inputTarjeta.contains(".")) {
//
//
//                                                                            valorTarj = Double.parseDouble(inputTarjeta);
//
//                                                                        } else if (!inputTarjeta.contains(".") && !inputTarjeta.contains(",")) {
//                                                                            valorTarj = Double.parseDouble(inputTarjeta);
//                                                                        }
//                                                                    }
//
//                                                                } else if (finalEmpresa.equals("AGCO")) {
//
//                                                                    if (!inputTarjeta.isEmpty()) {
//
//                                                                        if (inputTarjeta.contains(".")) {
//
//                                                                            inputTarjeta = inputTarjeta.replace(".", "");
//                                                                            valorTarj = Double.parseDouble(inputTarjeta);
//
//
//                                                                        } else if (!inputTarjeta.contains(".") && !inputTarjeta.contains(",")) {
//                                                                            valorTarj = Double.parseDouble(inputTarjeta);
//                                                                        }
//                                                                    }
//
//                                                                } else {
//
//                                                                    if (!inputTarjeta.isEmpty()) {
//
//                                                                        if (inputTarjeta.contains(",")) {
//
//                                                                            inputTarjeta = inputTarjeta.replace(",", "");
//                                                                            valorTarj = Double.parseDouble(inputTarjeta);
//
//                                                                        } else if (inputTarjeta.contains(".")) {
//
//                                                                            valorTarj = Double.parseDouble(inputTarjeta);
//
//
//                                                                        } else if (!inputTarjeta.contains(".") && !inputTarjeta.contains(",")) {
//                                                                            valorTarj = Double.parseDouble(inputTarjeta);
//                                                                        }
//                                                                    }
//                                                                }
//
//                                                                if (valorobtenidoSegundo < 0) {
//
//                                                                    nuevoValor = valorTarj;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                } else if (valorobtenidoSegundo > 0) {
//
//                                                                    if (valorTarj < 0) {
//
//                                                                        nuevoValor = valorTarj;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (valorTarj == 0) {
//
//                                                                        nuevoValor = valorTarj;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (valorTarj < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(valorTarj - valorobtenidoSegundo, 2);
//                                                                        nuevoValor = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorTarj));
//
//                                                                    } else if (valorTarj > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(valorTarj - valorobtenidoSegundo, 2);
//                                                                        nuevoValor = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    } else if (valorTarj == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(valorTarj - valorobtenidoSegundo, 2);
//                                                                        nuevoValor = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    }
//
//                                                                } else if (valorobtenidoSegundo == 0) {
//
//                                                                    nuevoValor = valorTarj;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                }
//
//                                                                break;
//
//                                                            case 1:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//
//                                                                if (valorobtenidoSegundo < 0) {
//
//                                                                    nuevoValor2 = nuevoValor;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                } else if (valorobtenidoSegundo > 0) {
//
//                                                                    if (nuevoValor < 0) {
//
//                                                                        nuevoValor2 = nuevoValor;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor == 0) {
//
//                                                                        nuevoValor2 = nuevoValor;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor < valorobtenidoSegundo) {
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                        nuevoValor2 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor));
//
//
//                                                                    } else if (nuevoValor > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                        nuevoValor2 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                    } else if (nuevoValor == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                        nuevoValor2 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    }
//
//                                                                } else if (valorobtenidoSegundo == 0) {
//
//                                                                    nuevoValor2 = nuevoValor;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                }
//
//                                                                break;
//                                                            case 2:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//                                                                if (valorobtenidoSegundo < 0) {
//                                                                    nuevoValor3 = nuevoValor2;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                } else if (valorobtenidoSegundo > 0) {
//                                                                    if (nuevoValor2 < 0) {
//
//                                                                        nuevoValor3 = nuevoValor2;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor2 == 0) {
//
//                                                                        nuevoValor3 = nuevoValor2;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor2 < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor3 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor2));
//
//                                                                    } else if (nuevoValor2 > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor3 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    } else if (nuevoValor2 == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor3 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor2));
//
//                                                                    }
//                                                                } else if (valorobtenidoSegundo == 0) {
//                                                                    nuevoValor3 = nuevoValor2;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                }
//
//
//                                                                break;
//                                                            case 3:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//                                                                if (valorobtenidoSegundo < 0) {
//                                                                    nuevoValor4 = nuevoValor3;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                } else if (valorobtenidoSegundo > 0) {
//                                                                    if (nuevoValor3 < 0) {
//
//                                                                        nuevoValor4 = nuevoValor3;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor3 == 0) {
//
//                                                                        nuevoValor4 = nuevoValor3;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor3 < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor4 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor3));
//
//                                                                    } else if (nuevoValor3 > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor4 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    } else if (nuevoValor3 == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor4 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor3));
//
//                                                                    }
//                                                                }
//
//                                                                break;
//                                                            case 4:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//                                                                if (valorobtenidoSegundo < 0) {
//                                                                    nuevoValor5 = nuevoValor4;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                } else if (valorobtenidoSegundo > 0) {
//                                                                    if (nuevoValor4 < 0) {
//
//                                                                        nuevoValor5 = nuevoValor4;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor4 == 0) {
//
//                                                                        nuevoValor5 = nuevoValor4;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor4 < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor5 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor4));
//
//                                                                    } else if (nuevoValor4 > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor5 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    } else if (nuevoValor4 == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor5 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor4));
//
//                                                                    }
//                                                                }
//
//                                                                break;
//                                                            case 5:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//                                                                if (valorobtenidoSegundo < 0) {
//                                                                    nuevoValor6 = nuevoValor5;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                } else if (valorobtenidoSegundo > 0) {
//                                                                    if (nuevoValor5 < 0) {
//
//                                                                        nuevoValor6 = nuevoValor5;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor5 == 0) {
//
//                                                                        nuevoValor6 = nuevoValor5;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor5 < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor6 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor5));
//
//                                                                    } else if (nuevoValor5 > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor6 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    } else if (nuevoValor5 == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor6 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor5));
//
//                                                                    }
//                                                                }
//
//
//                                                                break;
//                                                            case 6:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//                                                                if (valorobtenidoSegundo < 0) {
//                                                                    nuevoValor7 = nuevoValor6;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                } else if (valorobtenidoSegundo > 0) {
//                                                                    if (nuevoValor6 < 0) {
//
//                                                                        nuevoValor7 = nuevoValor6;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor6 == 0) {
//
//                                                                        nuevoValor7 = nuevoValor6;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor6 < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor7 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor6));
//
//                                                                    } else if (nuevoValor6 > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor7 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    } else if (nuevoValor6 == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor7 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor6));
//
//                                                                    }
//                                                                }
//
//                                                                break;
//                                                            case 7:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//                                                                if (valorobtenidoSegundo < 0) {
//                                                                    nuevoValor8 = nuevoValor7;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                } else if (valorobtenidoSegundo > 0) {
//                                                                    if (nuevoValor7 < 0) {
//
//                                                                        nuevoValor8 = nuevoValor7;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor7 == 0) {
//
//                                                                        nuevoValor8 = nuevoValor7;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor7 < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor8 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor7));
//
//                                                                    } else if (nuevoValor7 > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor8 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    } else if (nuevoValor7 == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor8 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor7));
//
//                                                                    }
//                                                                }
//
//
//                                                                break;
//                                                            case 8:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//                                                                if (valorobtenidoSegundo < 0) {
//                                                                    nuevoValor9 = nuevoValor8;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                } else if (valorobtenidoSegundo > 0) {
//                                                                    if (nuevoValor8 < 0) {
//
//                                                                        nuevoValor9 = nuevoValor8;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor8 == 0) {
//
//                                                                        nuevoValor9 = nuevoValor8;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor8 < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor9 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor8));
//
//
//                                                                    } else if (nuevoValor8 > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor9 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                    } else if (nuevoValor8 == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor9 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor8));
//
//                                                                    }
//
//                                                                }
//
//
//                                                                break;
//                                                            case 9:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//
//                                                                if (valorobtenidoSegundo < 0) {
//                                                                    nuevoValor10 = nuevoValor9;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                } else if (valorobtenidoSegundo > 0) {
//                                                                    if (nuevoValor9 < 0) {
//
//                                                                        nuevoValor10 = nuevoValor9;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor9 == 0) {
//
//                                                                        nuevoValor10 = nuevoValor9;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor9 < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor10 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor9));
//
//
//                                                                    } else if (nuevoValor9 > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor10 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                    } else if (nuevoValor9 == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor10 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor9));
//
//                                                                    }
//                                                                }
//
//
//                                                                break;
//                                                            case 10:
//
//                                                                valorobtenidoSegundo = Double.parseDouble(totalFacturasTarjeta.get(i));
//
//                                                                if (valorobtenidoSegundo < 0) {
//                                                                    nuevoValor11 = nuevoValor10;
//                                                                    preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                    preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//                                                                } else if (valorobtenidoSegundo > 0) {
//                                                                    if (nuevoValor10 < 0) {
//
//                                                                        nuevoValor11 = nuevoValor10;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor10 == 0) {
//
//                                                                        nuevoValor11 = nuevoValor10;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(0));
//
//                                                                    } else if (nuevoValor10 < valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor11 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor10));
//
//                                                                    } else if (nuevoValor10 > valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor11 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                    } else if (nuevoValor10 == valorobtenidoSegundo) {
//
//                                                                        totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                        nuevoValor11 = totalbotenido;
//                                                                        preciosParcialTarjeta.add(String.valueOf(valorTarj));
//                                                                        preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor10));
//
//                                                                    }
//                                                                }
//
//
//                                                                break;
//                                                            default:
//                                                                break;
//                                                        }


                                                        PreciosFacturasParcial parcial = new PreciosFacturasParcial();
                                                        parcial.valor = Double.parseDouble(preciosParcialTarjeta.get(i));
                                                        Gson gson33 = new Gson();
                                                        String jsonStringObject = gson33.toJson(parcial);
                                                        PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                                    }


                                                } catch (Exception exception) {
                                                    System.out.println("Error en la forma de pago parcial " + exception);
                                                }
                                            }

                                            DataBaseBO.eliminarRecaudos(idesPagoTarjeta.get(k), contexto);

                                            final String idTarjetaPago = finalCodigoVendedor + Utilidades.fechaActual("ddHHmmss") + k;

                                            listaFacHchasUnaPorUna.add(idTarjetaPago);

                                            DataBaseBO.guardarFormaPagParcial(idTarjetaPago, claseDocumento, sociedad,
                                                    cod_cliente,
                                                    finalCod_Vendedor,
                                                    "0", fechasDocumentos, fecha, precios, 0,
                                                    moneda, preciosfacturasLogicaTarjeta,
                                                    preciosParcialTarjeta, cuentaBancariaTarjeta,
                                                    moneda_Consig, NCF_Comprobante_fiscal,
                                                    documentosFinanciero, consecutivo1,
                                                    descripc, "O", usuario, operacion_Cme, sincronizado, "0",
                                                    "0", nombrePropietarioTarjeta, fotoTarjeta, finalConsecutivoidEfectivo, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto);

                                        }



                                    }


                                    //EFECTIVO

                                    if (finalCountEfectivo >= 1) {

                                        final List<Facturas> listsaFacturasParcialTotalEfectivo;
                                        final List<Facturas> listsaIDEfectivo;
                                        double valorCom = 0;
                                        String idPagoEfectivo = "";
                                        String referenciaEfectivo = "";
                                        String ideFoto = "";
                                        listsaFacturasParcialTotalEfectivo = Utilidades.listaFacturasParcialTotalEfectivo(contexto);
                                        listsaIDEfectivo = Utilidades.listaFacturasIDEfectivo(contexto);

                                        for (Facturas fac : listsaFacturasParcialTotalEfectivo) {
                                            valorCom = fac.valor;
                                            valoresfacturasEfectivo.add(String.valueOf(Utilidades.formatearDecimales(valorCom, 2)));

                                        }

                                        for (Facturas fac : listsaIDEfectivo) {
                                            referenciaEfectivo = fac.referencia;
                                            idPagoEfectivo = fac.idPago;
                                            ideFoto = fac.idenFoto;

                                        }


                                        inputEfect = String.valueOf(valorEfec);

                                        if (listsaFacturasParcialTotal.size() > 0) {
                                            try {


                                                for (int i = 0; i < precios.size(); i++) {


                                                    PreciosFacturasParcial preciosFacturasParcial = new PreciosFacturasParcial();
                                                    double valorobtenido = Double.parseDouble(precios.get(i));
                                                    double valorobtenidoSegundo = 0;
                                                    preciosFacturasParcial.valorobtenido = valorobtenido;
                                                    preciosFacturasParcial.valor = valor;
                                                    String acert = "";
                                                    double valorTotalParcial = 0;

                                                    double valorObtenidoFac = 0;
                                                    String totalesValoresLista = "";

                                                    if(i == 0)
                                                    {
                                                        if (listsaFacturasParcialTotal.size() > 0) {
                                                            if (preciosfacturasLogica.size() > 0) {
                                                                for (int j = 0; j < preciosfacturasLogica.size(); j++) {


                                                                    if (precios.get(j).equals(preciosfacturasLogica.get(j))) {

                                                                        if (listsaFacturasParcialTotal.size() > 0) {
                                                                            listsaFacturasParcialTotal.remove(j);
                                                                            preciosfacturasLogica.remove(j);

                                                                            if (finalCountTarjeta >= 2) {

                                                                                if (precios.get(j).equals(preciosfacturasLogicaTarjeta.get(j))) {
                                                                                    preciosfacturasLogicaTarjeta.remove(j);
                                                                                }

                                                                            }
                                                                            precios.remove(j);
                                                                        }

                                                                        documentosFinanciero.remove(j);
                                                                        claseDocumento.remove(j);
                                                                        fechasDocumentos.remove(j);
                                                                    }
                                                                }
                                                            }
                                                        }


                                                        for (int j = 0; j < preciosfacturasLogica.size(); j++) {
                                                            double valorLista = 0;

                                                            if (finalCountTarjeta >= 2) {
                                                                valorLista = Double.parseDouble(preciosfacturasLogicaTarjeta.get(j));
                                                            }

                                                            double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
                                                            double valorLista3 = Double.parseDouble(precios.get(j));
                                                            double valorLista4 = valorLista2 + valorLista;
                                                            totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista3 - valorLista4, 2));
                                                            totalFacturasEfectivo.add(totalesValoresLista);


                                                        }


                                                        valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));


                                                        if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                                                                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                                                            if (!inputEfect.isEmpty()) {
                                                                if (inputEfect.contains(".") && inputEfect.contains(",")) {

                                                                    inputEfect = inputEfect.replace(".", "");
                                                                    inputEfect = inputEfect.replace(",", ".");
                                                                    valorEfec = Double.parseDouble(inputEfect);

                                                                } else if (inputEfect.contains(",")) {

                                                                    inputEfect = inputEfect.replace(",", ".");
                                                                    valorEfec = Double.parseDouble(inputEfect);

                                                                } else if (inputEfect.contains(".")) {


                                                                    valorEfec = Double.parseDouble(inputEfect);

                                                                } else if (!inputEfect.contains(".") && !inputEfect.contains(",")) {
                                                                    valorEfec = Double.parseDouble(inputEfect);
                                                                }
                                                            }

                                                        } else if (finalEmpresa.equals("AGCO")) {

                                                            if (!inputEfect.isEmpty()) {

                                                                if (inputEfect.contains(".")) {

                                                                    inputEfect = inputEfect.replace(".", "");
                                                                    valorEfec = Double.parseDouble(inputEfect);


                                                                } else if (!inputEfect.contains(".") && !inputEfect.contains(",")) {
                                                                    valorEfec = Double.parseDouble(inputEfect);
                                                                }
                                                            }

                                                        } else {

                                                            if (!inputEfect.isEmpty()) {

                                                                if (inputEfect.contains(",")) {

                                                                    inputEfect = inputEfect.replace(",", "");
                                                                    valorEfec = Double.parseDouble(inputEfect);

                                                                } else if (inputEfect.contains(".")) {

                                                                    valorEfec = Double.parseDouble(inputEfect);


                                                                } else if (!inputEfect.contains(".") && !inputEfect.contains(",")) {
                                                                    valorEfec = Double.parseDouble(inputEfect);
                                                                }
                                                            }
                                                        }

                                                        if (valorobtenidoSegundo < 0) {

                                                            nuevoValor = valorEfec;
                                                            preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                            preciosfacturasLogicaEfectivo.add(String.valueOf(0));

                                                        } else if (valorobtenidoSegundo > 0) {

                                                            if (valorEfec < 0) {

                                                                nuevoValor = valorEfec;
                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));

                                                            } else if (valorEfec == 0) {

                                                                nuevoValor = valorEfec;
                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));

                                                            } else if (valorEfec < valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(valorEfec - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(valorEfec));

                                                            } else if (valorEfec > valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(valorEfec - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));

                                                            } else if (valorEfec == valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(valorEfec - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));

                                                            }

                                                        } else if (valorobtenidoSegundo == 0) {

                                                            nuevoValor = valorEfec;
                                                            preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                            preciosfacturasLogicaEfectivo.add(String.valueOf(0));

                                                        }
                                                    }
                                                    else
                                                    {
                                                        valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));


                                                        if (valorobtenidoSegundo < 0) {

                                                            preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                            preciosfacturasLogicaEfectivo.add(String.valueOf(0));

                                                        } else if (valorobtenidoSegundo > 0) {

                                                            if (nuevoValor < 0) {

                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));

                                                            } else if (nuevoValor == 0) {

                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));

                                                            } else if (nuevoValor < valorobtenidoSegundo) {
                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor));
                                                                nuevoValor = totalbotenido;

                                                            } else if (nuevoValor > valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));


                                                            } else if (nuevoValor == valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));

                                                            }

                                                        } else if (valorobtenidoSegundo == 0) {

                                                            nuevoValor = nuevoValor;
                                                            preciosParcialEfectivo.add(String.valueOf(valorEfec));
                                                            preciosfacturasLogicaEfectivo.add(String.valueOf(0));

                                                        }
                                                    }


//                                                    switch (i) {
//
//                                                        case 0:
//
//
//                                                            if (listsaFacturasParcialTotal.size() > 0) {
//                                                                if (preciosfacturasLogica.size() > 0) {
//                                                                    for (int j = 0; j < preciosfacturasLogica.size(); j++) {
//
//
//                                                                        if (precios.get(j).equals(preciosfacturasLogica.get(j))) {
//
//                                                                            if (listsaFacturasParcialTotal.size() > 0) {
//                                                                                listsaFacturasParcialTotal.remove(j);
//                                                                                preciosfacturasLogica.remove(j);
//
//                                                                                if (finalCountTarjeta >= 2) {
//
//                                                                                    if (precios.get(j).equals(preciosfacturasLogicaTarjeta.get(j))) {
//                                                                                        preciosfacturasLogicaTarjeta.remove(j);
//                                                                                    }
//
//                                                                                }
//                                                                                precios.remove(j);
//                                                                            }
//
//                                                                            documentosFinanciero.remove(j);
//                                                                            claseDocumento.remove(j);
//                                                                            fechasDocumentos.remove(j);
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//
//
//                                                            for (int j = 0; j < preciosfacturasLogica.size(); j++) {
//                                                                double valorLista = 0;
//
//                                                                if (finalCountTarjeta >= 2) {
//                                                                    valorLista = Double.parseDouble(preciosfacturasLogicaTarjeta.get(j));
//                                                                }
//
//                                                                double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
//                                                                double valorLista3 = Double.parseDouble(precios.get(j));
//                                                                double valorLista4 = valorLista2 + valorLista;
//                                                                totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista3 - valorLista4, 2));
//                                                                totalFacturasEfectivo.add(totalesValoresLista);
//
//
//                                                            }
//
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//
//                                                            if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
//                                                                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {
//
//                                                                if (!inputEfect.isEmpty()) {
//                                                                    if (inputEfect.contains(".") && inputEfect.contains(",")) {
//
//                                                                        inputEfect = inputEfect.replace(".", "");
//                                                                        inputEfect = inputEfect.replace(",", ".");
//                                                                        valorEfec = Double.parseDouble(inputEfect);
//
//                                                                    } else if (inputEfect.contains(",")) {
//
//                                                                        inputEfect = inputEfect.replace(",", ".");
//                                                                        valorEfec = Double.parseDouble(inputEfect);
//
//                                                                    } else if (inputEfect.contains(".")) {
//
//
//                                                                        valorEfec = Double.parseDouble(inputEfect);
//
//                                                                    } else if (!inputEfect.contains(".") && !inputEfect.contains(",")) {
//                                                                        valorEfec = Double.parseDouble(inputEfect);
//                                                                    }
//                                                                }
//
//                                                            } else if (finalEmpresa.equals("AGCO")) {
//
//                                                                if (!inputEfect.isEmpty()) {
//
//                                                                    if (inputEfect.contains(".")) {
//
//                                                                        inputEfect = inputEfect.replace(".", "");
//                                                                        valorEfec = Double.parseDouble(inputEfect);
//
//
//                                                                    } else if (!inputEfect.contains(".") && !inputEfect.contains(",")) {
//                                                                        valorEfec = Double.parseDouble(inputEfect);
//                                                                    }
//                                                                }
//
//                                                            } else {
//
//                                                                if (!inputEfect.isEmpty()) {
//
//                                                                    if (inputEfect.contains(",")) {
//
//                                                                        inputEfect = inputEfect.replace(",", "");
//                                                                        valorEfec = Double.parseDouble(inputEfect);
//
//                                                                    } else if (inputEfect.contains(".")) {
//
//                                                                        valorEfec = Double.parseDouble(inputEfect);
//
//
//                                                                    } else if (!inputEfect.contains(".") && !inputEfect.contains(",")) {
//                                                                        valorEfec = Double.parseDouble(inputEfect);
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            if (valorobtenidoSegundo < 0) {
//
//                                                                nuevoValor = valorEfec;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                                if (valorEfec < 0) {
//
//                                                                    nuevoValor = valorEfec;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (valorEfec == 0) {
//
//                                                                    nuevoValor = valorEfec;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (valorEfec < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(valorEfec - valorobtenidoSegundo, 2);
//                                                                    nuevoValor = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorEfec));
//
//                                                                } else if (valorEfec > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(valorEfec - valorobtenidoSegundo, 2);
//                                                                    nuevoValor = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (valorEfec == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(valorEfec - valorobtenidoSegundo, 2);
//                                                                    nuevoValor = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                }
//
//                                                            } else if (valorobtenidoSegundo == 0) {
//
//                                                                nuevoValor = valorEfec;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                            }
//
//                                                            break;
//
//                                                        case 1:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//
//                                                                nuevoValor2 = nuevoValor;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                                if (nuevoValor < 0) {
//
//                                                                    nuevoValor2 = nuevoValor;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor == 0) {
//
//                                                                    nuevoValor2 = nuevoValor;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor < valorobtenidoSegundo) {
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                    nuevoValor2 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor));
//
//
//                                                                } else if (nuevoValor > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                    nuevoValor2 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                } else if (nuevoValor == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                    nuevoValor2 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                }
//
//                                                            } else if (valorobtenidoSegundo == 0) {
//
//                                                                nuevoValor2 = nuevoValor;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                            }
//
//                                                            break;
//                                                        case 2:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor3 = nuevoValor2;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor2 < 0) {
//
//                                                                    nuevoValor3 = nuevoValor2;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor2 == 0) {
//
//                                                                    nuevoValor3 = nuevoValor2;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor2 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor3 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor2));
//
//                                                                } else if (nuevoValor2 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor3 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor2 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor3 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor2));
//
//                                                                }
//                                                            } else if (valorobtenidoSegundo == 0) {
//                                                                nuevoValor3 = nuevoValor2;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            }
//
//
//                                                            break;
//                                                        case 3:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor4 = nuevoValor3;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor3 < 0) {
//
//                                                                    nuevoValor4 = nuevoValor3;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor3 == 0) {
//
//                                                                    nuevoValor4 = nuevoValor3;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor3 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor4 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor3));
//
//                                                                } else if (nuevoValor3 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor4 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor3 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor4 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor3));
//
//                                                                }
//                                                            }
//
//                                                            break;
//                                                        case 4:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor5 = nuevoValor4;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor4 < 0) {
//
//                                                                    nuevoValor5 = nuevoValor4;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor4 == 0) {
//
//                                                                    nuevoValor5 = nuevoValor4;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor4 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor5 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor4));
//
//                                                                } else if (nuevoValor4 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor5 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor4 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor5 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor4));
//
//                                                                }
//                                                            }
//
//                                                            break;
//                                                        case 5:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor6 = nuevoValor5;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor5 < 0) {
//
//                                                                    nuevoValor6 = nuevoValor5;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor5 == 0) {
//
//                                                                    nuevoValor6 = nuevoValor5;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor5 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor6 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor5));
//
//                                                                } else if (nuevoValor5 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor6 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor5 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor6 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor5));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        case 6:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor7 = nuevoValor6;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor6 < 0) {
//
//                                                                    nuevoValor7 = nuevoValor6;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor6 == 0) {
//
//                                                                    nuevoValor7 = nuevoValor6;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor6 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor7 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor6));
//
//                                                                } else if (nuevoValor6 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor7 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor6 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor7 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor6));
//
//                                                                }
//                                                            }
//
//                                                            break;
//                                                        case 7:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor8 = nuevoValor7;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor7 < 0) {
//
//                                                                    nuevoValor8 = nuevoValor7;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor7 == 0) {
//
//                                                                    nuevoValor8 = nuevoValor7;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor7 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor8 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor7));
//
//                                                                } else if (nuevoValor7 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor8 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor7 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor8 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor7));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        case 8:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor9 = nuevoValor8;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor8 < 0) {
//
//                                                                    nuevoValor9 = nuevoValor8;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor8 == 0) {
//
//                                                                    nuevoValor9 = nuevoValor8;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor8 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor9 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor8));
//
//
//                                                                } else if (nuevoValor8 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor9 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                } else if (nuevoValor8 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor9 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor8));
//
//                                                                }
//
//                                                            }
//
//
//                                                            break;
//                                                        case 9:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor10 = nuevoValor9;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor9 < 0) {
//
//                                                                    nuevoValor10 = nuevoValor9;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor9 == 0) {
//
//                                                                    nuevoValor10 = nuevoValor9;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor9 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor10 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor9));
//
//
//                                                                } else if (nuevoValor9 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor10 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                } else if (nuevoValor9 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor10 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor9));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        case 10:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasEfectivo.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor11 = nuevoValor10;
//                                                                preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor10 < 0) {
//
//                                                                    nuevoValor11 = nuevoValor10;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor10 == 0) {
//
//                                                                    nuevoValor11 = nuevoValor10;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor10 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor11 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor10));
//
//                                                                } else if (nuevoValor10 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor11 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor10 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor11 = totalbotenido;
//                                                                    preciosParcialEfectivo.add(String.valueOf(valorEfec));
//                                                                    preciosfacturasLogicaEfectivo.add(String.valueOf(nuevoValor10));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        default:
//                                                            break;
//                                                    }


                                                    PreciosFacturasParcial parcial = new PreciosFacturasParcial();
                                                    parcial.valor = Double.parseDouble(preciosParcialEfectivo.get(i));
                                                    Gson gson33 = new Gson();
                                                    String jsonStringObject = gson33.toJson(parcial);
                                                    PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                                }


                                            } catch (Exception exception) {
                                                System.out.println("Error en la forma de pago parcial " + exception);
                                            }
                                        }

                                        System.out.println("id " + idPagoEfectivo);

                                        DataBaseBO.eliminarRecaudosPendientes(idPagoEfectivo, contexto);

                                        DataBaseBO.guardarFormaPagParcialPendiente(idEfectivoPago, claseDocumento,
                                                sociedad, cod_cliente, finalCod_Vendedor,
                                                referenciaEfectivo, fechasDocumentos,
                                                fecha, precios,
                                                moneda, preciosfacturasLogicaEfectivo, preciosParcialEfectivo, 0, cuentasBanco,
                                                moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                                consecutivo1,
                                                descripc, "A", usuario, operacion_Cme,
                                                sincronizado, spinBanco, "0",
                                                "0", ideFoto, finalConsecutivoidCheque, finalconsec1Id, formaPago != null ? formaPago.observacionesMotivo : "", contexto);


                                    }


                                    //CHEQUE

                                    if (finalCountCheque >= 1) {

                                        final List<Facturas> listsaFacturasParcialTotalCheq;
                                        final List<Facturas> listsaIDCheq;
                                        String idPagoCheque = "";
                                        String referenciaCheque = "";
                                        String numeroCheque = "";
                                        String ideFotoCheq = "";


                                        listsaFacturasParcialTotalCheq = Utilidades.listaFacturasParcialTotalCheq(contexto);
                                        listsaIDCheq = Utilidades.listaFacturasIDCheq(contexto);


                                        for (Facturas fac : listsaIDCheq) {

                                            referenciaCheque = fac.referencia;
                                            numeroCheque = fac.numeroCheque;
                                            idPagoCheque = fac.idPago;
                                            ideFotoCheq = fac.idenFoto;

                                        }

                                        inputCheque = String.valueOf(valorCheque);

                                        if (listsaFacturasParcialTotal.size() > 0) {
                                            try {


                                                for (int i = 0; i < precios.size(); i++) {


                                                    PreciosFacturasParcial preciosFacturasParcial = new PreciosFacturasParcial();
                                                    double valorobtenido = Double.parseDouble(precios.get(i));
                                                    double valorobtenidoSegundo = 0;
                                                    preciosFacturasParcial.valorobtenido = valorobtenido;
                                                    preciosFacturasParcial.valor = valor;
                                                    String acert = "";
                                                    double valorTotalParcial = 0;

                                                    double valorObtenidoFac = 0;
                                                    String totalesValoresLista = "";

                                                    if(i == 0)
                                                    {
                                                        if (listsaFacturasParcialTotal.size() > 0) {
                                                            if (preciosfacturasLogicaEfectivo.size() > 0) {
                                                                for (int j = 0; j < preciosfacturasLogicaEfectivo.size(); j++) {


                                                                    if (precios.get(j).equals(preciosfacturasLogicaEfectivo.get(j))) {

                                                                        if (listsaFacturasParcialTotal.size() > 0) {
                                                                            listsaFacturasParcialTotal.remove(j);
                                                                            preciosfacturasLogicaEfectivo.remove(j);
                                                                            precios.remove(j);
                                                                        }

                                                                        documentosFinanciero.remove(j);
                                                                        claseDocumento.remove(j);
                                                                        fechasDocumentos.remove(j);
                                                                    }
                                                                }
                                                            }
                                                        }


                                                        for (int j = 0; j < preciosfacturasLogica.size(); j++) {
                                                            double valorLista = 0;

                                                            if (finalCountTarjeta >= 2) {
                                                                valorLista = Double.parseDouble(preciosfacturasLogicaTarjeta.get(j));
                                                            }
                                                            double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
                                                            double valorLista3 = Double.parseDouble(preciosfacturasLogicaEfectivo.get(j));
                                                            double valorLista5 = Double.parseDouble(precios.get(j));
                                                            double valorLista4 = valorLista2 + valorLista + valorLista3;
                                                            totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista5 - valorLista4, 2));
                                                            totalFacturasCheque.add(totalesValoresLista);

                                                        }

                                                        /**else{
                                                         if (listsaFacturasParcialTotal.size() > 0) {
                                                         if (preciosfacturasLogica.size() > 0) {
                                                         for (int j = 0; j < preciosfacturasLogicaEfectivo.size(); j++) {


                                                         if (precios.get(j).equals(preciosfacturasLogica.get(j))) {

                                                         if (listsaFacturasParcialTotal.size() > 0) {
                                                         listsaFacturasParcialTotal.remove(j);
                                                         preciosfacturasLogica.remove(j);
                                                         precios.remove(j);
                                                         }

                                                         documentosFinanciero.remove(j);
                                                         claseDocumento.remove(j);
                                                         fechasDocumentos.remove(j);
                                                         }
                                                         }
                                                         }
                                                         }


                                                         for (int j = 0; j < preciosfacturasLogica.size(); j++) {
                                                         double valorLista = 0;

                                                         valorLista = Double.parseDouble(preciosfacturasLogica.get(j));


                                                         double valorLista2 = Double.parseDouble(precios.get(j));
                                                         totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2 - valorLista, 2));
                                                         totalFacturasCheque.add(totalesValoresLista);


                                                         }

                                                         }**/


                                                        valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));


                                                        if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                                                                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                                                            if (!inputCheque.isEmpty()) {
                                                                if (inputCheque.contains(".") && inputCheque.contains(",")) {

                                                                    inputCheque = inputCheque.replace(".", "");
                                                                    inputCheque = inputCheque.replace(",", ".");
                                                                    valorCheque = Double.parseDouble(inputCheque);

                                                                } else if (inputCheque.contains(",")) {

                                                                    inputCheque = inputCheque.replace(",", ".");
                                                                    valorCheque = Double.parseDouble(inputCheque);

                                                                } else if (inputCheque.contains(".")) {


                                                                    valorCheque = Double.parseDouble(inputCheque);

                                                                } else if (!inputCheque.contains(".") && !inputCheque.contains(",")) {
                                                                    valorCheque = Double.parseDouble(inputCheque);
                                                                }
                                                            }

                                                        } else if (finalEmpresa.equals("AGCO")) {

                                                            if (!inputCheque.isEmpty()) {

                                                                if (inputCheque.contains(".")) {

                                                                    inputCheque = inputCheque.replace(".", "");
                                                                    valorCheque = Double.parseDouble(inputCheque);


                                                                } else if (!inputCheque.contains(".") && !inputCheque.contains(",")) {
                                                                    valorCheque = Double.parseDouble(inputCheque);
                                                                }
                                                            }

                                                        } else {

                                                            if (!inputCheque.isEmpty()) {

                                                                if (inputCheque.contains(",")) {

                                                                    inputCheque = inputCheque.replace(",", "");
                                                                    valorCheque = Double.parseDouble(inputCheque);

                                                                } else if (inputCheque.contains(".")) {

                                                                    valorCheque = Double.parseDouble(inputCheque);


                                                                } else if (!inputCheque.contains(".") && !inputCheque.contains(",")) {
                                                                    valorCheque = Double.parseDouble(inputCheque);
                                                                }
                                                            }
                                                        }

                                                        if (valorobtenidoSegundo < 0) {

                                                            nuevoValor = valorCheque;
                                                            preciosParcialCheque.add(String.valueOf(valorCheque));
                                                            preciosfacturasLogicaCheque.add(String.valueOf(0));

                                                        } else if (valorobtenidoSegundo > 0) {

                                                            if (valorCheque < 0) {

                                                                nuevoValor = valorCheque;
                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));

                                                            } else if (valorCheque == 0) {

                                                                nuevoValor = valorCheque;
                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));

                                                            } else if (valorCheque < valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(valorCheque - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(valorCheque));

                                                            } else if (valorCheque > valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(valorCheque - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));

                                                            } else if (valorCheque == valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(valorCheque - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));

                                                            }

                                                        } else if (valorobtenidoSegundo == 0) {

                                                            nuevoValor = valorCheque;
                                                            preciosParcialCheque.add(String.valueOf(valorCheque));
                                                            preciosfacturasLogicaCheque.add(String.valueOf(0));

                                                        }
                                                    }
                                                    else
                                                    {
                                                        valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));


                                                        if (valorobtenidoSegundo < 0) {

                                                            preciosParcialCheque.add(String.valueOf(valorCheque));
                                                            preciosfacturasLogicaCheque.add(String.valueOf(0));

                                                        } else if (valorobtenidoSegundo > 0) {

                                                            if (nuevoValor < 0) {

                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));

                                                            } else if (nuevoValor == 0) {

                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));

                                                            } else if (nuevoValor < valorobtenidoSegundo) {
                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor));
                                                                nuevoValor = totalbotenido;

                                                            } else if (nuevoValor > valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));


                                                            } else if (nuevoValor == valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
                                                                preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));

                                                            }

                                                        } else if (valorobtenidoSegundo == 0) {

                                                            nuevoValor = nuevoValor;
                                                            preciosParcialCheque.add(String.valueOf(valorCheque));
                                                            preciosfacturasLogicaCheque.add(String.valueOf(0));

                                                        }
                                                    }


//                                                    switch (i) {
//
//                                                        case 0:
//
//
//                                                            if (listsaFacturasParcialTotal.size() > 0) {
//                                                                if (preciosfacturasLogicaEfectivo.size() > 0) {
//                                                                    for (int j = 0; j < preciosfacturasLogicaEfectivo.size(); j++) {
//
//
//                                                                        if (precios.get(j).equals(preciosfacturasLogicaEfectivo.get(j))) {
//
//                                                                            if (listsaFacturasParcialTotal.size() > 0) {
//                                                                                listsaFacturasParcialTotal.remove(j);
//                                                                                preciosfacturasLogicaEfectivo.remove(j);
//                                                                                precios.remove(j);
//                                                                            }
//
//                                                                            documentosFinanciero.remove(j);
//                                                                            claseDocumento.remove(j);
//                                                                            fechasDocumentos.remove(j);
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//
//
//                                                            for (int j = 0; j < preciosfacturasLogica.size(); j++) {
//                                                                double valorLista = 0;
//
//                                                                if (finalCountTarjeta >= 2) {
//                                                                    valorLista = Double.parseDouble(preciosfacturasLogicaTarjeta.get(j));
//                                                                }
//                                                                double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
//                                                                double valorLista3 = Double.parseDouble(preciosfacturasLogicaEfectivo.get(j));
//                                                                double valorLista5 = Double.parseDouble(precios.get(j));
//                                                                double valorLista4 = valorLista2 + valorLista + valorLista3;
//                                                                totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista5 - valorLista4, 2));
//                                                                totalFacturasCheque.add(totalesValoresLista);
//
//                                                            }
//
//                                                            /**else{
//                                                             if (listsaFacturasParcialTotal.size() > 0) {
//                                                             if (preciosfacturasLogica.size() > 0) {
//                                                             for (int j = 0; j < preciosfacturasLogicaEfectivo.size(); j++) {
//
//
//                                                             if (precios.get(j).equals(preciosfacturasLogica.get(j))) {
//
//                                                             if (listsaFacturasParcialTotal.size() > 0) {
//                                                             listsaFacturasParcialTotal.remove(j);
//                                                             preciosfacturasLogica.remove(j);
//                                                             precios.remove(j);
//                                                             }
//
//                                                             documentosFinanciero.remove(j);
//                                                             claseDocumento.remove(j);
//                                                             fechasDocumentos.remove(j);
//                                                             }
//                                                             }
//                                                             }
//                                                             }
//
//
//                                                             for (int j = 0; j < preciosfacturasLogica.size(); j++) {
//                                                             double valorLista = 0;
//
//                                                             valorLista = Double.parseDouble(preciosfacturasLogica.get(j));
//
//
//                                                             double valorLista2 = Double.parseDouble(precios.get(j));
//                                                             totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2 - valorLista, 2));
//                                                             totalFacturasCheque.add(totalesValoresLista);
//
//
//                                                             }
//
//                                                             }**/
//
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
//                                                                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {
//
//                                                                if (!inputCheque.isEmpty()) {
//                                                                    if (inputCheque.contains(".") && inputCheque.contains(",")) {
//
//                                                                        inputCheque = inputCheque.replace(".", "");
//                                                                        inputCheque = inputCheque.replace(",", ".");
//                                                                        valorCheque = Double.parseDouble(inputCheque);
//
//                                                                    } else if (inputCheque.contains(",")) {
//
//                                                                        inputCheque = inputCheque.replace(",", ".");
//                                                                        valorCheque = Double.parseDouble(inputCheque);
//
//                                                                    } else if (inputCheque.contains(".")) {
//
//
//                                                                        valorCheque = Double.parseDouble(inputCheque);
//
//                                                                    } else if (!inputCheque.contains(".") && !inputCheque.contains(",")) {
//                                                                        valorCheque = Double.parseDouble(inputCheque);
//                                                                    }
//                                                                }
//
//                                                            } else if (finalEmpresa.equals("AGCO")) {
//
//                                                                if (!inputCheque.isEmpty()) {
//
//                                                                    if (inputCheque.contains(".")) {
//
//                                                                        inputCheque = inputCheque.replace(".", "");
//                                                                        valorCheque = Double.parseDouble(inputCheque);
//
//
//                                                                    } else if (!inputCheque.contains(".") && !inputCheque.contains(",")) {
//                                                                        valorCheque = Double.parseDouble(inputCheque);
//                                                                    }
//                                                                }
//
//                                                            } else {
//
//                                                                if (!inputCheque.isEmpty()) {
//
//                                                                    if (inputCheque.contains(",")) {
//
//                                                                        inputCheque = inputCheque.replace(",", "");
//                                                                        valorCheque = Double.parseDouble(inputCheque);
//
//                                                                    } else if (inputCheque.contains(".")) {
//
//                                                                        valorCheque = Double.parseDouble(inputCheque);
//
//
//                                                                    } else if (!inputCheque.contains(".") && !inputCheque.contains(",")) {
//                                                                        valorCheque = Double.parseDouble(inputCheque);
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            if (valorobtenidoSegundo < 0) {
//
//                                                                nuevoValor = valorCheque;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                                if (valorCheque < 0) {
//
//                                                                    nuevoValor = valorCheque;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (valorCheque == 0) {
//
//                                                                    nuevoValor = valorCheque;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (valorCheque < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(valorCheque - valorobtenidoSegundo, 2);
//                                                                    nuevoValor = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorCheque));
//
//                                                                } else if (valorCheque > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(valorCheque - valorobtenidoSegundo, 2);
//                                                                    nuevoValor = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (valorCheque == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(valorCheque - valorobtenidoSegundo, 2);
//                                                                    nuevoValor = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                }
//
//                                                            } else if (valorobtenidoSegundo == 0) {
//
//                                                                nuevoValor = valorCheque;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                            }
//
//                                                            break;
//
//                                                        case 1:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//
//                                                                nuevoValor2 = nuevoValor;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                                if (nuevoValor < 0) {
//
//                                                                    nuevoValor2 = nuevoValor;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor == 0) {
//
//                                                                    nuevoValor2 = nuevoValor;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor < valorobtenidoSegundo) {
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                    nuevoValor2 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor));
//
//
//                                                                } else if (nuevoValor > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                    nuevoValor2 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                } else if (nuevoValor == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                    nuevoValor2 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                }
//
//                                                            } else if (valorobtenidoSegundo == 0) {
//
//                                                                nuevoValor2 = nuevoValor;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                            }
//
//                                                            break;
//                                                        case 2:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor3 = nuevoValor2;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor2 < 0) {
//
//                                                                    nuevoValor3 = nuevoValor2;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor2 == 0) {
//
//                                                                    nuevoValor3 = nuevoValor2;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor2 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor3 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor2));
//
//                                                                } else if (nuevoValor2 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor3 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor2 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor3 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor2));
//
//                                                                }
//                                                            } else if (valorobtenidoSegundo == 0) {
//                                                                nuevoValor3 = nuevoValor2;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            }
//
//
//                                                            break;
//                                                        case 3:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor4 = nuevoValor3;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor3 < 0) {
//
//                                                                    nuevoValor4 = nuevoValor3;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor3 == 0) {
//
//                                                                    nuevoValor4 = nuevoValor3;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor3 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor4 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor3));
//
//                                                                } else if (nuevoValor3 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor4 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor3 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor4 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor3));
//
//                                                                }
//                                                            }
//
//                                                            break;
//                                                        case 4:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor5 = nuevoValor4;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor4 < 0) {
//
//                                                                    nuevoValor5 = nuevoValor4;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor4 == 0) {
//
//                                                                    nuevoValor5 = nuevoValor4;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor4 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor5 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor4));
//
//                                                                } else if (nuevoValor4 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor5 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor4 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor5 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor4));
//
//                                                                }
//                                                            }
//
//                                                            break;
//                                                        case 5:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor6 = nuevoValor5;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor5 < 0) {
//
//                                                                    nuevoValor6 = nuevoValor5;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor5 == 0) {
//
//                                                                    nuevoValor6 = nuevoValor5;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor5 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor6 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor5));
//
//                                                                } else if (nuevoValor5 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor6 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor5 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor6 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor5));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        case 6:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor7 = nuevoValor6;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor6 < 0) {
//
//                                                                    nuevoValor7 = nuevoValor6;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor6 == 0) {
//
//                                                                    nuevoValor7 = nuevoValor6;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor6 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor7 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor6));
//
//                                                                } else if (nuevoValor6 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor7 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor6 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor7 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor6));
//
//                                                                }
//                                                            }
//
//                                                            break;
//                                                        case 7:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor8 = nuevoValor7;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor7 < 0) {
//
//                                                                    nuevoValor8 = nuevoValor7;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor7 == 0) {
//
//                                                                    nuevoValor8 = nuevoValor7;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor7 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor8 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor7));
//
//                                                                } else if (nuevoValor7 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor8 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor7 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor8 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor7));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        case 8:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor9 = nuevoValor8;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor8 < 0) {
//
//                                                                    nuevoValor9 = nuevoValor8;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor8 == 0) {
//
//                                                                    nuevoValor9 = nuevoValor8;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor8 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor9 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor8));
//
//
//                                                                } else if (nuevoValor8 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor9 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                } else if (nuevoValor8 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor9 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor8));
//
//                                                                }
//
//                                                            }
//
//
//                                                            break;
//                                                        case 9:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor10 = nuevoValor9;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor9 < 0) {
//
//                                                                    nuevoValor10 = nuevoValor9;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor9 == 0) {
//
//                                                                    nuevoValor10 = nuevoValor9;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor9 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor10 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor9));
//
//
//                                                                } else if (nuevoValor9 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor10 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                } else if (nuevoValor9 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor10 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor9));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        case 10:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor11 = nuevoValor10;
//                                                                preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                preciosfacturasLogicaCheque.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor10 < 0) {
//
//                                                                    nuevoValor11 = nuevoValor10;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor10 == 0) {
//
//                                                                    nuevoValor11 = nuevoValor10;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor10 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor11 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor10));
//
//                                                                } else if (nuevoValor10 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor11 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor10 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor11 = totalbotenido;
//                                                                    preciosParcialCheque.add(String.valueOf(valorCheque));
//                                                                    preciosfacturasLogicaCheque.add(String.valueOf(nuevoValor10));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        default:
//                                                            break;
//                                                    }


                                                    PreciosFacturasParcial parcial = new PreciosFacturasParcial();
                                                    parcial.valor = Double.parseDouble(preciosParcialCheque.get(i));
                                                    Gson gson33 = new Gson();
                                                    String jsonStringObject = gson33.toJson(parcial);
                                                    PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                                }


                                            } catch (Exception exception) {
                                                System.out.println("Error en la forma de pago parcial Cheque.... " + exception);
                                            }
                                        }


                                        DataBaseBO.eliminarRecaudosPendientes(idPagoCheque, contexto);

                                        DataBaseBO.guardarFormaPagParcialPendiente(idChequePago, claseDocumento,
                                                sociedad, cod_cliente, finalCod_Vendedor,
                                                referenciaCheque, fechasDocumentos,
                                                fecha, precios,
                                                moneda, preciosfacturasLogicaCheque, preciosParcialCheque, 0, cuentasBanco,
                                                moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                                consecutivo1,
                                                descripc, "B", usuario, operacion_Cme,
                                                sincronizado, spinBanco, numeroCheque,
                                                "0", ideFotoCheq, finalConsecutivoidTransferencia, finalconsec1Id, formaPago != null ? formaPago.observacionesMotivo : "", contexto);


                                    }


                                    //TRANSFERENCIA
                                    if (finalCountTrasnferencia >= 1) {
                                        final List<Facturas> listsaFacturasParcialTotalTransfe;
                                        final List<Facturas> listsaIDTransfe;
                                        double valorComTranf = 0;
                                        String idPagoTransf = "";
                                        String cuentBancariaTransferencia = "";
                                        String bancoTransferencia = "";
                                        String referenciaTransferencia = "";
                                        String fotoTransf = "";

                                        listsaFacturasParcialTotalTransfe = Utilidades.listaFacturasParcialTotalTransfe(contexto);
                                        listsaIDTransfe = Utilidades.listaFacturasIDTranfe(contexto);

                                        for (Facturas fac : listsaFacturasParcialTotalTransfe) {
                                            valorComTranf = fac.valor;
                                            valoresfacturasTransfe.add(String.valueOf(Utilidades.formatearDecimales(valorComTranf, 2)));

                                        }
                                        for (Facturas fac : listsaIDTransfe) {
                                            cuentBancariaTransferencia = fac.cuentaBancaria;
                                            bancoTransferencia = fac.banco;
                                            referenciaTransferencia = fac.referencia;
                                            idPagoTransf = fac.idPago;
                                            fotoTransf = fac.idenFoto;

                                        }

                                        inputTransf = String.valueOf(valorTransf);

                                        if (listsaFacturasParcialTotal.size() > 0) {
                                            try {


                                                for (int i = 0; i < precios.size(); i++) {


                                                    PreciosFacturasParcial preciosFacturasParcial = new PreciosFacturasParcial();
                                                    double valorobtenido = Double.parseDouble(precios.get(i));
                                                    double valorobtenidoSegundo = 0;
                                                    preciosFacturasParcial.valorobtenido = valorobtenido;
                                                    preciosFacturasParcial.valor = valor;
                                                    String acert = "";
                                                    double valorTotalParcial = 0;

                                                    double valorObtenidoFac = 0;
                                                    String totalesValoresLista = "";

                                                    if(i == 0)
                                                    {
                                                        if (listsaFacturasParcialTotal.size() > 0) {
                                                            if (preciosfacturasLogicaCheque.size() > 0) {
                                                                for (int j = 0; j < preciosfacturasLogicaCheque.size(); j++) {

                                                                    if (precios.get(j).equals(preciosfacturasLogicaCheque.get(j))) {

                                                                        if (listsaFacturasParcialTotal.size() > 0) {
                                                                            listsaFacturasParcialTotal.remove(j);
                                                                            preciosfacturasLogicaCheque.remove(j);
                                                                            precios.remove(j);
                                                                        }

                                                                        documentosFinanciero.remove(j);
                                                                        claseDocumento.remove(j);
                                                                        fechasDocumentos.remove(j);
                                                                    }
                                                                }
                                                            }
                                                        }


                                                        for (int j = 0; j < preciosfacturasLogicaCheque.size(); j++) {
                                                            double valorLista = 0;

                                                            if (finalCountTarjeta >= 2) {
                                                                valorLista = Double.parseDouble(preciosfacturasLogicaTarjeta.get(j));
                                                            }

                                                            double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
                                                            double valorLista3 = Double.parseDouble(preciosfacturasLogicaEfectivo.get(j));
                                                            double valorLista6 = Double.parseDouble(preciosfacturasLogicaCheque.get(j));
                                                            double valorLista5 = Double.parseDouble(precios.get(j));
                                                            double valorLista4 = valorLista2 + valorLista + valorLista3 + valorLista6;
                                                            totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista5 - valorLista4, 2));
                                                            totalFacturasTransfe.add(totalesValoresLista);


                                                        }


                                                        valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));


                                                        if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                                                                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

                                                            if (!inputTransf.isEmpty()) {
                                                                if (inputTransf.contains(".") && inputTransf.contains(",")) {

                                                                    inputTransf = inputTransf.replace(".", "");
                                                                    inputTransf = inputTransf.replace(",", ".");
                                                                    valorTransf = Double.parseDouble(inputTransf);

                                                                } else if (inputTransf.contains(",")) {

                                                                    inputTransf = inputTransf.replace(",", ".");
                                                                    valorTransf = Double.parseDouble(inputTransf);

                                                                } else if (inputTransf.contains(".")) {


                                                                    valorTransf = Double.parseDouble(inputTransf);

                                                                } else if (!inputTransf.contains(".") && !inputTransf.contains(",")) {
                                                                    valorTransf = Double.parseDouble(inputTransf);
                                                                }
                                                            }

                                                        } else if (finalEmpresa.equals("AGCO")) {

                                                            if (!inputTransf.isEmpty()) {

                                                                if (inputTransf.contains(".")) {

                                                                    inputTransf = inputTransf.replace(".", "");
                                                                    valorTransf = Double.parseDouble(inputTransf);


                                                                } else if (!inputTransf.contains(".") && !inputTransf.contains(",")) {
                                                                    valorTransf = Double.parseDouble(inputTransf);
                                                                }
                                                            }

                                                        } else {

                                                            if (!inputTransf.isEmpty()) {

                                                                if (inputTransf.contains(",")) {

                                                                    inputTransf = inputTransf.replace(",", "");
                                                                    valorTransf = Double.parseDouble(inputTransf);

                                                                } else if (inputTransf.contains(".")) {

                                                                    valorTransf = Double.parseDouble(inputTransf);


                                                                } else if (!inputTransf.contains(".") && !inputTransf.contains(",")) {
                                                                    valorTransf = Double.parseDouble(inputTransf);
                                                                }
                                                            }
                                                        }

                                                        if (valorobtenidoSegundo < 0) {

                                                            nuevoValor = valorTransf;
                                                            preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                            preciosfacturasLogicaTransfe.add(String.valueOf(0));

                                                        } else if (valorobtenidoSegundo > 0) {

                                                            if (valorTransf < 0) {

                                                                nuevoValor = valorTransf;
                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));

                                                            } else if (valorTransf == 0) {

                                                                nuevoValor = valorTransf;
                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));

                                                            } else if (valorTransf < valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(valorTransf - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(valorTransf));

                                                            } else if (valorTransf > valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(valorTransf - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));

                                                            } else if (valorTransf == valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(valorTransf - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));

                                                            }

                                                        } else if (valorobtenidoSegundo == 0) {

                                                            nuevoValor = valorTransf;
                                                            preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                            preciosfacturasLogicaTransfe.add(String.valueOf(0));

                                                        }
                                                    }
                                                    else
                                                    {
                                                        valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));


                                                        if (valorobtenidoSegundo < 0) {

                                                            preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                            preciosfacturasLogicaTransfe.add(String.valueOf(0));

                                                        } else if (valorobtenidoSegundo > 0) {

                                                            if (nuevoValor < 0) {

                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));

                                                            } else if (nuevoValor == 0) {

                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));

                                                            } else if (nuevoValor < valorobtenidoSegundo) {
                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor));
                                                                nuevoValor = totalbotenido;

                                                            } else if (nuevoValor > valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));


                                                            } else if (nuevoValor == valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                                preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));

                                                            }

                                                        } else if (valorobtenidoSegundo == 0) {

                                                            nuevoValor = nuevoValor;
                                                            preciosParcialTrasnf.add(String.valueOf(valorTransf));
                                                            preciosfacturasLogicaTransfe.add(String.valueOf(0));

                                                        }
                                                    }


//                                                    switch (i) {
//
//                                                        case 0:
//
//
//                                                            if (listsaFacturasParcialTotal.size() > 0) {
//                                                                if (preciosfacturasLogicaCheque.size() > 0) {
//                                                                    for (int j = 0; j < preciosfacturasLogicaCheque.size(); j++) {
//
//                                                                        if (precios.get(j).equals(preciosfacturasLogicaCheque.get(j))) {
//
//                                                                            if (listsaFacturasParcialTotal.size() > 0) {
//                                                                                listsaFacturasParcialTotal.remove(j);
//                                                                                preciosfacturasLogicaCheque.remove(j);
//                                                                                precios.remove(j);
//                                                                            }
//
//                                                                            documentosFinanciero.remove(j);
//                                                                            claseDocumento.remove(j);
//                                                                            fechasDocumentos.remove(j);
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//
//
//                                                            for (int j = 0; j < preciosfacturasLogicaCheque.size(); j++) {
//                                                                double valorLista = 0;
//
//                                                                if (finalCountTarjeta >= 2) {
//                                                                    valorLista = Double.parseDouble(preciosfacturasLogicaTarjeta.get(j));
//                                                                }
//
//                                                                double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
//                                                                double valorLista3 = Double.parseDouble(preciosfacturasLogicaEfectivo.get(j));
//                                                                double valorLista6 = Double.parseDouble(preciosfacturasLogicaCheque.get(j));
//                                                                double valorLista5 = Double.parseDouble(precios.get(j));
//                                                                double valorLista4 = valorLista2 + valorLista + valorLista3 + valorLista6;
//                                                                totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista5 - valorLista4, 2));
//                                                                totalFacturasTransfe.add(totalesValoresLista);
//
//
//                                                            }
//
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//
//                                                            if (finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
//                                                                    || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {
//
//                                                                if (!inputTransf.isEmpty()) {
//                                                                    if (inputTransf.contains(".") && inputTransf.contains(",")) {
//
//                                                                        inputTransf = inputTransf.replace(".", "");
//                                                                        inputTransf = inputTransf.replace(",", ".");
//                                                                        valorTransf = Double.parseDouble(inputTransf);
//
//                                                                    } else if (inputTransf.contains(",")) {
//
//                                                                        inputTransf = inputTransf.replace(",", ".");
//                                                                        valorTransf = Double.parseDouble(inputTransf);
//
//                                                                    } else if (inputTransf.contains(".")) {
//
//
//                                                                        valorTransf = Double.parseDouble(inputTransf);
//
//                                                                    } else if (!inputTransf.contains(".") && !inputTransf.contains(",")) {
//                                                                        valorTransf = Double.parseDouble(inputTransf);
//                                                                    }
//                                                                }
//
//                                                            } else if (finalEmpresa.equals("AGCO")) {
//
//                                                                if (!inputTransf.isEmpty()) {
//
//                                                                    if (inputTransf.contains(".")) {
//
//                                                                        inputTransf = inputTransf.replace(".", "");
//                                                                        valorTransf = Double.parseDouble(inputTransf);
//
//
//                                                                    } else if (!inputTransf.contains(".") && !inputTransf.contains(",")) {
//                                                                        valorTransf = Double.parseDouble(inputTransf);
//                                                                    }
//                                                                }
//
//                                                            } else {
//
//                                                                if (!inputTransf.isEmpty()) {
//
//                                                                    if (inputTransf.contains(",")) {
//
//                                                                        inputTransf = inputTransf.replace(",", "");
//                                                                        valorTransf = Double.parseDouble(inputTransf);
//
//                                                                    } else if (inputTransf.contains(".")) {
//
//                                                                        valorTransf = Double.parseDouble(inputTransf);
//
//
//                                                                    } else if (!inputTransf.contains(".") && !inputTransf.contains(",")) {
//                                                                        valorTransf = Double.parseDouble(inputTransf);
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            if (valorobtenidoSegundo < 0) {
//
//                                                                nuevoValor = valorTransf;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                                if (valorTransf < 0) {
//
//                                                                    nuevoValor = valorTransf;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (valorTransf == 0) {
//
//                                                                    nuevoValor = valorTransf;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (valorTransf < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(valorTransf - valorobtenidoSegundo, 2);
//                                                                    nuevoValor = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorTransf));
//
//                                                                } else if (valorTransf > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(valorTransf - valorobtenidoSegundo, 2);
//                                                                    nuevoValor = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (valorTransf == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(valorTransf - valorobtenidoSegundo, 2);
//                                                                    nuevoValor = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                }
//
//                                                            } else if (valorobtenidoSegundo == 0) {
//
//                                                                nuevoValor = valorTransf;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                            }
//
//                                                            break;
//
//                                                        case 1:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//
//                                                                nuevoValor2 = nuevoValor;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                            } else if (valorobtenidoSegundo > 0) {
//
//                                                                if (nuevoValor < 0) {
//
//                                                                    nuevoValor2 = nuevoValor;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor == 0) {
//
//                                                                    nuevoValor2 = nuevoValor;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor < valorobtenidoSegundo) {
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                    nuevoValor2 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor));
//
//
//                                                                } else if (nuevoValor > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                    nuevoValor2 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                } else if (nuevoValor == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
//                                                                    nuevoValor2 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                }
//
//                                                            } else if (valorobtenidoSegundo == 0) {
//
//                                                                nuevoValor2 = nuevoValor;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                            }
//
//                                                            break;
//                                                        case 2:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor3 = nuevoValor2;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor2 < 0) {
//
//                                                                    nuevoValor3 = nuevoValor2;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor2 == 0) {
//
//                                                                    nuevoValor3 = nuevoValor2;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor2 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor3 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor2));
//
//                                                                } else if (nuevoValor2 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor3 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor2 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor2 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor3 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor2));
//
//                                                                }
//                                                            } else if (valorobtenidoSegundo == 0) {
//                                                                nuevoValor3 = nuevoValor2;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            }
//
//
//                                                            break;
//                                                        case 3:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor4 = nuevoValor3;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor3 < 0) {
//
//                                                                    nuevoValor4 = nuevoValor3;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor3 == 0) {
//
//                                                                    nuevoValor4 = nuevoValor3;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor3 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor4 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor3));
//
//                                                                } else if (nuevoValor3 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor4 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor3 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor3 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor4 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor3));
//
//                                                                }
//                                                            }
//
//                                                            break;
//                                                        case 4:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor5 = nuevoValor4;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor4 < 0) {
//
//                                                                    nuevoValor5 = nuevoValor4;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor4 == 0) {
//
//                                                                    nuevoValor5 = nuevoValor4;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor4 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor5 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor4));
//
//                                                                } else if (nuevoValor4 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor5 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor4 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor4 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor5 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor4));
//
//                                                                }
//                                                            }
//
//                                                            break;
//                                                        case 5:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor6 = nuevoValor5;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor5 < 0) {
//
//                                                                    nuevoValor6 = nuevoValor5;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor5 == 0) {
//
//                                                                    nuevoValor6 = nuevoValor5;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor5 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor6 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor5));
//
//                                                                } else if (nuevoValor5 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor6 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor5 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor5 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor6 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor5));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        case 6:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor7 = nuevoValor6;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor6 < 0) {
//
//                                                                    nuevoValor7 = nuevoValor6;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor6 == 0) {
//
//                                                                    nuevoValor7 = nuevoValor6;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor6 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor7 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor6));
//
//                                                                } else if (nuevoValor6 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor7 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor6 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor6 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor7 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor6));
//
//                                                                }
//                                                            }
//
//                                                            break;
//                                                        case 7:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor8 = nuevoValor7;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor7 < 0) {
//
//                                                                    nuevoValor8 = nuevoValor7;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor7 == 0) {
//
//                                                                    nuevoValor8 = nuevoValor7;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor7 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor8 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor7));
//
//                                                                } else if (nuevoValor7 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor8 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor7 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor7 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor8 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor7));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        case 8:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor9 = nuevoValor8;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor8 < 0) {
//
//                                                                    nuevoValor9 = nuevoValor8;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor8 == 0) {
//
//                                                                    nuevoValor9 = nuevoValor8;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor8 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor9 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor8));
//
//
//                                                                } else if (nuevoValor8 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor9 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                } else if (nuevoValor8 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor8 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor9 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor8));
//
//                                                                }
//
//                                                            }
//
//
//                                                            break;
//                                                        case 9:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor10 = nuevoValor9;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor9 < 0) {
//
//                                                                    nuevoValor10 = nuevoValor9;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor9 == 0) {
//
//                                                                    nuevoValor10 = nuevoValor9;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor9 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor10 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor9));
//
//
//                                                                } else if (nuevoValor9 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor10 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//
//                                                                } else if (nuevoValor9 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor9 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor10 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor9));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        case 10:
//
//                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasTransfe.get(i));
//
//                                                            if (valorobtenidoSegundo < 0) {
//                                                                nuevoValor11 = nuevoValor10;
//                                                                preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                preciosfacturasLogicaTransfe.add(String.valueOf(0));
//                                                            } else if (valorobtenidoSegundo > 0) {
//                                                                if (nuevoValor10 < 0) {
//
//                                                                    nuevoValor11 = nuevoValor10;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor10 == 0) {
//
//                                                                    nuevoValor11 = nuevoValor10;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(0));
//
//                                                                } else if (nuevoValor10 < valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor11 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor10));
//
//                                                                } else if (nuevoValor10 > valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor11 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(valorobtenidoSegundo));
//
//                                                                } else if (nuevoValor10 == valorobtenidoSegundo) {
//
//                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor10 - valorobtenidoSegundo, 2);
//                                                                    nuevoValor11 = totalbotenido;
//                                                                    preciosParcialTrasnf.add(String.valueOf(valorTransf));
//                                                                    preciosfacturasLogicaTransfe.add(String.valueOf(nuevoValor10));
//
//                                                                }
//                                                            }
//
//
//                                                            break;
//                                                        default:
//                                                            break;
//                                                    }


                                                    PreciosFacturasParcial parcial = new PreciosFacturasParcial();
                                                    parcial.valor = Double.parseDouble(preciosParcialTrasnf.get(i));
                                                    Gson gson33 = new Gson();
                                                    String jsonStringObject = gson33.toJson(parcial);
                                                    PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                                }


                                            } catch (Exception exception) {
                                                System.out.println("Error en la forma de pago parcial " + exception);
                                            }
                                        }


                                        DataBaseBO.eliminarRecaudos(idPagoTransf, contexto);

                                        DataBaseBO.guardarFormaPagParcial(idTarjetaPago, claseDocumento,
                                                sociedad, cod_cliente, finalCod_Vendedor,
                                                referenciaTransferencia, fechasDocumentos,
                                                fecha, precios, 0,
                                                moneda, preciosfacturasLogicaTransfe, preciosParcialTrasnf, cuentBancariaTransferencia,
                                                moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                                consecutivo1,
                                                descripc, "6", usuario, operacion_Cme,
                                                sincronizado, bancoTransferencia, "0",
                                                "0", fotoTransf, finalConsecutivoidTarjeta, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto);
                                    }

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


                            } catch (Exception exception) {
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

                DataBaseBO.eliminarFotoIDFac(fotosListaid, contexto);
                DataBaseBO.eliminarFoto(documentoFacturas, contexto);

                dialogo.cancel();
            }
        });


        dialogo.setCancelable(false);
        dialogo.show();
    }
}
