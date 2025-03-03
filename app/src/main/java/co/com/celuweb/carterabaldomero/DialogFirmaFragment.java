package co.com.celuweb.carterabaldomero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.graphics.Matrix;
import android.widget.TextView;

import java.util.Objects;

public class DialogFirmaFragment extends DialogFragment {

    private SignatureView signatureView;
    private OnSignatureDialogListener listener;
    String idPago;
    String empresa;
    String lenguajeElegido;

    //ELEMENTOS VISTA
    Button clearButton;
    Button saveButton;
    TextView tvTitle;

    public DialogFirmaFragment(String idPago, String empresa, String lenguajeElegido) {
        this.idPago = idPago;
        this.empresa = empresa;
        this.lenguajeElegido = lenguajeElegido;
    }

    public interface OnSignatureDialogListener {
        void onDialogDismissed(Bitmap signatureBitmap, String idPago, String empresa);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnSignatureDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MyDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_firma_fragment, container, false);

        signatureView = new SignatureView(getActivity(), null);
        FrameLayout frameLayout = view.findViewById(R.id.signature_view);
        frameLayout.addView(signatureView);

        init(view);

        return view;
    }

    public void init(View view)
    {
        initViewElements(view);
        language();
    }

    private void initViewElements(View view) {

        tvTitle = view.findViewById(R.id.tvTitle);

        clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clear();
            }
        });

        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {

            //CONVERTIR FIRMA EN BITMAP
            Bitmap signatureBitmap = signatureView.getSignatureBitmap();

            //RETORNAR AL METODO DE MetodosDePagoActivity
            listener.onDialogDismissed(signatureBitmap, idPago, empresa);
            dismiss();
        });
    }

    private void language() {
        if(lenguajeElegido.equals("USA"))
        {
            tvTitle.setText("Signature");
            clearButton.setText("clear");
            saveButton.setText("save");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            WindowManager.LayoutParams params = Objects.requireNonNull(getDialog().getWindow()).getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes(params);
        }
    }

    public static class SignatureView extends View {

        private Paint paint;
        private Path path;
        private Bitmap bitmap;
        private Canvas canvas;

        public SignatureView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(5f);

            path = new Path();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE); // Fondo blanco inicial
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(bitmap, 0, 0, null); // Dibuja el bitmap actual
            canvas.drawPath(path, paint); // Dibuja el path actual
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    canvas.drawPath(path, paint);
                    path.reset();
                    break;
                default:
                    return false;
            }

            invalidate();
            return true;
        }

        public void clear() {
            path.reset();
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE); // Fondo blanco
            invalidate();
        }

        public Bitmap getSignatureBitmap() {
            Bitmap signatureBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(signatureBitmap);
            canvas.drawColor(Color.WHITE); // Fondo blanco
            canvas.drawBitmap(bitmap, 0, 0, null);
            return rotateBitmap(signatureBitmap); // Rotar 90 grados para que quede horizontal
        }

        private Bitmap rotateBitmap(Bitmap source) {
            Matrix matrix = new Matrix();
            matrix.postRotate((float) -90);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }
    }
}