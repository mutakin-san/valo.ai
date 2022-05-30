package com.capstone.valoai.features.onboarding.presentation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.capstone.valoai.features.onboarding.data.models.OnBoardingModel
import com.capstone.valoai.databinding.FragmentOnboardingItemBinding

private const val PAGE = "page"

class OnBoardingItemFragment : Fragment() {

    private lateinit var binding: FragmentOnboardingItemBinding

    private var page: OnBoardingModel? = null


    private fun getDrawable(id: Int): Drawable? {
        return ResourcesCompat.getDrawable(resources, id, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            page = it.getParcelable(PAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.title.text = page?.title
        binding.description.text = page?.description
        binding.illustration.setImageDrawable(page?.imageId?.let { getDrawable(it) })

    }

    companion object {

        @JvmStatic
        fun newInstance(page: OnBoardingModel) =
            OnBoardingItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PAGE, page)
                }
            }
    }
}