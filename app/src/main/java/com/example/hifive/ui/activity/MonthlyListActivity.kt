package com.example.hifive.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isNotEmpty
import com.example.hifive.R
import com.example.hifive.data.RetrofitClient
import com.example.hifive.data.model.SpentListRequest
import com.example.hifive.databinding.ActivityMonthlyListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class MonthlyListActivity : AppCompatActivity() {
    private var bundle: Bundle? = null
    private val cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH) + 1
    // CoroutineScope 변수 선언
    var loadTableJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // bundle data 호출
        bundle = intent.extras?.getBundle("user")

        val binding = ActivityMonthlyListBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //Log.d("date", "${year} + ${month} + ${cal.get(Calendar.DATE)}")
        // initcomponent
        binding.month.text = year.toString()+"년 "+month+"월"
        loadTable()

        // 이전달 호출
        binding.prevMonth.setOnClickListener {
            // 기존에 실행 중인 loadTable 코루틴이 있다면 취소
            loadTableJob?.cancel()

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
            try {
                loadTable()
            } catch (e : Exception){
                Log.e("error", "${e.message}")
            }
        }

        // 다음달 호출
        binding.nextMoth.setOnClickListener {
            // 기존에 실행 중인 loadTable 코루틴이 있다면 취소
            loadTableJob?.cancel()

            // 현재 날짜를 넘지 않는 경우
            if(year!=cal.get(Calendar.YEAR) || month!=cal.get(Calendar.MONTH)+1) {
                if (month == 12) {
                    month = 1
                    year++
                } else {
                    month++
                }
                loadTable()
            }
            else{ // 현재 날짜를 넘는 경우
                Toast.makeText(this, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show()
            }
            binding.month.text = year.toString()+"년 "+month+"월"

        }
    }

    fun loadTable(){
        // 기존에 실행 중인 loadTable 코루틴이 있다면 취소
        loadTableJob?.cancel()

        //월별 데이터 값 가져오기
        // total 설정
        val total = findViewById<TextView>(R.id.total)
        // TableLayout 가져오기
        val tableLayout = findViewById<TableLayout>(R.id.table)

        loadTableJob = CoroutineScope(Dispatchers.IO).launch {
            tableLayout.setStretchAllColumns(true) // Add this line
            if(tableLayout.isNotEmpty())
                launch(Dispatchers.Main) {
                    tableLayout.removeAllViews()
                }

            //total text 초기화
            launch(Dispatchers.Main) {
                total.text = "${0}"
            }

            // 서버 호출
            val paylistresponse = RetrofitClient.requestMonthData(SpentListRequest("${bundle?.getString("id")}",
                "${year}", "${month}")
            )

            if (paylistresponse != null) {
                Log.d("load list","${paylistresponse != null} / ${paylistresponse.success == true} / ${paylistresponse.data.size}")
            }


            // 리스트 출력
            if (paylistresponse != null && paylistresponse.success == true && paylistresponse.data.size != 0){
                launch(Dispatchers.Main) {
                    total.text = "${paylistresponse.total}"
                }
                val dataList = paylistresponse.data

                for (i in 0..dataList.size-1)  {
                    if(i==0){
                        val row0 = TableRow(this@MonthlyListActivity)
                        row0.layoutParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        row0.addView(createTextView("NO."))
                        row0.addView(createTextView("승차"))
                        row0.addView(createTextView("하차"))
                        row0.addView(createTextView("카드"))
                        row0.addView(createTextView("결제금액"))

                        launch(Dispatchers.Main) {
                            tableLayout.addView(row0)
                        }
                    }
                    val row = TableRow(this@MonthlyListActivity)
                    row.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1f
                    )


                    // 순번
                    row.addView(createTextView("${i+1}"))

                    // 승차
                    val ridingDate = dataList[i]?.date?.replace("T"," ")?.substring(5,10)
                    val ridingTime = dataList[i]?.date?.replace("T"," ")?.substring(11,19)
                    row.addView(createTextView("${ridingDate}\n${ridingTime}"))

                    // 하차
                    val quitDate = dataList[i]?.quit?.replace("T"," ")?.substring(5,10)
                    val quitTime = dataList[i]?.quit?.replace("T"," ")?.substring(11,19)
                    row.addView(createTextView("${quitDate}\n${quitTime}"))

                    // 카드
                    var part = dataList[i]?.card_name?:"N/A"
                    if(!part.equals("N/A") && part.contains("(")){
                        val partlist = part.split("(")
                        row.addView(createTextView("${partlist[0]}\n(${partlist[1]}"))
                    } else {
                        row.addView(createTextView(part))
                    }

                    // 결제금액
                    row.addView(createTextView("${dataList[i]?.fee?:"0"}원"))

                    launch(Dispatchers.Main) {
                        tableLayout.addView(row)
                    }

                }
            }else{
                val row0 = TableRow(this@MonthlyListActivity)
                row0.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )
                row0.addView(createTextView("NO."))
                row0.addView(createTextView("승차"))
                row0.addView(createTextView("하차"))
                row0.addView(createTextView("카드"))
                row0.addView(createTextView("결제금액"))

                launch(Dispatchers.Main) {
                    tableLayout.addView(row0)
                }
            }
        }
    }

    //TextView 만드는 함수
    fun createTextView(text: String): TextView {
        val textView = TextView(this@MonthlyListActivity)
        textView.text = text
        textView.setPadding(5,5,5,5)
        textView.gravity = Gravity.CENTER
        return textView
    }
}