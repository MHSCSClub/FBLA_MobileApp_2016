package org.mamkschools.mhs.fbla_mobileapp_2016.lib;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Andrew A. Katz on 2/28/2016.
 * Courtesy of http://stackoverflow.com/questions/31381474/menu-and-autohide-floatingactionbutton-of-android-design-support-library
 */
public class ScrollAwareFabBehavior {
    public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {
        public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
            super();
        }

        @Override
        public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                           final View directTargetChild, final View target, final int nestedScrollAxes) {
            // Ensure we react to vertical scrolling
            return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                    || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        }

        @Override
        public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                   final View target, final int dxConsumed, final int dyConsumed,
                                   final int dxUnconsumed, final int dyUnconsumed) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
            if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
                // User scrolled down and the FAB is currently visible -> hide the FAB
                child.hide();
            } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
                // User scrolled up and the FAB is currently not visible -> show the FAB
                child.show();
            }
        }
    }
}
