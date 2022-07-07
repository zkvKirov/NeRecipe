package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.databinding.CheckboxesFragmentBinding

class CheckboxFragment : Fragment() {

    private val checkboxes: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = CheckboxesFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        binding.choice.setOnClickListener {
            onChoiceButtonClicked(binding)
        }
    }.root

    private fun onChoiceButtonClicked(binding: CheckboxesFragmentBinding) {
        if (binding.checkbox1.isChecked) checkboxes.add(binding.checkbox1.text.toString())
        if (binding.checkbox2.isChecked) checkboxes.add(binding.checkbox2.text.toString())
        if (binding.checkbox3.isChecked) checkboxes.add(binding.checkbox3.text.toString())
        if (binding.checkbox4.isChecked) checkboxes.add(binding.checkbox4.text.toString())
        if (binding.checkbox5.isChecked) checkboxes.add(binding.checkbox5.text.toString())
        if (binding.checkbox6.isChecked) checkboxes.add(binding.checkbox6.text.toString())
        if (binding.checkbox7.isChecked) checkboxes.add(binding.checkbox7.text.toString())

        if (checkboxes.isNotEmpty()) {
            val resultBundle = Bundle(1)
            resultBundle.putStringArrayList(CHECKBOX_KEY, checkboxes)
            setFragmentResult(CHECKBOX_KEY, resultBundle)
        }
        findNavController().popBackStack()
    }

    companion object {
        const val CHECKBOX_KEY = "checkBoxContent"
    }
}