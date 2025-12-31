package net.imknown.testandroid

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import net.imknown.testandroid.databinding.T19ActivityCoordinatorBinding
import kotlin.math.max
import kotlin.math.min

class T19CoordinatorActivity : AppCompatActivity() {
    private val binding by lazy { T19ActivityCoordinatorBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = CustomAdapter((1..25).toList())
    }

    // region [Adapter]
    class CustomAdapter(
        private val dataSet: List<Int>
    ) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(android.R.layout.simple_list_item_1, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.textView.text = "${dataSet[position]}"
            viewHolder.textView.setOnClickListener {
                Toast.makeText(
                    it.context, "Click " + viewHolder.textView.text, Toast.LENGTH_SHORT
                ).show()
            }
            viewHolder.textView.setOnLongClickListener {
                Toast.makeText(
                    it.context, "Long click " + viewHolder.textView.text, Toast.LENGTH_SHORT
                ).show()
                true
            }
        }

        override fun getItemCount() = dataSet.size
    }
    // endregion [Adapter]

/**
 * A AppBarLayout.ScrollingViewBehavior that adapts the scrolling view height based on its content.
 * Specifically, the final height is a compromise among:
 * - visible height (e.g. height not occupied by the appbar itself)
 * - wrap content height (e.g. total height of the scrolling content)
 * - available height (e.g. total CoordinatorLayout height)
 *
 * For this to work, you need:
 * - set this behavior to the scrolling view
 * - have match_parent as height
 * - have at least one of the AppBarLayout childs use the flags:
 *
 * app:layout_scrollFlags="scroll|exitUntilCollapsed"
 */
class ConstrainedScrollBehavior(context: Context, attrs: AttributeSet?) :
    AppBarLayout.ScrollingViewBehavior(context, attrs) {
    private var ablOriginalMinHeight = -1

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        val childLpHeight = child.layoutParams.height
        Log.e(TAG, "onMeasureChild: getLayoutParams().height is $childLpHeight (-1=MP, -2=WC)")
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            return super.onMeasureChild(
                parent, child,
                parentWidthMeasureSpec, widthUsed,
                parentHeightMeasureSpec, heightUsed
            )
        }

        val dependencies = parent.getDependencies(child)
        val header = findAppBar(dependencies)
            ?: return super.onMeasureChild(
                parent, child,
                parentWidthMeasureSpec, widthUsed,
                parentHeightMeasureSpec, heightUsed
            )

        // TODO: If we had some height changes inside, at the end of the function we
        // will update the abl scroll range based on new height. However, for some reason,
        // that won't work if the abl is fully expanded (offset == 0). Seems like it is not
        // properly invalidated, even though we set the right value.
        // This is a dirty workaround.
        val p = header.layoutParams as CoordinatorLayout.LayoutParams
        val b = p.behavior as AppBarLayout.Behavior?
        if (b?.topAndBottomOffset == 0) {
            b.setTopAndBottomOffset(-1)
        }

        // Check original minHeight if needed.
        if (ablOriginalMinHeight == -1) {
            ablOriginalMinHeight = header.measuredHeight - header.totalScrollRange
        }

        // FitsSystemWindows stuff.
        if (header.fitsSystemWindows
            && !child.fitsSystemWindows
        ) {
            child.fitsSystemWindows = true
            if (child.fitsSystemWindows) {
                // If the set succeeded, trigger a new layout and return true
                child.requestLayout()
                return true
            }
        }

        // Get available height as imposed by the parent (the "screen height").
        var availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
        if (availableHeight == 0) {
            // If the measure spec doesn't specify a size, use the current height
            availableHeight = parent.height
        }

        // Get AppBarLayout measured height.
        val headerHeight = header.measuredHeight

        // Get visible height, the one we are aiming at.
        // The default was availableHeight - header.getMeasuredHeight() + header.getTotalScrollRange()
        val visibleHeight = availableHeight - headerHeight

        // Measure wrap-content height: if it's big enough, that's OK for us.
        val wcMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        parent.onMeasureChild(
            child,
            parentWidthMeasureSpec, widthUsed,
            wcMeasureSpec, heightUsed
        )
        val wcHeight = child.measuredHeight

        // If it's not big enough, then we must measure again and assign the whole visibleHeight.
        // -> The largest of the two
        val desiredHeight = max(visibleHeight, wcHeight)
        // -> No more than the available, or the bottom part will be hidden
        //    (Coordinator does not scroll itself)
        val finalHeight = min(desiredHeight, availableHeight)

        // Measure again :-( in most cases.
        if (finalHeight != wcHeight) {
            val heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(finalHeight, View.MeasureSpec.EXACTLY)
            parent.onMeasureChild(
                child,
                parentWidthMeasureSpec, widthUsed,
                heightMeasureSpec, heightUsed
            )
        }

        // When we use availableHeight: (bc wcHeight is too much), all is OK.
        //                              We will surely scroll until ABL is fully scrolled out.
        // When we use wcHeight: There are some issues. We don't fit the whole availableHeight,
        //                       but scroll events are forwarded to the ABL which scrolls itself.
        //                       We must stop ABL scrolling when we are fully visible.
        // When we use visibleHeight: Scrolling content is very small and there's no real scroll.
        //                            But user can still drag the ABL directly.
        //                            We must act too.
        //
        // -> ABL minHeight (=> scrollRange) must be redefined. It should be:
        // - equal to availableHeight - finalHeight
        // - but no less than original minHeight, I guess (fetched before).
        // To do this we set min height to the scrolling child, see AppBarLayout.getTotalScrollRange()
        // for info.
        // Note: scroll|exitUntilCollapsed flags are needed for this to work.
        if (finalHeight != availableHeight) {
            // Find the scrolling child
            val view = findAppBarScrollingChild(header)
            if (view != null) {
                // FIXME: add non-collapsed views
                val ablMinHeight = max(ablOriginalMinHeight, (availableHeight - finalHeight))
                view.minimumHeight = ablMinHeight
            }
        }

        Log.e(TAG, "onMeasureChild: availableHeight imposed by the parent is: $availableHeight")
        Log.e(TAG, "onMeasureChild: headerHeight: $headerHeight")
        Log.e(TAG, "onMeasureChild: Measured WC height is $wcHeight")
        Log.e(TAG, "onMeasureChild: desired Height (max(WC,visible): $desiredHeight")
        Log.e(TAG, "onMeasureChild: final Height (min(desired,available)): $finalHeight")
        return true
    }

    private fun findAppBar(dependencies: List<View?>): AppBarLayout? {
        var i = 0
        val z = dependencies.size
        while (i < z) {
            val view = dependencies[i]
            if (view is AppBarLayout) {
                return view
            }
            i++
        }
        return null
    }

    /** FIXME: Wrapped scrollable view */
    private fun findAppBarScrollingChild(appBarLayout: AppBarLayout): View? {
        for (i in 0 until appBarLayout.childCount) {
            val child = appBarLayout.getChildAt(i)
            val params = child.layoutParams as AppBarLayout.LayoutParams
            val flags = params.scrollFlags
            if ((flags and AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
                if ((flags and AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
                    return child
                }
            } else {
                break
            }
        }
        return null
    }

    companion object {
        private val TAG: String = ConstrainedScrollBehavior::class.java.simpleName
    }
}
}