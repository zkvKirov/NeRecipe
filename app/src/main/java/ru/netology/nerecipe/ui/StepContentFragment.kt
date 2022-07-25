package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.StepCreateResult
import ru.netology.nerecipe.databinding.StepContentFragmentBinding
import ru.netology.nerecipe.util.AndroidUtils

class StepContentFragment(
    private val initialContent: StepCreateResult?
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = StepContentFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        binding.editStep.editText?.setText(initialContent?.newContent)
        binding.pictureUrl.editText?.setText(initialContent?.newPicture)
        binding.editStep.requestFocus(0)

        binding.okStep.setOnClickListener {
            onOkButtonClicked(binding)
        }
    }.root

    private fun onOkButtonClicked (binding: StepContentFragmentBinding) {
        binding.stepTextField.error = null
        if (binding.editStep.editText?.text.isNullOrBlank()) {
            binding.stepTextField.error = getString(R.string.error)
        } else {
            val resultBundle = Bundle(2)
            resultBundle.putString(NEW_CONTENT, binding.editStep.editText?.text.toString())
            resultBundle.putString(NEW_PICTURE, binding.pictureUrl.editText?.text.toString())
            setFragmentResult(REQUEST_KEY, resultBundle)
            parentFragmentManager.popBackStack()
        }
        AndroidUtils.hideKeyboard(binding.root)
    }

    companion object {
        const val REQUEST_KEY = "createStep"
        const val NEW_CONTENT = "newContent"
        const val NEW_PICTURE = "newPicture"
    }
}