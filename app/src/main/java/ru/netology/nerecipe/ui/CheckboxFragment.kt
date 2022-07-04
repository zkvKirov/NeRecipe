package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.databinding.CheckboxesFragmentBinding

class CheckboxFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = CheckboxesFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
//        binding.checkbox1
//        binding.checkbox2
//        binding.checkbox3
//        binding.checkbox4
//        binding.checkbox5
//        binding.checkbox6
//        binding.checkbox7
        binding.choice.setOnClickListener {
            onChoiceButtonClicked(binding)
        }
    }.root

    private fun onChoiceButtonClicked(binding: CheckboxesFragmentBinding) {
        // TODO наисать код
        findNavController().popBackStack()
    }
}