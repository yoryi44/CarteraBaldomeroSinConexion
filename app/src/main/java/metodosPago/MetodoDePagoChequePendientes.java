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
import co.com.celuweb.carterabaldomero.MetodosDePagoPendientesActivity;
import co.com.celuweb.carterabaldomero.MultiplesFotosActivity;
import co.com.celuweb.carterabaldomero.R;
import dataobject.Anticipo;
import dataobject.Bancos;
import dataobject.Cartera;
import dataobject.ClienteSincronizado;
import dataobject.CuentasBanco;
import dataobject.FormaPago;
import dataobject.Fotos;
import dataobject.Lenguaje;
import dataobject.MultiplesEstado;
import dataobject.Pendientes;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesFacturaPendientesSeleccionada;
import sharedpreferences.PreferencesFacturasMultiplesPendientes;
import sharedpreferences.PreferencesFacturasMultiplesPendientesVarias;
import sharedpreferences.PreferencesFotos;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesPendientesFacturas;
import utilidades.Utilidades;

public class MetodoDePagoChequePendientes {


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
        final Button tomarFoto;
        final TextView tvReferenciaCheque, tvNombreCuenta, tvCuentaOrigen, tvCuentaDestino, tvMonto;
        final Cartera facturaCartera;
        final FormaPago formaPago;
        final Anticipo anticipo;
        ClienteSincronizado clienteSel;
        final Lenguaje lenguajeElegido;
        final LinearLayout lnNumCheque;
        final TextView txtCompaReciboDinero, txtCompaReciboDinero1, txtCompaReciboDinero2, tituloFechaCheque,
                tituloNumCheque, tituloMontoCheque, tituloReferenciaCheque, tituloNombreCuentaCheque, tituloCuentaOrigenCheque, tituloCuentaDestinoCheque;


        dialogo = new Dialog(contexto);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.fragment_cheque_pendientes);

        final Pendientes pendientesSeleccionada;
        final MultiplesEstado multipleestado;
        String empresa = "";


        //Calendario para obtener fecha
        final String CERO = "0";
        final String BARRA = "-";
        final Calendar c = Calendar.getInstance();

        //Fecha
        final int mes = c.get(Calendar.MONTH);
        final int dia = c.get(Calendar.DAY_OF_MONTH);
        final int anio = c.get(Calendar.YEAR);

        Gson gson223 = new Gson();
        String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(contexto);
        lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);


        tvValorFragEfec = dialogo.findViewById(R.id.tvMontoCheque);
        tvFechaFragEfec = dialogo.findViewById(R.id.tvFechaFragEfec);
      //  tvDescripcionCheque = dialogo.findViewById(R.id.tvDescripcionCheque);
        tvReferenciaCheque = dialogo.findViewById(R.id.tvReferenciaCheque);
        tvCuentaDestino = dialogo.findViewById(R.id.tvCuentaDestino);
        tvMonto = dialogo.findViewById(R.id.tvMonto);
        guardarFormaPagoFE = dialogo.findViewById(R.id.guardarFormaPagoFE);
        guardarFormaPagoPendienteChe = dialogo.findViewById(R.id.guardarFormaPagoPendienteChe);
        tvNumeroCheque = dialogo.findViewById(R.id.tvNumeroCheque);
        lnNumCheque = dialogo.findViewById(R.id.lnNumCheque);
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

        if (empresa.equals("AGUC"))
        {
            lnNumCheque.setVisibility(View.GONE);
        }

        final Spinner spinnerBanco = dialogo.findViewById(R.id.spinnerBancoCheq);
        spinnerBanco.setVisibility(View.VISIBLE);

        final Spinner spinnerCuentasBanco = dialogo.findViewById(R.id.spinnerCuentasBanco);
        spinnerCuentasBanco.setVisibility(View.VISIBLE);

        String[] items;
        final Vector<String> listaItems = new Vector<String>();
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
                //Muestro el widget
                recogerFecha.show();
                recogerFecha.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
            }
        });

        if(empresa.equals("AGUC"))
        {
            (dialogo.findViewById(R.id.lienarChequeReferencia)).setVisibility(View.GONE);
        }

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObject = PreferencesPendientesFacturas.obtenerPendientesFacturaSeleccionada(contexto);
        final Collection<Pendientes> facCollection = gson.fromJson(stringJsonObject, collectionType);

        Gson gson1 = new Gson();
        String stringJsonObject1 = PreferencesFacturaPendientesSeleccionada.obtenerFacturasPendiSeleccionado(contexto);
        pendientesSeleccionada = gson1.fromJson(stringJsonObject1, Pendientes.class);

        Gson gson3 = new Gson();
        String stringJsonObject3 = PreferencesAnticipo.obteneAnticipoSeleccionada(contexto);
        anticipo = gson3.fromJson(stringJsonObject3, Anticipo.class);

        Gson gsonPen = new Gson();
        Type collectionTypePendientes = new TypeToken<Collection<Pendientes>>() {
        }.getType();
        String stringJsonObjectPendientes = PreferencesFacturasMultiplesPendientesVarias.obtenerFacturasMultiplesPendientesVariasSeleccionado(contexto);
        final Collection<Pendientes> facCollectionPendientes = gsonPen.fromJson(stringJsonObjectPendientes, collectionTypePendientes);



        final List<String> doctoFinancieros = new ArrayList<>();
        final List<String> numeroRecibos = new ArrayList<>();
        final List<Integer> consecutivos = new ArrayList<>();
        final List<String> numeroRecibosMultiples = new ArrayList<>();
        final List<Integer> consecutivosMultiples = new ArrayList<>();
        final List<String> idPagos = new ArrayList<>();
        final List<String> idPagosPendientes = new ArrayList<>();
        final List<String> idPagosPen = new ArrayList<>();
        final List<Double> valoresFac = new ArrayList<>();
        final List<String> idPagosUnicosElminiar = new ArrayList<>();

        String idPago = "";
        String idPagoPendientes = "";
        String numeroRecibo = "";
        int consecutivo = 0;
        String doctoFinanciero = "";

        final List<String> codigoClientes = new ArrayList<>();
        final List<String> fechaCreacions = new ArrayList<>();
        final List<String> fechaCierres = new ArrayList<>();
        final List<String> fechasRecibos = new ArrayList<>();
        final List<String> montoPendientess = new ArrayList<>();
        final List<String> statuss = new ArrayList<>();
        final List<String> claseDocumentos = new ArrayList<>();
        final List<String> sociedads = new ArrayList<>();
        final List<String> cod_vendedors = new ArrayList<>();
        final List<String> referencias = new ArrayList<>();
        final List<String> valorDocumentos = new ArrayList<>();
        final List<String> valorConsignados = new ArrayList<>();
        final List<String> cuentaBancarias = new ArrayList<>();
        final List<String> monedaConsiganadas = new ArrayList<>();
        final List<String> monedas = new ArrayList<>();
        final List<String> comprobanteFiscals = new ArrayList<>();
        final List<String> observacioness = new ArrayList<>();
        final List<String> observacionesMotivos = new ArrayList<>();
        final List<String> viaPagos = new ArrayList<>();
        final List<String> usuarios = new ArrayList<>();
        final List<String> operacionCMEs = new ArrayList<>();
        final List<String> sincronizados = new ArrayList<>();
        final List<String> bancoPendientess = new ArrayList<>();
        final List<String> numeroCheqes = new ArrayList<>();
        final List<String> nombrePropietarios = new ArrayList<>();
        final List<String> precios = new ArrayList<>();
        final List<String> listaConsecutivoidFac = new ArrayList<>();
        final List<String> saldosAfavor = new ArrayList<>();
        final List<String> preciosMultiples = new ArrayList<>();

        String codigoCliente = "";
        String fechaCreacion = "";
        String fechaCierre = "";
        String fechaRecibo = "";
        double montoPendientes = 0;
        String status = "";
        String claseDocumento = "";
        String sociedad = "";
        String cod_vendedor = "";
        String referencia = "";
        double valorDocumento = 0;
        double valorConsignado = 0;
        String cuentaBancaria = "";
        String monedaConsiganada = "";
        String moneda = "";
        String comprobanteFiscal = "";
        String observaciones = "";
        String observacionesMotivo = "";
        String viaPago = "";
        String usuario = "";
        String operacionCME = "";
        String sincronizado = "";
        String bancoPendientes = "";
        String numeroCheqe = "";
        String nombrePropietario = "";
        double preciosPendientesMultiples = 0;


        idPagosPen.add(pendientesSeleccionada.getIdPago());



        Gson gson123 = new Gson();
        String stringJsonObject123 = PreferencesFacturasMultiplesPendientes.obtenerFacturasMultiplesPendientesSeleccionado(contexto);
        multipleestado = gson123.fromJson(stringJsonObject123, MultiplesEstado.class);


        if (multipleestado == null) {
            for (Pendientes pendientes : facCollection) {
                numeroRecibosMultiples.add(pendientes.getNumeroRecibo());
                consecutivosMultiples.add(pendientes.getConsecutivo());
                idPago = pendientes.getIdPago();
                idPagosUnicosElminiar.add(pendientes.getIdPago());
                idPagos.add(idPago);

            }

            List<Pendientes> listaFacturas = DataBaseBO.cargarFacturasParametroPendientesCheque(idPagosPen, contexto);

            for (Pendientes pendientes : listaFacturas) {
                claseDocumento = pendientes.getClaseDocumento();
                claseDocumentos.add(claseDocumento);
                sociedad = pendientes.getSociedad();
                sociedads.add(sociedad);
                codigoCliente = pendientes.getCodigoCliente();
                codigoClientes.add(codigoCliente);
                cod_vendedor = pendientes.getCod_vendedor();
                cod_vendedors.add(cod_vendedor);
                referencia = pendientes.getReferencia();
                referencias.add(referencia);
                fechaCreacion = pendientes.getFechaCreacion();
                fechaCreacions.add(fechaCreacion);
                fechaCierre = String.valueOf(pendientes.getFechaCierre());
                fechaCierres.add(fechaCierre);
                fechaRecibo = pendientes.getFechaRecibo();
                fechasRecibos.add(fechaRecibo);
                valorDocumento = pendientes.getValorDocumento();
                valorDocumentos.add(String.valueOf(valorDocumento));
                moneda = pendientes.getMoneda();
                monedas.add(moneda);
                montoPendientes = pendientes.getMontoPendientes();
                montoPendientess.add(String.valueOf(montoPendientes));
                valorConsignado = pendientes.getValorConsignado();
                valorConsignados.add(String.valueOf(valorConsignado));
                saldosAfavor.add(String.valueOf(pendientes.saldoAfavor));
                cuentaBancaria = pendientes.getCuentaBancaria();
                cuentaBancarias.add(cuentaBancaria);
                monedaConsiganada = pendientes.getMonedaConsiganada();
                monedaConsiganadas.add(monedaConsiganada);
                comprobanteFiscal = pendientes.getComprobanteFiscal();
                comprobanteFiscals.add(comprobanteFiscal);
                doctoFinanciero = pendientes.getDoctoFinanciero();
                doctoFinancieros.add(doctoFinanciero);
                numeroRecibo = pendientes.getNumeroRecibo();
                numeroRecibos.add(numeroRecibo);
                consecutivo = pendientes.getConsecutivo();
                consecutivos.add(consecutivo);
                observaciones = pendientes.getObservaciones();
                observacioness.add(observaciones);
                observacionesMotivo = pendientes.getObservacionesMotivo();
                observacionesMotivos.add(observacionesMotivo);
                viaPago = pendientes.getViaPago();
                viaPagos.add(viaPago);
                usuario = pendientes.getUsuario();
                usuarios.add(usuario);
                operacionCME = pendientes.getOperacionCME();
                operacionCMEs.add(operacionCME);
                sincronizado = pendientes.getSincronizado();
                sincronizados.add(sincronizado);
                bancoPendientes = pendientes.getBanco();
                bancoPendientess.add(bancoPendientes);
                numeroCheqe = pendientes.getNumeroCheqe();
                numeroCheqes.add(numeroCheqe);
                nombrePropietario = pendientes.getNombrePropietario();
                nombrePropietarios.add(nombrePropietario);
                precios.add(String.valueOf(valorDocumento));
                idPagoPendientes = pendientes.getIdPago();
                idPagosPendientes.add(idPago);
                listaConsecutivoidFac.add(pendientes.getConsecutivoidFac());

            }


        }else if (multipleestado != null) {

            for (Pendientes pendientes : facCollectionPendientes) {
                numeroRecibosMultiples.add(pendientes.getNumeroRecibo());
                consecutivosMultiples.add(pendientes.getConsecutivo());
                idPago = pendientes.getIdPago();
                idPagosUnicosElminiar.add(pendientes.getIdPago());
                idPagos.add(idPago);

            }

            List<Pendientes> listaFacturas = DataBaseBO.cargarFacturasParametroPendientesChequeMultiples(numeroRecibosMultiples, contexto);
            preciosPendientesMultiples = DataBaseBO.cargarFacturasParametroPendientesChequeMultiplesValorPendiente(numeroRecibosMultiples, contexto);


            for (Pendientes pendientes : listaFacturas) {
                claseDocumento = pendientes.getClaseDocumento();
                claseDocumentos.add(claseDocumento);
                sociedad = pendientes.getSociedad();
                sociedads.add(sociedad);
                codigoCliente = pendientes.getCodigoCliente();
                codigoClientes.add(codigoCliente);
                cod_vendedor = pendientes.getCod_vendedor();
                cod_vendedors.add(cod_vendedor);
                referencia = pendientes.getReferencia();
                referencias.add(referencia);
                fechaCreacion = pendientes.getFechaCreacion();
                fechaCreacions.add(fechaCreacion);
                fechaCierre = String.valueOf(pendientes.getFechaCierre());
                fechaCierres.add(fechaCierre);
                fechaRecibo = pendientes.getFechaRecibo();
                fechasRecibos.add(fechaRecibo);
                valorDocumento = pendientes.getValorDocumento();
                valorDocumentos.add(String.valueOf(valorDocumento));
                moneda = pendientes.getMoneda();
                monedas.add(moneda);
                montoPendientes = pendientes.getMontoPendientes();
                montoPendientess.add(String.valueOf(montoPendientes));
                valorConsignado = pendientes.getValorConsignado();
                valorConsignados.add(String.valueOf(valorConsignado));
                saldosAfavor.add(String.valueOf(pendientes.saldoAfavor));
                cuentaBancaria = pendientes.getCuentaBancaria();
                cuentaBancarias.add(cuentaBancaria);
                monedaConsiganada = pendientes.getMonedaConsiganada();
                monedaConsiganadas.add(monedaConsiganada);
                comprobanteFiscal = pendientes.getComprobanteFiscal();
                comprobanteFiscals.add(comprobanteFiscal);
                doctoFinanciero = pendientes.getDoctoFinanciero();
                doctoFinancieros.add(doctoFinanciero);
                numeroRecibo = pendientes.getNumeroRecibo();
                numeroRecibos.add(numeroRecibo);
                consecutivo = pendientes.getConsecutivo();
                consecutivos.add(consecutivo);
                observaciones = pendientes.getObservaciones();
                observacioness.add(observaciones);
                observacionesMotivo = pendientes.getObservacionesMotivo();
                observacionesMotivos.add(observacionesMotivo);
                viaPago = pendientes.getViaPago();
                viaPagos.add(viaPago);
                usuario = pendientes.getUsuario();
                usuarios.add(usuario);
                operacionCME = pendientes.getOperacionCME();
                operacionCMEs.add(operacionCME);
                sincronizado = pendientes.getSincronizado();
                sincronizados.add(sincronizado);
                bancoPendientes = pendientes.getBanco();
                bancoPendientess.add(bancoPendientes);
                numeroCheqe = pendientes.getNumeroCheqe();
                numeroCheqes.add(numeroCheqe);
                nombrePropietario = pendientes.getNombrePropietario();
                nombrePropietarios.add(nombrePropietario);
                precios.add(String.valueOf(valorDocumento));
                idPagoPendientes = pendientes.getIdPago();
                idPagosPendientes.add(idPagoPendientes);
//                preciosPendientesMultiples += pendientes.getMontoPendientes();
                listaConsecutivoidFac.add(pendientes.getConsecutivoidFac());

            }

            for (int i = 0; i < listaFacturas.size(); i++) {
                preciosMultiples.add(String.valueOf(preciosPendientesMultiples));
            }


        }

        if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {


            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
            if (multipleestado == null) {
                tvMonto.setText(formatoNumero.format(pendientesSeleccionada.valorConsignado));
                tvValorFragEfec.setText(formatoNumero.format(pendientesSeleccionada.valorConsignado));
            }else if (multipleestado != null) {
                tvMonto.setText(formatoNumero.format(preciosPendientesMultiples));
                tvValorFragEfec.setText(formatoNumero.format(preciosPendientesMultiples));
            }

        } else {

            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
            if (multipleestado == null) {
                tvMonto.setText(formatoNumero.format(pendientesSeleccionada.valorConsignado));
                tvValorFragEfec.setText(formatoNumero.format(pendientesSeleccionada.valorConsignado));
            }else if (multipleestado != null) {
                tvMonto.setText(formatoNumero.format(preciosPendientesMultiples));
                tvValorFragEfec.setText(formatoNumero.format(preciosPendientesMultiples));
            }

        }


        tvFechaFragEfec.setText(pendientesSeleccionada.fechaCierre);
        tvNumeroCheque.setText(pendientesSeleccionada.numeroCheqe);

        final String idenFoto = cod_vendedor + Utilidades.fechaActual("HHmmss") + 1;


        if (tomarFoto.isClickable()) {
            tomarFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Fotos fotosestado = new Fotos();
                    fotosestado.estadoFoto = true;
                    fotosestado.idenFoto = idenFoto;

                    Gson gson = new Gson();
                    String jsonStringObject = gson.toJson(fotosestado);
                    PreferencesFotos.guardarFotoSeleccionada(contexto,jsonStringObject);

                    SharedPreferences sharedPreferences = contexto.getSharedPreferences("session", Context.MODE_PRIVATE);
                    boolean estado = true;
                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putBoolean("estado_MultiplesFotos", estado);
                    editor1.commit();
                    Intent login = new Intent(contexto.getApplicationContext(), MultiplesFotosActivity.class);
                    login.putExtra("metodo_pago", "B");
                    contexto.startActivity(login);
                    MetodoDePagoChequePendientes.dialogo.onSaveInstanceState();


                }
            });
        }

        final String finalIdPago = idPagoPendientes;
        final String finalSociedad = sociedad;
        final String finalCodigoCliente = codigoCliente;
        final String finalCod_vendedor = cod_vendedor;
        final String finalNumeroRecibo = numeroRecibo;
        final int finalConsecutivo = consecutivo;
        final String finalViaPago = viaPago;
        final String finalUsuario = usuario;

        final String finalReferencia = referencia;

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

        String finalFechaCierre = fechaCierre;
        String finalOperacionCME = operacionCME;
        String finalEmpresa1 = empresa;
        String finalFechaRecibo = fechaRecibo;
        guardarFormaPagoFE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                String input = tvValorFragEfec.getText().toString();
                String fecha = tvFechaFragEfec.getText().toString();
                String referenciaCheq = tvReferenciaCheque.getText().toString();
                String cuentaDestino = tvCuentaDestino.getText().toString();
                double valor = 0;
                String numCheque = tvNumeroCheque.getText().toString();
                String operacion_Cme = "";


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

                            Toasty.warning(contexto, "The amount field cannot be blank..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo del monto no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                        }
                    }
                    return;
                }

                precios.add(input);


                if (valor == 0) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "The amount field cannot be blank..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo del monto no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                        }
                    }
                    return;
                }

                if (numCheque.equals("")) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "Check number field cannot be blank..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo del numero del cheque no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                        }
                    }
                    return;
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
                    return;
                }

                if (spinnerBanco.getSelectedItem().toString().equals("Seleccione")
                        || spinnerBanco.getSelectedItem().toString().equals("Select")) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "Account name cannot be left without selection..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El nombre de la cuenta no puede quedar sin seleccion..", Toasty.LENGTH_SHORT).show();

                        }
                    }
                    return;
                }

                Gson gson1 = new Gson();
                String stringJsonObject1 = PreferencesFotos.obteneFotoSeleccionada(contexto);
                Fotos fotos = gson1.fromJson(stringJsonObject1, Fotos.class);

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
                    return;
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
                    return;
                }

                if (referenciaCheq.equals("") && !finalEmpresa1.equals("AGUC")) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "The bank reference field cannot be blank..", Toasty.LENGTH_SHORT).show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo de la referencia no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                        }
                    }
                    return;
                }


                if (facCollection != null) {
                    if (referenciaCheq.equals("") && !finalEmpresa1.equals("AGUC")) {
                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Toasty.warning(contexto, "The bank reference field cannot be blank..", Toasty.LENGTH_SHORT).show();

                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Toasty.warning(contexto, "El campo de la referencia no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                            }
                        }
                        return;
                    }
                }

//                if (cuentaDestino.equals("")) {
//                    if (lenguajeElegido == null) {
//
//                    } else if (lenguajeElegido != null) {
//                        if (lenguajeElegido.lenguaje.equals("USA")) {
//
//                            Toasty.warning(contexto, "The source account field cannot be blank..", Toasty.LENGTH_SHORT).show();
//
//                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {
//
//                            Toasty.warning(contexto, "El campo de la cuenta origen no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();
//
//                        }
//                    }
//
//                }


                if ((valor != 0 && !fecha.equals("")
                        && !spinnerCuentasBanco.getSelectedItem().toString().equals("Seleccione")
                        && !spinnerBanco.getSelectedItem().toString().equals("Seleccione")
                        || !spinnerCuentasBanco.getSelectedItem().toString().equals("Select")
                        || !spinnerBanco.getSelectedItem().toString().equals("Select"))  && fotos != null) {


                    try {

                        if (anticipo != null) {
                            if (anticipo.estado == true) {
                                operacion_Cme = "A";
                            }else if (anticipo.estado == false) {
                                operacion_Cme = "X";
                            }
                        }

                        if (multipleestado == null) {

                            //CARGAR CONSUCUTIVO ID
                            final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                            String consecId1 = "", numeroAnulacionId1 = "";
                            String negocioId1 = "";
                            String vendedorId1 = "";

                            consecId1 = DataBaseBO.cargarConsecutivoId(contexto);
                            negocioId1 = DataBaseBO.cargarNegocioConsecutivoId(contexto);
                            vendedorId1 = DataBaseBO.cargarVendedorConsecutivoId(contexto);

                            int consec1Id = Integer.parseInt(consecId1);
                            int vendedorsumId = Integer.parseInt(vendedorId1);
                            int contadorId = 1;
                            consec1Id = consec1Id + contadorId;
                            numeroAnulacionId1 = String.valueOf(negocioId1 + vendedorsumId + consec1Id);

                           DataBaseBO.guardarConsecutivoId(negocioId1, vendedorsumId, consec1Id, fechacon, contexto);

                            if (DataBaseBO.guardarFormaPagPendientes(finalIdPago, claseDocumentos,
                                    finalSociedad, finalCodigoCliente, finalCod_vendedor,
                                    referenciaCheq, fechaCreacions,
                                    fecha, valorDocumentos,
                                    pendientesSeleccionada.getMoneda(), valorConsignados, montoPendientess,saldosAfavor, spinnerCuentasBanco.getSelectedItem().toString(),
                                    pendientesSeleccionada.getMonedaConsiganada(), pendientesSeleccionada.getComprobanteFiscal(), doctoFinancieros,
                                    finalNumeroRecibo,
                                    pendientesSeleccionada.getObservaciones(), finalViaPago, finalUsuario, operacionCMEs,
                                    0, spinnerBanco.getSelectedItem().toString(), tvNumeroCheque.getText().toString(),
                                    "0",fotos.idenFoto,numeroAnulacionId1, finalConsecutivo, pendientesSeleccionada.getObservacionesMotivo(), finalFechaRecibo, contexto)) {

                                    //SE ACTUALIZAN LOS ID DE LAS FOTOS PARA RELACIONARLAS CON LAS FOTOS DE BUZON
//                                    if(finalEmpresa1.equals("AGUC"))
//                                    {
                                        DataBaseBO.actualizarFotoBuzon(finalIdPago, contexto);
//                                    }

                         //       DataBaseBO.eliminarRecaudosTotalPendientes(idPagosPendientes);
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

                        }else if (multipleestado != null) {

                            String consec = "",numeroAnulacion="";
                            String negocio = "";
                            String vendedor = "";

                            consec = DataBaseBO.cargarConsecutivoPaquete(contexto);
                            negocio = DataBaseBO.cargarNegocioConsecutivoPaquete(contexto);
                            vendedor = DataBaseBO.cargarVendedorConsecutivoPaquete(contexto);

                            int consec1 = Integer.parseInt(consec);
                            int vendedorsum = Integer.parseInt(vendedor);
                            int contador = 1;
                            consec1 = consec1 + contador;
                            numeroAnulacion = String.valueOf(negocio + vendedorsum + consec1);
                            final String fechacon = Utilidades.fechaActual("yyyy-MM-dd");
                            DataBaseBO.guardarConsecutivoPaquete(negocio, vendedorsum, consec1, fechacon, contexto);

                            //CARGAR CONSUCUTIVO ID
                            String consecId = "",numeroAnulacionId="";
                            String negocioId = "";
                            String vendedorId = "";

                            consecId = DataBaseBO.cargarConsecutivoId(contexto);
                            negocioId = DataBaseBO.cargarNegocioConsecutivoId(contexto);
                            vendedorId = DataBaseBO.cargarVendedorConsecutivoId(contexto);

                            int consec1Id = Integer.parseInt(consecId);
                            int vendedorsumId = Integer.parseInt(vendedorId);
                            int contadorId = 1;
                            consec1Id = consec1Id + contadorId;
                            numeroAnulacionId = String.valueOf(negocioId + vendedorsumId + consec1Id);
                            final String fechaconId = Utilidades.fechaActual("yyyy-MM-dd");
                            DataBaseBO.guardarConsecutivoId(negocioId, vendedorsumId, consec1Id, fechacon, contexto);



                            if (DataBaseBO.guardarFormaPagPendientesMultiples(idPagosPendientes, claseDocumentos,
                                    finalSociedad, codigoClientes, finalCod_vendedor,
                                    referenciaCheq,fechaCreacions,
                                    fecha, valorDocumentos,
                                    pendientesSeleccionada.getMoneda(), montoPendientess, preciosMultiples,saldosAfavor, spinnerCuentasBanco.getSelectedItem().toString(),
                                    pendientesSeleccionada.getMonedaConsiganada(), pendientesSeleccionada.getComprobanteFiscal(), doctoFinancieros,
                                    numeroRecibos,
                                    observacioness, viaPagos, finalUsuario, operacionCMEs,
                                    0,  spinnerBanco.getSelectedItem().toString(), tvNumeroCheque.getText().toString(),
                                    "0",fotos.idenFoto,numeroAnulacion,numeroAnulacionId,valorConsignados,consecutivos, observacionesMotivos, listaConsecutivoidFac, fechasRecibos, contexto)) {

                                    //SE ACTUALIZAN LOS ID DE LAS FOTOS PARA RELACIONARLAS CON LAS FOTOS DE BUZON
//                                    if(finalSociedad.equals("AGUC"))
//                                    {
                                        DataBaseBO.actualizarFotoBuzonMultiples(idPagosPendientes, contexto);
                                        DataBaseBO.actualizarNumeroChequePrecargado(idPagosPendientes, contexto);
//                                    }

                    //            DataBaseBO.eliminarRecaudosTotalPendientes(idPagosPendientes);
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



                    } catch (Exception exception) {
                        System.out.println("Error en la forma de pago parcial " + exception);
                    }


                    Intent login = new Intent(contexto.getApplicationContext(), MetodosDePagoPendientesActivity.class);
                    contexto.startActivity(login);
                    ((MetodosDePagoPendientesActivity) contexto).finish();
                    dialogo.cancel();
                }


            }
        });

        cancelarFormaPagoFE = dialogo.findViewById(R.id.cancelarFormaPagoFE);
        cancelarFormaPagoFE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<String> fotosListaid = new ArrayList<>();
                String fotoID= "";

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
                DataBaseBO.eliminarFotoIDFac(fotosListaid, contexto);
                dialogo.cancel();
            }
        });


        dialogo.setCancelable(false);
        dialogo.show();
    }

}
