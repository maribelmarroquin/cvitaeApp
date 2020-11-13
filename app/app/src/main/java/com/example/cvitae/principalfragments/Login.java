package com.example.cvitae.principalfragments;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cvitae.CVitaeActivity;
import com.example.cvitae.R;
import com.example.cvitae.Tools.ConexionSQLiteHelper;
import com.example.cvitae.Tools.Utilidades;
import com.example.cvitae.Tools.VolleySingleton;
import com.example.cvitae.entidades.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {
    EditText email, password;
    Button iniciar;
    CheckBox recordarSesion;

    ProgressDialog progreso;
    JsonObjectRequest jsonObjectRequest;

    private OnFragmentInteractionListener mListener;

    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.txtLoginEmail);
        password = view.findViewById(R.id.txtLoginPassword);

        iniciar = view.findViewById(R.id.btnLoginIniciar);
        recordarSesion = view.findViewById(R.id.cbLoginRecordar);

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getContext(), "bd_cvitae", null, 1);
        if (recordarSesion.isChecked()) {

        }
        if (isExistent(conn)) {
            Intent intent = new Intent(getContext(), CVitaeActivity.class);
            startActivity(intent);
        } else {
            iniciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iniciarWebService();
                }
            });
        }


        return view;
    }

    private void iniciarWebService() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String ip = getString(R.string.ip);

        String url = ip + "login";

        System.out.println("------------Email: " + email.getText().toString());
        System.out.println("------------Password: " + password.getText().toString());

        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        Map<String, String> parametros = new HashMap<String, String>();

        parametros.put("email", emailString);
        parametros.put("password", passwordString);

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(parametros), this, this);

        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }


    private void registrarSesion(Usuario usuario) {
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getContext(), "bd_cvitae", null, 1);
        if (isExistent(conn)) {
            Toast.makeText(getContext(), "¡Hola!", Toast.LENGTH_LONG).show();
            Log.i("------USUARIO-----", "Usuario ya extá registrado en la base de datos de SQLITE");
        } else {
            SQLiteDatabase db = conn.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(Utilidades.CAMPO_ID, "");
            contentValues.put(Utilidades.CAMPO_NAME, usuario.getName());
            contentValues.put(Utilidades.CAMPO_EMAIL, usuario.getEmail());
            contentValues.put(Utilidades.CAMPO_TOKEN, usuario.getToken());


            Long idResultante = db.insert(Utilidades.TABLA_USUARIO, Utilidades.CAMPO_ID, contentValues);
            Toast.makeText(getContext(), "Id Usuario: " + idResultante, Toast.LENGTH_LONG).show();
            Log.i("------USUARIO-----", "Usuario Registrado en SQLITE");
        }

        conn.close();
    }

    private boolean isExistent(ConexionSQLiteHelper conn) {
        boolean existe;

        SQLiteDatabase dbConsulta = conn.getReadableDatabase();
        String[] parametro = {email.getText().toString()};
        String[] campos = {Utilidades.CAMPO_EMAIL, Utilidades.CAMPO_NAME};

        Cursor cursor = dbConsulta.query(Utilidades.TABLA_USUARIO, campos, Utilidades.CAMPO_EMAIL + "=?", parametro, null, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() != 0) {
            existe = true;
        } else {
            existe = false;
        }
        cursor.close();
        return existe;
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onErrorResponse(VolleyError error) {

        JSONObject jsonObject = null;
        try {
            String s = new String(error.networkResponse.data, StandardCharsets.UTF_8);
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("---------ERROR-------ER", "\n---------" + jsonObject.optString("message") + "\n---------");
        Toast.makeText(getContext(), "ACCESO DENEGADO: " + jsonObject.optString("message"), Toast.LENGTH_LONG).show();

        progreso.hide();
    }

    @Override
    public void onResponse(JSONObject response) {

        if (response.optString("success").equals("true")) {
            progreso.hide();

            String token = response.optString("token");

            JSONObject jsonObject = response.optJSONObject("user");

            System.out.println(jsonObject.toString());

            Usuario usuario = new Usuario();
            usuario.setName(jsonObject.optString("name"));
            usuario.setEmail(jsonObject.optString("email"));
            usuario.setConfirmed(jsonObject.optInt("confirmed"));
            usuario.setToken(token);

            registrarSesion(usuario);
            Intent intent = new Intent(getContext(), CVitaeActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(getContext(), "Error : " + response.opt("message"), Toast.LENGTH_LONG).show();
            Log.i("----------ERROR-------R", "\n---------" + response.opt("message") + "\n---------");
            progreso.hide();
        }

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
