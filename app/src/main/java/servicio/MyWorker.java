package servicio;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.gson.Gson;

import java.util.concurrent.CountDownLatch;

import businessObject.DataBaseBO;
import configuracion.Synchronizer;
import dataobject.Usuario;
import sharedpreferences.PreferencesUsuario;
import utilidades.Constantes;

public class MyWorker extends Worker implements Synchronizer {

    private CountDownLatch latch; // Para sincronizar el flujo
    private boolean envioExitoso; // Para almacenar el resultado del envío

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d("BackgroundTaskTest", "✅ Proceso en segundo plano iniciado correctamente.");

            if (DataBaseBO.hayInformacionXEnviar()) {
                Log.i("BackgroundTaskTest", "Existe informacion pendiente por enviar...");
                latch = new CountDownLatch(1); // Inicializar el CountDownLatch
                enviarInformacionServidor();
                latch.await(); // Esperar a que respuestaEnviarInformacion termine
                if (!envioExitoso) {
                    return Result.retry(); // Reintentar si el envío falla
                }
            } else {
                Log.i("BackgroundTaskTest", "No existe informacion pendiente por enviar...");
            }

            return Result.success();
        } catch (Exception e) {
            Log.e("BackgroundTaskTest", "❌ Error en el proceso en segundo plano: " + e.getMessage(), e);
            return Result.retry(); // Reintentar en caso de fallo
        }
    }

    private void enviarInformacionServidor() {
        try {
            Log.i("BackgroundTaskTest", "Enviando información...");
            final String empresa = DataBaseBO.cargarCodigo();
            Sync sync = new Sync(MyWorker.this, Constantes.ENVIARINFORMACION);
            sync.user = empresa;
            sync.start();
        } catch (Exception e) {
            Log.e("BackgroundTaskTest", "❌ Error enviando información: " + e.getMessage(), e);
            envioExitoso = false; // Marcar el envío como fallido
            latch.countDown(); // Liberar el CountDownLatch
        }
    }

    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
        try {
            switch (codeRequest) {
                case Constantes.ENVIARINFORMACION:
                    respuestaEnviarInformacion(ok, respuestaServer);
                    break;
                case Constantes.DESCARGARINFO:
                    respuestaDescargarInfo(ok, respuestaServer);
                    break;
                default:
                    break;
            }
        } catch (Exception exception) {
            Log.e("RespSync", exception.getMessage(), exception);
        }
    }

    private void respuestaEnviarInformacion(boolean ok, String respuestaServer) {
        if (ok) {
            Log.i("BackgroundTaskTest", "✅ Envio de informacion en segundo plano correctamente.");
            envioExitoso = true; // Marcar el envío como exitoso
            iniciarDescarga();
        } else {
            Log.e("BackgroundTaskTest", "❌ Error en el envio de informacion en segundo plano: " + respuestaServer);
            envioExitoso = false; // Marcar el envío como fallido
        }
        latch.countDown(); // Liberar el CountDownLatch
    }

    private void iniciarDescarga() {
        Gson gson = new Gson();
        String stringJsonObject = PreferencesUsuario.obtenerUsuario(this.getApplicationContext());
        Usuario usuarioApp = gson.fromJson(stringJsonObject, Usuario.class);
        Sync sync = new Sync(MyWorker.this, Constantes.DESCARGARINFO);
        sync.user = usuarioApp.codigo;
        sync.password = usuarioApp.contrasena;
        sync.start();
    }

    private void respuestaDescargarInfo(boolean ok, String respuestaServer) {
        if (ok) {
            Log.i("BackgroundTaskTest", "✅ Informacion actualizada en segundo plano correctamente.");
        } else {
            Log.e("BackgroundTaskTest", "❌ Error actualizando informacion en segundo plano: " + respuestaServer);
        }
    }
}