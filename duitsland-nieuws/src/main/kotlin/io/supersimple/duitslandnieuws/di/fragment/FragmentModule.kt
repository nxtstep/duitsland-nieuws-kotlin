package io.supersimple.duitslandnieuws.di.fragment

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides

@Module
abstract class FragmentModule<out F : Fragment>(protected val fragment: F) {

    @Provides
    @FragmentScope
    fun provideFragment(): F {
        return fragment
    }
}
