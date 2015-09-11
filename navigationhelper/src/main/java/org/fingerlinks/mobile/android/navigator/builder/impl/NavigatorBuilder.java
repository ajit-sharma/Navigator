package org.fingerlinks.mobile.android.navigator.builder.impl;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import org.fingerlinks.mobile.android.navigationhelper.R;
import org.fingerlinks.mobile.android.navigator.utils.ContextReference;
import org.fingerlinks.mobile.android.navigator.NavigatorBean;
import org.fingerlinks.mobile.android.navigator.NavigatorException;
import org.fingerlinks.mobile.android.navigator.builder.Builders;

/**
 * Created by fabio on 07/09/15.
 */
public class NavigatorBuilder extends BaseBuilder implements Builders.Any.N {

    private boolean mCommitIsForFragment;

    public NavigatorBuilder(ContextReference context, NavigatorBean navigatorBean, boolean commitIsFragment) throws NavigatorException {
        super(context, navigatorBean);
        mCommitIsForFragment = commitIsFragment;
    }

    @Override
    public Builders.Any.N debug() {
        return null;
    }

    @Override
    public void commit() {
        String alive = mContextReference.isAlive();
        if(null != alive) {
            throw new NavigatorException("Building request with dead context: " + alive);
        }

        if(mCommitIsForFragment) {
            commitFragment();
        } else {
            commitActivity();
        }
    }

    private void commitActivity() {

        if(mNavigatorBean.getRequestCode() != -1) {
            ((Activity)mContextReference.getContext()).
                    startActivityForResult(
                            mNavigatorBean.getIntent(),
                            mNavigatorBean.getRequestCode());
        } else {
            mContextReference.getContext().
                    startActivity(mNavigatorBean.getIntent());
        }
        if (mNavigatorBean.isAnimation()) {
            int _length = (mNavigatorBean.getAnimations() == null)?0:mNavigatorBean.getAnimations().length;
            switch(_length) {
                case 2:
                    ((Activity)mContextReference.getContext()).
                            overridePendingTransition(
                                    mNavigatorBean.getAnimations()[0],
                                    mNavigatorBean.getAnimations()[1]);
                    break;
                default:
                    ((Activity)mContextReference.getContext()).
                            overridePendingTransition(
                                    R.anim.anim_window_in, R.anim.anim_window_out);
                    break;
            }
        }
    }

    private void commitFragment() {
        //listStep.add(mNavBean.getTag());

        FragmentTransaction fragmentTransaction = mNavigatorBean.getFragmentManager().beginTransaction();
        if (mNavigatorBean.isAnimation()) {
            int _length = (mNavigatorBean.getAnimations() == null)?0:mNavigatorBean.getAnimations().length;
            switch(_length) {
                case 2:
                    fragmentTransaction.setCustomAnimations(
                            mNavigatorBean.getAnimations()[0],
                            mNavigatorBean.getAnimations()[1]);
                    break;
                case 4:
                    fragmentTransaction.setCustomAnimations(
                            mNavigatorBean.getAnimations()[0],
                            mNavigatorBean.getAnimations()[1],
                            mNavigatorBean.getAnimations()[2],
                            mNavigatorBean.getAnimations()[3]);
                    break;
                default:
                    fragmentTransaction.setCustomAnimations(
                            R.anim.view_flipper_transition_in_left,
                            R.anim.view_flipper_transition_out_left,
                            R.anim.view_flipper_transition_in_right,
                            R.anim.view_flipper_transition_out_right);
                    break;
            }
        }
        switch (mNavigatorBean.getType()) {
            case REPLACE:
                fragmentTransaction.replace(
                        mNavigatorBean.getContainer(),
                        mNavigatorBean.getFragment(),
                        mNavigatorBean.getTag());
                break;
            case ADD:
                fragmentTransaction.add(
                        mNavigatorBean.getContainer(),
                        mNavigatorBean.getFragment(),
                        mNavigatorBean.getTag());
                break;
        }

        if (mNavigatorBean.isAddToBackStack()) {
            fragmentTransaction.addToBackStack(mNavigatorBean.getTag());
        }
        fragmentTransaction.commit();
    }
};