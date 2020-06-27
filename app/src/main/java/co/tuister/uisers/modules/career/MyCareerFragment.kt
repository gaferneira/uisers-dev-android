package co.tuister.uisers.modules.career

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import co.tuister.domain.entities.Subject
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseFragment
import co.tuister.uisers.databinding.FragmentCareerBinding
import co.tuister.uisers.modules.career.schedule.ScheduleFragment
import co.tuister.uisers.modules.career.semesters.SemestersFragment
import co.tuister.uisers.modules.career.subjects.SubjectsAdapter
import co.tuister.uisers.modules.career.subjects.SubjectsFragment
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale

class MyCareerFragment : BaseFragment<FragmentCareerBinding>(), SubjectsAdapter.SubjectListener {

    private var subjectFragment: SubjectsFragment? = null
    private var scheduleFragment: ScheduleFragment? = null
    private var semestersFragment: SemestersFragment? = null

    private var currentPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCareerBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.pager.isSaveFromParentEnabled = true
        binding.pager.apply {
            offscreenPageLimit = 2
            adapter = TasksPagerAdapter()
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentPosition = position

                    val attr = if (position == 1) R.attr.colorPrimary else R.attr.colorSecondary
                    val typedArray = context.theme.obtainStyledAttributes(intArrayOf(attr))
                    val color = typedArray.getColor(0, 0)
                    typedArray.recycle()

                    binding.buttonAdd.backgroundTintList = ColorStateList.valueOf(color)
                }
            })
        }

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> resources.getString(R.string.career_title_subjects).toUpperCase(Locale.getDefault())
                1 -> resources.getString(R.string.career_title_schedule).toUpperCase(Locale.getDefault())
                2 -> resources.getString(R.string.career_title_semesters).toUpperCase(Locale.getDefault())
                else -> ""
            }
        }.attach()

        binding.buttonAdd.setOnClickListener {
            when (currentPosition) {
                0 -> findNavController().navigate(R.id.action_subjects_to_subject_add)
                1 -> scheduleFragment?.addNewItem()
                else -> semestersFragment?.showSemesterDialog()
            }
        }
    }

    override fun onClickSubject(subject: Subject) {
        val action =
            MyCareerFragmentDirections.actionSubjectsToSubjectDetails(
                subject
            )
        findNavController().navigate(action)
    }

    override fun onClickEdit(subject: Subject) {
        val action =
            MyCareerFragmentDirections.actionSubjectsToSubjectAdd(
                subject
            )
        findNavController().navigate(action)
    }

    private inner class TasksPagerAdapter : FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 ->
                    SubjectsFragment().apply {
                        listener = this@MyCareerFragment
                    }.also {
                        subjectFragment = it
                    }
                1 -> ScheduleFragment().also {
                    scheduleFragment = it
                }
                else -> SemestersFragment().also {
                    semestersFragment = it
                }
            }
        }
    }

    override fun onDestroy() {
        subjectFragment?.listener = null
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (currentPosition == 0) {
            subjectFragment?.refresh()
        }
    }
}
