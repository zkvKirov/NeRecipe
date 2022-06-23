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

    //private val args by navArgs<RecipeContentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RecipeContentFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        binding.editTitle.setText("Рецепт")
        binding.editAuthor.setText("Я")
        binding.editTitle.requestFocus(0)
        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }
    }.root

    private fun onOkButtonClicked(binding: RecipeContentFragmentBinding) {
        if (!binding.editTitle.text.isNullOrBlank()) {
            val resultBundle = Bundle(2)
            resultBundle.putString(NEW_CONTENT, binding.editTitle.text.toString())
            resultBundle.putString(NEW_VIDEO_URL, binding.editAuthor.text.toString())
            setFragmentResult(REQUEST_KEY, resultBundle)
            Toast.makeText(context, "Успех", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
    }

    companion object {
        const val REQUEST_KEY = "createRecipe"
        const val NEW_CONTENT = "newContent" // исправить названия
        const val NEW_VIDEO_URL = "newVideoUrl" // исправить названия
    }

}