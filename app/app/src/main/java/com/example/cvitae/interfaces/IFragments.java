package com.example.cvitae.interfaces;

import com.example.cvitae.fragments.ResumenFragment;
import com.example.cvitae.fragments.consulta.ResumenConsultaFragment;
import com.example.cvitae.fragments.formularios.DatosPersonalesFormularioFragment;
import com.example.cvitae.fragments.formularios.ResumenFormularioFragment;

public interface IFragments extends ResumenFragment.OnFragmentInteractionListener,
        ResumenFormularioFragment.OnFragmentInteractionListener,
        ResumenConsultaFragment.OnFragmentInteractionListener, DatosPersonalesFormularioFragment.OnFragmentInteractionListener {
}
