package com.example.twainz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public interface BackPressListener {

    public boolean onBackPressed();


    class backPressHandler implements BackPressListener {

        private Fragment parentFragment;

        public backPressHandler(Fragment parentFragment) {
            this.parentFragment = parentFragment;
        }

        //This function will pop the next fragment from the correct child backstack and update the appbar title
        @Override
        public boolean onBackPressed() {

            if (parentFragment == null) return false;   //Cant go back. Return false to indicate the backpress was not handled

            int childCount = parentFragment.getChildFragmentManager().getBackStackEntryCount();

            if (childCount == 0) {
                return false;   //There are no children to go back to
            } else {
                FragmentManager childFragmentManager = parentFragment.getChildFragmentManager();
                BackPressListener childFragment = (BackPressListener) childFragmentManager.getFragments().get(0);


                if (!childFragment.onBackPressed()) {  //If the backpress wasnt handled by the child fragment handle it here
                    childFragmentManager.popBackStack();

                }

                return true; //Return true to indicate the backpress was handled
            }
        }
    }
}