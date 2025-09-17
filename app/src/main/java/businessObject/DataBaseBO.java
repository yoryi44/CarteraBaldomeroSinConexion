package businessObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import dataobject.Bancos;
import dataobject.Cartera;
import dataobject.Cliente;
import dataobject.ClienteSincronizado;
import dataobject.CuentasBanco;
import dataobject.Dias;
import dataobject.Facturas;
import dataobject.FacturasRealizadas;
import dataobject.FirmaNombre;
import dataobject.Fotos;
import dataobject.Main;
import dataobject.MotivosAbono;
import dataobject.ObjetoCalculoCartera;
import dataobject.Pendientes;
import dataobject.Usuario;
import utilidades.Utilidades;

public class DataBaseBO {

    private static final String TAG = "DataBaseBO";

    private static File dbFile;

    private static String mensaje;


    @SuppressLint("LongLogTag")
    public static List<ClienteSincronizado> obtenerListaClientesSincronizadosAux(int opcionOrden, Context context) {

        SQLiteDatabase db = null;
        List<ClienteSincronizado> listaClientes = new Vector<>();
        String query = "";

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String queryDiasMora = "SELECT max( " +
                    "CAST ( ( julianday(datetime('now')) - julianday(datetime(substr(FechaVecto,1,4) || '-' || substr(FechaVecto,5,2) || '-' || SUBSTR(FechaVecto, 7, 2)) ) ) AS INT ))   AS dias\n" +
                    " FROM Cartera INNER JOIN clientes cli ON Cartera.cliente = cli.Codigo " +
                    " WHERE cli.codigo=Clientes.Codigo GROUP by cli.codigo ORDER BY cli.codigo DESC ";

            if (opcionOrden == 0 || opcionOrden == 1) {

                query = "SELECT Clientes.Codigo Codigo, Clientes.Nombre Nombre, Razonsocial Razonsocial, Nit Nit, Direccion Direccion, Ciudad Ciudad, " +
                        "Telefono Telefono, Canal Canal, Clientes.Subcanal Subcanal, Cupo Cupo, Actividad Actividad, Agencia Agencia, Linea Linea, " +
                        "Territorio Territorio, Clientes.latitud latitud, Clientes.longitud longitud, condicionpago condicionpago, Barrio Barrio, " +
                        "Nombres Nombres, Apellidos Apellidos, Zonaventas Zonaventas, email email, Potencial Potencial, Valor Valor, " +
                        "Necesidad Necesidad, ciudad2 ciudad2, Cupo_disponible Cupo_disponible, cw.latitud latitudW, cw.longitud longitudW, tp.nombre Segmento, ruta1 ruta1, ruta2 ruta2," +
                        "email email, (" + queryDiasMora + ") AS diasMora " +
                        "FROM Clientes " +
                        "LEFT JOIN coordenadaswaze cw ON Clientes.codigo = cw.codigo " +
                        "LEFT JOIN Tipologia tp ON Clientes.Actividad = tp.codigo " +
                        "GROUP BY Clientes.Codigo";

            } else {

                // 1 - UBICACIÓN-> UTILIZA API DE GOOGLE
                // 2 - CONDICIONPAGO
                // 3 - SALDOCARTERA
                // 4 - DIASMORA [ SE IMPLEMENTA POR LOGICA DESDE EL CODIGO ]
                if (opcionOrden == 2) {

                    query = "SELECT Clientes.Codigo Codigo, Clientes.Nombre Nombre, Razonsocial Razonsocial, Nit Nit, Direccion Direccion, Ciudad Ciudad, " +
                            "Telefono Telefono, Canal Canal, Clientes.Subcanal Subcanal, Cupo Cupo, Actividad Actividad, Agencia Agencia, Linea Linea, " +
                            "Territorio Territorio, Clientes.latitud latitud, Clientes.longitud longitud, condicionpago condicionpago, Barrio Barrio, " +
                            "Nombres Nombres, Apellidos Apellidos, Zonaventas Zonaventas, email email, Potencial Potencial, Valor Valor, " +
                            "Necesidad Necesidad, ciudad2 ciudad2, Cupo_disponible Cupo_disponible, cw.latitud latitudW, cw.longitud longitudW, tp.nombre Segmento, ruta1 ruta1, ruta2 ruta2," +
                            "(" + queryDiasMora + ") AS diasMora " +
                            "FROM Clientes " +
                            "LEFT JOIN coordenadaswaze cw ON Clientes.codigo = cw.codigo " +
                            "LEFT JOIN Tipologia tp ON Clientes.Actividad = tp.codigo " +
                            "GROUP BY Clientes.Codigo " +
                            "ORDER BY Clientes.prioridadcondicion DESC, clientes.condicionpago DESC";

                } else if (opcionOrden == 3) {

                    query = "SELECT Clientes.Codigo Codigo, " +
                            "CASE WHEN (SELECT SUM(SALDO) FROM Cartera WHERE Cartera.cliente = Clientes.Codigo) IS NULL THEN 0 ELSE (SELECT SUM(SALDO) FROM Cartera WHERE Cartera.cliente = Clientes.Codigo) END AS Cartera,  " +
                            "Clientes.Nombre Nombre, Razonsocial Razonsocial, Nit Nit, Direccion Direccion, Ciudad Ciudad, " +
                            "Telefono Telefono, Canal Canal, Clientes.Subcanal Subcanal, Cupo Cupo, Actividad Actividad, Agencia Agencia, Linea Linea, " +
                            "Territorio Territorio, Clientes.latitud latitud, Clientes.longitud longitud, condicionpago condicionpago, Barrio Barrio, " +
                            "Nombres Nombres, Apellidos Apellidos, Zonaventas Zonaventas, email email, Potencial Potencial, Valor Valor, " +
                            "Necesidad Necesidad, ciudad2 ciudad2, Cupo_disponible Cupo_disponible, cw.latitud latitudW, cw.longitud longitudW, tp.nombre Segmento, ruta1 ruta1, ruta2 ruta2," +
                            "(" + queryDiasMora + ") AS diasMora " +
                            "FROM Clientes " +
                            "LEFT JOIN coordenadaswaze cw ON Clientes.codigo = cw.codigo " +
                            "LEFT JOIN Tipologia tp ON Clientes.Actividad = tp.codigo " +
                            "GROUP BY Clientes.Codigo " +
                            "ORDER BY Cartera DESC";

                } else if (opcionOrden == 4) {

                    query = "SELECT Clientes.Codigo Codigo, " +
                            "(SELECT fechavecto AS FechaVecto FROM Cartera WHERE Cartera.cliente = Clientes.Codigo ORDER BY fechavecto ASC LIMIT 1) AS FechaVecto, " +
                            "Clientes.Nombre Nombre, Razonsocial Razonsocial, Nit Nit, Direccion Direccion, Ciudad Ciudad, " +
                            "Telefono Telefono, Canal Canal, Clientes.Subcanal Subcanal, Cupo Cupo, Actividad Actividad, Agencia Agencia, Linea Linea, " +
                            "Territorio Territorio, Clientes.latitud latitud, Clientes.longitud longitud, condicionpago condicionpago, Barrio Barrio, " +
                            "Nombres Nombres, Apellidos Apellidos, Zonaventas Zonaventas, email email, Potencial Potencial, Valor Valor, " +
                            "Necesidad Necesidad, ciudad2 ciudad2, Cupo_disponible Cupo_disponible, cw.latitud latitudW, cw.longitud longitudW, tp.nombre Segmento, ruta1 ruta1, ruta2 ruta2," +
                            "(" + queryDiasMora + ") AS diasMora " +
                            "FROM Clientes " +
                            "LEFT JOIN coordenadaswaze cw ON Clientes.codigo = cw.codigo " +
                            "LEFT JOIN Tipologia tp ON Clientes.Actividad = tp.codigo " +
                            "WHERE fechavecto NOT NULL " +
                            "GROUP BY Clientes.Codigo " +
                            "ORDER BY FechaVecto ASC";
                }

                Main.clientes = new String[]{};
                Main.nuevosClientesSeleccionados.clear();
            }

            Cursor cursor = db.rawQuery(query, null);
            int orden = 1;
            if (cursor.moveToFirst()) {

                do {

                    ClienteSincronizado cliente = new ClienteSincronizado();

                    cliente.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    cliente.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                    cliente.razonSocial = cursor.getString(cursor.getColumnIndex("Razonsocial"));
                    cliente.nit = cursor.getString(cursor.getColumnIndex("Nit"));
                    cliente.direccion = cursor.getString(cursor.getColumnIndex("Direccion"));
                    cliente.ciudad = cursor.getString(cursor.getColumnIndex("Ciudad"));
                    cliente.telefono = cursor.getString(cursor.getColumnIndex("Telefono"));
                    cliente.canal = cursor.getString(cursor.getColumnIndex("Canal"));
                    cliente.subcanal = cursor.getString(cursor.getColumnIndex("Subcanal"));
                    cliente.cupo = cursor.getDouble(cursor.getColumnIndex("Cupo"));
                    cliente.actividad = cursor.getString(cursor.getColumnIndex("Actividad"));
                    cliente.agencia = cursor.getString(cursor.getColumnIndex("Agencia"));
                    cliente.linea = cursor.getString(cursor.getColumnIndex("Linea"));
                    cliente.territorio = cursor.getString(cursor.getColumnIndex("Territorio"));
                    cliente.latitud = cursor.getDouble(cursor.getColumnIndex("latitud"));
                    cliente.longitud = cursor.getDouble(cursor.getColumnIndex("longitud"));
                    cliente.condicionPago = cursor.getString(cursor.getColumnIndex("condicionpago"));
                    cliente.barrio = cursor.getString(cursor.getColumnIndex("Barrio"));
                    cliente.nombres = cursor.getString(cursor.getColumnIndex("Nombres"));
                    cliente.apellidos = cursor.getString(cursor.getColumnIndex("Apellidos"));
                    cliente.zonaVentas = cursor.getString(cursor.getColumnIndex("Zonaventas"));
                    cliente.email = cursor.getString(cursor.getColumnIndex("email"));
                    cliente.diasMora = cursor.getInt(cursor.getColumnIndex("diasMora"));
                    if (cliente.diasMora <= 0) {
                        cliente.diasMora = 0;
                    }
                    cliente.potencial = cursor.getString(cursor.getColumnIndex("Potencial"));
                    cliente.valor = cursor.getString(cursor.getColumnIndex("Valor"));
                    cliente.necesidad = cursor.getString(cursor.getColumnIndex("Necesidad"));
                    cliente.ciudad2 = cursor.getString(cursor.getColumnIndex("ciudad2"));
                    cliente.cupoDisponible = cursor.getDouble(cursor.getColumnIndex("Cupo_disponible"));
                    cliente.latitudW = cursor.getDouble(cursor.getColumnIndex("latitudW"));
                    cliente.longitudW = cursor.getDouble(cursor.getColumnIndex("longitudW"));
                    cliente.segmento = cursor.getString(cursor.getColumnIndex("Segmento"));
                    cliente.ruta1 = cursor.getString(cursor.getColumnIndex("ruta1"));
                    cliente.ruta2 = cursor.getString(cursor.getColumnIndex("ruta2"));
                    cliente.email = cursor.getString(cursor.getColumnIndex("email"));
                    cliente.NumeroRuta = orden;

                    // SE AGREGA LA LOGICA DE CARTERA POR CLIENTE
                    cliente.totalCartera = Utilidades.separarMilesSinDecimal(String.valueOf(DataBaseBO.obtenerCarteraCliente(cliente.codigo, context)), context);

                    orden++;
                    listaClientes.add(cliente);

                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("Consulta Clientes Sincronizados", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        Main.listaClientes = listaClientes;

        if (opcionOrden == 4) {
            obtenerListaDiasMoraNull(context);
        }

        return listaClientes;
    }

    @SuppressLint("LongLogTag")
    private static void obtenerListaDiasMoraNull(Context context) {

        SQLiteDatabase db = null;

        String query;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String queryDiasMora = "SELECT max( " +
                    "CAST ( ( julianday(datetime('now')) - julianday(datetime(substr(FechaVecto,1,4) || '-' || substr(FechaVecto,5,2) || '-' || SUBSTR(FechaVecto, 7, 2)) ) ) AS INT ))   AS dias\n" +
                    " FROM Cartera INNER JOIN clientes cli ON Cartera.cliente = cli.Codigo " +
                    " WHERE cli.codigo=Clientes.Codigo GROUP by cli.codigo ORDER BY cli.codigo DESC ";


            query = "SELECT Clientes.Codigo Codigo, " +
                    "(SELECT fechavecto AS FechaVecto FROM Cartera WHERE Cartera.cliente = Clientes.Codigo ORDER BY fechavecto ASC LIMIT 1) AS FechaVecto, " +
                    "Clientes.Nombre Nombre, Razonsocial Razonsocial, Nit Nit, Direccion Direccion, Ciudad Ciudad, " +
                    "Telefono Telefono, Canal Canal, Clientes.Subcanal Subcanal, Cupo Cupo, Actividad Actividad, Agencia Agencia, Linea Linea, " +
                    "Territorio Territorio, Clientes.latitud latitud, Clientes.longitud longitud, condicionpago condicionpago, Barrio Barrio, " +
                    "Nombres Nombres, Apellidos Apellidos, Zonaventas Zonaventas, email email, Potencial Potencial, Valor Valor, " +
                    "Necesidad Necesidad, ciudad2 ciudad2, Cupo_disponible Cupo_disponible, cw.latitud latitudW, cw.longitud longitudW, tp.nombre Segmento, ruta1 ruta1, ruta2 ruta2," +
                    "(" + queryDiasMora + ") AS diasMora " +
                    "FROM Clientes " +
                    "LEFT JOIN coordenadaswaze cw ON Clientes.codigo = cw.codigo " +
                    "LEFT JOIN Tipologia tp ON Clientes.Actividad = tp.codigo " +
                    "WHERE fechavecto IS NULL " +
                    "GROUP BY Clientes.Codigo " +
                    "ORDER BY FechaVecto ASC";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                do {

                    ClienteSincronizado cliente = new ClienteSincronizado();

                    cliente.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    cliente.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                    cliente.razonSocial = cursor.getString(cursor.getColumnIndex("Razonsocial"));
                    cliente.nit = cursor.getString(cursor.getColumnIndex("Nit"));
                    cliente.direccion = cursor.getString(cursor.getColumnIndex("Direccion"));
                    cliente.ciudad = cursor.getString(cursor.getColumnIndex("Ciudad"));
                    cliente.telefono = cursor.getString(cursor.getColumnIndex("Telefono"));
                    cliente.canal = cursor.getString(cursor.getColumnIndex("Canal"));
                    cliente.subcanal = cursor.getString(cursor.getColumnIndex("Subcanal"));
                    cliente.cupo = cursor.getDouble(cursor.getColumnIndex("Cupo"));
                    cliente.actividad = cursor.getString(cursor.getColumnIndex("Actividad"));
                    cliente.agencia = cursor.getString(cursor.getColumnIndex("Agencia"));
                    cliente.linea = cursor.getString(cursor.getColumnIndex("Linea"));
                    cliente.territorio = cursor.getString(cursor.getColumnIndex("Territorio"));
                    cliente.latitud = cursor.getDouble(cursor.getColumnIndex("latitud"));
                    cliente.longitud = cursor.getDouble(cursor.getColumnIndex("longitud"));
                    cliente.condicionPago = cursor.getString(cursor.getColumnIndex("condicionpago"));
                    cliente.barrio = cursor.getString(cursor.getColumnIndex("Barrio"));
                    cliente.nombres = cursor.getString(cursor.getColumnIndex("Nombres"));
                    cliente.apellidos = cursor.getString(cursor.getColumnIndex("Apellidos"));
                    cliente.zonaVentas = cursor.getString(cursor.getColumnIndex("Zonaventas"));
                    cliente.email = cursor.getString(cursor.getColumnIndex("email"));
                    cliente.diasMora = cursor.getInt(cursor.getColumnIndex("diasMora"));
                    if (cliente.diasMora <= 0) {
                        cliente.diasMora = 0;
                    }
                    cliente.potencial = cursor.getString(cursor.getColumnIndex("Potencial"));
                    cliente.valor = cursor.getString(cursor.getColumnIndex("Valor"));
                    cliente.necesidad = cursor.getString(cursor.getColumnIndex("Necesidad"));
                    cliente.ciudad2 = cursor.getString(cursor.getColumnIndex("ciudad2"));
                    cliente.cupoDisponible = cursor.getDouble(cursor.getColumnIndex("Cupo_disponible"));
                    cliente.latitudW = cursor.getDouble(cursor.getColumnIndex("latitudW"));
                    cliente.longitudW = cursor.getDouble(cursor.getColumnIndex("longitudW"));
                    cliente.segmento = cursor.getString(cursor.getColumnIndex("Segmento"));
                    cliente.ruta1 = cursor.getString(cursor.getColumnIndex("ruta1"));
                    cliente.ruta2 = cursor.getString(cursor.getColumnIndex("ruta2"));
                    cliente.email = cursor.getString(cursor.getColumnIndex("email"));
                    cliente.NumeroRuta = Main.listaClientes.size() + 1;

                    // SE AGREGA LA LOGICA DE CARTERA POR CLIENTE
                    cliente.totalCartera = Utilidades.separarMilesSinDecimal(String.valueOf(DataBaseBO.obtenerCarteraCliente(cliente.codigo, context)), context);


                    Main.listaClientes.add(cliente);

                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("Consulta Clientes Sincronizados", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

    }

    public static boolean bannerUsu(String empresa, Context context) {

        boolean respuesta = false;
        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {
            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT u.empresa FROM usuario u  WHERE u.empresa = 'AGSC'  or 'AGCO'";
            db.execSQL(query);

            dbTemp.execSQL(query);

            respuesta = true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "bannereliminado-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
        return respuesta;
    }

    public static String cargarEmpresa(Context context) {
        String empresa = "";
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT empresa  FROM usuario ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                empresa = cursor.getString(cursor.getColumnIndex("empresa"));
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return empresa;
    }

    public static boolean ExisteDocumento(List<String> param2, Context context) {

        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }
        String empresa = "";
        boolean respuesta = false;
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT  docto_Financiero FROM (SELECT docto_Financiero FROM recaudos  WHERE  docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') UNION ALL SELECT docto_Financiero FROM recaudosPen WHERE  docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "')) T";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                empresa = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                if (!empresa.equals("")) {
                    respuesta = true;
                } else {
                    respuesta = false;
                }
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return respuesta;
    }


    public static String cargarRazonSocial(Context context) {
        String descripcion = "";
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT descripcion  FROM razonsocial ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return descripcion;
    }


    public static String cargarNroRecibo(String parametro, String param, String param2, String param3, String param4, String param5, String param6, Context context) {
        String nro_Recibo = "";
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT nro_Recibo  FROM recaudos where operacion_Cme ='" + parametro + "' and cod_Cliente = '" + param + "' and via_Pago = '" + param2 + "' or via_Pago = '" + param3 + "' or via_Pago = '" + param4 + "' or via_Pago = '" + param5 + "' or via_Pago = '" + param6 + "'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                nro_Recibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }
        return nro_Recibo;
    }

    public static List<FacturasRealizadas> cargarFacturasRealizadasDif(String param, Context context) {

        List<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT r.clase_Documento,r.sociedad,r.cod_Cliente,r.cod_Vendedor,r.referencia,r.fecha_Documento,r.fecha_Consignacion, " +
                    "r.Fecha_recibo,r.valor_Documento,r.moneda,SUM(r.valor_Pagado) AS valor_Pagado,r.valor_Consignado,r.cuenta_Bancaria,r.moneda_Consig, " +
                    "r.NCF_Comprobante_Fiscal,r.docto_Financiero,r.nro_Recibo,r.observaciones,r.via_Pago,r.usuario,r.operacion_Cme,r.idPago,r.sincronizado, " +
                    "r.banco,r.Numero_de_cheque,r.Nombre_del_propietario,r.Estado From recaudosRealizados r " +
                    "LEFT JOIN Solicitudes_Anulacion s ON s.nro_recibo = r.nro_Recibo " +
                    "LEFT JOIN Solicitudes_Anulaciones an ON an.nro_recibo = r.nro_Recibo " +
                    "WHERE r.cod_Cliente = '" + param + "' AND (s.nro_recibo is null AND an.nro_recibo is null)  /**AND Estado is NULL or Estado!=1**/  GROUP BY r.nro_Recibo  ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));

                    listaFacturasRealizadas.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasRealizadas;
    }

    public static List<FacturasRealizadas> cargarFacturasRealizadas(Context context) {

        List<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT r.clase_Documento,r.sociedad,r.Documento,r.cod_Cliente,r.cod_Vendedor,r.referencia,r.fecha_Documento," +
                    "r.fecha_Consignacion,r.valor_Documento,r.moneda,SUM(r.valor_Pagado) AS valor_Pagado,SUM(DISTINCT r.valor_Consignado) AS valor_Consignado," +
                    "r.cuenta_Bancaria,r.moneda_Consig,r.NCF_Comprobante_Fiscal,r.docto_Financiero,r.nro_Recibo,r.observaciones,r.via_Pago," +
                    "r.usuario,r.operacion_Cme,r.idPago,r.sincronizado,r.banco,r.Numero_de_cheque,r.Nombre_del_propietario,r.Estado " +
                    "From recaudosRealizados r " +
                    "LEFT JOIN Solicitudes_Anulaciones s ON s.nro_recibo = r.nro_Recibo " +
                    "WHERE s.nro_recibo is null " +
                    "GROUP BY r.nro_Recibo";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.documentoFactura = cursor.getString(cursor.getColumnIndex("Documento"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));

                    listaFacturasRealizadas.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasRealizadas;
    }


    public static boolean eliminarFotoID(List<Facturas> param2, Context context) {

        String str = "";
        for (Facturas fruit : param2) {
            str += "\'" + fruit.idenFoto + "\',";
        }

        boolean resultado = false;

        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "DELETE FROM fotos WHERE Iden_Foto IN('" + str.substring(1, str.length() - 2) + "')";

            db.execSQL(query);

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
        return resultado;
    }

    public static boolean eliminarFotoIDFac(List<String> param2, Context context) {

        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        boolean resultado = false;

        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM fotos WHERE Iden_Foto IN('" + str.substring(1, str.length() - 2) + "')");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
        return resultado;
    }

    public static boolean eliminarFoto(List<String> param2, Context context) {

        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        boolean resultado = false;

        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM fotos WHERE Idfoto IN(" + str + "'')");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
        return resultado;
    }

    /**
     * public static Fotos buscar(String id) {
     * Fotos foto = null;
     * <p>
     * SQLiteDatabase db = null;
     * try {
     * <p>
     * dbFile = new File(Utilidades.dirApp(), "Temp.db");
     * db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
     * <p>
     * String query = "SELECT * From fotos where = '" + id + "' ";
     * <p>
     * Cursor cursor = db.rawQuery(query, null);
     * if (cursor.moveToFirst()) {
     * do {
     * <p>
     * foto = new Fotos(id, cursor.getString(1),
     * cursor.getString(2), cursor.getString(3));
     * <p>
     * } while (cursor.moveToNext());
     * <p>
     * }
     * cursor.close();
     * <p>
     * } catch (Exception e) {
     * mensaje = e.getMessage();
     * <p>
     * } finally {
     * <p>
     * if (db != null)
     * db.close();
     * <p>
     * }
     * <p>
     * <p>
     * return foto;
     * }
     **/

    public static List<String> listarString(Context context) {
        List<String> listaFotos = new ArrayList<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT * From fotos ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    listaFotos.add("ID: " + cursor.getString(0));

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaFotos;
    }

    public static List<Fotos> cargarFotos(Context context) {

        List<Fotos> listaFotos = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT * From fotos ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Fotos fotos = new Fotos();

                    listaFotos.add(fotos);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaFotos;
    }

    public static List<FacturasRealizadas> cargarFacturasRealizadasCompletaInformes(String numeroRecibo, Context context) {

        List<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT rr.clase_Documento,rr.sociedad,rr.Documento,rr.cod_Cliente,rr.cod_Vendedor,rr.referencia,rr.fecha_Documento,rr.fecha_Consignacion," +
                    "rr.valor_Documento,rr.moneda,SUM(rr.valor_Pagado)AS valor_Pagado,SUM(DISTINCT rr.valor_Consignado) AS valor_Consignado,rr.cuenta_Bancaria," +
                    "rr.moneda_Consig,rr.NCF_Comprobante_Fiscal,rr.docto_Financiero,rr.nro_Recibo,rr.observaciones,rr.via_Pago,tp.denominacion," +
                    "rr.usuario,rr.operacion_Cme,rr.idPago,rr.sincronizado,rr.banco,rr.Numero_de_cheque,rr.Nombre_del_propietario,rr.Estado From recaudosRealizados rr inner join tiposdocumentos tp  on tp.documento = rr.clase_Documento  WHERE rr.nro_Recibo ='" + numeroRecibo + "' GROUP BY docto_Financiero ORDER BY rr.valor_Documento DESC ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.documentoFactura = cursor.getString(cursor.getColumnIndex("Documento"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.denominacion = cursor.getString(cursor.getColumnIndex("denominacion"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));


                    listaFacturasRealizadas.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasRealizadas;
    }

    public static List<FacturasRealizadas> cargarFacturasRealizadasCompleta(String numeroRecibo, Context context) {

        List<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago,denominacion," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,Estado From recaudosRealizados inner join tiposdocumentos  on tiposdocumentos.documento = recaudosRealizados.clase_Documento  WHERE nro_Recibo ='" + numeroRecibo + "'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.denominacion = cursor.getString(cursor.getColumnIndex("denominacion"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));


                    listaFacturasRealizadas.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasRealizadas;
    }

    public static String cargarEmpresaRazonSocial(Context context) {
        String empresa = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT empresa,descripcion FROM razonsocial r ";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                empresa = cursor.getString(cursor.getColumnIndex("descripcion"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return empresa;
    }

    public static List<Pendientes> cargarFacturasPendientesCompletaPorid(String numeroRecibo, Context context) {

        List<Pendientes> listaFacturasPendientes = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,Estado,Iden_Foto,consecutivoid, consecutivo, Fecha_recibo " +
                    " From recaudosPendientes WHERE idPago ='" + numeroRecibo + "' and (via_Pago='6' or via_Pago='O')  ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));
                    pendientes.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));
                    pendientes.consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));
                    pendientes.fechaRecibo = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));


                    listaFacturasPendientes.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasPendientes;
    }

    public static List<Pendientes> cargarFacturasPendientesCompletaPoridMultiples(List<String> numeroRecibo, Context context) {

        List<Pendientes> listaFacturasPendientes = new ArrayList<>();
        String str = "";
        for (String fruit : numeroRecibo) {
            str += "\'" + fruit + "\',";
        }


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,observacionesmotivo,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,Estado,Iden_Foto,ifnull(consecutivoid,0) as consecutivoid, ifnull(consecutivo,0) as consecutivo, Fecha_recibo " +
                    " From recaudosPendientes WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') AND via_Pago='6' or via_Pago='O'";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.observacionesMotivo = cursor.getString(cursor.getColumnIndex("observacionesmotivo"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));
                    pendientes.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));
                    pendientes.consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));
                    pendientes.fechaRecibo = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));


                    listaFacturasPendientes.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasPendientes;
    }


    public static List<Pendientes> cargarFacturasPendientesCompleta(String numeroRecibo, Context context) {

        List<Pendientes> listaFacturasPendientes = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,Estado,consecutivoid From recaudosPendientes WHERE nro_Recibo ='" + numeroRecibo + "' ";

            System.out.println(query);

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));


                    listaFacturasPendientes.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasPendientes;
    }


    public static List<Pendientes> cargarFacturasPendientesAnulacion(Context context) {

        List<Pendientes> listaFacturasPendientes = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor," +
                    "valor_Documento,docto_Financiero,nro_Recibo,nro_anulacion,fecha," +
                    "codigo_causal From RecaudosAnulados GROUP BY nro_Recibo  ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_documento"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.numeroAnulacion = cursor.getString(cursor.getColumnIndex("nro_anulacion"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha"));
                    pendientes.codigoCausal = cursor.getString(cursor.getColumnIndex("codigo_causal"));

                    listaFacturasPendientes.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasPendientes;
    }

    public static List<Pendientes> cargarSumasPendientesViaPago(Context context) {

        List<Pendientes> listaFacturasPendientes = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT SUM(valor_Pagado) AS valor_Pagado From recaudosPendientes WHERE via_Pago = 'A' or via_Pago = 'B' GROUP BY nro_Recibo  ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Pendientes pendientes = new Pendientes();

                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));


                    listaFacturasPendientes.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasPendientes;
    }

    public static List<Pendientes> cargarFacturasPendientes(Context context) {

        List<Pendientes> listaFacturasPendientes = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT *,sum(valor_Final) as valor_Pagado " +
                           "FROM(SELECT c.clase_Documento,c.sociedad,c.cod_Cliente,c.cod_Vendedor,c.referencia, " +
                           "c.fecha_Documento,c.Fecha_recibo,c.valor_Documento,c.moneda,c.valor_Consignado AS valor_Final, " +
                           "c.valor_Consignado,c.saldo_favor,c.cuenta_Bancaria,c.moneda_Consig,c.NCF_Comprobante_Fiscal, " +
                           "c.docto_Financiero,c.nro_Recibo,c.observaciones,c.via_Pago,c.usuario,c.operacion_Cme,c.idPago, " +
                           "c.sincronizado,c.banco,c.Numero_de_cheque,Nombre,c.Estado,c.consecutivoid, c.observacionesmotivo, c.consecutivo as consecutivo " +
                           "From recaudosPendientes c INNER JOIN clientes cli ON c.cod_cliente = cli.codigo " +
                           "LEFT JOIN recaudosRealizados r ON r.nro_Recibo = c.nro_Recibo " +
                           "LEFT JOIN recaudosAnulados ra ON c.nro_Recibo = ra.nro_Recibo " +
                           "WHERE (c.via_Pago = 'A' or c.via_Pago = 'B') AND r.nro_Recibo IS NULL AND ra.nro_Recibo IS NULL GROUP BY c.idPago)" +
                           "GROUP BY nro_Recibo";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.consignadoM = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.observacionesMotivo = cursor.getString(cursor.getColumnIndex("observacionesmotivo"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));
                    pendientes.consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));


                    listaFacturasPendientes.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasPendientes;
    }

    public static Bitmap buscarImagen(String id, Context context) {
        SQLiteDatabase dbTemp = null;
        Bitmap bitmap = null;

        try {

            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String sql = "SELECT * FROM fotos WHERE idFoto ='" + id + "'";
            Cursor cursor = dbTemp.rawQuery(sql, new String[]{});

            if (cursor.moveToFirst()) {


                YuvImage yuvimage = new YuvImage(cursor.getBlob(1), ImageFormat.NV21, 100, 100, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, 100, 100), 100, baos);
                byte[] jdata = baos.toByteArray();

                // Convert to Bitmap
                bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);


                /**byte[] blob = cursor.getBlob(1);
                 int blobLength = (int) cursor.getBlob(1).length;
                 ByteArrayInputStream bais = new ByteArrayInputStream(blob);
                 bitmap = BitmapFactory.decodeByteArray(blob,0,blobLength);
                 bitmap = BitmapFactory.decodeStream(bais);**/

            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "buscarImagen-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }

        return bitmap;
    }

    public static void guardarImagen(String idFoto, Bitmap bitmap, List<String> param2, String empresa, String idenFotos, Context context) {

        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        try {
            // tamaño del baos depende del tamaño de tus imagenes en promedio
            SQLiteDatabase dbTemp = null;

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);
            byte[] b = bos.toByteArray();

            // aqui tenemos el byte[] con el imagen comprimido, ahora lo guardemos en SQLite
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String sql = "INSERT INTO fotos (IdFoto, Nombre, IdPago,Empresa,Iden_Foto) VALUES(?,?,?,?,?)";
            SQLiteStatement insert = dbTemp.compileStatement(sql);

            for (int i = 0; i < param2.size(); i++) {

                insert.clearBindings();
                insert.bindString(1, idFoto);
                insert.bindBlob(2, b);
                insert.bindString(3, param2.get(i));
                insert.bindString(4, empresa);
                insert.bindString(5, idenFotos);
                insert.executeInsert();

            }


            dbTemp.close();
        } catch (Exception e) {
            Log.println(Log.ERROR, "Error guardarImagenes", e.getMessage());
        }
    }

    public static void guardarImagenMutilples(String idFoto, Bitmap bitmap, List<String> param2, String empresa, String idenFotos, String metodoPago, Context context) {

        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        try {
            // tamaño del baos depende del tamaño de tus imagenes en promedio
            SQLiteDatabase dbTemp = null;

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);
            byte[] b = bos.toByteArray();

            // aqui tenemos el byte[] con el imagen comprimido, ahora lo guardemos en SQLite
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String sql = "INSERT INTO fotos (IdFoto, Nombre, IdPago,Empresa,Iden_Foto) VALUES(?,?,?,?,?)";
            SQLiteStatement insert = dbTemp.compileStatement(sql);

            for (int i = 0; i < param2.size(); i++) {

                if (DataBaseBO.ValidarMetodoDePagoPorRecibo(param2.get(i), metodoPago, context)) {
                    insert.clearBindings();
                    insert.bindString(1, idFoto);
                    insert.bindBlob(2, b);
                    insert.bindString(3, param2.get(i));
                    insert.bindString(4, empresa);
                    insert.bindString(5, idenFotos);
                    insert.executeInsert();
                }
            }


            dbTemp.close();
        } catch (Exception e) {
            Log.println(Log.ERROR, "Error guardarImagenes", e.getMessage());
        }
    }

    public static void guardarImagenes(String idFoto, Bitmap bitmap, List<String> param2, String empresa, String idenFotos, Context context) {

        String str = "";
        for (String fruit : param2) {
            str = fruit;
        }
        try {
            // tamaño del baos depende del tamaño de tus imagenes en promedio
            SQLiteDatabase dbTemp = null;

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);
            bos.flush();
            bos.close();
            byte[] b = bos.toByteArray();

            // aqui tenemos el byte[] con el imagen comprimido, ahora lo guardemos en SQLite
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String sql = "INSERT INTO fotos (IdFoto, Nombre, IdPago,Empresa,Iden_Foto) VALUES(?,?,?,?,?)";
            SQLiteStatement insert = dbTemp.compileStatement(sql);

            for (int i = 0; i < param2.size(); i++) {

                insert.clearBindings();
                insert.bindString(1, idFoto);
                insert.bindBlob(2, b);
                insert.bindString(3, str);
                insert.bindString(4, empresa);
                insert.bindString(5, idenFotos);
                insert.executeInsert();

            }


            dbTemp.close();
        } catch (Exception e) {
            Log.println(Log.ERROR, "Error guardarImagenes", e.getMessage());
        }

    }


    public static boolean guardarFotos(String idFoto, String nombreFoto, String idPagoFoto, String empresa, Context context) {
        boolean resultado = false;
        SQLiteDatabase dbTemp = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues fotos = new ContentValues();
            fotos.put("idFoto", idFoto);
            fotos.put("Nombre", nombreFoto);
            fotos.put("idPago", idPagoFoto);
            fotos.put("Empresa", empresa);

            dbTemp.insertOrThrow("fotos", null, fotos);

            resultado = true;


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFotos-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean hayInformacionFotos(Context context) {

        mensaje = "";
        SQLiteDatabase db = null;
        boolean resultado = false;

        SQLiteDatabase dbTemp = null;


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT COUNT(*) FROM fotos";
            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {

                do {
                    int count = cursor.getInt(0);
                    if (count == 0) {
                        resultado = false;
                    } else if (count >= 1) {
                        resultado = true;
                    }

                } while (cursor.moveToNext());
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "VerificarRecaudos-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;


    }


    public static boolean hayInformacionRecaudos(Context context) {

        mensaje = "";
        SQLiteDatabase db = null;
        boolean resultado = false;

        SQLiteDatabase dbTemp = null;


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT COUNT(*) FROM recaudos ";
            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {

                do {
                    int count = cursor.getInt(0);
                    if (count == 0) {
                        resultado = false;
                    } else if (count >= 1) {
                        resultado = true;
                    }

                } while (cursor.moveToNext());
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "VerificarRecaudos-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;


    }

    public static boolean hayInformacionPendientes(Context context) {

        mensaje = "";
        SQLiteDatabase db = null;
        boolean resultado = false;

        SQLiteDatabase dbTemp = null;


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT COUNT(*) FROM recaudosPen ";
            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {

                do {
                    int count = cursor.getInt(0);
                    if (count == 0) {
                        resultado = false;
                    } else if (count >= 1) {
                        resultado = true;
                    }

                } while (cursor.moveToNext());
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "VerificarPendientes-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;


    }


    public static String cargarCodigo(Context context) {
        String nombre = "";
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT usuario  FROM usuario ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                nombre = cursor.getString(cursor.getColumnIndex("usuario"));
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }
        return nombre;
    }

    public static String cargarUsuarioApp(Context context) {
        String nombre = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT Nombre  FROM usuario ";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                nombre = cursor.getString(cursor.getColumnIndex("Nombre"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return nombre;
    }

    public static String cargarTipoUsuarioApp(Context context) {
        String nombre = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT TipoUsuario  FROM usuario ";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                nombre = cursor.getString(cursor.getColumnIndex("TipoUsuario"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return nombre;
    }

    public static String cargarNegocioConsecutivoUpdate(Context context) {
        String negocio = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbtemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT negocio  FROM consecutivorecibos ORDER BY negocio DESC ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                negocio = cursor.getString(cursor.getColumnIndex("negocio"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return negocio;
    }

    public static String cargarNegocioConsecutivo(Context context) {
        String negocio = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbtemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT negocio  FROM consecutivorecibos ORDER BY negocio DESC ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                negocio = cursor.getString(cursor.getColumnIndex("negocio"));

            }
            cursor.close();

            if (negocio.equals("")) {
                String query1 = "SELECT negocio  FROM consecutivorecibos ORDER BY negocio DESC ";

                Cursor cursor1 = db.rawQuery(query1, null);
                if (cursor.moveToFirst()) {
                    negocio = cursor.getString(cursor.getColumnIndex("negocio"));

                }
                cursor.close();
            }


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return negocio;
    }

    public static String cargarNegocioConsecutivoId(Context context) {
        String negocio = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT negocio  FROM consecutivoID ORDER BY negocio DESC ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                negocio = cursor.getString(cursor.getColumnIndex("negocio"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("consecutivoID", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return negocio;
    }

    public static String cargarNegocioConsecutivoPaquete(Context context) {
        String negocio = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT negocio  FROM consecutivopaquete ORDER BY negocio DESC ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                negocio = cursor.getString(cursor.getColumnIndex("negocio"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return negocio;
    }

    public static String cargarVendedorConsecutivoUpdate(Context context) {
        String vendedor = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbtemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT vendedor  FROM consecutivorecibos  ORDER BY vendedor DESC ";

            Cursor cursor = dbtemp.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return vendedor;
    }

    public static String cargarDocumentoFinanciero(List<String> parametro, Context context) {
        String docu = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }


        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            dbtemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT Documento_Financiero  FROM cartera WHERE documento  IN ('" + str.substring(1, str.length() - 2) + "')";

            Cursor cursor = dbtemp.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                docu = cursor.getString(cursor.getColumnIndex("vendedor"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return docu;
    }

    public static String cargarVendedorConsecutivo(Context context) {
        String vendedor = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbtemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT vendedor  FROM consecutivorecibos  ORDER BY vendedor DESC ";

            Cursor cursor = dbtemp.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));

            }
            cursor.close();

            if (vendedor.equals("")) {
                String query1 = "SELECT vendedor  FROM consecutivorecibos ORDER BY vendedor DESC ";

                Cursor cursor1 = db.rawQuery(query1, null);
                if (cursor1.moveToFirst()) {
                    vendedor = cursor1.getString(cursor1.getColumnIndex("vendedor"));

                }
                cursor1.close();
            }

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return vendedor;
    }

    public static String cargarVendedorConsecutivoId(Context context) {
        String vendedor = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT vendedor  FROM consecutivoID ORDER BY vendedor DESC ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("consecutivoID", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return vendedor;
    }

    public static String cargarVendedorConsecutivoPaquete(Context context) {
        String vendedor = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT vendedor  FROM consecutivopaquete ORDER BY vendedor DESC ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return vendedor;
    }


    public static String cargarConsecutivoUpdate(String parametro, Context context) {
        String consecutivo = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbtemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT nro_Recibo  FROM recaudos WHERE idPago = '" + parametro + "' ";

            Cursor cursor = dbtemp.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                consecutivo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return consecutivo;
    }


    public static String cargarFechaMaxReciboRealizados(String parametro, Context context) {
        String consecutivo = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            dbtemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT MAX(Fecha_recibo)As Fecha_recibo  FROM recaudosRealizados WHERE nro_Recibo = '" + parametro + "' ";

            Cursor cursor = dbtemp.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                consecutivo = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));

            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return consecutivo;
    }


    public static String cargarConsecutivo(Context context) {
        String consecutivo = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbtemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT consecutivo  FROM consecutivorecibos ORDER BY  consecutivo DESC ";

            Cursor cursor = dbtemp.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                consecutivo = cursor.getString(cursor.getColumnIndex("consecutivo"));

            }
            cursor.close();

            if (consecutivo.equals("")) {
                String query1 = "SELECT consecutivo  FROM consecutivorecibos ORDER BY  consecutivo DESC  ";

                Cursor cursor1 = db.rawQuery(query1, null);
                if (cursor1.moveToFirst()) {
                    consecutivo = cursor1.getString(cursor1.getColumnIndex("consecutivo"));

                }
                cursor1.close();

            }

            if (!consecutivo.isEmpty())
                consecutivo = validarUltimoConsecutivo(consecutivo, context);

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return consecutivo;
    }

    public static String cargarConsecutivoId(Context context) {
        String consecutivo = "";
        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");

            if(dbFile.exists())
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT consecutivo  FROM consecutivoID ORDER BY  consecutivo DESC  ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                consecutivo = cursor.getString(cursor.getColumnIndex("consecutivo"));

            }
            cursor.close();

            if (consecutivo.isEmpty()) {
                dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


                String query1 = "SELECT consecutivo  FROM consecutivoID ORDER BY  consecutivo DESC  ";

                Cursor cursor1 = db.rawQuery(query1, null);
                if (cursor1.moveToFirst()) {
                    consecutivo = cursor1.getString(cursor1.getColumnIndex("consecutivo"));

                }
                cursor1.close();
            }

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("consecutivoID", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return consecutivo;
    }


    public static String cargarConsecutivoIdDataBase(Context context) {
        String consecutivo = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT consecutivo  FROM consecutivoID ORDER BY  consecutivo DESC  ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                consecutivo = cursor.getString(cursor.getColumnIndex("consecutivo"));

            }
            cursor.close();

            if (consecutivo.isEmpty()) {
                dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


                String query1 = "SELECT consecutivo  FROM consecutivoID ORDER BY  consecutivo DESC  ";

                Cursor cursor1 = db.rawQuery(query1, null);
                if (cursor1.moveToFirst()) {
                    consecutivo = cursor1.getString(cursor1.getColumnIndex("consecutivo"));

                }
                cursor1.close();
            }

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("consecutivoID", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return consecutivo;
    }


    public static String cargarConsecutivoPaquete(Context context) {
        String consecutivo = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query1 = "SELECT consecutivo  FROM consecutivopaquete ORDER BY  consecutivo DESC  ";

            Cursor cursor1 = db.rawQuery(query1, null);
            if (cursor1.moveToFirst()) {
                consecutivo = cursor1.getString(cursor1.getColumnIndex("consecutivo"));

            }
            cursor1.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return consecutivo;
    }

    @NonNull
    public static List<Facturas> cargarFacParTotal(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "')) T GROUP BY docto_Financiero  ORDER BY CASE docto_Financiero WHEN docto_Financiero THEN docto_Financiero End ASC";

                System.out.println("fffffffffffffff " + query);

                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {
// consulta para verificar si existe
                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }


    public static List<Facturas> cargarFacParTotalHechas(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudos  WHERE nro_Recibo = '" + param + "' and idPago IN ('" + str.substring(1, str.length() - 2) + "') UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudosPen WHERE nro_Recibo = '" + param + "' and idPago IN ('" + str.substring(1, str.length() - 2) + "')) T GROUP BY docto_Financiero  ORDER BY CASE docto_Financiero WHEN docto_Financiero THEN docto_Financiero End ASC";

                System.out.println("fffffffffffffff " + query);

                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {
// consulta para verificar si existe
                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarFacParTotalEfec(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado  FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B'  UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B' ) T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarFacParTotalEfecCantidadFacturas(String param, List<String> param2, List<String> ides, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : ides) {
            str1 += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado  FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'A'  UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'A' ) T GROUP BY docto_Financiero  ORDER BY CASE docto_Financiero WHEN docto_Financiero THEN docto_Financiero End ASC";

                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarFacParTotalEfecTarjeta(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado  FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'O'  UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'O' ) T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarFacParTotalEfecTransferencia(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado  FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '6'  UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '6' ) T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }


    public static List<Facturas> cargarFacParTotalCheq(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado,idPago FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A' UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarFacParTotalCheqTarjeta(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado,idPago FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A' UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarFacParTotalTransf(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado,idPago FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B' UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarFacParTotalTransfCheque(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado,idPago FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A' UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarFacParTotalTarjeta(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado,idPago FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '6' UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '6') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }


    public static List<Facturas> cargarFacParTotalBitcoin(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,SUM(valor_Pagado) AS valor_Pagado,idPago FROM(SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'O' UNION ALL SELECT fecha_Documento,docto_Financiero,valor_Pagado,idPago FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'O') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }


    public static List<Facturas> cargarIDEfectivo(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM(SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A' UNION ALL SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                        cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                        cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                        cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                        cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }


    public static List<Facturas> cargarListaEfectivo(String param, List<String> param2, List<String> ids, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : ids) {
            str1 += "\'" + fruit + "\',";
        }


        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM(SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "')and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'A'" +
                        " UNION ALL SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'A') T GROUP BY idPago";

                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                        cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                        cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                        cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                        cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarListaCheque(String param, List<String> param2, List<String> ids, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : ids) {
            str1 += "\'" + fruit + "\',";
        }


        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM(SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "')and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'B'" +
                        " UNION ALL SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'B') T GROUP BY idPago";

                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                        cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                        cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                        cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                        cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarListaTransferencia(String param, List<String> param2, List<String> ids, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : ids) {
            str1 += "\'" + fruit + "\',";
        }


        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM(SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "')and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = '6'" +
                        " UNION ALL SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = '6') T GROUP BY idPago";

                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                        cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                        cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                        cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                        cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarListaTarjeta(String param, List<String> param2, List<String> ids, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : ids) {
            str1 += "\'" + fruit + "\',";
        }


        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM(SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "')and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'O'" +
                        " UNION ALL SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'O') T GROUP BY idPago";

                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                        cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                        cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                        cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                        cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarIDCheq(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,clase_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Numero_de_cheque,Iden_Foto FROM(SELECT fecha_Documento,clase_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Numero_de_cheque,Iden_Foto FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B' UNION ALL SELECT fecha_Documento,clase_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Numero_de_cheque,Iden_Foto FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.tipoDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                        cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                        cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                        cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                        cartera.numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                        cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarIDTransfe(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM(SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '6' UNION ALL SELECT fecha_Documento,docto_Financiero,idPago,referencia,banco,cuenta_Bancaria,Iden_Foto FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '6') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                        cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                        cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                        cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                        cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarIDTarjeta(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,referencia,idPago,Nombre_del_propietario,cuenta_Bancaria FROM(SELECT fecha_Documento,docto_Financiero,referencia,idPago,Nombre_del_propietario,cuenta_Bancaria FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'O' UNION ALL SELECT fecha_Documento,docto_Financiero,referencia,idPago,Nombre_del_propietario,cuenta_Bancaria FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'O') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                        cartera.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                        cartera.nombrePropietario = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarIDBitcoin(String param, List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  fecha_Documento,docto_Financiero,idPago FROM(SELECT fecha_Documento,docto_Financiero,idPago FROM recaudos  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '4' UNION ALL SELECT fecha_Documento,docto_Financiero,idPago FROM recaudosPen WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '4') T GROUP BY docto_Financiero  ORDER BY CASE fecha_Documento WHEN fecha_Documento THEN fecha_Documento End ASC";


                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }


    @NonNull
    public static List<Facturas> cargaridFotos(List<String> param2, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }

        if (param2 != null) {

            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT  Iden_Foto FROM(SELECT foto_buzon as Iden_Foto FROM recaudos  WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') UNION ALL SELECT Iden_Foto FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "')) T ";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {

                        Facturas cartera = new Facturas();
                        cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                        listaCartera.add(cartera);


                    } while (cursor.moveToNext());

                }
                cursor.close();

            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    public static List<Facturas> cargarFacParTotalPen(String param, List<String> param2, Vector<String> listaItems, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }


        if (param2 != null) {


            try {

                dbFile = new File(Utilidades.dirApp(context), "Temp.db");
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


                if (param2.size() >= 3) {

                    String query = "SELECT docto_Financiero,SUM(valor_Pagado) AS valor_Pagado FROM recaudosPen  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') GROUP BY docto_Financiero  ORDER BY CASE valor_Pagado WHEN valor_Pagado > 0 THEN valor_Pagado End ASC";

                    Cursor cursor = db.rawQuery(query, null);
                    if (cursor.moveToFirst()) {
                        do {

                            Facturas cartera = new Facturas();


                            cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                            cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

                            listaCartera.add(cartera);
                            listaItems.add(cartera.documento + "-" + cartera.valor);

                        } while (cursor.moveToNext());


                    }
                    cursor.close();

                }

                if (param2.size() <= 2) {

                    String query = "SELECT docto_Financiero,SUM(valor_Pagado) AS valor_Pagado FROM recaudosPen  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') GROUP BY docto_Financiero  ORDER BY valor_Pagado ASC";

                    Cursor cursor = db.rawQuery(query, null);
                    if (cursor.moveToFirst()) {
                        do {

                            Facturas cartera = new Facturas();


                            cartera.documento = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                            cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

                            listaCartera.add(cartera);
                            listaItems.add(cartera.documento + "-" + cartera.valor);

                        } while (cursor.moveToNext());


                    }
                    cursor.close();


                }


            } catch (Exception e) {
                mensaje = e.getMessage();
                Log.e("obtenerCarteraCliente", mensaje, e);

            } finally {

                if (db != null)
                    db.close();

            }

        }


        return listaCartera;
    }

    @NonNull
    public static List<Pendientes> cargarFacParTotalPendientes(String param, List<String> param2, Vector<String> listaItems, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Pendientes> listaCarteraPendientes = new ArrayList<>();


        String str = "";
        for (String fruit : param2) {
            str += "\'" + fruit + "\',";
        }


        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT docto_Financiero,SUM(valor_Pagado) AS valor_Pagado FROM recaudosPendientes  WHERE nro_Recibo = '" + param + "' and docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') GROUP BY docto_Financiero ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Pendientes cartera = new Pendientes();


                    cartera.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    cartera.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

                    listaCarteraPendientes.add(cartera);
                    listaItems.add(cartera.doctoFinanciero + "-" + cartera.montoPendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCarteraPendientes;
    }

    public static List<Fotos> cargarIdPagoFoto(Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Fotos> listaFotos = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT Idfoto FROM fotos";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Fotos fotos = new Fotos();


                    fotos.idFoto = cursor.getString(cursor.getColumnIndex("Idfoto"));
                    listaFotos.add(fotos);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaFotos;
    }

    public static List<Facturas> cargarIdPago(String param, String param2, Vector<String> listaItems, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT idPago FROM recaudos  WHERE nro_Recibo = '" + param + "' and via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);
                    listaItems.add(cartera.idPago);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarIdPagoOG(String param, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT DISTINCT idPago FROM( SELECT r.idPago FROM recaudos r WHERE r.nro_Recibo = '" + param + "' and (r.via_Pago = 'A' or r.via_Pago = 'B' or r.via_Pago = 'O'  or r.via_Pago = '6') " +
                    " UNION ALL SELECT  re.idPago FROM recaudosPen re WHERE re.nro_Recibo = '" + param + "' and (re.via_Pago = 'A' or re.via_Pago = 'B' or re.via_Pago = 'O'  or re.via_Pago = '6'))T";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarIdPagoOGRecaudosRealizados(String param, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT DISTINCT idPago FROM( SELECT r.idPago FROM recaudos r WHERE r.nro_Recibo = '" + param + "' and (r.via_Pago = 'A' or r.via_Pago = 'B' or r.via_Pago = 'O'  or r.via_Pago = '6')" +
                    " UNION ALL SELECT  re.idPago FROM recaudosRealizados re WHERE re.nro_Recibo = '" + param + "' and (re.via_Pago = 'A' or re.via_Pago = 'B' or re.via_Pago = 'O'  or re.via_Pago = '6'))T";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarIdPagoOGRecaudosPendientes(String param, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT DISTINCT idPago FROM( SELECT r.idPago FROM recaudos r WHERE r.nro_Recibo = '" + param + "' and r.via_Pago = 'A' or r.via_Pago = 'B' or r.via_Pago = 'O'  or r.via_Pago = '6'\n" +
                    " UNION ALL SELECT  re.idPago FROM recaudosPendientes re WHERE re.nro_Recibo = '" + param + "' and (re.via_Pago = 'A' or re.via_Pago = 'B' or re.via_Pago = 'O'  or re.via_Pago = '6'))T";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarIdPagoOGRecaudosPendientesDatabase(String param, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT DISTINCT idPago FROM( SELECT r.idPago FROM recaudos r WHERE r.nro_Recibo = '" + param + "' and r.via_Pago = 'A' or r.via_Pago = 'B' or r.via_Pago = 'O'  or r.via_Pago = '6'\n" +
                    " UNION ALL SELECT  re.idPago FROM recaudosPendientes re WHERE re.nro_Recibo = '" + param + "' and (re.via_Pago = 'A' or re.via_Pago = 'B' or re.via_Pago = 'O'  or re.via_Pago = '6'))T";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarIdPagoOGRealizados(String param, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT r.idPago FROM recaudosRealizados r WHERE r.nro_Recibo = '" + param + "'";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static List<Facturas> cargarIdPagoOGPendientes(String param, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT idPago FROM recaudosPen  WHERE nro_Recibo = '" + param + "' and via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static List<Facturas> cargarIdPagoOGPendientesRecaudosRealizados(String param, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT idPago FROM recaudosRealizados  WHERE nro_Recibo = '" + param + "' and (via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6')";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarIdPagoOGPendientesRecaudos(String param, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT idPago FROM recaudosPen  WHERE nro_Recibo = '" + param + "' and (via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6')";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarIdPagoOGPendientesRecaudosDataBase(String param, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<Facturas> listaCartera = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT idPago FROM recaudosPen  WHERE nro_Recibo = '" + param + "' and (via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6')";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();


                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static double TotalValoresNegativos(String numeroRecibo, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT SUM(DISTINCT rr.valor_Documento)AS valor_documento,rr.docto_Financiero,rr.nro_Recibo,rr.via_Pago,tp.denominacion From recaudosRealizados rr inner join tiposdocumentos tp  on tp.documento = rr.clase_Documento  WHERE rr.nro_Recibo ='" + numeroRecibo + "' AND rr.valor_documento < 0";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_documento"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalValoresNegativosRecaudosPendientes(String numeroRecibo, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;


        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT SUM(DISTINCT rr.valor_Documento)AS valor_documento,rr.docto_Financiero,rr.nro_Recibo,rr.via_Pago,tp.denominacion From recaudosPendientes rr inner join tiposdocumentos tp  on tp.documento = rr.clase_Documento  WHERE rr.nro_Recibo ='" + numeroRecibo + "' AND rr.valor_documento < 0";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_documento"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoEfectivo(List<String> codigo, String parametro, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;

        String str = "";
        for (String fruit : codigo) {
            str += "\'" + fruit + "\',";
        }


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT cod_Cliente,SUM(DISTINCT valor_Consignado)AS valor_Consignado FROM recaudos  WHERE docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') AND via_Pago='" + parametro + "' GROUP BY idPago HAVING idPago ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoEfectivoDatabase(List<String> codigo, String parametro, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;

        String str = "";
        for (String fruit : codigo) {
            str += "\'" + fruit + "\',";
        }


        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT cod_Cliente,SUM(DISTINCT valor_Consignado)AS valor_Consignado FROM recaudos  WHERE docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') AND via_Pago='" + parametro + "' GROUP BY idPago HAVING idPago ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoAnticipoRR(String str, String parametro, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,SUM(valor_Pagado) AS valor_Pagado FROM recaudos  WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') AND via_Pago='" + parametro + "' ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoPendientesEfectivo(List<String> id, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        String str = "";
        for (String fruit : id) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,sum(valor_Pagado) as valor_Pagado FROM (SELECT cod_Cliente,valor_consignado AS valor_Pagado FROM recaudosPendientes  WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') AND via_Pago = 'A' group by idPago)";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double TotalFormasPagoPendientesEfectivoMultiples(List<String> id, List<String> id2, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        String str = "";
        for (String fruit : id) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : id2) {
            str1 += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,valor_consignado AS valor_Pagado FROM recaudosPendientes  WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') AND idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') AND via_Pago = 'A' group by idPago";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    valor += cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                }
                while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double TotalFormasPagoPendientesChequesData(List<String> id, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        String str = "";
        for (String fruit : id) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,sum(valor_Pagado) AS valor_Pagado FROM (SELECT cod_Cliente,valor_consignado AS valor_Pagado FROM recaudosPendientes  WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') AND via_Pago = 'B' group by idPago)";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoPendientesTransferenciaMultiples(List<String> id, List<String> id2, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        String str = "";
        for (String fruit : id) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : id2) {
            str1 += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,valor_consignado AS valor_Pagado FROM recaudosPendientes  WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') AND idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') AND via_Pago = '6' GROUP BY idPago";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    valor += cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoPendientesChequesDataMultiples(List<String> id, List<String> id2, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        String str = "";
        for (String fruit : id) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : id2) {
            str1 += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,valor_consignado AS valor_Pagado FROM recaudosPendientes  WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') AND idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') AND via_Pago = 'B' GROUP BY idPago";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    valor += cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double TotalFormasPagoPendientes(List<String> id, String numeroRecibo, List<String> id2, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        String str = "";
        for (String fruit : id) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : id2) {
            str1 += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,sum(valor_Pagado) AS valor_Pagado FROM  (SELECT cod_Cliente,valor_Consignado AS valor_Pagado FROM recaudosPendientes  WHERE nro_Recibo ='" + numeroRecibo + "' AND  idPago IN ('" + str.substring(1, str.length() - 2) + "') AND NOT idPago IN ('" + str1.substring(1, str1.length() - 2) + "') group by idPago)";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor += cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double TotalFormasPagoPendientesMultiples(List<String> id, List<String> numeroRecibo, List<String> id2, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        String str = "";
        for (String fruit : id) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : numeroRecibo) {
            str1 += "\'" + fruit + "\',";
        }

        String str2 = "";
        for (String fruit : id2) {
            str2 += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,SUM(valor_Pagado) AS valor_Pagado FROM recaudosPendientes  WHERE nro_Recibo IN ('" + str1.substring(1, str1.length() - 2) + "') AND  idPago IN ('" + str.substring(1, str.length() - 2) + "') AND NOT idPago IN ('" + str2.substring(1, str2.length() - 2) + "') ";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double DiferenciaFormasPagoPendientes(List<String> id, List<Double> valorPagado, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        String str = "";
        for (String fruit : id) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (Double fruit : valorPagado) {
            str1 += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,valor_Pagado FROM recaudos  WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') AND valor_Pagado IN ('" + str1.substring(1, str1.length() - 2) + "') AND via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoAnticipoRROGPendientes(String str, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  cod_Cliente,SUM(valor_Pagado) AS valor_Pagado FROM(SELECT cod_Cliente,SUM(valor_Pagado) AS valor_Pagado FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6'\n" +
                    " UNION ALL SELECT cod_Cliente,SUM(valor_Pagado) AS valor_Pagado FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6')T";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double SumaViasPagoPendientes(Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT SUM(valor_Pagado) AS valor_Pagado FROM recaudosPendientes  WHERE  via_Pago = 'A' or via_Pago = 'B'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double TotalFormasPagoAnticipoRROG(String str, String nroRecibo, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,SUM(valor_Pagado) AS valor_Pagado,via_Pago" +
                    " FROM(SELECT cod_Cliente,valor_consignado AS valor_Pagado,via_Pago" +
                    " FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') AND " +
                    " nro_recibo = '" + nroRecibo + "' " +
                    "and (via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6') GROUP BY idPago " +
                    " UNION ALL SELECT cod_Cliente,valor_consignado AS valor_Pagado,via_Pago " +
                    "FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') AND " +
                    " nro_recibo = '" + nroRecibo + "' " +
                    "and (via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6') GROUP BY idPAgo)T";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoAnticipoRROGRealizados(String str, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  cod_Cliente,SUM( valor_Pagado) AS valor_Pagado,via_Pago FROM recaudosRealizados WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "and via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6'\n";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double TDiferenciaSumadelTotalDelDOcumento(String str, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  cod_Cliente,SUM( valor_Documento) AS valor_Documento,via_Pago" +
                    " FROM(SELECT cod_Cliente,SUM(  valor_Documento) AS valor_Documento,via_Pago" +
                    " FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "and via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6'\n" +
                    " UNION ALL SELECT cod_Cliente,SUM( valor_Documento) AS valor_Documento,via_Pago " +
                    "FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "and via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6')T";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotaDifeAfav(String str, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  cod_Cliente,saldo_favor,via_Pago" +
                    " FROM(SELECT cod_Cliente,saldo_favor,via_Pago" +
                    " FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "and via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6' and saldo_favor !=0 \n" +
                    " UNION ALL SELECT cod_Cliente,saldo_favor,via_Pago " +
                    "FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "and saldo_favor != 0 and via_Pago = 'A' or via_Pago = 'B' or via_Pago = 'O'  or via_Pago = '6')T ORDER BY saldo_favor ASC";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double SaldoAfavor(String str, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,(saldo_favor) as saldo_favor,via_Pago" +
                    " FROM recaudosRealizados WHERE nro_Recibo = '" + str + "' and saldo_favor !=0 " +
                    " ORDER BY saldo_favor ASC";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double SaldoAfavorRecaudosPendientes(String str, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,(saldo_favor) as saldo_favor,via_Pago" +
                    " FROM recaudosPendientes WHERE nro_Recibo = '" + str + "' and saldo_favor !=0 " +
                    " ORDER BY saldo_favor ASC";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalValorConsignado(String str, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  idPago,SUM( valor_Consignado) as valor_Consignado FROM(SELECT DISTINCT idPago,valor_Consignado " +
                    " FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') \n" +
                    " UNION ALL SELECT DISTINCT idPago,valor_Consignado " +
                    "FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') )T ";


            System.out.println("DISTINCT    " + query);

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalValorConsignadoRealizados(String str, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  idPago,SUM( valor_Consignado) as valor_Consignado FROM(SELECT DISTINCT idPago,valor_Consignado " +
                    " FROM recaudosRealizados WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') ) ";


            System.out.println("DISTINCT    " + query);

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoAnticipoRROGEFec(String str, Context context) {
        double valor = 0;
        String viaPago = "";
        String idPago = "";
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  valor_Consignado,via_Pago,idPago FROM(SELECT  valor_Consignado,via_Pago,idPago FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A'" +
                    "UNION ALL SELECT  valor_Consignado,via_Pago,idPago FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A')T GROUP BY via_Pago";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                idPago = cursor.getString(cursor.getColumnIndex("idPago"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoAnticipoRROGEFecCantidadFac(String str, List<String> idPagos, Context context) {
        double valor = 0;
        String viaPago = "";
        String idPago = "";
        SQLiteDatabase db = null;
        double precioTotal = 0;

        String str1 = "";
        for (String fruit : idPagos) {
            str1 += "\'" + fruit + "\',";
        }


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  DISTINCT valor_Consignado,via_Pago,idPago FROM(SELECT DISTINCT valor_Consignado,via_Pago,idPago FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'A'" +
                    "UNION ALL SELECT DISTINCT valor_Consignado,via_Pago,idPago FROM recaudosPen WHERE idPago IN ('" + str + "') and idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and via_Pago = 'A')T GROUP BY valor_Consignado ";

            System.out.println("simposo " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                idPago = cursor.getString(cursor.getColumnIndex("idPago"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double countMetodEfec(String str, Context context) {

        mensaje = "";
        SQLiteDatabase db = null;
        double resultado = 0;

        SQLiteDatabase dbTemp = null;


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT count(DISTINCT idPago) FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A'";
            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {

                do {
                    int count = cursor.getInt(0);
                    if (count == 1) {
                        resultado = 1;
                    } else if (count >= 2) {
                        resultado = 2;
                    }

                } while (cursor.moveToNext());
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "VerificarRecaudos-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;


    }

    public static int cuantasEfectivo(List<String> params, Context context) {
        int totalFacturas = 0;
        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        String str = "";
        for (String fruit : params) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT count(DISTINCT nro_Recibo) FROM recaudosPendientes WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'A'";
            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {
                do {
                    int count = cursor.getInt(0);
                    totalFacturas = count;
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e(TAG, "conteoFacturas-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

        }

        return totalFacturas;
    }

    public static int cuantasCheque(List<String> params, Context context) {
        int totalFacturas = 0;
        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        String str = "";
        for (String fruit : params) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT count(DISTINCT nro_Recibo) FROM recaudosPendientes WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B'";
            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {
                do {
                    int count = cursor.getInt(0);
                    totalFacturas = count;
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e(TAG, "conteoFacturas-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

        }

        return totalFacturas;
    }


    public static double countMetodCheq(String str, Context context) {

        mensaje = "";
        SQLiteDatabase db = null;
        double resultado = 0;

        SQLiteDatabase dbTemp = null;


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT count(DISTINCT idPago) FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B'";
            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {

                do {
                    int count = cursor.getInt(0);
                    if (count == 1) {
                        resultado = 1;
                    } else if (count >= 2) {
                        resultado = 2;
                    }

                } while (cursor.moveToNext());
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "VerificarRecaudos-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;


    }

    public static double countMetodTarjeta(String str, Context context) {

        mensaje = "";
        SQLiteDatabase db = null;
        double resultado = 0;

        SQLiteDatabase dbTemp = null;


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT count(DISTINCT idPago) FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'O'";
            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {

                do {
                    int count = cursor.getInt(0);
                    if (count == 1) {
                        resultado = 1;
                    } else if (count >= 2) {
                        resultado = 2;
                    }

                } while (cursor.moveToNext());
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "VerificarRecaudos-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;


    }

    public static double countMetodTransferencia(String str, Context context) {

        mensaje = "";
        SQLiteDatabase db = null;
        double resultado = 0;

        SQLiteDatabase dbTemp = null;


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT count(DISTINCT idPago) FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '6'";
            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {

                do {
                    int count = cursor.getInt(0);
                    if (count == 1) {
                        resultado = 1;
                    } else if (count >= 2) {
                        resultado = 2;
                    }

                } while (cursor.moveToNext());
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "VerificarRecaudos-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;


    }

    public static double TotalFormasPagoAnticipoRROGCheq(String str, Context context) {
        double valor = 0;
        String viaPago = "";
        String idPago = "";
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  valor_Consignado,via_Pago,idPago FROM(SELECT  valor_Consignado,via_Pago,idPago FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B'" +
                    "UNION ALL SELECT  valor_Consignado,via_Pago,idPago FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'B')T GROUP BY via_Pago";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                idPago = cursor.getString(cursor.getColumnIndex("idPago"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoAnticipoRROGTarje(String str, Context context) {
        double valor = 0;
        String viaPago = "";
        String idPago = "";
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  valor_Consignado,via_Pago,idPago FROM(SELECT  valor_Consignado,via_Pago,idPago FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'O'" +
                    "UNION ALL SELECT  valor_Consignado,via_Pago,idPago FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = 'O')T GROUP BY via_Pago";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                idPago = cursor.getString(cursor.getColumnIndex("idPago"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoAnticipoRROGTrasnf(String str, Context context) {
        double valor = 0;
        String viaPago = "";
        String idPago = "";
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  valor_Consignado,via_Pago,idPago FROM(SELECT  valor_Consignado,via_Pago,idPago FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '6'" +
                    "UNION ALL SELECT  valor_Consignado,via_Pago,idPago FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '6')T GROUP BY via_Pago";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                idPago = cursor.getString(cursor.getColumnIndex("idPago"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static double TotalFormasPagoAnticipoRROGBit(String str, Context context) {
        double valor = 0;
        String viaPago = "";
        String idPago = "";
        SQLiteDatabase db = null;
        double precioTotal = 0;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT  valor_Consignado,via_Pago,idPago FROM(SELECT  valor_Consignado,via_Pago,idPago FROM recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '4'" +
                    "UNION ALL SELECT  valor_Consignado,via_Pago,idPago FROM recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') and via_Pago = '4')T GROUP BY via_Pago";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                idPago = cursor.getString(cursor.getColumnIndex("idPago"));

            }
            cursor.close();
        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double TotalFormasPagoAnticipo(List<Facturas> codigo, String parametro, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;
        double precioTotal = 0;
        String str = "";
        for (Facturas fruit : codigo) {
            str += "\'" + fruit.idPago + "\',";
            precioTotal += fruit.getValor();
        }


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT cod_Cliente,SUM(valor_Consignado)AS valor_Consignado FROM recaudos  WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') AND via_Pago='" + parametro + "' GROUP BY idPago ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }


    public static double TotalFormasPago(List<String> codigo, String parametro, Context context) {
        double valor = 0;
        SQLiteDatabase db = null;

        String str = "";
        for (String fruit : codigo) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT cod_Cliente,SUM(valor_Consignado)AS valor_Consignado FROM recaudos  WHERE docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') AND via_Pago='" + parametro + "' GROUP BY docto_Financiero ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return valor;
    }

    public static List<Cartera> cargarCarteraTipoParametroBusqueda(String parametro, String param, Vector<String> listaItems, Context context) {


        List<Cartera> listaCartera = new ArrayList<>();
        String parametro2 = "";

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT c.documento,c.tipo,c.fechavecto,c.saldo,c.diasmora,c.documento_Financiero," +
                    "c.vendedor  FROM cartera c INNER JOIN clientes cli ON c.cliente = cli.codigo " +
                    "LEFT JOIN RecaudosPendientes r ON r.docto_Financiero = c.Documento_Financiero " +
                    " AND r.docto_Financiero IS NULL WHERE c.cliente = '" + param + "' and c.tipo LIKE '" + '%' + parametro + '%' + "'";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Cartera cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
                    cartera.vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));
                    cartera.concepto = cursor.getString(cursor.getColumnIndex("tipo"));
                    cartera.fechaVencto = cursor.getString(cursor.getColumnIndex("FechaVecto"));
                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("saldo"));
                    cartera.dias = cursor.getInt(cursor.getColumnIndex("diasmora"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("Documento_Financiero"));
                    cartera.facturaSeleccionadaGestion = false;
                    listaCartera.add(cartera);
                    listaItems.add(cartera.documento + "-" + cartera.concepto + "-" + cartera.fechaVencto + "-" +
                            cartera.saldo + "-" + cartera.dias + "-");


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Cartera> cargarCarteraParametroBusqueda(String parametro, String param, Vector<String> listaItems, Context context) {


        List<Cartera> listaCartera = new ArrayList<>();
        String parametro2 = "";

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT c.documento,c.tipo,c.fechavecto,c.saldo,c.diasmora,c.documento_Financiero," +
                    "c.vendedor FROM cartera c INNER JOIN clientes cli ON c.cliente = cli.codigo " +
                    "LEFT JOIN RecaudosPendientes r ON r.docto_Financiero = c.Documento_Financiero " +
                    "WHERE c.cliente = '" + param + "' AND r.docto_Financiero IS NULL  GROUP BY c.documento HAVING c.documento " +
                    "LIKE '" + '%' + parametro + '%' + "' OR c.tipo LIKE '" + '%' + parametro + '%' + "' " +
                    "OR c.saldo LIKE '" + '%' + parametro + '%' + "' ";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Cartera cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
                    cartera.vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));
                    cartera.concepto = cursor.getString(cursor.getColumnIndex("tipo"));
                    cartera.fechaVencto = cursor.getString(cursor.getColumnIndex("FechaVecto"));
                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("saldo"));
                    cartera.dias = cursor.getInt(cursor.getColumnIndex("diasmora"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("Documento_Financiero"));
                    cartera.facturaSeleccionadaGestion = false;
                    listaCartera.add(cartera);
                    listaItems.add(cartera.documento + "-" + cartera.concepto + "-" + cartera.fechaVencto + "-" +
                            cartera.saldo + "-" + cartera.dias + "-");


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static Vector<MotivosAbono> cargarMotivosAbono(Vector<String> listaItems, Context context) {


        Vector<MotivosAbono> listamotivos = new Vector<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT DISTINCT  Nombre  FROM motivosabono";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    MotivosAbono motivos = new MotivosAbono();

                    motivos.Nombre = cursor.getString(cursor.getColumnIndex("Nombre"));

                    listamotivos.add(motivos);
                    listaItems.add(motivos.Nombre);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("lista motivos", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listamotivos;
    }

    public static String cargarCodigobanco(String parametro, Context context) {
        String codigo = "";
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT Codigo FROM bancos where Nombre ='" + parametro + "' ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                codigo = cursor.getString(cursor.getColumnIndex("Codigo"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return codigo;
    }

    public static Vector<FacturasRealizadas> cargarFechaCarteraAnulados(String initialDate, String finalDate, String param, Context context) {

        Vector<FacturasRealizadas> listaCartera = new Vector<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,fecha,SUM(valor_Pagado) AS valor_Pagado,nro_Recibo FROM recaudosAnulados  WHERE fecha_Consignacion BETWEEN '" + initialDate + "' AND '" + finalDate + "' AND cod_Cliente = '" + param + "'  GROUP BY nro_Recibo";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static Vector<FacturasRealizadas> cargarFechaCarteraRealizados(String initialDate, String finalDate, Context context) {


        Vector<FacturasRealizadas> listaCartera = new Vector<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,cod_Cliente,fecha_Consignacion,SUM(valor_Pagado) AS valor_Pagado,SUM(DISTINCT valor_Consignado) AS valor_Consignado,nro_Recibo FROM recaudosRealizados  WHERE fecha_Consignacion BETWEEN '" + initialDate + "' AND '" + finalDate + "' GROUP BY nro_Recibo";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static Vector<Cartera> cargarFechaCartera(String parametro, Vector<String> listaItems, String initialDate, String finalDate, Context context) {

        Vector<Cartera> listaCartera = new Vector<>();
        initialDate = initialDate.replace("-", "");
        finalDate = finalDate.replace("-", "");
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT  c.documento,c.tipo,c.fechavecto,c.saldo,c.diasmora,c.documento_Financiero," +
                    "c.vendedor  FROM cartera c INNER JOIN clientes cli ON c.cliente = cli.codigo " +
                    "LEFT JOIN RecaudosPendientes r ON r.docto_Financiero = c.Documento_Financiero " +
                    "WHERE c.cliente = '" + parametro + "' AND c.fechavecto BETWEEN '" + initialDate + "' AND '" + finalDate + "' " +
                    " AND r.docto_Financiero IS NULL ORDER BY c.fechavecto ASC";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Cartera cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
                    cartera.vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));
                    cartera.concepto = cursor.getString(cursor.getColumnIndex("tipo"));
                    cartera.fechaVencto = cursor.getString(cursor.getColumnIndex("FechaVecto"));
                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("saldo"));
                    cartera.dias = cursor.getInt(cursor.getColumnIndex("diasmora"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("Documento_Financiero"));
                    cartera.facturaSeleccionadaGestion = false;
                    listaCartera.add(cartera);
                    listaItems.add(cartera.documento + "-" + cartera.concepto + "-" + cartera.fechaVencto + "-" +
                            cartera.saldo + "-" + cartera.dias + "-");

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static Vector<Cartera> cargarTipoCartera(Vector<String> listaItems, Context context) {

        Vector<Cartera> listaCartera = new Vector<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT DISTINCT  c.tipo  FROM cartera c INNER JOIN clientes cli ON c.cliente = cli.codigo WHERE c.cliente = cli.codigo ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Cartera cartera = new Cartera();

                    cartera.concepto = cursor.getString(cursor.getColumnIndex("tipo"));

                    listaCartera.add(cartera);
                    listaItems.add(cartera.concepto);


                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static Vector<CuentasBanco> cargarCuentasBancos(Vector<String> listaItems, String parametro, Context context) {

        Vector<CuentasBanco> listaCuentasBancos = new Vector<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT NumeroCuenta  FROM CuentasBancarias  WHERE CodigoBanco = '" + parametro + "'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    CuentasBanco bancos = new CuentasBanco();

                    bancos.NombreCuenta = cursor.getString(cursor.getColumnIndex("NumeroCuenta"));

                    listaCuentasBancos.add(bancos);
                    listaItems.add(bancos.NombreCuenta);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtener bancos ", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCuentasBancos;
    }

    public static Vector<CuentasBanco> cargarCuentasBancosSolo(Vector<String> listaItems, Context context) {

        Vector<CuentasBanco> listaCuentasBancos = new Vector<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT b.NumeroCuenta  FROM CuentasBancarias b ORDER BY b.NumeroCuenta DESC";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    CuentasBanco bancos = new CuentasBanco();

                    bancos.NombreCuenta = cursor.getString(cursor.getColumnIndex("NumeroCuenta"));

                    listaCuentasBancos.add(bancos);
                    listaItems.add(bancos.NombreCuenta);


                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtener bancos ", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCuentasBancos;
    }

    public static Vector<Bancos> cargarMotivosAnulacion(Vector<String> listaItems, Context context) {

        Vector<Bancos> listaBancos = new Vector<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT b.descripcion,b.codigo_causal  FROM MotivosAnulacion b ORDER BY b.codigo_causal ASC";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Bancos bancos = new Bancos();

                    bancos.nombre_Banco = cursor.getString(cursor.getColumnIndex("descripcion"));
                    bancos.codigo_Banco = cursor.getString(cursor.getColumnIndex("codigo_causal"));

                    listaBancos.add(bancos);
                    listaItems.add(bancos.nombre_Banco);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtener bancos ", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaBancos;
    }

    public static Vector<Bancos> cargarTipoBancos(Vector<String> listaItems, Context context) {

        Vector<Bancos> listaBancos = new Vector<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT b.Nombre  FROM bancos b ORDER BY b.Nombre DESC";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Bancos bancos = new Bancos();

                    bancos.nombre_Banco = cursor.getString(cursor.getColumnIndex("Nombre"));

                    listaBancos.add(bancos);
                    listaItems.add(bancos.nombre_Banco);


                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtener bancos ", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaBancos;
    }

    public static List<FacturasRealizadas> cargarClientesBusquedaFacRealizadas(String parametro, Context context) {

        List<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<>();
        String parametro2 = "";

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,SUM(valor_Pagado) AS valor_Pagado,valor_Consignado,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,Estado " +
                    "From recaudosRealizados WHERE cod_Cliente LIKE '" + '%' + parametro + '%' + "'  OR nro_Recibo LIKE '" + '%' + parametro + '%' + "' GROUP BY nro_Recibo ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));
                    listaFacturasRealizadas.add(pendientes);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaFacturasRealizadas;
    }


    public static List<FacturasRealizadas> cargarClientesBusquedaFacAnulados(String parametro, Context context) {

        List<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<>();
        String parametro2 = "";

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,SUM(valor_Pagado) AS valor_Pagado,valor_Consignado,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,Estado " +
                    "From recaudosAnulados WHERE cod_Cliente LIKE '" + '%' + parametro + '%' + "'  OR nro_Recibo LIKE '" + '%' + parametro + '%' + "' GROUP BY nro_Recibo ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.status = cursor.getString(cursor.getColumnIndex("Estado"));
                    listaFacturasRealizadas.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaFacturasRealizadas;
    }


    public static List<ClienteSincronizado> cargarClientesBusqueda(String parametro, Vector<String> listaItems, Context context) {

        List<ClienteSincronizado> listaClientes = new ArrayList<>();
        String parametro2 = "";

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT codigo,nombre,razonsocial,nit,email,telefono,a.Balance,a.ProBalance,c.cupo,c.condicionpago FROM clientes  c LEFT JOIN AccountManagement a ON c.codigo = a.cliente  WHERE nombre LIKE '" + '%' + parametro + '%' + "' OR codigo LIKE '" + '%' + parametro + '%' + "'  ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    ClienteSincronizado cliente = new ClienteSincronizado();

                    cliente.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    cliente.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                    cliente.razonSocial = cursor.getString(cursor.getColumnIndex("Razonsocial"));
                    cliente.nit = cursor.getString(cursor.getColumnIndex("Nit"));
                    cliente.email = cursor.getString(cursor.getColumnIndex("email"));
                    cliente.telefono = cursor.getString(cursor.getColumnIndex("Telefono"));
                    cliente.carteraVencida = cursor.getDouble(cursor.getColumnIndex("Balance"));
                    cliente.porcentajeCarteraVenciada = cursor.getFloat(cursor.getColumnIndex("ProBalance"));
                    cliente.cupo = cursor.getDouble(cursor.getColumnIndex("cupo"));
                    cliente.condicionPago = cursor.getString(cursor.getColumnIndex("condicionpago"));
                    listaClientes.add(cliente);
                    listaItems.add(cliente.codigo + "-" + cliente.nombre + "-" + cliente.razonSocial + "-" +
                            cliente.nit + "-" + cliente.email + "-" + cliente.ciudad + "-" + cliente.telefono);


                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaClientes;
    }

    public static String cargarMoneda(Context context) {
        String descripcion = "";
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT descripcion,empresa  FROM moneda ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return descripcion;
    }

    public static List<String> cargarViaFormasdePago(List<String> documento, String numeroRecibo, Context context) {

        List<String> listaviaPagos = new ArrayList<>();

        String documents = "";
        String str = "";
        for (String fruit : documento) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT r.via_Pago  FROM recaudosPendientes r  WHERE r.nro_Recibo =  '" + numeroRecibo + "' AND r.via_Pago IN ('" + str.substring(1, str.length() - 2) + "') GROUP BY r.via_Pago ORDER BY r.via_Pago DESC ";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {

                    String viapagos = new String();
                    viapagos = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    listaviaPagos.add(viapagos);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtener via", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaviaPagos;
    }

    public static String cargarViaFormasdePagoEmpresa(String busqeda, Context context) {
        String via = "";

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT LetraInicial  FROM MetodosDePago  WHERE LetraInicial = '" + busqeda + "'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                via = cursor.getString(cursor.getColumnIndex("LetraInicial"));

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtener via", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return via;
    }


    public static List<ClienteSincronizado> cargarClientes(Vector<String> listaItems, Context context) {

        List<ClienteSincronizado> listaClientes = new ArrayList<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT codigo,nombre,razonsocial,nit,email,telefono,condicionpago,vendedor1,a.Balance,a.ProBalance,c.cupo  FROM clientes c LEFT JOIN AccountManagement a ON c.codigo = a.cliente ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    ClienteSincronizado cliente = new ClienteSincronizado();

                    cliente.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    cliente.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                    cliente.razonSocial = cursor.getString(cursor.getColumnIndex("Razonsocial"));
                    cliente.nit = cursor.getString(cursor.getColumnIndex("Nit"));
                    cliente.email = cursor.getString(cursor.getColumnIndex("email"));
                    cliente.telefono = cursor.getString(cursor.getColumnIndex("Telefono"));
                    cliente.condicionPago = cursor.getString(cursor.getColumnIndex("condicionpago"));
                    cliente.Vendedor1 = cursor.getString(cursor.getColumnIndex("Vendedor1"));
                    cliente.carteraVencida = cursor.getDouble(cursor.getColumnIndex("Balance"));
                    cliente.porcentajeCarteraVenciada = cursor.getFloat(cursor.getColumnIndex("ProBalance"));
                    cliente.Vendedor1 = cursor.getString(cursor.getColumnIndex("Vendedor1"));
                    cliente.cupo = cursor.getDouble(cursor.getColumnIndex("cupo"));
                    listaClientes.add(cliente);
                    listaItems.add(cliente.codigo + "-" + cliente.nombre + "-" + cliente.razonSocial + "-" +
                            cliente.nit + "-" + cliente.email + "-" + cliente.ciudad + "-" + cliente.telefono + "-" + cliente.condicionPago + "-" + cliente.Vendedor1);


                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaClientes;
    }

    public static List<Cartera> cargarCartera(Vector<String> listaItems, Context context) {


        List<Cartera> listaCartera = new ArrayList<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT documento,tipo,fechavecto,saldo,diasmora  FROM cartera";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Cartera cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("documento"));
                    cartera.concepto = cursor.getString(cursor.getColumnIndex("tipo"));
                    cartera.fechaVencto = cursor.getString(cursor.getColumnIndex("fechavecto"));
                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("saldo"));
                    cartera.dias = cursor.getInt(cursor.getColumnIndex("diasmora"));
                    listaCartera.add(cartera);
                    listaItems.add(cartera.documento + "-" + cartera.concepto + "-" + cartera.fechaVencto + "-" +
                            cartera.saldo + "-" + cartera.dias + "-");

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static List<Cartera> cargarCarteraCompleta(String parametro, Vector<String> listaItems, Context context) {

        int idUnico = 0;
        List<Cartera> listaCartera = new ArrayList<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

//            String query = "SELECT c.documento,c.tipo,c.fechavecto,c.saldo,c.diasmora,c.documento_Financiero," +
//                    "c.vendedor  FROM cartera c INNER JOIN clientes cli ON c.cliente = cli.codigo  " +
//                    "LEFT JOIN RecaudosPendientes r ON r.docto_Financiero = c.Documento_Financiero " +
//                    "LEFT JOIN RecaudosAnulados ra ON ra.nro_recibo = r.nro_Recibo " +
//                    "WHERE c.cliente = '" + parametro + "'  AND (r.docto_Financiero IS NULL OR ra.docto_financiero IS NOT NULL) ORDER BY c.cliente DESC";

            String query = "SELECT " +
                    "c.documento, c.tipo, c.fechavecto, c.saldo, c.diasmora, " +
                    "c.documento_Financiero, c.vendedor, ra.nro_recibo, r.nro_Recibo " +
                    "FROM cartera c " +
                    "INNER JOIN clientes cli ON c.cliente = cli.codigo  " +
                    "LEFT JOIN RecaudosPendientes r ON r.docto_Financiero = c.Documento_Financiero " +
                    "LEFT JOIN RecaudosAnulados ra ON ra.nro_recibo = r.nro_Recibo " +
                    "WHERE c.cliente = '" + parametro + "' " +
                    "AND ( " +
                    "(r.docto_Financiero IS NULL) " +
                    "OR " +
                    "( " +
                    "r.docto_Financiero IS NOT NULL " +
                    "AND ra.docto_financiero IS NOT NULL " +
                    "AND NOT EXISTS ( " +
                    "SELECT 1 FROM RecaudosPendientes r2 " +
                    "LEFT JOIN RecaudosAnulados ra2 ON ra2.nro_recibo = r2.nro_Recibo " +
                    "WHERE r2.docto_Financiero = c.Documento_Financiero " +
                    "AND ra2.nro_recibo IS NULL ))) " +
                    "GROUP BY c.Documento_Financiero ORDER BY c.cliente DESC";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Cartera dias = new Cartera();
                    Cartera cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
                    cartera.vendedor = cursor.getString(cursor.getColumnIndex("vendedor"));
                    cartera.concepto = cursor.getString(cursor.getColumnIndex("tipo"));
                    cartera.fechaVencto = cursor.getString(cursor.getColumnIndex("FechaVecto"));
                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("saldo"));
                    cartera.dias = cursor.getInt(cursor.getColumnIndex("diasmora"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("Documento_Financiero"));
                    cartera.idUnicoCartera = 0;
                    listaCartera.add(cartera);
                    listaItems.add(cartera.documento + "-" + cartera.concepto + "-" + cartera.fechaVencto + "-" +
                            cartera.saldo + "-" + cartera.dias + "-" + cartera.idUnicoCartera + "-" + cartera.documentoFinanciero);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Cartera> cargarCarteraParametro(String parametro, String param, Vector<String> listaItems, Context context) {


        List<Cartera> listaCartera = new ArrayList<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT c.documento,c.tipo,c.fechavecto,c.saldo,c.diasmora  FROM cartera c INNER JOIN clientes cli ON c.cliente = cli.codigo  WHERE c.cliente = '" + param + "'ORDER BY  c.'" + parametro + "' DESC";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Cartera dias = new Cartera();
                    Cartera cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("documento"));
                    cartera.concepto = cursor.getString(cursor.getColumnIndex("tipo"));
                    cartera.fechaVencto = cursor.getString(cursor.getColumnIndex("fechavecto"));
                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("saldo"));
                    cartera.dias = cursor.getInt(cursor.getColumnIndex("diasmora"));
                    listaCartera.add(cartera);
                    listaItems.add(cartera.documento + "-" + cartera.concepto + "-" + cartera.fechaVencto + "-" +
                            cartera.saldo + "-" + cartera.dias + "-");

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarFacturasParametroReciboPendientes(String param, List<Facturas> idpago, Vector<String> listaItems, String consecutivo, Context context) {

        List<Facturas> listaCartera = new ArrayList<>();

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT c.cod_Cliente,c.clase_Documento," +
                    "c.valor_Consignado,c.via_Pago,c.idPago, banco, cuenta_Bancaria, Iden_Foto  FROM recaudosPen c WHERE  c.idPago IN ('" + str.substring(1, str.length() - 2) + "') AND c.cod_Cliente = '" + param + "' AND c.nro_recibo = '" + consecutivo + "' GROUP BY c.idPago ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                    listaCartera.add(cartera);
                    listaItems.add(cartera.codCliente + "-" + cartera.documento + "-" + cartera.consecutivo + "-"
                            + cartera.tipoDocumento + "-" +
                            cartera.valor + "-" + cartera.fecha + "-" + cartera.formaPago + "-" + cartera.idPago + "-");

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static List<Facturas> cargarFacturasParametroRecibo(String param, List<Facturas> idpago, Vector<String> listaItems, Context context) {

        List<Facturas> listaCartera = new ArrayList<>();

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT c.cod_Cliente,c.clase_Documento," +
                    "c.valor_Consignado,c.via_Pago,c.idPago  FROM recaudos c WHERE  c.idPago IN ('" + str.substring(1, str.length() - 2) + "') AND c.cod_Cliente = '" + param + "' GROUP BY c.idPago ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));

                    listaCartera.add(cartera);
                    listaItems.add(cartera.codCliente + "-" + cartera.documento + "-" + cartera.consecutivo + "-"
                            + cartera.tipoDocumento + "-" +
                            cartera.valor + "-" + cartera.fecha + "-" + cartera.formaPago + "-" + cartera.idPago + "-");

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Pendientes> cargarFacturasParametroPendientesEfectivo(List<String> parametro, Context context) {

        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,consecutivoid,consecutivo, observacionesmotivo, Fecha_recibo  " +
                    "FROM recaudosPendientes c WHERE  c.idPago IN ('" + str.substring(1, str.length() - 2) + "') AND c.via_Pago = 'A' " +
                    "order BY c.valor_Consignado DESC ";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.observacionesMotivo = cursor.getString(cursor.getColumnIndex("observacionesmotivo"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));
                    pendientes.consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));
                    pendientes.fechaRecibo = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Pendientes> cargarFacturasParametroPendientesEfectivoMultiples(List<String> parametro, Context context) {

        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


//            String query = "SELECT *,sum(valorFinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
//                    "valor_Documento,moneda,valor_Pagado,valor_Consignado as valorFinal,saldo_favor,cuenta_Bancaria," +
//                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
//                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,consecutivoid  " +
//                    "FROM recaudosPendientes c WHERE   c.nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') " +
//                    "AND c.via_Pago = 'A' GROUP BY idPAgo order BY c.valor_Consignado DESC) group by idPAgo ";

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,ifnull(consecutivoid,0) as consecutivoid, observacionesmotivo, ifnull(consecutivo,0) as consecutivo, Fecha_recibo  " +
                    "FROM recaudosPendientes c WHERE   c.nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "AND c.via_Pago = 'A' order BY c.valor_Consignado DESC";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.observacionesMotivo = cursor.getString(cursor.getColumnIndex("observacionesmotivo"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));
                    pendientes.consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));
                    pendientes.fechaRecibo = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static Double cargarFacturasParametroPendientesEfectivoMultiplesValorPendiente(List<String> parametro, Context context) {

        Double preciosPendientesMultiples = 0.0;

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT *,sum(valorFinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado as valorFinal,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,consecutivoid, observacionesmotivo  " +
                    "FROM recaudosPendientes c WHERE   c.nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "AND c.via_Pago = 'A' GROUP BY idPAgo order BY c.valor_Consignado DESC) group by idPAgo ";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    preciosPendientesMultiples += cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return preciosPendientesMultiples;
    }

    public static Double cargarFacturasParametroPendientesTransferenciaMultiplesValorPendiente(List<String> parametro, Context context) {

        Double preciosPendientesMultiples = 0.0;

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT *,sum(valorFinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado as valorFinal,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,consecutivoid  " +
                    "FROM recaudosPendientes c WHERE   c.nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "AND c.via_Pago = '6' GROUP BY idPAgo order BY c.valor_Consignado DESC) group by idPAgo ";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    preciosPendientesMultiples += cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return preciosPendientesMultiples;
    }

    public static List<Pendientes> cargarRazon(List<String> parametro, Context context) {

        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT Razonsocial FROM recaudosPendientes c INNER JOIN clientes cli ON c.cod_cliente = cli.codigo where cli.codigo  IN ('" + str.substring(1, str.length() - 2) + "') GROUP by c.cod_cliente";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Razonsocial"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static List<Pendientes> cargarFacturasParametroPendientesCheque(List<String> parametro, Context context) {


        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,consecutivoid, consecutivo, Fecha_recibo  " +
                    "FROM recaudosPendientes c WHERE  c.idPago IN ('" + str.substring(1, str.length() - 2) + "') AND c.via_Pago = 'B' order BY c.valor_Consignado DESC ";


            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));
                    pendientes.consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));
                    pendientes.fechaRecibo = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));

                    listaCartera.add(pendientes);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Pendientes> cargarFacturasParametroPendientesChequeMultiples(List<String> parametro, Context context) {

        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

//            String query = "SELECT *,sum(valorFinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
//                    "valor_Documento,moneda,valor_Pagado,valor_Consignado as valorFinal,saldo_favor,cuenta_Bancaria," +
//                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
//                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,consecutivoid  FROM recaudosPendientes c WHERE    c.nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') " +
//                    "AND c.via_Pago = 'B' GROUP BY idPago order BY c.valor_Consignado DESC) GROUP BY idPago";

            String query = "SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado as valor_Consignado,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,observacionesmotivo,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,ifnull(consecutivoid,0) as consecutivoid, ifnull(consecutivo,0) as consecutivo,Fecha_recibo  FROM recaudosPendientes c WHERE    c.nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "AND c.via_Pago = 'B' order BY c.valor_Consignado DESC";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.observacionesMotivo = cursor.getString(cursor.getColumnIndex("observacionesmotivo"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));
                    pendientes.consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));
                    pendientes.fechaRecibo = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static Double cargarFacturasParametroPendientesChequeMultiplesValorPendiente(List<String> parametro, Context context) {

        Double preciosPendientesMultiples = 0.0;

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT *,sum(valorFinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado as valorFinal,saldo_favor,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario,consecutivoid, consecutivo  FROM recaudosPendientes c WHERE    c.nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "AND c.via_Pago = 'B' GROUP BY idPago order BY c.valor_Consignado DESC) GROUP BY idPago";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    preciosPendientesMultiples += cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return preciosPendientesMultiples;
    }


    public static List<Pendientes> cargarFacturasParametroPendientesEnRecaudos(List<String> parametro, String numeroRecibo, Context context) {


        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT *,sum(valorFinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado,valor_Consignado as valorFinal,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario  " +
                    "FROM recaudos c WHERE nro_Recibo ='" + numeroRecibo + "' AND  c.idPago IN ('" + str.substring(1, str.length() - 2) + "') and saldo_favor !=0" +
                    " group by idPago order BY c.valor_Consignado DESC) group by idPago";


            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));

                    listaCartera.add(pendientes);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static List<Pendientes> cargarFacturasParametroPendientes(List<String> parametro, String numeroRecibo, List<String> parametro2, Context context) {

        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : parametro2) {
            str1 += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT *,SUM(valorFinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado AS valor_Pagado,valor_Consignado as valorFinal,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario, observacionesmotivo  " +
                    "FROM recaudosPendientes c WHERE nro_Recibo ='" + numeroRecibo + "' AND  c.idPago IN ('" + str.substring(1, str.length() - 2) + "')" +
                    " and c.idPago NOT IN ('" + str1.substring(1, str1.length() - 2) + "') and valor_Pagado != 0  group by idPago order BY c.valor_Consignado DESC) group by idPago";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.observacionesMotivo = cursor.getString(cursor.getColumnIndex("observacionesmotivo"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Pendientes> cargarFacturasParametroPendientesTemporal(List<String> parametro, Context context) {


        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT *,sum(valorfinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado AS valor_Pagado,valor_Consignado as valorFinal,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario  " +
                    "FROM recaudos c WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') " +
                    "and valor_Pagado != 0 group by idPago order BY c.valor_Consignado DESC) group by idPago";


            Cursor cursor = db.rawQuery(query, null);


            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.consignadoM = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));

                    listaCartera.add(pendientes);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static List<Pendientes> cargarFacturasParametroPendientesTemporalVarias(List<String> parametro, List<String> numeroRecibo, Context context) {


        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        String str1 = "";
        for (String fruit : numeroRecibo) {
            str1 += "\'" + fruit + "\',";
        }


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT *,SUM(valorFinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado AS valor_Pagado,valor_Consignado as valorFinal,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario " +
                    " FROM recaudos c WHERE nro_Recibo IN ('" + str1.substring(1, str1.length() - 2) + "')  group by idPago order BY c.valor_Consignado DESC) group by idPago";


            Cursor cursor = db.rawQuery(query, null);


            System.out.println("rrrr" + query);

            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.consignadoM = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));

                    listaCartera.add(pendientes);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static List<Pendientes> cargarFacturasParametroPendientesMultiples(List<String> parametro, List<String> numeroRecibo, List<String> parametro2, Context context) {

        List<Pendientes> listaCartera = new ArrayList<>();

        String str = "", str1 = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        for (String fruit : numeroRecibo) {
            str1 += "\'" + fruit + "\',";
        }

        String str2 = "";
        for (String fruit : parametro2) {
            str2 += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT *,SUM(valorFinal) as valor_Consignado FROM (SELECT clase_Documento,sociedad,cod_Cliente,cod_Vendedor,referencia,fecha_Documento,fecha_Consignacion," +
                    "valor_Documento,moneda,valor_Pagado AS valor_Pagado, valor_Consignado as valorFinal,cuenta_Bancaria," +
                    "moneda_Consig,NCF_Comprobante_Fiscal,docto_Financiero,nro_Recibo,observaciones,via_Pago," +
                    "usuario,operacion_Cme,idPago,sincronizado,banco,Numero_de_cheque,Nombre_del_propietario, observacionesmotivo, consecutivo as consecutivo, consecutivoid as consecutivoid  " +
                    "FROM recaudosPendientes c WHERE c.nro_Recibo IN ('" + str1.substring(1, str1.length() - 2) + "')  " +
                    "and c.idPago NOT IN ('" + str2.substring(1, str2.length() - 2) + "')  GROUP BY c.via_Pago,idPago order BY c.valor_Pagado DESC) GROUP BY via_Pago";

            System.out.println("ffff" + query);
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Pendientes pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.observacionesMotivo = cursor.getString(cursor.getColumnIndex("observacionesmotivo"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.consecutivo = cursor.getInt(cursor.getColumnIndex("consecutivo"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }


    public static List<Facturas> cargarFacturasParametroIDSPen(List<String> parametro, Context context) {

        List<Facturas> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT referencia,fecha_Consignacion,cuenta_Bancaria,observaciones,banco,Numero_de_cheque From recaudosPen WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "')";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();

                    cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    cartera.fechaConsignacion = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    cartera.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    listaCartera.add(cartera);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarFacturasParametroIDS(List<String> parametro, Context context) {

        List<Facturas> listaCartera = new ArrayList<>();

        String str = "";
        for (String fruit : parametro) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT referencia,fecha_Consignacion,cuenta_Bancaria,observaciones,banco,Numero_de_cheque,Nombre_del_propietario From recaudos WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "')";


            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();

                    cartera.referencia = cursor.getString(cursor.getColumnIndex("referencia"));
                    cartera.fechaConsignacion = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    cartera.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    cartera.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    listaCartera.add(cartera);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Facturas> cargarFacturasParametro(List<Facturas> parametro, Vector<String> listaItems, String consecutivo, Context context) {

        List<Facturas> listaCartera = new ArrayList<>();

        String str = "";
        for (Facturas fruit : parametro) {
            str += "\'" + fruit.idPago + "\',";
        }

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT c.cod_Cliente,c.clase_Documento," +
                    "SUM(DISTINCT valor_Consignado)AS valor_Consignado,c.via_Pago,c.idPago, banco, cuenta_Bancaria, Iden_Foto  FROM recaudos c WHERE  c.idPago IN ('" + str.substring(1, str.length() - 2) + "') AND c.nro_recibo = '" + consecutivo + "' GROUP BY c.idPago order BY c.valor_Consignado DESC ";


            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {

                    Facturas cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    cartera.idenFoto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                    listaCartera.add(cartera);
                    listaItems.add(cartera.codCliente + "-" + cartera.documento + "-" + cartera.consecutivo + "-"
                            + cartera.tipoDocumento + "-" +
                            cartera.valor + "-" + cartera.fecha + "-" + cartera.formaPago + "-" + cartera.idPago + "-");

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static List<Cartera> cargarCarteraFacturaParametro(String parametro, Vector<String> listaItems, Context context) {

        List<Cartera> listaCartera = new ArrayList<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT c.documento,c.valor  FROM recaudos_formapago c WHERE c.documento =  c.'" + parametro + "' DESC";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Cartera dias = new Cartera();
                    Cartera cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("documento"));

                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("valor"));

                    listaCartera.add(cartera);
                    listaItems.add(cartera.documento + "-" +
                            cartera.saldo + "-");

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;
    }

    public static void eliminarFacturaCartera(List<String> documentoFactura, Context context) {

        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM cartera WHERE documento IN('" + str.substring(1, str.length() - 2) + "')");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosTotalAnticiPenD(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudosPen WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }


    public static void eliminarRecaudosTotalAntici(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudos WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosTotalPendientesRecaudos(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudos WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarConsecutivoId(String documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";


        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM consecutivoID WHERE vendedor ='" + documentoFactura + "'");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarConsecutivoPaquete(String documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM consecutivopaquete WHERE vendedor ='" + documentoFactura + "'");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosRealziadosNumRe(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudosRealizados WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }


    public static void eliminarRecaudosTotalPendientesNumRe(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudosPendientes WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosTotalPendientes(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudosPendientes WHERE idPago IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosTotalPendiente(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudosPen WHERE docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }


    public static void eliminarRecaudosTotal(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudos WHERE docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosTotalAnticipo(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudos WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosTotalPen(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudosPen WHERE docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }


    public static void eliminarRecaudosPendientes(String documentoFactura, Context context) {

        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbTemp.execSQL("DELETE FROM recaudosPen WHERE idPago = '" + documentoFactura + "'");

            db.execSQL("DELETE FROM recaudosPendientes WHERE idPago = '" + documentoFactura + "'");

            db.execSQL("DELETE FROM recaudosRealizados WHERE idPago = '" + documentoFactura + "'");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }
    }


    public static void eliminarRecaudos(String documentoFactura, Context context) {

        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbTemp.execSQL("DELETE FROM recaudos WHERE idPago = '" + documentoFactura + "'");

            db.execSQL("DELETE FROM recaudosPendientes WHERE idPago = '" + documentoFactura + "'");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static Cliente cargarCliente(String cod_cliente, Context context) {
        String nombre = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;
        Cliente cliente = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT Nit,Nombre,Telefono,Codigo,Direccion,ciudad2,Ciudad  FROM clientes where Codigo='" + cod_cliente + "' ";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                cliente = new Cliente();
                cliente.nit = cursor.getString(cursor.getColumnIndex("Nit"));
                cliente.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                cliente.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                cliente.telefono = cursor.getString(cursor.getColumnIndex("Telefono"));
                cliente.ciudad = cursor.getString(cursor.getColumnIndex("Ciudad"));
                cliente.codigoZip = cursor.getString(cursor.getColumnIndex("ciudad2"));
                cliente.direccion = cursor.getString(cursor.getColumnIndex("Direccion"));


            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return cliente;
    }

    public static Cartera cargarCarteraClienteRecaudosRealizados(List<String> cod_cliente, Context context) {
        String nombre = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;
        Cartera cartera = null;

        /**   String str = "";
         for (Facturas fruit : cod_cliente) {
         str += "\'" + fruit.idPago + "\',";
         }**/

        String str = "";
        for (String fruit : cod_cliente) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT Documento FROM recaudosRealizados where docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') ";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                cartera = new Cartera();

                cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return cartera;
    }

    public static Cartera cargarCarteraClienteRecaudosPendientes(List<String> cod_cliente, Context context) {
        String nombre = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;
        Cartera cartera = null;

        /**   String str = "";
         for (Facturas fruit : cod_cliente) {
         str += "\'" + fruit.idPago + "\',";
         }**/

        String str = "";
        for (String fruit : cod_cliente) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT Documento FROM recaudosPendientes where docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') ";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                cartera = new Cartera();

                cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return cartera;
    }

    public static Cartera cargarCarteraCliente(List<String> cod_cliente, Context context) {
        String nombre = "";
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;
        Cartera cartera = null;

        /**   String str = "";
         for (Facturas fruit : cod_cliente) {
         str += "\'" + fruit.idPago + "\',";
         }**/

        String str = "";
        for (String fruit : cod_cliente) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT Documento FROM cartera where Documento_Financiero IN ('" + str.substring(1, str.length() - 2) + "') ";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                cartera = new Cartera();

                cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
            }
            cursor.close();


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return cartera;
    }

    public static Facturas getImpresionClienteRecaudosRealizados(String param, List<Facturas> idpago, String numeroRecibo, Context context) {
        SQLiteDatabase db = null;
        Facturas cartera = null;

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT  cod_Cliente,clase_Documento,valor_Consignado,via_Pago,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,SUM(valor_Documento) as valor_Documento,SUM(valor_Pagado) as valor_Pagado,via_Pago,docto_Financiero,Fecha_recibo " +
                    "FROM(SELECT cod_Cliente,clase_Documento,valor_Consignado,via_Pago,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento,valor_Pagado,via_Pago,docto_Financiero,Fecha_recibo " +
                    "FROM recaudos WHERE cod_Cliente = '" + param + "' UNION ALL SELECT cod_Cliente,clase_Documento,valor_Consignado,via_Pago,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,docto_Financiero,Fecha_recibo " +
                    "FROM recaudosRealizados WHERE cod_Cliente = '" + param + "')T WHERE nro_Recibo = '" + numeroRecibo + "'  GROUP BY docto_Financiero";

            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.valorDocumento = cursor.getFloat(cursor.getColumnIndex("valor_Documento"));
                    cartera.valorPagado = cursor.getFloat(cursor.getColumnIndex("valor_Pagado"));
                    cartera.nroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    cartera.fechaRecibo = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));

                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getImpresionClienteRecaudosPendientes: " + e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
        return cartera;
    }

    public static Facturas getImpresionClienteRecaudosPendientes(String param, List<Facturas> idpago, String numeroRecibo, Context context) {
        SQLiteDatabase db = null;
        Facturas cartera = null;

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT  cod_Cliente,clase_Documento,valor_Consignado,via_Pago,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,SUM(valor_Documento) as valor_Documento,SUM(valor_Pagado) as valor_Pagado,via_Pago,docto_Financiero, Fecha_recibo \n" +
                    "FROM(SELECT cod_Cliente,clase_Documento,valor_Consignado,via_Pago,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento,valor_Pagado,via_Pago,docto_Financiero,Fecha_recibo \n" +
                    "FROM recaudos WHERE cod_Cliente = '" + param + "' UNION ALL SELECT cod_Cliente,clase_Documento,valor_Consignado,via_Pago,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,docto_Financiero,Fecha_recibo \n" +
                    "FROM recaudosPendientes WHERE cod_Cliente = '" + param + "')T WHERE nro_Recibo = '" + numeroRecibo + "' GROUP BY docto_Financiero";

            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.valorDocumento = cursor.getFloat(cursor.getColumnIndex("valor_Documento"));
                    cartera.valorPagado = cursor.getFloat(cursor.getColumnIndex("valor_Pagado"));
                    cartera.nroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    cartera.fechaRecibo = cursor.getString(cursor.getColumnIndex("Fecha_recibo"));

                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getImpresionClienteRecaudosPendientes: " + e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
        return cartera;
    }

    public static Facturas getImpresionCliente(String param, List<Facturas> idpago, Context context) {
        SQLiteDatabase db = null;
        Facturas cartera = null;

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT  cod_Cliente,clase_Documento,valor_Consignado,via_Pago,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,SUM(valor_Documento) as valor_Documento,SUM(valor_Pagado) as valor_Pagado,via_Pago,docto_Financiero \n" +
                    "FROM(SELECT cod_Cliente,clase_Documento,valor_Consignado,via_Pago,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento,valor_Pagado,via_Pago,docto_Financiero \n" +
                    "FROM recaudos WHERE cod_Cliente = '" + param + "' UNION ALL SELECT cod_Cliente,clase_Documento,valor_Consignado,via_Pago,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,docto_Financiero \n" +
                    "FROM recaudosPen WHERE cod_Cliente = '" + param + "')T GROUP BY docto_Financiero";


            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.valorDocumento = cursor.getFloat(cursor.getColumnIndex("valor_Documento"));
                    cartera.valorPagado = cursor.getFloat(cursor.getColumnIndex("valor_Pagado"));
                    cartera.nroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Documento"));

                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
        } finally {
            if (db != null)
                db.close();
        }
        return cartera;
    }

    public static ArrayList<Facturas> getImpresionFactura(String param, List<Facturas> idpago, Context context) {

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }
        SQLiteDatabase db = null;
        Facturas cartera = null;
        ArrayList<Facturas> listaCabecera = new ArrayList<Facturas>();
        try {
            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            String query = "SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento,SUM(DISTINCT valor_consignado) as valor_Pagado,via_Pago,banco,Numero_de_cheque \n" +
                    "FROM (SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque \n" +
                    "FROM recaudos WHERE cod_Cliente = '" + param + "'  UNION ALL SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque \n" +
                    "FROM recaudosPen WHERE cod_Cliente = '" + param + "') T GROUP BY valor_Documento DESC";


            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.nroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    cartera.valorPagado = cursor.getFloat(cursor.getColumnIndex("valor_Pagado"));
                    cartera.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.valorDocumento = cursor.getFloat(cursor.getColumnIndex("valor_Documento"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    cartera.fechaConsignacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));


                    listaCabecera.add(cartera);
                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
        } finally {
            if (db != null)
                db.close();
        }
        return listaCabecera;
    }

    public static ArrayList<Facturas> getImpresionFacturaRealizados(String param, List<Facturas> idpago, String numeroRecibo, Context context) {

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }
        SQLiteDatabase db = null;
        Facturas cartera = null;
        ArrayList<Facturas> listaCabecera = new ArrayList<Facturas>();
        try {
            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento,SUM(DISTINCT valor_consignado) as valor_Pagado,via_Pago,banco,Numero_de_cheque \n" +
                    "FROM (SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque \n" +
                    "FROM recaudos WHERE cod_Cliente = '" + param + "'  UNION ALL SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque \n" +
                    "FROM recaudosRealizados WHERE cod_Cliente = '" + param + "') T WHERE nro_Recibo = '" + numeroRecibo + "' GROUP BY idPago,via_Pago";


            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.nroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    cartera.valorPagado = cursor.getFloat(cursor.getColumnIndex("valor_Pagado"));
                    cartera.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.valorDocumento = cursor.getFloat(cursor.getColumnIndex("valor_Documento"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    cartera.fechaConsignacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));

                    listaCabecera.add(cartera);
                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getImpresionFacturaPendientes: " + e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
        return listaCabecera;
    }

    public static ArrayList<Facturas> getImpresionFacturaPendientes(String param, List<Facturas> idpago, String numeroRecibo, Context context) {

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }
        SQLiteDatabase db = null;
        Facturas cartera = null;
        ArrayList<Facturas> listaCabecera = new ArrayList<Facturas>();
        try {
            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento,SUM(DISTINCT valor_consignado) as valor_Pagado,via_Pago,ifnull(banco,'') as banco,Numero_de_cheque \n" +
                    "FROM (SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque \n" +
                    "FROM recaudos WHERE cod_Cliente = '" + param + "'  UNION ALL SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque \n" +
                    "FROM recaudosPendientes WHERE cod_Cliente = '" + param + "') T WHERE nro_Recibo = '" + numeroRecibo + "' GROUP BY idPago,via_Pago";

            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.nroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    cartera.valorPagado = cursor.getFloat(cursor.getColumnIndex("valor_Pagado"));
                    cartera.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.valorDocumento = cursor.getFloat(cursor.getColumnIndex("valor_Documento"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    cartera.fechaConsignacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));

                    listaCabecera.add(cartera);
                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getImpresionFacturaPendientes: " + e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
        return listaCabecera;
    }

    public static ArrayList<Facturas> getImpresionFacturaHechasRecaudosRealizados(String param, List<Facturas> idpago, String numeroRecibo, Context context) {

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }
        SQLiteDatabase db = null;
        Facturas cartera = null;
        ArrayList<Facturas> listaCabecera = new ArrayList<Facturas>();
        try {
            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT DISTINCT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento as valor_Documento,SUM(valor_Pagado) as valor_Pagado,via_Pago,banco,Numero_de_cheque,docto_Financiero,observaciones " +
                    "FROM (SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque,docto_Financiero,observaciones " +
                    "FROM recaudos WHERE cod_Cliente = '" + param + "'  UNION ALL SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque,docto_Financiero,observaciones " +
                    "FROM recaudosRealizados WHERE cod_Cliente = '" + param + "') T WHERE nro_Recibo = '" + numeroRecibo + "' GROUP BY docto_Financiero,valor_Documento ORDER BY valor_Documento DESC ";

            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.nroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    cartera.fechaConsignacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    cartera.valorPagado = cursor.getFloat(cursor.getColumnIndex("valor_Pagado"));
                    cartera.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.valorDocumento = cursor.getFloat(cursor.getColumnIndex("valor_Documento"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    cartera.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));

                    listaCabecera.add(cartera);
                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getImpresionFacturaHechasRecaudosPendientes: " + e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
        return listaCabecera;
    }

    public static ArrayList<Facturas> getImpresionFacturaHechasRecaudosPendientes(String param, List<Facturas> idpago, String numeroRecibo, Context context) {

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }
        SQLiteDatabase db = null;
        Facturas cartera = null;
        ArrayList<Facturas> listaCabecera = new ArrayList<Facturas>();
        try {
            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT DISTINCT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento as valor_Documento,SUM(valor_Pagado) as valor_Pagado,via_Pago,banco,Numero_de_cheque,docto_Financiero, observaciones \n" +
                    "FROM (SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque,docto_Financiero, observaciones \n" +
                    "FROM recaudos WHERE cod_Cliente = '" + param + "'  UNION ALL SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque,docto_Financiero, observaciones \n" +
                    "FROM recaudosPendientes WHERE cod_Cliente = '" + param + "') T WHERE nro_Recibo = '" + numeroRecibo + "' GROUP BY docto_Financiero,valor_Documento ORDER BY valor_Documento DESC ";


            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.nroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    cartera.fechaConsignacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    cartera.valorPagado = cursor.getFloat(cursor.getColumnIndex("valor_Pagado"));
                    cartera.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.valorDocumento = cursor.getFloat(cursor.getColumnIndex("valor_Documento"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    cartera.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));

                    listaCabecera.add(cartera);
                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getImpresionFacturaHechasRecaudosPendientes: " + e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
        return listaCabecera;
    }

    public static ArrayList<Facturas> getImpresionFacturaHechas(String param, List<Facturas> idpago, Context context) {

        String str = "";
        for (Facturas fruit : idpago) {
            str += "\'" + fruit.idPago + "\',";
        }
        SQLiteDatabase db = null;
        Facturas cartera = null;
        ArrayList<Facturas> listaCabecera = new ArrayList<Facturas>();

        try {
            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT DISTINCT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento as valor_Documento,SUM(valor_Pagado) as valor_Pagado,via_Pago,banco,Numero_de_cheque,docto_Financiero \n" +
                    "FROM (SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque,docto_Financiero \n" +
                    "FROM recaudos WHERE cod_Cliente = '" + param + "'  UNION ALL SELECT cod_Cliente,clase_Documento,valor_Consignado,idPago,nro_Recibo,fecha_Documento,fecha_Consignacion,valor_Documento, valor_Pagado,via_Pago,banco,Numero_de_cheque,docto_Financiero \n" +
                    "FROM recaudosPen WHERE cod_Cliente = '" + param + "') T GROUP BY docto_Financiero,valor_Documento ORDER BY valor_Documento DESC ";

            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Facturas();

                    cartera.codCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    cartera.documento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    cartera.documentoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    cartera.valor = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    cartera.formaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    cartera.nroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    cartera.fecha = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    cartera.fechaConsignacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    cartera.valorPagado = cursor.getFloat(cursor.getColumnIndex("valor_Pagado"));
                    cartera.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    cartera.valorDocumento = cursor.getFloat(cursor.getColumnIndex("valor_Documento"));
                    cartera.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    cartera.numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));

                    listaCabecera.add(cartera);
                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
        } finally {
            if (db != null)
                db.close();
        }
        return listaCabecera;
    }

    public static ArrayList<Cartera> getImpresionCarteraRecaudosRealizados(List<String> idpago, Context context) {

        String str = "";
        for (String fruit : idpago) {
            str += "\'" + fruit + "\',";
        }
        SQLiteDatabase db = null;
        Cartera cartera = null;
        ArrayList<Cartera> listaCabecera = new ArrayList<Cartera>();
        try {
            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT Documento FROM recaudosRealizados where docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') GROUP BY valor_Documento ORDER BY valor_Documento Desc ";

            System.out.println("Impresion infoCliente Recaudos Realizados---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Cartera();
                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));

                    listaCabecera.add(cartera);
                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getImpresionCarteraRecaudosRealizados: " + e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
        if (listaCabecera.size() > 0) {
            return listaCabecera;
        } else {
            cartera = new Cartera();
            cartera.documento = "";

            listaCabecera.add(cartera);
            return listaCabecera;
        }
    }

    public static ArrayList<Cartera> getImpresionCarteraRecaudosPendientes(List<String> idpago, Context context) {

        String str = "";
        for (String fruit : idpago) {
            str += "\'" + fruit + "\',";
        }
        SQLiteDatabase db = null;
        Cartera cartera = null;
        ArrayList<Cartera> listaCabecera = new ArrayList<Cartera>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT ifnull(Documento,'') as Documento FROM recaudosPendientes where docto_Financiero IN ('" + str.substring(1, str.length() - 2) + "') GROUP BY valor_Documento ORDER BY valor_Documento Desc ";


            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));

                    listaCabecera.add(cartera);
                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getImpresionCarteraRecaudosPendientes: " + e.getMessage());
        } finally {
            if (db != null)
                db.close();
        }
        return listaCabecera;
    }

    public static ArrayList<Cartera> getImpresionCartera(List<String> idpago, Context context) {

        String str = "";
        for (String fruit : idpago) {
            str += "\'" + fruit + "\',";
        }
        SQLiteDatabase db = null;
        Cartera cartera = null;
        ArrayList<Cartera> listaCabecera = new ArrayList<Cartera>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT Documento FROM cartera where Documento_Financiero IN ('" + str.substring(1, str.length() - 2) + "') GROUP BY saldo ORDER BY saldo Desc ";

            System.out.println("Impresion infoCliente---> " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));

                    listaCabecera.add(cartera);
                }
                while (cursor.moveToNext());
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
        } finally {
            if (db != null)
                db.close();
        }
        return listaCabecera;
    }

    public static List<Dias> cargarDias(String parametro, Vector<String> listaItems, Context context) {

        List<Dias> listadias = new ArrayList<>();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT r.codigo,r.diavisita,c.codigo,c.nombre,c.razonsocial,c.nit,c.email,c.telefono,c.condicionpago,c.vendedor1,a.Balance,a.ProBalance From clientes c INNER JOIN rutero r ON c.codigo = r.codigo LEFT JOIN AccountManagement a ON c.codigo = a.cliente WHERE r.DIAVISITA = '" + parametro + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Dias dias = new Dias();
                    dias.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    dias.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                    dias.razonSocial = cursor.getString(cursor.getColumnIndex("Razonsocial"));
                    dias.nit = cursor.getString(cursor.getColumnIndex("Nit"));
                    dias.email = cursor.getString(cursor.getColumnIndex("email"));
                    dias.telefono = cursor.getString(cursor.getColumnIndex("Telefono"));
                    dias.condicionPago = cursor.getString(cursor.getColumnIndex("condicionpago"));
                    dias.Vendedor1 = cursor.getString(cursor.getColumnIndex("Vendedor1"));
                    dias.diasVisita = cursor.getString(cursor.getColumnIndex("DIAVISITA"));
                    dias.carteraVencida = cursor.getDouble(cursor.getColumnIndex("Balance"));
                    dias.porcentajeCarteraVenciada = cursor.getFloat(cursor.getColumnIndex("ProBalance"));

                    listadias.add(dias);
                    listaItems.add(dias.codigo + "-" + dias.nombre + "-" + dias.razonSocial + "-" +
                            dias.nit + "-" + dias.email + "-" + dias.telefono + "-" + dias.condicionPago + "-" + dias.Vendedor1);

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return listadias;
    }

    public static void eliminarCliente(String codigoClienteAEliminar, Context context) {

        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM clientes WHERE Codigo = '" + codigoClienteAEliminar + "'");

            mensaje = "Imagen borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando Imagen: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    /**
     * Metodo para eliminar un cobro programado que ya fue realizado y no ha sido sincronizado
     *
     * @param documento documento del cobro programado a eliminar
     */
    public static boolean eliminarCobroProgramado(String documento, Context context) {

        boolean respuesta = false;
        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {
            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "DELETE FROM ProgramacionPago WHERE documento = '" + documento + "'";

            db.execSQL(query);

            dbTemp.execSQL(query);

            respuesta = true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "EliminarCobroProgramado-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
        return respuesta;
    }

    /**
     * Metodo para calcular la cartera del cliente
     *
     * @param codigo codigo del cliente al cual se le calcula la cartera
     * @return valor de la cartera del cliente
     */
    public static double obtenerCarteraCliente(String codigo, Context context) {

        double cartera = 0;
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT SUM(saldo) AS valorCartera FROM cartera WHERE cliente='" + codigo + "'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                cartera = cursor.getDouble(cursor.getColumnIndex("valorCartera"));
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return cartera;
    }

    public static List<String> obtenerListaVendedorCartera(String codigoCliente, Context context) {

        String vendedor;
        SQLiteDatabase db = null;
        List<String> listaVendedor = new Vector<>();

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT 'Todos' AS Vendedor, 0 as Orden UNION SELECT DISTINCT Vendedor, 1 as Orden " +
                    "FROM Cartera WHERE Cliente = '" + codigoCliente + "' ORDER BY Orden";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    vendedor = cursor.getString(cursor.getColumnIndex("Vendedor"));
                    listaVendedor.add(vendedor);

                } while (cursor.moveToNext());

                mensaje = "Vendedores Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ListaVendedoresCartera", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaVendedor;
    }

    /**
     * Metodo saber cual es el valor de la cartera vencida que tiene un cliente
     *
     * @param codCliente codigo del cliente al cual se desea el saldo de la cartera  vencida del cliente
     */
    public static double getTotalCarteraVencidaCliente(String codCliente, Context context) {
        double valorCarteraVencido = 0;
        SQLiteDatabase db = null;

        try {
            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = " SELECT SUM(cartera.saldo) AS saldo, " +
                    "CAST ( ( julianday(datetime('now')) - julianday(datetime(substr(FechaVecto,1,4) || '-' || substr(FechaVecto,5,2) || '-' || SUBSTR(FechaVecto, 7, 2)) ) ) AS INT )   AS dias " +
                    "FROM cartera WHERE cartera.cliente='" + codCliente + "' AND dias >0";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                valorCarteraVencido = cursor.getDouble(cursor.getColumnIndex("saldo"));
            }
            cursor.close();

        } catch (Exception e) {

            Log.e(TAG, "" + e.getMessage());

        } finally {
            if (db != null)
                db.close();
        }
        return valorCarteraVencido;
    }


    public static boolean updateFormaPagoParcialAnticPend(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                                          String referencia, String fecha_Documento,
                                                          String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                          List<String> valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                          String docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                                          String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String idenFoto, String consecutivoid, Context context) {

        boolean resultado = false;
        List<Facturas> listaCartera = new ArrayList<>();
        Vector<String> listaItems = new Vector<>();
        SQLiteDatabase dbTemp = null;
        double valorCarteraVencido = 0;
        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : clase_Documento) {
            str += "\'" + fruit + "\',";
        }

        try {
            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            for (int i = 0; i < clase_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();
                Facturas cartera = new Facturas();
                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                facturasCartera.put("fecha_Documento", Utilidades.fechaActual("yyyy-MM-dd"));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero);
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("banco", banco);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Iden_Foto", idenFoto);
                facturasCartera.put("consecutivoid", consecutivoid);

                if (via_Pago.equals("6")) {
                    db.update("recaudos", facturasCartera, " via_Pago= ? AND idPago= ?",
                            new String[]{via_Pago, idPago});
                } else {
                    db.update("recaudosPen", facturasCartera, " via_Pago= ? AND idPago= ?",
                            new String[]{via_Pago, idPago});
                }

                resultado = true;
            }
            resultado = true;
        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean updateFormaPagoParcial(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                                 String referencia, String fecha_Documento,
                                                 String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                 List<String> valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                 String docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                                 String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String idenFoto, String consecutivoid, Context context) {

        boolean resultado = false;
        List<Facturas> listaCartera = new ArrayList<>();
        Vector<String> listaItems = new Vector<>();
        SQLiteDatabase dbTemp = null;
        double valorCarteraVencido = 0;
        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : clase_Documento) {
            str += "\'" + fruit + "\',";
        }

        try {
            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            for (int i = 0; i < clase_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();
                Facturas cartera = new Facturas();
                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                facturasCartera.put("fecha_Documento", Utilidades.fechaActual("yyyy-MM-dd"));
                facturasCartera.put("fecha_Consignacion", Utilidades.fechaActual("yyyy-MM-dd"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero);
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("banco", banco);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Iden_Foto", idenFoto);
                facturasCartera.put("consecutivoid", consecutivoid);

                db.update("recaudos", facturasCartera, " via_Pago= ? AND idPago= ?",
                        new String[]{via_Pago, idPago});

                resultado = true;
            }
            resultado = true;
        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean updateFormaPagoPendientes(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                                    String referencia, List<String> fecha_Documento,
                                                    String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                    List<String> valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                    List<String> docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                                    String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String idenFoto, String consecutivoid, Context context) {

        boolean resultado = false;
        List<Facturas> listaCartera = new ArrayList<>();
        Vector<String> listaItems = new Vector<>();
        SQLiteDatabase dbTemp = null;
        double valorCarteraVencido = 0;
        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : clase_Documento) {
            str += "\'" + fruit + "\',";
        }
        String str1 = "";
        for (String fruit : docto_Financiero) {
            str1 += "\'" + fruit + "\',";
        }

        try {
            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            for (int i = 0; i < clase_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();
                Facturas cartera = new Facturas();
                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("banco", banco);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Iden_Foto", idenFoto);
                facturasCartera.put("consecutivoid", consecutivoid);

                db.update("recaudosPen", facturasCartera, "docto_Financiero= ? AND via_Pago= ? AND idPago= ?",
                        new String[]{docto_Financiero.get(i), via_Pago, idPago});

                resultado = true;


            }


            resultado = true;
        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean updateFormaPagoPendientesCheque(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                                          String referencia, List<String> fecha_Documento,
                                                          String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                          List<String> valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                          List<String> docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                                          String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String idenFoto, Context context) {

        boolean resultado = false;
        List<Facturas> listaCartera = new ArrayList<>();
        Vector<String> listaItems = new Vector<>();
        SQLiteDatabase dbTemp = null;
        double valorCarteraVencido = 0;
        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : clase_Documento) {
            str += "\'" + fruit + "\',";
        }
        String str1 = "";
        for (String fruit : docto_Financiero) {
            str1 += "\'" + fruit + "\',";
        }

        try {
            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            for (int i = 0; i < clase_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();
                Facturas cartera = new Facturas();
                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("banco", banco);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Iden_Foto", idenFoto);

                db.update("recaudosPen", facturasCartera, "docto_Financiero= ? AND via_Pago= ? AND idPago= ?",
                        new String[]{docto_Financiero.get(i), via_Pago, idPago});

                resultado = true;
            }
            resultado = true;
        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }


    public static boolean updateFormaPago(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                          String referencia, List<String> fecha_Documento,
                                          String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                          List<String> valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                          List<String> docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                          String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String idenFoto, String consecutivoid, Context context) {

        boolean resultado = false;
        List<Facturas> listaCartera = new ArrayList<>();
        Vector<String> listaItems = new Vector<>();
        SQLiteDatabase dbTemp = null;
        double valorCarteraVencido = 0;
        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : clase_Documento) {
            str += "\'" + fruit + "\',";
        }
        String str1 = "";
        for (String fruit : docto_Financiero) {
            str1 += "\'" + fruit + "\',";
        }

        try {
            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            for (int i = 0; i < fecha_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();
                Facturas cartera = new Facturas();
                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                facturasCartera.put("fecha_Consignacion", Utilidades.fechaActual("yyyy-MM-dd"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("banco", banco);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Iden_Foto", idenFoto);
                facturasCartera.put("consecutivoid", consecutivoid);

                db.update("recaudos", facturasCartera, "docto_Financiero= ? AND via_Pago= ? AND idPago= ?",
                        new String[]{docto_Financiero.get(i), via_Pago, idPago});

                resultado = true;
            }
            resultado = true;
        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean updateFacturas(List<String> valor_Documento, List<String> docto_Financiero, Context context) {

        boolean resultado = false;
        List<Facturas> listaCartera = new ArrayList<>();
        Vector<String> listaItems = new Vector<>();
        SQLiteDatabase dbTemp = null;
        double valorCarteraVencido = 0;
        SQLiteDatabase db = null;
        String str = "";

        String str1 = "";
        for (String fruit : docto_Financiero) {
            str1 += "\'" + fruit + "\',";
        }

        try {
            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            for (int i = 0; i < docto_Financiero.size(); i++) {

                ContentValues facturasCartera = new ContentValues();
                Cartera cartera = new Cartera();

                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));

                facturasCartera.put("valor_Pagado", valor_Documento.get(i)); //saldo


                db.update("recaudos", facturasCartera, "docto_Financiero= ?",
                        new String[]{docto_Financiero.get(i)});

                resultado = true;
            }
            resultado = true;
        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean guardarConsecutivo(String negocio, int vendedor, int consecutivo, String fecha, Context context) {

        boolean resultado = false;
        String negocioCon = "";
        SQLiteDatabase dbTemp = null;
        //SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            //   File dbFile = new File(Utilidades.dirApp(), "DataBase.db");

            //  db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query1 = " SElECT negocio FROM consecutivorecibos";

            Cursor cursor = dbTemp.rawQuery(query1, null);

            if (cursor.moveToFirst()) {
                negocioCon = cursor.getString(cursor.getColumnIndex("negocio"));
            }

            cursor.close();


            if (negocioCon.isEmpty()) {

                ContentValues facturasCartera1 = new ContentValues();

                facturasCartera1.put("negocio", negocio);
                facturasCartera1.put("vendedor", vendedor);
                facturasCartera1.put("consecutivo", consecutivo);
                facturasCartera1.put("fecha", Utilidades.fechaActual("yyyy-MM-dd"));

                dbTemp.insertOrThrow("consecutivorecibos", null, facturasCartera1);
                dbTemp.close();
                resultado = true;

            } else if (!negocioCon.isEmpty()) {
                String query = " UPDATE consecutivorecibos SET  negocio = '" + negocio + "' ,vendedor = '" + vendedor + "',consecutivo ='" + consecutivo + "'," +
                        "fecha = '" + fecha + "'  WHERE vendedor = '" + vendedor + "'";

                dbTemp.rawQuery(query, null);

                ContentValues facturasCartera2 = new ContentValues();

                facturasCartera2.put("negocio", negocio);
                facturasCartera2.put("vendedor", vendedor);
                facturasCartera2.put("consecutivo", consecutivo);
                facturasCartera2.put("fecha", Utilidades.fechaActual("yyyy-MM-dd"));

                dbTemp.update("consecutivorecibos", facturasCartera2, " vendedor= ?",
                        new String[]{String.valueOf(vendedor)});
                dbTemp.close();
                resultado = true;
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean guardarConsecutivoId(String negocio, int vendedor, int consecutivo, String fecha, Context context) {

        boolean resultado = false;
        String negocioCon = "";
        SQLiteDatabase dbTemp = null;
        //SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            //   File dbFile = new File(Utilidades.dirApp(), "DataBase.db");

            //  db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query1 = " SElECT negocio FROM consecutivoID";

            Cursor cursor = dbTemp.rawQuery(query1, null);

            if (cursor.moveToFirst()) {
                negocioCon = cursor.getString(cursor.getColumnIndex("negocio"));
            }
            cursor.close();


            if (negocioCon.isEmpty()) {
                ContentValues facturasCartera1 = new ContentValues();

                facturasCartera1.put("negocio", negocio);
                facturasCartera1.put("vendedor", vendedor);
                facturasCartera1.put("consecutivo", consecutivo);
                facturasCartera1.put("fecha", Utilidades.fechaActual("yyyy-MM-dd"));

                dbTemp.insertOrThrow("consecutivoID", null, facturasCartera1);
                dbTemp.close();
                resultado = true;
            } else if (!negocioCon.isEmpty()) {
                String query = " UPDATE consecutivoID SET  negocio = '" + negocio + "' ,vendedor = '" + vendedor + "',consecutivo ='" + consecutivo + "'," +
                        "fecha = '" + fecha + "'  WHERE vendedor = '" + vendedor + "'";

                dbTemp.rawQuery(query, null);

                ContentValues facturasCartera2 = new ContentValues();

                facturasCartera2.put("negocio", negocio);
                facturasCartera2.put("vendedor", vendedor);
                facturasCartera2.put("consecutivo", consecutivo);
                facturasCartera2.put("fecha", Utilidades.fechaActual("yyyy-MM-dd"));

                dbTemp.update("consecutivoID", facturasCartera2, " vendedor= ?",
                        new String[]{String.valueOf(vendedor)});
                dbTemp.close();
                resultado = true;
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "consecutivoID-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean guardarConsecutivoPaquete(String negocio, int vendedor, int consecutivo, String fecha, Context context) {

        boolean resultado = false;
        String negocioCon = "";
        SQLiteDatabase dbTemp = null;
        //SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            //   File dbFile = new File(Utilidades.dirApp(), "DataBase.db");

            //  db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query1 = " SElECT negocio FROM consecutivopaquete";

            Cursor cursor = dbTemp.rawQuery(query1, null);

            if (cursor.moveToFirst()) {
                negocioCon = cursor.getString(cursor.getColumnIndex("negocio"));
            }
            cursor.close();


            if (negocioCon.isEmpty()) {
                ContentValues facturasCartera1 = new ContentValues();

                facturasCartera1.put("negocio", negocio);
                facturasCartera1.put("vendedor", vendedor);
                facturasCartera1.put("consecutivo", consecutivo);
                facturasCartera1.put("fecha", Utilidades.fechaActual("yyyy-MM-dd"));

                dbTemp.insertOrThrow("consecutivopaquete", null, facturasCartera1);
                dbTemp.close();
                resultado = true;
            } else if (!negocioCon.isEmpty()) {
                String query = " UPDATE consecutivorecibos SET  negocio = '" + negocio + "' ,vendedor = '" + vendedor + "',consecutivo ='" + consecutivo + "'," +
                        "fecha = '" + fecha + "'  WHERE vendedor = '" + vendedor + "'";

                dbTemp.rawQuery(query, null);

                ContentValues facturasCartera2 = new ContentValues();

                facturasCartera2.put("negocio", negocio);
                facturasCartera2.put("vendedor", vendedor);
                facturasCartera2.put("consecutivo", consecutivo);
                facturasCartera2.put("fecha", Utilidades.fechaActual("yyyy-MM-dd"));

                dbTemp.update("consecutivopaquete", facturasCartera2, " vendedor= ?",
                        new String[]{String.valueOf(vendedor)});
                dbTemp.close();
                resultado = true;
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean guardarFormaPagParcialPendiente(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                                          String referencia, List<String> fecha_Documento,
                                                          String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                          List<String> valor_Consignado, double saldo_favor, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                          List<String> docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                                          String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String identFoto, String consecutivoid, int consecId1, String observacionesMotivo, Context context) {
        boolean resultado = false;
        String empresa = cargarEmpresa(context);
        //   if (clase_Documento.size()>0) {
        // resultados = valor_Pagado/ clase_Documento.size();
        // }
        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        List<Pendientes> excluidos = DataBaseBO.cargarDocumentosRecaudosPendientesExcluidos(docto_Financiero,nro_Recibo, context);
        if (excluidos.size() == 0)
            excluidos = DataBaseBO.cargarDocumentosRecaudosExcluidos(docto_Financiero,nro_Recibo, context);

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            if(dbFile.exists())
                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            if(tempFile.exists())
                dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < clase_Documento.size(); i++) {


                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                if (empresa.equals("AGUC"))
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(i)));
                else
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("saldo_favor", saldo_favor);
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("banco", banco);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Estado", 0);
                facturasCartera.put("Iden_Foto", identFoto);
                facturasCartera.put("consecutivoid", consecutivoid);
                facturasCartera.put("consecutivo", consecId1);
                facturasCartera.put("observacionesmotivo", observacionesMotivo);

                db.insertOrThrow("recaudosPendientes", null, facturasCartera);
                dbTemp.insertOrThrow("recaudosPen", null, facturasCartera);

                if(via_Pago.equals("6"))
                {
                    facturasCartera.remove("consecutivoid");
                    db.insertOrThrow("recaudosRealizados", null, facturasCartera);
                }

                resultado = true;

            }

            resultado = true;

            for (int i = 0; i < excluidos.size(); i++) {


                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", excluidos.get(i).claseDocumento);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                if (empresa.equals("AGUC"))
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(0)));
                else
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(0));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", excluidos.get(i).valorDocumento);
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", 0);
                facturasCartera.put("valor_Consignado", valor_Consignado.get(0));
                facturasCartera.put("saldo_favor", saldo_favor);
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", excluidos.get(i).doctoFinanciero);
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("banco", banco);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Estado", 0);
                facturasCartera.put("Iden_Foto", identFoto);
                facturasCartera.put("consecutivoid", consecutivoid);
                facturasCartera.put("observacionesmotivo", observacionesMotivo);
                //   db.insertOrThrow("cobrosrealizados", null, valuesCobroRealizado);
                dbTemp.insertOrThrow("recaudosPen", null, facturasCartera);
                resultado = true;

            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

        }

        return resultado;
    }

    public static List<Pendientes> cargarDocumentosRecaudosPendientesExcluidos(List<String> docto_Financiero, String nro_recibo, Context context) {

        Pendientes pendientes;
        List<Pendientes> listaCartera = new ArrayList<>();
        SQLiteDatabase dbTemp = null;
        String sql = "";
        Gson gson = new Gson();

        try {

            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            sql = "select * from recaudospen where nro_Recibo = '" + nro_recibo + "' AND docto_financiero not in (" + gson.toJson(docto_Financiero).replace("[", "").replace("]", "") + ")  AND via_Pago = 'A'";

            Cursor cursor = dbTemp.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {
                    pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());

                cursor.close();
            } else {
                sql = "select * from recaudospen where nro_Recibo = '" + nro_recibo + "' AND  docto_financiero not in (" + gson.toJson(docto_Financiero).replace("[", "").replace("]", "") + ")  AND via_Pago = 'B'";

                cursor = dbTemp.rawQuery(sql, null);

                if (cursor.moveToFirst()) {

                    do {
                        pendientes = new Pendientes();
                        pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                        pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                        pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                        pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                        pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                        pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                        pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                        pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                        pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                        pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                        pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                        pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                        pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                        pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                        pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                        pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                        pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                        pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                        pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                        pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                        pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                        pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                        pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                        pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                        pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                        pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));

                        listaCartera.add(pendientes);

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Lista Pendientes Excluidos" +
                    ": " + mensaje, e);

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }

        return listaCartera;
    }

    public static boolean guardarFormaPagParcialPendienteTipoCobrador(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, List<String> cod_Vendedor,
                                                                      String referencia, List<String> fecha_Documento,
                                                                      String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                                      List<String> valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                                      List<String> docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                                                      String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String identFoto, String consecutivoid, String observacionesMotivo, Context context) {
        boolean resultado = false;
        String empresa = cargarEmpresa(context);
        //   if (clase_Documento.size()>0) {
        // resultados = valor_Pagado/ clase_Documento.size();
        // }
        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < clase_Documento.size(); i++) {


                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor.get(i));
                facturasCartera.put("referencia", referencia);
                if (empresa.equals("AGUC"))
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(i)));
                else
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("banco", banco);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Estado", 0);
                facturasCartera.put("Iden_Foto", identFoto);
                facturasCartera.put("consecutivoid", consecutivoid);
                facturasCartera.put("observacionesmotivo", observacionesMotivo);

                db.insertOrThrow("recaudosPendientes", null, facturasCartera);
                dbTemp.insertOrThrow("recaudosPen", null, facturasCartera);
                resultado = true;

            }

            resultado = true;


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

               if (db != null)
                 db.close();

            if (dbTemp != null)
                dbTemp.close();
        }

        return resultado;
    }


    public static boolean guardarFormaPagAnuladosSolicitud(List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor, List<String> docto_Financiero,
                                                           List<String> valor_Documento, String nro_Recibo, String numeroAnulacion, String codigoCausal, String usuario, String estado, String fechaRechazado, String observacion, Context context) {
        boolean resultado = false;

        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File File = new File(Utilidades.dirApp(context), "DataBase.db");

            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            db = SQLiteDatabase.openDatabase(File.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < clase_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("nro_anulacion", numeroAnulacion);
                facturasCartera.put("fecha", Utilidades.fechaActual("yyyy-MM-dd"));
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("codigo_causal", codigoCausal);
                facturasCartera.put("estado", estado);
                facturasCartera.put("Observacion", observacion);

                db.insertOrThrow("Solicitudes_Anulaciones", null, facturasCartera);

                facturasCartera.put("fecha_rechazo", fechaRechazado);

                dbTemp.insertOrThrow("Solicitudes_Anulacion", null, facturasCartera);

                resultado = true;
            }

            resultado = true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Guardar Anulacion-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }

        return resultado;
    }

    public static boolean guardarFormaPagAnulados(List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor, List<String> docto_Financiero,
                                                  List<String> valor_Documento, String nro_Recibo, String numeroAnulacion, String codigoCausal, String observaciones, Context context) {
        boolean resultado = false;

        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < clase_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("nro_anulacion", numeroAnulacion);
                facturasCartera.put("fecha", Utilidades.fechaActual("yyyy-MM-dd"));
                facturasCartera.put("codigo_causal", codigoCausal);
                facturasCartera.put("Observacion", observaciones);

                dbTemp.insertOrThrow("RecaudosAnulados", null, facturasCartera);
                db.insertOrThrow("RecaudosAnulados", null, facturasCartera);
                resultado = true;
            }

            resultado = true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Guardar Anulacion-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }

        return resultado;
    }

    public static boolean guardarFormaPagPendientesPen(List<String> idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                                       String referencia, List<String> fecha_Documento,
                                                       String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                       List<String> valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                       List<String> docto_Financiero, String nro_Recibo, String observaciones, List<String> via_Pago, String usuario,
                                                       String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, int status, Context context) {
        boolean resultado = false;
        String empresa = cargarEmpresa(context);

        SQLiteDatabase dbTemp = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");

            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < idPago.size(); i++) {

                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                if (empresa.equals("AGUC"))
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(i)));
                else
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago.get(i));
                facturasCartera.put("banco", banco);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Estado", status);

                //   db.insertOrThrow("cobrosrealizados", null, valuesCobroRealizado);
                dbTemp.insertOrThrow("recaudosPen", null, facturasCartera);
                resultado = true;
            }

            resultado = true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }


    public static boolean guardarFormaPagPendientes(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                                    String referencia, List<String> fecha_Documento,
                                                    String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                    List<String> valor_Consignado, List<String> saldo_favor, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                    List<String> docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                                    List<String> operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario,
                                                    String idenFoto, String numeroAnulacionId, int consec1Id, String observacionesMotivo, String fechRecibo, Context context) {
        boolean resultado = false;
        String empresa = cargarEmpresa(context);
        //   if (clase_Documento.size()>0) {
        // resultados = valor_Pagado/ clase_Documento.size();
        // }
        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            for (int i = 0; i < clase_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia.isEmpty() ? cod_Vendedor : referencia );
                if (empresa.equals("AGUC"))
                {
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(i)));
                    facturasCartera.put("Fecha_recibo", Utilidades.ordenarFechaHora(fechRecibo.replaceAll("/","-")));
                }
                else
                {
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                    facturasCartera.put("Fecha_recibo", fechRecibo);
                }
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("saldo_favor", saldo_favor.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("observacionesmotivo", observacionesMotivo);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme.get(i));
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("banco", banco);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Iden_Foto", idenFoto);
                facturasCartera.put("consecutivo", consec1Id);
                facturasCartera.put("consecutivoid", numeroAnulacionId);
                facturasCartera.put("envioCorreo", "1");

                dbTemp.insertOrThrow("recaudos", null, facturasCartera);

                facturasCartera.remove("consecutivoid");
                facturasCartera.remove("envioCorreo");
                facturasCartera.put("Documento", DataBaseBO.cargarDocumentoCartera(cod_Cliente,cod_Vendedor,docto_Financiero.get(i),valor_Documento.get(i), context));

                db.insertOrThrow("recaudosRealizados", null, facturasCartera);
                resultado = true;
            }

            resultado = true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
               db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean guardarFormaPagPendientesMultiples(List<String> idPago, List<String> clase_Documento, String sociedad, List<String> cod_Cliente, String cod_Vendedor,
                                                             String referencia, List<String> fecha_Documento,
                                                             String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                             List<String> consignadoM, List<String> saldo_favor, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                             List<String> docto_Financiero, List<String> nro_Recibo, List<String> observaciones, List<String> via_Pago, String usuario,
                                                             List<String> operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario,
                                                             String idenFoto, String numPaquete, String numPaqueteId, List<String> valor_Consignado, List<Integer> consecutivosMultiples, List<String> observacionesMotivo, List<String> listaConsecutivoidFac, List<String> fechasRecibos, Context context) {
        boolean resultado = false;
        String empresa = cargarEmpresa(context);
        //   if (clase_Documento.size()>0) {
        // resultados = valor_Pagado/ clase_Documento.size();
        // }
        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);



            for (int i = 0; i < clase_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente.get(i));
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia.isEmpty() ? cod_Vendedor : referencia );
                if (empresa.equals("AGUC"))
                {
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(i)));
                    facturasCartera.put("Fecha_recibo", Utilidades.ordenarFechaHora(fechasRecibos.get(i).replaceAll("/","-")));
                }
                else
                {
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                    facturasCartera.put("Fecha_recibo", fechasRecibos.get(i));
                }
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                if (via_Pago.get(i).equals("6"))
                {
                    facturasCartera.put("consignadoM", valor_Consignado.get(i));
                    facturasCartera.put("consecutivoid", listaConsecutivoidFac.get(i));
                }
                else
                {
                    facturasCartera.put("consignadoM", consignadoM.get(i));
                    facturasCartera.put("consecutivoid", listaConsecutivoidFac.get(0));
                }
                facturasCartera.put("saldo_favor", saldo_favor.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo.get(i));
                facturasCartera.put("observaciones", observaciones.get(i));
                facturasCartera.put("observacionesmotivo", observacionesMotivo.get(i));
                facturasCartera.put("via_Pago", via_Pago.get(i));
                facturasCartera.put("banco", banco);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme.get(i));
                facturasCartera.put("idPago", idPago.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Iden_Foto", idenFoto);
                facturasCartera.put("nro_paquete", numPaquete);
                facturasCartera.put("consecutivo", consecutivosMultiples.get(i));
                facturasCartera.put("envioCorreo", "1");

                dbTemp.insertOrThrow("recaudos", null, facturasCartera);

                facturasCartera.remove("envioCorreo");
                facturasCartera.remove("consignadoM");
                facturasCartera.remove("nro_paquete");
                facturasCartera.remove("consecutivoid");
                facturasCartera.put("Documento", DataBaseBO.cargarDocumentoCartera(cod_Cliente.get(i),cod_Vendedor,docto_Financiero.get(i),valor_Documento.get(i), context));

                db.insertOrThrow("recaudosRealizados", null, facturasCartera);
                resultado = true;
            }

            resultado = true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean guardarFormaPagPendientesMultiplesReferencias(List<String> idPago, List<String> clase_Documento, String sociedad, List<String> cod_Cliente, String cod_Vendedor,
                                                                        List<String> referencia, List<String> fecha_Documento,
                                                                        String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                                        List<String> consignadoM, List<String> saldo_favor, List<String> cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                                        List<String> docto_Financiero, List<String> nro_Recibo, List<String> observaciones, List<String> via_Pago, String usuario,
                                                                        List<String> operacion_Cme, int sincronizado, List<String> banco, String Numero_de_cheque, String Nombre_del_propietario,
                                                                        List<String> idenFoto, String numPaquete, List<String> numPaqueteId, List<String> valor_Consignado, List<Integer> consecutivos, List<String> fechasRecibos, List<String> observacionesMotivo, Context context) {
        boolean resultado = false;
        String empresa = cargarEmpresa(context);
        //   if (clase_Documento.size()>0) {
        // resultados = valor_Pagado/ clase_Documento.size();
        // }
        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < clase_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente.get(i));
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia.get(i));
                if (empresa.equals("AGUC"))
                {
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(i)));
                    facturasCartera.put("Fecha_recibo", Utilidades.ordenarFechaHora(fechasRecibos.get(i).replaceAll("/","-")));
                }
                else
                {
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                    facturasCartera.put("Fecha_recibo", fechasRecibos.get(i));
                }
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                if (via_Pago.get(i).equals("6"))
                    facturasCartera.put("consignadoM", valor_Consignado.get(i));
                else
                    facturasCartera.put("consignadoM", consignadoM.get(i));
                facturasCartera.put("saldo_favor", saldo_favor.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria.get(i));
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo.get(i));
                facturasCartera.put("observaciones", observaciones.get(i));
                facturasCartera.put("observacionesmotivo", observacionesMotivo.get(i));
                facturasCartera.put("via_Pago", via_Pago.get(i));
                facturasCartera.put("banco", banco.get(i));
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme.get(i));
                facturasCartera.put("idPago", idPago.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Iden_Foto", idenFoto.get(i));
                facturasCartera.put("nro_paquete", numPaquete);
                facturasCartera.put("consecutivoid", numPaqueteId.get(i));
                facturasCartera.put("envioCorreo", "1");
                facturasCartera.put("consecutivo", consecutivos.get(i));

                //   db.insertOrThrow("cobrosrealizados", null, valuesCobroRealizado);
                dbTemp.insertOrThrow("recaudos", null, facturasCartera);


                facturasCartera.remove("envioCorreo");
                facturasCartera.remove("consignadoM");
                facturasCartera.remove("nro_paquete");
                facturasCartera.remove("consecutivoid");
                facturasCartera.put("Documento", DataBaseBO.cargarDocumentoCartera(cod_Cliente.get(i),cod_Vendedor,docto_Financiero.get(i),valor_Documento.get(i), context));

                db.insertOrThrow("recaudosRealizados", null, facturasCartera);
                resultado = true;
            }

            resultado = true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean guardarFormaPagParcial(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                                 String referencia, List<String> fecha_Documento,
                                                 String fecha_Consignacion, List<String> valor_Documento, double saldo_favor, String moneda, List<String> valor_Pagado,
                                                 List<String> valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                 List<String> docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                                 String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String idenFoto, String consecutivoid, int consecutivo2, String observacionesMotivo, Context context) {
        boolean resultado = false;
        String empresa = cargarEmpresa(context);
        //   if (clase_Documento.size()>0) {
        // resultados = valor_Pagado/ clase_Documento.size();
        // }
        SQLiteDatabase dbTemp = null;
        List<Pendientes> excluidos = new ArrayList<>();
        SQLiteDatabase db = null;

        excluidos = DataBaseBO.cargarDocumentosRecaudosPendientesExcluidos(docto_Financiero, nro_Recibo, context);
        if (excluidos.size() == 0)
            excluidos = DataBaseBO.cargarDocumentosRecaudosExcluidos(docto_Financiero, nro_Recibo, context);

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < fecha_Documento.size(); i++) {

                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                if (empresa.equals("AGUC"))
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(i)));
                else
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("saldo_favor", saldo_favor);
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("banco", banco);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Estado", 0);
                facturasCartera.put("Iden_Foto", idenFoto);
                facturasCartera.put("consecutivoid", consecutivoid);
                facturasCartera.put("consecutivo", consecutivo2);
                facturasCartera.put("observacionesmotivo", observacionesMotivo);

                db.insertOrThrow("recaudosPendientes", null, facturasCartera);
                dbTemp.insertOrThrow("recaudos", null, facturasCartera);

                if(via_Pago.equals("6"))
                {
                    facturasCartera.remove("consecutivoid");
                    db.insertOrThrow("recaudosRealizados", null, facturasCartera);
                }

                resultado = true;

            }

            resultado = true;

            for (int i = 0; i < excluidos.size(); i++) {

                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", excluidos.get(i).claseDocumento);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                if (empresa.equals("AGUC"))
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(0)));
                else
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(0));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", excluidos.get(i).valorDocumento);
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", 0);
                facturasCartera.put("valor_Consignado", valor_Consignado.get(0));
                facturasCartera.put("saldo_favor", saldo_favor);
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", excluidos.get(i).doctoFinanciero);
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("banco", banco);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Estado", 0);
                facturasCartera.put("Iden_Foto", idenFoto);
                facturasCartera.put("consecutivoid", consecutivoid);
                facturasCartera.put("observacionesmotivo", observacionesMotivo);

                db.insertOrThrow("recaudosPendientes", null, facturasCartera);
                dbTemp.insertOrThrow("recaudos", null, facturasCartera);

                if(via_Pago.equals("6"))
                {
                    facturasCartera.remove("consecutivoid");
                    db.insertOrThrow("recaudosRealizados", null, facturasCartera);
                }

                resultado = true;

            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
               db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static List<Pendientes> cargarDocumentosRecaudosExcluidos(List<String> docto_Financiero, String nro_recibo, Context context) {

        SQLiteDatabase db = null;
        Pendientes pendientes;
        List<Pendientes> listaCartera = new ArrayList<>();
        SQLiteDatabase dbTemp = null;
        String sql = "";
        Gson gson = new Gson();

        try {

            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            sql = "select * from recaudos where nro_Recibo = '" + nro_recibo + "' AND docto_financiero not in (" + gson.toJson(docto_Financiero).replace("[", "").replace("]", "") + ")";

            Cursor cursor = dbTemp.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {
                    pendientes = new Pendientes();
                    pendientes.claseDocumento = cursor.getString(cursor.getColumnIndex("clase_Documento"));
                    pendientes.sociedad = cursor.getString(cursor.getColumnIndex("sociedad"));
                    pendientes.codigoCliente = cursor.getString(cursor.getColumnIndex("cod_Cliente"));
                    pendientes.cod_vendedor = cursor.getString(cursor.getColumnIndex("cod_Vendedor"));
                    pendientes.fechaCreacion = cursor.getString(cursor.getColumnIndex("fecha_Documento"));
                    pendientes.fechaCierre = cursor.getString(cursor.getColumnIndex("fecha_Consignacion"));
                    pendientes.valorDocumento = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));
                    pendientes.moneda = cursor.getString(cursor.getColumnIndex("moneda"));
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Pagado"));
                    pendientes.saldoAfavor = cursor.getDouble(cursor.getColumnIndex("saldo_favor"));
                    pendientes.cuentaBancaria = cursor.getString(cursor.getColumnIndex("cuenta_Bancaria"));
                    pendientes.monedaConsiganada = cursor.getString(cursor.getColumnIndex("moneda_Consig"));
                    pendientes.comprobanteFiscal = cursor.getString(cursor.getColumnIndex("NCF_Comprobante_Fiscal"));
                    pendientes.doctoFinanciero = cursor.getString(cursor.getColumnIndex("docto_Financiero"));
                    pendientes.numeroRecibo = cursor.getString(cursor.getColumnIndex("nro_Recibo"));
                    pendientes.observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    pendientes.operacionCME = cursor.getString(cursor.getColumnIndex("operacion_Cme"));
                    pendientes.idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    pendientes.sincronizado = cursor.getString(cursor.getColumnIndex("sincronizado"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));
                    pendientes.nombrePropietario = cursor.getString(cursor.getColumnIndex("Nombre_del_propietario"));
                    pendientes.consecutivoidFac = cursor.getString(cursor.getColumnIndex("consecutivoid"));

                    listaCartera.add(pendientes);

                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Lista Pendientes Excluidos" +
                    ": " + mensaje, e);

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }

        return listaCartera;
    }


    public static boolean guardarFormaPagParcialTipoCobrador(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, List<String> cod_Vendedor,
                                                             String referencia, List<String> fecha_Documento,
                                                             String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                                             List<String> valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                             List<String> docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                                             String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String idenFoto, String consecutivoid, String observacionesMotivo, Context context) {
        boolean resultado = false;
        String empresa = cargarEmpresa(context);
        //   if (clase_Documento.size()>0) {
        // resultados = valor_Pagado/ clase_Documento.size();
        // }
        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < fecha_Documento.size(); i++) {


                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor.get(i));
                facturasCartera.put("referencia", referencia);
                if (empresa.equals("AGUC"))
                    facturasCartera.put("fecha_Documento", Utilidades.ordenarFecha2(fecha_Documento.get(i)));
                else
                    facturasCartera.put("fecha_Documento", fecha_Documento.get(i));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado.get(i));
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("banco", banco);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Estado", 0);
                facturasCartera.put("Iden_Foto", idenFoto);
                facturasCartera.put("consecutivoid", consecutivoid);
                facturasCartera.put("observacionesmotivo", observacionesMotivo);

                db.insertOrThrow("recaudosPendientes", null, facturasCartera);
                dbTemp.insertOrThrow("recaudos", null, facturasCartera);

                if(via_Pago.equals("6"))
                {
                    facturasCartera.remove("consecutivoid");
                    db.insertOrThrow("recaudosRealizados", null, facturasCartera);
                }

                resultado = true;

            }

            resultado = true;


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
               db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }


    public static boolean guardarFormaPago(String idPago, List<String> clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor,
                                           String referencia, String fecha_Documento,
                                           String fecha_Consignacion, List<String> valor_Documento, String moneda, List<String> valor_Pagado,
                                           double valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                           List<String> docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario,
                                           String operacion_Cme, int sincronizado, String banco, String Numero_de_cheque, String Nombre_del_propietario, String idenFotos, String consecutivoid, int consecutivo2, Context context) {
        boolean resultado = false;
        String empresa = cargarEmpresa(context);
        //   if (clase_Documento.size()>0) {
        // resultados = valor_Pagado/ clase_Documento.size();
        // }
        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < clase_Documento.size(); i++) {


                ContentValues facturasCartera = new ContentValues();

                facturasCartera.put("clase_Documento", clase_Documento.get(i));
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("cod_Cliente", cod_Cliente);
                facturasCartera.put("cod_Vendedor", cod_Vendedor);
                facturasCartera.put("referencia", referencia);
                facturasCartera.put("fecha_Documento", Utilidades.fechaActual("yyyy-MM-dd"));
                facturasCartera.put("fecha_Consignacion", fecha_Consignacion);
                facturasCartera.put("Fecha_recibo", Utilidades.fechaActual("yyyy-MM-dd HH:mm"));
                facturasCartera.put("valor_Documento", valor_Documento.get(i));
                facturasCartera.put("moneda", moneda);
                facturasCartera.put("valor_Pagado", valor_Pagado.get(i));
                facturasCartera.put("valor_Consignado", valor_Consignado);
                facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
                facturasCartera.put("moneda_Consig", moneda_Consig);
                facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
                facturasCartera.put("docto_Financiero", docto_Financiero.get(i));
                facturasCartera.put("nro_Recibo", nro_Recibo);
                facturasCartera.put("observaciones", observaciones);
                facturasCartera.put("via_Pago", via_Pago);
                facturasCartera.put("banco", banco);
                facturasCartera.put("usuario", usuario);
                facturasCartera.put("operacion_Cme", operacion_Cme);
                facturasCartera.put("idPago", idPago);
                facturasCartera.put("sociedad", sociedad);
                facturasCartera.put("sincronizado", sincronizado);
                facturasCartera.put("Numero_de_cheque", Numero_de_cheque);
                facturasCartera.put("Nombre_del_propietario", Nombre_del_propietario);
                facturasCartera.put("Estado", 0);
                facturasCartera.put("Iden_Foto", idenFotos);
                facturasCartera.put("consecutivoid", consecutivoid);
                facturasCartera.put("consecutivo", consecutivo2);

                db.insertOrThrow("recaudosPendientes", null, facturasCartera);
                dbTemp.insertOrThrow("recaudos", null, facturasCartera);

                if(via_Pago.equals("6"))
                {
                    facturasCartera.remove("consecutivoid");
                    db.insertOrThrow("recaudosRealizados", null, facturasCartera);
                }

                resultado = true;

            }

            resultado = true;


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
               db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean guardarFormaPagoTotal(String idPago, String clase_Documento, String sociedad, String cod_Cliente, String cod_Vendedor, String referencia, String fecha_Documento,
                                                String fecha_Consignacion, double valor_Documento, String moneda, double valor_Pagado, double valor_Consignado, String cuenta_Bancaria, String moneda_Consig, String NCF_Comprobante_fiscal,
                                                String docto_Financiero, String nro_Recibo, String observaciones, String via_Pago, String usuario, String operacion_Cme, int sincronizado, Context context) {
        boolean resultado = false;

        SQLiteDatabase dbTemp = null;
        //SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            //   File dbFile = new File(Utilidades.dirApp(), "DataBase.db");

            //  db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues facturasCartera = new ContentValues();

            facturasCartera.put("clase_Documento", clase_Documento);
            facturasCartera.put("sociedad", sociedad);
            facturasCartera.put("cod_cliente", cod_Cliente);
            facturasCartera.put("cod_Vendedor", cod_Vendedor);
            facturasCartera.put("referencia", referencia);
            facturasCartera.put("fecha_Documento", Utilidades.fechaActual("yyyy-MM-dd"));
            facturasCartera.put("fecha_Consignacion", Utilidades.fechaActual("yyyy-MM-dd"));
            facturasCartera.put("valor_Documento", Utilidades.separarMilesSinDecimal(String.valueOf(valor_Documento), context));
            facturasCartera.put("moneda", moneda);
            facturasCartera.put("valor_Pagado", valor_Pagado);
            facturasCartera.put("valor_Consignado", valor_Consignado);
            facturasCartera.put("cuenta_Bancaria", cuenta_Bancaria);
            facturasCartera.put("moneda_Consig", moneda_Consig);
            facturasCartera.put("NCF_Comprobante_fiscal", NCF_Comprobante_fiscal);
            facturasCartera.put("docto_Financiero", docto_Financiero);
            facturasCartera.put("nro_Recibo", nro_Recibo);
            facturasCartera.put("observaciones", observaciones);
            facturasCartera.put("via_Pago", via_Pago);
            facturasCartera.put("usuario", usuario);
            facturasCartera.put("operacion_Cme", operacion_Cme);
            facturasCartera.put("idPago", idPago);
            facturasCartera.put("sociedad", sociedad);
            facturasCartera.put("sincronizado", sincronizado);

            //   db.insertOrThrow("cobrosrealizados", null, valuesCobroRealizado);
            dbTemp.insertOrThrow("recaudos", null, facturasCartera);

            resultado = true;


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            //   if (db != null)
            //     db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }


    /**
     * Metodo para validar si hay información guardada en la base de datos del dispositivo
     * para enviarla al sincronizador
     *
     * @return true si hay información por enviar y false si no la hay
     */
    public static boolean hayInformacionXEnviar(Context context) {

        mensaje = "";
        SQLiteDatabase db = null;

        boolean hayInfoPendiente = false;
        Vector<String> tableNames = new Vector<>();

        try {


            File dbFile = new File(Utilidades.dirApp(context), "Temp.db");

            if (dbFile.exists()) {

                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                String query = "SELECT tbl_name FROM sqlite_master WHERE tbl_name <> 'android_metadata' AND [type] = 'table'";
                Cursor cursor = db.rawQuery(query, null);

                if (cursor.moveToFirst()) {

                    do {

                        String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));
                        tableNames.addElement(tableName);

                    } while (cursor.moveToNext());
                }

                cursor.close();

                for (String tableName : tableNames) {

                    query = "SELECT COUNT(*) AS total FROM " + tableName;
                    cursor = db.rawQuery(query, null);

                    if (cursor.moveToFirst()) {

                        int total = cursor.getInt(cursor.getColumnIndex("total"));

                        if (total > 0) {

                            hayInfoPendiente = true;
                            break;
                        }
                    }

                    cursor.close();
                }

                cursor.close();

            } else {

                Log.e(TAG, "hayInformacionXEnviar");
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "hayInformacionXEnviar" + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return hayInfoPendiente;
    }

    /**
     * Metodo para trater lo que ha sido gestionado en la pestaña de gestion de recaudo
     *
     * @param codigoCliente codigo del cliente al cual se le carga la cartera
     * @param ocCartera     calculos de la cartera
     * @return retorna la lista de carteras de un cliente el cual ya se le realizo el recaudo
     */
    public static List<Cartera> cargarCarteraClienteGestionadaRecaudo(String codigoCliente, ObjetoCalculoCartera ocCartera, Context context) {

        SQLiteDatabase db = null;
        Cartera cartera;
        List<Cartera> listaCartera = new ArrayList<>();

        ocCartera.saldo = 0;
        ocCartera.saldoVencido = 0;
        ocCartera.totalRecaudoProgramado = 0;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);

            String sql;

            String dias = "CAST ( ( julianday(datetime('now')) - julianday(datetime(substr(FechaVecto,1,4) || '-' || substr(FechaVecto,5,2) || '-' || SUBSTR(FechaVecto, 7, 2)) ) ) AS INT )  ";

            sql = "select cartera.Documento Documento, cartera.CondicionPag CondicionPag, cartera.Saldo Saldo, "
                    + dias
                    + " AS dias, "
                    + "fechavecto FechaVecto, "
                    + "Conceptos.Nombre AS Nombre, "
                    + "Descripcion Descripcion, 0 AS indicador, "
                    + "Cartera.Vendedor AS Vendedor, "
                    + "cobrosrealizados.Valor AS valorPagado "
//                    + "cobrosrealizados.sincronizado "
                    + "FROM Cartera INNER JOIN conceptos ON cartera.Concepto = conceptos.Codigo "
                    + "INNER JOIN  cobrosrealizados ON cobrosrealizados.documento = cartera.documento "
                    + "WHERE cartera.cliente = '"
                    + codigoCliente
                    + "' AND cartera.Documento  IN ( SELECT documento documento FROM cobrosrealizados ) "
                    + "ORDER BY indicador DESC, dias  DESC";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {

                    cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
                    cartera.condPago = cursor.getString(cursor.getColumnIndex("CondicionPag"));
                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("valorPagado"));
                    cartera.dias = cursor.getInt(cursor.getColumnIndex("dias"));
                    cartera.fechaVencto = cursor.getString(cursor.getColumnIndex("FechaVecto"));
                    // cartera.fecha       = cursor.getString(cursor.getColumnIndex("fechaDoc"));
                    cartera.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                    cartera.descripcion = cursor.getString(cursor.getColumnIndex("Descripcion"));
                    cartera.indicador = cursor.getInt(cursor.getColumnIndex("indicador"));
                    cartera.vendedor = cursor.getString(cursor.getColumnIndex("Vendedor"));

//                    cartera.sincronizado = !cursor.getString(cursor.getColumnIndex("sincronizado")).equalsIgnoreCase("0");


                    ///// SE AGREGA NUEVA LOGICA PARA EL CALCULO DE LA DIFERENCIA EN DIAS ENTRE DOS FECHAS
                    cartera.dias = Utilidades.toInt(Utilidades.calcularFechaDiferenciaDias(cartera.fechaVencto));
                    //////////////////////////////////////////////////////////////////////////////////////

                    ocCartera.saldo += cursor.getDouble(cursor.getColumnIndex("valorPagado"));

                    listaCartera.add(cartera);

                } while (cursor.moveToNext());
            }

            cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "ListaLineas: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaCartera;
    }

    public static List<Cartera> cargarCarteraClienteGestionada(String codigoCliente, ObjetoCalculoCartera ocCartera, Context context) {

        SQLiteDatabase db = null;
        Cartera cartera;
        List<Cartera> listaCartera = new ArrayList<>();

        ocCartera.saldo = 0;
        ocCartera.saldoVencido = 0;
        ocCartera.totalRecaudoProgramado = 0;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);

            String sql;

            String dias = "CAST ( ( julianday(datetime('now')) - julianday(datetime(substr(FechaVecto,1,4) || '-' || substr(FechaVecto,5,2) || '-' || SUBSTR(FechaVecto, 7, 2)) ) ) AS INT )  ";

//            sql = "select cartera.Documento Documento, cartera.CondicionPag CondicionPag, cartera.Saldo Saldo, "
//                    + dias
//                    + " AS dias, "
//                    + "fechavecto FechaVecto, "
//                    + "Conceptos.Nombre AS Nombre, "
//                    + "Descripcion Descripcion, 0 AS indicador, "
//                    + "Cartera.Vendedor AS Vendedor, "
//                    + "programacionPago.Valor AS valorPagado, "
//                    + "programacionPago.sincronizado "
//                    + "FROM Cartera INNER JOIN conceptos ON cartera.Concepto = conceptos.Codigo "
//                    + "INNER JOIN  ProgramacionPago ON ProgramacionPago.documento = cartera.documento "
//                    + "WHERE cartera.cliente = '"
//                    + codigoCliente
//                    + "' AND cartera.Documento  IN ( SELECT documento documento FROM programacionpago ) "
//                    + "ORDER BY indicador DESC, dias  DESC";

            sql = "select c.Documento Documento, c.CondicionPag CondicionPag, c.Saldo Saldo, CAST ( ( julianday(datetime('now')) - julianday(datetime(substr(c.FechaVecto,1,4) || '-' || substr(c.FechaVecto,5,2) || '-' || SUBSTR(c.FechaVecto, 7, 2)) ) ) AS INT )   AS dias, c.FechaVecto FechaVecto, con.Nombre AS Nombre, Descripcion Descripcion, 0 AS indicador, c.Vendedor AS Vendedor, pp.Valor AS valorPagado, pp.sincronizado " +
                    "FROM Cartera c " +
                    "INNER JOIN conceptos con " +
                    "ON c.Concepto = con.Codigo " +
                    "INNER JOIN  ProgramacionPago pp " +
                    "ON pp.documento = c.documento " +
                    "and pp.fecha = (select max(fecha) from  ProgramacionPago " +
                    "                             where documento = pp.documento ) " +
                    "WHERE c.cliente = '" + codigoCliente + "'" +
                    " AND c.Documento  IN ( SELECT documento documento FROM programacionpago )  AND (Descripcion='DR' OR Descripcion='AB') ";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {

                    cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
                    cartera.condPago = cursor.getString(cursor.getColumnIndex("CondicionPag"));
                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("valorPagado"));
                    cartera.dias = cursor.getInt(cursor.getColumnIndex("dias"));
                    cartera.fechaVencto = cursor.getString(cursor.getColumnIndex("FechaVecto"));
                    // cartera.fecha       = cursor.getString(cursor.getColumnIndex("fechaDoc"));
                    cartera.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                    cartera.descripcion = cursor.getString(cursor.getColumnIndex("Descripcion"));
                    cartera.indicador = cursor.getInt(cursor.getColumnIndex("indicador"));
                    cartera.vendedor = cursor.getString(cursor.getColumnIndex("Vendedor"));

                    cartera.sincronizado = !cursor.getString(cursor.getColumnIndex("sincronizado")).equalsIgnoreCase("0");


                    ///// SE AGREGA NUEVA LOGICA PARA EL CALCULO DE LA DIFERENCIA EN DIAS ENTRE DOS FECHAS
                    cartera.dias = Utilidades.toInt(Utilidades.calcularFechaDiferenciaDias(cartera.fechaVencto));
                    //////////////////////////////////////////////////////////////////////////////////////

                    ocCartera.saldo += cursor.getDouble(cursor.getColumnIndex("valorPagado"));

                    listaCartera.add(cartera);

                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "ListaLineas: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaCartera;
    }

    /**
     * Método para borrar la información de la base de datos temporal, que se encuentra en el dispositivo
     */
    public static void borrarInfoTemp(Context context) {

        SQLiteDatabase dbTemp = null;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "Temp.db");

            if (dbFile.exists()) {

                dbTemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                Vector<String> tableNames = new Vector<>();
                String query = "SELECT tbl_name FROM sqlite_master";
                Cursor cursor = dbTemp.rawQuery(query, null);

                if (cursor.moveToFirst()) {

                    do {

                        String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));

                        if (tableName.equals("android_metadata"))
                            continue;

                        tableNames.addElement(tableName);

                    } while (cursor.moveToNext());
                }

                cursor.close();

                for (String tableName : tableNames) {

                    query = "DELETE FROM " + tableName;
                    dbTemp.execSQL(query);
                }
            }


        } catch (Exception e) {


            Log.e(TAG, "BorrarInfoTemp: " + e.getMessage(), e);

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static void borrarInfoDatabase(Context context) {

        SQLiteDatabase dbTemp = null;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            if (dbFile.exists()) {

                dbTemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                Vector<String> tableNames = new Vector<>();
                String query = "SELECT tbl_name FROM sqlite_master";
                Cursor cursor = dbTemp.rawQuery(query, null);

                if (cursor.moveToFirst()) {

                    do {

                        String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));

                        if (tableName.equals("android_metadata"))
                            continue;

                        tableNames.addElement(tableName);

                    } while (cursor.moveToNext());
                }

                cursor.close();

                for (String tableName : tableNames) {

                    query = "DELETE FROM " + tableName;
                    dbTemp.execSQL(query);
                }
            }


        } catch (Exception e) {


            Log.e(TAG, "BorrarInfoTemp: " + e.getMessage(), e);

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static boolean ExisteDataBase(Context context) {
        File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
        return dbFile.exists();
    }

    public static Usuario obtenerUsuario(Context context) {
        Usuario usuario = null;
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);

            String sql;

            sql = "SELECT codigousuario, Token " +
                    " FROM usuario";

            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                usuario = new Usuario();
                usuario.codigo = cursor.getString(cursor.getColumnIndex("codigousuario"));
                usuario.token = cursor.getString(cursor.getColumnIndex("Token"));
            }
            cursor.close();
        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "usuarioToken: " + mensaje, e);

        } finally {
            if (db != null)
                db.close();
        }
        return usuario;
    }

    public static void borrarDatos(String numDoc, String nunFactura, Context context) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {
            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            //SE BORRA INFO TMP
            String query = "DELETE FROM detalleacuerdocomercial WHERE idacuerdo = '" + numDoc + "' AND numerofactura = '" + nunFactura + "' ";
            db.execSQL(query);

            //SE BORRA INFO TMP
//            String query1 = "DELETE FROM firmaacuerdocomercial WHERE idacuerdo = '" + numDoc + "' AND numerofactura = '" + nunFactura + "' ";
            String queryEliminarFirma = "DELETE FROM firmaacuerdocomercial WHERE idacuerdo = '" + numDoc + "'";
            db.execSQL(queryEliminarFirma);


        } catch (Exception e) {

            mensaje = e.getMessage();

            Log.e(TAG, "ListaLineas: " + mensaje, e);
            Log.e(TAG, "EliminarCobroProgramado-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }

    }

    public static List<Cartera> cargarCarteraTipoParametroBusqueda2(String parametroBusqueda2, String parametro, Vector<String> listaItems, Context context) {


        List<Cartera> listaCartera = new ArrayList<>();
        String parametro2 = "";

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT c.documento,c.tipo,c.fechavecto,c.saldo,c.diasmora  FROM cartera c INNER JOIN clientes cli ON c.cliente = cli.codigo WHERE c.cliente = '" + parametro + "'   ORDER BY c.fechavecto =c. '" + parametroBusqueda2 + "'";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Cartera cartera = new Cartera();

                    cartera.documento = cursor.getString(cursor.getColumnIndex("Documento"));
                    cartera.concepto = cursor.getString(cursor.getColumnIndex("tipo"));
                    cartera.fechaVencto = cursor.getString(cursor.getColumnIndex("FechaVecto"));
                    cartera.saldo = cursor.getDouble(cursor.getColumnIndex("saldo"));
                    cartera.dias = cursor.getInt(cursor.getColumnIndex("diasmora"));
                    cartera.facturaSeleccionadaGestion = false;
                    listaCartera.add(cartera);
                    listaItems.add(cartera.documento + "-" + cartera.concepto + "-" + cartera.fechaVencto + "-" +
                            cartera.saldo + "-" + cartera.dias + "-");


                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaCartera;

    }

    public static List<FacturasRealizadas> cargarFacturasViaPago(String numeroRecibo, Context context) {

        List<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<>();

        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT sum(valorPagado) as valorPagado,sum(valor_Consignado) as valor_Consignado,via_Pago as via_Pago, banco, Numero_de_cheque FROM " +
                    "(select valor_Consignado as valorPagado,valor_Consignado as valor_Consignado, via_Pago, banco, Numero_de_cheque from recaudosRealizados  " +
                    "where nro_Recibo = '" + numeroRecibo + "' group by idPago) group by via_Pago ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valorPagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));

                    listaFacturasRealizadas.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasRealizadas;
    }

    public static List<FacturasRealizadas> cargarFacturasViaPagoAGUC(String numeroRecibo, Context context) {

        List<FacturasRealizadas> listaFacturasRealizadas = new ArrayList<>();


        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT sum(valorPagado) as valorPagado,sum(valor_Consignado) as valor_Consignado,via_Pago as via_Pago, banco, Numero_de_cheque FROM " +
                    "(select valor_Consignado as valorPagado,valor_Consignado as valor_Consignado, via_Pago, banco, Numero_de_cheque, idPago from recaudosRealizados  " +
                    "where nro_Recibo = '" + numeroRecibo + "' group by idPago) group by idPago,via_Pago ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    FacturasRealizadas pendientes = new FacturasRealizadas();
                    pendientes.montoPendientes = cursor.getDouble(cursor.getColumnIndex("valorPagado"));
                    pendientes.valorConsignado = cursor.getDouble(cursor.getColumnIndex("valor_Consignado"));
                    pendientes.viaPago = cursor.getString(cursor.getColumnIndex("via_Pago"));
                    pendientes.banco = cursor.getString(cursor.getColumnIndex("banco"));
                    pendientes.numeroCheqe = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));

                    listaFacturasRealizadas.add(pendientes);

                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return listaFacturasRealizadas;
    }

    public static Boolean ValidarMetodoDePagoPorRecibo(String numeroRecibo, String metodoPago, Context context) {

        boolean existe = false;

        SQLiteDatabase db = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT * FROM recaudosPendientes WHERE nro_Recibo = '" + numeroRecibo + "' AND via_Pago = '" + metodoPago + "'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    existe = true;

                } while (cursor.moveToNext());

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }

        return existe;
    }

    @SuppressLint("Range")
    public static boolean actualizarFotoBuzon(String idPago, Context context) {

        boolean resultado = false;
        String Iden_Foto = "";
        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            String query1 = "SElECT Iden_Foto FROM recaudosPendientes WHERE idPago = '" + idPago + "' ";

            Cursor cursor = db.rawQuery(query1, null);

            if (cursor.moveToFirst()) {

                Iden_Foto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                String query = " UPDATE recaudos SET  foto_buzon =  Iden_Foto, Iden_Foto = '" + Iden_Foto + "' " +
                        " WHERE idPago = '" + idPago + "'";

                dbTemp.execSQL(query);
            }
            cursor.close();


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean actualizarFotoBuzonMultiples(List<String> idPagos, Context context) {

        boolean resultado = false;
        String Iden_Foto = "";

        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {

            //QUITAR VALORES REPETIDOS
            Set<String> conjuntoSinDuplicados = new LinkedHashSet<>(idPagos);
            List<String> idPagosFinal = new ArrayList<>(conjuntoSinDuplicados);

            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (String idPago : idPagosFinal) {

                String query1 = "SElECT Iden_Foto FROM recaudosPendientes WHERE idPago = '" + idPago + "' ";

                Cursor cursor = db.rawQuery(query1, null);

                if (cursor.moveToFirst()) {

                    Iden_Foto = cursor.getString(cursor.getColumnIndex("Iden_Foto"));

                    String query = " UPDATE recaudos SET   foto_buzon =  Iden_Foto, Iden_Foto = '" + Iden_Foto + "' " +
                            " WHERE idPago = '" + idPago + "'";

                    dbTemp.execSQL(query);
                }
                cursor.close();
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static boolean actualizarNumeroChequePrecargado(List<String> idPagos, Context context) {

        boolean resultado = false;
        String numeroCheque = "";

        SQLiteDatabase dbTemp = null;
        SQLiteDatabase db = null;

        try {

            //QUITAR VALORES REPETIDOS
            Set<String> conjuntoSinDuplicados = new LinkedHashSet<>(idPagos);
            List<String> idPagosFinal = new ArrayList<>(conjuntoSinDuplicados);

            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");

            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (String idPago : idPagosFinal) {

                String query1 = "SElECT Numero_de_cheque FROM recaudosPendientes WHERE idPago = '" + idPago + "' ";

                Cursor cursor = db.rawQuery(query1, null);

                if (cursor.moveToFirst()) {

                    numeroCheque = cursor.getString(cursor.getColumnIndex("Numero_de_cheque"));

                    String query = " UPDATE recaudos SET   Numero_de_cheque = '" + numeroCheque + "' " +
                            " WHERE idPago = '" + idPago + "'";

                    dbTemp.execSQL(query);
                }
                cursor.close();
            }


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarFactura-> " + e.getMessage());

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    /***********
     * Meétodo que se encarga de guardar la firma digitada por el vendedor
     * @param IdPAgo
     * @param bitmap
     * @param empresa
     * @param vendedor
     * @param nombreFirma
     */
    public static void guardarFirma(String IdPAgo, byte[] bitmap, String empresa, String vendedor, String nombreFirma, Context context) {

        try {
            // tamaño del baos depende del tamaño de tus imagenes en promedio
            SQLiteDatabase dbTemp = null;
            SQLiteDatabase db = null;

            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String sql = "INSERT INTO firmas (IdPago, Foto, Empresa, Vendedor, Firma) VALUES(?,?,?,?,?)";
            SQLiteStatement insert = dbTemp.compileStatement(sql);

            insert.clearBindings();
            insert.bindString(1, IdPAgo);
            insert.bindBlob(2, bitmap);
            insert.bindString(3, empresa);
            insert.bindString(4, vendedor);
            insert.bindString(5, nombreFirma);
            insert.executeInsert();

            sql = "INSERT INTO firmas (IdPago, Foto, Empresa, Vendedor, Firma) VALUES(?,?,?,?,?)";
            insert = db.compileStatement(sql);

            insert.clearBindings();
            insert.bindString(1, IdPAgo);
            insert.bindBlob(2, bitmap);
            insert.bindString(3, empresa);
            insert.bindString(4, vendedor);
            insert.bindString(5, nombreFirma);
            insert.executeInsert();

            dbTemp.close();
            db.close();

        } catch (Exception e) {
            Log.println(Log.ERROR, "Error guardarFirma", Objects.requireNonNull(e.getMessage()));
        }
    }

    public static FirmaNombre cargarFirmaNombre(String numeroRecibo, Context context) {

        FirmaNombre firmaNombre = new FirmaNombre();

        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT Foto as Foto, Firma as Firma FROM firmas_vendedor " +
                    "where idPago IN (SELECT DISTINCT r.idPago FROM recaudosRealizados r " +
                    "WHERE r.nro_Recibo = '" + numeroRecibo + "') ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                do {

                    firmaNombre.firma = cursor.getBlob(cursor.getColumnIndex("Foto"));
                    firmaNombre.firmaNombre = cursor.getString(cursor.getColumnIndex("Firma"));

                } while (cursor.moveToNext() && firmaNombre.firma == null);

            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();

        }


        return firmaNombre;
    }

    public static String validarUltimoConsecutivo(String consecutivo, Context context) {
        int ultimoConsecutivoReacudosPen = 0;
        int ultimoConsecutivoReacudosRe = 0;
        int ultimoConsecutivoReacudosReTemp = 0;
        int ultimoConsecutivoRecaudosPendientes = 0;
        int ultimoConsecutivoRecaudosRealizados = 0;
        int consecutivoActualPedido = 0;
        SQLiteDatabase db = null;
        SQLiteDatabase dbtemp = null;

        try {


            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbtemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            //TRAER ULTIMO CONSECUTIVO RECAUDOSPEN
            String query = "SELECT consecutivo  FROM recaudosPen ORDER BY  consecutivo DESC ";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                ultimoConsecutivoReacudosPen = cursor.getInt(cursor.getColumnIndex("consecutivo"));
            }
            cursor.close();

            //TRAER ULTIMO CONSECUTIVO RECAUDOS
            query = "SELECT consecutivo  FROM recaudos ORDER BY  consecutivo DESC ";

            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                ultimoConsecutivoReacudosRe = cursor.getInt(cursor.getColumnIndex("consecutivo"));
            }
            cursor.close();

            //TRAER ULTIMO CONSECUTIVO RECAUDOSPENDIENTES
            query = "SELECT consecutivo  FROM recaudosPendientes ORDER BY  consecutivo DESC ";

            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                ultimoConsecutivoRecaudosPendientes = cursor.getInt(cursor.getColumnIndex("consecutivo"));
            }
            cursor.close();

            //TRAER ULTIMO CONSECUTIVO RECAUDOS
            query = "SELECT consecutivo  FROM recaudosRealizados ORDER BY  consecutivo DESC ";

            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                ultimoConsecutivoRecaudosRealizados = cursor.getInt(cursor.getColumnIndex("consecutivo"));
            }
            cursor.close();

            //TRAER ULTIMO CONSECUTIVO RECAUDOS TEMP
            query = "SELECT consecutivo  FROM recaudos ORDER BY  consecutivo DESC ";

            cursor = dbtemp.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                ultimoConsecutivoReacudosReTemp = cursor.getInt(cursor.getColumnIndex("consecutivo"));
            }
            cursor.close();


            //VALIDAR CUAL ES EL CONSECUTIVO MAYOR DE LOS PEDIDOS ACTUALES
            consecutivoActualPedido = Math.max(
                    Math.max(ultimoConsecutivoReacudosPen, ultimoConsecutivoRecaudosPendientes),
                    Math.max(ultimoConsecutivoReacudosRe, ultimoConsecutivoRecaudosRealizados));

            consecutivoActualPedido = Math.max(consecutivoActualPedido,ultimoConsecutivoReacudosReTemp);


            if (consecutivoActualPedido > Integer.parseInt(consecutivo)) {
                return String.valueOf(consecutivoActualPedido);
            }


        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (dbtemp != null)
                dbtemp.close();

            if (db != null)
                db.close();

        }

        return consecutivo;
    }

    public static void eliminarFotosSinDocumentosAsociados(Context context) {

        SQLiteDatabase dbTemp = null;

        try {

            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbTemp.execSQL("DELETE FROM fotos WHERE Idfoto IN(SELECT f.Idfoto FROM fotos f " +
                    "LEFT JOIN recaudos r ON f.Iden_Foto = r.Iden_Foto " +
                    "LEFT JOIN recaudosPen rp ON f.Iden_Foto = rp.Iden_Foto " +
                    "WHERE r.clase_Documento IS NULL AND rp.clase_Documento IS NULL)");

            mensaje = "fotos borradas con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static String cargarFotosSinDocumentosAsociados(String numeroRecibo, Context context) {

        SQLiteDatabase dbTemp = null;
        String idenFoto = null;

        try {

            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT Iden_Foto as Foto FROM fotos " +
                    "where IdPago = '" + numeroRecibo + "' ";

            Cursor cursor = dbTemp.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                idenFoto = cursor.getString(cursor.getColumnIndex("Foto"));

            }
            cursor.close();

            return idenFoto;

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();
            return idenFoto;

        } finally {

            if (dbTemp != null)
                dbTemp.close();

            return idenFoto;
        }
    }

    public static boolean guardarCoordenadas(Double latitud, Double longitud, String codigo, String nroRecibo, Context context) {
        boolean resultado = false;
        SQLiteDatabase dbTemp = null;

        try {
            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues fotos = new ContentValues();
            fotos.put("latitud", latitud);
            fotos.put("longitud", longitud);
            fotos.put("codigo", codigo);
            fotos.put("nro_Recibo", nroRecibo);

            dbTemp.insertOrThrow("coordenadasClientes", null, fotos);

            resultado = true;


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "GuardarCoordenada-> " + e.getMessage());

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }


        return resultado;
    }

    public static void verificarPagoCompletoPorTransferencia(String numeroRecibo, Context context) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;
        Double valorPagado = 0.0;
        Double valorTotal = 0.0;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File tempFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(tempFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT " +
                    "SUM(r.valor_Pagado) as valorPagado, " +
                    "(SELECT SUM(valor_Documento) " +
                    "FROM (SELECT DISTINCT valor_Documento  " +
                    "FROM RecaudosPendientes " +
                    "WHERE nro_Recibo = '" + numeroRecibo + "' AND via_Pago = '6') as doc) as valor_Documento FROM RecaudosPendientes r " +
                    "where nro_Recibo = '" + numeroRecibo + "' AND via_Pago ='6'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                valorPagado = cursor.getDouble(cursor.getColumnIndex("valorPagado"));
                valorTotal = cursor.getDouble(cursor.getColumnIndex("valor_Documento"));

            }
            cursor.close();

            if((Utilidades.formatearDecimales(valorTotal,2).equals(Utilidades.formatearDecimales(valorPagado,2)) && (valorTotal != 0 && valorPagado != 0)))
            {
                query = "DELETE FROM RecaudosPendientes " +
                        "where nro_Recibo = '" + numeroRecibo + "' ";

                db.execSQL(query);

            }
            else
            {
                query = "DELETE FROM RecaudosRealizados " +
                        "where nro_Recibo = '" + numeroRecibo + "' ";

                db.execSQL(query);

                query = "INSERT INTO recaudosPen " +
                        "SELECT clase_Documento, sociedad, cod_Cliente, cod_Vendedor, referencia, fecha_Documento, fecha_Consignacion, Fecha_recibo, valor_Documento, moneda, valor_Pagado, valor_Consignado, saldo_favor, cuenta_Bancaria, moneda_Consig, NCF_Comprobante_Fiscal, docto_Financiero, nro_Recibo, observaciones, observacionesmotivo, via_Pago, usuario, operacion_Cme, idPago, sincronizado, banco, Numero_de_cheque, Nombre_del_propietario, Estado, Iden_Foto, consecutivoid, consignadoM, consecutivo " +
                        "FROM recaudos where nro_Recibo  = '" + numeroRecibo + "' AND via_Pago ='6'";

                dbTemp.execSQL(query);

                query = "DELETE FROM recaudos " +
                        "where nro_Recibo = '" + numeroRecibo + "' ";

                dbTemp.execSQL(query);

            }

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

        }
    }

    public static String cargarDocumentoCartera(String cliente, String vendedor, String docto_financiero, String saldo, Context context) {


        List<Cartera> listaCartera = new ArrayList<>();
        String documento = "";
        SQLiteDatabase db = null;
        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT c.Documento as documento FROM Cartera c where c.cliente = '" + cliente + "' AND c.vendedor = '" + vendedor + "' " +
                    "AND c.Documento_Financiero = '" + docto_financiero + "'  AND c.saldo = '" + saldo + "'";


            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                documento = cursor.getString(cursor.getColumnIndex("documento"));
            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
        } finally {

            if (db != null)
                db.close();
        }
        return documento;
    }

    public static void eliminarRecaudosPendientesDataBase(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudosPendientes WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosRealizadosPendientesRecaudos(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";
        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudosRealizados WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosRealizadosDataBase(List<String> documentoFactura, Context context) {

        SQLiteDatabase db = null;
        String str = "";

        for (String fruit : documentoFactura) {
            str += "\'" + fruit + "\',";
        }

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM recaudosRealizados WHERE nro_Recibo IN ('" + str.substring(1, str.length() - 2) + "') ");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void eliminarRecaudosFinalizados(Context context) {

        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Utilidades.dirApp(context), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbTemp.execSQL("DELETE FROM recaudosPen WHERE nro_Recibo IN (SELECT DISTINCT re.nro_Recibo FROM recaudos re)");

            mensaje = "borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando: " + e.getMessage();

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static List<String> cargarIdPagosNumeroRecibo(String numeroRecibo, Context context) {
        String valor = "";
        SQLiteDatabase db = null;

        List<String> listaIdPago = new ArrayList<>();

        try {

            dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT idPago FROM RecaudosPendientes r WHERE r.nro_Recibo = '" + numeroRecibo + "'";

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    String idPago = cursor.getString(cursor.getColumnIndex("idPago"));
                    listaIdPago.add(idPago);


                } while (cursor.moveToNext());


            }
            cursor.close();

        } catch (Exception e) {
            mensaje = e.getMessage();
            Log.e("obtenerCarteraCliente", mensaje, e);

        } finally {

            if (db != null)
                db.close();

        }

        return listaIdPago;
    }

    public static String ObtenerVersionApp(Context context) {

        String version = "";
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT version FROM Version";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                version = cursor.getString(cursor.getColumnIndex("version"));
            }

            Log.i("ObtenerVersionApp", "version = " + version);

            if (cursor != null)
                cursor.close();

            //Se registra el Log de la consulta SQl Registrada

            ArrayList<String> querys = new ArrayList<String>();
            querys.add(query);


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "ObtenerVersionApp: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return version;
    }

    public static String obtenerUrlDownloadVersionApp(Context context) {

        String url = "";
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Utilidades.dirApp(context), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT url as url FROM version";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                url = cursor.getString(cursor.getColumnIndex("url"));
            }

            Log.i("obtenerUrlDownloadVersionApp", "url = " + url);

            if (cursor != null)
                cursor.close();

            //Se registra el Log de la consulta SQl Registrada

            ArrayList<String> querys = new ArrayList<String>();
            querys.add(query);


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "obtenerUrlDownloadVersionApp: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return url;
    }

}
