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

public class MetodoDePagoCheque {

    public static Dialog dialogo;
    public static Vector<Bancos> listaParametrosBancosSpinner;
    public static Vector<CuentasBanco> listaParametrosCuentas;

    /**
     * VISTA DIALOGO CHEQUE
     *
     * @param contexto
     * @param titulo
     * @param texto
     * @param onClickListenerAceptar
     * @param onClickListenerCancelar
     */
    public static void vistaDialogoCheque(final Context contexto,
                                          @NonNull String titulo,
                                          @NonNull String texto, View.OnClickListener onClickListenerAceptar,
                                          View.OnClickListener onClickListenerCancelar) {

        ImageView cancelarFormaPagoFE, guardarFormaPagoFE, guardarFormaPagoPendienteChe;
        final TextView tvFechaFragEfec, tvDescripcionCheque, tvNumeroCheque, simboloCheq;
        final EditText tvValorFragEfec;
        final TextView tvReferenciaCheque, tvNombreCuenta, tvCuentaOrigen, tvCuentaDestino, tvMonto;
        final Cartera facturaCartera;
        final FormaPago formaPago;
        final Anticipo anticipo;
        List<Facturas> listaFacturas2;
        List<Facturas> listaFacturas4;
        final List<Facturas> listsaFacturasParcialTotal;
        final List<Facturas> listsaFacturasParcialTotalPendientes;
        final List<Facturas> listsaFacturasParcialTotal2;
        final ClienteSincronizado clienteSel;
        final Button tomarFoto;
        final Lenguaje lenguajeElegido;
        final TextView txtCompaReciboDinero, txtCompaReciboDinero1, txtCompaReciboDinero2, tituloFechaCheque,
                tituloNumCheque, tituloMontoCheque, tituloReferenciaCheque, tituloNombreCuentaCheque, tituloCuentaOrigenCheque, tituloCuentaDestinoCheque;


        dialogo = new Dialog(contexto);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.fragment_cheque);

        Gson gson223 = new Gson();
        String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(contexto);
        lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);


        //Calendario para obtener fecha
        final String CERO = "0";
        final String BARRA = "-";
        final Calendar c = Calendar.getInstance();

        //Fecha
        final int mes = c.get(Calendar.MONTH);
        final int dia = c.get(Calendar.DAY_OF_MONTH);
        final int anio = c.get(Calendar.YEAR);
        String empresa = "";

        tvValorFragEfec = dialogo.findViewById(R.id.tvMontoCheque);
        tvFechaFragEfec = dialogo.findViewById(R.id.tvFechaFragEfec);
        //  tvDescripcionCheque = dialogo.findViewById(R.id.tvDescripcionCheque);
        tvReferenciaCheque = dialogo.findViewById(R.id.tvReferenciaCheque);
        tvCuentaDestino = dialogo.findViewById(R.id.tvCuentaDestino);
        tvMonto = dialogo.findViewById(R.id.tvMonto);
        guardarFormaPagoFE = dialogo.findViewById(R.id.guardarFormaPagoFE);
        guardarFormaPagoPendienteChe = dialogo.findViewById(R.id.guardarFormaPagoPendienteChe);
        tvNumeroCheque = dialogo.findViewById(R.id.tvNumeroCheque);
        simboloCheq = dialogo.findViewById(R.id.simboloCheq);
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
        tipoUsuario = DataBaseBO.cargarTipoUsuarioApp(contexto);

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


        empresa = DataBaseBO.cargarEmpresa(contexto);

        final Spinner spinnerBanco = dialogo.findViewById(R.id.spinnerBancoCheq);
        spinnerBanco.setVisibility(View.VISIBLE);

        String[] items;
        Vector<String> listaItems = new Vector<String>();

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                listaItems.addElement("Select");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                listaItems.addElement("Seleccione");

            }
        }

        listaParametrosBancosSpinner = DataBaseBO.cargarTipoBancos(listaItems, contexto);

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

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                listaItems2.addElement("Select");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                listaItems2.addElement("Seleccione");

            }
        }


        String parametro = spinnerBanco.getSelectedItem().toString();
        listaParametrosCuentas = DataBaseBO.cargarCuentasBancos(listaItems2, parametro, contexto);

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

        spinnerBanco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                for (int j = 0; j < spinnerBanco.getCount(); j++) {

                    String banco = "";
                    banco = spinnerBanco.getSelectedItem().toString();
                    String parametro;
                    parametro = DataBaseBO.cargarCodigobanco(banco, contexto);
                    String[] items2;
                    Vector<String> listaItems2 = new Vector<String>();
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            listaItems2.addElement("Select");

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            listaItems2.addElement("Seleccione");

                        }
                    }

                    listaParametrosCuentas = DataBaseBO.cargarCuentasBancos(listaItems2, DataBaseBO.cargarCodigobanco(banco, contexto), contexto);

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
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
            simboloCheq.setText("$");
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

                }, anio, mes, dia);
                recogerFecha.show();
                recogerFecha.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
            }
        });


        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesClienteSeleccionado.obtenerClienteSeleccionado(contexto);
        clienteSel = gson1.fromJson(stringJsonObject1, ClienteSincronizado.class);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesFormaPago.obteneFacturaSeleccionada(contexto);
        formaPago = gson2.fromJson(stringJsonObject2, FormaPago.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

        if(empresa.equals("AGUC"))
        {
            (dialogo.findViewById(R.id.lienarChequeReferencia)).setVisibility(View.GONE);
        }

        if (anticipo != null) {
            if (anticipo.estado == true) {
                LinearLayout lienarChequeReferencia = dialogo.findViewById(R.id.lienarChequeReferencia);
                if(!empresa.equals("AGUC"))
                    lienarChequeReferencia.setVisibility(View.VISIBLE);
                guardarFormaPagoFE.setVisibility(View.GONE);
            } else if (anticipo.estado == false) {
                LinearLayout lienarChequeReferencia = dialogo.findViewById(R.id.lienarChequeReferencia);
                if(!empresa.equals("AGUC"))
                    lienarChequeReferencia.setVisibility(View.VISIBLE);
                guardarFormaPagoFE.setVisibility(View.GONE);
            }
        }

        Gson gson = new Gson();
        List<Cartera> carteraS = new ArrayList<>();
        Type collectionType = new TypeToken<Collection<Cartera>>() {
        }.getType();
        String stringJsonObject222 = PreferencesCartera.obteneCarteraSeleccionada(contexto);


        final Collection<Cartera> facCollection = gson.fromJson(stringJsonObject222, collectionType);

        final List<String> claseDocumento = new ArrayList<>();
        final List<String> documentosFinanciero = new ArrayList<>();
        final List<String> valoresfacturas = new ArrayList<>();
        final List<String> preciosAcomparar = new ArrayList<>();
        final List<String> totalFacturas = new ArrayList<>();
        final List<String> precios = new ArrayList<>();
        final List<String> fechaConsignacionesAntiPend = new ArrayList<>();
        final List<String> preciosParcial = new ArrayList<>();
        final List<String> preciosAnticipo = new ArrayList<>();
        final List<String> documentt = new ArrayList<>();
        final List<Facturas> listaFacturas3 = new ArrayList<>();
        final List<String> preciosComparar = new ArrayList<>();
        final List<String> preciosfacturasLogica = new ArrayList<>();
        final List<String> fechasDocumentos = new ArrayList<>();
        final List<String> listaPrecios = new ArrayList<>();
        final List<String> listaNumeroRecibos = new ArrayList<>();
        final List<String> vendedoresCartera = new ArrayList<>();
        final List<String> preciosListaTotal = new ArrayList<>();

        String claseDocument = "";
        String preciosFacturas = "";
        String documentoFinanciero = "";
        String nombreU = "";
        String fechasDocus = "";
        double precioTotal = 0;
        String document = "";
        String vendedorCartera = "";

        if (facCollection != null) {

            for (Cartera cartera1 : facCollection) {
                vendedorCartera = cartera1.getVendedor();
                vendedoresCartera.add(vendedorCartera);
                document = cartera1.getDocumento();
                precioTotal += cartera1.getSaldo();
                fechasDocus = cartera1.getFechaVencto();
                documentoFinanciero = cartera1.getDocumentoFinanciero();
                claseDocument = cartera1.getConcepto();
                fechasDocumentos.add(cartera1.getFechaVencto());
                int Position = 2;
                claseDocument = claseDocument.substring(0, Position);
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
        //   listsaFacturasParcialTotal = DataBaseBO.cargarFacParTotal(nroReciboFacTotalPar, documentosFinanciero);
        listsaFacturasParcialTotal = Utilidades.listaFacturasParcialTotal(contexto);

        for (Facturas fac : listsaFacturasParcialTotal) {
            String acert = "";
            acert = fac.idPago;
            double valorCom = 0;
            valorCom = fac.valor;
            valoresfacturas.add(String.valueOf(Utilidades.formatearDecimales(valorCom, 2)));

        }

        String monedaTipo = "";
        String consecutivo = "";
        String consecutivofinal = "";
        int consecutivoInicial = 0;
        String consecutivoNegocio = "";
        String consecutivoVendedor = "";
        String codigoVendedor = "";
        double DiferenciaFormasPago;
        codigoVendedor = DataBaseBO.cargarCodigo(contexto);

        monedaTipo = DataBaseBO.cargarMoneda(contexto);
        consecutivo = DataBaseBO.cargarConsecutivo(contexto);
        consecutivoNegocio = DataBaseBO.cargarNegocioConsecutivo(contexto);
        consecutivoVendedor = DataBaseBO.cargarVendedorConsecutivo(contexto);


        double DiferenciaFormasPagoE = 0;
        double DiferenciaFormasPagoPEN = 0;

        String nroRecibo = "";
        if (anticipo != null) {

            String parametroCme = "A";

            nroRecibo = clienteSel.consecutivo;
            documentosFinanciero.add(nroRecibo);

        }

        DiferenciaFormasPagoE = Utilidades.totalFormasPago(contexto);
        DiferenciaFormasPago = (DiferenciaFormasPagoE);

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

        tvReferenciaCheque.setText("");

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
        fechaConsignacionesAntiPend.add(Utilidades.fechaActual("yyyy-MM-dd"));
        final String moneda = monedaTipo;
        final String valor_Consignado = "";
        final String bancos = null;
        final String cuenta_Bancaria = null;
        final String moneda_Consig = monedaTipo;
        final String NCF_Comprobante_fiscal = null;
        final String docto_Financiero = null;
        final String consecutivo1 = consecutivofinal;
        final int consecutivo2 = consecutivoInicial;
        final String finalValor = String.valueOf(Double.parseDouble(String.valueOf(valorfinal)));
        final String obserbaciones = "";
        final String via_Pago = "B";
        final String usuario = codigoVendedor;
        final String operacion_Cme = null;
        final int sincronizado = 0;
        int Position = 2;
        codigoVendedor = codigoVendedor.substring(0, Position);
        final String idPago = codigoVendedor + Utilidades.fechaActual("ddHHmmss");
        final double finalValorfinal = valorfinal;
        int numero = (int) (Math.random() * 1000) + 1;
        final String idFoto = idPago + "_ID_" + numero;
        final String nombreFoto = idPago + numero + ".jpg";
        //Actualizada 2
        final String idenFoto = codigoVendedor + Utilidades.fechaActual("HHmmss") + 1;

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
                MetodoDePagoCheque.dialogo.onSaveInstanceState();


            }
        });

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

                } while (cantidadFacturasPraciales != listsaFacturasParcialTotal.size());
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

        double finalComparar = Utilidades.formatearDecimales(comparar, 2);

        String finalCod_Vendedor = cod_Vendedor;
        String finalTipoUsuario = tipoUsuario;

        guardarFormaPagoPendienteChe.setOnClickListener(new View.OnClickListener() {
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
                double totalesValoresParciales = 0;
                String input = "";
                String fecha = "";
                String descripcion = "";
                String referenciaCheq = "";
                String cuentaDestino = "";
                double valor = 0;
                String numCheque = "";
                String nombrePropietario = "";
                String cuentasBanco = "";
                double totalbotenido = 0;
                String spinBanco = "";
                double resultadosValores = 0;
                String fotoID = "";


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

                            input = tvValorFragEfec.getText().toString();
                            fecha = tvFechaFragEfec.getText().toString();
                            descripcion = anticipo.getObservaciones();
                            referenciaCheq = tvReferenciaCheque.getText().toString();
                            numCheque = tvNumeroCheque.getText().toString();
                            cuentasBanco = null;
                            spinBanco = null;

                        } else if (anticipo.estado == false) {

                            input = tvValorFragEfec.getText().toString();
                            fecha = tvFechaFragEfec.getText().toString();
                            descripcion = descripcion = anticipo.getObservaciones();
                            referenciaCheq = tvReferenciaCheque.getText().toString();
                            numCheque = tvNumeroCheque.getText().toString();
                            cuentasBanco = null;
                            spinBanco = null;
                        }
                    }
                }

                Gson gsonFotos = new Gson();
                String stringJsonObjectFotos = PreferencesFotos.obteneFotoSeleccionada(contexto);
                Fotos fotos = gsonFotos.fromJson(stringJsonObjectFotos, Fotos.class);

                double valorConvertido = 0;

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
                double valorFLEX = 0;
                if (formaPago != null) {
                    valorFLEX = formaPago.getValor();
                    valorFLEX = Utilidades.formatearDecimales(valorFLEX, 2);

                }


                if (anticipo != null) {
                    valorFLEX = anticipo.getValor();
                    valorFLEX = Utilidades.formatearDecimales(valorFLEX, 2);
                }
                if (valorConvertido <= Utilidades.formatearDecimales(valorFLEX-sumaXValorConsignado,2)) {

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


                        if (facCollection != null) {


                            //Actualizada total 28
                            if (listsaFacturasParcialTotal.size() == 0) {
                                try {
                                    if (formaPago.parcial == true) {

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

                                                        if (!DataBaseBO.ExisteDocumento(documentosFinanciero, contexto)) {
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


                    }


                }

                else if (valorConvertido > Utilidades.formatearDecimales(valorFLEX-sumaXValorConsignado,2)) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "The value earned is greater than the amount to be collected..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El valor ingresado es mayor al monto a recaudar..", Toasty.LENGTH_SHORT).show();

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

                        if (descripcion.equals("")) {
                            Toasty.warning(contexto, "The description field cannot be blank...", Toasty.LENGTH_SHORT).show();
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

                        if (descripcion.equals("")) {
                            Toasty.warning(contexto, "El campo de la descripcion no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                        }

                    }
                }


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
                        if (valor != 0 && !fecha.equals("")
                                && !descripcion.equals("") && !documentt.equals("")
                                && !numCheque.equals("")) {

                            gsonFotos = new Gson();
                            stringJsonObjectFotos = PreferencesFotos.obteneFotoSeleccionada(contexto);
                            fotos = gsonFotos.fromJson(stringJsonObjectFotos, Fotos.class);

                            if (fotos == null) {
                                fotoID = null;
                            }
                            if (fotos != null) {
                                fotoID = fotos.idenFoto;
                            }

                            if (finalEmpresa.equals("AGUC"))
                            {
                                if(fotoID == null)
                                {
                                    if (lenguajeElegido == null) {

                                    } else if (lenguajeElegido != null) {
                                        if (lenguajeElegido.lenguaje.equals("USA")) {

                                            Toasty.warning(contexto, "Attaching images or taking photo collection is mandatory..", Toasty.LENGTH_LONG).show();


                                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                            Toasty.warning(contexto, "El adjuntar imagenes o tomar foto del recaudo es obligatoria..", Toasty.LENGTH_LONG).show();

                                        }
                                    }
                                    return;
                                }
                            }

                            if (facCollection != null) {

                                try {
                                    if (formaPago.parcial == true) {

                                        final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                                        String consecId1 = "", numeroAnulacionId1 = "";
                                        String negocioId1 = "";
                                        String vendedorId1 = "";
                                        String consecutivoid = "";

                                        consecId1 = DataBaseBO.cargarConsecutivoId(contexto);
                                        negocioId1 = DataBaseBO.cargarNegocioConsecutivoId(contexto);
                                        vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId(contexto);

                                        int consec1Id = Integer.parseInt(consecId1);
                                        int vendedorsumId = Integer.parseInt(vendedorId1);
                                        int contadorId = 1;
                                        consec1Id = consec1Id + contadorId;
                                        numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                                        DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon, contexto);
                                        consecutivoid = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                                        if (finalTipoUsuario.equals("10")) {
                                            if (DataBaseBO.guardarFormaPagParcialPendienteTipoCobrador(idPago, claseDocumento,
                                                    sociedad, cod_cliente, vendedoresCartera,
                                                    referenciaCheq, fechasDocumentos,
                                                    fecha, precios,
                                                    moneda, preciosfacturasLogica, preciosParcial, cuentasBanco,
                                                    moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                                    consecutivo1,
                                                    descripcion, via_Pago, usuario, operacion_Cme,
                                                    sincronizado, spinBanco, numCheque,
                                                    "0", fotoID, consecutivoid, formaPago != null ? formaPago.observacionesMotivo : "", contexto)) {


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

                                            if (formaPago.valor == DiferenciaFormasPago + valor) {


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

                                                if (DataBaseBO.guardarFormaPagParcialPendiente(idPago, claseDocumento, sociedad, cod_cliente,
                                                        finalCod_Vendedor, referenciaCheq, fechasDocumentos, fecha,
                                                        precios,
                                                        moneda, preciosfacturasLogica,
                                                        preciosParcial, resultadoAfavor, cuentasBanco, moneda_Consig, NCF_Comprobante_fiscal,
                                                        documentosFinanciero, consecutivo1,
                                                        descripcion, via_Pago, usuario, operacion_Cme,
                                                        sincronizado, spinBanco, numCheque, "0", fotoID, consecutivoid,consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto)) {

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

                                                if (DataBaseBO.guardarFormaPagParcialPendiente(idPago, claseDocumento, sociedad, cod_cliente,
                                                        finalCod_Vendedor, referenciaCheq, fechasDocumentos, fecha,
                                                        precios,
                                                        moneda, preciosfacturasLogica,
                                                        preciosParcial, AfavorResultado, cuentasBanco, moneda_Consig, NCF_Comprobante_fiscal,
                                                        documentosFinanciero, consecutivo1,
                                                        descripcion, via_Pago, usuario, operacion_Cme,
                                                        sincronizado, spinBanco, numCheque, "0", fotoID, consecutivoid,consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto)) {

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

                                        final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                                        String consecId1 = "", numeroAnulacionId1 = "";
                                        String negocioId1 = "";
                                        String vendedorId1 = "";
                                        String consecutivoid = "";

                                        consecId1 = DataBaseBO.cargarConsecutivoId(contexto);
                                        negocioId1 = DataBaseBO.cargarNegocioConsecutivoId(contexto);
                                        vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId(contexto);

                                        int consec1Id = Integer.parseInt(consecId1);
                                        int vendedorsumId = Integer.parseInt(vendedorId1);
                                        int contadorId = 1;
                                        consec1Id = consec1Id + contadorId;
                                        numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                                        DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon, contexto);
                                        consecutivoid = String.valueOf(negocioId1 + vendedorsumId + consec1Id);


                                        if (finalTipoUsuario.equals("10")) {
                                            if (DataBaseBO.guardarFormaPagParcialPendienteTipoCobrador(idPago, claseDocumento,
                                                    sociedad, cod_cliente, vendedoresCartera,
                                                    referenciaCheq, fechasDocumentos,
                                                    fecha, precios,
                                                    moneda, preciosfacturasLogica, preciosParcial, cuentasBanco,
                                                    moneda_Consig, NCF_Comprobante_fiscal, documentosFinanciero,
                                                    consecutivo1,
                                                    descripcion, via_Pago, usuario, operacion_Cme,
                                                    sincronizado, spinBanco, numCheque,
                                                    "0", fotoID, consecutivoid, formaPago != null ? formaPago.observacionesMotivo : "", contexto)) {

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

                                            if (DataBaseBO.guardarFormaPagParcialPendiente(idPago, claseDocumento, sociedad, cod_cliente,
                                                    finalCod_Vendedor, referenciaCheq, fechasDocumentos, fecha,
                                                    precios,
                                                    moneda, preciosfacturasLogica,
                                                    preciosParcial, 0, cuentasBanco, moneda_Consig, NCF_Comprobante_fiscal,
                                                    documentosFinanciero, consecutivo1,
                                                    descripcion, via_Pago, usuario, operacion_Cme,
                                                    sincronizado, spinBanco, numCheque,
                                                    "0", fotoID, consecutivoid,consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto)) {

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
                        }
                    }

                    if (facCollection == null) {
                        if (valor != 0 && !fecha.equals("")
                                && !descripcion.equals("") && !documentt.equals("")
                                && !numCheque.equals("")) {

                            gsonFotos = new Gson();
                            stringJsonObjectFotos = PreferencesFotos.obteneFotoSeleccionada(contexto);
                            fotos = gsonFotos.fromJson(stringJsonObjectFotos, Fotos.class);

                            if (fotos == null) {
                                fotoID = null;
                            }
                            if (fotos != null) {
                                fotoID = fotos.idenFoto;
                            }

                            if (finalEmpresa.equals("AGUC"))
                            {
                                if(fotoID == null)
                                {
                                    if (lenguajeElegido == null) {

                                    } else if (lenguajeElegido != null) {
                                        if (lenguajeElegido.lenguaje.equals("USA")) {

                                            Toasty.warning(contexto, "Attaching images or taking photo collection is mandatory..", Toasty.LENGTH_LONG).show();


                                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                            Toasty.warning(contexto, "El adjuntar imagenes o tomar foto del recaudo es obligatoria..", Toasty.LENGTH_LONG).show();

                                        }
                                    }
                                    return;
                                }
                            }

                            if (facCollection == null) {

                                if (anticipo.estado == true) {
                                    final String operacion_Cme = "A";

                                    Parcial parcial = new Parcial();
                                    parcial.parcial = true;
                                    Gson gson33 = new Gson();
                                    String jsonStringObject = gson33.toJson(parcial);
                                    PreferencesParcial.guardarParcialSeleccionada(contexto.getApplicationContext(), jsonStringObject);

                                    final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                                    String consecId1 = "", numeroAnulacionId1 = "";
                                    String negocioId1 = "";
                                    String vendedorId1 = "";
                                    String consecutivoid = "";

                                    consecId1 = DataBaseBO.cargarConsecutivoId(contexto);
                                    negocioId1 = DataBaseBO.cargarNegocioConsecutivoId(contexto);
                                    vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId(contexto);

                                    int consec1Id = Integer.parseInt(consecId1);
                                    int vendedorsumId = Integer.parseInt(vendedorId1);
                                    int contadorId = 1;
                                    consec1Id = consec1Id + contadorId;
                                    numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                                    DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon, contexto);
                                    consecutivoid = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                                    String spBanco = spinnerBanco.getSelectedItemPosition() == 0 ? "" : spinnerBanco.getSelectedItem().toString();
                                    String spCuenta = spinnerCuentasBanco.getSelectedItemPosition() == 0 ? "" : spinnerCuentasBanco.getSelectedItem().toString();

                                    if (DataBaseBO.guardarFormaPagParcialPendiente(idPago, claseDocumento, sociedad, cod_cliente,
                                            finalCod_Vendedor, referenciaCheq, fechaConsignacionesAntiPend, fecha,
                                            preciosAnticipo,
                                            moneda, precios,
                                            precios, 0, spCuenta, moneda_Consig, NCF_Comprobante_fiscal,
                                            documentosFinanciero, consecutivo1,
                                            descripcion, via_Pago, usuario, operacion_Cme,
                                            sincronizado, spBanco, numCheque,
                                            "0", fotoID, consecutivoid, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto)) {

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


                                    final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                                    String consecId1 = "", numeroAnulacionId1 = "";
                                    String negocioId1 = "";
                                    String vendedorId1 = "";
                                    String consecutivoid = "";

                                    consecId1 = DataBaseBO.cargarConsecutivoId(contexto);
                                    negocioId1 = DataBaseBO.cargarNegocioConsecutivoId(contexto);
                                    vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId(contexto);

                                    int consec1Id = Integer.parseInt(consecId1);
                                    int vendedorsumId = Integer.parseInt(vendedorId1);
                                    int contadorId = 1;
                                    consec1Id = consec1Id + contadorId;
                                    numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                                    DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon, contexto);
                                    consecutivoid = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                                    String spBanco = spinnerBanco.getSelectedItemPosition() == 0 ? "" : spinnerBanco.getSelectedItem().toString();
                                    String spCuenta = spinnerCuentasBanco.getSelectedItemPosition() == 0 ? "" : spinnerCuentasBanco.getSelectedItem().toString();

                                    if (DataBaseBO.guardarFormaPagParcialPendiente(idPago, claseDocumento, sociedad, cod_cliente,
                                            finalCod_Vendedor, referenciaCheq, fechaConsignacionesAntiPend, fecha,
                                            preciosAnticipo,
                                            moneda, precios,
                                            precios, 0, spCuenta, moneda_Consig, NCF_Comprobante_fiscal,
                                            documentosFinanciero, consecutivo1,
                                            descripcion, via_Pago, usuario, operacion_Cme,
                                            sincronizado, spBanco, numCheque,
                                            "0", fotoID, consecutivoid,consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto)) {

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
                            Intent login = new Intent(contexto.getApplicationContext(), MetodosDePagoActivity.class);
                            contexto.startActivity(login);
                            ((MetodosDePagoActivity) contexto).finish();
                            dialogo.dismiss();
                            dialogo.cancel();
                        }
                    }

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
        });

        String finalCod_Vendedor1 = cod_Vendedor;
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
                String input = "";
                String fecha = "";
                String descripcion = "";
                String referenciaCheq = "";
                String cuentaDestino = "";
                double valor = 0;
                String numCheque = "";
                String nombrePropietario = "";
                String cuentasBanco = "";
                double totalbotenido = 0;
                String spinBanco = "";
                double totalesValoresParciales = 0;
                double resultadosValores = 0;
                String fotoID = "";

                final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                String consecId1 = "", numeroAnulacionId1 = "";
                String negocioId1 = "";
                String vendedorId1 = "";
                String consecutivoid = "";

                consecId1 = DataBaseBO.cargarConsecutivoId(contexto);
                negocioId1 = DataBaseBO.cargarNegocioConsecutivoId(contexto);
                vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId(contexto);

                int consec1Id = Integer.parseInt(consecId1);
                int vendedorsumId = Integer.parseInt(vendedorId1);
                int contadorId = 1;
                consec1Id = consec1Id + contadorId;
                numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon, contexto);
                consecutivoid = String.valueOf(negocioId1 + vendedorsumId + consec1Id);


                String finalConsecutivoid = consecutivoid;


                Gson gsonFotos = new Gson();
                String stringJsonObjectFotos = PreferencesFotos.obteneFotoSeleccionada(contexto);
                Fotos fotos = gsonFotos.fromJson(stringJsonObjectFotos, Fotos.class);

                if (facCollection != null) {
                    fecha = tvFechaFragEfec.getText().toString();
                    descripcion = formaPago.getObservaciones();
                    referenciaCheq = tvReferenciaCheque.getText().toString();
                    cuentaDestino = tvCuentaDestino.getText().toString();
                    numCheque = tvNumeroCheque.getText().toString();
                    nombrePropietario = "";
                    valor = 0;
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
                            valor = 0;
                            input = tvValorFragEfec.getText().toString();
                            referenciaCheq = tvReferenciaCheque.getText().toString();
                            cuentasBanco = null;
                            spinBanco = null;

                        } else if (anticipo.estado == false) {
                            fecha = tvFechaFragEfec.getText().toString();
                            descripcion = anticipo.getObservaciones();
                            cuentaDestino = tvCuentaDestino.getText().toString();
                            numCheque = tvNumeroCheque.getText().toString();
                            nombrePropietario = "";
                            valor = 0;
                            input = tvValorFragEfec.getText().toString();
                            referenciaCheq = tvReferenciaCheque.getText().toString();
                            cuentasBanco = null;
                            spinBanco = null;

                        }
                    }
                }

//Actualizada total 27
                if (listsaFacturasParcialTotal.size() == 0) {
                    try {
                        if (formaPago.parcial == true) {

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
                                                input = input.replace(".", "");
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
                                                input = input.replace(".", "");
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


                                            double valorLista2 = Double.parseDouble(precios.get(j));
                                            totalesValoresLista = String.valueOf(Utilidades.formatearDecimales(valorLista2 - valorLista, 2));
                                            totalFacturas.add(totalesValoresLista);


                                        }

                                    }

                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));


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
                                                input = input.replace(".", "");
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
                                    valorobtenidoSegundo = Double.parseDouble(totalFacturas.get(i));


                                    if (valorobtenidoSegundo < 0) {

                                        preciosParcial.add(String.valueOf(valor));
                                        preciosfacturasLogica.add(String.valueOf(0));

                                    } else if (valorobtenidoSegundo > 0) {

                                        if (nuevoValor < 0) {

                                            preciosParcial.add(String.valueOf(valor));
                                            preciosfacturasLogica.add(String.valueOf(0));

                                        } else if (nuevoValor == 0) {

                                            nuevoValor2 = nuevoValor;
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
                                double valorobtenido = 0;
                                double valorobtenidoSegundo = 0;
                                preciosFacturasParcial.valorobtenido = valorobtenido;
                                preciosFacturasParcial.valor = valor;
                                String acert = "";
                                double valorTotalParcial = 0;
                                double valorCom = 0;
                                double valorObtenidoFac = 0;
                                String totalesValoresLista = "";

                                if( i == 0)
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
                                                input = input.replace(".", "");
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


                                Gson gson33 = new Gson();
                                String jsonStringObject = gson33.toJson(preciosParcial.get(i));
                                PreferencesParcial.guardarParcialSeleccionada(contexto, jsonStringObject);

                            }

                        }

                    } catch (Exception exception) {
                        System.out.println("Error en la forma de pago parcial " + exception);
                    }
                }
/// fac para anticipo 2
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
                        Toasty.warning(contexto, "El campo del monto no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                    }

                    precios.add(input);

                }

                if (valor == 0) {
                    Toasty.warning(contexto, "El campo del monto no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                }


                if (numCheque.equals("")) {
                    Toasty.warning(contexto, "El campo del numero del cheque no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                }

                if (spinnerCuentasBanco.getSelectedItem().toString().equals("Seleccione")) {
                    Toasty.warning(contexto, "La cuenta destino  no puede quedar sin seleccion..", Toasty.LENGTH_SHORT).show();
                }

                if (spinnerBanco.getSelectedItem().toString().equals("Seleccione")) {
                    Toasty.warning(contexto, "El nombre de la cuenta no puede quedar sin seleccion..", Toasty.LENGTH_SHORT).show();
                }

                if (fecha.equals("")) {
                    Toasty.warning(contexto, "El campo de fecha no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                }

                if (descripcion.equals("")) {
                    Toasty.warning(contexto, "El campo de la descripcion no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                }

                if (facCollection != null) {
                    if (referenciaCheq.equals("")) {
                        Toasty.warning(contexto, "El campo de la referencia no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
                    }
                }

                if (cuentaDestino.equals("")) {

                    Toasty.warning(contexto, "El campo de la cuenta origen no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                }


                if (facCollection != null) {
                    if (valor != 0 && !fecha.equals("")
                            && !descripcion.equals("") && !documentt.equals("") && !cuentaDestino.equals("")
                            && !numCheque.equals("") && !spinnerCuentasBanco.getSelectedItem().toString().equals("Seleccione")
                            && !spinnerBanco.getSelectedItem().toString().equals("Seleccione")) {


                        if (fotos == null) {
                            fotoID = null;
                        }
                        if (fotos != null) {
                            fotoID = fotos.idenFoto;
                        }

                        if (finalEmpresa.equals("AGUC"))
                        {
                            if(fotoID == null)
                            {
                                if (lenguajeElegido == null) {

                                } else if (lenguajeElegido != null) {
                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                        Toasty.warning(contexto, "Attaching images or taking photo collection is mandatory..", Toasty.LENGTH_LONG).show();


                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                        Toasty.warning(contexto, "El adjuntar imagenes o tomar foto del recaudo es obligatoria..", Toasty.LENGTH_LONG).show();

                                    }
                                }
                                return;
                            }
                        }

                        if (facCollection != null) {

                            try {
                                if (formaPago.parcial == true) {

                                    if (DataBaseBO.guardarFormaPagParcial(idPago, claseDocumento, sociedad, cod_cliente,
                                            finalCod_Vendedor1, referenciaCheq, fechasDocumentos, fecha_Consignacion,
                                            precios, 0,
                                            moneda, preciosfacturasLogica,
                                            preciosParcial, cuentasBanco, moneda_Consig, NCF_Comprobante_fiscal,
                                            documentosFinanciero, consecutivo1,
                                            descripcion, via_Pago, usuario, operacion_Cme,
                                            sincronizado, spinBanco, numCheque, "0", fotoID, finalConsecutivoid, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto)) {

                                        Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                    } else {
                                        Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();
                                    }
                                }

                                if (formaPago.parcial == false) {
                                    if (DataBaseBO.guardarFormaPagParcial(idPago, claseDocumento, sociedad, cod_cliente,
                                            finalCod_Vendedor1, referenciaCheq, fechasDocumentos, fecha_Consignacion,
                                            precios, 0,
                                            moneda, preciosfacturasLogica,
                                            preciosParcial, cuentasBanco, moneda_Consig, NCF_Comprobante_fiscal,
                                            documentosFinanciero, consecutivo1,
                                            descripcion, via_Pago, usuario, operacion_Cme,
                                            sincronizado, spinBanco, numCheque,
                                            "0", fotoID, finalConsecutivoid, consecutivo2, formaPago != null ? formaPago.observacionesMotivo : "", contexto)) {

                                        Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                    } else {
                                        Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception exception) {
                                System.out.println("Error al guardar en la forma de pago total " + exception);
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
                            && !descripcion.equals("") && !documentt.equals("") && !cuentaDestino.equals("")
                            && !numCheque.equals("")
                            && !spinnerCuentasBanco.getSelectedItem().toString().equals("Seleccione")
                            && !spinnerBanco.getSelectedItem().toString().equals("Seleccione")) {

                        if (fotos == null) {
                            fotoID = null;
                        }
                        if (fotos != null) {
                            fotoID = fotos.idenFoto;
                        }

                        if (finalEmpresa.equals("AGUC"))
                        {
                            if(fotoID == null)
                            {
                                if (lenguajeElegido == null) {

                                } else if (lenguajeElegido != null) {
                                    if (lenguajeElegido.lenguaje.equals("USA")) {

                                        Toasty.warning(contexto, "Attaching images or taking photo collection is mandatory..", Toasty.LENGTH_LONG).show();


                                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                        Toasty.warning(contexto, "El adjuntar imagenes o tomar foto del recaudo es obligatoria..", Toasty.LENGTH_LONG).show();

                                    }
                                }
                                return;
                            }
                        }

                        if (facCollection == null) {

                            if (anticipo.estado == true) {
                                final String operacion_Cme = "A";

                                Parcial parcial = new Parcial();
                                parcial.parcial = true;
                                Gson gson33 = new Gson();
                                String jsonStringObject = gson33.toJson(parcial);
                                PreferencesParcial.guardarParcialSeleccionada(contexto.getApplicationContext(), jsonStringObject);

                                String spBanco = spinnerBanco.getSelectedItemPosition() == 0 ? "" : spinnerBanco.getSelectedItem().toString();
                                String spCuenta = spinnerCuentasBanco.getSelectedItemPosition() == 0 ? "" : spinnerCuentasBanco.getSelectedItem().toString();

                                if (DataBaseBO.guardarFormaPago(idPago, claseDocumento, sociedad, cod_cliente,
                                        finalCod_Vendedor1, referenciaCheq, fecha_Consignacion, fecha_Consignacion,
                                        preciosAnticipo,
                                        moneda, precios,
                                        valor, spCuenta, moneda_Consig, NCF_Comprobante_fiscal,
                                        documentosFinanciero, consecutivo1,
                                        descripcion, via_Pago, usuario, operacion_Cme,
                                        sincronizado, spBanco, numCheque,
                                        "0", fotoID, finalConsecutivoid, consecutivo2, contexto)) {

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

                                String spCuenta = spinnerCuentasBanco.getSelectedItemPosition() == 0 ? "" : spinnerCuentasBanco.getSelectedItem().toString();

                                if (DataBaseBO.guardarFormaPago(idPago, claseDocumento, sociedad, cod_cliente,
                                        finalCod_Vendedor1, referenciaCheq, fecha_Consignacion, fecha_Consignacion,
                                        preciosAnticipo,
                                        moneda, precios,
                                        valor, spCuenta, moneda_Consig, NCF_Comprobante_fiscal,
                                        documentosFinanciero, consecutivo1,
                                        descripcion, via_Pago, usuario, operacion_Cme,
                                        sincronizado, spinnerBanco.getSelectedItem().toString(), numCheque,
                                        "0", fotoID, finalConsecutivoid, consecutivo2, contexto)) {

                                    Toasty.warning(contexto, "El registro fue almacenado correctamente.", Toasty.LENGTH_SHORT).show();

                                } else {
                                    Toasty.warning(contexto, "No se pudo almacenar el registro.", Toasty.LENGTH_SHORT).show();
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
