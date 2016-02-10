package org.xvdr.robotv.setup;

import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import org.xvdr.robotv.R;

import java.util.List;

public class SetupFragmentRoot extends GuidedStepFragment {

    static final String TAG = "SetupFragmentRoot";

    static final int ACTION_SERVER = 1;
    static final int ACTION_LANGUAGE = 2;
    static final int ACTION_REFRESHRATE = 3;
    static final int ACTION_IMPORT = 4;

    private GuidedAction mActionServer;
    private GuidedAction mActionLanguage;
    private GuidedAction mActionRefreshRate;

    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = getString(R.string.setup_root_title);
        String breadcrumb = "";
        String description = getString(R.string.setup_root_desc);
        Drawable icon = getActivity().getDrawable(R.drawable.ic_live_tv_white_48dp);

        return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
    }

    @Override
    public void onCreateActions(List actions, Bundle savedInstanceState) {
        String server = SetupUtils.getServer(getActivity());

        mActionServer = new GuidedAction.Builder()
                .id(ACTION_SERVER)
                .title(server)
                .description(getString(R.string.setup_root_server_desc))
                .editable(true)
                .build();

        mActionLanguage = new GuidedAction.Builder()
                .id(ACTION_LANGUAGE)
                .title(getString(R.string.setup_root_language_title))
                .hasNext(true)
                .build();

        mActionRefreshRate = new GuidedAction.Builder()
                .id(ACTION_REFRESHRATE)
                .title(getString(R.string.setup_root_refreshrate_title))
                .hasNext(true)
                .build();

        actions.add(mActionServer);
        actions.add(mActionLanguage);
        actions.add(mActionRefreshRate);

        actions.add(new GuidedAction.Builder()
                .id(ACTION_IMPORT)
                .title(getString(R.string.setup_root_import_title))
                .description(getString(R.string.setup_root_import_desc))
                .build());
    }

    @Override
    public void onResume() {
        super.onResume();

        // language

        int langIndex = SetupUtils.getLanguageIndex(getActivity());
        String[] langArray = getResources().getStringArray(R.array.languages_array);

        if(langIndex > langArray.length -1) {
            langIndex = 0;
        }

        mActionLanguage.setDescription(langArray[langIndex]);

        // refresh rate
        String[] refreshRates = getResources().getStringArray(R.array.refresh_rate_name_array);
        int refreshRateIndex = SetupUtils.getRefreshRateIndex(getActivity());

        mActionRefreshRate.setDescription(refreshRates[refreshRateIndex]);
    }

    public void onGuidedActionClicked(GuidedAction action) {
        FragmentManager fm = getFragmentManager();

        switch((int)action.getId()) {
            case ACTION_SERVER:
                SetupUtils.setServer(getActivity(), mActionServer.getTitle().toString());
                break;
            case ACTION_LANGUAGE:
                GuidedStepFragment.add(fm, new SetupFragmentLanguage());
                break;
            case ACTION_REFRESHRATE:
                GuidedStepFragment.add(fm, new SetupFragmentRefreshRate());
                break;
            case ACTION_IMPORT:
                importChannels();
                break;
        }
    }

    private void importChannels() {
        SetupFragmentImport progress = new SetupFragmentImport();

        getFragmentManager().beginTransaction().add(android.R.id.content, progress).commit();

        SetupActivity activity = (SetupActivity) getActivity();
        activity.registerChannels(progress);
    }
}