package com.example.foodx.app.bindingAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.example.foodx.R

class ListItemRecipeBindingAdapter {

    companion object {

        @BindingAdapter("setFavourite")
        @JvmStatic
        fun setFavourites(textView: TextView, likes: Int) {
            textView.text = likes.toString()
        }

        @BindingAdapter("setTime")
        @JvmStatic
        fun setTime(textView: TextView, time: Int) {
            textView.text = time.toString()
        }


        @BindingAdapter("setImage")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, image: String) {
            imageView.load(image) {
                crossfade(600)
            }
        }


        @BindingAdapter("applyVegetarianColor")
        @JvmStatic
        fun applyVegetarianColor(view: View, vegan: Boolean) {
            if (vegan) {
                when (view) {
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }

                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }
    }
}