package businessObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.qrcode.WriterException;

import co.com.celuweb.carterabaldomero.R;
import dataobject.Cartera;
import dataobject.Cliente;
import dataobject.Facturas;
import dataobject.ParseBitmap;
import utilidades.Utilidades;


/**
 * Clase encargada de contener los metodos los cuales generan las tirillas de
 * impresion
 *
 * @author Juan Carlos Hidalgo <Celuweb>
 */
public class PrinterBO extends AppCompatActivity {


    // atribuctos necesarios para la impresion
    ProgressDialog progressDialog;
    protected static int _splashTime = 1000;
    protected static int _splashTime2 = 4000;

    static Thread splashTread;

    public static float valorRecogido = 0;
    public static float valorNoRecogido = 0;
    public static float valorDevolucionParcial = 0;


    /**
     * Metodo encargado de generar la tirilla de prueba (Establecer Impresora)
     *
     * @return
     */
    public static String formatoPrueba() {
        String strPrint, dato;
        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "CENTER" + enter;
        dato = "Cartera baldomero.";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;
        dato = "NIT 890.807.529-8 ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "INVENTARIO";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "--------------------------------";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Vend:  " + "Vendedor de pruebas";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Fecha: " + Utilidades.fechaActual("yyyy-MM-dd");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;


    }

    /**
     * Metodo encargado de generar la tirilla de entrega(Establecer Impresora)
     */

    public static String formatoTirillaEntrega1(String param, List<Facturas> idpago, Context context) throws WriterException {

        String strPrint;

        char ret1 = 13;
        char ret2 = 10;

        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;

        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;

        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion

        String enter = String.valueOf(ret1) + ret2;

        strPrint = "";


        String formato = "";

        String[] arreglo = Utilidades.split("", "38");
        for (int i = 0; i < arreglo.length; i++) {
            formato = Utilidades.CentrarLinea(arreglo[i], 38);
            strPrint += formato + enter;
            if (i + 1 == arreglo.length) {
                posY += spaceLine2;
            } else {
                posY += spaceLine1;
            }
        }

        String c1 = "";
        String c2 = "";


        ArrayList<Facturas> facturas = new ArrayList<Facturas>();

        facturas = DataBaseBO.getImpresionFactura(param, idpago, context);
        Facturas encabezadoFactura = DataBaseBO.getImpresionCliente(param, idpago, context);

        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);


        formato = Utilidades.CentrarLinea("  Recibo de Ingreso", 30);
        strPrint += formato + enter;
        posY += spaceLine2;

        formato = Utilidades.rpad("", 30, "");
        strPrint += formato + enter;
        posY += spaceLine3;

        formato = "  Recibo de ingreso No: " + encabezadoFactura.nroRecibo;
        strPrint += formato + enter;
        posY += spaceLine2;

        formato = Utilidades.rpad("", 30, "-");
        strPrint += formato + enter;
        posY += spaceLine1;

        formato = "  Nit :" + cliente.nit;
        strPrint += formato + enter;
        posY += spaceLine2;

        formato = "  Empresa: " + empresa;
        strPrint += formato + enter;
        posY += spaceLine2;


        formato = "  Nombre :" + cliente.razonSocial;
        strPrint += formato + enter;
        posY += spaceLine2;


        formato = "  Telefono : " + cliente.telefono;
        strPrint += formato + enter;
        posY += spaceLine2;

        formato = "  Vendedor: " + vendedor;
        strPrint += formato + enter;
        posY += spaceLine3;


        formato = Utilidades.rpad("", 30, "-");
        strPrint += formato + enter;
        posY += spaceLine1;

        /*********************************************INFORMACION PAGO************************************************************/


        formato = Utilidades.CentrarLinea("  Detalles formas pago", 30);
        strPrint += formato + enter;
        posY += spaceLine1;

        formato = Utilidades.rpad("", 30, "");
        strPrint += formato + enter;
        posY += spaceLine1;

        for (Facturas facturas1 : facturas) {

            formato = "CK No/ Aprob.No: " + facturas1.nroRecibo;
            strPrint += formato + enter;
            posY += spaceLine2;

            formato = "Banco/Tarjeta: " + facturas1.banco;
            strPrint += formato + enter;
            posY += spaceLine2;
            String tipoPago = "N/A";

            if (facturas1.viaPago.equals("A")) {
                tipoPago = "EFECTIVO";
            }

            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHEQUE";

            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFERENCIA";


            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN";

            }

            formato = "Forma Pago: " + tipoPago;
            strPrint += formato + enter;
            posY += spaceLine2;


            formato = Utilidades.lpad("", 30, "-");
            strPrint += formato + enter;
            posY += spaceLine2;
        }


        /*********************************************INFORMACION************************************************************/


        formato = "  Distinguidos Sres";
        strPrint += formato + enter;
        posY += spaceLine1;


        formato = "  Hemos recibido con fecha " + //Utilidades.fechaActual("yyyy-MM-dd") +
                "   la suma de RD$ " + encabezadoFactura.valorDocumento + " aplicando a los siguientes documentos: ";
        strPrint += formato + enter;
        posY += spaceLine1;

        formato = Utilidades.lpad("", 30, "-");
        strPrint += formato + enter;
        posY += spaceLine2;

        for (Facturas facturas1 : facturas) {


            formato = "No Factura: " + facturas1.nroRecibo;
            strPrint += formato + enter;
            posY += spaceLine1;

            formato = "Tipo recaudo : Factura";
            strPrint += formato + enter;
            posY += spaceLine1;

            formato = "Fecha: " + facturas1.fecha;
            strPrint += formato + enter;
            posY += spaceLine1;

            formato = "Importe : ";
            strPrint += formato + enter;
            posY += spaceLine1;

            formato = "Valor : " + facturas1.valor;
            strPrint += formato + enter;
            posY += spaceLine1;

            formato = Utilidades.lpad("", 30, "-");
            strPrint += formato + enter;
            posY += spaceLine2;
        }

        formato = "  Sub-total : " + encabezadoFactura.valorDocumento;
        strPrint += formato + enter;
        posY += spaceLine1;

        formato = "  Total : " + encabezadoFactura.valorDocumento;
        strPrint += formato + enter;
        posY += spaceLine1;


        formato = Utilidades.lpad("", 30, "-");
        strPrint += formato + enter;
        posY += spaceLine2;


        formato = "  Recibo conforme ";
        strPrint += formato + enter;
        posY += spaceLine1;

        formato = Utilidades.lpad("", 30, "");
        strPrint += formato + enter;
        posY += spaceLine2;


        formato = Utilidades.CentrarLinea("  ORIGINAL", 30);
        strPrint += formato + enter;
        posY += spaceLine1;


        formato = Utilidades.lpad("", 30, "");
        strPrint += formato + enter + enter + enter + enter;
        posY += spaceLine2;

        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaUSARecaudosRealizados(Context context, String param, List<Facturas> idpago, String numeroRecibo) throws WriterException, DocumentException, IOException, ParseException {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaFacturasSubTotal = 0;


        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();

        final List<String> Documentos = new ArrayList<>();

        facturas = DataBaseBO.getImpresionFacturaRealizados(param, idpago, numeroRecibo, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionClienteRecaudosRealizados(param, idpago, numeroRecibo, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechasRecaudosRealizados(param, idpago, numeroRecibo, context);


        for (Facturas facturas12 : facturasHechas) {

            monto.add(Double.valueOf(facturas12.valorDocumento));
            documentoFin.add(facturas12.documentoFinanciero);
            tipo.add(facturas12.documento);
            fechas.add(facturas12.fechaConsignacion);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);


        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "CASH     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHECK       ";
            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFER";
            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }


            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;

        }

        listCarteraDocu = DataBaseBO.getImpresionCarteraRecaudosRealizados(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

            String docu = "";
            if (cartera.documento.length() == 1) {
                docu = cartera.documento + "          ";
            }
            if (cartera.documento.length() == 2) {
                docu = cartera.documento + "         ";
            }
            if (cartera.documento.length() == 3) {
                docu = cartera.documento + "        ";
            }
            if (cartera.documento.length() == 4) {
                docu = cartera.documento + "       ";
            }
            if (cartera.documento.length() == 5) {
                docu = cartera.documento + "      ";
            }
            if (cartera.documento.length() == 6) {
                docu = cartera.documento + "     ";
            }
            if (cartera.documento.length() == 7) {
                docu = cartera.documento + "    ";
            }
            if (cartera.documento.length() == 8) {
                docu = cartera.documento + "   ";
            }
            if (cartera.documento.length() == 9) {
                docu = cartera.documento + "  ";
            }
            if (cartera.documento.length() == 10) {
                docu = cartera.documento + " ";
            }
            Documentos.add(docu);


        }

        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraClienteRecaudosRealizados(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.image1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 50, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(100, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine3;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                           <--ORIGINAL RECEIPT-->";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                        Date: " + Utilidades.voltearFecha(Utilidades.fechaActual("yyyy-MM-dd"));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                        Time: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                               Regular Payment";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Receipt #: " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Customer #: " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Customer Name: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Address :" + cliente.direccion;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    City: " + cliente.ciudad;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Zip Code :" + cliente.codigoZip;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Phone : " + cliente.telefono;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Sales Rep: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "                           PAYMENT METHOD";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "     Check #      " + "  Payment Method  " + "    Bank  " + "            Amount   ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 130 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  130 " + (posY-spaceLine2) + " 300 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  300 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {
                String arrayObjetos[] = new String[1];
                String arrayObjetosDatos[] = new String[1];

                datos = "     " + cheke.get(i) + "     " + metodo.get(i) + "        " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));

                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);

                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 130 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;


                dato = "";
                strPrint += "BOX  130 " + (posY-spaceLine1) + " 300 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  300 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                      " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                       " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Thank you for your business!";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Your payment will be apply to the following invoices. ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    Document Number " + "  Collection type " + "    Date    " + "        Amount ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 156 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  156 " + (posY-spaceLine2) + " 315 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine2) + " 455 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (listCarteraDocu.size() > 0) {
            if (cartera1 != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < listCarteraDocu.size(); i++) {

                    datos2 = "     " + Documentos.get(i) + "      " + tipo.get(i) + "                   " + Utilidades.voltearFecha(fechas.get(i)) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));

                    arrayObjetosDatos[0] = new String(datos2);

                    String num1="20 ";
                    String num2="990 ";
                    String num3="156 ";
                    String num4="1120 ";
                    String num5="1 ";
                    String num6="'dato'";

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }
                }


            }
        }

        dato = "Sub-total" + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacturasSubTotal,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "Total    " + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Received by:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaUSARecaudosRealizadosNEW(Context context, String param, List<Facturas> idpago, String numeroRecibo,String operacion) throws WriterException, DocumentException, IOException, ParseException {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaValorPAgado = 0;
        double sumaBalance = 0;
        double sumaFacturasSubTotal = 0;


        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<Double> pagado = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();
        final List<Double> balance = new ArrayList<>();
        double salfoAFA = DataBaseBO.SaldoAfavor(numeroRecibo, context);
        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo, context);
        Boolean isPrimerRegistro = true;

        final List<String> Documentos = new ArrayList<>();

        facturas = DataBaseBO.getImpresionFacturaRealizados(param, idpago, numeroRecibo, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionClienteRecaudosRealizados(param, idpago, numeroRecibo, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechasRecaudosRealizados(param, idpago, numeroRecibo, context);

        for (Facturas facturas12 : facturasHechas) {
            //SUMAR VALOR PAGADO
            sumaValorPAgado += Double.valueOf(facturas12.valorPagado);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);
        }

        if (salfoAFA == 0) {


            salfoAFA = ((sumaValorPAgado - sumaFacturasSubTotal) * -1);

        }

        if(Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaFacturasSubTotal), 2)) <= Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaValorPAgado),2)))
        {

            isPrimerRegistro = true;
            Double pagadoInicial = 0.0;

            for (Facturas facturas12 : facturasHechas) {

                pagadoInicial = (double) (facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento);

                if(isPrimerRegistro)
                {
                    if(salfoAFA < 0)
                    {
                        pagadoInicial += (salfoAFA * -1);
                        balance.add(salfoAFA);
                        sumaBalance += salfoAFA;

                    }
                    else
                    {
                        balance.add(0.0);
                    }
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(pagadoInicial);
                balance.add(0.0);
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);

                //SUMAR BALANCES
                sumaBalance += 0;

                isPrimerRegistro = false;

            }
        }
        else if(Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaFacturasSubTotal), 2)) > Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaValorPAgado),2)))
        {
            Double pagadoInicial = 0.0;

            for (Facturas facturas12 : facturasHechas) {

                pagadoInicial = (double) (facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento);

                if(pagadoInicial - facturas12.valorPagado != 0)
                {
                    balance.add(Double.valueOf((pagadoInicial - facturas12.valorPagado) -(-valorNegativos)));
                    //SUMAR BALANCES
                    sumaBalance += Double.valueOf((pagadoInicial - facturas12.valorPagado) -(-valorNegativos));
                }
                else
                {
                    balance.add(Double.valueOf(pagadoInicial - facturas12.valorPagado));
                    //SUMAR BALANCES
                    sumaBalance += Double.valueOf(pagadoInicial - facturas12.valorPagado);
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(Double.valueOf(facturas12.valorPagado));
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);

                isPrimerRegistro = false;

            }
        }
        else
        {
            for (Facturas facturas12 : facturasHechas) {

                if(isPrimerRegistro)
                {
                    if(salfoAFA < 0)
                    {
                        facturas12.valorPagado = Float.parseFloat(String.valueOf(facturas12.valorPagado + (salfoAFA*-1)));
                    } else
                    {
                        balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado - (-valorNegativos)));
                    }
                }
                else
                {
                    balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado));
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(Double.valueOf(facturas12.valorPagado));
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);
                balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado));

                //SUMAR BALANCES
                sumaBalance += Double.valueOf(facturas12.valorDocumento - facturas12.valorPagado);

                isPrimerRegistro = false;

            }
        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "CASH     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHECK       ";
            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFER";
            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }


            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;


        }

        listCarteraDocu = DataBaseBO.getImpresionCarteraRecaudosRealizados(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

            String docu = "";
            if (cartera.documento.length() == 1) {
                docu = cartera.documento + "          ";
            }
            if (cartera.documento.length() == 2) {
                docu = cartera.documento + "         ";
            }
            if (cartera.documento.length() == 3) {
                docu = cartera.documento + "        ";
            }
            if (cartera.documento.length() == 4) {
                docu = cartera.documento + "       ";
            }
            if (cartera.documento.length() == 5) {
                docu = cartera.documento + "      ";
            }
            if (cartera.documento.length() == 6) {
                docu = cartera.documento + "     ";
            }
            if (cartera.documento.length() == 7) {
                docu = cartera.documento + "    ";
            }
            if (cartera.documento.length() == 8) {
                docu = cartera.documento + "   ";
            }
            if (cartera.documento.length() == 9) {
                docu = cartera.documento + "  ";
            }
            if (cartera.documento.length() == 10) {
                docu = cartera.documento + " ";
            }
            Documentos.add(docu);


        }

        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraClienteRecaudosRealizados(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.image1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 50, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(100, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine3;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/

        /*************************************
         * ENCABEZADO DE LA FACTURA          *
         *************************************/
        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        if (operacion == null) {
            operacion = "RECEIPT";
        } else if (operacion != null) {
            if (operacion.equals("X")) {
                operacion = "RECEIPT LEGALIZED";
            } else if (operacion.equals("A")) {
                operacion = "RECEIPT ADVANCE";
            }
        }

        dato = "                             " + operacion;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Consecutive : " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Customer Name: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Customer code : " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Created by: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Date: " + encabezadoFactura.fechaRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Time: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Your payment will be applied to the following invoices.";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        /****************************************
         * TABLA DETALLES DEL DOCUMENTO         *
         ****************************************/
        dato = "                           DOCUMENT DETAIL";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Collection  " + "Document Number " + " Document       " + "Paid         " + "Balance";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + (posY+5) + " " + dato + enter;
        posY += spaceLine1;

        dato = "    type        " + "                " + " Amount         " + "             " + "       ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 120 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  120 " + (posY-spaceLine2) + " 260 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  260 " + (posY-spaceLine2) + " 380 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  380 " + (posY-spaceLine2) + " 480 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  480 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (tipo.size() > 0) {
            if (tipo != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < tipo.size(); i++) {

//                    datos2 = "     " + tipo.get(i) + "          " + Documentos.get(i) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2))) + "         " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(pagado.get(i),2))) + "         " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i) - pagado.get(i),2)));

                    datos = "        " + tipo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                " + Documentos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                    " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                   " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(pagado.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                               " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(balance.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

//                    arrayObjetosDatos[0] = new String(datos2);

                    String num1="20 ";
                    String num2="990 ";
                    String num3="156 ";
                    String num4="1120 ";
                    String num5="1 ";
                    String num6="'dato'";

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 120 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  120 " + (posY-spaceLine1) + " 260 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  260 " + (posY-spaceLine1) + " 380 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  380 " + (posY-spaceLine1) + " 480 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  480 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + "" + enter;
                        posY += spaceLine2;


                    }
                }


            }
        }

        dato = "                " + "                  " + " TOTAL";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "                                                   " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "                                                               " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaBalance,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "";
        strPrint += "BOX  260 " + (posY-spaceLine2) + " 380 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  380 " + (posY-spaceLine2) + " 480 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  480 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                           PAYMENT DETAIL";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Payment Method  " + "  Check #      " + "    Bank  " + "            Amount   ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 150 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  150 " + (posY-spaceLine2) + " 280 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  280 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {

                datos = "     " + metodo.get(i) + "     " + cheke.get(i) + "        " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));

                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);

                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 150 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  150 " + (posY-spaceLine1) + " 280 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  280 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "        " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                       " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                        " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = "                  " + "                  " + "   TOTAL  " + "          " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "";
        strPrint += "BOX  280 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "OBSERVATIONS: " + facturasHechas.get(0).observaciones;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "___________________________________________________________________________________";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "THIS RECEIPT IS THE ONLY PROOF OF PAYMENT RECOGNIZED BY THE COMPANY, ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " MUST NOT CONTAIN ALTERATIONS";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "___________________________________________________________________________________";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Received by:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaUSAPendientes(Context context, String param, List<Facturas> idpago, String numeroRecibo) throws WriterException, DocumentException, IOException, ParseException {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaFacturasSubTotal = 0;


        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();

        final List<String> Documentos = new ArrayList<>();

        facturas = DataBaseBO.getImpresionFacturaPendientes(param, idpago, numeroRecibo, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionClienteRecaudosPendientes(param, idpago, numeroRecibo, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechasRecaudosPendientes(param, idpago, numeroRecibo, context);


        for (Facturas facturas12 : facturasHechas) {

            monto.add(Double.valueOf(facturas12.valorDocumento));
            documentoFin.add(facturas12.documentoFinanciero);
            tipo.add(facturas12.documento);
            fechas.add(facturas12.fechaConsignacion);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);


        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "CASH     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHECK       ";
            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFER";
            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }


            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;

        }

        listCarteraDocu = DataBaseBO.getImpresionCarteraRecaudosPendientes(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

            String docu = "";
            if (cartera.documento.length() == 1) {
                docu = cartera.documento + "          ";
            }
            if (cartera.documento.length() == 2) {
                docu = cartera.documento + "         ";
            }
            if (cartera.documento.length() == 3) {
                docu = cartera.documento + "        ";
            }
            if (cartera.documento.length() == 4) {
                docu = cartera.documento + "       ";
            }
            if (cartera.documento.length() == 5) {
                docu = cartera.documento + "      ";
            }
            if (cartera.documento.length() == 6) {
                docu = cartera.documento + "     ";
            }
            if (cartera.documento.length() == 7) {
                docu = cartera.documento + "    ";
            }
            if (cartera.documento.length() == 8) {
                docu = cartera.documento + "   ";
            }
            if (cartera.documento.length() == 9) {
                docu = cartera.documento + "  ";
            }
            if (cartera.documento.length() == 10) {
                docu = cartera.documento + " ";
            }
            Documentos.add(docu);


        }

        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraClienteRecaudosPendientes(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.image1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 50, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(100, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine3;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                           <--ORIGINAL RECEIPT-->";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                        Date: " + Utilidades.voltearFecha(Utilidades.fechaActual("yyyy-MM-dd"));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                        Time: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                               Regular Payment";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Receipt #: " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Customer #: " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Customer Name: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Address :" + cliente.direccion;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    City: " + cliente.ciudad;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Zip Code :" + cliente.codigoZip;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Phone : " + cliente.telefono;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Sales Rep: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "                           PAYMENT METHOD";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "     Check #      " + "  Payment Method  " + "    Bank  " + "            Amount   ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 130 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  130 " + (posY-spaceLine2) + " 300 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  300 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {
                String arrayObjetos[] = new String[1];
                String arrayObjetosDatos[] = new String[1];

                datos = "     " + cheke.get(i) + "     " + metodo.get(i) + "        " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));

                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);

                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 130 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;


                dato = "";
                strPrint += "BOX  130 " + (posY-spaceLine1) + " 300 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  300 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                      " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                       " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Thank you for your business!";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Your payment will be apply to the following invoices. ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    Document Number " + "  Collection type " + "    Date    " + "        Amount ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 156 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  156 " + (posY-spaceLine2) + " 315 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine2) + " 455 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (listCarteraDocu.size() > 0) {
            if (cartera1 != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < listCarteraDocu.size(); i++) {

                    datos2 = "     " + Documentos.get(i) + "      " + tipo.get(i) + "                   " + Utilidades.voltearFecha(fechas.get(i)) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));

                    arrayObjetosDatos[0] = new String(datos2);

                    String num1="20 ";
                    String num2="990 ";
                    String num3="156 ";
                    String num4="1120 ";
                    String num5="1 ";
                    String num6="'dato'";

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }
                }


            }
        }

        dato = "Sub-total" + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacturasSubTotal,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "Total    " + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Received by:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaUSAPendientesNEW(Context context, String param, List<Facturas> idpago, String numeroRecibo) throws WriterException, DocumentException, IOException, ParseException
    {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaFacturasSubTotal = 0;
        double sumaBalance = 0;
        double sumaValorPAgado = 0;
        double salfoAFA = DataBaseBO.SaldoAfavorRecaudosPendientes(numeroRecibo, context);
        double valorNegativos = DataBaseBO.TotalValoresNegativosRecaudosPendientes(numeroRecibo, context);
        Boolean isPrimerRegistro = true;

        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();
        final List<Double> pagado = new ArrayList<>();
        final List<Double> balance = new ArrayList<>();

        final List<String> Documentos = new ArrayList<>();

        facturas = DataBaseBO.getImpresionFacturaPendientes(param, idpago, numeroRecibo, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionClienteRecaudosPendientes(param, idpago, numeroRecibo, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechasRecaudosPendientes(param, idpago, numeroRecibo, context);

        for (Facturas facturas12 : facturasHechas) {
            //SUMAR VALOR PAGADO
            sumaValorPAgado += Double.valueOf(facturas12.valorPagado);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);
        }

        if (salfoAFA == 0) {


            salfoAFA = ((sumaValorPAgado - sumaFacturasSubTotal) * -1);

        }

        if(Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaFacturasSubTotal), 2)) <= Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaValorPAgado),2)))
        {

            isPrimerRegistro = true;
            Double pagadoInicial = 0.0;

            for (Facturas facturas12 : facturasHechas) {

                pagadoInicial = (double) (facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento);

                if(isPrimerRegistro)
                {
                    if(salfoAFA < 0)
                    {
                        pagadoInicial += (salfoAFA * -1);
                        balance.add(salfoAFA);
                        sumaBalance += salfoAFA;

                    }
                    else
                    {
                        balance.add(0.0);
                    }
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(pagadoInicial);
                balance.add(0.0);
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);

                //SUMAR BALANCES
                sumaBalance += 0;

                isPrimerRegistro = false;

            }
        }
        else if(Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaFacturasSubTotal), 2)) > Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaValorPAgado),2)))
        {
            Double pagadoInicial = 0.0;

            for (Facturas facturas12 : facturasHechas) {

                pagadoInicial = (double) (facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento);

                if(pagadoInicial - facturas12.valorPagado != 0)
                {
                    balance.add(Double.valueOf((pagadoInicial - facturas12.valorPagado) -(-valorNegativos)));
                    //SUMAR BALANCES
                    sumaBalance += Double.valueOf((pagadoInicial - facturas12.valorPagado) -(-valorNegativos));
                }
                else
                {
                    balance.add(Double.valueOf(pagadoInicial - facturas12.valorPagado));
                    //SUMAR BALANCES
                    sumaBalance += Double.valueOf(pagadoInicial - facturas12.valorPagado);
                }

                monto.add(pagadoInicial);
                pagado.add(Double.valueOf(facturas12.valorPagado));
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);

                isPrimerRegistro = false;

            }
        }
        else
        {
            for (Facturas facturas12 : facturasHechas) {

                if(isPrimerRegistro)
                {
                    if(salfoAFA < 0)
                    {
                        facturas12.valorPagado = Float.parseFloat(String.valueOf(facturas12.valorPagado + (salfoAFA*-1)));
                    } else
                    {
                        balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado - (-valorNegativos)));
                    }
                }
                else
                {
                    balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado));
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(Double.valueOf(facturas12.valorPagado));
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);
                balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado));

                //SUMAR BALANCES
                sumaBalance += Double.valueOf(facturas12.valorDocumento - facturas12.valorPagado);

                isPrimerRegistro = false;

            }
        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "CASH     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHECK       ";
            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFER";
            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }


            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;

        }

        listCarteraDocu = DataBaseBO.getImpresionCarteraRecaudosPendientes(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

            String docu = "";
            if (cartera.documento.length() == 1) {
                docu = cartera.documento + "          ";
            }
            if (cartera.documento.length() == 2) {
                docu = cartera.documento + "         ";
            }
            if (cartera.documento.length() == 3) {
                docu = cartera.documento + "        ";
            }
            if (cartera.documento.length() == 4) {
                docu = cartera.documento + "       ";
            }
            if (cartera.documento.length() == 5) {
                docu = cartera.documento + "      ";
            }
            if (cartera.documento.length() == 6) {
                docu = cartera.documento + "     ";
            }
            if (cartera.documento.length() == 7) {
                docu = cartera.documento + "    ";
            }
            if (cartera.documento.length() == 8) {
                docu = cartera.documento + "   ";
            }
            if (cartera.documento.length() == 9) {
                docu = cartera.documento + "  ";
            }
            if (cartera.documento.length() == 10) {
                docu = cartera.documento + " ";
            }
            Documentos.add(docu);


        }

        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraClienteRecaudosPendientes(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.image1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 50, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(100, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine3;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/


        /*************************************
         * ENCABEZADO DE LA FACTURA          *
         *************************************/
        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                             RECEIPT";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Consecutive : " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Customer Name: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Customer code : " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Created by: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Date: " + encabezadoFactura.fechaRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Time: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Your payment will be applied to the following invoices.";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        /****************************************
         * TABLA DETALLES DEL DOCUMENTO         *
         ****************************************/
        dato = "                           DOCUMENT DETAIL";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Collection  " + "Document Number " + " Document       " + "Paid         " + "Balance";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "     type       " + "                " + " Amount         " + "             " + "       ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 120 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  120 " + (posY-spaceLine2) + " 260 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  260 " + (posY-spaceLine2) + " 380 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  380 " + (posY-spaceLine2) + " 480 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  480 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (listCarteraDocu.size() > 0) {
            if (cartera1 != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < listCarteraDocu.size(); i++) {

//                    datos2 = "     " + tipo.get(i) + "          " + Documentos.get(i) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2))) + "         " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(pagado.get(i),2))) + "         " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i) - pagado.get(i),2)));

                    datos = "        " + tipo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                " + Documentos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                  " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(pagado.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                             " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(balance.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

//                    arrayObjetosDatos[0] = new String(datos2);

                    String num1="20 ";
                    String num2="990 ";
                    String num3="156 ";
                    String num4="1120 ";
                    String num5="1 ";
                    String num6="'dato'";

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 120 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  120 " + (posY-spaceLine1) + " 260 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  260 " + (posY-spaceLine1) + " 380 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  380 " + (posY-spaceLine1) + " 480 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  480 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + "" + enter;
                        posY += spaceLine2;


                    }
                }


            }
        }

        dato = "                                  TOTAL";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "                                                " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "                                                             " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaBalance,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "";
        strPrint += "BOX  260 " + (posY-spaceLine2) + " 380 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  380 " + (posY-spaceLine2) + " 480 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  480 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                           PAYMENT DETAIL";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Payment Method  " + "  Check #      " + "    Bank  " + "            Amount   ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 150 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  150 " + (posY-spaceLine2) + " 280 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  280 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {

                datos = "     " + metodo.get(i) + "     " + cheke.get(i) + "        " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));

                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);

                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 150 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  150 " + (posY-spaceLine1) + " 280 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  280 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "        " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                       " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = "                  " + "                  " + "   TOTAL  " + "       " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "";
        strPrint += "BOX  280 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "OBSERVATIONS: " + facturasHechas.get(0).observaciones;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "___________________________________________________________________________________";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "THIS RECEIPT IS THE ONLY PROOF OF PAYMENT RECOGNIZED BY THE COMPANY, ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " MUST NOT CONTAIN ALTERATIONS";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "___________________________________________________________________________________";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Received by:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaUSA(Context context, String param, List<Facturas> idpago) throws WriterException, DocumentException, IOException, ParseException {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaFacturasSubTotal = 0;


        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();

        final List<String> Documentos = new ArrayList<>();

        facturas = DataBaseBO.getImpresionFactura(param, idpago, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionCliente(param, idpago, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechas(param, idpago, context);


        for (Facturas facturas12 : facturasHechas) {

            monto.add(Double.valueOf(facturas12.valorDocumento));
            documentoFin.add(facturas12.documentoFinanciero);
            tipo.add(facturas12.documento);
            fechas.add(facturas12.fechaConsignacion);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);


        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "CASH     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHECK       ";
            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFER";
            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }


            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;

        }

        listCarteraDocu = DataBaseBO.getImpresionCartera(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

            String docu = "";
            if (cartera.documento.length() == 1) {
                docu = cartera.documento + "          ";
            }
            if (cartera.documento.length() == 2) {
                docu = cartera.documento + "         ";
            }
            if (cartera.documento.length() == 3) {
                docu = cartera.documento + "        ";
            }
            if (cartera.documento.length() == 4) {
                docu = cartera.documento + "       ";
            }
            if (cartera.documento.length() == 5) {
                docu = cartera.documento + "      ";
            }
            if (cartera.documento.length() == 6) {
                docu = cartera.documento + "     ";
            }
            if (cartera.documento.length() == 7) {
                docu = cartera.documento + "    ";
            }
            if (cartera.documento.length() == 8) {
                docu = cartera.documento + "   ";
            }
            if (cartera.documento.length() == 9) {
                docu = cartera.documento + "  ";
            }
            if (cartera.documento.length() == 10) {
                docu = cartera.documento + " ";
            }
            Documentos.add(docu);


        }

        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraCliente(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.image1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 50, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(100, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine3;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                           <--ORIGINAL RECEIPT-->";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                        Date: " + Utilidades.voltearFecha(Utilidades.fechaActual("yyyy-MM-dd"));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                        Time: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                               Regular Payment";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Receipt #: " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Customer #: " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Customer Name: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Address :" + cliente.direccion;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    City: " + cliente.ciudad;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Zip Code :" + cliente.codigoZip;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Phone : " + cliente.telefono;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Sales Rep: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "                           PAYMENT METHOD";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "     Check #      " + "  Payment Method  " + "    Bank  " + "            Amount   ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 130 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  130 " + (posY-spaceLine2) + " 300 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  300 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {
                String arrayObjetos[] = new String[1];
                String arrayObjetosDatos[] = new String[1];

                datos = "     " + cheke.get(i) + "     " + metodo.get(i) + "        " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));

                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);

                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 130 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;


                dato = "";
                strPrint += "BOX  130 " + (posY-spaceLine1) + " 300 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  300 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                      " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                       " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Thank you for your business!";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Your payment will be apply to the following invoices. ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    Document Number " + "  Collection type " + "    Date    " + "        Amount ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 156 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  156 " + (posY-spaceLine2) + " 315 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine2) + " 455 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (listCarteraDocu.size() > 0) {
            if (cartera1 != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < listCarteraDocu.size(); i++) {

                    datos2 = "     " + Documentos.get(i) + "      " + tipo.get(i) + "                   " + Utilidades.voltearFecha(fechas.get(i)) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));

                    arrayObjetosDatos[0] = new String(datos2);

                    String num1="20 ";
                    String num2="990 ";
                    String num3="156 ";
                    String num4="1120 ";
                    String num5="1 ";
                    String num6="'dato'";

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }
                }


            }
        }

        dato = "Sub-total" + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacturasSubTotal,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "Total    " + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Received by:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaEspaRecaudosRealizados(Context context, String param, List<Facturas> idpago, String numeroRecibo) throws WriterException, DocumentException, IOException, ParseException {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaFacturasSubTotal = 0;


        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();

        final List<String> Documentos = new ArrayList<>();


        facturas = DataBaseBO.getImpresionFacturaRealizados(param, idpago, numeroRecibo, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionClienteRecaudosRealizados(param, idpago,numeroRecibo, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechasRecaudosRealizados(param, idpago, numeroRecibo, context);


        for (Facturas facturas12 : facturasHechas) {

            monto.add(Double.valueOf(facturas12.valorDocumento));
            documentoFin.add(facturas12.documentoFinanciero);
            tipo.add(facturas12.documento);
            fechas.add(facturas12.fechaConsignacion);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);

        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "EFECTIVO     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHEQUE       ";

            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFERENCIA";


            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }

            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;

        }

        listCarteraDocu = DataBaseBO.getImpresionCarteraRecaudosRealizados(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

            String docu = "";
            if (cartera.documento.length() == 1) {
                docu = cartera.documento + "          ";
            }
            if (cartera.documento.length() == 2) {
                docu = cartera.documento + "         ";
            }
            if (cartera.documento.length() == 3) {
                docu = cartera.documento + "        ";
            }
            if (cartera.documento.length() == 4) {
                docu = cartera.documento + "       ";
            }
            if (cartera.documento.length() == 5) {
                docu = cartera.documento + "      ";
            }
            if (cartera.documento.length() == 6) {
                docu = cartera.documento + "     ";
            }
            if (cartera.documento.length() == 7) {
                docu = cartera.documento + "    ";
            }
            if (cartera.documento.length() == 8) {
                docu = cartera.documento + "   ";
            }
            if (cartera.documento.length() == 9) {
                docu = cartera.documento + "  ";
            }
            if (cartera.documento.length() == 10) {
                docu = cartera.documento + " ";
            }
            Documentos.add(docu);


        }


        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraClienteRecaudosRealizados(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.espbon1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.espbon);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 100, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(180, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine3;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                           <--RECIBO ORIGINAL-->";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                      Fecha: " + Utilidades.fechaActual("yyyy-MM-dd");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                      Hora: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                               Recibo de ingreso";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Recibo #: " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Cliente #: " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Nombre cliente: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Direccion :" + cliente.direccion;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Ciudad: " + cliente.ciudad;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Codigo Zip :" + cliente.codigoZip;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Telefono : " + cliente.telefono;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Vendedor: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "                           Metodo de Pago";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "     Cheque #      " + "  Metodo Pago  " + "    Banco  " + "            Monto   ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 130 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  130 " + (posY-spaceLine2) + " 300 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  300 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {
                String arrayObjetos[] = new String[1];
                String arrayObjetosDatos[] = new String[1];

                datos = "     " + cheke.get(i) + "     " + metodo.get(i) + "        " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));


                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);


                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 130 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;


                dato = "";
                strPrint += "BOX  130 " + (posY-spaceLine1) + " 300 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  300 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                      " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                       " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Gracias por su negocio!";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Su pago se aplicara a las siguientes facturas. ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "   Numero documento   " + "  Tipo recaudo " + "    Fecha    " + "          Monto ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 156 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  156 " + (posY-spaceLine2) + " 315 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine2) + " 455 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (listCarteraDocu.size() > 0) {
            if (cartera1 != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < listCarteraDocu.size(); i++) {

                    datos2 = "     " + Documentos.get(i) + "      " + tipo.get(i) + "                   " + fechas.get(i) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));

                    arrayObjetosDatos[0] = new String(datos2);


                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }
                }


            }
        } else {

            for (Facturas facturas1 : facturas) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                double sumaFac = 0;
                sumaFac += facturas1.valor;

                String strDate = sdf.format(c.getTime());

                if (cartera1 == null) {

                    datos2 = "     " + "N/A        " + "      " + facturas1.documento + "                   " + strDate + "       " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFac,2)));

                    arrayObjetosDatos[0] = new String(datos2);

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }

                }


            }
        }

        dato = "Sub-total" + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacturasSubTotal,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "Total    " + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Recibido por:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaEspaRecaudosRealizadosNEW(Context context, String param, List<Facturas> idpago, String numeroRecibo, String operacion) throws WriterException, DocumentException, IOException, ParseException {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaFacturasSubTotal = 0;
        double sumaValorPAgado = 0;
        double sumaBalance = 0;
        double salfoAFA = DataBaseBO.SaldoAfavor(numeroRecibo, context);
        double valorNegativos = DataBaseBO.TotalValoresNegativos(numeroRecibo, context);
        Boolean isPrimerRegistro = true;

        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<Double> pagado = new ArrayList<>();
        final List<Double> balance = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();

        final List<String> Documentos = new ArrayList<>();


        facturas = DataBaseBO.getImpresionFacturaRealizados(param, idpago, numeroRecibo, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionClienteRecaudosRealizados(param, idpago,numeroRecibo, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechasRecaudosRealizados(param, idpago, numeroRecibo, context);

        for (Facturas facturas12 : facturasHechas) {
            //SUMAR VALOR PAGADO
            sumaValorPAgado += Double.valueOf(facturas12.valorPagado);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);
        }

        if (salfoAFA == 0) {


            salfoAFA = ((sumaValorPAgado - sumaFacturasSubTotal) * -1);

        }

        if(Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaFacturasSubTotal), 2)) <= Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaValorPAgado),2)))
        {

            isPrimerRegistro = true;
            Double pagadoInicial = 0.0;

            for (Facturas facturas12 : facturasHechas) {

                pagadoInicial = (double) (facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento);

                if(isPrimerRegistro)
                {
                    if(salfoAFA < 0)
                    {
                        pagadoInicial += (salfoAFA * -1);
                        balance.add(salfoAFA);
                        sumaBalance += salfoAFA;

                    }
                    else
                    {
                        balance.add(0.0);
                    }
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(pagadoInicial);
                balance.add(0.0);
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);

                //SUMAR BALANCES
                sumaBalance += 0;

                isPrimerRegistro = false;

            }
        }
        else if(Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaFacturasSubTotal), 2)) > Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaValorPAgado),2)))
        {
            Double pagadoInicial = 0.0;

            for (Facturas facturas12 : facturasHechas) {

                pagadoInicial = (double) (facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento);

                if(pagadoInicial - facturas12.valorPagado != 0)
                {
                    balance.add(Double.valueOf((pagadoInicial - facturas12.valorPagado) -(-valorNegativos)));
                    //SUMAR BALANCES
                    sumaBalance += Double.valueOf((pagadoInicial - facturas12.valorPagado) -(-valorNegativos));
                }
                else
                {
                    balance.add(Double.valueOf(pagadoInicial - facturas12.valorPagado));
                    //SUMAR BALANCES
                    sumaBalance += Double.valueOf(pagadoInicial - facturas12.valorPagado);
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(Double.valueOf(facturas12.valorPagado));
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);

                isPrimerRegistro = false;

            }
        }
        else
        {
            for (Facturas facturas12 : facturasHechas) {

                if(isPrimerRegistro)
                {
                    if(salfoAFA < 0)
                    {
                        facturas12.valorPagado = Float.parseFloat(String.valueOf(facturas12.valorPagado + (salfoAFA*-1)));
                    } else
                    {
                        balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado - (-valorNegativos)));
                    }
                }
                else
                {
                    balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado));
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(Double.valueOf(facturas12.valorPagado));
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);
                balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado));

                //SUMAR BALANCES
                sumaBalance += Double.valueOf(facturas12.valorDocumento - facturas12.valorPagado);

                isPrimerRegistro = false;

            }
        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "EFECTIVO     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHEQUE       ";

            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFERENCIA";


            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }

            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;

        }

        listCarteraDocu = DataBaseBO.getImpresionCarteraRecaudosRealizados(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

            String docu = "";
            if (cartera.documento.length() == 1) {
                docu = cartera.documento + "          ";
            }
            if (cartera.documento.length() == 2) {
                docu = cartera.documento + "         ";
            }
            if (cartera.documento.length() == 3) {
                docu = cartera.documento + "        ";
            }
            if (cartera.documento.length() == 4) {
                docu = cartera.documento + "       ";
            }
            if (cartera.documento.length() == 5) {
                docu = cartera.documento + "      ";
            }
            if (cartera.documento.length() == 6) {
                docu = cartera.documento + "     ";
            }
            if (cartera.documento.length() == 7) {
                docu = cartera.documento + "    ";
            }
            if (cartera.documento.length() == 8) {
                docu = cartera.documento + "   ";
            }
            if (cartera.documento.length() == 9) {
                docu = cartera.documento + "  ";
            }
            if (cartera.documento.length() == 10) {
                docu = cartera.documento + " ";
            }
            Documentos.add(docu);


        }


        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraClienteRecaudosRealizados(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.espbon1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.espbon);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 100, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(180, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine2;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        if (operacion == null) {
            operacion = "RECIBO";
        } else if (operacion != null) {
            if (operacion.equals("X")) {
                operacion = "RECIBO LEGALIZADO";
            } else if (operacion.equals("A")) {
                operacion = "RECIBO ANTICIPO";
            }
        }

        dato = "                             " + operacion;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Consecutivo : " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Nombre cliente: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Código Cliente: " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Creado por: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Fecha: " + encabezadoFactura.fechaRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Hora: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Su pago se aplicara a las siguientes facturas.";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "                           DETALLES DOCUMENTO";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Descripcion " + "Factura           " + " Importe        " + "Total        " + "Saldo  ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + (posY+5) + " " + dato + enter;
        posY += spaceLine1;

        dato = "                " + "                  " + " Documento      " + "cancelado    " + "       ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 120 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  120 " + (posY-spaceLine2) + " 260 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  260 " + (posY-spaceLine2) + " 380 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  380 " + (posY-spaceLine2) + " 480 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  480 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (listCarteraDocu.size() > 0) {
            if (cartera1 != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < listCarteraDocu.size(); i++) {

//                    datos2 = "     " + Documentos.get(i) + "      " + tipo.get(i) + "                   " + fechas.get(i) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));
//                    arrayObjetosDatos[0] = new String(datos2);

                    datos = "        " + tipo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                  " + Documentos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                    " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                  " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(pagado.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                               " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(balance.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 120 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  120 " + (posY-spaceLine1) + " 260 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  260 " + (posY-spaceLine1) + " 380 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  380 " + (posY-spaceLine1) + " 480 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  480 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + "" + enter;
                        posY += spaceLine2;


                    }
                }


            }
        } else {

            for (Facturas facturas1 : facturas) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                double sumaFac = 0;
                sumaFac += facturas1.valor;

                String strDate = sdf.format(c.getTime());

                if (cartera1 == null) {

                    datos2 = "     " + "N/A        " + "      " + facturas1.documento + "                   " + strDate + "       " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFac,2)));

                    arrayObjetosDatos[0] = new String(datos2);

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }

                }


            }
        }

        dato = "                " + "                  " + " TOTAL";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "                                                 " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "                                                               " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaBalance,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "";
        strPrint += "BOX  260 " + (posY-spaceLine2) + " 380 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  380 " + (posY-spaceLine2) + " 480 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  480 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "                           DETALLES PAGO";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Metodos de Pago " + "  Check #      " + "    Banco  " + "        Monto a     ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                    " + "               " + "           " + "        Pagar       ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 150 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  150 " + (posY-spaceLine2) + " 280 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  280 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {

                datos = "     " + metodo.get(i) + "     " + cheke.get(i) + "     " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));

                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);

                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 150 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  150 " + (posY-spaceLine1) + " 280 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  280 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "        " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                    " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                        " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = "                  " + "                  " + "   TOTAL  " + "          " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "";
        strPrint += "BOX  280 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "OBSERVACIONES: " + facturasHechas.get(0).observaciones;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "___________________________________________________________________________________";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "ESTE RECIBO ES EL UNICO COMPROBANTE DE PAGO RECONOCIDO POR LA EMPRESA, ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "NO DEBE CONTENER ALTERACIONES";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "___________________________________________________________________________________";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Recibido por:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaEspaRecaudosPendientes(Context context, String param, List<Facturas> idpago, String numeroRecibo) throws WriterException, DocumentException, IOException, ParseException {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaFacturasSubTotal = 0;


        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();

        final List<String> Documentos = new ArrayList<>();


        facturas = DataBaseBO.getImpresionFacturaPendientes(param, idpago, numeroRecibo, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionClienteRecaudosPendientes(param, idpago, numeroRecibo, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechasRecaudosPendientes(param, idpago, numeroRecibo, context);


        for (Facturas facturas12 : facturasHechas) {

            monto.add(Double.valueOf(facturas12.valorDocumento));
            documentoFin.add(facturas12.documentoFinanciero);
            tipo.add(facturas12.documento);
            fechas.add(facturas12.fechaConsignacion);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);

        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "EFECTIVO     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHEQUE       ";

            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFERENCIA";


            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }

            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;

        }

        listCarteraDocu = DataBaseBO.getImpresionCarteraRecaudosPendientes(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

            String docu = "";
            if (cartera.documento.length() == 1) {
                docu = cartera.documento + "          ";
            }
            if (cartera.documento.length() == 2) {
                docu = cartera.documento + "         ";
            }
            if (cartera.documento.length() == 3) {
                docu = cartera.documento + "        ";
            }
            if (cartera.documento.length() == 4) {
                docu = cartera.documento + "       ";
            }
            if (cartera.documento.length() == 5) {
                docu = cartera.documento + "      ";
            }
            if (cartera.documento.length() == 6) {
                docu = cartera.documento + "     ";
            }
            if (cartera.documento.length() == 7) {
                docu = cartera.documento + "    ";
            }
            if (cartera.documento.length() == 8) {
                docu = cartera.documento + "   ";
            }
            if (cartera.documento.length() == 9) {
                docu = cartera.documento + "  ";
            }
            if (cartera.documento.length() == 10) {
                docu = cartera.documento + " ";
            }
            Documentos.add(docu);


        }


        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraClienteRecaudosPendientes(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.espbon1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.espbon);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 100, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(180, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine3;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                           <--RECIBO ORIGINAL-->";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                      Fecha: " + Utilidades.fechaActual("yyyy-MM-dd");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                      Hora: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                               Recibo de ingreso";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Recibo #: " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Cliente #: " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Nombre cliente: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Direccion :" + cliente.direccion;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Ciudad: " + cliente.ciudad;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Codigo Zip :" + cliente.codigoZip;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Telefono : " + cliente.telefono;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Vendedor: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "                           Metodo de Pago";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "     Cheque #      " + "  Metodo Pago  " + "    Banco  " + "            Monto   ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 130 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  130 " + (posY-spaceLine2) + " 300 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  300 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {
                String arrayObjetos[] = new String[1];
                String arrayObjetosDatos[] = new String[1];

                datos = "     " + cheke.get(i) + "     " + metodo.get(i) + "        " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));


                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);


                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 130 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;


                dato = "";
                strPrint += "BOX  130 " + (posY-spaceLine1) + " 300 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  300 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                      " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                       " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Gracias por su negocio!";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Su pago se aplicara a las siguientes facturas. ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "   Numero documento   " + "  Tipo recaudo " + "    Fecha    " + "          Monto ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 156 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  156 " + (posY-spaceLine2) + " 315 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine2) + " 455 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (listCarteraDocu.size() > 0) {
            if (cartera1 != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < listCarteraDocu.size(); i++) {

                    datos2 = "     " + Documentos.get(i) + "      " + tipo.get(i) + "                   " + Utilidades.ordenarFecha(fechas.get(i)) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));

                    arrayObjetosDatos[0] = new String(datos2);


                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }
                }


            }
        } else {

            for (Facturas facturas1 : facturas) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                double sumaFac = 0;
                sumaFac += facturas1.valor;

                String strDate = sdf.format(c.getTime());

                if (cartera1 == null) {

                    datos2 = "     " + "N/A        " + "      " + facturas1.documento + "                   " + Utilidades.ordenarFecha2(strDate) + "       " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFac,2)));

                    arrayObjetosDatos[0] = new String(datos2);

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }

                }


            }
        }

        dato = "Sub-total" + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacturasSubTotal,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "Total    " + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Recibido por:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaEspaRecaudosPendientesNEW(Context context, String param, List<Facturas> idpago, String numeroRecibo) throws WriterException, DocumentException, IOException, ParseException {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaFacturasSubTotal = 0;
        double sumaValorPAgado = 0;
        double sumaBalance = 0;
        double salfoAFA = DataBaseBO.SaldoAfavorRecaudosPendientes(numeroRecibo, context);
        double valorNegativos = DataBaseBO.TotalValoresNegativosRecaudosPendientes(numeroRecibo, context);
        Boolean isPrimerRegistro = true;


        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<Double> pagado = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<Double> balance = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();

        final List<String> Documentos = new ArrayList<>();


        facturas = DataBaseBO.getImpresionFacturaPendientes(param, idpago, numeroRecibo, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionClienteRecaudosPendientes(param, idpago, numeroRecibo, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechasRecaudosPendientes(param, idpago, numeroRecibo, context);

        for (Facturas facturas12 : facturasHechas) {
            //SUMAR VALOR PAGADO
            sumaValorPAgado += Double.valueOf(facturas12.valorPagado);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);
        }

        if (salfoAFA == 0) {


            salfoAFA = ((sumaValorPAgado - sumaFacturasSubTotal) * -1);

        }

        if(Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaFacturasSubTotal), 2)) <= Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaValorPAgado),2)))
        {

            isPrimerRegistro = true;
            Double pagadoInicial = 0.0;

            for (Facturas facturas12 : facturasHechas) {

                pagadoInicial = (double) (facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento);

                if(isPrimerRegistro)
                {
                    if(salfoAFA < 0)
                    {
                        pagadoInicial += (salfoAFA * -1);
                        balance.add(salfoAFA);
                        sumaBalance += salfoAFA;

                    }
                    else
                    {
                        balance.add(0.0);
                    }
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(pagadoInicial);
                balance.add(0.0);
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);

                //SUMAR BALANCES
                sumaBalance += 0;

                isPrimerRegistro = false;

            }
        }
        else if(Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaFacturasSubTotal), 2)) > Double.parseDouble(Utilidades.Redondear(String.valueOf(sumaValorPAgado),2)))
        {
            Double pagadoInicial = 0.0;

            for (Facturas facturas12 : facturasHechas) {

                pagadoInicial = (double) (facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento);

                if(pagadoInicial - facturas12.valorPagado != 0)
                {
                    balance.add(Double.valueOf((pagadoInicial - facturas12.valorPagado) -(-valorNegativos)));
                    //SUMAR BALANCES
                    sumaBalance += Double.valueOf((pagadoInicial - facturas12.valorPagado) -(-valorNegativos));
                }
                else
                {
                    balance.add(Double.valueOf(pagadoInicial - facturas12.valorPagado));
                    //SUMAR BALANCES
                    sumaBalance += Double.valueOf(pagadoInicial - facturas12.valorPagado);
                }

                monto.add(pagadoInicial);
                pagado.add(Double.valueOf(facturas12.valorPagado));
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);

                isPrimerRegistro = false;

            }
        }
        else
        {
            for (Facturas facturas12 : facturasHechas) {

                if(isPrimerRegistro)
                {
                    if(salfoAFA < 0)
                    {
                        facturas12.valorPagado = Float.parseFloat(String.valueOf(facturas12.valorPagado + (salfoAFA*-1)));
                    } else
                    {
                        balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado - (-valorNegativos)));
                    }
                }
                else
                {
                    balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado));
                }

                monto.add(Double.valueOf(facturas12.valorDocumento));
                pagado.add(Double.valueOf(facturas12.valorPagado));
                documentoFin.add(facturas12.documentoFinanciero);
                tipo.add(facturas12.documento);
                fechas.add(facturas12.fechaConsignacion);
                balance.add(Double.valueOf(facturas12.valorDocumento < 0 ? 0 : facturas12.valorDocumento - facturas12.valorPagado));

                //SUMAR BALANCES
                sumaBalance += Double.valueOf(facturas12.valorDocumento - facturas12.valorPagado);

                isPrimerRegistro = false;

            }
        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "EFECTIVO     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHEQUE       ";

            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFERENCIA";


            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }

            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;

        }

        listCarteraDocu = DataBaseBO.getImpresionCarteraRecaudosPendientes(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

            String docu = "";
            if (cartera.documento.length() == 1) {
                docu = cartera.documento + "          ";
            }
            if (cartera.documento.length() == 2) {
                docu = cartera.documento + "         ";
            }
            if (cartera.documento.length() == 3) {
                docu = cartera.documento + "        ";
            }
            if (cartera.documento.length() == 4) {
                docu = cartera.documento + "       ";
            }
            if (cartera.documento.length() == 5) {
                docu = cartera.documento + "      ";
            }
            if (cartera.documento.length() == 6) {
                docu = cartera.documento + "     ";
            }
            if (cartera.documento.length() == 7) {
                docu = cartera.documento + "    ";
            }
            if (cartera.documento.length() == 8) {
                docu = cartera.documento + "   ";
            }
            if (cartera.documento.length() == 9) {
                docu = cartera.documento + "  ";
            }
            if (cartera.documento.length() == 10) {
                docu = cartera.documento + " ";
            }
            Documentos.add(docu);


        }


        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraClienteRecaudosPendientes(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.espbon1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.espbon);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 100, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(180, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine3;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                             RECIBO";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Consecutivo : " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Nombre cliente: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Código Cliente: " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Creado por: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Fecha: " + encabezadoFactura.fechaRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Hora: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Su pago se aplicara a las siguientes facturas.";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "                           DETALLES DOCUMENTO";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Descripcion " + "Factura           " + " Importe        " + "Total        " + "Saldo  ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                " + "                  " + " Documento      " + "cancelado    " + "       ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 120 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  120 " + (posY-spaceLine2) + " 260 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  260 " + (posY-spaceLine2) + " 380 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  380 " + (posY-spaceLine2) + " 480 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  480 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (listCarteraDocu.size() > 0) {
            if (cartera1 != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < listCarteraDocu.size(); i++) {

//                    datos2 = "     " + Documentos.get(i) + "      " + tipo.get(i) + "                   " + fechas.get(i) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));
//                    arrayObjetosDatos[0] = new String(datos2);

                    datos = "        " + tipo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                " + Documentos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                  " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(pagado.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                             " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(balance.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 120 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  120 " + (posY-spaceLine1) + " 260 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  260 " + (posY-spaceLine1) + " 380 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  380 " + (posY-spaceLine1) + " 480 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  480 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + "" + enter;
                        posY += spaceLine2;


                    }
                }


            }
        } else {

            for (Facturas facturas1 : facturas) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                double sumaFac = 0;
                sumaFac += facturas1.valor;

                String strDate = sdf.format(c.getTime());

                if (cartera1 == null) {

                    datos2 = "     " + "N/A        " + "      " + facturas1.documento + "                   " + strDate + "       " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFac,2)));

                    arrayObjetosDatos[0] = new String(datos2);

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }

                }


            }
        }

        dato = "                                  TOTAL";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "                                                " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "                                                             " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaBalance,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "";
        strPrint += "BOX  260 " + (posY-spaceLine2) + " 380 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  380 " + (posY-spaceLine2) + " 480 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  480 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "                           DETALLES PAGO";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    Metodos de Pago " + "  Check #      " + "    Banco  " + "        Monto a     ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                    " + "               " + "           " + "        Pagar       ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 150 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  150 " + (posY-spaceLine2) + " 280 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  280 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {

                datos = "     " + metodo.get(i) + "     " + cheke.get(i) + "     " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));

                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);

                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 150 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  150 " + (posY-spaceLine1) + " 280 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  280 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "        " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                    " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = "                  " + "                  " + "   TOTAL  " + "       " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2)));
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "";
        strPrint += "BOX  280 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "OBSERVACIONES: " + facturasHechas.get(0).observaciones;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "___________________________________________________________________________________";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "ESTE RECIBO ES EL UNICO COMPROBANTE DE PAGO RECONOCIDO POR LA EMPRESA, ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "NO DEBE CONTENER ALTERACIONES";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "___________________________________________________________________________________";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Recibido por:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresaEspa(Context context, String param, List<Facturas> idpago) throws WriterException, DocumentException, IOException, ParseException {

        String strPrint, dato, datos, datos2;

        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "LEFT" + enter;
        double sumaFacTuras = 0;
        double sumaFacturasSubTotal = 0;


        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        ArrayList<Facturas> facturasHechas = new ArrayList<Facturas>();
        ArrayList<Facturas> listaFacs = new ArrayList<Facturas>();
        ArrayList<Cartera> listCarteraDocu = new ArrayList<Cartera>();

        final List<String> documentoFin = new ArrayList<>();
        final List<String> tipo = new ArrayList<>();
        final List<Double> valorSum = new ArrayList<>();

        final List<String> cheke = new ArrayList<>();
        final List<String> metodo = new ArrayList<>();
        final List<String> bancos = new ArrayList<>();
        final List<Double> monto = new ArrayList<>();
        final List<String> fechas = new ArrayList<>();

        final List<String> Documentos = new ArrayList<>();


        facturas = DataBaseBO.getImpresionFactura(param, idpago, context);

        Facturas encabezadoFactura = DataBaseBO.getImpresionCliente(param, idpago, context);

        facturasHechas = DataBaseBO.getImpresionFacturaHechas(param, idpago, context);


        for (Facturas facturas12 : facturasHechas) {

            monto.add(Double.valueOf(facturas12.valorDocumento));
            documentoFin.add(facturas12.documentoFinanciero);
            tipo.add(facturas12.documento);
            fechas.add(facturas12.fechaConsignacion);

            //SUMAR MONTO FACTURAS INDIVIDUAL
            sumaFacturasSubTotal += Double.valueOf(facturas12.valorDocumento);

        }

        for (Facturas facturas1 : facturas) {


            String tipoPago = "N/A";
            String tipobank = "N/A         ";


            if (facturas1.viaPago.equals("A")) {
                tipoPago = "EFECTIVO     ";
            }
            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHEQUE       ";

            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFERENCIA";


            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA      ";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN      ";

            }

            metodo.add(tipoPago);

            if (facturas1.banco.equals("Select")) {

                tipobank = "N/A         ";

            } else if (facturas1.banco.equals("Seleccione")) {
                tipobank = "N/A         ";

            } else if (!facturas1.banco.equals("Select") || !facturas1.banco.equals("Seleccione")) {
                if (facturas1.banco.length() == 1) {
                    tipobank = facturas1.banco + "           ";
                }
                if (facturas1.banco.length() == 2) {
                    tipobank = facturas1.banco + "          ";
                }
                if (facturas1.banco.length() == 3) {
                    tipobank = facturas1.banco + "         ";
                }
                if (facturas1.banco.length() == 4) {
                    tipobank = facturas1.banco + "        ";
                }
                if (facturas1.banco.length() == 5) {
                    tipobank = facturas1.banco + "       ";
                }
                if (facturas1.banco.length() == 6) {
                    tipobank = facturas1.banco + "      ";
                }
                if (facturas1.banco.length() == 7) {
                    tipobank = facturas1.banco + "     ";
                }
                if (facturas1.banco.length() == 8) {
                    tipobank = facturas1.banco + "    ";
                }
                if (facturas1.banco.length() == 9) {
                    tipobank = facturas1.banco + "   ";
                }
                if (facturas1.banco.length() == 10) {
                    tipobank = facturas1.banco + "  ";
                }
                if (facturas1.banco.length() == 11) {
                    tipobank = facturas1.banco + " ";
                }
                if (facturas1.banco.length() == 12) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 13) {
                    tipobank = facturas1.banco.substring(0,11) + " ";
                }
                if (facturas1.banco.length() == 14) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 15) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 16) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 17) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 18) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 19) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }
                if (facturas1.banco.length() == 20) {
                    tipobank = facturas1.banco.substring(0,11) + " ";

                }

            }
            bancos.add(tipobank);

            if (!facturas1.numeroCheque.equals(null)) {
                String numchk = "";
                if (facturas1.numeroCheque.length() == 1) {
                    numchk = facturas1.numeroCheque + "        ";
                }
                if (facturas1.numeroCheque.length() == 2) {
                    numchk = facturas1.numeroCheque + "       ";
                }
                if (facturas1.numeroCheque.length() == 3) {
                    numchk = facturas1.numeroCheque + "      ";
                }
                if (facturas1.numeroCheque.length() == 4) {
                    numchk = facturas1.numeroCheque + "     ";
                }
                if (facturas1.numeroCheque.length() == 5) {
                    numchk = facturas1.numeroCheque + "    ";
                }
                if (facturas1.numeroCheque.length() == 6) {
                    numchk = facturas1.numeroCheque + "   ";
                }
                if (facturas1.numeroCheque.length() == 7) {
                    numchk = facturas1.numeroCheque + "  ";
                }
                if (facturas1.numeroCheque.length() == 8) {
                    numchk = facturas1.numeroCheque + " ";
                }
                cheke.add(numchk);

            }else{
                String numchk = "";
                numchk = facturas1.numeroCheque+"           ";
                cheke.add(numchk);
            }

            valorSum.add(facturas1.valor);
            sumaFacTuras += facturas1.valor;

        }

        listCarteraDocu = DataBaseBO.getImpresionCartera(documentoFin, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        for (Cartera cartera:listCarteraDocu) {

                String docu = "";
                if (cartera.documento.length() == 1) {
                    docu = cartera.documento + "          ";
                }
                if (cartera.documento.length() == 2) {
                    docu = cartera.documento + "         ";
                }
                if (cartera.documento.length() == 3) {
                    docu = cartera.documento + "        ";
                }
                if (cartera.documento.length() == 4) {
                    docu = cartera.documento + "       ";
                }
                if (cartera.documento.length() == 5) {
                    docu = cartera.documento + "      ";
                }
                if (cartera.documento.length() == 6) {
                    docu = cartera.documento + "     ";
                }
                if (cartera.documento.length() == 7) {
                    docu = cartera.documento + "    ";
                }
                if (cartera.documento.length() == 8) {
                    docu = cartera.documento + "   ";
                }
                if (cartera.documento.length() == 9) {
                    docu = cartera.documento + "  ";
                }
                if (cartera.documento.length() == 10) {
                    docu = cartera.documento + " ";
                }
            Documentos.add(docu);


        }


        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        Cartera cartera1 = DataBaseBO.cargarCarteraCliente(documentoFin, context);

        byte[] data = null;
        Resources res = Resources.getSystem();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {

            //InputStream is = new FileInputStream(new File(Util.DirApp(), "logoluker.bmp"));
            @SuppressLint("ResourceType") InputStream is = context.getResources().openRawResource(R.drawable.espbon1);
            Bitmap bi = BitmapFactory.decodeResource(context.getResources(), R.drawable.espbon);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 100, baos);

            data = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmapimage = BitmapFactory.decodeByteArray(data, 0, data.length);

        ParseBitmap BmpParserImage = new ParseBitmap(bitmapimage);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // 1.0 LOGO SUPERIOR
        String strLogo = BmpParserImage.ExtractGraphicsDataForCPCL(180, 0);
        String zplDataLogo = strLogo;

        strPrint += zplDataLogo;
        posY += spaceLine3;

        /** dato = "CORDIALSA USA,INC";
         strPrint += "TEXT " + font + " " + size + " " + 240 + " " + 210 + " " + dato + enter;
         posY += spaceLine2; **/

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        dato = "                           <--RECIBO ORIGINAL-->";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                      Fecha: " + Utilidades.fechaActual("yyyy-MM-dd");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                                      Hora: " + Utilidades.fechaActual("HH:mm:ss");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "                               Recibo de ingreso";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Recibo #: " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Cliente #: " + cliente.codigo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Nombre cliente: " + cliente.nombre;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;


        dato = "    Direccion :" + cliente.direccion;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Ciudad: " + cliente.ciudad;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Codigo Zip :" + cliente.codigoZip;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Telefono : " + cliente.telefono;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Vendedor: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "                           Metodo de Pago";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "     Cheque #      " + "  Metodo Pago  " + "    Banco  " + "            Monto   ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 130 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  130 " + (posY-spaceLine2) + " 300 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  300 " + (posY-spaceLine2) + " 420 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  420 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        if (metodo.size() > 0) {
            for (int i = 0; i < metodo.size(); i++) {
                String arrayObjetos[] = new String[1];
                String arrayObjetosDatos[] = new String[1];

                datos = "     " + cheke.get(i) + "     " + metodo.get(i) + "        " + bancos.get(i) + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));


                arrayObjetos[0] = new String(dato);
                arrayObjetosDatos[0] = new String(datos);


                dato = " ";
                strPrint += "BOX  20 " + (posY-spaceLine1) + " 130 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;


                dato = "";
                strPrint += "BOX  130 " + (posY-spaceLine1) + " 300 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  300 " + (posY-spaceLine1) + " 420 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                dato = "";
                strPrint += "BOX  420 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                for (int j = 0; j < arrayObjetosDatos.length; j++) {


                    datos = "     " + cheke.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                      " + metodo.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                       " + bancos.get(i);
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;

                    datos = "                                                     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(valorSum.get(i),2)));
                    strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + datos + enter;
                    posY += spaceLine2;


                }
            }

        }

        dato = " ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Gracias por su negocio!";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "    Su pago se aplicara a las siguientes facturas. ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "   Numero documento   " + "  Tipo recaudo " + "    Fecha    " + "          Monto ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = " ";
        strPrint += "BOX  20 " + (posY-spaceLine2) + " 156 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  156 " + (posY-spaceLine2) + " 315 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine2) + " 455 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine2) + " 560 " + ((posY)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        String arrayObjetos[] = new String[1];
        String arrayObjetosDatos[] = new String[1];


        if (listCarteraDocu.size() > 0) {
            if (cartera1 != null) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


                String strDate = sdf.format(c.getTime());
                for (int i = 0; i < listCarteraDocu.size(); i++) {

                    datos2 = "     " + Documentos.get(i) + "      " + tipo.get(i) + "                   " + Utilidades.ordenarFecha(fechas.get(i)) + "     " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(monto.get(i),2)));

                    arrayObjetosDatos[0] = new String(datos2);


                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }
                }


            }
        } else {

            for (Facturas facturas1 : facturas) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                double sumaFac = 0;
                sumaFac += facturas1.valor;

                String strDate = sdf.format(c.getTime());

                if (cartera1 == null) {

                    datos2 = "     " + "N/A        " + "      " + facturas1.documento + "                   " + strDate + "       " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFac,2)));

                    arrayObjetosDatos[0] = new String(datos2);

                    for (int j = 0; j < arrayObjetosDatos.length; j++) {

                        dato = " ";
                        strPrint += "BOX  20 " + (posY-spaceLine1) + " 156 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  156 " + (posY-spaceLine1) + " 315 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  315 " + (posY-spaceLine1) + " 455 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        dato = "";
                        strPrint += "BOX  455 " + (posY-spaceLine1) + " 560 " + ((posY+25)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

                        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + arrayObjetosDatos[j] + enter;
                        posY += spaceLine2;


                    }

                }


            }
        }

        dato = "Sub-total" + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacturasSubTotal,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "Total    " + "      " + Utilidades.separarMiles(String.valueOf(Utilidades.formatearDecimales(sumaFacTuras,2))) + "  ";
        strPrint += "TEXT " + font + " " + size + " " + 340 + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "";
        strPrint += "BOX  315 " + (posY-spaceLine3) + " 455 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "";
        strPrint += "BOX  455 " + (posY-spaceLine3) + " 560 " + ((posY-spaceLine1)) + " 1 + 'dato'" + font + " " + size + " " + posX + " " + posY + " " + dato + enter;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;

        dato = "    Recibido por:";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;


        dato = "    ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }


    public static String formatoTirillaEntrega2(String param, List<Facturas> idpago, Context context) throws WriterException {

        String strPrint, dato;
        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "CENTER" + enter;

        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        facturas = DataBaseBO.getImpresionFactura(param, idpago, context);
        Facturas encabezadoFactura = DataBaseBO.getImpresionCliente(param, idpago, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        dato = "Recibo de Ingreso";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;
        dato = "  Recibo de ingreso No: " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Nit :" + cliente.nit;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Empresa: " + empresa;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Nombre :" + cliente.razonSocial;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Telefono : " + cliente.telefono;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Vendedor: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;
        dato = " /**********INFORMACION PAGO***********/";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;
        dato = "  Detalles formas pago";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        for (Facturas facturas1 : facturas) {

            dato = "CK No/ Aprob.No: " + facturas1.nroRecibo;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine2;

            dato = "Banco/Tarjeta: " + facturas1.banco;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            String tipoPago = "N/A";

            if (facturas1.viaPago.equals("A")) {
                tipoPago = "EFECTIVO";
            }

            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHEQUE";

            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFERENCIA";


            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN";

            }

            dato = "Forma Pago: " + tipoPago;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine2;

        }

        dato = " /**********INFORMACION ***********/";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "  Distinguidos Sres";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "  Hemos recibido con fecha " + Utilidades.fechaActual("yyyy-MM-dd");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;
        dato = "   la suma de RD$ " + encabezadoFactura.valorDocumento + " aplicando a los siguientes documentos: ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        for (Facturas facturas1 : facturas) {

            dato = "No Factura: " + facturas1.nroRecibo;
            ;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            dato = "Tipo recaudo : Factura";
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            dato = "Fecha: " + facturas1.fecha;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            dato = "Importe : ";
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            dato = "Valor : " + facturas1.valor;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

        }

        dato = "  Sub-total : " + encabezadoFactura.valorDocumento;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "  Total : " + encabezadoFactura.valorDocumento;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "  Recibo conforme ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = " ORIGINAL ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }

    public static String formatoTirillaEntregaEmpresa(String param, List<Facturas> idpago, Context context) throws WriterException {

        String strPrint, dato;
        char ret1 = 13;
        char ret2 = 10;
        int font, size, posX, posY, cantCL, spaceLine1, spaceLine2, spaceLine3, spaceLine4;
        spaceLine1 = 25;
        spaceLine2 = spaceLine1 * 2;
        spaceLine3 = spaceLine1 * 3;
        spaceLine4 = spaceLine1 * 4;
        font = 0;
        size = 2;
        posX = 0;
        posY = 10;
        cantCL = 71; //La cantidad de caracteres que recibe una linea de impresion
        String enter = String.valueOf(ret1) + String.valueOf(ret2);
        strPrint = "";
        strPrint += "CENTER" + enter;

        ArrayList<Facturas> facturas = new ArrayList<Facturas>();
        facturas = DataBaseBO.getImpresionFactura(param, idpago, context);
        Facturas encabezadoFactura = DataBaseBO.getImpresionCliente(param, idpago, context);
        String empresa = DataBaseBO.cargarEmpresa(context);
        String vendedor = DataBaseBO.cargarUsuarioApp(context);

        Cliente cliente = DataBaseBO.cargarCliente(encabezadoFactura.codCliente, context);

        dato = "Recibo de Ingreso";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;
        dato = "  Recibo de ingreso No: " + encabezadoFactura.nroRecibo;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Nit :" + cliente.nit;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Empresa: " + empresa;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Nombre :" + cliente.razonSocial;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Telefono : " + cliente.telefono;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine1;
        dato = "  Vendedor: " + vendedor;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;
        dato = " /**********INFORMACION PAGO***********/";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;
        dato = "  Detalles formas pago";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        for (Facturas facturas1 : facturas) {

            dato = "CK No/ Aprob.No: " + facturas1.nroRecibo;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine2;

            dato = "Banco/Tarjeta: " + facturas1.banco;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            String tipoPago = "N/A";

            if (facturas1.viaPago.equals("A")) {
                tipoPago = "EFECTIVO";
            }

            if (facturas1.viaPago.equals("B")) {
                tipoPago = "CHEQUE";

            }
            if (facturas1.viaPago.equals("6")) {
                tipoPago = "TRANSFERENCIA";


            }
            if (facturas1.viaPago.equals("O")) {
                tipoPago = "TARJETA";

            }
            if (facturas1.viaPago.equals("4")) {
                tipoPago = "BITCOIN";

            }

            dato = "Forma Pago: " + tipoPago;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine2;

        }

        dato = " /**********INFORMACION ***********/";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = "  Distinguidos Sres";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "  Hemos recibido con fecha " + Utilidades.fechaActual("yyyy-MM-dd");
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;
        dato = "   la suma de RD$ " + encabezadoFactura.valorDocumento + " aplicando a los siguientes documentos: ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        for (Facturas facturas1 : facturas) {

            dato = "No Factura: " + facturas1.nroRecibo;
            ;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            dato = "Tipo recaudo : Factura";
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            dato = "Fecha: " + facturas1.fecha;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            dato = "Importe : ";
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

            dato = "Valor : " + facturas1.valor;
            strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
            posY += spaceLine1;

        }

        dato = "  Sub-total : " + encabezadoFactura.valorDocumento;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "  Total : " + encabezadoFactura.valorDocumento;
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;

        dato = "  Recibo conforme ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine2;


        dato = " ORIGINAL ";
        strPrint += "TEXT " + font + " " + size + " " + posX + " " + posY + " " + dato + enter;
        posY += spaceLine3;

        strPrint += "PRINT" + enter;
        strPrint = "! 0 200 200 " + posY + " 1" + enter + strPrint;
        strPrint += enter + enter;


        return strPrint;
    }


    /**
     * Metodo encargado de armar el formato de devolucion
     *
     * @return
     */
    public static String formatoDevolucion(boolean isCheckIn) {
        return "";
    }


    /**
     * Metodo encargado de calcular el tamano maximo de un vector para cargar
     * los espacios
     *
     * @param cadenas
     * @return
     */
    public static int calcularEspacios(Vector<String> cadenas) {
        int espacio = 0;
        int auxiliar = 0;
        for (String cadena : cadenas) {
            auxiliar = cadena.length();
            if (espacio < auxiliar)
                espacio = auxiliar;
        }
        return espacio;
    }


    /**
     * Metodo encargado de agregar espacios a las una cadena
     *
     * @param cadena
     * @param espacio
     * @return
     */
    public static String agregarEspacioCadena(String cadena, int espacio) {
        String cadenaEspacio = "";
        if (espacio > cadena.length()) {
            int numeroEspacios = espacio - cadena.length();
            for (int i = 0; i < numeroEspacios; i++) {
                cadenaEspacio += " ";
            }
        }
        return (cadenaEspacio + cadena);
    }


    /**
     * Metodo encargado de imprimir las tirillas de la impresora Apex
     *
     * @param socket
     * @param textoImprimir
     */
    public static void ImprimiendoPrinter(final BluetoothSocket socket, final String textoImprimir) {

        splashTread = new Thread() {
            @Override
            public void run() {

                try {
                    synchronized (this) {
                        wait(_splashTime);
                    }

                } catch (InterruptedException e) {
                } finally {
                    OutputStream salida = null;
                    try {


                        char ret1 = 13;
                        char ret2 = 10;
                        String enter = String.valueOf(ret1) + String.valueOf(ret2);

                        String textoImprimirfinal = textoImprimir + enter + enter + enter;

                        salida = socket.getOutputStream();

                        synchronized (this) {

                            wait(_splashTime);
                        }

                        salida.write(textoImprimirfinal.getBytes());

                        synchronized (this) {

                            wait(_splashTime2);
                        }

                        salida.flush();


                    } catch (Exception e2) {

//						String e = e2.getMessage();
                        e2.printStackTrace();

                    } finally {

                        try {

                            if (socket != null) {
                                socket.close();
                            }
                            salida.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        splashTread.start();
    }
}