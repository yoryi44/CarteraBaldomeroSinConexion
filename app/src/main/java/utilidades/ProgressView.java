package utilidades;

import android.app.ProgressDialog;
import android.content.Context;

import co.com.celuweb.carterabaldomero.R;

public  class ProgressView
{
    public static volatile ProgressView instance;
    private ProgressDialog progressDialog;

    private ProgressView()
    {
        if (instance != null)
        {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static ProgressView getInstance()
    {
        if (instance == null)
        { //if there is no instance available... create new one
            synchronized (ProgressView.class)
            {
                if (instance == null) instance = new ProgressView();
            }
        }

        return instance;
    }

    public void Show(Context context,String text)
    {
        Dismiss();

        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(context,R.style.PrimaryAccent);
            //progressDialog.setProgressStyle(R.style.PrimaryAccent);
            //progressDialog.setTitle("Espere un momento");

            progressDialog.setMessage(text);
            progressDialog.setCancelable(false);
        }

        progressDialog.show();
    }

    public void Dismiss()
    {
        if(progressDialog != null)
            progressDialog.dismiss();
            progressDialog=null;
    }


}
