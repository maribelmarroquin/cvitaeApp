package com.solinfori.cvitae.fragments.edicion;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.solinfori.cvitae.R;
import com.solinfori.cvitae.Tools.ConexionSQLiteHelper;
import com.solinfori.cvitae.Tools.VolleySingleton;
import com.solinfori.cvitae.entidades.Resumen;
import com.solinfori.cvitae.entidades.Usuario;
import com.solinfori.cvitae.fragments.ResumenFragment;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.solinfori.cvitae.dao.UsuariosDAO.getUsuario;

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

    ConexionSQLiteHelper conn;

    public ResumenEdicionFragment(Resumen resumen) {
        this.resumen = resumen;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        conn = new ConexionSQLiteHelper(getContext(), "bd_cvitae", null, 1);
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
                        Fragment fragment = new ResumenFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
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

        Usuario usuario = getUsuario(conn);
        final String token = usuario.getToken();

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progreso.hide();
                Toast.makeText(context, "Actualización exitosa de resumen. ", Toast.LENGTH_SHORT).show();
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
                else {
                    /*Toast.makeText(getContext(), "No se estableció conexión." + error, Toast.LENGTH_SHORT).show();*/

                    /*JSONObject jsonObjectErrors = jsonObject.optJSONObject("errors");*/

                    Log.i("---------ERROR-------RR", "\n---------" + jsonObject.optString("message") + "\n---------" + jsonObject);


                    Toast.makeText(getContext(), "ATENCIÓN: " + jsonObject.optString("message") + "\n", Toast.LENGTH_LONG).show();


                    progreso.hide();
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> parametros = new HashMap<>();

                parametros.put("Accept", "application/json");
                parametros.put("Authorization", "Bearer "+token);
                return parametros;
            }

            @Override
            protected Map<String, String> getParams() {
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
