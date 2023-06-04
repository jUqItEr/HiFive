package com.example.hifive.ui.activity

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.hifive.R
import com.example.hifive.data.RetrofitClient
import com.example.hifive.data.model.CardData
import com.example.hifive.databinding.ActivityCardListBinding
import com.example.hifive.databinding.ActivitySignupBinding
import com.example.hifive.ui.dialog.CardDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class CardListActivity : AppCompatActivity() {
    private var bundle: Bundle? = null
    private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCardListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras?.getBundle("user")
        id = bundle?.getString("id").toString()

        loadList(id)
    }

    fun loadList(id: String){
        val scrollLinear = findViewById<LinearLayout>(R.id.scrollLinear)
        scrollLinear.removeAllViews()
        CoroutineScope(Dispatchers.IO).launch {
            val result = RetrofitClient.getCardList(id)
            launch(Dispatchers.Main) {
                if (result != null) {
                    if (result.success) {
                        Log.d("result", "${result}")
                        if (result.card.size != 0) {
                            for (i in 0 until result.card.size) {
                                scrollLinear.addView(createDynamicViews(result.card[i]))
                            }
                        } else {
                            scrollLinear.addView(
                                createDynamicViews(
                                    CardData(
                                        "카드 데이터 없음",
                                        "",
                                        0,
                                        "",
                                        ""
                                    )
                                )
                            )
                        }
                    } else {
                        Log.d("result ${result.success}", "connection error")
                    }
                    Log.d("result ${result.success}", "result null")
                }
            }
        }
    }

    fun createDynamicViews(data: CardData): LinearLayout {
        val linearLayout = LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.HORIZONTAL

        // 이미지뷰 카드이미지
        val imageView = ImageView(this)
        imageView.layoutParams = LinearLayout.LayoutParams(
            300,
            100,
            1f
        )
        if (data.url != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val inputStream = URL(data.url).openStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(bitmap)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // 텍스트뷰 카드이름
        val textView = TextView(this)
        textView.layoutParams = LinearLayout.LayoutParams(
            500,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            3f
        )
        textView.text = "${data.card_name}"

        // 버튼 상세보기
        val button = Button(this)
        button.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        if (data.pay_card == 1) {
            button.text = "★"
        } else {
            button.text = "⫶"
        }
        button.setOnClickListener {
            // 버튼 클릭 이벤트 처리
            //data
            val builder = CardDialog.Builder(this, id)

            builder.show()
            builder.setView(data)
            Log.d("카드이름", "${data.card_name}")
        }

        // LinearLayout에 위젯 추가
        linearLayout.addView(imageView)
        linearLayout.addView(textView)
        linearLayout.addView(button)

        // Activity의 레이아웃에 동적으로 생성된 LinearLayout 추가
        return linearLayout
    }
}


