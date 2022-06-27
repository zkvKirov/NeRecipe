package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.databinding.RecipeContentFragmentBinding

class RecipeContentFragment : Fragment() {

    private val args by navArgs<RecipeContentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeContentFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        binding.editTitle.setText(args.initialContent?.newTitle)
        binding.editAuthor.setText(args.initialContent?.newAuthor)
        binding.editCategory.setText(args.initialContent?.newCategory)
        binding.editTitle.requestFocus(0)
        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }
    }.root

    private fun onOkButtonClicked(binding: RecipeContentFragmentBinding) {
        if (!binding.editTitle.text.isNullOrBlank()) {
            val resultBundle = Bundle(5)
            resultBundle.putString(NEW_TITLE, binding.editTitle.text.toString())
            resultBundle.putString(NEW_AUTHOR, binding.editAuthor.text.toString())
            resultBundle.putString(NEW_CATEGORY, binding.editCategory.text.toString())
            resultBundle.putString(STEP1, binding.editStep1.text.toString())
            resultBundle.putString(STEP2, binding.editStep2.text.toString())
            setFragmentResult(REQUEST_KEY, resultBundle)
            Toast.makeText(context, "Успех", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    companion object {
        const val REQUEST_KEY = "createRecipe"
        const val NEW_TITLE = "newTitle"
        const val NEW_AUTHOR = "newAuthor"
        const val NEW_CATEGORY = "newCategory"
        const val STEP1 = "step1"
        const val STEP2 = "step2"
    }

}