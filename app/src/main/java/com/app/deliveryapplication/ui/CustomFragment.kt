package com.app.deliveryapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.app.deliveryapplication.setUpLoading

open class CustomFragment : Fragment(){


    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().setUpLoading(false)
    }

}