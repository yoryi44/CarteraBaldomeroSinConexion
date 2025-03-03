package co.com.celuweb.carterabaldomero;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.tool.xml.html.table.Table;
import com.zebra.android.comm.BluetoothPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnection;
import com.zebra.android.comm.ZebraPrinterConnectionException;
import com.zebra.android.printer.PrinterLanguage;
import com.zebra.android.printer.ZebraPrinter;
import com.zebra.android.printer.ZebraPrinterFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;

import Adapters.AdapterSeleccionFacRealizados;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import businessObject.DataBaseBO;
import businessObject.PrinterBO;
import dataobject.Cliente;
import dataobject.Facturas;
import dataobject.FacturasRealizadas;
import dataobject.FirmaNombre;
import dataobject.Lenguaje;
import es.dmoral.toasty.Toasty;
import sharedpreferences.PreferencesFacRealizadasSeleccionadas;
import sharedpreferences.PreferencesLenguaje;
import utilidades.Alert;
import utilidades.Constantes;
import utilidades.ProgressView;
import utilidades.Utilidades;

public class FacturasRealizadasSeleccionadasActivity extends AppCompatActivity implements AdapterSeleccionFacRealizados.facturaCarteraRealizadosSelecc {

    String tituloText = "Este es el titulo del documento";
    TextView tvSaldoCarteraTotalFP;
    String descripcionText = "";
    String negocio = "";
    String numeroRecibo = "";
    String operacion = "";
    String cliente = "";
    String observaciones = "";
    String vendedor = "";
    Cliente cliente1;
    String empresa = "";

    ImageView IvImprimir;
    String documentoFactura = "";
    private Lenguaje lenguajeElegido;
    boolean estadoLenguaje = false;
    boolean visualizarPDF = false;

    boolean estadoEnviadoRespuesta = false;


    private List<FacturasRealizadas> listaFacturasRealizadas;


    String codigoCliente = "";
    static String nombrePdf = "";
    Collection<FacturasRealizadas> facCollection;

    ProgressDialog progressDoalog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facturas_realizadas);

        Gson gson223 = new Gson();
        String stringJsonObject223 = PreferencesLenguaje.obtenerLenguajeSeleccionada(this);
        lenguajeElegido = gson223.fromJson(stringJsonObject223, Lenguaje.class);

        if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {
                estadoLenguaje = true;
            } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                estadoLenguaje = false;
            }
        }

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<FacturasRealizadas>>() {
        }.getType();
        String stringJsonObject = PreferencesFacRealizadasSeleccionadas.obtenerFacRealizadasSeleccionadas(FacturasRealizadasSeleccionadasActivity.this);
        facCollection = gson.fromJson(stringJsonObject, collectionType);

        RecyclerView rvListaFacturasSelecRealizadas = findViewById(R.id.rvListaFacturasSelecRealizadas);
        rvListaFacturasSelecRealizadas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        final AdapterSeleccionFacRealizados adapter = new AdapterSeleccionFacRealizados((List<FacturasRealizadas>) facCollection, FacturasRealizadasSeleccionadasActivity.this);
        rvListaFacturasSelecRealizadas.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        TextView tvSaldoCarteraTotalFP = findViewById(R.id.txtSaldoTotalFacturasRealizados);

        TextView txt = findViewById(R.id.txt);
        TextView txt1 = findViewById(R.id.txt1);
        TextView txt2 = findViewById(R.id.txt2);
        TextView txt3 = findViewById(R.id.txt3);
        IvImprimir = findViewById(R.id.IvImprimir);


        if (lenguajeElegido == null) {

        } else if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {


                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Payments Collected"));
                txt.setText("Document number");
                txt1.setText("Type");
                txt2.setText("Balance");
                txt3.setText("total Collection");

            } else if (lenguajeElegido.lenguaje.equals("ESP")) {


                ActionBar barVista = getSupportActionBar();
                Objects.requireNonNull(barVista).setDisplayHomeAsUpEnabled(true);
                barVista.setTitle(Utilidades.tituloFormato(this, "Facturas Realizadas"));

            }
        }


        double precioTotal = 0;
        double precioDocumentos = 0;


        if (facCollection != null) {

            for (FacturasRealizadas cartera1 : facCollection) {
                descripcionText = cartera1.codigoCliente;
                precioTotal += cartera1.getMontoPendientes();
                precioDocumentos += cartera1.getValorDocumento();
                negocio = cartera1.sociedad;
                codigoCliente = cartera1.codigoCliente;
                numeroRecibo = cartera1.numeroRecibo;
                observaciones = cartera1.observaciones;
                documentoFactura = cartera1.documentoFactura;
                operacion = cartera1.operacionCME;

            }
        }

        vendedor = DataBaseBO.cargarUsuarioApp();
        empresa = DataBaseBO.cargarEmpresaRazonSocial();

        cliente1 = DataBaseBO.cargarCliente(codigoCliente);

        //EJEMPLO DE LA DESCRIPCION
        String empresa = "";
        empresa = DataBaseBO.cargarEmpresa();
        final String finalEmpresa = empresa;

        if(finalEmpresa.equals("ADHB"))
        {
            IvImprimir.setVisibility(View.VISIBLE);
        }
        else if(finalEmpresa.equals("AGUC"))
        {
            IvImprimir.setVisibility(View.GONE);
        }

        if (finalEmpresa.equals("AGCO") || finalEmpresa.equals("AGSC") || finalEmpresa.equals("AGGC") || finalEmpresa.equals("AFPN")
                || finalEmpresa.equals("AFPZ") || finalEmpresa.equals("AGAH") || finalEmpresa.equals("AGDP")) {

            NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
            tvSaldoCarteraTotalFP.setText(formatoNumero.format(precioTotal));

        } else {

            if (finalEmpresa.equals("AGUC")) {
                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                tvSaldoCarteraTotalFP.setText("$" + formatoNumero.format(precioTotal));
            } else {
                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                tvSaldoCarteraTotalFP.setText(formatoNumero.format(precioTotal));
            }

        }


        guardarVista();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        if (lenguajeElegido != null) {
            if (lenguajeElegido.lenguaje.equals("USA")) {
                inflater.inflate(R.menu.menu_pdf_eng, menu);
                estadoLenguaje = true;
            } else if (lenguajeElegido.lenguaje.equals("ESP")) {
                inflater.inflate(R.menu.menu_pdf, menu);
                estadoLenguaje = false;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {


            item.setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    item.setEnabled(true);
                }
            }, 600);


            onBackPressed();
            return true;
        }
        switch (item.getItemId()) {

            case R.id.descargar:

                item.setEnabled(false);

                String empresas = DataBaseBO.cargarEmpresa();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);

                if (estadoLenguaje == true) {
                    ProgressView.getInstance().Show(this,"Downloading...");
                } else if (estadoLenguaje == false) {
                    ProgressView.getInstance().Show(this,"Descargando...");
                }



                //generarPdf();
                new Thread(new Runnable() {
                    public void run() {
                        if(empresas.equals("ADHB") || empresas.equals("AGUC")) {

                            if (estadoLenguaje == true) {
                                visualizarPDF = true;
                                generarCotizacionPdfUSANEW();


                            } else if (estadoLenguaje == false) {
                                visualizarPDF = true;
                                generarCotizacionPdfNEW();
                            }

                        } else  {

                            if (estadoLenguaje == true) {
                                visualizarPDF = true;
                                generarCotizacionPdfUSA();


                            } else if (estadoLenguaje == false) {
                                visualizarPDF = true;
                                generarCotizacionPdf();
                            }


                        }

                        ProgressView.getInstance().Dismiss();
                    }
                }).start();

                return true;

            case R.id.visualizar:


                item.setEnabled(false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setEnabled(true);
                    }
                }, 600);
                if (visualizarPDF == true) {
                    visualizar();
                } else if (visualizarPDF == false) {
                    if (lenguajeElegido == null) {

                    } else if (lenguajeElegido != null) {
                        if (lenguajeElegido.lenguaje.equals("USA")) {

                            Toasty.warning(getApplicationContext(), "Download The PDF Correctly, you can now view").show();

                        } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                            Toasty.warning(getApplicationContext(), "Para visualizar el PDF primero se tiene que descargar").show();
                        }
                    }
                }


                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void generarPdf() {

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        TextPaint titulo = new TextPaint();
        TextPaint descripcion = new TextPaint();

        Bitmap bitmap, bitmapEscala;

        PdfDocument.PageInfo paginaInfo = new PdfDocument.PageInfo.Builder(816, 1054, 1).create();
        PdfDocument.Page pagina1 = pdfDocument.startPage(paginaInfo);

        Canvas canvas = pagina1.getCanvas();

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.agco);
        bitmapEscala = Bitmap.createScaledBitmap(bitmap, 100, 80, false);
        canvas.drawBitmap(bitmapEscala, 18, 20, paint);


        //titulo
        titulo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titulo.setTextSize(20);
        canvas.drawText(tituloText, 10, 150, titulo);

        //descripcion
        descripcion.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        descripcion.setTextSize(14);

        String[] arrDescripcion = codigoCliente.split("\n");
        int y = 200;
        for (int i = 0; i < arrDescripcion.length; i++) {
            canvas.drawText(arrDescripcion[i], 10, y, descripcion);
            y += 15;
        }

        pdfDocument.finishPage(pagina1);

        File file = new File(Environment.getExternalStorageDirectory(), "Comprobante.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Toasty.warning(getApplicationContext(), "Download The PDF Correctly, you can now view").show();

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Toasty.warning(getApplicationContext(), "Se Descargo El PDF Correctamente, ya se puede visualizar").show();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        pdfDocument.close();
    }

    public static File getRuta() {
        // El fichero sera almacenado en un directorio dentro del directorio
        // Descargas
        File ruta = Utilidades.dirAppPDF();
        File carpeta = null;
        if (ruta != null) {
            carpeta = new File(ruta, Constantes.NOMBRE_DIRECTORIO_PDF);
            if (!carpeta.isDirectory()) carpeta.mkdirs();
            return carpeta;
        }
        return carpeta;
    }


    public static File crearFichero(String nombreFichero) throws IOException {
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null)
            fichero = new File(ruta, nombreFichero);
        return fichero;
    }

    public boolean generarCotizacionPdfUSA() {


        Boolean estado = false;
        String fechaDocumento = Utilidades.fechaActual("yyyyMMdd");
        String fecha = Utilidades.fechaActual("dd/MM/yyyy");
        String fechaRecibo = "";
        fechaRecibo = DataBaseBO.cargarFechaMaxReciboRealizados(numeroRecibo);
        String fechaRealizadosRecibo = " ";
        double salfoAFA;
        salfoAFA = DataBaseBO.SaldoAfavor(numeroRecibo);


        if (fechaRecibo != null) {
            if (fechaRecibo.equals("null")) {

                fechaRealizadosRecibo = "                   ";
            } else if (!fechaRecibo.equals("null")) {

                fechaRealizadosRecibo = fechaRecibo;
            }
        }

        // Creamos el documento.
        Document documento = new Document();
        String empresas = "";
        empresas = DataBaseBO.cargarEmpresa();

        try {


            File f = crearFichero("Comprobante.pdf");

            nombrePdf = "Comprobante" + ".pdf";

            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    f.getAbsolutePath());

            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            // Abrimos el documento.
            documento.open();

            if (operacion == null) {
                operacion = "  ";
            } else if (operacion != null) {


                if (operacion.equals("X")) {
                    operacion = "Receipt to legalize";
                } else if (operacion.equals("A")) {
                    operacion = "Advance";
                }


            }


            Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado = new Paragraph("          Company: " + empresa + "                      Receipt  " + operacion, font);


            if (empresas.equals("AABR")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.alicapsa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);


            } else if (empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGCO")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.nutresa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGGC") || empresas.equals("AGSC")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pozuelo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGDP")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pops);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("ADHB")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bon);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGUC")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.cordialsa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            }


            Font font4 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezadoUno = new Paragraph("\n" + "                        Date: " + fechaRealizadosRecibo + "                      Consecutive: " + numeroRecibo, font4);
            textoEncabezadoUno.setAlignment(Element.ALIGN_CENTER);//el 1 es para centrar

            textoEncabezado.setAlignment(Element.ALIGN_CENTER);//el 1 es para centrar


            Font font2 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado2 = new Paragraph("\n" + "                                                  Created by: " + vendedor, font2);
            textoEncabezado2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar


            if (cliente1 == null) {
                Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
                Paragraph textoEncabezado3 = new Paragraph("Customer Name: ", font3);
                textoEncabezado3.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(textoEncabezado3);
            } else {
                Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
                Paragraph textoEncabezado3 = new Paragraph("\n" + "                                                  Customer Name: " + cliente1.nombre, font3);
                textoEncabezado3.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(textoEncabezado3);
            }


            Font font54 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado35 = new Paragraph("\n" + "                                                 Customer Code: " + codigoCliente, font54);
            textoEncabezado35.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar


            PdfPCell cell2 = new PdfPCell();
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setPaddingTop(-7);
            cell2.setRightIndent(12);
//            cell2.addElement(textoEncabezado);
//            cell2.addElement(textoEncabezado1);
//            cell2.addElement(textoEncabezado2);
            cell2.setColspan(4);


            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 2});
            table.getDefaultCell().setBorder(0);
            //table.addCell(imagen);
            // documento.add(textoEncabezado);
            documento.add(textoEncabezadoUno);
            documento.add(textoEncabezado2);
            documento.add(textoEncabezado35);


            documento.add(new Paragraph("\n"));


            PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            table1.setWidths(new int[]{1, 2});
            table1.getDefaultCell().setBorder(0);
            table1.addCell(cell2);
            documento.add(table1);
            documento.add(new Paragraph("\n"));


            Font fontB = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
            Font fontC = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);


            documento.add(new Paragraph("\n"));

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(6);
            table.setWidthPercentage(100);
            tabla.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla.addCell(new Paragraph("Description", fontC));
            tabla.addCell(new Paragraph("Document number", fontC));
            tabla.addCell(new Paragraph("Legal invoice", fontC));
            tabla.addCell(new Paragraph("Amount", fontC));
            tabla.addCell(new Paragraph("Balance", fontC));
            tabla.addCell(new Paragraph("Total paid", fontC));

            tabla.setWidths(new float[]{26, 40, 16, 33, 33, 33});


            int conteo = 1;
            double precioTotal = 0;
            double totalRecaudado = 0;
            double precioTotalFac = 0;
            double precioTotalReal = 0;
            double precioDocumentos = 0;

            double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

            if (facCollection != null) {

                for (FacturasRealizadas cartera1 : facCollection) {
                    descripcionText = cartera1.codigoCliente;
                    precioTotalReal += Utilidades.formatearDecimales(cartera1.getValorDocumento(),2);
                    precioTotalReal = Utilidades.formatearDecimales(precioTotalReal,2);
                    precioTotal += cartera1.getMontoPendientes();
                    precioTotal = Utilidades.formatearDecimales(precioTotal,2);
                    totalRecaudado = cartera1.getValorConsignado();


                }



                for (FacturasRealizadas cartera1 : facCollection) {
                    descripcionText = cartera1.codigoCliente;

                    precioTotalFac += cartera1.getValorDocumento();
                    precioTotalFac = Utilidades.formatearDecimales(precioTotalFac,2);
                    precioDocumentos += cartera1.getValorDocumento();
                    precioDocumentos = Utilidades.formatearDecimales(precioDocumentos,2);

                    negocio = cartera1.sociedad;
                    codigoCliente = cartera1.codigoCliente;
                    numeroRecibo = cartera1.numeroRecibo;
                    documentoFactura = cartera1.documentoFactura;

                    tabla.addCell(cartera1.denominacion);
                    tabla.addCell(cartera1.documentoFactura);
                    tabla.addCell("");

                    if (salfoAFA == 0) {


                        salfoAFA = (precioTotal - precioTotalReal) * -1;

                    }



                    if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                            || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo);


                        if (cartera1.valorDocumento < 0) {
                            tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));

                        } else if (cartera1.valorDocumento > 0) {


                            if (cartera1.valorDocumento - cartera1.montoPendientes == 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));
                                }
                                //  tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));


                            } else if (cartera1.valorDocumento - cartera1.montoPendientes != 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    if (cartera1.montoPendientes != 0) {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));

                                    } else {

                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));

                                    }
                                }


                            }


                        }

                        if (salfoAFA < 0) {
                            if (cartera1.valorDocumento < 0) {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                            } else {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                            }
                        } else {

                            if (precioTotalReal == precioTotal) {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                }
                            } else {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.montoPendientes, 2))));
                                }

                            }


                        }


                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));


                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo);

                        if (cartera1.valorDocumento < 0) {
                            tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));

                        } else if (cartera1.valorDocumento > 0) {
                            if (cartera1.valorDocumento - cartera1.montoPendientes == 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));
                                }
                                //  tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));


                            } else if (cartera1.valorDocumento - cartera1.montoPendientes != 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    if (cartera1.montoPendientes != 0) {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));

                                    } else {

                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));

                                    }

                                }


                            }
                        }

                        /**     if (salfoAFA < 0) {

                         tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));

                         }else {**/

                        if (salfoAFA < 0) {
                            if (cartera1.valorDocumento < 0) {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                            } else {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                            }
                        } else {

                            if (precioTotalReal == precioTotal) {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                }
                            } else {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.montoPendientes, 2))));
                                }

                            }
                        }
                    }

                }
            }

            documento.add(tabla);


            documento.add(new Paragraph(""));


            if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                    || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                documento.add(new Paragraph(""));
                if (salfoAFA < 0) {


                    salfoAFA = salfoAFA * -1;


                    Paragraph parrafo2 = new Paragraph("                                                                                                                           Totally Canceled :       " + "   " + formatoNumero.format(Utilidades.formatearDecimales(precioDocumentos, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                            Total Collection :      " + "    " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    Paragraph parrafo223 = new Paragraph("                                                                                                                           Positive Balance :      " + "    " + formatoNumero.format(Utilidades.formatearDecimales(salfoAFA, 2)), font);
                    parrafo223.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo223));
                    documento.add(new Paragraph("\n"));


                } else {


                    salfoAFA = salfoAFA * -1;

                    Paragraph parrafo2 = new Paragraph("                                                                                                                           Totally Canceled :       " + "   " + formatoNumero.format(Utilidades.formatearDecimales(precioDocumentos, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                            Total Collection :      " + "    " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    documento.add(new Paragraph("\n"));


                }


            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));

                if (salfoAFA < 0) {


                    salfoAFA = salfoAFA * -1;


                    Paragraph parrafo2 = new Paragraph("                                                                                                                           Totally Canceled :       " + "   " + formatoNumero.format(Utilidades.formatearDecimales(precioDocumentos, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                            Total Collection :      " + "    " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    Paragraph parrafo223 = new Paragraph("                                                                                                                           Positive Balance :      " + "    " + formatoNumero.format(Utilidades.formatearDecimales(salfoAFA, 2)), font);
                    parrafo223.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo223));
                    documento.add(new Paragraph("\n"));


                } else {


                    salfoAFA = salfoAFA * -1;

                    Paragraph parrafo2 = new Paragraph("                                                                                                                          Totally Canceled :       " + "   " + formatoNumero.format(Utilidades.formatearDecimales(precioDocumentos, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                           Total Collection :       " + "    " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    documento.add(new Paragraph("\n"));


                }

            }


            // Insertamos segunda  tabla.


            listaFacturasRealizadas = new ArrayList<>();
            listaFacturasRealizadas = DataBaseBO.cargarFacturasViaPago(numeroRecibo);

            PdfPTable tabla1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            tabla1.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla1.addCell(new Paragraph("Payment Method", fontC));
            tabla1.addCell(new Paragraph("Total Collection", fontC));


            tabla1.setWidths(new float[]{25, 15});


            if (listaFacturasRealizadas != null) {

                String viaPago = "";

                for (FacturasRealizadas cartera1 : listaFacturasRealizadas) {

                    if (salfoAFA == 0) {


                        salfoAFA = precioTotal- precioTotalReal  ;

                    }

                    if (cartera1.viaPago.equals("A")) {
                        viaPago = ("CASH");
                    }
                    if (cartera1.viaPago.equals("B")) {
                        viaPago = ("CHECK");
                    }
                    if (cartera1.viaPago.equals("6")) {
                        viaPago = ("TRANSFER");
                    }
                    if (cartera1.viaPago.equals("O")) {
                        viaPago = ("CARD");
                    }
                    if (cartera1.viaPago.equals("4")) {
                        viaPago = ("BITCOIN");
                    }


                    if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                            || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                        if (salfoAFA > 0) {

                            if (sumaXValorConsignado >= precioTotalFac) {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));
                            } else {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));
                            }

                        }else{


                            tabla1.addCell(viaPago);

                            tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));


                        }
                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));

                        if (salfoAFA > 0) {

                            if (sumaXValorConsignado >= precioTotalFac) {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));
                            } else {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));
                            }

                        }else{


                            tabla1.addCell(viaPago);

                            tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));


                        }
                    }


                }
            }

            documento.add(tabla1);

            documento.add(new Paragraph("\n"));


            if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                    || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                Paragraph parrafo = new Paragraph("TOTAL COLLECTION: " + formatoNumero.format(Utilidades.formatearDecimales(totalRecaudado, 2)), font);

                parrafo.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(parrafo);

                documento.add(new Paragraph("\n"));


            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                Paragraph parrafo = new Paragraph("TOTAL COLLECTION: " + formatoNumero.format(Utilidades.formatearDecimales(totalRecaudado, 2)), font);

                parrafo.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(parrafo);

                documento.add(new Paragraph("\n"));


            }


            Paragraph cordialmente = new Paragraph("Observation: " + observaciones,
                    font);
            cordialmente.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar

            documento.add(new Paragraph(cordialmente));
            documento.add(new Paragraph("\n\n"));


            Paragraph texto = new Paragraph("THIS RECEIPT IS THE ONLY PROOF OF PAYMENT RECOGNIZED BY THE COMPANY, IT MUST NOT CONTAIN ALTERATIONS",
                    font);
            texto.setAlignment(Element.ALIGN_CENTER);//el 1 es para centrar


            documento.add(new Paragraph(texto));


            estado = true;

        } catch (DocumentException e) {

            Log.e("Error", e.getMessage());

        } catch (IOException e) {

            Log.e("Error", e.getMessage());

        } finally {
            // Cerramos el documento.
            documento.close();
//            Toasty.warning(getApplicationContext(), "Download The PDF Correctly, you can now view").show();
            return estado;
        }
    }

    public boolean generarCotizacionPdfUSANEW(){


        Boolean estado = false;
        String fechaDocumento = Utilidades.fechaActual("yyyyMMdd");
        String fecha = Utilidades.fechaActual("dd/MM/yyyy");
        String fechaRecibo = "";
        fechaRecibo = DataBaseBO.cargarFechaMaxReciboRealizados(numeroRecibo);
        String fechaRealizadosRecibo = " ";
        double salfoAFA;
        salfoAFA = DataBaseBO.SaldoAfavor(numeroRecibo);
        int i = 0;


        if (fechaRecibo != null) {
            if (fechaRecibo.equals("null")) {

                fechaRealizadosRecibo = "                   ";
            } else if (!fechaRecibo.equals("null")) {

                fechaRealizadosRecibo = fechaRecibo;
            }
        }

        // Creamos el documento.
        Document documento = new Document();
        String empresas = "";
        empresas = DataBaseBO.cargarEmpresa();

        try {


            File f = crearFichero("Comprobante.pdf");

            nombrePdf = "Comprobante" + ".pdf";

            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    f.getAbsolutePath());

            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            // Abrimos el documento.
            documento.open();

            if (operacion == null) {
                operacion = "RECEIPT";
            } else if (operacion != null) {


                if (operacion.equals("X")) {
                    operacion = "RECEIPT LEGALIZED";
                } else if (operacion.equals("A")) {
                    operacion = "RECEIPT ADVANCE";
                }


            }


            Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado = new Paragraph("          Company: " + empresa + "                      Receipt  " + operacion, font);


            /***************************************
             * LOGO DE LA EMPRESA                   *
             ***************************************/
            if (empresas.equals("AABR")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.alicapsa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);


            } else if (empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGCO")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.nutresa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(20, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGGC") || empresas.equals("AGSC")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pozuelo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGDP")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pops);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("ADHB")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bon);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGUC")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.cordialsa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            }

            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));

            /***************************************
             * ENCABEZADO DEL DOCUMENTO            *
             ***************************************/
            Font font66 = FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLD);
            Paragraph textoEncabezado66 = new Paragraph(operacion, font66);
            textoEncabezado66.setAlignment(Element.ALIGN_CENTER);//el 1 es para centrar
            documento.add(textoEncabezado66);

            documento.add(new Paragraph("\n"));

            Font font55 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado55 = new Paragraph("Consecutive: " + numeroRecibo, font55);
            textoEncabezado55.setAlignment(Element.ALIGN_RIGHT);//el 1 es para centrar
            documento.add(textoEncabezado55);
            documento.add(new Paragraph("\n"));

            if (cliente1 == null) {
                Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
                Paragraph textoEncabezado3 = new Paragraph("Customer Name: ", font3);
                textoEncabezado3.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(textoEncabezado3);
            } else {
                Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
                Paragraph textoEncabezado3 = new Paragraph("Customer Name: " + cliente1.nombre, font3);
                textoEncabezado3.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(textoEncabezado3);
            }

            Font font54 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            Paragraph textoEncabezado35 = new Paragraph("Customer Code: " + codigoCliente, font54);
            textoEncabezado35.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
            documento.add(textoEncabezado35);

            Font font2 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            Paragraph textoEncabezado2 = new Paragraph("Created by: " + vendedor, font2);
            textoEncabezado2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
            documento.add(textoEncabezado2);

            Font font4 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            Paragraph textoEncabezadoUno = new Paragraph("Date: " + fechaRealizadosRecibo.substring(0,11), font4);
            textoEncabezadoUno.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
            documento.add(textoEncabezadoUno);

            PdfPCell cell2 = new PdfPCell();
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setPaddingTop(-7);
            cell2.setRightIndent(12);
//            cell2.addElement(textoEncabezado);
//            cell2.addElement(textoEncabezado1);
//            cell2.addElement(textoEncabezado2);
            cell2.setColspan(4);


            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 2});
            table.getDefaultCell().setBorder(0);
            //table.addCell(imagen);
            // documento.add(textoEncabezado);


            documento.add(new Paragraph("\n"));


            PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            table1.setWidths(new int[]{1, 2});
            table1.getDefaultCell().setBorder(0);
            table1.addCell(cell2);
            documento.add(table1);
            documento.add(new Paragraph("\n"));


            Font fontB = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
            Font fontC = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);

            /***************************************
             * TABLA DETALLE DEL DOCUMENTO         *
             ***************************************/
            Font font44 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado44 = new Paragraph("DOCUMENT DETAIL", font44);
            textoEncabezado44.setAlignment(Element.ALIGN_CENTER);
            documento.add(textoEncabezado44);


            documento.add(new Paragraph("\n"));

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(5);
            table.setWidthPercentage(100);
            tabla.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla.addCell(new Paragraph("Document Type", fontC));
            tabla.addCell(new Paragraph("Document number", fontC));
//            tabla.addCell(new Paragraph("Legal invoice", fontC));
            tabla.addCell(new Paragraph("Document Amount", fontC));
            tabla.addCell(new Paragraph("Paid", fontC));
            tabla.addCell(new Paragraph("Balance", fontC));

            tabla.setWidths(new float[]{26, 40, 33, 33, 33});


            int conteo = 1;
            double precioTotal = 0;
            double precioTotalFac = 0;
            double precioTotalReal = 0;
            double precioDocumentos = 0;

            double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

            if (facCollection != null) {

                for (FacturasRealizadas cartera1 : facCollection) {
                    descripcionText = cartera1.codigoCliente;
                    precioTotalReal += Utilidades.formatearDecimales(cartera1.getValorDocumento(),2);
                    precioTotalReal = Utilidades.formatearDecimales(precioTotalReal,2);
                    precioTotal += cartera1.getMontoPendientes();
                    precioTotal = Utilidades.formatearDecimales(precioTotal,2);


                }



                for (FacturasRealizadas cartera1 : facCollection) {
                    descripcionText = cartera1.codigoCliente;

                    precioTotalFac += cartera1.getValorDocumento();
                    precioTotalFac = Utilidades.formatearDecimales(precioTotalFac,2);
                    precioDocumentos += cartera1.getValorDocumento();
                    precioDocumentos = Utilidades.formatearDecimales(precioDocumentos,2);

                    negocio = cartera1.sociedad;
                    codigoCliente = cartera1.codigoCliente;
                    numeroRecibo = cartera1.numeroRecibo;
                    documentoFactura = cartera1.documentoFactura;

                    tabla.addCell(cartera1.claseDocumento);
                    tabla.addCell(cartera1.documentoFactura);
//                    tabla.addCell("");

                    if (salfoAFA == 0) {


                        salfoAFA = (precioTotal - precioTotalReal) * -1;

                    }



                    if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                            || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo);


                        if (cartera1.valorDocumento < 0) {
                            tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));

                        } else if (cartera1.valorDocumento > 0) {


                            if (cartera1.valorDocumento - cartera1.montoPendientes == 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));
                                }
                                //  tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));


                            } else if (cartera1.valorDocumento - cartera1.montoPendientes != 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    if (cartera1.montoPendientes != 0) {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));

                                    } else {

                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));

                                    }
                                }


                            }


                        }

                        if (salfoAFA < 0) {
                            if (cartera1.valorDocumento < 0) {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                            } else {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                            }
                        } else {

                            if (precioTotalReal == precioTotal) {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                }
                            } else {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.montoPendientes, 2))));
                                }

                            }


                        }


                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));


                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo);

                        /**     if (salfoAFA < 0) {

                         tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));

                         }else {**/

                        /**************************************************
                         * CAMPO TOTAL CANCELADO                          *
                         **************************************************/
                        if (salfoAFA < 0) {
                            if (cartera1.valorDocumento < 0) {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                            } else {
                                if(i == 0)
                                {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento + (salfoAFA *-1), 2))));
                                }
                                else
                                {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                }
                            }
                        } else {

                            if (precioTotalReal == precioTotal) {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                }
                            } else {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.montoPendientes, 2))));
                                }

                            }
                        }

                        /**************************************************
                         * CAMPO BALANCE                                    *
                         **************************************************/
                        if (cartera1.valorDocumento < 0) {
                            tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));

                        } else if (cartera1.valorDocumento > 0) {
                            if (cartera1.valorDocumento - cartera1.montoPendientes == 0) {

                                if (salfoAFA < 0) {
                                    if(i == 0)
                                    {
                                        tabla.addCell(String.valueOf(Utilidades.formatearDecimales(salfoAFA, 2)));
                                    }
                                    else
                                    {
                                        tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                    }
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));
                                }
                                //  tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));


                            } else if (cartera1.valorDocumento - cartera1.montoPendientes != 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(salfoAFA, 2)));
                                } else {
                                    if (cartera1.montoPendientes != 0) {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));

                                    } else {

                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));

                                    }

                                }


                            }
                        }
                    }
                    i += 1;
                }
            }

            documento.add(tabla);

            // Insertamos una tabla.
            tabla = new PdfPTable(4);
            table.setWidthPercentage(100);
            tabla.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla.addCell(new Paragraph("", fontC));
            tabla.addCell(new Paragraph("TOTAL", fontC));

            if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                    || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                documento.add(new Paragraph(""));
                if (salfoAFA < 0) {


                    salfoAFA = salfoAFA * -1;


                    Paragraph parrafo2 = new Paragraph("                                                                                                                           Totally Canceled :       " + "   " + formatoNumero.format(Utilidades.formatearDecimales(precioDocumentos, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                            Total Collection :      " + "    " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    Paragraph parrafo223 = new Paragraph("                                                                                                                           Positive Balance :      " + "    " + formatoNumero.format(Utilidades.formatearDecimales(salfoAFA, 2)), font);
                    parrafo223.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo223));
                    documento.add(new Paragraph("\n"));


                } else {


                    salfoAFA = salfoAFA * -1;

                    Paragraph parrafo2 = new Paragraph("                                                                                                                           Totally Canceled :       " + "   " + formatoNumero.format(Utilidades.formatearDecimales(precioDocumentos, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                            Total Collection :      " + "    " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    documento.add(new Paragraph("\n"));


                }


            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));

                if (salfoAFA < 0) {

                    sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;


//                    Paragraph parrafo2 = new Paragraph("                                                                                                                              Total cancelado :      " + formatoNumero.format(Utilidades.formatearDecimales(precioTotalFac, 2)), font);
//                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(parrafo2);
//
//
//                    Paragraph parrafo22 = new Paragraph("                                                                                                                                  Total pagado :       " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
//                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(new Paragraph(parrafo22));
//
//
//                    Paragraph parrafo223 = new Paragraph("                                                                                                                                  Saldo a favor :       " + formatoNumero.format(Utilidades.formatearDecimales(salfoAFA, 2)), font);
//                    parrafo223.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(new Paragraph(parrafo223));
//                    documento.add(new Paragraph("\n"));

                    tabla.addCell(new Paragraph(formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), fontC));
                    tabla.addCell(new Paragraph(formatoNumero.format(Utilidades.formatearDecimales(salfoAFA * -1, 2)), fontC));

                } else {

                    sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;

//                    Paragraph parrafo2 = new Paragraph("                                                                                                                          Totally Canceled :       " + "   " + formatoNumero.format(Utilidades.formatearDecimales(precioDocumentos, 2)), font);
//                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(parrafo2);
//
//
//                    Paragraph parrafo22 = new Paragraph("                                                                                                                           Total Collection :       " + "    " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
//                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(new Paragraph(parrafo22));

//                    documento.add(new Paragraph("\n"));

                    tabla.addCell(new Paragraph(formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), fontC));
                    tabla.addCell(new Paragraph(formatoNumero.format(Utilidades.formatearDecimales(salfoAFA * -1, 2)), fontC));

                }

            }

            tabla.setWidths(new float[]{20, 10, 10, 10});
            documento.add(tabla);

            documento.add(new Paragraph("\n"));

            /***************************************
             * TABLA DETALLE DEL PAGO              *
             ***************************************/
            font44 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            textoEncabezado44 = new Paragraph("PAYMENT DETAILS", font44);
            textoEncabezado44.setAlignment(Element.ALIGN_CENTER);
            documento.add(textoEncabezado44);

            documento.add(new Paragraph("\n"));

            // Insertamos segunda  tabla.
            listaFacturasRealizadas = new ArrayList<>();

            if(empresas.equals("AGUC"))
            {
                listaFacturasRealizadas = DataBaseBO.cargarFacturasViaPagoAGUC(numeroRecibo);
            }
            else
            {
                listaFacturasRealizadas = DataBaseBO.cargarFacturasViaPago(numeroRecibo);
            }

            PdfPTable tabla1 = new PdfPTable(4);
            table1.setWidthPercentage(100);
            tabla1.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla1.addCell(new Paragraph("Payment Method", fontC));
            tabla1.addCell(new Paragraph("Check #", fontC));
            tabla1.addCell(new Paragraph("Bank", fontC));
            tabla1.addCell(new Paragraph("Amount", fontC));


            tabla1.setWidths(new float[]{25, 25,25, 15});


            if (listaFacturasRealizadas != null) {

                String viaPago = "";

                for (FacturasRealizadas cartera1 : listaFacturasRealizadas) {

                    if (salfoAFA == 0) {


                        salfoAFA = precioTotal- precioTotalReal  ;

                    }

                    if (cartera1.viaPago.equals("A")) {
                        viaPago = ("CASH");
                    }
                    if (cartera1.viaPago.equals("B")) {
                        viaPago = ("CHECK");
                    }
                    if (cartera1.viaPago.equals("6")) {
                        viaPago = ("TRANSFER");
                    }
                    if (cartera1.viaPago.equals("O")) {
                        viaPago = ("CARD");
                    }
                    if (cartera1.viaPago.equals("4")) {
                        viaPago = ("BITCOIN");
                    }


                    if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                            || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                        if (salfoAFA > 0) {

                            if (sumaXValorConsignado >= precioTotalFac) {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));
                            } else {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));
                            }

                        }else{


                            tabla1.addCell(viaPago);

                            tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));


                        }
                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));

                        if (salfoAFA > 0) {

                            if (sumaXValorConsignado >= precioTotalFac) {
                                tabla1.addCell(viaPago);
                                tabla1.addCell(cartera1.numeroCheqe);
                                tabla1.addCell(cartera1.banco);
                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));
                            } else {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));
                            }

                        }else{


                            tabla1.addCell(viaPago);
                            tabla1.addCell(cartera1.numeroCheqe);
                            tabla1.addCell(cartera1.banco);
                            tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));


                        }
                    }


                }
            }

            documento.add(tabla1);

            tabla1 = new PdfPTable(3);
            table1.setWidthPercentage(100);
            tabla1.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla1.addCell(new Paragraph("", fontC));
            tabla1.addCell(new Paragraph("TOTAL", fontC));


            if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                    || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                Paragraph parrafo = new Paragraph("TOTAL COLLECTION: " + formatoNumero.format(Utilidades.formatearDecimales(precioTotal, 2)), font);

                parrafo.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(parrafo);

                documento.add(new Paragraph("\n"));


            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
//                Paragraph parrafo = new Paragraph("TOTAL COLLECTION: " + formatoNumero.format(Utilidades.formatearDecimales(precioTotal, 2)), font);
//                parrafo.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                documento.add(parrafo);

                if(salfoAFA > 0)
                {
                    sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));
                    tabla1.addCell(formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)));
                }
                else
                {
                    tabla1.addCell(formatoNumero.format(Utilidades.formatearDecimales(precioTotal, 2)));
                }
            }

            tabla1.setWidths(new float[]{50, 25, 15});

            documento.add(tabla1);

            Paragraph cordialmente = new Paragraph("Observation: " + observaciones,
                    font);
            cordialmente.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar

            documento.add(new Paragraph(cordialmente));
            documento.add(new Paragraph("\n\n"));

            //FIRMA Y NOMBRE DE VENTEDOR EN CASO DE QUE CORRESPONDA CON LA EMPRESA
            if(empresas.equals("AGUC"))
            {
                for (FacturasRealizadas factura : facCollection) {

                    //CARGA LA INFORMACION DE LA FIRMA SEGUNDO EL ID PAGO
                    FirmaNombre firmaNombre = DataBaseBO.cargarFirmaNombre(factura.numeroRecibo);

                    //CARGAR LA IMAGEN DE LA FIRMA
                    Bitmap bitmap = BitmapFactory.decodeByteArray(firmaNombre.firma, 0, firmaNombre.firma.length);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    Image imagen = Image.getInstance(baos.toByteArray());
                    imagen.scaleToFit(90, 90);

                    PdfPTable tablaFirma = new PdfPTable(4);
                    tablaFirma.setWidthPercentage(100);
                    tablaFirma.setWidthPercentage(100);

                    PdfPCell cell;

                    //PRIMERA FILA
                    cell = new PdfPCell(new Paragraph(firmaNombre.firmaNombre,font));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    cell = new PdfPCell(new Paragraph("",font));
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    cell = new PdfPCell(imagen);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    cell = new PdfPCell(new Paragraph("",font));
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    //TERCERA FILA
                    cell = new PdfPCell(new Paragraph("------------------------------------------------",font));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    cell = new PdfPCell(new Paragraph("",font));
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    cell = new PdfPCell(new Paragraph("------------------------------------------------",font));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    cell = new PdfPCell(new Paragraph("",font));
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    //SEGUNDA FILA
                    cell = new PdfPCell(new Paragraph("Print Name",font));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    cell = new PdfPCell(new Paragraph("",font));
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    cell = new PdfPCell(new Paragraph("Signature",font));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    cell = new PdfPCell(new Paragraph("",font));
                    cell.setBorderColor(BaseColor.WHITE);
                    tablaFirma.addCell(cell);

                    tablaFirma.setWidths(new float[]{12,2, 12, 12});

                    documento.add(tablaFirma);

                    break;
                }
            }


            Paragraph texto = new Paragraph("THIS RECEIPT IS THE ONLY PROOF OF PAYMENT RECOGNIZED BY THE COMPANY, IT MUST NOT CONTAIN ALTERATIONS",
                    font);
            texto.setAlignment(Element.ALIGN_CENTER);//el 1 es para centrar


            documento.add(new Paragraph(texto));


            estado = true;

        } catch (DocumentException e) {

            Log.e("Error", e.getMessage());

        } catch (IOException e) {

            Log.e("Error", e.getMessage());

        } finally {
            // Cerramos el documento.
            documento.close();
//            Toasty.warning(getApplicationContext(), "Download The PDF Correctly, you can now view").show();
            return estado;
        }
    }

    public boolean generarCotizacionPdf() {


        Boolean estado = false;
        String fechaDocumento = Utilidades.fechaActual("yyyyMMdd");
        String fecha = Utilidades.fechaActual("dd/MM/yyyy");
        String fechaRecibo = "";
        fechaRecibo = DataBaseBO.cargarFechaMaxReciboRealizados(numeroRecibo);
        String fechaRealizadosRecibo = " ";
        double salfoAFA;
        salfoAFA = DataBaseBO.SaldoAfavor(numeroRecibo);


        if (fechaRecibo != null) {
            if (fechaRecibo.equals("null")) {

                fechaRealizadosRecibo = "                   ";
            } else if (!fechaRecibo.equals("null")) {

                fechaRealizadosRecibo = fechaRecibo;
            }
        }

        // Creamos el documento.
        Document documento = new Document();
        String empresas = "";
        empresas = DataBaseBO.cargarEmpresa();

//        double DiferenciaFormasPagoE = Utilidades.totalFormasPagoFacRealizadas(getApplicationContext(), numeroRecibo);

        try {
            File f = crearFichero("Comprobante.pdf");


            nombrePdf = "Comprobante" + ".pdf";

            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    f.getAbsolutePath());

            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            // Abrimos el documento.
            documento.open();

            if (operacion == null) {
                operacion = "  ";
            } else if (operacion != null) {


                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        if (operacion.equals("X")) {
                            operacion = "Receipt to legalize";
                        } else if (operacion.equals("A")) {
                            operacion = "Advance";
                        }


                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        if (operacion.equals("X")) {
                            operacion = "Recibo por legalizar";
                        } else if (operacion.equals("A")) {
                            operacion = "Anticipo";
                        }

                    }
                }


            }


            Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);

            if (empresas.equals("AABR")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.alicapsa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);


            } else if (empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGCO")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.nutresa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGGC") || empresas.equals("AGSC")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pozuelo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGDP")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pops);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("ADHB")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bon);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGUC")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.cordialsa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            }


            Font font4 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezadoUno = new Paragraph("\n" + "                              Fecha: " + fechaRealizadosRecibo + "                     Consecutivo: " + numeroRecibo, font4);
            textoEncabezadoUno.setAlignment(Element.ALIGN_CENTER);//el 1 es para centrar


            Font font2 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado2 = new Paragraph("\n" + "                                                  Creado por: " + vendedor, font2);
            textoEncabezado2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar

            if (cliente1 == null) {
                Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
                Paragraph textoEncabezado3 = new Paragraph("\n" + "                                                  Cliente: " + "                          ", font3);
                textoEncabezado3.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(textoEncabezado3);
            } else {
                Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
                Paragraph textoEncabezado3 = new Paragraph("\n" + "                                                  Cliente: " + cliente1.nombre, font3);
                textoEncabezado3.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(textoEncabezado3);
            }

            Font font54 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado35 = new Paragraph("\n" + "                                                 Codigo: " + codigoCliente, font54);
            textoEncabezado35.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar


            PdfPCell cell2 = new PdfPCell();
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setPaddingTop(-7);
            cell2.setRightIndent(12);
//            cell2.addElement(textoEncabezado);
//            cell2.addElement(textoEncabezado1);
//            cell2.addElement(textoEncabezado2);
            cell2.setColspan(4);


            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 2});
            table.getDefaultCell().setBorder(0);
            //table.addCell(imagen);
            // documento.add(textoEncabezado);
            documento.add(textoEncabezadoUno);
            documento.add(textoEncabezado2);

            documento.add(textoEncabezado35);


            documento.add(new Paragraph("\n"));


            PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            table1.setWidths(new int[]{1, 2});
            table1.getDefaultCell().setBorder(0);
            table1.addCell(cell2);
            documento.add(table1);
            documento.add(new Paragraph("\n"));


            Font fontB = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
            Font fontC = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);


            documento.add(new Paragraph("\n"));

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(6);
            table.setWidthPercentage(100);
            tabla.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla.addCell(new Paragraph("Descripcion", fontC));
            tabla.addCell(new Paragraph("Factura", fontC));
            tabla.addCell(new Paragraph("Factura legal", fontC));
            tabla.addCell(new Paragraph("Importe", fontC));
            tabla.addCell(new Paragraph("Saldo", fontC));
            tabla.addCell(new Paragraph("Total cancelado", fontC));

            tabla.setWidths(new float[]{26, 40, 16, 33, 33, 33});


            int conteo = 1;
            double precioTotal = 0;
            double totalRecaudado = 0;
            double precioTotalFac = 0;
            double precioTotalReal = 0;
            double precioDocumentos = 0;


            if (facCollection != null) {

                for (FacturasRealizadas cartera1 : facCollection) {
                    descripcionText = cartera1.codigoCliente;
                    precioTotalReal += Utilidades.formatearDecimales(cartera1.getValorDocumento(),2);
                    precioTotalReal = Utilidades.formatearDecimales(precioTotalReal,2);

                    precioTotal += cartera1.getMontoPendientes();
                    precioTotal = Utilidades.formatearDecimales(precioTotal,2);
                    totalRecaudado = cartera1.getValorConsignado();

                }


                for (FacturasRealizadas cartera1 : facCollection) {
                    descripcionText = cartera1.codigoCliente;

                    precioTotalFac += cartera1.getValorDocumento();
                    precioTotalFac = Utilidades.formatearDecimales(precioTotalFac,2);
                    precioDocumentos += cartera1.getValorDocumento();
                    precioDocumentos = Utilidades.formatearDecimales(precioDocumentos,2);


                    negocio = cartera1.sociedad;
                    codigoCliente = cartera1.codigoCliente;
                    numeroRecibo = cartera1.numeroRecibo;
                    documentoFactura = cartera1.documentoFactura;

                    tabla.addCell(cartera1.denominacion);
                    tabla.addCell(cartera1.documentoFactura);
                    tabla.addCell("");


                    if (salfoAFA == 0) {


                        salfoAFA = (precioTotal - precioTotalReal) * -1;

                    }




                    if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                            || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo);


                        if (cartera1.valorDocumento < 0) {
                            tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));

                        } else if (cartera1.valorDocumento > 0) {


                            if (cartera1.valorDocumento - cartera1.montoPendientes == 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));
                                }
                                //  tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));


                            } else if (cartera1.valorDocumento - cartera1.montoPendientes != 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    if (cartera1.montoPendientes != 0) {
                                        double saldoN = cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos);
                                        if(saldoN < 0)
                                        {
                                            tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                        }
                                        else
                                        {
                                            tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));
                                        }
                                    } else {

                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));

                                    }


                                }
                                // tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));


                            }


                        }

                        if (salfoAFA < 0) {

                            if (cartera1.valorDocumento < 0) {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                            } else {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                            }

                        } else {


                            if (precioTotalReal == precioTotal) {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                }
                            } else {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.montoPendientes, 2))));
                                }

                            }

                        }


                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));


                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo);

                        if (cartera1.valorDocumento < 0) {
                            tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));

                        } else if (cartera1.valorDocumento > 0) {

                            if (cartera1.valorDocumento - cartera1.montoPendientes == 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));
                                }
                                //  tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));


                            } else if (cartera1.valorDocumento - cartera1.montoPendientes != 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    if (cartera1.montoPendientes != 0) {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));

                                    } else {

                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));

                                    }

                                }


                            }

                        }


                        if (salfoAFA < 0) {

                            if (cartera1.valorDocumento < 0) {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                            } else {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                            }

                        } else {


                            if (precioTotalReal == precioTotal) {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                }
                            } else {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.montoPendientes, 2))));
                                }

                            }
                        }
                    }

                }
            }


            documento.add(tabla);


            documento.add(new Paragraph(""));


            if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                    || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                if (salfoAFA < 0) {
                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;


                    Paragraph parrafo2 = new Paragraph("                                                                                                                             Total cancelado :      " + formatoNumero.format(Utilidades.formatearDecimales(precioTotalFac, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                                 Total pagado :       " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    Paragraph parrafo223 = new Paragraph("                                                                                                                                 Saldo a favor :       " + formatoNumero.format(Utilidades.formatearDecimales(salfoAFA, 2)), font);
                    parrafo223.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo223));
                    documento.add(new Paragraph("\n"));


                } else {

                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;

                    Paragraph parrafo2 = new Paragraph("                                                                                                                           Total cancelado :      " + formatoNumero.format(Utilidades.formatearDecimales(precioTotalFac, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                               Total pagado :       " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    documento.add(new Paragraph("\n"));


                }


            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));


                if (salfoAFA < 0) {
                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;


                    Paragraph parrafo2 = new Paragraph("                                                                                                                              Total cancelado :      " + formatoNumero.format(Utilidades.formatearDecimales(precioTotalFac, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                                  Total pagado :       " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    Paragraph parrafo223 = new Paragraph("                                                                                                                                  Saldo a favor :       " + formatoNumero.format(Utilidades.formatearDecimales(salfoAFA, 2)), font);
                    parrafo223.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo223));
                    documento.add(new Paragraph("\n"));


                } else {

                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;

                    Paragraph parrafo2 = new Paragraph("                                                                                                                                 Total cancelado :      " + formatoNumero.format(Utilidades.formatearDecimales(precioDocumentos, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                                     Total pagado :       " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    documento.add(new Paragraph("\n"));


                }


            }


            // Insertamos segunda  tabla.


            listaFacturasRealizadas = new ArrayList<>();
            listaFacturasRealizadas = DataBaseBO.cargarFacturasViaPago(numeroRecibo);

            PdfPTable tabla1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            tabla1.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla1.addCell(new Paragraph("Metodo de pago", fontC));
            tabla1.addCell(new Paragraph("Valor a pagar", fontC));


            tabla1.setWidths(new float[]{25, 15});


            if (listaFacturasRealizadas != null) {

                String viaPago = "";

                for (FacturasRealizadas cartera1 : listaFacturasRealizadas) {

                    if (salfoAFA == 0) {


                        salfoAFA = precioTotal- precioTotalReal  ;

                    }

                    if (cartera1.viaPago.equals("A")) {
                        viaPago = ("EFECTIVO");
                    }
                    if (cartera1.viaPago.equals("B")) {
                        viaPago = ("CHEQUE");
                    }
                    if (cartera1.viaPago.equals("6")) {
                        viaPago = ("TRANSFERENCIA");
                    }
                    if (cartera1.viaPago.equals("O")) {
                        viaPago = ("TARJETA");
                    }
                    if (cartera1.viaPago.equals("4")) {
                        viaPago = ("BITCOIN");
                    }

                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                            || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                        if (salfoAFA > 0) {

                            if (sumaXValorConsignado >= precioTotalFac) {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));
                            } else {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));
                            }

                        }else{


                            tabla1.addCell(viaPago);

                            tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));


                        }

                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));



                        if (salfoAFA > 0) {

                            if (sumaXValorConsignado >= precioTotalFac) {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));
                            } else {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));
                            }

                        }else{


                            tabla1.addCell(viaPago);

                            tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));


                        }
                    }


                }
            }

            documento.add(tabla1);

            documento.add(new Paragraph("\n"));


            if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                    || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                Paragraph parrafo = new Paragraph("TOTAL RECAUDO: " + formatoNumero.format(Utilidades.formatearDecimales(totalRecaudado, 2)), font);

                parrafo.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(parrafo);

                documento.add(new Paragraph("\n"));


            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
                Paragraph parrafo = new Paragraph("TOTAL RECAUDO: " + formatoNumero.format(Utilidades.formatearDecimales(totalRecaudado, 2)), font);

                parrafo.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(parrafo);

                documento.add(new Paragraph("\n"));


            }


            Paragraph cordialmente = new Paragraph("Observaciones: " + observaciones,
                    font);
            cordialmente.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar

            documento.add(new Paragraph(cordialmente));
            documento.add(new Paragraph("\n\n"));


            Paragraph texto = new Paragraph("ESTE RECIBO ES EL UNICO COMPROBANTE DE PAGO RECONOCIDO POR LA EMPRESA,\n" +
                    " NO DEBE CONTENER ALTERACIONES ",
                    font);
            texto.setAlignment(Element.ALIGN_CENTER);//el 1 es para centrar


            documento.add(new Paragraph(texto));


            estado = true;

        } catch (DocumentException e) {

            Log.e("Error", e.getMessage());

        } catch (IOException e) {

            Log.e("Error", e.getMessage());

        } finally {
            // Cerramos el documento.
            documento.close();
//            Toasty.warning(getApplicationContext(), "Se Descargo El PDF Correctamente, ya se puede visualizar").show();
            return estado;
        }
    }

    public boolean generarCotizacionPdfNEW() {


        Boolean estado = false;
        String fechaDocumento = Utilidades.fechaActual("yyyyMMdd");
        String fecha = Utilidades.fechaActual("dd/MM/yyyy");
        String fechaRecibo = "";
        fechaRecibo = DataBaseBO.cargarFechaMaxReciboRealizados(numeroRecibo);
        String fechaRealizadosRecibo = " ";
        double salfoAFA;
        salfoAFA = DataBaseBO.SaldoAfavor(numeroRecibo);
        int i = 0;


        if (fechaRecibo != null) {
            if (fechaRecibo.equals("null")) {

                fechaRealizadosRecibo = "                   ";
            } else if (!fechaRecibo.equals("null")) {

                fechaRealizadosRecibo = fechaRecibo;
            }
        }

        // Creamos el documento.
        Document documento = new Document();
        String empresas = "";
        empresas = DataBaseBO.cargarEmpresa();

//        double DiferenciaFormasPagoE = Utilidades.totalFormasPagoFacRealizadas(getApplicationContext(), numeroRecibo);

        try {
            File f = crearFichero("Comprobante.pdf");


            nombrePdf = "Comprobante" + ".pdf";

            // Creamos el flujo de datos de salida para el fichero donde
            // guardaremos el pdf.
            FileOutputStream ficheroPdf = new FileOutputStream(
                    f.getAbsolutePath());

            // Asociamos el flujo que acabamos de crear al documento.
            PdfWriter writer = PdfWriter.getInstance(documento, ficheroPdf);

            // Abrimos el documento.
            documento.open();

            if (operacion == null) {
                operacion = "RECIBO";
            } else if (operacion != null) {


                if (lenguajeElegido == null) {

                } else if (lenguajeElegido != null) {
                    if (lenguajeElegido.lenguaje.equals("USA")) {

                        if (operacion.equals("X")) {
                            operacion = "RECEIPT LEGALIZED";
                        } else if (operacion.equals("A")) {
                            operacion = "RECEIPT ADVANCE";
                        }


                    } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                        if (operacion.equals("X")) {
                            operacion = "RECIBO LEGALIZADO";
                        } else if (operacion.equals("A")) {
                            operacion = "RECIBO ANTICIPO";
                        }

                    }
                }


            }


            Font font = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);

            if (empresas.equals("AABR")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.alicapsa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);


            } else if (empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGCO")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.nutresa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGGC") || empresas.equals("AGSC")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pozuelo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGDP")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pops);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("ADHB")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bon);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            } else if (empresas.equals("AGUC")) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.cordialsa);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image imagen = Image.getInstance(stream.toByteArray());
                imagen.scaleToFit(90, 90);
                imagen.setAbsolutePosition(50, 700);
                documento.add(imagen);

            }

            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("\n"));

            /***************************************
             * ENCABEZADO DEL DOCUMENTO            *
             ***************************************/
            Font font66 = FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLD);
            Paragraph textoEncabezado66 = new Paragraph(operacion, font66);
            textoEncabezado66.setAlignment(Element.ALIGN_CENTER);//el 1 es para centrar
            documento.add(textoEncabezado66);

            documento.add(new Paragraph("\n"));

            Font font55 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado55 = new Paragraph("Consecutivo: " + numeroRecibo, font55);
            textoEncabezado55.setAlignment(Element.ALIGN_RIGHT);//el 1 es para centrar
            documento.add(textoEncabezado55);

            if (cliente1 == null) {
                Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
                Paragraph textoEncabezado3 = new Paragraph("Cliente: " + "                          ", font3);
                textoEncabezado3.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(textoEncabezado3);
            } else {
                Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
                Paragraph textoEncabezado3 = new Paragraph("Cliente: " + cliente1.nombre, font3);
                textoEncabezado3.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(textoEncabezado3);
            }

            Font font2 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            Paragraph textoEncabezado2 = new Paragraph("Creado por: " + vendedor, font2);
            textoEncabezado2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
            documento.add(textoEncabezado2);

            Font font54 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            Paragraph textoEncabezado35 = new Paragraph("Código: " + codigoCliente, font54);
            textoEncabezado35.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
            documento.add(textoEncabezado35);

            Font font4 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            Paragraph textoEncabezadoUno = new Paragraph("Fecha: " + fechaRealizadosRecibo.substring(0,11), font4);
            textoEncabezadoUno.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
            documento.add(textoEncabezadoUno);

            PdfPCell cell2 = new PdfPCell();
            cell2.setBorder(Rectangle.NO_BORDER);
            cell2.setPaddingTop(-7);
            cell2.setRightIndent(12);
//            cell2.addElement(textoEncabezado);
//            cell2.addElement(textoEncabezado1);
//            cell2.addElement(textoEncabezado2);
            cell2.setColspan(4);


            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 2});
            table.getDefaultCell().setBorder(0);
            //table.addCell(imagen);
            // documento.add(textoEncabezado);

            PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            table1.setWidths(new int[]{1, 2});
            table1.getDefaultCell().setBorder(0);
            table1.addCell(cell2);
            documento.add(table1);
            documento.add(new Paragraph("\n"));


            Font fontB = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD);
            Font fontC = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);

            /***************************************
             * TABLA DETALLE DEL DOCUMENTO         *
             ***************************************/
            Font font44 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            Paragraph textoEncabezado44 = new Paragraph("DATALLE DEL DOCUMENTO", font44);
            textoEncabezado44.setAlignment(Element.ALIGN_CENTER);
            documento.add(textoEncabezado44);

            documento.add(new Paragraph("\n"));

            // Insertamos una tabla.
            PdfPTable tabla = new PdfPTable(5);
            table.setWidthPercentage(100);
            tabla.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla.addCell(new Paragraph("Descripcion", fontC));
            tabla.addCell(new Paragraph("Factura", fontC));
            tabla.addCell(new Paragraph("Importe", fontC));
            tabla.addCell(new Paragraph("Total cancelado", fontC));
            tabla.addCell(new Paragraph("Saldo", fontC));

            tabla.setWidths(new float[]{26, 40, 33, 33, 33});


            int conteo = 1;
            double precioTotal = 0;
            double precioTotalFac = 0;
            double precioTotalReal = 0;
            double precioDocumentos = 0;


            if (facCollection != null) {

                for (FacturasRealizadas cartera1 : facCollection) {
                    descripcionText = cartera1.codigoCliente;
                    precioTotalReal += Utilidades.formatearDecimales(cartera1.getValorDocumento(),2);
                    precioTotalReal = Utilidades.formatearDecimales(precioTotalReal,2);

                    precioTotal += cartera1.getMontoPendientes();
                    precioTotal = Utilidades.formatearDecimales(precioTotal,2);




                }


                for (FacturasRealizadas cartera1 : facCollection) {
                    descripcionText = cartera1.codigoCliente;

                    precioTotalFac += cartera1.getValorDocumento();
                    precioTotalFac = Utilidades.formatearDecimales(precioTotalFac,2);
                    precioDocumentos += cartera1.getValorDocumento();
                    precioDocumentos = Utilidades.formatearDecimales(precioDocumentos,2);


                    negocio = cartera1.sociedad;
                    codigoCliente = cartera1.codigoCliente;
                    numeroRecibo = cartera1.numeroRecibo;
                    documentoFactura = cartera1.documentoFactura;

                    tabla.addCell(cartera1.claseDocumento);
                    tabla.addCell(cartera1.documentoFactura);


                    if (salfoAFA == 0) {


                        salfoAFA = (precioTotal - precioTotalReal) * -1;

                    }




                    if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                            || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo);


                        if (cartera1.valorDocumento < 0) {
                            tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));

                        } else if (cartera1.valorDocumento > 0) {


                            if (cartera1.valorDocumento - cartera1.montoPendientes == 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));
                                }
                                //  tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));


                            } else if (cartera1.valorDocumento - cartera1.montoPendientes != 0) {

                                if (salfoAFA < 0) {
                                    tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                } else {
                                    if (cartera1.montoPendientes != 0) {
                                        double saldoN = cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos);
                                        if(saldoN < 0)
                                        {
                                            tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                        }
                                        else
                                        {
                                            tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));
                                        }
                                    } else {

                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));

                                    }


                                }
                                // tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));


                            }


                        }

                        if (salfoAFA < 0) {

                            if (cartera1.valorDocumento < 0) {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                            } else {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                            }

                        } else {


                            if (precioTotalReal == precioTotal) {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                }
                            } else {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.montoPendientes, 2))));
                                }

                            }

                        }


                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));


                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo);

                        /**********************************
                         *TOTAL CANCELADO                 *
                         **********************************/
                        if (salfoAFA < 0) {

                            if (cartera1.valorDocumento < 0) {
                                tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                            } else {
                                if(i == 0)
                                {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento + (salfoAFA*-1), 2))));
                                }
                                else
                                {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                }
                            }

                        } else {


                            if (precioTotalReal == precioTotal) {

                                //SI EL PAGO REAL ES MENOR AL VALOR DEL DOCUMENTO, SE MUESTRA EL VALOR CONSEIGNADO
                                if(precioTotalReal < cartera1.valorDocumento)
                                {
                                    if (cartera1.valorDocumento < 0) {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                    } else {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorConsignado, 2))));
                                    }
                                }
                                else
                                {
                                    if (cartera1.valorDocumento < 0) {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                    } else {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento, 2))));
                                    }
                                }
                            } else {

                                if (cartera1.valorDocumento < 0) {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(0.0, 2))));
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.montoPendientes, 2))));
                                }

                            }
                        }

                        /**********************************
                         *SALDO                           *
                         **********************************/
                        if (cartera1.valorDocumento < 0) {
                            tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                        } else if (cartera1.valorDocumento > 0) {

                            if (cartera1.valorDocumento - cartera1.montoPendientes == 0) {

                                if (salfoAFA < 0) {
                                    if(i == 0)
                                    {
                                        tabla.addCell(String.valueOf(Utilidades.formatearDecimales(salfoAFA, 2)));
                                    }
                                    else
                                    {
                                        tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                    }
                                } else {
                                    tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));
                                }
                                //  tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));


                            } else if (cartera1.valorDocumento - cartera1.montoPendientes != 0) {

                                if (salfoAFA < 0) {
                                    if(i == 0)
                                    {
                                        tabla.addCell(String.valueOf(Utilidades.formatearDecimales(salfoAFA, 2)));
                                    }
                                    else
                                    {
                                        tabla.addCell(String.valueOf(Utilidades.formatearDecimales(0.0, 2)));
                                    }
                                } else {
                                    if (cartera1.montoPendientes != 0) {
                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes - (-valorNegativos), 2))));

                                    } else {

                                        tabla.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.valorDocumento - cartera1.montoPendientes, 2))));

                                    }

                                }


                            }

                        }

                    }
                    i += 1;
                }
            }


            documento.add(tabla);


            documento.add(new Paragraph(""));

            tabla = new PdfPTable(4);
            table.setWidthPercentage(100);
            tabla.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla.addCell(new Paragraph("", fontC));
            tabla.addCell(new Paragraph("TOTAL", fontC));

            if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                    || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                if (salfoAFA < 0) {
                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;


                    Paragraph parrafo2 = new Paragraph("                                                                                                                             Total cancelado :      " + formatoNumero.format(Utilidades.formatearDecimales(precioTotalFac, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                                 Total pagado :       " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    Paragraph parrafo223 = new Paragraph("                                                                                                                                 Saldo a favor :       " + formatoNumero.format(Utilidades.formatearDecimales(salfoAFA, 2)), font);
                    parrafo223.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo223));
                    documento.add(new Paragraph("\n"));


                } else {

                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;

                    Paragraph parrafo2 = new Paragraph("                                                                                                                           Total cancelado :      " + formatoNumero.format(Utilidades.formatearDecimales(precioTotalFac, 2)), font);
                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(parrafo2);


                    Paragraph parrafo22 = new Paragraph("                                                                                                                               Total pagado :       " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                    documento.add(new Paragraph(parrafo22));


                    documento.add(new Paragraph("\n"));


                }


            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));


                if (salfoAFA < 0) {
                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;


//                    Paragraph parrafo2 = new Paragraph("                                                                                                                              Total cancelado :      " + formatoNumero.format(Utilidades.formatearDecimales(precioTotalFac, 2)), font);
//                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(parrafo2);
//
//
//                    Paragraph parrafo22 = new Paragraph("                                                                                                                                  Total pagado :       " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
//                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(new Paragraph(parrafo22));
//
//
//                    Paragraph parrafo223 = new Paragraph("                                                                                                                                  Saldo a favor :       " + formatoNumero.format(Utilidades.formatearDecimales(salfoAFA, 2)), font);
//                    parrafo223.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(new Paragraph(parrafo223));
//                    documento.add(new Paragraph("\n"));

                    tabla.addCell(new Paragraph(formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), fontC));
                    tabla.addCell(new Paragraph(formatoNumero.format(Utilidades.formatearDecimales(salfoAFA * -1, 2)), fontC));


                } else {

                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    salfoAFA = salfoAFA * -1;

//                    Paragraph parrafo2 = new Paragraph("                                                                                                                                 Total cancelado :      " + formatoNumero.format(Utilidades.formatearDecimales(precioDocumentos, 2)), font);
//                    parrafo2.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(parrafo2);
//
//
//                    Paragraph parrafo22 = new Paragraph("                                                                                                                                     Total pagado :       " + formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), font);
//                    parrafo22.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                    documento.add(new Paragraph(parrafo22));
//
//
//                    documento.add(new Paragraph("\n"));

                    tabla.addCell(new Paragraph(formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)), fontC));
                    tabla.addCell(new Paragraph(formatoNumero.format(Utilidades.formatearDecimales(salfoAFA * -1, 2)), fontC));


                }


            }

            // Insertamos segunda  tabla.
            tabla.setWidths(new float[]{20, 10, 10, 10});
            documento.add(tabla);

            documento.add(new Paragraph("\n"));

            /***************************************
             * TABLA DETALLE DEL PAGO              *
             ***************************************/
            font44 = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
            textoEncabezado44 = new Paragraph("DETALLE DEL PAGO", font44);
            textoEncabezado44.setAlignment(Element.ALIGN_CENTER);
            documento.add(textoEncabezado44);

            documento.add(new Paragraph("\n"));

            listaFacturasRealizadas = new ArrayList<>();

            if(empresas.equals("AGUC"))
            {
                listaFacturasRealizadas = DataBaseBO.cargarFacturasViaPagoAGUC(numeroRecibo);
            }
            else
            {
                listaFacturasRealizadas = DataBaseBO.cargarFacturasViaPago(numeroRecibo);
            }

            PdfPTable tabla1 = new PdfPTable(4);
            table1.setWidthPercentage(100);
            tabla1.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla1.addCell(new Paragraph("Metodo de pago", fontC));
            tabla1.addCell(new Paragraph("CHEQUE #", fontC));
            tabla1.addCell(new Paragraph("BANCO", fontC));
            tabla1.addCell(new Paragraph("Valor a pagar", fontC));


            tabla1.setWidths(new float[]{25, 25,25, 15});


            if (listaFacturasRealizadas != null) {

                String viaPago = "";

                for (FacturasRealizadas cartera1 : listaFacturasRealizadas) {

                    if (salfoAFA == 0) {


                        salfoAFA = precioTotal- precioTotalReal  ;

                    }

                    if (cartera1.viaPago.equals("A")) {
                        viaPago = ("EFECTIVO");
                    }
                    if (cartera1.viaPago.equals("B")) {
                        viaPago = ("CHEQUE");
                    }
                    if (cartera1.viaPago.equals("6")) {
                        viaPago = ("TRANSFERENCIA");
                    }
                    if (cartera1.viaPago.equals("O")) {
                        viaPago = ("TARJETA");
                    }
                    if (cartera1.viaPago.equals("4")) {
                        viaPago = ("BITCOIN");
                    }

                    double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));

                    if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                            || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));

                        if (salfoAFA > 0) {

                            if (sumaXValorConsignado >= precioTotalFac) {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));
                            } else {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));
                            }

                        }else{


                            tabla1.addCell(viaPago);

                            tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));


                        }

                    } else {

                        NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));



                        if (salfoAFA > 0) {

                            if (sumaXValorConsignado >= precioTotalFac) {
                                tabla1.addCell(viaPago);
                                tabla1.addCell(cartera1.numeroCheqe);
                                tabla1.addCell(cartera1.banco);
                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));
                            } else {
                                tabla1.addCell(viaPago);

                                tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getMontoPendientes(), 2))));
                            }

                        }else{


                            tabla1.addCell(viaPago);
                            tabla1.addCell(cartera1.numeroCheqe);
                            tabla1.addCell(cartera1.banco);
                            tabla1.addCell(String.valueOf(formatoNumero.format(Utilidades.formatearDecimales(cartera1.getValorConsignado(), 2))));


                        }
                    }


                }
            }

            documento.add(tabla1);

            tabla1 = new PdfPTable(3);
            table1.setWidthPercentage(100);
            tabla1.setWidthPercentage(100);
            //el numero indica la cantidad de Columnas
            tabla1.addCell(new Paragraph("", fontC));
            tabla1.addCell(new Paragraph("TOTAL", fontC));


            if (empresas.equals("AGCO") || empresas.equals("AGSC") || empresas.equals("AGGC") || empresas.equals("AFPN")
                    || empresas.equals("AFPZ") || empresas.equals("AGAH") || empresas.equals("AGDP")) {


                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("es"));
                Paragraph parrafo = new Paragraph("TOTAL RECAUDO: " + formatoNumero.format(Utilidades.formatearDecimales(precioTotal, 2)), font);

                parrafo.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
                documento.add(parrafo);

                documento.add(new Paragraph("\n"));


            } else {

                NumberFormat formatoNumero = NumberFormat.getInstance(new Locale("en"));
//                Paragraph parrafo = new Paragraph("TOTAL RECAUDO: " + formatoNumero.format(Utilidades.formatearDecimales(precioTotal, 2)), font);
//
//                parrafo.setAlignment(Element.ALIGN_LEFT);//el 1 es para centrar
//                documento.add(parrafo);
//
//                documento.add(new Paragraph("\n"));

                if(salfoAFA > 0)
                {
                    Double sumaXValorConsignado = (Utilidades.formatearDecimales(Utilidades.sumaValorConsigRealizados(this, numeroRecibo), 2));
                    tabla1.addCell(formatoNumero.format(Utilidades.formatearDecimales(sumaXValorConsignado, 2)));
                }
                else
                {
                    tabla1.addCell(formatoNumero.format(Utilidades.formatearDecimales(precioTotal, 2)));
                }
            }

            tabla1.setWidths(new float[]{50, 25, 15});

            documento.add(tabla1);

            Paragraph cordialmente = new Paragraph("Observaciones: " + observaciones,font);
            cordialmente.setAlignment(Element.ALIGN_LEFT);
            documento.add(new Paragraph(cordialmente));
            documento.add(new Paragraph("\n\n"));

            //FIRMA Y NOMBRE DE VENTEDOR EN CASO DE QUE CORRESPONDA CON LA EMPRESA
            if(empresas.equals("AGUC"))
            {
                for (FacturasRealizadas factura : facCollection) {

                    //CARGA LA INFORMACION DE LA FIRMA SEGUNDO EL ID PAGO
                    FirmaNombre firmaNombre = DataBaseBO.cargarFirmaNombre(factura.numeroRecibo);

                    if(firmaNombre.firmaNombre != null)
                    {
                        //CARGAR LA IMAGEN DE LA FIRMA
                        Bitmap bitmap = BitmapFactory.decodeByteArray(firmaNombre.firma, 0, firmaNombre.firma.length);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        Image imagen = Image.getInstance(baos.toByteArray());
                        imagen.scaleToFit(90, 90);

                        PdfPTable tablaFirma = new PdfPTable(4);
                        tablaFirma.setWidthPercentage(100);
                        tablaFirma.setWidthPercentage(100);

                        PdfPCell cell;

                        //PRIMERA FILA
                        cell = new PdfPCell(new Paragraph(firmaNombre.firmaNombre,font));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        cell = new PdfPCell(new Paragraph("",font));
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        cell = new PdfPCell(imagen);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        cell = new PdfPCell(new Paragraph("",font));
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        //TERCERA FILA
                        cell = new PdfPCell(new Paragraph("------------------------------------------------",font));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        cell = new PdfPCell(new Paragraph("",font));
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        cell = new PdfPCell(new Paragraph("------------------------------------------------",font));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        cell = new PdfPCell(new Paragraph("",font));
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        //SEGUNDA FILA
                        cell = new PdfPCell(new Paragraph("Nombre",font));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        cell = new PdfPCell(new Paragraph("",font));
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        cell = new PdfPCell(new Paragraph("Firma",font));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        cell = new PdfPCell(new Paragraph("",font));
                        cell.setBorderColor(BaseColor.WHITE);
                        tablaFirma.addCell(cell);

                        tablaFirma.setWidths(new float[]{12,2, 12, 12});

                        documento.add(tablaFirma);

                        break;
                    }
                }
            }

            documento.add(new Paragraph("\n\n\n"));
            Paragraph texto = new Paragraph("ESTE RECIBO ES EL UNICO COMPROBANTE DE PAGO RECONOCIDO POR LA EMPRESA,\n" +
                    " NO DEBE CONTENER ALTERACIONES ",
                    font);
            texto.setAlignment(Element.ALIGN_CENTER);//el 1 es para centrar


            documento.add(new Paragraph(texto));


            estado = true;

        } catch (DocumentException e) {

            Log.e("Error", e.getMessage());

        } catch (IOException e) {

            Log.e("Error", e.getMessage());

        } finally {
            // Cerramos el documento.
            documento.close();
//            Toasty.warning(getApplicationContext(), "Se Descargo El PDF Correctamente, ya se puede visualizar").show();
            return estado;
        }
    }

    @Override
    public FacturasRealizadas facturaRealizados(double cartera) {
        return null;
    }

    private void guardarVista() {

        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        boolean estado = true;
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putBoolean("estado_FacturasSeleccRealizadas", true);
        editor1.commit();
    }

    public void cancelarFacRealizadas(View view) {


        view.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        SharedPreferences settings = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = settings.edit();
        editor1.putBoolean("estado_FacturasSeleccRealizadas", true);
        editor1.remove("estado_FacturasSeleccRealizadas");
        editor1.commit();
        finish();
        Intent vistaClienteActivity = new Intent(this, PrincipalActivity.class);
        startActivity(vistaClienteActivity);

    }

    public void visualizar() {
        Intent vistaClienteActivity = new Intent(this, PDFViewActivity.class);
        startActivity(vistaClienteActivity);

    }


    public void imprimir(View view)
    {

        view.setEnabled(false);
        progressDoalog = new ProgressDialog(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 600);


        SharedPreferences settings = getApplicationContext().getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
        String macImpresora = settings.getString(Constantes.MAC_IMPRESORA, "-");

        if (macImpresora.equals("-")) {

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    Alert.nutresaShow(FacturasRealizadasSeleccionadasActivity.this, "Information", "No Printer Set. Please first Configure the Printer!", "OK", null,

                            new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }

                            }, null);

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    Alert.nutresaShow(FacturasRealizadasSeleccionadasActivity.this, "Informacion", "No hay Impresora Establecida. Por Favor primero Configure la Impresora!", "Aceptar", null,

                            new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }

                            }, null);

                }
            }


        } else {

            if (lenguajeElegido == null) {

            } else if (lenguajeElegido != null) {
                if (lenguajeElegido.lenguaje.equals("USA")) {

                    progressDoalog = ProgressDialog.show(this, "",
                            "Please Wait...\n\nProcessing Information!", true);
                    progressDoalog.show();

                } else if (lenguajeElegido.lenguaje.equals("ESP")) {

                    progressDoalog = ProgressDialog.show(this, "",
                            "Por Favor Espere...\n\nProcesando Informacion!", true);
                    progressDoalog.show();

                }
            }


            SharedPreferences set = getApplicationContext().getSharedPreferences(Constantes.CONFIG_IMPRESORA, MODE_PRIVATE);
            String tipoImpresora = set.getString(Constantes.TIPO_IMPRESORA, "otro");

            if (!tipoImpresora.equals("Intermec")) {
                ImprimirTirilla(macImpresora);
                            /*sewooLKP20 = new SewooLKP20(FormConfigPrinterNuevo.this);
                            imprimirSewooLKP20(macImpresora);*/
            } else {
                ImprimirTirilla(macImpresora);
            }
        }

    }

    private void ImprimirTirilla(final String macAddress) {

        List<Facturas> listaFacturas2 = DataBaseBO.cargarIdPagoOGRecaudosRealizados(numeroRecibo);
        List<Facturas> listaFacturas4 = DataBaseBO.cargarIdPagoOGPendientesRecaudosRealizados(numeroRecibo);

        List<Facturas> listaFacturas3 = new ArrayList<>();

        listaFacturas3.addAll(listaFacturas2);

        listaFacturas3.addAll(listaFacturas4);

        new Thread(new Runnable() {
            public void run() {
                ZebraPrinterConnection printerConnection = null;
                try {
                    printerConnection = new BluetoothPrinterConnection(macAddress);
                    Looper.prepare();
                    //Abre la Conexion, la conexion Fisica es establecida aqui.
                    printerConnection.open();
                    ZebraPrinter zebraPrinter = ZebraPrinterFactory.getInstance(printerConnection);
                    PrinterLanguage printerLanguage = zebraPrinter.getPrinterControlLanguage();
                    Log.w("FormEstadisticaPedidos", "ImprimirTirilla -> Lenguaje de la Impresora " + printerLanguage);
                    // Envia los datos a la Impresora como un Arreglo de bytes.

                    //        String footer = PrinterBO.formatoTirillaEntrega1(clienteSel.codigo, listaFacturas3);
                    //
                    zebraPrinter.getPrinterControlLanguage();
                    String empresas = "";
                    empresas = DataBaseBO.cargarEmpresa();
                    String cpclData = "";

                    if (empresas.equals("ADHB")) {

                        cpclData = PrinterBO.formatoTirillaEntregaEmpresaEspaRecaudosRealizadosNEW(getApplicationContext(), codigoCliente, listaFacturas3,numeroRecibo,operacion);

                    } else if (empresas.equals("AGUC")) {

                        cpclData = PrinterBO.formatoTirillaEntregaEmpresaUSARecaudosRealizadosNEW(getApplicationContext(), codigoCliente, listaFacturas3,numeroRecibo,operacion);


                    } else {
                        cpclData = PrinterBO.formatoTirillaEntrega2(listaFacturasRealizadas.get(0).codigoCliente, listaFacturas3);

                    }


                    String cpc = cpclData + "\n";
                    printerConnection.write(cpc.getBytes());
                    // Se asegura que los datos son enviados a la Impresora antes de cerrar la conexion.
                    Thread.sleep(100);
                    handle.sendEmptyMessage(0);
                    Looper.myLooper().quit();
                    Toast.makeText(FacturasRealizadasSeleccionadasActivity.this,"No se pudo Imprimir",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("FormEstadisticaPedidos", "ImprimirTirilla -> " + e.getMessage(), e);
                    String mensaje = "No se pudo Imprimir.\n\n" + e.getMessage();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(FacturasRealizadasSeleccionadasActivity.this,"No se pudo Imprimir: " + e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
                    handlerFinish.sendEmptyMessage(0);
                    Looper.myLooper().quit();
                } finally {                        // Cierra la conexion para liberar Recursos
                    try {
                        if (printerConnection != null) printerConnection.close();
                    } catch (ZebraPrinterConnectionException e) {
                        Log.e("FormEstadisticaPedidos", "ImprimirTirilla", e);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(FacturasRealizadasSeleccionadasActivity.this,"No se pudo Imprimir",Toast.LENGTH_SHORT).show();
                            }
                        });
                        handlerFinish.sendEmptyMessage(0);
                        Looper.myLooper().quit();
                    }
                }
            }
        }).start();
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.dismiss();
        }
    };

    private Handler handlerFinish = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (progressDoalog != null)
                progressDoalog.cancel();

            DataBaseBO.borrarInfoTemp();

            guardarVista();
            Intent vistaInforme = new Intent(FacturasRealizadasSeleccionadasActivity.this, PrincipalActivity.class);
            startActivity(vistaInforme);

            estadoEnviadoRespuesta = true;
            if(Alert.dialogo != null)
                Alert.dialogo.cancel();
        }
    };

}
