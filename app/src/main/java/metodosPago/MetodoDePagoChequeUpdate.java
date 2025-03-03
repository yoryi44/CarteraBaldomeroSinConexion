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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MetodoDePagoChequeUpdate {

    public static Dialog dialogo;
    public static Vector<Bancos> listaParametrosBancosSpinner;
    public static Vector<CuentasBanco> listaParametrosCuentas;

    /**
     * VISTA DIALOGO UPDATE CHEQUE
     *
     * @param contexto
     * @param titulo
     * @param texto
     * @param onClickListenerAceptar
     * @param onClickListenerCancelar
     */
    public static void vistaDialogoUpdateCheque(final Context contexto, @NonNull String titulo, @NonNull String texto, View.OnClickListener onClickListenerAceptar,
                                                View.OnClickListener onClickListenerCancelar) {

        ImageView cancelarFormaPagoFE, guardarFormaPagoFE, guardarFormaPagoPendienteChe;
        final TextView tvFechaFragEfec, tvDescripcionCheque, tvNumeroCheque, simboloCheq;
        final TextView tvReferenciaCheque, tvNombreCuenta, tvCuentaOrigen, tvCuentaDestino, tvMonto;
        final EditText tvValorFragEfec;
        final Button tomarFoto;
        final Cartera facturaCartera;
        final FormaPago formaPago;
        final Anticipo anticipo;
        final ClienteSincronizado clienteSel;
        Facturas fack;
        List<Facturas> listaFacturas2;
        List<Facturas> cargarFacturas;
        List<Facturas> cargarFacturasPen;
        final List<Facturas> listsaFacturasParcialTotal2;
        List<Facturas> listaFacturas4;
        final List<Facturas> listsaFacturasParcialTotal;
        final List<Facturas> listaFacturasCheque;
        final List<Facturas> listsaFacturasParcialTotalPendientes;
        final Lenguaje lenguajeElegido;
        final TextView txtCompaReciboDinero, txtCompaReciboDinero1, txtCompaReciboDinero2, tituloFechaCheque,
                tituloNumCheque, tituloMontoCheque, tituloReferenciaCheque, tituloNombreCuentaCheque, tituloCuentaOrigenCheque, tituloCuentaDestinoCheque;

        Gson gson223 = new Gson();
        String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(contexto);
        lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);


        dialogo = new Dialog(contexto);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.fragment_cheque);

        simboloCheq = dialogo.findViewById(R.id.simboloCheq);
        tvValorFragEfec = dialogo.findViewById(R.id.tvMontoCheque);
        tvFechaFragEfec = dialogo.findViewById(R.id.tvFechaFragEfec);
        //     tvDescripcionCheque = dialogo.findViewById(R.id.tvDescripcionCheque);
        tvReferenciaCheque = dialogo.findViewById(R.id.tvReferenciaCheque);
        tvNumeroCheque = dialogo.findViewById(R.id.tvNumeroCheque);
        tvCuentaDestino = dialogo.findViewById(R.id.tvCuentaDestino);
        tvMonto = dialogo.findViewById(R.id.tvMonto);
        guardarFormaPagoFE = dialogo.findViewById(R.id.guardarFormaPagoFE);
        guardarFormaPagoPendienteChe = dialogo.findViewById(R.id.guardarFormaPagoPendienteChe);
        tomarFoto = dialogo.findViewById(R.id.tomarFoto);

        txtCompaReciboDinero = dialogo.findViewById(R.id.txtCompaReciboDinero);
        txtCompaReciboDinero1 = dialogo.findViewById(R.id.txtCompaReciboDinero1);
        txtCompaReciboDinero2 = dialogo.findViewById(R.id.txtCompaReciboDinero2);
        tituloFechaCheque = dialogo.findViewById(R.id.tituloFechaCheque);
        tituloNumCheque = dialogo.findViewById(R.id.tituloNumCheque);
        tituloMontoCheque = dialogo.findViewById(R.id.tituloMontoCheque);
        tituloReferenciaCheque = dialogo.findViewById(R.id.tituloReferenciaCheque);
        tituloNombreCuentaCheque = dialogo.findViewById(R.id.tituloNombreCuentaCheque);
        tituloCuentaOrigenCheque = dialogo.findViewById(R.id.tituloCuentaOrigenCheque);
        tituloCuentaDestinoCheque = dialogo.findViewById(R.id.tituloCuentaDestinoCheque);

        String tipoUsuario = "";
        tipoUsuario = DataBaseBO.cargarTipoUsuarioApp();

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                txtCompaReciboDinero.setText("Register payment method");
                txtCompaReciboDinero1.setText("Check");
                txtCompaReciboDinero2.setText("Amount to be collected: ");
                tituloFechaCheque.setText("Date:");
                tituloNumCheque.setText("Num Check:");
                tituloMontoCheque.setText("Amount:");
                tituloReferenciaCheque.setText("Reference");
                tituloNombreCuentaCheque.setText("Name Account");
                tituloCuentaOrigenCheque.setText("Source Account:");
                tituloCuentaDestinoCheque.setText("Destination Account:");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {


            }
        }


        String empresa = "";
        String monedaTipo = "";
        String consecutivo = "";
        String consecutivofinal = "";
        String consecutivoNegocio = "";
        String consecutivoVendedor = "";
        String codigoVendedor = "";
        double countEfectivo = 0;
        double countCheque = 0;
        double countTarjeta = 0;
        double countTrasnferencia = 0;

        double DiferenciaFormasPago;
        double DiferenciaFormasPagoE = 0;
        double DiferenciaFormasPagoPEN = 0;
        double valorConsignadoEfectivo = 0;
        double valorConsignadoTarjeta = 0;
        double valorConsignadoTransferencia = 0;
        double valorConsignadoCheque = 0;
        double valorConsignadoBitcoin = 0;
        codigoVendedor = DataBaseBO.cargarCodigo();
        empresa = DataBaseBO.cargarEmpresa();
        monedaTipo = DataBaseBO.cargarMoneda();


        //Calendario para obtener fecha
        final String CERO = "0";
        final String BARRA = "-";
        final Calendar c = Calendar.getInstance();

        //Fecha
        final int mes = c.get(Calendar.MONTH);
        final int dia = c.get(Calendar.DAY_OF_MONTH);
        final int anio = c.get(Calendar.YEAR);


        final Spinner spinnerBanco = dialogo.findViewById(R.id.spinnerBancoCheq);
        spinnerBanco.setVisibility(View.VISIBLE);

        String[] items;
        Vector<String> listaItems = new Vector<String>();
        listaItems.addElement("Seleccione");
        listaParametrosBancosSpinner = DataBaseBO.cargarTipoBancos(listaItems);

        if (listaItems.size() > 0) {
            items = new String[listaItems.size()];
            listaItems.copyInto(items);

        } else {
            items = new String[]{};

            if (listaParametrosBancosSpinner != null)
                listaParametrosBancosSpinner.removeAllElements();
        }

        ArrayAdapter adapterBanco = new ArrayAdapter<>(contexto.getApplicationContext(), android.R.layout.simple_spinner_item, items);
        adapterBanco.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBanco.setAdapter(adapterBanco);
        adapterBanco.notifyDataSetChanged();
        String banco = "";
        banco = spinnerBanco.getSelectedItem().toString();

        final Spinner spinnerCuentasBanco = dialogo.findViewById(R.id.spinnerCuentasBanco);
        spinnerCuentasBanco.setVisibility(View.VISIBLE);

        String[] items2;
        Vector<String> listaItems2 = new Vector<String>();
        listaItems2.addElement("Seleccione");
        String parametro = spinnerBanco.getSelectedItem().toString();
        listaParametrosCuentas = DataBaseBO.cargarCuentasBancos(listaItems2, parametro);

        if (listaItems2.size() > 0) {
            items2 = new String[listaItems2.size()];
            listaItems2.copyInto(items2);

        } else {
            items2 = new String[]{};

            if (listaParametrosCuentas != null)
                listaParametrosCuentas.removeAllElements();
        }

        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("AABR")) {
            simboloCheq.setText("$");
        }
        if (finalEmpresa.equals("ADHB")) {
            simboloCheq.setText("$");
        }
        if (finalEmpresa.equals("AGSC")) {
            simboloCheq.setText("$");
        }
        if (finalEmpresa.equals("AGGC")) {
            simboloCheq.setText("Q");
        }
        if (finalEmpresa.equals("AFPN")) {
            simboloCheq.setText("C$");
        }
        if (finalEmpresa.equals("AFPZ")) {
            simboloCheq.setText("₡");
        }
        if (finalEmpresa.equals("AGCO")) {
            simboloCheq.setText("$");
        }
        if (finalEmpresa.equals("AGAH")) {
            simboloCheq.setText("₡");
        }
        if (finalEmpresa.equals("AGDP")) {
            simboloCheq.setText("Q");
        }
        if (finalEmpresa.equals("AGUC")) {
            simboloCheq.setText("US");
        }


        tvFechaFragEfec.setOnClickListener(new View.OnClickListener() {
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
                        tvFechaFragEfec.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);

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
        final List<String> idesPagoCheque = new ArrayList<>();
        String idsPagos = "",idFotos = "";

        if(empresa.equals("AGUC"))
        {
            (dialogo.findViewById(R.id.lienarChequeReferencia)).setVisibility(View.GONE);
        }

        if (anticipo != null) {
            if (anticipo.estado == true) {
                LinearLayout lienarChequeReferencia = dialogo.findViewById(R.id.lienarChequeReferencia);
                if(!empresa.equals("AGUC"))
                    lienarChequeReferencia.setVisibility(View.VISIBLE);
            } else if (anticipo.estado == false) {
                LinearLayout lienarChequeReferencia = dialogo.findViewById(R.id.lienarChequeReferencia);
                if(!empresa.equals("AGUC"))
                    lienarChequeReferencia.setVisibility(View.VISIBLE);
            }
        }


        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject222, collectionType);

        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        final List<String> documentosFinancieroAnticipo = new ArrayList<>();
        final List<String> precios = new ArrayList<>();
        final List<String> fechaConsignacionesAntiPend = new ArrayList<>();
        final List<String> valoresfacturas = new ArrayList<>();
        final List<String> valoresfacturasEfectivo = new ArrayList<>();
        final List<String> valoresfacturasTransfe = new ArrayList<>();
        final List<String> valoresfacturasTarjerta = new ArrayList<>();
        final List<String> valoresfacturasBitcoin = new ArrayList<>();
        final List<String> preciosAcomparar = new ArrayList<>();
        final List<String> totalFacturas = new ArrayList<>();
        final List<String> totalFacturasEfectivo = new ArrayList<>();
        final List<String> totalFacturasCheque = new ArrayList<>();
        final List<String> totalFacturasTransfe = new ArrayList<>();
        final List<String> totalFacturasTarjeta = new ArrayList<>();
        final List<String> totalFacturasBitcoin = new ArrayList<>();
        final List<String> preciosParcial = new ArrayList<>();
        final List<String> preciosParcialCheq = new ArrayList<>();
        final List<String> preciosParcialEfectivo = new ArrayList<>();
        final List<String> preciosParcialTrasnf = new ArrayList<>();
        final List<String> preciosParcialTarjeta = new ArrayList<>();
        final List<String> preciosParcialBitcoin = new ArrayList<>();
        final List<String> preciosAnticipo = new ArrayList<>();
        final List<String> documentt = new ArrayList<>();
        final List<String> preciosfacturasLogica = new ArrayList<>();
        final List<String> preciosfacturasLogicaCheq = new ArrayList<>();
        final List<String> preciosfacturasLogicaEfectivo = new ArrayList<>();
        final List<String> preciosfacturasLogicaTransfe = new ArrayList<>();
        final List<String> preciosfacturasLogicaTarjeta = new ArrayList<>();
        final List<String> preciosfacturasLogicaBitcoin = new ArrayList<>();
        final List<String> fechasDocumentos = new ArrayList<>();
        final List<Facturas> listaFacturas3 = new ArrayList<>();
        final List<String> preciosComparar = new ArrayList<>();
        final List<String> listaPrecios = new ArrayList<>();
        final List<String> preciosListaTotal = new ArrayList<>();

        String claseDocument = "";
        String preciosFacturas = "";
        String documentoFinanciero = "";
        String nombreU = "";
        String fechasDocus = "";
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
                fechasDocumentos.add(cartera1.getFechaVencto());
                int Position = 2;
                claseDocument = claseDocument.substring(0, Position);

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

        consecutivo = DataBaseBO.cargarConsecutivoUpdate(idsPagos);

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

        String nroReciboFacTotalPar = clienteSel.consecutivo;
        //  listsaFacturasParcialTotal = DataBaseBO.cargarFacParTotal(nroReciboFacTotalPar, documentosFinanciero);
        listsaFacturasParcialTotal = Utilidades.listaFacturasParcialTotal(contexto);
        listaFacturasCheque = Utilidades.listaFacturasMetodoCheque(contexto, idesPago);

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


        countEfectivo = Utilidades.CountMetodoPagoEfec(contexto);
        countCheque = Utilidades.CountMetodoPagoCheq(contexto);
        countTarjeta = Utilidades.CountMetodoPagoTarjeta(contexto);
        countTrasnferencia = Utilidades.CountMetodoPagoTransF(contexto);

        valorConsignadoEfectivo = Utilidades.totalFormasPagoEfec(contexto);
        valorConsignadoTransferencia = Utilidades.totalFormasPagoTranF(contexto);
        valorConsignadoCheque = Utilidades.totalFormasPagoCheq(contexto);
        valorConsignadoTarjeta = Utilidades.totalFormasPagoTarje(contexto);
        valorConsignadoBitcoin = Utilidades.totalFormasPagoBit(contexto);

        DiferenciaFormasPago = (DiferenciaFormasPagoE);

        double valorfinal = 00;
        double salfoAFA = 0;
        double rfv = 0;

        double rfd = (Utilidades.formatearDecimales(Utilidades.totalDifereFAv(contexto.getApplicationContext()), 2));
        salfoAFA = rfd;
        double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsig(contexto.getApplicationContext()), 2));
        double comparar=0;
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

        tvReferenciaCheque.setText("");

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
        final String fecha_Consignacion = tvFechaFragEfec.getText().toString().trim();
        fechaConsignacionesAntiPend.add(Utilidades.fechaActual("yyyyMMdd"));
        final String moneda = monedaTipo;
        final double valor_Consignado = 0;
        final String cuenta_Bancaria = "0";
        final double finalValor = valorfinal;
        final String moneda_Consig = monedaTipo;
        final String NCF_Comprobante_fiscal = null;
        final String docto_Financiero = "0";
        final String consecutivo1 = clienteSel.consecutivo;
        final int consecutivo2 = clienteSel.consecutivoInicial;
        final String obserbaciones = "";
        final String via_Pago = "B";
        final String usuario = codigoVendedor;
        final String operacion_Cme = null;
        final int sincronizado = 0;
        int Position = 2;
        codigoVendedor = codigoVendedor.substring(0, Position);
        final double finalValorfinal = valorfinal;
        final String finalIdsPagos = idsPagos;
        final String finalIdsFotos = idFotos;
        final String finalDocumentoFinanciero = documentoFinanciero;
        final String idenFoto = codigoVendedor + Utilidades.fechaActual("HHmmss") + 1;
        final String idPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss");
        final String idEfectivoPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + 4;
        final String idChequePago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + 1;
        final String idTarjetaPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + 2;
        final String idTransferenciaPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss") + 3;

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fotos fotosestado = new Fotos();
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
                MetodoDePagoChequeUpdate.dialogo.onSaveInstanceState();


            }
        });


        cargarFacturas = DataBaseBO.cargarFacturasParametroIDS(idesPago);

        ArrayAdapter adapterCuentasBanco = new ArrayAdapter<>(contexto.getApplicationContext(), android.R.layout.simple_spinner_item, items2);
        adapterCuentasBanco.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuentasBanco.setAdapter(adapterCuentasBanco);
        adapterCuentasBanco.notifyDataSetChanged();

        spinnerBanco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                for (int j = 0; j < spinnerBanco.getCount(); j++) {

                    String banco = "";
                    banco = spinnerBanco.getSelectedItem().toString();
                    String parametro;
                    parametro = DataBaseBO.cargarCodigobanco(banco);
                    String[] items2;
                    Vector<String> listaItems2 = new Vector<String>();
                    listaItems2.addElement("Seleccione");
                    listaParametrosCuentas = DataBaseBO.cargarCuentasBancos(listaItems2, DataBaseBO.cargarCodigobanco(banco));

                    if (listaItems2.size() > 0) {
                        items2 = new String[listaItems2.size()];
                        listaItems2.copyInto(items2);

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

                    for(Facturas factura : facCollectionFacturas)
                    {
                        if(spinnerCuentasBanco != null)
                        {
                            spinnerCuentasBanco.setSelection(adapterCuentasBanco.getPosition(factura.getCuentaBancaria()));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                tvReferenciaCheque.setText(facturas1.getReferencia());
                spinnerBanco.setSelection(adapterBanco.getPosition(facturas1.getBanco()));
                tvFechaFragEfec.setText(facturas1.getFechaConsignacion());
                tvNumeroCheque.setText(facturas1.getNumeroCheque());
            }
        }
        cargarFacturasPen = DataBaseBO.cargarFacturasParametroIDSPen(idesPago);

        if (cargarFacturasPen.size() > 0) {
            for (Facturas facturas1 : cargarFacturasPen) {
                if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                        || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                    tvValorFragEfec.setText(formatoNumero.format(valorSetText));

                } else {

                    NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                    tvValorFragEfec.setText(formatoNumero.format(valorSetText));

                }
                tvReferenciaCheque.setText(facturas1.getReferencia());
                spinnerBanco.setSelection(adapterBanco.getPosition(facturas1.getBanco()));
                tvFechaFragEfec.setText(facturas1.getFechaConsignacion());
                tvNumeroCheque.setText(facturas1.getNumeroCheque());
            }
        }

        guardarFormaPagoFE.setVisibility(View.GONE);

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
        double finalValorConsignadoTarjeta = valorConsignadoTarjeta;
        double finalValorConsignadoTransferencia = valorConsignadoTransferencia;
        double finalValorConsignadoBitcoin = valorConsignadoBitcoin;
        double finalValorConsignadoCheque = valorConsignadoCheque;
        String finalIdsPagos1 = idsPagos;

        double finalCountCheque = countCheque;
        double finalCountTrasnferencia = countTrasnferencia;
        double finalCountTarjeta = countTarjeta;

        double finalCountEfectivo = countEfectivo;

        String finalCod_Vendedor = cod_Vendedor;

        String vendedor = "";

        vendedor = DataBaseBO.cargarVendedorConsecutivo();

        DataBaseBO.eliminarConsecutivoId(vendedor);

        final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
        String consecId1 = "", numeroAnulacionId1 = "";
        String negocioId1 = "";
        String vendedorId1 = "";

        String consecutivoid = "";
        String consecutivoidEfectivo2 = "";
        String consecutivoidCheque = "";
        String consecutivoidTarjeta = "";
        String consecutivoidTransferencia = "";

        consecId1 = DataBaseBO.cargarConsecutivoId();
        negocioId1 = DataBaseBO.cargarNegocioConsecutivoId();
        vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId();

        int consec1Id = Integer.parseInt(consecId1);
        int consec1IdEfec = Integer.parseInt(consecId1) + 1;
        int consec1IdCheq = Integer.parseInt(consecId1) + 2;
        int consec1IdTarjeta = Integer.parseInt(consecId1) + 3;
        int consec1IdTransferencia = Integer.parseInt(consecId1) + 4;

        int vendedorsumId = Integer.parseInt(vendedorId1);
        int contadorId = 1;
        consec1Id = consec1Id + contadorId;
        numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

        DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon);

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
        guardarFormaPagoPendienteChe.setOnClickListener(new View.OnClickListener() {
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
                String input = "";
                String inputCheq = "";
                String inputEfect = "";
                String inputTransf = "";
                String inputTarjeta = "";
                String inputBitcoin = "";
                String fecha = "";
                String descripcion = "";
                String referenciaCheq = "";
                String cuentaDestino = "";
                String fotoID = "";
                double valor = 0;
                double valorCheq = finalValorConsignadoCheque;
                double valorEfec = finalValorConsignadoEfect;
                double valorTarj = finalValorConsignadoTarjeta;
                double valorTransf = finalValorConsignadoTransferencia;
                double valorBit = finalValorConsignadoBitcoin;
                String numCheque = "";
                String nombrePropietario = "";
                String cuentasBanco = "";
                double totalbotenido = 0;
                String spinBanco = "";


                Gson gson1 = new Gson();
                String stringJsonObject1 = PreferencesFotos.obteneFotoSeleccionada(contexto);
                Fotos fotos = gson1.fromJson(stringJsonObject1, Fotos.class);


                if (facCollection != null) {
                    fecha = tvFechaFragEfec.getText().toString();
                    descripcion = formaPago.getObservaciones();
                    referenciaCheq = tvReferenciaCheque.getText().toString();
                    cuentaDestino = tvCuentaDestino.getText().toString();
                    numCheque = tvNumeroCheque.getText().toString();
                    nombrePropietario = "";
                    input = tvValorFragEfec.getText().toString();
                    cuentasBanco = spinnerCuentasBanco.getSelectedItemPosition() == 0 ? "" : spinnerCuentasBanco.getSelectedItem().toString();
                    spinBanco = spinnerBanco.getSelectedItemPosition() == 0 ? "" : spinnerBanco.getSelectedItem().toString();
                }

                if (facCollection == null) {
                    if (anticipo != null) {
                        if (anticipo.estado == true) {
                            fecha = tvFechaFragEfec.getText().toString();
                            descripcion = anticipo.getObservaciones();
                            cuentaDestino = tvCuentaDestino.getText().toString();
                            numCheque = tvNumeroCheque.getText().toString();
                            nombrePropietario = "";
                            input = tvValorFragEfec.getText().toString();
                            referenciaCheq = tvReferenciaCheque.getText().toString();
                            cuentasBanco = spinnerCuentasBanco.getSelectedItemPosition() == 0 ? "" : spinnerCuentasBanco.getSelectedItem().toString();
                            spinBanco = spinnerBanco.getSelectedItemPosition() == 0 ? "" : spinnerBanco.getSelectedItem().toString();
                        } else if (anticipo.estado == false) {
                            fecha = tvFechaFragEfec.getText().toString();
                            descripcion = anticipo.getObservaciones();
                            cuentaDestino = tvCuentaDestino.getText().toString();
                            numCheque = tvNumeroCheque.getText().toString();
                            nombrePropietario = "";
                            input = tvValorFragEfec.getText().toString();
                            referenciaCheq = tvReferenciaCheque.getText().toString();
                            cuentasBanco = null;
                            spinBanco = null;
                        }
                    }
                }

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

                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        if (valor == 0) {
                            Toasty.warning(contexto, "The amount field cannot be blank...", Toasty.LENGTH_SHORT).show();
                        }

                        if (numCheque.equals("")) {
                            Toasty.warning(contexto, "The check number field cannot be blank...", Toasty.LENGTH_SHORT).show();
                        }

                        if (fecha.equals("")) {
                            Toasty.warning(contexto, "The date field cannot be blank...", Toasty.LENGTH_SHORT).show();
                        }

                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        if (valor == 0) {
                            Toasty.warning(contexto, "El campo del monto no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                        }

                        if (numCheque.equals("")) {
                            Toasty.warning(contexto, "El campo del numero del cheque no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                        }


                        if (fecha.equals("")) {
                            Toasty.warning(contexto, "El campo de fecha no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                        }

                    }
                }

                if (input.equals("0") || input.equals("")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {


                            Toasty.warning(contexto, "The amount field cannot be blank or set to 0..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                            Toasty.warning(contexto, "El campo del monto no puede quedar en blanco o estar en 0..", Toasty.LENGTH_SHORT).show();


                        }
                    }

                } else if (!input.equals("0")) {

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
//CHEQUE
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

                                }
//                                else if (input.contains(",")) {
//
//                                    input = input.replace(",", ".");
//                                    valor = Double.parseDouble(input);
//
//                                } else if (input.contains(".")) {
//
//                                    input = input.replace(".", "");
//                                    valor = Double.parseDouble(input);
//
//                                } else if (!input.contains(".") && !input.contains(",")) {
//                                    valor = Double.parseDouble(input);
//                                }
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
                            Toasty.warning(contexto, "El campo del monto no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                        }

                        precios.add(input);

                    }


                }

                if (facCollection != null) {
                    if (valor != 0 && !fecha.equals("")
                            && !numCheque.equals("")) {

                        if (facCollection != null) {

                            if (fotos == null) {
                                fotoID = DataBaseBO.cargarFotosSinDocumentosAsociados(documentosFinanciero.get(0));
                            }
                            if (fotos != null) {
                                if(finalIdsFotos != null)
                                {
                                    if(!finalIdsFotos.isEmpty())
                                    {
                                        fotoID = finalIdsFotos;
                                    }
                                    else
                                    {
                                        fotoID = fotos.idenFoto;
                                    }
                                }
                                else
                                {
                                    fotoID = fotos.idenFoto;
                                }
                            }


                            try {

                                String vendedor = "";
                                vendedor = DataBaseBO.cargarVendedorConsecutivo();
                                DataBaseBO.eliminarConsecutivoId(vendedor);

                                if (DataBaseBO.updateFormaPagoPendientes(finalIdsPagos, claseDocumento, sociedad,
                                        cod_cliente, finalCod_Vendedor,
                                        referenciaCheq, fechasDocumentos, fecha, precios,
                                        moneda, preciosfacturasLogica,
                                        preciosParcial, cuentasBanco, moneda_Consig, NCF_Comprobante_fiscal,
                                        documentosFinanciero, consecutivo1,
                                        descripcion, via_Pago, usuario, operacion_Cme, sincronizado, spinBanco, numCheque,
                                        "0", fotoID, finalConsecutivoid)) {

                                    DataBaseBO.eliminarRecaudosPendientes(finalIdsPagos);

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


                                    DataBaseBO.guardarFormaPagParcialPendiente(idPago, claseDocumento,
                                            sociedad, cod_cliente, finalCod_Vendedor,
                                            referenciaCheq, fechasDocumentos,
                                            fecha, precios,
                                            moneda, preciosfacturasLogica, preciosParcial, resultadoAfavor, cuentasBanco,
                                            moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                            consecutivo1,
                                            descripcion, "B", usuario, operacion_Cme,
                                            sincronizado, spinBanco, numCheque,
                                            "0", fotoID, finalConsecutivoid, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "");


                                    //CHEQUE

                                    if (finalCountCheque >= 2) {


                                        //CHEQUE

                                        final List<Facturas> listsaFacturasParcialTotalCheq;
                                        final List<Facturas> listsaIDCheq;
                                        final List<String> listaFacHchasUnaPorUna = new ArrayList<>();

                                        double valorCom = 0;
                                        String idPagoCheque = "";
                                        String referenciaCheque = "";
                                        String numeroCheque = "";


                                        listaFacHchasUnaPorUna.add(idPago);
                                        listsaFacturasParcialTotalCheq = Utilidades.listaFacturasParcialTotalCheq(contexto);
                                        listsaIDCheq = Utilidades.listaFacturasIDCheq(contexto);


                                        for (Facturas facturasids : listaFacturasCheque) {
                                            idPagoCheque = facturasids.getIdPago();
                                            if (finalIdsPagos1 != idPagoCheque) {
                                                idesPagoCheque.add(idPagoCheque);
                                                referenciaCheque = facturasids.referencia;

                                            }
                                        }

                                        for (int k = 0; k < idesPagoCheque.size(); k++) {

                                            double valorPorid = Utilidades.totalFormasPagoEfecCantidadFacturasHechas(contexto, idesPagoCheque.get(k), listaFacHchasUnaPorUna);
                                            inputCheq = String.valueOf(valorPorid);

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
                                                                        totalFacturasCheque.clear();
                                                                        preciosParcialCheq.clear();
                                                                        preciosfacturasLogicaCheq.clear();
                                                                        totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista3 - valorvaloresFacReales, 2));
                                                                        totalFacturasCheque.add(totalesValoresLista);
                                                                    }

                                                                } else {

                                                                    totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista3 - valorLista2, 2));
                                                                    totalFacturasCheque.add(totalesValoresLista);
                                                                }

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

                                                                if (!inputCheq.isEmpty()) {
                                                                    if (inputCheq.contains(".") && inputCheq.contains(",")) {

                                                                        inputCheq = inputCheq.replace(".", "");
                                                                        inputCheq = inputCheq.replace(",", ".");
                                                                        valorCheq = Double.parseDouble(inputCheq);

                                                                    } else if (inputCheq.contains(",")) {

                                                                        inputCheq = inputCheq.replace(",", ".");
                                                                        valorCheq = Double.parseDouble(inputCheq);

                                                                    } else if (inputCheq.contains(".")) {


                                                                        valorCheq = Double.parseDouble(inputCheq);

                                                                    } else if (!inputCheq.contains(".") && !inputCheq.contains(",")) {
                                                                        valorCheq = Double.parseDouble(inputCheq);
                                                                    }
                                                                }

                                                            } else if (finalEmpresa.equals("AGCO")) {

                                                                if (!inputCheq.isEmpty()) {

                                                                    if (inputCheq.contains(".")) {

                                                                        inputCheq = inputCheq.replace(".", "");
                                                                        valorCheq = Double.parseDouble(inputCheq);


                                                                    } else if (!inputCheq.contains(".") && !inputCheq.contains(",")) {
                                                                        valorCheq = Double.parseDouble(inputCheq);
                                                                    }
                                                                }

                                                            } else {

                                                                if (!inputCheq.isEmpty()) {

                                                                    if (inputCheq.contains(",")) {

                                                                        inputCheq = inputCheq.replace(",", "");
                                                                        valorCheq = Double.parseDouble(inputCheq);

                                                                    } else if (inputCheq.contains(".")) {

                                                                        valorCheq = Double.parseDouble(inputCheq);


                                                                    } else if (!inputCheq.contains(".") && !inputCheq.contains(",")) {
                                                                        valorCheq = Double.parseDouble(inputCheq);
                                                                    }
                                                                }
                                                            }

                                                            if (valorobtenidoSegundo < 0) {

                                                                nuevoValor = valorCheq;
                                                                preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                preciosfacturasLogicaCheq.add(String.valueOf(0));

                                                            } else if (valorobtenidoSegundo > 0) {

                                                                if (valorCheq < 0) {

                                                                    nuevoValor = valorCheq;
                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(0));

                                                                } else if (valorCheq == 0) {

                                                                    nuevoValor = valorCheq;
                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(0));

                                                                } else if (valorCheq < valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(valorCheq - valorobtenidoSegundo, 2);
                                                                    nuevoValor = totalbotenido;
                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(valorCheq));

                                                                } else if (valorCheq > valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(valorCheq - valorobtenidoSegundo, 2);
                                                                    nuevoValor = totalbotenido;
                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(valorobtenidoSegundo));

                                                                } else if (valorCheq == valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(valorCheq - valorobtenidoSegundo, 2);
                                                                    nuevoValor = totalbotenido;
                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(valorobtenidoSegundo));

                                                                }

                                                            } else if (valorobtenidoSegundo == 0) {

                                                                nuevoValor = valorCheq;
                                                                preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                preciosfacturasLogicaCheq.add(String.valueOf(0));

                                                            }
                                                        }
                                                        else
                                                        {
                                                            valorobtenidoSegundo = Double.parseDouble(totalFacturasCheque.get(i));


                                                            if (valorobtenidoSegundo < 0) {

                                                                preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                preciosfacturasLogicaCheq.add(String.valueOf(0));

                                                            } else if (valorobtenidoSegundo > 0) {

                                                                if (nuevoValor < 0) {

                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(0));

                                                                } else if (nuevoValor == 0) {

                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(0));

                                                                } else if (nuevoValor < valorobtenidoSegundo) {
                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(nuevoValor));
                                                                    nuevoValor = totalbotenido;

                                                                } else if (nuevoValor > valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                    nuevoValor = totalbotenido;
                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(valorobtenidoSegundo));


                                                                } else if (nuevoValor == valorobtenidoSegundo) {

                                                                    totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                    nuevoValor = totalbotenido;
                                                                    preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                    preciosfacturasLogicaCheq.add(String.valueOf(valorobtenidoSegundo));

                                                                }

                                                            } else if (valorobtenidoSegundo == 0) {

                                                                nuevoValor = nuevoValor;
                                                                preciosParcialCheq.add(String.valueOf(valorCheq));
                                                                preciosfacturasLogicaCheq.add(String.valueOf(0));

                                                            }
                                                        }

                                                        PreciosFacturasParcial parcial = new PreciosFacturasParcial();
                                                        parcial.valor = Double.parseDouble(preciosParcialCheq.get(i));
                                                        Gson gson33 = new Gson();
                                                        String jsonStringObject = gson33.toJson(parcial);
                                                        PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                                                    }


                                                } catch (Exception exception) {
                                                    System.out.println("Error en la forma de pago parcial Cheque.... " + exception);
                                                }
                                            }

                                            DataBaseBO.eliminarRecaudosPendientes(idPagoCheque);

                                            final String idChequePago = finalCodigoVendedor + Utilidades.fechaActual("ddHHmmss") + k;

                                            listaFacHchasUnaPorUna.add(idChequePago);
                                            DataBaseBO.guardarFormaPagParcialPendiente(idChequePago, claseDocumento,
                                                    sociedad, cod_cliente, finalCod_Vendedor,
                                                    referenciaCheque, fechasDocumentos,
                                                    fecha, precios,
                                                    moneda, preciosfacturasLogicaCheq, preciosParcialCheq, 0, cuentasBanco,
                                                    moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                                    consecutivo1,
                                                    descripcion, "B", usuario, operacion_Cme,
                                                    sincronizado, spinBanco, numeroCheque,
                                                    "0", fotoID, finalConsecutivoidEfectivo, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "");
                                        }









                                    }


                                    //EFECTIVO

                                    if (finalCountEfectivo >= 1) {


                                        final List<Facturas> listsaFacturasParcialTotalEfectivo;
                                        final List<Facturas> listsaIDEfectivo;
                                        double valorCom = 0;
                                        String idPagoEfectivo = "";
                                        String referenciaEfectivo = "";
                                        String banco = "";
                                        String cuentaBancaria = "";
                                        listsaFacturasParcialTotalEfectivo = Utilidades.listaFacturasParcialTotalEfectivo(contexto);
                                        listsaIDEfectivo = Utilidades.listaFacturasIDEfectivo(contexto);

                                        for (Facturas fac : listsaFacturasParcialTotalEfectivo) {
                                            valorCom = fac.valor;
                                            valoresfacturasEfectivo.add(String.valueOf(Utilidades.formatearDecimales(valorCom, 2)));

                                        }
                                        for (Facturas fac : listsaIDEfectivo) {
                                            referenciaEfectivo = fac.referencia;
                                            idPagoEfectivo = fac.idPago;
                                            fotoID = fac.idenFoto;
                                            banco = fac.banco;
                                            cuentaBancaria = fac.cuentaBancaria;

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

                                                                            if (finalCountCheque >= 2) {

                                                                                if (precios.get(j).equals(preciosfacturasLogicaCheq.get(j))) {
                                                                                    preciosfacturasLogicaCheq.remove(j);
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

                                                            if (finalCountCheque >= 2) {
                                                                valorLista = Double.parseDouble(preciosfacturasLogicaCheq.get(j));
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


                                        DataBaseBO.eliminarRecaudosPendientes(idPagoEfectivo);

                                        DataBaseBO.guardarFormaPagParcialPendiente(idEfectivoPago, claseDocumento,
                                                sociedad, cod_cliente, finalCod_Vendedor,
                                                referenciaEfectivo, fechasDocumentos,
                                                fecha, precios,
                                                moneda, preciosfacturasLogicaEfectivo, preciosParcialEfectivo, 0, cuentaBancaria,
                                                moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                                consecutivo1,
                                                descripcion, "A", usuario, operacion_Cme,
                                                sincronizado, banco, "0",
                                                "0", fotoID, finalConsecutivoidCheque, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "");

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
                                        String fotoTrans = "";

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
                                            fotoTrans = fac.idenFoto;

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


                                                        for (int j = 0; j < preciosfacturasLogicaEfectivo.size(); j++) {
                                                            double valorLista = 0;

                                                            if (finalCountCheque >= 2) {
                                                                valorLista = Double.parseDouble(preciosfacturasLogicaCheq.get(j));
                                                            }

                                                            double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
                                                            double valorLista3 = Double.parseDouble(preciosfacturasLogicaEfectivo.get(j));
                                                            double valorLista5 = Double.parseDouble(precios.get(j));
                                                            double valorLista4 = valorLista2 + valorLista + valorLista3;
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


//                                        DataBaseBO.eliminarRecaudos(idPagoTransf);
//
//                                        DataBaseBO.guardarFormaPagParcial(idTransferenciaPago, claseDocumento,
//                                                sociedad, cod_cliente, finalCod_Vendedor,
//                                                referenciaTransferencia, fechasDocumentos,
//                                                fecha, precios, 0,
//                                                moneda, preciosfacturasLogicaTransfe, preciosParcialTrasnf, cuentBancariaTransferencia,
//                                                moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
//                                                consecutivo1,
//                                                descripcion, "6", usuario, operacion_Cme,
//                                                sincronizado, bancoTransferencia, "0",
//                                                "0", fotoTrans, finalConsecutivoidTransferencia);
                                    }


                                    /***DataBaseBO.updateFormaPago(idPagoTransf, claseDocumento, sociedad, cod_cliente,
                                     cod_Vendedor, referenciaTransferencia, fechasDocumentos,
                                     fecha,
                                     precios, moneda,
                                     preciosfacturasLogicaTransfe,
                                     preciosParcialTrasnf,
                                     cuentBancariaTransferencia, moneda_Consig,
                                     NCF_Comprobante_fiscal, documentosFinanciero, consecutivo1,
                                     descripcion, "6",
                                     usuario, operacion_Cme, sincronizado, bancoTransferencia, "0",
                                     "0", fotos.idenFoto);***/


                                    //Tarjeta

                                    if (finalCountTarjeta >= 1) {
                                        final List<Facturas> listsaFacturasParcialTotalTarjeta;
                                        final List<Facturas> listsaIDTarjeta;
                                        double valorComTarjeta = 0;
                                        String idPagoTarjeta = "";
                                        String nombrePropietarioTarjeta = "";
                                        String referenciaTarjeta = "";
                                        String cuentaBancariaTarjeta = "";
                                        String fotoTarjeta = "";
                                        listsaFacturasParcialTotalTarjeta = Utilidades.listaFacturasParcialTotalTarjeta(contexto);
                                        listsaIDTarjeta = Utilidades.listaFacturasIDTTarjeta(contexto);

                                        for (Facturas fac : listsaFacturasParcialTotalTarjeta) {
                                            valorComTarjeta = fac.valor;
                                            valoresfacturasTarjerta.add(String.valueOf(Utilidades.formatearDecimales(valorComTarjeta, 2)));

                                        }
                                        for (Facturas fac : listsaIDTarjeta) {
                                            referenciaTarjeta = fac.referencia;
                                            cuentaBancariaTarjeta = fac.cuentaBancaria;
                                            idPagoTarjeta = fac.idPago;
                                            nombrePropietarioTarjeta = fac.nombrePropietario;
                                            fotoTarjeta = fac.idenFoto;

                                        }

                                        inputTarjeta = String.valueOf(valorTarj);

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
                                                            if (preciosfacturasLogicaTransfe.size() > 0) {
                                                                for (int j = 0; j < preciosfacturasLogicaTransfe.size(); j++) {

                                                                    if (precios.get(j).equals(preciosfacturasLogicaTransfe.get(j))) {

                                                                        if (listsaFacturasParcialTotal.size() > 0) {
                                                                            listsaFacturasParcialTotal.remove(j);
                                                                            preciosfacturasLogicaTransfe.remove(j);
                                                                            precios.remove(j);
                                                                        }

                                                                        documentosFinanciero.remove(j);
                                                                        claseDocumento.remove(j);
                                                                        fechasDocumentos.remove(j);
                                                                    }
                                                                }
                                                            }
                                                        }


                                                        for (int j = 0; j < preciosfacturasLogicaTransfe.size(); j++) {
                                                            double valorLista = 0;

                                                            if (finalCountCheque >= 2) {
                                                                valorLista = Double.parseDouble(preciosfacturasLogicaCheq.get(j));
                                                            }

                                                            double valorLista2 = Double.parseDouble(preciosfacturasLogica.get(j));
                                                            double valorLista3 = Double.parseDouble(preciosfacturasLogicaEfectivo.get(j));
                                                            double valorLista6 = Double.parseDouble(preciosfacturasLogicaTransfe.get(j));
                                                            double valorLista5 = Double.parseDouble(precios.get(j));
                                                            double valorLista4 = valorLista2 + valorLista + valorLista3 + valorLista6;
                                                            totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista5 - valorLista4, 2));
                                                            totalFacturasTarjeta.add(totalesValoresLista);


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

                                                            preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                            preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                        } else if (valorobtenidoSegundo > 0) {

                                                            if (nuevoValor < 0) {

                                                                preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                            } else if (nuevoValor == 0) {

                                                                preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                            } else if (nuevoValor < valorobtenidoSegundo) {
                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                preciosfacturasLogicaTarjeta.add(String.valueOf(nuevoValor));
                                                                nuevoValor = totalbotenido;

                                                            } else if (nuevoValor > valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));


                                                            } else if (nuevoValor == valorobtenidoSegundo) {

                                                                totalbotenido = Utilidades.formatearDecimales(nuevoValor - valorobtenidoSegundo, 2);
                                                                nuevoValor = totalbotenido;
                                                                preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                                preciosfacturasLogicaTarjeta.add(String.valueOf(valorobtenidoSegundo));

                                                            }

                                                        } else if (valorobtenidoSegundo == 0) {

                                                            nuevoValor = nuevoValor;
                                                            preciosParcialTarjeta.add(String.valueOf(valorTarj));
                                                            preciosfacturasLogicaTarjeta.add(String.valueOf(0));

                                                        }
                                                    }

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

                                        DataBaseBO.eliminarRecaudos(idPagoTarjeta);

                                        DataBaseBO.guardarFormaPagParcial(idTarjetaPago, claseDocumento, sociedad,
                                                cod_cliente,
                                                finalCod_Vendedor,
                                                "0", fechasDocumentos, fecha, precios, 0,
                                                moneda, preciosfacturasLogicaTarjeta,
                                                preciosParcialTarjeta, cuentaBancariaTarjeta,
                                                moneda_Consig, NCF_Comprobante_fiscal,
                                                documentosFinanciero, consecutivo1,
                                                descripcion, "O", usuario, operacion_Cme, sincronizado, "0",
                                                "0", nombrePropietarioTarjeta, fotoTarjeta, finalConsecutivoidTarjeta, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "");


                                    }

                                    //BITCOIN


                                    Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                } else {
                                    Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();
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
                    }
                }

                if (facCollection == null) {
                    if (valor != 0 && !fecha.equals("")
                            && !descripcion.equals("") && !documentt.equals("") && !numCheque.equals("")) {

                        if (fotos == null) {
                            fotoID = DataBaseBO.cargarFotosSinDocumentosAsociados(documentosFinanciero.get(0));
                        }
                        if (fotos != null) {
                            fotoID = fotos.idenFoto;
                        }

                        if (anticipo != null) {
                            claseDocumento.add("DZ");
                            documentosFinancieroAnticipo.add(null);
                        }

                        if (facCollection == null) {

                            if (anticipo.estado == true) {
                                final String operacion_Cme = "A";

                                Parcial parcial = new Parcial();
                                parcial.parcial = true;
                                Gson gson33 = new Gson();
                                String jsonStringObject = gson33.toJson(parcial);
                                PreferencesParcial.guardarParcialSeleccionada(contexto.getApplicationContext(), jsonStringObject);

                                if (DataBaseBO.updateFormaPagoParcialAnticPend(finalIdsPagos, claseDocumento, sociedad,
                                        cod_cliente, finalCod_Vendedor,
                                        referenciaCheq, fecha_Consignacion, fecha, precios,
                                        moneda, precios,
                                        precios, cuentasBanco, moneda_Consig, NCF_Comprobante_fiscal,
                                        finalDocumentoFinanciero, consecutivo1,
                                        descripcion, via_Pago, usuario, operacion_Cme, sincronizado, spinBanco, numCheque,
                                        "0", fotoID, finalConsecutivoid)) {

                                    Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                } else {
                                    Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();
                                }
                            }

                            if (anticipo.estado == false) {
                                final String operacion_Cme = "X";

                                Parcial parcial = new Parcial();
                                parcial.parcial = true;
                                Gson gson33 = new Gson();
                                String jsonStringObject = gson33.toJson(parcial);
                                PreferencesParcial.guardarParcialSeleccionada(contexto.getApplicationContext(), jsonStringObject);

                                if (DataBaseBO.updateFormaPagoParcialAnticPend(finalIdsPagos, claseDocumento, sociedad,
                                        cod_cliente, finalCod_Vendedor,
                                        referenciaCheq, fecha_Consignacion, fecha, precios,
                                        moneda, precios,
                                        precios, cuentasBanco, moneda_Consig, NCF_Comprobante_fiscal,
                                        finalDocumentoFinanciero, consecutivo1,
                                        descripcion, via_Pago, usuario, operacion_Cme, sincronizado, spinBanco, numCheque,
                                        "0", fotoID, finalConsecutivoid)) {

                                    Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                } else {
                                    Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();
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
