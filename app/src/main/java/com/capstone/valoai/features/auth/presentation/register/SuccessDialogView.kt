package com.capstone.valoai.features.auth.presentation.register

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.capstone.valoai.R


class SuccessDialogView(private val ctx: Activity) {
    fun showDialog(title: String?, msg: String?, onClickListener: View.OnClickListener) {
        val dialog = Dialog(ctx)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.success_dialog)
        val titleDialog = dialog.findViewById<View>(R.id.success_title_id) as TextView
        val msgDialog = dialog.findViewById<View>(R.id.success_message_id) as TextView
        titleDialog.text = title
        msgDialog.text = msg
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogButton = dialog.findViewById<View>(R.id.success_btn_id) as Button
        dialogButton.setOnClickListener {
            onClickListener.onClick(it)
            dialog.dismiss()
        }
        dialog.show()
    }

}