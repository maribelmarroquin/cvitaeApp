package com.example.cvitae.entidades;

public class Resumen {
    private int id_resumen;
    private String titulo;
    private String resumen;
    private String ver_pdf;
    private String ver_pagina;

    public Resumen() {
    }

    public int getId_resumen() {
        return id_resumen;
    }

    public void setId_resumen(int id_resumen) {
        this.id_resumen = id_resumen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getVer_pdf() {
        return ver_pdf;
    }

    public void setVer_pdf(String ver_pdf) {
        this.ver_pdf = ver_pdf;
    }

    public String getVer_pagina() {
        return ver_pagina;
    }

    public void setVer_pagina(String ver_pagina) {
        this.ver_pagina = ver_pagina;
    }

}
