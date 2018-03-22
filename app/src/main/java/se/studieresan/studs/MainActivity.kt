package se.studieresan.studs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import se.studieresan.studs.events.master.EventListFragment
import se.studieresan.studs.events.master.loop.EffectHandler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewpager.adapter = adapter
        tablayout.setupWithViewPager(viewpager)
        viewpager.offscreenPageLimit = 2
    }

    val handleEffects by lazy {
        val eventSource = (application as StudsApplication).eventSource
        val effectHandler = EffectHandler(eventSource)
        effectHandler.effectHandler()
    }


    val adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment =
                when (position) {
                    0 -> EventListFragment.newInstance()
                    1 -> EventListFragment.newInstance()
                    2 -> EventListFragment.newInstance()
                    else ->
                        throw IllegalStateException("Unimplemented pager position $position")
                }

        override fun getCount() = 3

        override fun getPageTitle(position: Int): CharSequence =
                when (position) {
                    0 -> "Event"
                    1 -> "Updates"
                    2 -> "Map"
                    else ->
                        throw IllegalStateException("Unimplemented pager position $position")
                }

    }
}
