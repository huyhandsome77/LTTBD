package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appdatmon.R
import com.example.appdatmon.data.api.PointRequest
import com.example.appdatmon.data.api.PointResponse
import com.example.appdatmon.data.api.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminPointsFragment : Fragment() {

    private lateinit var etPhone: EditText
    private lateinit var etOrderId: EditText
    private lateinit var btnAddPoints: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_points, container, false)
        
        etPhone = view.findViewById(R.id.etPointPhone)
        etOrderId = view.findViewById(R.id.etOrderId)
        btnAddPoints = view.findViewById(R.id.btnAddPoints)

        btnAddPoints.setOnClickListener {
            processAddPoints()
        }

        return view
    }

    private fun processAddPoints() {
        val phone = etPhone.text.toString().trim()
        val orderIdStr = etOrderId.text.toString().trim()

        if (phone.isEmpty() || orderIdStr.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val orderId = orderIdStr.toLongOrNull()
        if (orderId == null) {
            Toast.makeText(context, "Mã hóa đơn không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        btnAddPoints.isEnabled = false
        btnAddPoints.text = "ĐANG XỬ LÝ..."

        val request = PointRequest(phone, orderId)
        
        RetrofitClient.pointApi.addPoints(request).enqueue(object : Callback<PointResponse> {
            override fun onResponse(call: Call<PointResponse>, response: Response<PointResponse>) {
                btnAddPoints.isEnabled = true
                btnAddPoints.text = "XÁC NHẬN TÍCH ĐIỂM"

                if (response.isSuccessful) {
                    val body = response.body()
                    Toast.makeText(context, body?.message ?: "Tích điểm thành công", Toast.LENGTH_LONG).show()
                    
                    // Clear fields on success
                    etPhone.text.clear()
                    etOrderId.text.clear()
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        val jObj = JSONObject(errorBody ?: "")
                        jObj.getString("message")
                    } catch (e: Exception) {
                        "Lỗi: ${response.code()}"
                    }
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PointResponse>, t: Throwable) {
                btnAddPoints.isEnabled = true
                btnAddPoints.text = "XÁC NHẬN TÍCH ĐIỂM"
                Toast.makeText(context, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
