package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import ru.netology.nerecipe.databinding.FullStepPictureBinding

class FullStepPictureFragment : Fragment() {

    private val args by navArgs<FullStepPictureFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FullStepPictureBinding.inflate(inflater, container, false).also { binding ->
        binding.fullPicture

        context?.let {
            Glide.with(it)
                .load(args.pictureUrl)
                .into(binding.image)
        }

    }.root
}