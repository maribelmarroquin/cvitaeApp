package com.solinfori.cvitae;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.solinfori.cvitae.Tools.ConexionSQLiteHelper;
import com.solinfori.cvitae.Tools.Utilidades;
import com.solinfori.cvitae.Tools.VolleySingleton;
import com.solinfori.cvitae.entidades.Usuario;
import com.solinfori.cvitae.fragments.AcercaDeFragment;
import com.solinfori.cvitae.fragments.ResumenFragment;
import com.solinfori.cvitae.fragments.formularios.DatosPersonalesFormularioFragment;
import com.solinfori.cvitae.interfaces.IFragments;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.solinfori.cvitae.dao.UsuariosDAO.deleteUsuario;
import static com.solinfori.cvitae.dao.UsuariosDAO.getUsuario;

public class CVitaeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IFragments, Response.ErrorListener, Response.Listener<JSONObject> {

    ProgressDialog progreso;
    JsonObjectRequest jsonObjectRequest;

    Fragment fragment;

    ConexionSQLiteHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createElementsForActivity();
    }

    /*
    @Override
    protected void onStart(){
        super.onStart();
        if (Utilidades.validaPantalla == true) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            Utilidades.validaPantalla = false;
        }
    }
    */

    @Override
    protected void onResume(){
        super.onResume();
        if (Utilidades.validaPantalla == true) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            Utilidades.validaPantalla = false;
        }
    }


    /*
    @Override
    protected void onRestart(){
        super.onRestart();
        if (Utilidades.validaPantalla == true) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            Utilidades.validaPantalla = false;
        }
    }
    */

    private void createElementsForActivity(){
        setContentView(R.layout.activity_cvitae);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (Utilidades.validaPantalla == true) {
            fragment = new ResumenFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
            Utilidades.validaPantalla = false;
        }

        conn = new ConexionSQLiteHelper(getApplicationContext(), "bd_cvitae", null, 1);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder myBulid = new AlertDialog.Builder(this);
            myBulid.setMessage("¿Deseas salir de la aplicación?");
            myBulid.setTitle("Mensaje");
            myBulid.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity(); System.exit(0);
                }
            });
            myBulid.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = myBulid.create();
            dialog.show();
        }
    }
/*
    public Dialog mostrarDialogOpciones() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("¿Desea salir de la aplicación?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Fragment fragment = null;

        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrar_sesion) {
            Usuario usuario = getUsuario(conn);
            eliminarSesion(usuario);
            deleteUsuario(conn, usuario);
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.acerca_de) {
            fragment = new AcercaDeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.acerca_de, fragment).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void eliminarSesion(Usuario usuario){
        Toast.makeText(getApplicationContext(), "Eliminando sesión.", Toast.LENGTH_LONG).show();

        String ip = getString(R.string.ip);

        String url = ip + "logout";

        System.out.println("------------Email de token a eliminar: " + usuario.getEmail());

        final String token = usuario.getToken();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, this, this){
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

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        boolean fragmentSelected = false;

        if (id == R.id.resumen_profesional) {
            fragment = new ResumenFragment();
            fragmentSelected = true;
        } else if (id == R.id.datos_personales) {
            fragment = new DatosPersonalesFormularioFragment();
            fragmentSelected = true;
        } else if (id == R.id.formacion_academica) {

        } else if (id == R.id.formacion_extracademica) {

        } else if (id == R.id.conocimientos) {

        } else if (id == R.id.experiencia_profesional) {

        } else if (id == R.id.otros_datos) {

        } else if (id == R.id.objetivo_profesional) {

        } else if (id == R.id.exportar) {

        } else if (id == R.id.configurar) {

        } else if (id == R.id.consultado) {

        }

        if (fragmentSelected == true) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Error al cerrar sesión: " + error.getMessage() + ": " + error.toString(), Toast.LENGTH_LONG).show();
        Log.i("------ERROR------", error.toString() + error.getMessage());
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(getApplicationContext(), response.optString("message"), Toast.LENGTH_LONG).show();
        Log.i("----Cerra Sesión------", response.optString("message"));
    }

    /*
    public static void refreshFragment(Fragment fragment){
        if (Utilidades.validaPantalla == true) {
            RefreshFragment refreshFragment = new RefreshFragment();
            refreshFragment.
            Utilidades.validaPantalla = false;
        }
    }
    */
}
