package co.tuister.uisers.utils.view

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import co.tuister.uisers.R
import co.tuister.uisers.databinding.DialogFragmentPaletteColorBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PaletteColorDialogFragment : AppCompatDialogFragment() {

    interface PaletteColorDialogListener {
        fun onSelectColor(index: Int)
    }

    lateinit var binding: DialogFragmentPaletteColorBinding

    private var listener: PaletteColorDialogListener? = null

    private lateinit var colors: IntArray

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AppCompatDialog(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_palette_color,
            null,
            false
        )
        binding.lifecycleOwner = this
        colors = arguments?.getIntArray(ARGUMENT_COLORS) ?: IntArray(0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        colors.forEachIndexed { index, color ->
            val fab = FloatingActionButton(requireContext()).apply {
                backgroundTintList = ColorStateList.valueOf(color)
                setOnClickListener {
                    listener?.onSelectColor(index)
                    dismiss()
                }
            }
            binding.flexbox.addView(
                fab,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(resources.getDimension(R.dimen.padding_margin_micro).toInt())
                }
            )
        }
    }

    companion object {

        const val TAG = "PaletteColorDialogFragment"
        private const val ARGUMENT_COLORS = "ARGUMENT_COLORS"

        fun create(
            colors: IntArray,
            listener: PaletteColorDialogListener
        ): PaletteColorDialogFragment {
            val dialog =
                PaletteColorDialogFragment()
            dialog.arguments = bundleOf(
                Pair(ARGUMENT_COLORS, colors)
            )
            dialog.listener = listener
            return dialog
        }
    }
}
