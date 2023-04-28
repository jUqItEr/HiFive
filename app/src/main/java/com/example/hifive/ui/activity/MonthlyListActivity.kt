package com.example.hifive.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.example.hifive.R
import com.example.hifive.databinding.ActivityMonthlyListBinding
import java.util.*


class MonthlyListActivity : AppCompatActivity() {
    val cal = Calendar.getInstance()
    var year = cal.get(Calendar.YEAR)
    var month = cal.get(Calendar.MONTH) + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMonthlyListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("date", "${year} + ${month} + ${cal.get(Calendar.DATE)}")
        binding.month.text = year.toString()+"년 "+month+"월"
        loadTable()

        binding.prevMonth.setOnClickListener {
            //text view editing
            if(month == 1){
                month = 12
                year--
            }
            else{
                month--
            }
            binding.month.text = year.toString()+"년 "+month+"월"

            //load fun
            loadTable()
        }
        binding.nextMoth.setOnClickListener {
            //text view editing
            if(year!=cal.get(Calendar.YEAR) || month!=cal.get(Calendar.MONTH)+1) {
                if (month == 12) {
                    month = 1
                    year++
                } else {
                    month++
                }
            }
            else{
                Toast.makeText(this, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show()
            }
            binding.month.text = year.toString()+"년 "+month+"월"

            //load fun
            loadTable()
        }
    }

    fun loadTable(){
// total 설정
        val total = findViewById<TextView>(R.id.total)
        //total.text

// TableLayout 가져오기
        val tableLayout = findViewById<TableLayout>(R.id.table)
        tableLayout.removeAllViews()

//월별 데이터 값 가져오기
        //todo add load data, input year, month


// 테이블 내용 추가
        //todo for문 10을 받아온 데이터의 length로 변경
        for (i in 1..100) {
            val row = TableRow(this)
            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            val text1 = TextView(this)
            text1.text = "Row $i, NO."
            text1.setPadding(5, 5, 5, 5)
            row.addView(text1)

            val text2 = TextView(this)
            text2.text = "Row $i, 일시"
            text2.setPadding(5, 5, 5, 5)
            row.addView(text2)

            val text3 = TextView(this)
            text3.text = "Row $i, 승차"
            text3.setPadding(5, 5, 5, 5)
            row.addView(text3)

            val text4 = TextView(this)
            text4.text = "Row $i, 하차"
            text4.setPadding(5, 5, 5, 5)
            row.addView(text4)

            val text5 = TextView(this)
            text5.text = "Row $i, 결제금액"
            text5.setPadding(5, 5, 5, 5)
            row.addView(text5)

            tableLayout.addView(row)
        }
    }
}