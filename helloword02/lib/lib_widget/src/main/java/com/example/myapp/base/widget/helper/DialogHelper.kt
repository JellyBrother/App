//package com.example.myapp.base.widget.helper
//
//import android.app.Dialog
//import android.content.Context
//import android.view.View
//import android.widget.EditText
//import android.widget.TextView
//import com.example.myapp.base.widget.R
//import com.example.myapp.base.widget.dialog.SimpleDialog
//import com.example.myapp.base.widget.dialog.SimpleTitleDialog
//
//object DialogHelper {
//    fun create2BtnDialog(
//        context: Context,
//        title: CharSequence?,
//        ensureBlock: (v: Dialog) -> Unit
//    ): Dialog {
//        return SimpleDialog(context).apply {
//            setTitleView(R.layout.base_dialog_title) {
//                val tvTitle = it.findViewById<TextView>(R.id.base_title)
//                tvTitle.text = title
//            }
//            setOnLeftClickListener { v, dialog ->
//                dialog.dismiss()
//            }
//            setOnRightClickListener { v, dialog ->
//                ensureBlock.invoke(dialog)
//            }
//        }
//    }
//
//    fun create2ButtonDialog(
//        context: Context,
//        title: CharSequence?,
//        ensureBlock: (v: View) -> Unit
//    ): Dialog {
//        return SimpleDialog(context).apply {
//            setTitleView(R.layout.base_dialog_title) {
//                val tvTitle = it.findViewById<TextView>(R.id.base_title)
//                tvTitle.text = title
//            }
//            setOnLeftClickListener { v, dialog ->
//                dialog.dismiss()
//            }
//            setOnRightClickListener { v, dialog ->
//                dialog.dismiss()
//                ensureBlock.invoke(v)
//            }
//        }
//    }
//
//    fun create2ButtonCancelDialog(
//        context: Context,
//        title: CharSequence?,
//        cancelBlock: (v: View) -> Unit,
//    ): Dialog {
//        return SimpleDialog(context).apply {
//            setTitleView(R.layout.base_dialog_title) {
//                val tvTitle = it.findViewById<TextView>(R.id.base_title)
//                tvTitle.text = title
//            }
//            setOnLeftClickListener { v, dialog ->
//                dialog.dismiss()
//                cancelBlock.invoke(v)
//            }
//            setOnRightClickListener { v, dialog ->
//                dialog.dismiss()
//            }
//        }
//    }
//
//    fun createEdit2ButtonDialog(
//        context: Context,
//        title: CharSequence?,
//        hint: String?,
//        ensureBlock: (v: View, inputText: String) -> Boolean
//    ): Dialog {
//        return SimpleDialog(context).apply {
//            setTitleView(R.layout.base_edit_dialog_title) {
//                val tvTitle = it.findViewById<TextView>(R.id.base_title)
//                val tvInput = it.findViewById<EditText>(R.id.base_input)
//                tvInput.hint = hint
//                tvTitle.text = title
//            }
//            setOnLeftClickListener { v, dialog ->
//                dialog.dismiss()
//            }
//            setOnRightClickListener { v, dialog ->
//                val tvInput = getTitleView().findViewById<EditText>(R.id.base_input)
//                val needDismiss = ensureBlock.invoke(v, tvInput.text.toString())
//                if (needDismiss) dialog.dismiss()
//            }
//        }
//    }
//
//    fun create2ButtonTitleDialog(
//        context: Context,
//        title: CharSequence?,
//        content: CharSequence?,
//        ensureBlock: (v: View) -> Unit
//    ): Dialog {
//        return SimpleTitleDialog(context).apply {
//            setTitleText(title)
//            setContentText(content)
//            setOnLeftClickListener { v, dialog ->
//                dialog.dismiss()
//            }
//            setOnRightClickListener { v, dialog ->
//                dialog.dismiss()
//                ensureBlock.invoke(v)
//            }
//        }
//    }
//
//    fun create2ButtonTitleDialog(
//        context: Context,
//        title: CharSequence?,
//        content: CharSequence?,
//        leftText: String?,
//        rightText: String?,
//        ensureBlock: (v: View) -> Unit
//    ): Dialog {
//        return SimpleTitleDialog(context).apply {
//            setTitleText(title)
//            setContentText(content)
//            leftRightButtonText(leftText, rightText)
//            setOnLeftClickListener { v, dialog ->
//                dialog.dismiss()
//            }
//            setOnRightClickListener { v, dialog ->
//                dialog.dismiss()
//                ensureBlock.invoke(v)
//            }
//        }
//    }
//}