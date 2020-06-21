package co.tuister.uisers.modules.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import co.tuister.uisers.R
import co.tuister.uisers.databinding.DialogFragmentAboutBinding

class AboutDialogFragment : AppCompatDialogFragment() {

    lateinit var binding: DialogFragmentAboutBinding

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
            R.layout.dialog_fragment_about,
            null,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    companion object {

        const val TAG = "AboutDialogFragment"

        fun create(): AboutDialogFragment {
            return AboutDialogFragment()
        }
    }
}
