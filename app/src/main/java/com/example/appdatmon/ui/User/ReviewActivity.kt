package com.example.appdatmon

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdatmon.data.api.AuthManager
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Review
import com.example.appdatmon.data.api.ReviewCreateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewActivity : AppCompatActivity() {

    private lateinit var stars: List<ImageView>
    private lateinit var etUserPhone: EditText
    private lateinit var etReviewContent: EditText
    private lateinit var btnSubmit: Button
    
    private var selectedRating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        initViews()
        setupRatingLogic()
        checkLoginStatus()

        findViewById<ImageView>(R.id.btnBackReview).setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {
            submitReview()
        }
    }

    private fun initViews() {
        stars = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )
        etUserPhone = findViewById(R.id.etUserPhone)
        etReviewContent = findViewById(R.id.etReviewContent)
        btnSubmit = findViewById(R.id.btnSubmitReview)
    }

    private fun setupRatingLogic() {
        stars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                updateRating(index + 1)
            }
        }
    }

    private fun updateRating(rating: Int) {
        selectedRating = rating
        for (i in stars.indices) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.ic_star_fill)
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty)
            }
        }
    }

    private fun checkLoginStatus() {
        val userId = AuthManager.getUserId(this)
        if (userId == -1L) {
            etUserPhone.visibility = View.VISIBLE
        } else {
            etUserPhone.visibility = View.GONE
        }
    }

    private fun submitReview() {
        if (selectedRating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show()
            return
        }

        val phone = etUserPhone.text.toString().trim()
        val isGuest = etUserPhone.visibility == View.VISIBLE
        
        if (isGuest && phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            return
        }

        val content = etReviewContent.text.toString().trim()
        val userId = AuthManager.getUserId(this).let { if (it == -1L) null else it }

        val reviewRequest = Review(
            id = 0, // Sẽ được sinh bởi Backend
            userId = userId,
            phone = if (isGuest) phone else null,
            dishName = "Tổng quát", // Có thể mở rộng lấy theo món ăn cụ thể sau
            comment = content,
            rating = selectedRating
        )

        btnSubmit.isEnabled = false
        btnSubmit.text = "ĐANG GỬI..."

        RetrofitClient.reviewApi.createReview(reviewRequest).enqueue(object : Callback<ReviewCreateResponse> {
            override fun onResponse(call: Call<ReviewCreateResponse>, response: Response<ReviewCreateResponse>) {
                btnSubmit.isEnabled = true
                btnSubmit.text = "GỬI ĐÁNH GIÁ"
                
                if (response.isSuccessful) {
                    Toast.makeText(this@ReviewActivity, "Cảm ơn bạn đã gửi đánh giá!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@ReviewActivity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ReviewCreateResponse>, t: Throwable) {
                btnSubmit.isEnabled = true
                btnSubmit.text = "GỬI ĐÁNH GIÁ"
                Toast.makeText(this@ReviewActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
