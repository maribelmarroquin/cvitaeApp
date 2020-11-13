package com.solinfori.cvitae.fragments.formularios;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.solinfori.cvitae.R;
import com.solinfori.cvitae.Tools.ConexionSQLiteHelper;
import com.solinfori.cvitae.Tools.VolleySingleton;
import com.solinfori.cvitae.entidades.Usuario;
import com.solinfori.cvitae.fragments.ResumenFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.solinfori.cvitae.dao.UsuariosDAO.getUsuario;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResumenFormularioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResumenFormularioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResumenFormularioFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private EditText titulo, resumen;
    private CheckBox verPdf, verPagina;
    private Button guardar, limpiar;

    ProgressDialog progressDialog;
    ConexionSQLiteHelper conn;
    JsonObjectRequest jsonObjectRequest;

    private OnFragmentInteractionListener mListener;

    public ResumenFormularioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResumenFormularioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResumenFormularioFragment newInstance(String param1, String param2) {
        ResumenFormularioFragment fragment = new ResumenFormularioFragment();
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
        View view = inflater.inflate(R.layout.fragment_resumen_formulario, container, false);

        titulo = view.findViewById(R.id.txt_regTituloRes);
        resumen = view.findViewById(R.id.txt_regResumenRes);
        verPdf = view.findViewById(R.id.cb_regVerPdfRes);
        verPagina = view.findViewById(R.id.cb_regVerPaginaRes);
        guardar = view.findViewById(R.id.btn_regGuardarRes);
        limpiar = view.findViewById(R.id.btn_regLimpiarRes);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createResumenWebService();
            }
        });

        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanFormulario();
                Toast.makeText(getContext(), "Registro cancelado.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void cleanFormulario() {
        titulo.setText("");
        resumen.setText("");
        verPagina.setChecked(false);
        verPdf.setChecked(false);
    }

    private void createResumenWebService() {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        String ip = getString(R.string.ip);

        String url = ip + "resumen/register";

        Usuario usuario = getUsuario(conn);
        final String token = usuario.getToken();

        Log.i("-----TOKEN-----", token);

        String emailString = titulo.getText().toString();
        String passwordString = resumen.getText().toString();
        String verPdfString, verPaginaString;

        if (verPdf.isChecked()) {
            verPdfString = "yes";
        } else {
            verPdfString = "-";
        }

        if (verPagina.isChecked()) {
            verPaginaString = "yes";
        } else {
            verPaginaString = "-";
        }

        Map<String, String> parametros = new HashMap<String, String>();

        parametros.put("titulo", emailString);
        parametros.put("resumen", passwordString);
        parametros.put("principal", verPdfString);
        parametros.put("principal_vista", verPaginaString);

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(parametros), this, this) {
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

            Log.i("---------ERROR-------RR", "\n---------" + jsonObjectErrors.optString("message") + "\n---------"+jsonObjectErrors);


            Toast.makeText(getContext(), "ATENCIÓN: " + jsonObject.optString("message")+"\n"+jsonObjectErrors.optString("titulo")+jsonObjectErrors.optString("resumen"), Toast.LENGTH_LONG).show();

        }


        progressDialog.hide();
    }

    @Override
    public void onResponse(JSONObject response) {
        cleanFormulario();
        Toast.makeText(getContext(), "Registro procesado correctamente.", Toast.LENGTH_SHORT).show();
        progressDialog.hide();

        Fragment fragment = new ResumenFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
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
