package com.solinfori.cvitae.interfaces;


import com.solinfori.cvitae.fragments.ResumenFragment;
import com.solinfori.cvitae.fragments.consulta.ResumenConsultaFragment;
import com.solinfori.cvitae.fragments.formularios.DatosPersonalesFormularioFragment;
import com.solinfori.cvitae.fragments.formularios.ResumenFormularioFragment;

public interface IFragments extends ResumenFragment.OnFragmentInteractionListener,
        ResumenFormularioFragment.OnFragmentInteractionListener,
        ResumenConsultaFragment.OnFragmentInteractionListener,
        DatosPersonalesFormularioFragment.OnFragmentInteractionListener {
}
