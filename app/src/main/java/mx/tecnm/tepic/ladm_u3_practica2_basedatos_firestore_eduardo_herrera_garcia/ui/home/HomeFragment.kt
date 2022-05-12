package mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia.ui.home

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia.MainActivity2
import mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia.MainActivity3
import mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    val baseRemota = FirebaseFirestore.getInstance()
    var listaID = ArrayList<String>()
    var consul = false
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseFirestore.getInstance()
            .collection("arrendamiento")
            .addSnapshotListener { query, error ->
                if (error!=null){
                    //SI HUBO ERROR!
                    AlertDialog.Builder(this.requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener
                }

                val arreglo = ArrayList<String>()
                listaID.clear()
                for (documento in query!!){
                    var cadena = "Nombre: ${documento.getString("nombre")}\n" +
                            "Domicilio:  ${documento.getString("domicilio")} \nModelo: ${documento.getString("modelo")}\n" +
                            "Marca: ${documento.getString("marca")}"
                    arreglo.add(cadena)

                    listaID.add(documento.id.toString())
                }

                binding.lista.adapter= ArrayAdapter<String>(this.requireContext(),
                    R.layout.simple_list_item_1, arreglo)
                binding.lista.setOnItemClickListener { adapterView, view, posicion, l ->
                    val idSeleccionado = listaID.get(posicion)

                    AlertDialog.Builder(this.requireContext())
                        .setTitle("ATENCIÓN")
                        .setMessage("¿Qué deseas hacer con ID: ${idSeleccionado}?")
                        .setPositiveButton("ELIMINAR"){d, i->
                            eliminar(idSeleccionado)
                        }
                        .setNeutralButton("ACTUALIZAR"){d, i->
                            actualizar(idSeleccionado)
                        }
                        .setNegativeButton("CANCELAR"){d, i->}
                        .show()
                }
            }

        binding.insertar.setOnClickListener {
            //---------------------------------------------------CONSULTAAAAAAAAAAA---------------------------------
            baseRemota.collection("auto")
                .whereEqualTo("marca",binding.marca.text.toString())
                .whereEqualTo("modelo",binding.modelo.text.toString())
                .addSnapshotListener { query, error ->
                    if (error!=null){
                        //SI HUBO ERROR!
                        AlertDialog.Builder(this.requireContext())
                            .setMessage(error.message)
                            .show()
                        return@addSnapshotListener
                    }
                    val arreglo = ArrayList<String>()
                    for (documento in query!!){
                        arreglo.add(documento.getString("marca").toString())
                        arreglo.add(documento.getString("modelo").toString())
                    }
                        if( arreglo.size==0 ) {
                            Toast.makeText(this.requireContext(), "El modelo o marca no existe", Toast.LENGTH_LONG)
                                .show()
                        }else{
                            val datos = hashMapOf(
                                "nombre" to binding.nombre.text.toString(),
                                "domicilio" to binding.domicilio.text.toString(),
                                "licencia" to binding.licencia.text.toString(),
                                "marca" to binding.marca.text.toString(),
                                "modelo" to binding.modelo.text.toString(),
                                "kilometrage" to binding.kilometrage.text.toString().toInt()
                            )

                            baseRemota.collection("arrendamiento")
                                .add(datos)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this.requireContext(),
                                        "EXITO SE INSERTO",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                                .addOnFailureListener {
                                    AlertDialog.Builder(this.requireContext())
                                        .setMessage(it.message)
                                        .show()
                                }
                            binding.nombre.setText("")
                            binding.domicilio.setText("")
                            binding.licencia.setText("")
                            binding.marca.setText("")
                            binding.modelo.setText("")
                            binding.kilometrage.setText("")
                        }
                }
            //Toast.makeText(this.requireContext(), "SE ENCONTRÓ EL DOC ${consul}", Toast.LENGTH_LONG)
              //  .show()
        }
/*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    private fun actualizar(idSeleccionado: String) {
        var otraVentana = Intent(this.requireContext(), MainActivity3::class.java)

        otraVentana.putExtra("idSeleccionado",idSeleccionado)

        startActivity(otraVentana)
    }

    private fun eliminar(idSeleccionado: String) {
        baseRemota.collection("arrendamiento")
            .document(idSeleccionado)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this.requireContext(), "EXITO SE ELIMINó", Toast.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener {
                AlertDialog.Builder(this.requireContext())
                    .setMessage(it.message)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}