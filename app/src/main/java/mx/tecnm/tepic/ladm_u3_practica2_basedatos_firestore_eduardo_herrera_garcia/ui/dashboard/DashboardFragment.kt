package mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia.ui.dashboard

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
import mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    val baseRemota = FirebaseFirestore.getInstance()
    var listaID = ArrayList<String>()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
    /*
        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        FirebaseFirestore.getInstance()
            .collection("auto")
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
                    var cadena = "Marca: ${documento.getString("marca")}\n" +
                            "Modelo:  ${documento.getString("modelo")} \nKilometrage: ${documento.getLong("kilometrage")}"
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
            val datos = hashMapOf(
                "marca" to binding.marca.text.toString(),
                "modelo" to binding.modelo.text.toString(),
                "kilometrage" to binding.kilometrage.text.toString().toInt()
            )

            baseRemota.collection("auto")
                .add(datos)
                .addOnSuccessListener {
                    Toast.makeText(this.requireContext(), "EXITO SE INSERTO", Toast.LENGTH_LONG)
                        .show()
                }
                .addOnFailureListener{
                    AlertDialog.Builder(this.requireContext())
                        .setMessage(it.message)
                        .show()
                }
            binding.marca.setText("")
            binding.modelo.setText("")
            binding.kilometrage.setText("")
        }
        return root
    }

    private fun actualizar(idSeleccionado: String) {
        var otraVentana = Intent(this.requireContext(), MainActivity2::class.java)

        otraVentana.putExtra("idSeleccionado",idSeleccionado)

        startActivity(otraVentana)
    }

    private fun eliminar(idSeleccionado: String) {
        baseRemota.collection("auto")
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