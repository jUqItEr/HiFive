package com.example.hifive.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.location.GnssAntennaInfo.Listener
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hifive.R
import com.example.hifive.data.RetrofitClient
import com.example.hifive.data.model.CardData
import com.example.hifive.ui.activity.CardListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

// todo dialog 제작
class CardDialog(context: Activity, id: String) : Dialog(context), View.OnClickListener {
    protected var data = CardData("","",0, "", "")
    protected var id = id
    protected var context = context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_card)

        val closeButton = findViewById<ImageButton>(R.id.btn_close)
        closeButton.setOnClickListener(this)

        val representButton = findViewById<Button>(R.id.representButton)
        representButton.setOnClickListener(this)

        val deleteButton = findViewById<Button>(R.id.btn_delete)
        deleteButton.setOnClickListener(this)

        setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // 뒤로가기 키가 눌렸을 때 수행할 동작
                (context as CardListActivity).loadList(id)
                dismiss() // 다이얼로그 닫기
                true
            } else {
                false
            }
        }
    }

    class Builder(context: Activity, id: String){
        private val dialog = CardDialog(context, id)

        fun setView(data: CardData): CardDialog {
            val name = dialog.findViewById<TextView>(R.id.card_name)
            val number = dialog.findViewById<TextView>(R.id.card_num)
            val img = dialog.findViewById<ImageView>(R.id.img_card)
            val represent = dialog.findViewById<Button>(R.id.representButton)

            dialog.data = data
            dialog.setCanceledOnTouchOutside(false)

            Log.d("data", "${data}")

            number.text = "${data.card_num.substring(0,4)} - " +
                    "${data.card_num.substring(4,8)} - " +
                    "${data.card_num.substring(8,12)} - " +
                    "${data.card_num.substring(12,16)}"

            if(data.pay_card == 1) {
                represent.text = "대표카드"
                represent.isEnabled = false
                name.text = "★ ${data.card_name} ★"
            } else {
                name.text = "${data.card_name}"
            }

            if (data.url != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val inputStream = URL(data.url).openStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        withContext(Dispatchers.Main) {
                            img.setImageBitmap(bitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }



            return dialog
        }

        fun show(): CardDialog {
            dialog.show()
            return dialog
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_close -> {
                // 확인버튼
                (context as CardListActivity).loadList(id)
                dismiss() // 다이얼로그 닫기
            }
            R.id.representButton -> {
                // 대표카드 설정 버튼
                CoroutineScope(Dispatchers.IO).launch {
                    val result = RetrofitClient.setRepresentCard(context, id, data.card_num)
                    if(result) {
                        launch(Dispatchers.Main) {
                            val representButton = findViewById<Button>(R.id.representButton)
                            representButton.isEnabled = false
                            representButton.text = "대표카드"
                        }
                    } else{
                        //실패
                    }
                }
                // 다이얼로그를 닫지 않음
            }
            R.id.btn_delete -> {
                // 카드 삭제 버튼
                CoroutineScope(Dispatchers.IO).launch {
                    val result = RetrofitClient.deleteCard(context, id, data.card_num)
                    // if success -> 카드 삭제
                    if(result) {
                        launch(Dispatchers.Main) {
                            // 삭제 성공 시 다이얼로그를 닫음
                            (context as CardListActivity).loadList(id)
                            dismiss()
                        }
                    } else{
                        //실패
                    }
                }
            }
        }
    }
}
