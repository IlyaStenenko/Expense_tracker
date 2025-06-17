package com.labs.lab_2_expense_tracker.ui

import com.labs.lab_2_expense_tracker.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.labs.lab_2_expense_tracker.data.Transaction

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleText)
        val amount: TextView = view.findViewById(R.id.amountText)
        val category: TextView = view.findViewById(R.id.categoryText)
        val card: CardView = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.title.text = transaction.title
        holder.amount.text = "${transaction.amount} ₽"
        holder.category.text = transaction.category
        holder.card.setOnClickListener { onItemClick(transaction) }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(old: Transaction, new: Transaction) = old.id == new.id
            override fun areContentsTheSame(old: Transaction, new: Transaction) = old == new
        }
    }
}
