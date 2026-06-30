package com.example.appdatmon.ui.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.api.UploadResponse
import com.example.appdatmon.data.model.Product
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductFormFragment : Fragment() {

    private lateinit var edtName: EditText
    private lateinit var edtDescription: EditText
    private lateinit var edtPrice: EditText
    private lateinit var edtImage: EditText
    private lateinit var ivPreview: ImageView
    private lateinit var btnPickImage: Button
    private lateinit var cbIsAvailable: CheckBox
    private lateinit var btnSave: Button
    private lateinit var tvTitle: TextView
    
    private var categoryId: Long = -1
    private var productId: Long? = null
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let {
                ivPreview.setImageURI(it)
                uploadImageToBackend(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_form, container, false)

        edtName = view.findViewById(R.id.edtProductName)
        edtDescription = view.findViewById(R.id.edtProductDescription)
        edtPrice = view.findViewById(R.id.edtProductPrice)
        edtImage = view.findViewById(R.id.edtProductImage)
        ivPreview = view.findViewById(R.id.ivProductPreview)
        btnPickImage = view.findViewById(R.id.btnPickImage)
        cbIsAvailable = view.findViewById(R.id.cbIsAvailable)
        btnSave = view.findViewById(R.id.btnSave)
        tvTitle = view.findViewById(R.id.tvFormTitle)

        categoryId = arguments?.getLong("CATEGORY_ID", -1) ?: -1
        productId = if (arguments?.containsKey("PRODUCT_ID") == true) arguments?.getLong("PRODUCT_ID") else null

        if (productId != null) {
            tvTitle.text = "Sửa món ăn"
            loadProductDetails()
        }

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        btnSave.setOnClickListener { saveProduct() }
        view.findViewById<View>(R.id.btnCancel).setOnClickListener { parentFragmentManager.popBackStack() }
        view.findViewById<View>(R.id.tvBackTitle).setOnClickListener { parentFragmentManager.popBackStack() }

        return view
    }

    private fun loadProductDetails() {
        productId?.let { id ->
            RetrofitClient.productApi.getProductById(id).enqueue(object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if (response.isSuccessful) {
                        response.body()?.let { product ->
                            edtName.setText(product.name)
                            edtDescription.setText(product.description)
                            edtPrice.setText(product.price.toString())
                            edtImage.setText(product.image)
                            cbIsAvailable.isChecked = product.isAvailable
                            
                            uploadedImageUrl = product.image
                            if (!product.image.isNullOrEmpty()) {
                                val fullUrl = "http://10.0.2.2:3000${product.image}"
                                Glide.with(this@ProductFormFragment).load(fullUrl).into(ivPreview)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<Product>, t: Throwable) {}
            })
        }
    }

    private fun uploadImageToBackend(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            if (bytes != null) {
                val requestBody = RequestBody.create(MediaType.parse("image/*"), bytes)
                val body = MultipartBody.Part.createFormData("image", "upload.jpg", requestBody)
                
                Toast.makeText(requireContext(), "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show()
                
                RetrofitClient.uploadApi.uploadImage(body).enqueue(object : Callback<UploadResponse> {
                    override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                        if (response.isSuccessful) {
                            uploadedImageUrl = response.body()?.imageUrl
                            edtImage.setText(uploadedImageUrl)
                            Toast.makeText(requireContext(), "Tải ảnh thành công", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Lỗi tải ảnh lên server", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Lỗi kết nối khi tải ảnh", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveProduct() {
        val name = edtName.text.toString().trim()
        val description = edtDescription.text.toString().trim()
        val priceStr = edtPrice.text.toString().trim()
        val isAvailable = cbIsAvailable.isChecked

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tên và giá", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0

        val product = Product(
            id = productId,
            name = name,
            description = description,
            price = price,
            image = uploadedImageUrl, // Sử dụng URL ảnh đã upload
            isAvailable = isAvailable,
            categoryId = categoryId
        )

        val call = if (productId == null) {
            RetrofitClient.productApi.createProduct(product)
        } else {
            RetrofitClient.productApi.updateProduct(productId!!, product)
        }

        call.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Lưu thành công", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Lưu thất bại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
