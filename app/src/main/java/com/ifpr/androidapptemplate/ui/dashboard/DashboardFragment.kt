package com.ifpr.androidapptemplate.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ifpr.androidapptemplate.R
import com.ifpr.androidapptemplate.baseclasses.Item

class DashboardFragment : Fragment() {

    private lateinit var nomeEditText: EditText
    private lateinit var tempoEditText: EditText
    private lateinit var descricaoEditText: EditText
    private lateinit var itemImageView: ImageView

    private lateinit var salvarButton: Button
    private lateinit var selectImageButton: Button

    private var imageUri: Uri? = null

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        nomeEditText = view.findViewById(R.id.nomeRelogio)
        tempoEditText = view.findViewById(R.id.tempoAdministrador)
        descricaoEditText = view.findViewById(R.id.adminDescription)

        itemImageView = view.findViewById(R.id.image_item)
        salvarButton = view.findViewById(R.id.salvarItemButton)
        selectImageButton = view.findViewById(R.id.button_select_image)

        auth = FirebaseAuth.getInstance()

        selectImageButton.setOnClickListener {
            openFileChooser()
        }

        salvarButton.setOnClickListener {
            salvarItem()
        }

        return view
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun salvarItem() {
        val nome = nomeEditText.text.toString().trim()
        val tempo = tempoEditText.text.toString().trim().toIntOrNull()
        val descricao = descricaoEditText.text.toString().trim()

        if (nome.isEmpty() || tempo == null || descricao.isEmpty() || imageUri == null) {
            Toast.makeText(context, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            return
        }

        uploadImageToFirebase(nome, tempo, descricao)
    }

    private fun uploadImageToFirebase(nome: String, tempo: Int, descricao: String) {
        if (imageUri != null) {

            val inputStream = context?.contentResolver?.openInputStream(imageUri!!)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

                val item = Item(
                    base64Image = base64Image,
                    adminDescription = descricao,
                    tempoAdministrador = tempo,
                    nomeRelogio = nome
                )

                saveItemIntoDatabase(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data?.data != null
        ) {
            imageUri = data.data
            Glide.with(this).load(imageUri).into(itemImageView)
        }
    }

    private fun saveItemIntoDatabase(item: Item) {

        databaseReference = FirebaseDatabase.getInstance().getReference("itens")

        val itemId = databaseReference.push().key

        if (itemId != null) {
            databaseReference
                .child(auth.uid.toString())
                .child(itemId)
                .setValue(item)
                .addOnSuccessListener {
                    Toast.makeText(context, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                }
        }
    }
}