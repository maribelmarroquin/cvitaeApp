package com.example.cvitae.fragments.edicion;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cvitae.R;
import com.example.cvitae.Tools.VolleySingleton;
import com.example.cvitae.entidades.Resumen;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class ResumenEdicionFragment extends DialogFragment {

    ProgressDialog progreso;
    StringRequest stringRequest;
    Context context;
    private int id;
    private Resumen resumen;
    private EditText txt_tituloEd, txt_resumenEd;
    private TextView txt_contadorEd;
    private CheckBox cb_verPdfEd, cb_verPaginaEe;

    public ResumenEdicionFragment(Resumen resumen) {
        this.resumen = resumen;
    }

    /*public void setId(int id){
        this.id = id;
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        context = inflater.getContext();

        View view = inflater.inflate(R.layout.fragment_resumen_edicion, null);
        txt_tituloEd = view.findViewById(R.id.txt_edTituloRes);
        txt_resumenEd = view.findViewById(R.id.txt_edResumenRes);
        cb_verPdfEd = view.findViewById(R.id.cb_edVerPdfRes);
        cb_verPaginaEe = view.findViewById(R.id.cb_edVerPaginaRes);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        updateResumenWebService();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ResumenEdicionFragment.this.getDialog().cancel();
                    }
                });

        cargarDatosResumen();

        return builder.create();
    }

    private void updateResumenWebService() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        String ip = getString(R.string.ip);

        String url = ip + "resumen/updater";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progreso.hide();
                Toast.makeText(context, "Actualización exitosa de resumen. ", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "No se estableció conexión." + error, Toast.LENGTH_SHORT).show();
                progreso.hide();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();

                parametros.put("Accept", "application/json");
                parametros.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI4IiwianRpIjoiNmRmM2RjN2NkMTU4Mzk5NzRkZDRkNDA2OTVmNzI1ZDNjM2YyZDg5NTJiMjM4NzNiZGYxMTVhMDdmZTZjNjUzOGVmZmY0NDZhNjgwYzZiZmYiLCJpYXQiOjE2MDMzMDk0ODAsIm5iZiI6MTYwMzMwOTQ4MCwiZXhwIjoxNjM0ODQ1NDgwLCJzdWIiOiIyIiwic2NvcGVzIjpbXX0.KnTXSqZjQ5NfS6KBegqwVXJ-c53HiQjuwIR5AfFWnlTUYbK09R55f_T-wKX3QARMsICDRQpZlxYuMtioP37k-kjxhEPpDq-D9hSCQ3QQ6eokoa5fVCihlcTKRrsQI1dlvl142uLbdwyYrH8EXUDkha7KA7P4grx0iiGs1ZohQrNlsQtImfb2McFcEGD9LfYxdDZuM9Kg2KK1vy3vHdr4RjyHohN73dZt5R2jTzFW7kxN4pOEEcWPXgSPMSjUdzccdFGltfI2jL8TudRKS8NcdBP3mzN1jjlnNree5vY5ss5BtjPEI_Yk4Iiepr26uJ2I25qK4K7InbPcEh9EC-02MaxIRuIzaRjnmYe4PgWoSFYH8HHkotjt80D4jtav4_qZM-QwIeAKfAyJuyfT30gSQSb067bGw2ZO3m45jLg4NMO0xRvhGFr0CTVCzOKk9fjdJzDIQymeRKAqXcSQnaAwExlknGA9pl1gr1sAPmH9fv8mRhdKBncGhYACYarooNJB-ydIJ9PfpWNaUE0X5t8x8gb6B5RPMvlqZL68khEIvCCNhxG2O43iEcOT-skPkxdFsZ4f0ISfZQfOtZ5cT6msls6ELng3jSAIslfzhvvBca8QrWX-5QQNs_ms1enylmixJb8tf2myhGCJru4qo7iZdYe7NgOIIU9L4fv3HreQ5e0");

                return parametros;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int id = resumen.getId_resumen();
                String titulo = txt_tituloEd.getText().toString();
                String resumen = txt_resumenEd.getText().toString();
                String verPdf, verPagina;

                if (cb_verPdfEd.isChecked()) {
                    verPdf = "yes";
                } else {
                    verPdf = "-";
                }

                if (cb_verPaginaEe.isChecked()) {
                    verPagina = "yes";
                } else {
                    verPagina = "-";
                }

                Map<String, String> parametros = new HashMap<>();

                parametros.put("id_resumen", String.valueOf(id));
                parametros.put("titulo", titulo);
                parametros.put("resumen", resumen);
                parametros.put("principal", verPdf);
                parametros.put("principal_vista", verPagina);


                return parametros;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void cargarDatosResumen() {
        txt_tituloEd.setText(resumen.getTitulo());
        txt_resumenEd.setText(resumen.getResumen());

        if (resumen.getVer_pdf().equals("OK")) {
            cb_verPdfEd.setChecked(true);
        } else {
            cb_verPdfEd.setChecked(false);
        }
        if (resumen.getVer_pagina().equals("OK")) {
            cb_verPaginaEe.setChecked(true);
        } else {
            cb_verPaginaEe.setChecked(false);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
