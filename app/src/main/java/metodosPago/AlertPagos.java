package metodosPago;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import businessObject.DataBaseBO;
import co.com.celuweb.carterabaldomero.MetodosDePagoActivity;
import co.com.celuweb.carterabaldomero.R;
import co.com.celuweb.carterabaldomero.ReciboDineroActivity;
import co.com.celuweb.carterabaldomero.VistaClienteActivity;
import dataobject.Anticipo;
import dataobject.Bancos;
import dataobject.Cartera;
import dataobject.ClienteSincronizado;
import dataobject.CuentasBanco;
import dataobject.Facturas;
import dataobject.FormaPago;
import dataobject.Lenguaje;
import dataobject.ReciboDinero;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesAnticipo;
import sharedpreferences.PreferencesFormaPago;
import sharedpreferences.PreferencesLenguaje;
import sharedpreferences.PreferencesReciboDinero;
import utilidades.Utilidades;

public class AlertPagos {

    public static Dialog dialogo;

    public static Vector<Bancos> listaParametrosBancosSpinner;
    public static Vector<CuentasBanco> listaParametrosCuentas;


    /**
     * VISTA DIALOGO RECIBO LEGALIZAR
     *
     * @param contexto
     * @param titulo
     * @param texto
     * @param onClickListenerAceptar
     * @param onClickListenerCancelar
     */
    public static void vistaDialRecibolegalizar(final Context contexto, @NonNull String titulo, @NonNull String texto, View.OnClickListener onClickListenerAceptar,
                                                View.OnClickListener onClickListenerCancelar) {

        final TextView simboloMontoAnticipo, txtObservaciones, tituloMontoReciboLegalizar, tituloObservacionReciboLegalizar;
        final EditText tvValorFragEfec;
        ImageView cancelarFormaPagoFE, guardarFormaPagoFE;
        Lenguaje lenguajeElegido;


        dialogo = new Dialog(contexto);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.fragment_recibo_legalizar);

        tvValorFragEfec = dialogo.findViewById(R.id.tvMontoReciboLegalizar);
        simboloMontoAnticipo = dialogo.findViewById(R.id.simboloMontoAnticipo);
        guardarFormaPagoFE = dialogo.findViewById(R.id.guardarFormaPagoFE);
        txtObservaciones = dialogo.findViewById(R.id.tvObservaciones);

        tituloMontoReciboLegalizar = dialogo.findViewById(R.id.tituloMontoReciboLegalizar);
        tituloObservacionReciboLegalizar = dialogo.findViewById(R.id.tituloObservacionReciboLegalizar);


        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(contexto);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);


        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                tituloMontoReciboLegalizar.setText("Amount Receipt L");
                tituloObservacionReciboLegalizar.setText("Observations");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {


            }
        }


        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();

        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("AABR")) {
            simboloMontoAnticipo.setText("$");
        }
        if (finalEmpresa.equals("ADHB")) {
            simboloMontoAnticipo.setText("$");
        }
        if (finalEmpresa.equals("AGSC")) {
            simboloMontoAnticipo.setText("$");
        }
        if (finalEmpresa.equals("AGGC")) {
            simboloMontoAnticipo.setText("Q");
        }
        if (finalEmpresa.equals("AFPN")) {
            simboloMontoAnticipo.setText("C$");
        }
        if (finalEmpresa.equals("AFPZ")) {
            simboloMontoAnticipo.setText("₡");
        }
        if (finalEmpresa.equals("AGCO")) {
            simboloMontoAnticipo.setText("$");
        }
        if (finalEmpresa.equals("AGAH")) {
            simboloMontoAnticipo.setText("₡");
        }
        if (finalEmpresa.equals("AGDP")) {
            simboloMontoAnticipo.setText("Q");
        }
        if (finalEmpresa.equals("AGUC")) {
            tituloMontoReciboLegalizar.setText("Amount");
            simboloMontoAnticipo.setText("$");
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

        guardarFormaPagoFE.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                double valor = 0;
                Anticipo anticipo = new Anticipo();
                anticipo.estado = false;
                anticipo.letra = "X";
                anticipo.observaciones = txtObservaciones.getText().toString().trim();
                String input = tvValorFragEfec.getText().toString();

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

                if (txtObservaciones.getText().toString().isEmpty()) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "Field of observations cannot be blank..", Toasty.LENGTH_SHORT).show();


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo de observaciones no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

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

                }


                if (!input.isEmpty() && !txtObservaciones.getText().toString().isEmpty()) {

                    ReciboDinero reciboDinero = new ReciboDinero();
                    reciboDinero.estadoRecibo = true;

                    Gson gson33 = new Gson();
                    String jsonStringObject = gson33.toJson(reciboDinero);
                    PreferencesReciboDinero.guardarReciboFormaSeleccionada(contexto, jsonStringObject);


                    if (valor != 0) {

                        anticipo.valor = valor;
                        Gson gson = new Gson();
                        String jsonStringObject33 = gson.toJson(anticipo);
                        PreferencesAnticipo.guardarAnticipoSeleccionada(contexto, jsonStringObject33);
                        PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(contexto);

                        Intent login = new Intent(contexto.getApplicationContext(), MetodosDePagoActivity.class);
                        contexto.startActivity(login);
                        ((MetodosDePagoActivity) contexto).finish();
                        dialogo.dismiss();
                        dialogo.cancel();
                    } else if (valor == 0) {

                        if (lenguajeElegido == null) {

                        } else if (lenguajeElegido != null) {
                            if (lenguajeElegido.lenguaje.equals("USA")) {

                                Toasty.warning(contexto, "The value entered cannot be 0..", Toasty.LENGTH_SHORT).show();


                            } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                                Toasty.warning(contexto, "El valor ingresado no puede ser 0..", Toasty.LENGTH_SHORT).show();

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
                SharedPreferences settings = contexto.getSharedPreferences("session", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = settings.edit();
                editor1.putBoolean("estado_AnticipoRecibo", true);
                editor1.remove("estado_AnticipoRecibo");
                editor1.commit();
                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(contexto);
                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(contexto);
                Intent vistaInforme = new Intent(contexto, ReciboDineroActivity.class);
                contexto.startActivity(vistaInforme);
                ((MetodosDePagoActivity) contexto).finish();
                dialogo.dismiss();
                AlertPagos.dialogo.cancel();
                dialogo.cancel();
            }
        });


        dialogo.setCancelable(false);
        dialogo.show();

    }


    /**
     * VISTA DIALOGO MONTO ANTICIPO
     *
     * @param contexto
     * @param titulo
     * @param texto
     * @param onClickListenerAceptar
     * @param onClickListenerCancelar
     */
    public static void vistaDialMontoAnticipo(final Context contexto, @NonNull String titulo, @NonNull String texto, View.OnClickListener onClickListenerAceptar,
                                              View.OnClickListener onClickListenerCancelar) {

        final TextView tvFechaFragEfec, simboloMontoAnticipo, tituloObservacionesAnticipo, tituloMontoAnticipo, tvDescripcionFragEfec, tvMonto, tvReferenciaEfectivo, tvCuentaDestinoEfectivo, txtObservaciones;
        final EditText tvValorFragEfec;
        ImageView cancelarFormaPagoFE, guardarFormaPagoFE;
        final Cartera facturaCartera;
        final Facturas facturas;
        final FormaPago formaPago;
        ClienteSincronizado clienteSel;
        Lenguaje lenguajeElegido;

        dialogo = new Dialog(contexto);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.fragment_monto);
        tvValorFragEfec = dialogo.findViewById(R.id.tvMontoAnticipo);
        simboloMontoAnticipo = dialogo.findViewById(R.id.simboloMontoAnticipo);
        guardarFormaPagoFE = dialogo.findViewById(R.id.guardarFormaPagoFE);
        txtObservaciones = dialogo.findViewById(R.id.tvObservaciones);

        tituloMontoAnticipo = dialogo.findViewById(R.id.tituloMontoAnticipo);
        tituloObservacionesAnticipo = dialogo.findViewById(R.id.tituloObservacionesAnticipo);

        Gson gson2 = new Gson();
        String stringJsonObject2 = PreferencesLenguaje.obtenerLenguajeSeleccionada(contexto);
        lenguajeElegido = gson2.fromJson(stringJsonObject2, Lenguaje.class);

        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {

                tituloMontoAnticipo.setText("Advance Amount");
                tituloObservacionesAnticipo.setText("Observation");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {


            }
        }


        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();


        final String finalEmpresa = empresa;

        if (finalEmpresa.equals("AABR")) {
            simboloMontoAnticipo.setText("$");
        }
        if (finalEmpresa.equals("ADHB")) {
            simboloMontoAnticipo.setText("$");
        }
        if (finalEmpresa.equals("AGSC")) {
            simboloMontoAnticipo.setText("$");
        }
        if (finalEmpresa.equals("AGGC")) {
            simboloMontoAnticipo.setText("Q");
        }
        if (finalEmpresa.equals("AFPN")) {
            simboloMontoAnticipo.setText("C$");
        }
        if (finalEmpresa.equals("AFPZ")) {
            simboloMontoAnticipo.setText("₡");
        }
        if (finalEmpresa.equals("AGCO")) {
            simboloMontoAnticipo.setText("$");
        }
        if (finalEmpresa.equals("AGAH")) {
            simboloMontoAnticipo.setText("₡");
        }
        if (finalEmpresa.equals("AGDP")) {
            simboloMontoAnticipo.setText("Q");
        }
        if (finalEmpresa.equals("AGUC")) {
            simboloMontoAnticipo.setText("$");
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

        guardarFormaPagoFE.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                ReciboDinero reciboDinero = new ReciboDinero();

                double valor = 0;
                Anticipo anticipo = new Anticipo();
                anticipo.estado = true;
                anticipo.letra = "A";
                anticipo.observaciones = txtObservaciones.getText().toString().trim();
                reciboDinero.estadoRecibo = true;
                String input = tvValorFragEfec.getText().toString();

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
                }

                if (txtObservaciones.getText().toString().isEmpty()) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "Field of observations cannot be blank..", Toasty.LENGTH_SHORT).show();


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El campo de observaciones no puede quedar en blanco..", Toasty.LENGTH_SHORT).show();

                        }
                    }
                }

                Gson gson33 = new Gson();
                String jsonStringObject33 = gson33.toJson(reciboDinero);
                PreferencesReciboDinero.guardarReciboFormaSeleccionada(contexto, jsonStringObject33);


                if (valor != 0 && !txtObservaciones.getText().toString().isEmpty()) {

                    anticipo.valor = valor;
                    Gson gson = new Gson();
                    String jsonStringObject = gson.toJson(anticipo);
                    PreferencesAnticipo.guardarAnticipoSeleccionada(contexto, jsonStringObject);
                    PreferencesFormaPago.vaciarPreferencesFormaPagoSeleccionada(contexto);

                    Intent login = new Intent(contexto.getApplicationContext(), MetodosDePagoActivity.class);
                    contexto.startActivity(login);
                    ((MetodosDePagoActivity) contexto).finish();
                    dialogo.dismiss();
                    dialogo.cancel();
                } else if (valor == 0) {

                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(contexto, "The value entered cannot be 0..", Toasty.LENGTH_SHORT).show();


                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(contexto, "El valor ingresado no puede ser 0..", Toasty.LENGTH_SHORT).show();

                        }
                    }

                }

            }
        });


        cancelarFormaPagoFE = dialogo.findViewById(R.id.cancelarFormaPagoFE);

        cancelarFormaPagoFE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = contexto.getSharedPreferences("session", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = settings.edit();
                editor1.putBoolean("estado_AnticipoRecibo", true);
                editor1.remove("estado_AnticipoRecibo");
                editor1.commit();
                PreferencesAnticipo.vaciarPreferencesAnticipoSeleccionada(contexto);
                PreferencesReciboDinero.vaciarPreferencesReciboFormaSeleccionada(contexto);
                Intent vistaInforme = new Intent(contexto, ReciboDineroActivity.class);
                contexto.startActivity(vistaInforme);
                AlertPagos.dialogo.cancel();
                dialogo.cancel();
            }
        });


        dialogo.setCancelable(false);
        dialogo.show();

    }


}
