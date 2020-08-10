package com.app.deliveryapplication.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.app.deliveryapplication.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.address_fragment.*
@AndroidEntryPoint
class AddressFragment:Fragment(){

    private val basketViewModel: BasketViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.address_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        address_confirm_btn.setOnClickListener {
            if(city_enter_field.text?.isNotEmpty()!!&&district_enter_field.text?.isNotEmpty()!!) {
                basketViewModel.currentAddress = "${city_enter_field.text}//${district_enter_field.text}//" +
                        "${office_enter_field.text}//${porch_enter_field.text}//${floor_enter_field.text}" +
                        "${comment_enter_field.text}"

            }
            else{
                if(city_enter_field.text!!.isEmpty()) city_enter_box.error = "Поле не должно быть пустым"
                if(district_enter_field.text!!.isEmpty()) district_enter_box.error = "Поле не должно быть пустым"
            }
        }

    }
}