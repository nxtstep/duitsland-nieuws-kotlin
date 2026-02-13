package io.supersimple.duitslandnieuws.di.fragment

import androidx.fragment.app.Fragment
import dagger.MembersInjector

interface FragmentComponent<F : Fragment> : MembersInjector<F>