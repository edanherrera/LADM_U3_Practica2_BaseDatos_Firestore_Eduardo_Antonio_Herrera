package mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia.databinding.ActivityMain2Binding
import mx.tecnm.tepic.ladm_u3_practica2_basedatos_firestore_eduardo_herrera_garcia.databinding.ActivityMain3Binding

class MainActivity3 : AppCompatActivity() {
    var idSeleccionado = ""
    lateinit var binding: ActivityMain3Binding
    val baseRemota = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        idSeleccionado = intent.extras!!.getString("idSeleccionado")!!

        baseRemota.collection("arrendamiento")
            .document(idSeleccionado)
            .get()
            .addOnSuccessListener {
                binding.nombre.setText(it.getString("nombre"))
                binding.domicilio.setText(it.getString("domicilio"))
                binding.licencia.setText(it.getString("licencia"))
                binding.modelo.setText(it.getString("modelo"))
                binding.marca.setText(it.getString("marca"))
                binding.fecha.setText(it.getString("fecha"))
                binding.kilometrage.setText(it.getLong("kilometrage").toString())
            }
            .addOnFailureListener{
                AlertDialog.Builder(this)
                    .setMessage(it.message)
                    .show()
            }
        binding.regresar.setOnClickListener {
            finish()
        }
        binding.actualizar.setOnClickListener {

            baseRemota.collection("auto")
                .whereEqualTo("marca",binding.marca.text.toString())
                .whereEqualTo("modelo",binding.modelo.text.toString())
                .addSnapshotListener { query, error ->
                    if (error!=null){
                        //SI HUBO ERROR!
                        //consul = true
                        AlertDialog.Builder(this)
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
                        Toast.makeText(this, "El modelo o marca no existe", Toast.LENGTH_LONG)
                            .show()
                    }else{
                        baseRemota.collection("arrendamiento")
                            .document(idSeleccionado)
                            .update("nombre",binding.nombre.text.toString(),
                                "domicilio",binding.domicilio.text.toString(),
                                "licencia",binding.licencia.text.toString(),
                                "fecha",binding.fecha.text.toString(),
                                "marca" , binding.marca.text.toString(),
                                "modelo" , binding.modelo.text.toString(),
                                "kilometrage" , binding.kilometrage.text.toString().toInt()
                            )
                            .addOnSuccessListener {
                                Toast.makeText(this, "EXITO SE ACTUALIZÓ", Toast.LENGTH_LONG)
                                    .show()
                            }
                            .addOnFailureListener{
                                AlertDialog.Builder(this)
                                    .setMessage(it.message)
                                    .show()
                            }
                        binding.nombre.setText("")
                        binding.domicilio.setText("")
                        binding.licencia.setText("")
                        binding.marca.setText("")
                        binding.modelo.setText("")
                        binding.fecha.setText("")
                        binding.kilometrage.setText("")
                    }
                }
        }
    }
}