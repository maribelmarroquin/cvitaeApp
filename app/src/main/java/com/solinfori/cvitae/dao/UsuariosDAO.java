package com.solinfori.cvitae.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.solinfori.cvitae.Tools.ConexionSQLiteHelper;
import com.solinfori.cvitae.Tools.Utilidades;
import com.solinfori.cvitae.entidades.Usuario;

public class UsuariosDAO {

    public static Usuario getUsuario(ConexionSQLiteHelper conn){
        SQLiteDatabase dbConsulta = conn.getReadableDatabase();
        String[] campos = {Utilidades.CAMPO_NAME, Utilidades.CAMPO_EMAIL, Utilidades.CAMPO_TOKEN};

        Cursor cursor = dbConsulta.query(Utilidades.TABLA_USUARIO, campos, Utilidades.CAMPO_RECORDAR + "=1", null, null, null, null);
        cursor.moveToFirst();

        Usuario usuario;

        usuario = new Usuario();
        usuario.setName(cursor.getString(0));
        usuario.setEmail(cursor.getString(1));
        usuario.setToken(cursor.getString(2));

        cursor.close();
        return usuario;
    }

    public static Long createUsuario(Usuario usuario, ConexionSQLiteHelper conn, int recordar){

        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Utilidades.CAMPO_ID, "");
        contentValues.put(Utilidades.CAMPO_NAME, usuario.getName());
        contentValues.put(Utilidades.CAMPO_EMAIL, usuario.getEmail());
        contentValues.put(Utilidades.CAMPO_TOKEN, usuario.getToken());
        contentValues.put(Utilidades.CAMPO_RECORDAR, recordar);

        Long idResultante = db.insert(Utilidades.TABLA_USUARIO, Utilidades.CAMPO_ID, contentValues);

        conn.close();
        return idResultante;
    }

    public static void udpateUsuario(Usuario usuario, ConexionSQLiteHelper conn, String email, int recordar){

        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Utilidades.CAMPO_ID, "");
        contentValues.put(Utilidades.CAMPO_NAME, usuario.getName());
        contentValues.put(Utilidades.CAMPO_EMAIL, usuario.getEmail());
        contentValues.put(Utilidades.CAMPO_TOKEN, usuario.getToken());
        contentValues.put(Utilidades.CAMPO_RECORDAR, recordar);

        String[] parametros={email};
        db.update(Utilidades.TABLA_USUARIO, contentValues, Utilidades.CAMPO_EMAIL+"=?", parametros);

        conn.close();
    }

    public static void deleteUsuario(ConexionSQLiteHelper conn, Usuario usuario){
        SQLiteDatabase db = conn.getReadableDatabase();

        String[] parametro = {usuario.getToken()};
        db.delete(Utilidades.TABLA_USUARIO, Utilidades.CAMPO_TOKEN+"=?", parametro);

        conn.close();
    }

    public static boolean isExistent(ConexionSQLiteHelper conn, String email) {
        boolean existe;

        SQLiteDatabase dbConsulta = conn.getReadableDatabase();
        String[] parametro = {email};
        String[] campos = {Utilidades.CAMPO_EMAIL, Utilidades.CAMPO_NAME, Utilidades.CAMPO_RECORDAR};

        Cursor cursor = dbConsulta.query(Utilidades.TABLA_USUARIO, campos, Utilidades.CAMPO_EMAIL + "=?", parametro, null, null, null, null);
        cursor.moveToFirst();

        existe = cursor.getCount() != 0;
        cursor.close();
        return existe;
    }

    public static boolean isRemembered(ConexionSQLiteHelper conn){
        boolean recordado;

        SQLiteDatabase dbConsulta = conn.getReadableDatabase();
        String[] campos = {Utilidades.CAMPO_EMAIL, Utilidades.CAMPO_RECORDAR};

        Cursor cursor = dbConsulta.query(Utilidades.TABLA_USUARIO, campos, Utilidades.CAMPO_RECORDAR + "=1", null, null, null, null);
        cursor.moveToFirst();

        recordado = cursor.getCount() != 0;
        cursor.close();
        return recordado;
    }
}
