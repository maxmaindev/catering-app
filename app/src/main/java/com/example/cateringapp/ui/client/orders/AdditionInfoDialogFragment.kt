package com.example.cateringapp.ui.client.orders

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.cateringapp.R


class AdditionInfoDialogFragment(val onTableNameEntered: (String) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
                .setTitle(getString(R.string.extra_data))

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_table_num, null)

            builder.setView(view)
                .setPositiveButton(R.string.ok) { dialog, id ->
                    val editText = view.findViewById<EditText>(R.id.table_name_input)
                    val tableNum = editText.text.toString()
                    if (tableNum.isNotEmpty()) {
                        onTableNameEntered(tableNum)
                    }
                }
                .setNegativeButton(R.string.cancel) { dialog, id ->
                    dialog.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}