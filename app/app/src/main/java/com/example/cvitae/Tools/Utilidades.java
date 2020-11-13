package com.example.cvitae.Tools;

public class Utilidades {

    //Constantes de la base de datos.
    public static final String TABLA_USUARIO = "usuario";
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_NAME = "name";
    public static final String CAMPO_EMAIL = "email";
    public static final String CAMPO_TOKEN = "token";
    public static final String CREAR_TABLA_USUARIO = "CREATE TABLE " + TABLA_USUARIO + " (" + CAMPO_ID + " INTEGER," + CAMPO_NAME + " TEXT, " + CAMPO_EMAIL + " TEXT, " + CAMPO_TOKEN + " TEXT)";
    //Constantes para la rotación del teléfono
    public static int rotacion = 0;
    public static boolean validaPantalla = true;

}
