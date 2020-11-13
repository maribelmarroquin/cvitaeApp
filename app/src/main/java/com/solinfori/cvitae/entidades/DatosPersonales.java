package com.solinfori.cvitae.entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.Date;

public class DatosPersonales {
    private int id_datos;
    private Bitmap imagen;
    private String dato;
    private String nombreImagen;
    private String nombre;
    private String profesion;
    private Date fechaNacimiento;
    private String lugarNacimiento;
    private String estadoCivil;
    private String domicilio;
    private String telefono;
    private String email;
    private String sitioWeb;

    public int getId_datos() {
        return id_datos;
    }

    public void setId_datos(int id_datos) {
        this.id_datos = id_datos;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
        try {
            byte[] byteCode = Base64.decode(dato, Base64.DEFAULT);
            //this.imagen = BitmapFactory.decodeByteArray(byteCode, 0, byteCode.length);

            int min_ancho = 150;
            int min_alto = 150;

            int max_ancho = 200;
            int max_alto = 200;

            Bitmap foto = BitmapFactory.decodeByteArray(byteCode, 0, byteCode.length);

            int ancho = foto.getWidth();
            int alto = foto.getHeight();

            if(ancho<=min_ancho && alto<=min_alto){
                this.imagen = Bitmap.createScaledBitmap(foto, ancho, alto, true);
            }
            else{
                int nuevoAlto = nuevoAlto(ancho, alto);
                if(nuevoAlto<=max_alto){
                    this.imagen = Bitmap.createScaledBitmap(foto, min_ancho, nuevoAlto, true);
                }else {
                    int nuevoAncho = nuevoAncho(ancho, alto);
                    this.imagen = Bitmap.createScaledBitmap(foto, nuevoAncho, max_alto, true);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int nuevoAncho(int ancho, int alto){
        int porcentaje = (200*100)/alto;
        int nuevoAncho= (ancho*porcentaje)/100;
        return nuevoAncho;
    }

    private int nuevoAlto(int ancho, int alto){
        int porcentaje = (150*100)/ancho;

        int nuevoAlto = (alto*porcentaje)/100;
        return nuevoAlto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getLugarNacimiento() {
        return lugarNacimiento;
    }

    public void setLugarNacimiento(String lugarNacimiento) {
        this.lugarNacimiento = lugarNacimiento;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }
}
