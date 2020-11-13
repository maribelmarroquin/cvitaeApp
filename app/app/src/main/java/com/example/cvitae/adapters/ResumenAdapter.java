package com.example.cvitae.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cvitae.R;
import com.example.cvitae.entidades.Resumen;
import com.example.cvitae.fragments.edicion.ResumenEdicionFragment;

import java.util.List;

public class ResumenAdapter extends RecyclerView.Adapter<ResumenAdapter.ResumenHolder> {

    List<Resumen> listaResumen;
    boolean isLargeLayout;
    private Context context;

    public ResumenAdapter(List<Resumen> listaResumen) {
        this.listaResumen = listaResumen;
    }

    @NonNull
    @Override
    public ResumenHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_resumen, viewGroup, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);
        return new ResumenHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResumenHolder resumenHolder, final int i) {

        int[] alternador_colores = resumenHolder.itemView.getContext().getResources().getIntArray(R.array.alternador_colores);

        resumenHolder.imageView.setBackgroundColor(alternador_colores[i]);

        resumenHolder.txtNumero.setText(String.valueOf(i + 1));
        resumenHolder.txtTitulo.setText(listaResumen.get(i).getTitulo());
        resumenHolder.txtResumen.setText(listaResumen.get(i).getResumen());

        if (listaResumen.get(i).getVer_pdf().equals("OK")) {
            resumenHolder.txtVerPDF.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle_black_24dp, 0, 0, 0);
        } else {
            resumenHolder.txtVerPDF.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_black_24dp, 0, 0, 0);
        }

        if (listaResumen.get(i).getVer_pagina().equals("OK")) {
            resumenHolder.txtVerPagina.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle_black_24dp, 0, 0, 0);
        } else {
            resumenHolder.txtVerPagina.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_black_24dp, 0, 0, 0);
        }

        resumenHolder.btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new ResumenEdicionFragment(listaResumen.get(i));
                newFragment.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "missiles");
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaResumen.size();
    }

    public class ResumenHolder extends RecyclerView.ViewHolder {

        LinearLayout expandibleView;
        Button arrowBtn;
        ImageButton btnActualizar, btnEliminar;
        CardView cardView;
        ImageView imageView;

        TextView txtNumero, txtTitulo, txtResumen, txtVerPDF, txtVerPagina;

        public ResumenHolder(@NonNull View itemView) {
            super(itemView);

            isLargeLayout = itemView.getResources().getBoolean(R.bool.large_layout);

            expandibleView = itemView.findViewById(R.id.resumenLayoutExpandible);
            arrowBtn = itemView.findViewById(R.id.btn_resumenExpandir);
            cardView = itemView.findViewById(R.id.cards);
            imageView = itemView.findViewById(R.id.imagenTargetResumen);

            arrowBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    if (expandibleView.getVisibility() == View.GONE) {
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        expandibleView.setVisibility(View.VISIBLE);
                        arrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
                    } else {
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        expandibleView.setVisibility(View.GONE);
                        arrowBtn.setBackgroundResource(R.drawable.ic_arrow_drop_down_black_24dp);
                    }
                }
            });

            txtNumero = itemView.findViewById(R.id.txtLResumenNo);
            txtTitulo = itemView.findViewById(R.id.txtLResumenTitulo);
            txtResumen = itemView.findViewById(R.id.txtLResumen);
            txtVerPDF = itemView.findViewById(R.id.txtLResumenPDF);
            txtVerPagina = itemView.findViewById(R.id.txtLResumenPagina);
            btnActualizar = itemView.findViewById(R.id.btn_resumenEditar);
            btnEliminar = itemView.findViewById(R.id.btn_resumenEliminar);
        }
    }
}
