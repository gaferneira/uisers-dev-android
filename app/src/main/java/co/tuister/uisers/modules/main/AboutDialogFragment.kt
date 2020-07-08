package co.tuister.uisers.modules.main

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import co.tuister.uisers.R
import co.tuister.uisers.databinding.DialogFragmentHomeAboutBinding
import co.tuister.uisers.utils.extensions.singleClick

class AboutDialogFragment : AppCompatDialogFragment() {

    lateinit var binding: DialogFragmentHomeAboutBinding

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
            R.layout.dialog_fragment_home_about,
            null,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewFounder1.singleClick {
            openLinkedInPage(getString(R.string.home_about_founder_1_linked_in))
        }

        binding.textViewFounder2.singleClick {
            openLinkedInPage(getString(R.string.home_about_founder_2_linked_in))
        }

        binding.textViewEmail.singleClick {
            sendEmail(getString(R.string.home_about_email))
        }

        binding.textViewWhatsapp.singleClick {
            openWhatsapp(getString(R.string.home_about_whatsapp_number))
        }

        binding.textViewRepository.singleClick {
            openUrl(getString(R.string.home_about_repository_url))
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    private fun openLinkedInPage(linkedId: String) {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://add/%@$linkedId"))
        val packageManager = requireContext().packageManager
        val list =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.isEmpty()) {
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.linkedin.com/in/$linkedId")
            )
        }
        startActivity(intent)
    }

    private fun openWhatsapp(number: String) {
        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    private fun sendEmail(email: String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "There are no email clients installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

    companion object {

        const val TAG = "AboutDialogFragment"

        fun create(): AboutDialogFragment {
            return AboutDialogFragment()
        }
    }
}
