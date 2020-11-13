package com.solinfori.cvitae.fragments.formularios;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.solinfori.cvitae.BuildConfig;
import com.solinfori.cvitae.R;
import com.solinfori.cvitae.Tools.ConexionSQLiteHelper;
import com.solinfori.cvitae.Tools.VolleySingleton;
import com.solinfori.cvitae.entidades.DatosPersonales;
import com.solinfori.cvitae.entidades.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.solinfori.cvitae.dao.UsuariosDAO.getUsuario;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatosPersonalesFormularioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DatosPersonalesFormularioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatosPersonalesFormularioFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private ImageView imagenId;
    private TextView nombreImagen;
    private EditText nombre, profesion, fechaNacimiento, lugarNacimiento, estadoCivil, domicilio, telefono, email, sitioWeb;
    private Button cargarImg, guardar, cancelar;

    private static final String CARPETA_RAIZ = "cvitae/";
    private static final String CARPETA_IMAGEN = "misImagenes";
    private static final String RUTA_IMAGEN = CARPETA_RAIZ+CARPETA_IMAGEN;
    private String path;
    File fileImage;
    Bitmap bitmap;

    private boolean existe;

    private ConexionSQLiteHelper conn;

    ProgressDialog progreso;
    JsonObjectRequest jsonObjectRequest;

    private static final int COD_SELECCIONA=10;
    private static final int COD_FOTO=20;

    private OnFragmentInteractionListener mListener;

    public DatosPersonalesFormularioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatosPersonalesFormularioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatosPersonalesFormularioFragment newInstance(String param1, String param2) {
        DatosPersonalesFormularioFragment fragment = new DatosPersonalesFormularioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        conn = new ConexionSQLiteHelper(getContext(), "bd_cvitae", null, 1);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_datos_personales_formulario, container, false);

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            /*imagenSinConexion.setVisibility(View.INVISIBLE);*/
            recibeCampos(view);
            getDatosPersonalesWebService();

        } else {
            /*imagenSinConexion.setVisibility(View.VISIBLE);*/
            Toast.makeText(getContext(), "No se pudo conectar, verifique el acceso a internet e intente de nuevo.", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    public void recibeCampos(View view){
        imagenId = view.findViewById(R.id.dp_imagen);
        nombreImagen = view.findViewById(R.id.dp_txtNombreArchivo);
        nombre = view.findViewById(R.id.dp_txtNombre);
        profesion = view.findViewById(R.id.dp_txtProfesion);
        fechaNacimiento = view.findViewById(R.id.dp_txtFechaNacimiento);
        lugarNacimiento = view.findViewById(R.id.dp_txtLugarNacimiento);
        estadoCivil = view.findViewById(R.id.dp_txtEstadoCivil);
        domicilio = view.findViewById(R.id.dp_txtDomicilio);
        telefono = view.findViewById(R.id.dp_txtTelefono);
        email = view.findViewById(R.id.dp_txtEmail);
        sitioWeb = view.findViewById(R.id.dp_txtSitioWeb);

        cargarImg = view.findViewById(R.id.dp_btnImagen);
        guardar = view.findViewById(R.id.dp_btnGuardar);
        cancelar = view.findViewById(R.id.dp_btnCancelar);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDatosPersonalesWebService();
            }
        });

        if(validaPermisos()){
            cargarImg.setEnabled(true);
        }else{
            cargarImg.setEnabled(false);
        }

        cargarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogOpciones();
            }
        });
    }

    private boolean getDatosPersonalesWebService(){

        String ip = getString(R.string.ip);
        String url = ip + "datos_personales/getter";

        Usuario usuario = getUsuario(conn);
        final String token = usuario.getToken();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /*progreso.hide();*/

                DatosPersonales datosPersonales = null;
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                Date fechaDate = null;

                try {
                    datosPersonales = new DatosPersonales();

                    datosPersonales.setNombre(response.optString("nombre"));
                    datosPersonales.setDato(response.optString("imagen"));
                    datosPersonales.setProfesion(response.optString("profesion"));

                    fechaDate = formato.parse(response.optString("fecha_nac"));
                    datosPersonales.setFechaNacimiento(fechaDate);

                    datosPersonales.setLugarNacimiento(response.optString("lugar_nac"));
                    datosPersonales.setEstadoCivil(response.optString("edo_civil"));
                    datosPersonales.setDomicilio(response.optString("direccion"));
                    datosPersonales.setTelefono(response.optString("telefono"));
                    datosPersonales.setEmail(response.optString("email"));
                    datosPersonales.setSitioWeb(response.optString("sitio"));

                    if (datosPersonales.getImagen()!= null) {
                        imagenId.setImageBitmap(datosPersonales.getImagen());
                    }else{
                        imagenId.setImageResource(R.drawable.imagen);
                    }
                    nombre.setText(datosPersonales.getNombre());
                    profesion.setText(datosPersonales.getProfesion());
                    fechaNacimiento.setText(datosPersonales.getFechaNacimiento().toString());
                    lugarNacimiento.setText(datosPersonales.getLugarNacimiento());
                    estadoCivil.setText(datosPersonales.getEstadoCivil());
                    domicilio.setText(datosPersonales.getDomicilio());
                    telefono.setText(datosPersonales.getTelefono());
                    email.setText(datosPersonales.getEmail());
                    sitioWeb.setText(datosPersonales.getSitioWeb());

                    existe = true;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                JSONObject jsonObject = null;
                String s = new String(error.networkResponse.data, StandardCharsets.UTF_8);

                if (s == null){
                    Toast.makeText(getContext(), "No se han podido consultar datos de usuario en la Base de datos. Intente más tarde.", Toast.LENGTH_LONG).show();
                }
                else{

                    try {

                        jsonObject = new JSONObject(s);
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "No se ha podido consultar datos de usuario en la Base de datos. Intente más tarde.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    JSONObject jsonObjectErrors = jsonObject.optJSONObject("errors");

                    Log.i("--------ERROR------DPR", "\n---------" + jsonObjectErrors.optString("message") + "\n---------"+jsonObjectErrors);


                    Toast.makeText(getContext(), "ATENCIÓN: " + jsonObject.optString("message")+"\n"+jsonObjectErrors.optString("titulo")+jsonObjectErrors.optString("resumen"), Toast.LENGTH_LONG).show();

                }


                progreso.hide();
                existe = false;
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
        };


        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        return existe;
    }

    private void createDatosPersonalesWebService() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String ip=getString(R.string.ip);

        String url=ip+"datos_personales/register";

        Usuario usuario = getUsuario(conn);
        final String token = usuario.getToken();

        String imagen = convertirImagenString(bitmap);

        Map<String, String> parametros = new HashMap<String, String>();
        parametros.put("ruta", imagen);
        parametros.put("nombre", nombre.getText().toString());
        parametros.put("profesion", profesion.getText().toString());
        parametros.put("fecha_nac", fechaNacimiento.getText().toString());
        parametros.put("lugar_nac", lugarNacimiento.getText().toString());
        parametros.put("edo_civil", estadoCivil.getText().toString());
        parametros.put("direccion", domicilio.getText().toString());
        parametros.put("telefono", telefono.getText().toString());
        parametros.put("email_u", email.getText().toString());
        parametros.put("sitio", sitioWeb.getText().toString());

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(parametros), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progreso.hide();
                if (response.optJSONObject("success").equals(true)) {
                    Toast.makeText(getContext(), "El registro se realizó exitosamente.", Toast.LENGTH_SHORT).show();

                } else {
                    System.out.println("\n ----------Response---------- \n");
                    System.out.println("" + response + "");
                    Toast.makeText(getContext(), "No se realizó registro." + response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                JSONObject jsonObject = null;
                String s = new String(error.networkResponse.data, StandardCharsets.UTF_8);

                if (s == null){
                    Toast.makeText(getContext(), "No se han podido consultar datos de usuario en la Base de datos. Intente más tarde.", Toast.LENGTH_LONG).show();
                }
                else{

                    try {

                        jsonObject = new JSONObject(s);
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "No se ha podido consultar datos de usuario en la Base de datos. Intente más tarde.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    JSONObject jsonObjectErrors = jsonObject.optJSONObject("errors");

                    Log.i("--------ERROR------DPR", "\n---------" + jsonObjectErrors.optString("message") + "\n---------"+jsonObjectErrors);


                    Toast.makeText(getContext(), "ATENCIÓN: " + jsonObject.optString("message")+"\n"+jsonObjectErrors.optString("titulo")+jsonObjectErrors.optString("resumen"), Toast.LENGTH_LONG).show();

                }


                progreso.hide();
            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer "+token);
                return headers;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private String convertirImagenString(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imagenByte = array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte, Base64.DEFAULT);
        return imagenString;
    }

    private boolean validaPermisos() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if ((getContext().checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED) &&
                (getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if ((shouldShowRequestPermissionRationale(CAMERA)) || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
        }

        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Permisos Desactivados");
        dialog.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la Aplicación");

        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==100){
            if (grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                cargarImg.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones = {"SI", "NO"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getContext());
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(opciones[which].equals("SI")){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "Los permisos no fueron aceptados", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void mostrarDialogOpciones() {
        final CharSequence[] opciones = {"Tomar Foto", "Cargar Imagen", "Cancelar"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getContext());
        alertOpciones.setTitle("Seleccione una opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(opciones[which].equals("Tomar Foto")){
                    /*Toast.makeText(getContext(), "TOMAR FOTO", Toast.LENGTH_SHORT).show();*/
                    abrirCamara();
                }else{
                    if (opciones[which].equals("Cargar Imagen")){
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent, "Selecciona la aplicacion"), COD_SELECCIONA);
                    }else{
                        dialog.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();
    }

    private void abrirCamara() {
        File file = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = file.exists();

        if (isCreada==false){
            isCreada = file.mkdirs();
        }
        if (isCreada==true){
            Long consecutivo = System.currentTimeMillis()/1000;
            String nombre = consecutivo.toString()+".jpg";

            path = Environment.getExternalStorageDirectory() + File.separator+CARPETA_RAIZ + File.separator + nombre;

            fileImage = new File(path);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
            {
                String authorities = BuildConfig.APPLICATION_ID+".provider";
                Uri imageUri= FileProvider.getUriForFile( getContext(),authorities,fileImage);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }else
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));
            }

            startActivityForResult(intent, COD_FOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case COD_SELECCIONA:
                Uri miPath = data.getData();
                imagenId.setImageURI(miPath);

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), miPath);
                    imagenId.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case COD_FOTO:
                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("Path", ""+path);
                    }
                });

                bitmap = BitmapFactory.decodeFile(path);
                imagenId.setImageBitmap(bitmap);

                break;
        }
        bitmap = redimensionarImagen(bitmap);
    }

    private Bitmap redimensionarImagen(Bitmap bitmap1) {
        int min_ancho = 350;
        int min_alto = 350;

        int max_ancho = 400;
        int max_alto = 400;

        int ancho = bitmap1.getWidth();
        int alto = bitmap1.getHeight();

        if(ancho<=min_ancho && alto<=min_alto){
            bitmap1.createScaledBitmap(bitmap, ancho, alto, true);
        }
        else{
            int nuevoAlto = nuevoAlto(ancho, alto);
            if(nuevoAlto<=max_alto){
                return bitmap1.createScaledBitmap(bitmap, min_ancho, nuevoAlto, true);
            }else {
                int nuevoAncho = nuevoAncho(ancho, alto);
                return bitmap1.createScaledBitmap(bitmap, nuevoAncho, max_alto, true);
            }

        }

        return null;
    }

    private int nuevoAncho(int ancho, int alto){
        int porcentaje = (400*100)/alto;
        int nuevoAncho= (ancho*porcentaje)/100;
        return nuevoAncho;
    }

    private int nuevoAlto(int ancho, int alto){
        int porcentaje = (350*100)/ancho;

        int nuevoAlto = (alto*porcentaje)/100;
        return nuevoAlto;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
