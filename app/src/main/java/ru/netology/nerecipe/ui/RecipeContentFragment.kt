package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.RecipeContentFragmentBinding
import ru.netology.nerecipe.util.AndroidUtils

class RecipeContentFragment : Fragment() {

    private val args by navArgs<RecipeContentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeContentFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        val items = listOf("Европейская", "Азиатская", "Паназиатская", "Восточная", "Американская", "Русская", "Средиземноморская")
        //val items = listOf("Europe", "Asia", "Pan-Asian", "Eastern", "American", "Russian", "Mediterranean")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.category.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.editTitle.editText?.setText(args.initialContent?.newTitle).toString()
        binding.editAuthor.editText?.setText(args.initialContent?.newAuthor).toString()
        binding.category.editText?.setText(args.initialContent?.newCategory).toString()

        binding.editTitle.requestFocus(0)

        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val draft = Bundle(5)
            draft.putString(NEW_TITLE, binding.editTitle.editText?.text.toString())
            draft.putString(NEW_AUTHOR, binding.editAuthor.editText?.text.toString())
            draft.putString(NEW_CATEGORY, binding.category.editText?.text.toString())
            setFragmentResult(DRAFT_KEY, draft)
            Toast.makeText(context, "черновик рецепта сохранён", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

    }.root

    private fun onOkButtonClicked(binding: RecipeContentFragmentBinding) {
        if (binding.editTitle.editText?.text.isNullOrBlank()) {
            binding.editTitle.error = getString(R.string.error)
        } else if (binding.editAuthor.editText?.text.isNullOrBlank()) {
            binding.authorTextField.error = getString(R.string.error)
        } else if (binding.category.editText?.text.isNullOrBlank()) {
            binding.category.error = getString(R.string.error)
        } else {
            binding.editTitle.error = null
            binding.authorTextField.error = null
            binding.category.error = null
            val resultBundle = Bundle(5)
            resultBundle.putString(NEW_TITLE, binding.editTitle.editText?.text.toString())
            resultBundle.putString(NEW_AUTHOR, binding.editAuthor.editText?.text.toString())
            resultBundle.putString(NEW_CATEGORY, binding.category.editText?.text.toString())
            setFragmentResult(REQUEST_KEY, resultBundle)
            findNavController().popBackStack()
        }
        AndroidUtils.hideKeyboard(binding.root)
    }

    companion object {
        const val REQUEST_KEY = "createRecipe"
        const val DRAFT_KEY = "draftRecipe"
        const val NEW_TITLE = "newTitle"
        const val NEW_AUTHOR = "newAuthor"
        const val NEW_CATEGORY = "newCategory"
        const val STEP1 = "step1"
        const val STEP2 = "step2"
    }

}