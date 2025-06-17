package com.labs.lab_2_expense_tracker.ui
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.labs.lab_2_expense_tracker.R
import com.labs.lab_2_expense_tracker.data.TransactionStorage
import java.util.concurrent.TimeUnit

class StatsFragment : Fragment() {

    private lateinit var storage: TransactionStorage
    private lateinit var pieChart: PieChart
    private lateinit var periodSpinner: Spinner
    private var selectedPeriod: Period = Period.MONTH

    enum class Period(val label: String, val durationMillis: Long) {
        DAY("1 день", TimeUnit.DAYS.toMillis(1)),
        WEEK("1 неделя", TimeUnit.DAYS.toMillis(7)),
        MONTH("1 месяц", TimeUnit.DAYS.toMillis(30))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = TransactionStorage(requireContext())
        pieChart = view.findViewById(R.id.pieChart)
        periodSpinner = view.findViewById(R.id.periodSpinner)

        setupPeriodSpinner()
        showChart()
    }

    override fun onResume() {
        super.onResume()
        showChart()
    }

    private fun setupPeriodSpinner() {
        val periodOptions = Period.values().map { it.label }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            periodOptions
        )
        periodSpinner.adapter = adapter
        periodSpinner.setSelection(Period.values().indexOf(selectedPeriod))

        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedPeriod = Period.values()[position]
                showChart()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showChart() {
        val now = System.currentTimeMillis()
        val thresholdTime = now - selectedPeriod.durationMillis

        val filteredTransactions = storage.load().filter { it.timestamp > thresholdTime }

        val categoryTotals = filteredTransactions
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = categoryTotals.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Категории").apply {
            colors = listOf(
                Color.parseColor("#F44336"), Color.parseColor("#E91E63"),
                Color.parseColor("#9C27B0"), Color.parseColor("#673AB7"),
                Color.parseColor("#3F51B5"), Color.parseColor("#2196F3"),
                Color.parseColor("#03A9F4"), Color.parseColor("#00BCD4"),
                Color.parseColor("#009688"), Color.parseColor("#4CAF50"),
                Color.parseColor("#8BC34A"), Color.parseColor("#CDDC39"),
                Color.parseColor("#FFEB3B"), Color.parseColor("#FFC107"),
                Color.parseColor("#FF9800"), Color.parseColor("#FF5722"),
                Color.parseColor("#795548"), Color.parseColor("#9E9E9E"),
                Color.parseColor("#607D8B"), Color.parseColor("#BDBDBD"),
                Color.parseColor("#FFCDD2"), Color.parseColor("#C5CAE9"),
                Color.parseColor("#D1C4E9"), Color.parseColor("#AED581")
            )
            valueTextSize = 16f
            valueTextColor = Color.WHITE
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value < 1f) "" else String.format("%.0f ₽", value)
                }
            }
        }

        pieChart.apply {
            data = PieData(dataSet)
            setUsePercentValues(false)
            description.isEnabled = false
            setDrawEntryLabels(true)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(18f)
            animateY(1000)
            invalidate()

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                textSize = 28f
                isWordWrapEnabled = true
                maxSizePercent = 1f
                xEntrySpace = 36f
                yEntrySpace = 32f
                formSize = 20f
            }
        }
    }
}




