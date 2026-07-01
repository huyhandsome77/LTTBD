package com.example.appdatmon.ui.staff

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.adapter.StaffTableAdapter
import com.example.appdatmon.model.BillItem
import com.example.appdatmon.model.TableOrder
import java.text.DecimalFormat

class StaffOrderFragment : Fragment(R.layout.fragment_staff_quanlydonhang) {

    private lateinit var rvStaffTables: RecyclerView
    private lateinit var tableAdapter: StaffTableAdapter
    private val moneyFormat = DecimalFormat("#,###đ")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvStaffTables = view.findViewById(R.id.rvStaffTables)
        rvStaffTables.layoutManager = GridLayoutManager(context, 2) // Dạng lưới 2 cột siêu đẹp
        val mockData = listOf(
            TableOrder("1", "Bàn số 01", "Đang gọi món"),
            TableOrder("2", "Bàn số 02", "Đang chế biến"),
            TableOrder(
                "5", "Bàn số 05", "Chờ thanh toán", listOf(
                    BillItem("Lẩu Thái Hải Sản", 1, 180000.0),
                    BillItem("Bò Ba Chỉ Cuộn Nấm", 2, 70000.0)
                )
            )
        )

        tableAdapter = StaffTableAdapter(mockData) { selectedTable ->
            showBillingDialog(selectedTable) // Bấm nút là bật bảng chi tiết tính tiền liền
        }
        rvStaffTables.adapter = tableAdapter
    }

    // --- DIALOG XEM CHI TIẾT TÍNH TIỀN ---
    private fun showBillingDialog(table: TableOrder) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_billing_details)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Ánh xạ UI trong Dialog
        val tvTitle = dialog.findViewById<TextView>(R.id.tvDialogTitle)
        val tvTotalAmount = dialog.findViewById<TextView>(R.id.tvTotalAmount)
        val rgPaymentMethod = dialog.findViewById<RadioGroup>(R.id.rgPaymentMethod)
        val layoutCashInput = dialog.findViewById<LinearLayout>(R.id.layoutCashInput)
        val edtCustomerCash = dialog.findViewById<EditText>(R.id.edtCustomerCash)
        val tvReturnCash = dialog.findViewById<TextView>(R.id.tvReturnCash)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirmPayment)

        tvTitle.text = "Chi Tiết Hóa Đơn - ${table.tableName}"

        // Tính toán tổng tiền gom đơn theo bàn
        val subTotal = table.orderItems.sumOf { it.price * it.quantity }
        val discount = 20000.0 // Giả lập giảm giá voucher
        val finalTotal = if (subTotal > 0) subTotal - discount else 0.0
        tvTotalAmount.text = "Tổng thanh toán: ${moneyFormat.format(finalTotal)}"

        //  Chọn Chuyển khoản QR -> Ẩn nhập tiền mặt. Chọn Tiền mặt -> Hiện lại.
        rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbQR) {
                layoutCashInput.visibility = View.GONE
            } else {
                layoutCashInput.visibility = View.VISIBLE
            }
        }

        // Tự động tính tiền thừa trả khách khi Staff gõ chữ (doAfterTextChanged)
        edtCustomerCash.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val inputCash = s.toString().toDoubleOrNull() ?: 0.0
                if (inputCash >= finalTotal && finalTotal > 0) {
                    val returnCash = inputCash - finalTotal
                    tvReturnCash.text = "Tiền thừa trả khách: ${moneyFormat.format(returnCash)}"
                } else {
                    tvReturnCash.text = "Tiền thừa trả khách: 0đ"
                }
            }
        })

        // Bấm nút Xác Nhận Thanh Toán
        btnConfirm.setOnClickListener {
            dialog.dismiss()
            if (rgPaymentMethod.checkedRadioButtonId == R.id.rbQR) {
                showQRDialog(finalTotal, table.tableName) // Rẽ nhánh bật QR cho backend xử lý tiếp
            } else {
                Toast.makeText(context, "Thành công! Đã chuyển trạng thái Đã thanh toán", Toast.LENGTH_SHORT).show()
                table.status = "Đã thanh toán"
                tableAdapter.notifyDataSetChanged()
            }
        }

        dialog.show()
    }

    // --- DIALOG HIỂN THỊ QR TỰ ĐỘNG ---
    private fun showQRDialog(amount: Double, tableName: String) {
        val qrDialog = Dialog(requireContext())
        qrDialog.setContentView(R.layout.dialog_qr_payment)
        qrDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val tvQRTotal = qrDialog.findViewById<TextView>(R.id.tvQRTotal)
        val tvBankInfo = qrDialog.findViewById<TextView>(R.id.tvBankInfo)
        val btnCloseQR = qrDialog.findViewById<Button>(R.id.btnCloseQR)

        tvQRTotal.text = "Số tiền: ${moneyFormat.format(amount)}"
        tvBankInfo.text = "Ngân hàng: Vietcombank\nSTK: 05230500xxxx\nTên: NHA HANG APP DAT MON\nNội dung: Thanh toan $tableName"

        btnCloseQR.setOnClickListener { qrDialog.dismiss() }
        qrDialog.show()
    }
}