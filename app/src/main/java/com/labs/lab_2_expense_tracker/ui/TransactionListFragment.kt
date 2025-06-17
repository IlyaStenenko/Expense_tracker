import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.labs.lab_2_expense_tracker.R
import com.labs.lab_2_expense_tracker.data.Transaction
import com.labs.lab_2_expense_tracker.data.TransactionStorage
import com.labs.lab_2_expense_tracker.ui.AddTransactionActivity
import com.labs.lab_2_expense_tracker.ui.LimitSettingsActivity
import com.labs.lab_2_expense_tracker.ui.TransactionAdapter

class TransactionListFragment : Fragment() {

    private lateinit var adapter: TransactionAdapter
    private lateinit var storage: TransactionStorage
    private lateinit var addEditLauncher: ActivityResultLauncher<Intent>
    private lateinit var recyclerView: RecyclerView
    private lateinit var filterSpinner: Spinner

    private var allTransactions: List<Transaction> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadTransactions()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_transaction_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        val limitButton = view.findViewById<FloatingActionButton>(R.id.limitButton)
        filterSpinner = view.findViewById(R.id.categoryFilterSpinner)

        storage = TransactionStorage(requireContext())
        adapter = TransactionAdapter { transaction -> showEditDeleteDialog(transaction) }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            val intent = Intent(requireContext(), AddTransactionActivity::class.java)
            addEditLauncher.launch(intent)
        }

        limitButton.setOnClickListener {
            val intent = Intent(requireContext(), LimitSettingsActivity::class.java)
            startActivity(intent)
        }

        filterSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                applyFilter()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        loadTransactions()
    }


    private fun loadTransactions() {
        allTransactions = storage.load()
        Log.d("TX_DEBUG", "loadTransactions: loaded ${allTransactions.size} items")
        setupSpinner()
        applyFilter()
    }

    private fun setupSpinner() {
        val categories = allTransactions.map { it.category }.distinct().sorted()
        val options = listOf("Все категории") + categories
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = spinnerAdapter
    }

    private fun applyFilter() {
        val selectedCategory = filterSpinner.selectedItem as String
        val filteredList = if (selectedCategory == "Все категории") {
            allTransactions
        } else {
            allTransactions.filter { it.category == selectedCategory }
        }
        adapter.submitList(filteredList.toList())
    }

    private fun showEditDeleteDialog(transaction: Transaction) {
        AlertDialog.Builder(requireContext())
            .setTitle(transaction.title)
            .setMessage("Выберите действие")
            .setPositiveButton("Редактировать") { _, _ ->
                val intent = Intent(requireContext(), AddTransactionActivity::class.java)
                intent.putExtra("transaction", transaction)
                addEditLauncher.launch(intent)
            }
            .setNegativeButton("Удалить") { _, _ ->
                storage.delete(transaction)
                loadTransactions()
            }
            .setNeutralButton("Отмена", null)
            .show()
    }
}


