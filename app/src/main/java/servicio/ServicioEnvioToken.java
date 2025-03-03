package servicio;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Timer;
import java.util.TimerTask;

import businessObject.DataBaseBO;
import configuracion.Synchronizer;
import dataobject.Usuario;
import utilidades.Constantes;

public class ServicioEnvioToken extends Service implements Synchronizer {

    public long time =  10 * 30 * 1000;
    EnvioTokens envioTokens;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        iniciarServicioToken();
    }

    private void iniciarServicioToken() {

        Timer timer = new Timer();
        envioTokens = new EnvioTokens();
        timer.schedule(envioTokens, time);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        this.stopSelf();

        super.onDestroy();
    }

    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {
            Log.i("TokenEnviado", respuestaServer );
    }

    public class EnvioTokens extends TimerTask {

        @Override
        public void run() {
            if(holderEnviarToken!= null)
                holderEnviarToken.sendEmptyMessage(0);
        }
    }

    private Handler holderEnviarToken = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                enviarToken();
        }
    };

    private void enviarToken() {
        if (DataBaseBO.ExisteDataBase()) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.i("TokenR", refreshedToken);
            Usuario tokenUsuari = DataBaseBO.obtenerUsuario();
            if(!tokenUsuari.token.equals(refreshedToken)){
                Sync sync = new Sync(ServicioEnvioToken.this, Constantes.ENVIAR_TOKEN_NOTIFICATION);
                sync.token = refreshedToken;
                sync.user = tokenUsuari.codigo;
                sync.start();
            }

        }
    }


}
