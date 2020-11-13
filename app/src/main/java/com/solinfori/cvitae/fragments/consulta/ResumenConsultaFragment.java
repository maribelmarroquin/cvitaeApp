package com.solinfori.cvitae.fragments.consulta;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.solinfori.cvitae.R;
import com.solinfori.cvitae.Tools.ConexionSQLiteHelper;
import com.solinfori.cvitae.Tools.VolleySingleton;
import com.solinfori.cvitae.adapters.ResumenAdapter;
import com.solinfori.cvitae.entidades.Resumen;
import com.solinfori.cvitae.entidades.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.solinfori.cvitae.dao.UsuariosDAO.getUsuario;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResumenConsultaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResumenConsultaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResumenConsultaFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    RecyclerView recyclerView;
    Button actualizarRegistros;

    ArrayList<Resumen> listaResumen;
    ProgressDialog progressDialog;
    JsonObjectRequest jsonObjectRequest;

    ConexionSQLiteHelper conn;

    /*ImageView imagenSinConexion;*/
    private OnFragmentInteractionListener mListener;

    public ResumenConsultaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResumenConsultaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResumenConsultaFragment newInstance(String param1, String param2) {

        ResumenConsultaFragment fragment = new ResumenConsultaFragment();
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
        View view = inflater.inflate(R.layout.fragment_resumen_consulta, container, false);

        /*
        actualizarRegistros = view.findViewById(R.id.btn_resumenActualizarRegistros);
        actualizarRegistros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarRegistros();
            }
        });
*/

        listaResumen = new ArrayList<>();
        recyclerView = view.findViewById(R.id.idRecyclerResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);

        /*
        imagenSinConexion = view.findViewById(R.id.imagenSinConexion);
        imagenSinConexion.setVisibility(View.INVISIBLE);
        */

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            /*imagenSinConexion.setVisibility(View.INVISIBLE);*/
            cargarWebService();
        } else {
            /*imagenSinConexion.setVisibility(View.VISIBLE);*/
            Toast.makeText(getContext(), "No se pudo conectar, verifique el acceso a internet e intente de nuevo.", Toast.LENGTH_LONG).show();
        }


        return view;
    }

    private void cargarWebService() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Consultando...");
        progressDialog.show();

        String ip = getString(R.string.ip);
        String url = ip + "resumen/getter";

        Usuario usuario = getUsuario(conn);
        final String token = usuario.getToken();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, this, this) {
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

        /*
        System.out.println("-----------------ERROR-----------------\n");
        System.out.println(jsonObjectRequest);
        System.out.println("---------------------------------------\n");
        */
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
        String s = null;
        if(error.networkResponse.data.equals(null)){
            Toast.makeText(getContext(), "No se ha podido consultar datos de usuario en la Base de datos. Intente más tarde.", Toast.LENGTH_LONG).show();
        }
        else{
            s = new String(error.networkResponse.data, StandardCharsets.UTF_8);
        }


        if (s == null){
            Toast.makeText(getContext(), "No se ha podido consultar datos de usuario en la Base de datos. Intente más tarde.", Toast.LENGTH_LONG).show();
        }
        else{

            try {

                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                Toast.makeText(getContext(), "No se ha podido consultar datos de usuario en la Base de datos. Intente más tarde.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Log.i("---------ERROR-------ER", "\n---------" + jsonObject.optString("message") + "\n---------");
            Toast.makeText(getContext(), "ATENCIÓN: " + jsonObject.optString("message"), Toast.LENGTH_LONG).show();

        }


        progressDialog.hide();
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialog.hide();

        Resumen resumen = null;
        JSONArray jsonArray = response.optJSONArray("data");

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                resumen = new Resumen();
                JSONObject jsonObject = null;
                jsonObject = jsonArray.getJSONObject(i);

                resumen.setId_resumen(jsonObject.optInt("id_resumen"));
                resumen.setTitulo(jsonObject.optString("titulo"));
                resumen.setResumen(jsonObject.optString("resumen"));
                resumen.setVer_pdf(jsonObject.optString("principal"));
                resumen.setVer_pagina(jsonObject.optString("principal_vista"));
                listaResumen.add(resumen);
            }
            ResumenAdapter adapter = new ResumenAdapter(listaResumen);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No se ha podido establecer la conexion con el servidor: " + response, Toast.LENGTH_LONG).show();
        }
    }
/*
    public void actualizarRegistros(){
        Fragment fragment = new ResumenFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
    }
*/
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
