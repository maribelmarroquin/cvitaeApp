package com.example.cvitae.fragments.consulta;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cvitae.R;
import com.example.cvitae.Tools.VolleySingleton;
import com.example.cvitae.adapters.ResumenAdapter;
import com.example.cvitae.entidades.Resumen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    ArrayList<Resumen> listaResumen;
    ProgressDialog progressDialog;
    JsonObjectRequest jsonObjectRequest;

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
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resumen_consulta, container, false);
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

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, this, this) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI4IiwianRpIjoiNmRmM2RjN2NkMTU4Mzk5NzRkZDRkNDA2OTVmNzI1ZDNjM2YyZDg5NTJiMjM4NzNiZGYxMTVhMDdmZTZjNjUzOGVmZmY0NDZhNjgwYzZiZmYiLCJpYXQiOjE2MDMzMDk0ODAsIm5iZiI6MTYwMzMwOTQ4MCwiZXhwIjoxNjM0ODQ1NDgwLCJzdWIiOiIyIiwic2NvcGVzIjpbXX0.KnTXSqZjQ5NfS6KBegqwVXJ-c53HiQjuwIR5AfFWnlTUYbK09R55f_T-wKX3QARMsICDRQpZlxYuMtioP37k-kjxhEPpDq-D9hSCQ3QQ6eokoa5fVCihlcTKRrsQI1dlvl142uLbdwyYrH8EXUDkha7KA7P4grx0iiGs1ZohQrNlsQtImfb2McFcEGD9LfYxdDZuM9Kg2KK1vy3vHdr4RjyHohN73dZt5R2jTzFW7kxN4pOEEcWPXgSPMSjUdzccdFGltfI2jL8TudRKS8NcdBP3mzN1jjlnNree5vY5ss5BtjPEI_Yk4Iiepr26uJ2I25qK4K7InbPcEh9EC-02MaxIRuIzaRjnmYe4PgWoSFYH8HHkotjt80D4jtav4_qZM-QwIeAKfAyJuyfT30gSQSb067bGw2ZO3m45jLg4NMO0xRvhGFr0CTVCzOKk9fjdJzDIQymeRKAqXcSQnaAwExlknGA9pl1gr1sAPmH9fv8mRhdKBncGhYACYarooNJB-ydIJ9PfpWNaUE0X5t8x8gb6B5RPMvlqZL68khEIvCCNhxG2O43iEcOT-skPkxdFsZ4f0ISfZQfOtZ5cT6msls6ELng3jSAIslfzhvvBca8QrWX-5QQNs_ms1enylmixJb8tf2myhGCJru4qo7iZdYe7NgOIIU9L4fv3HreQ5e0");
                return headers;
            }
        };

        System.out.println("-----------------ERROR-----------------\n");
        System.out.println(jsonObjectRequest);
        System.out.println("---------------------------------------\n");

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

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error, nos se realizó la consulta: " + error.getMessage() + ": " + error.toString(), Toast.LENGTH_LONG).show();
        Log.i("------ERROR------", error.toString() + error.getMessage());
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
