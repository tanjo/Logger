package in.tanjo.logger.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.tanjo.logger.R;
import in.tanjo.logger.activity.MainActivity;
import in.tanjo.logger.service.ScreenshotService;

public class PlaceholderFragment extends Fragment {

  @Bind(R.id.screenshot_start_button) Button mStartButton;
  @Bind(R.id.screenshot_end_button) Button mEndButton;

  public PlaceholderFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.main_fragment, container, false);
    ButterKnife.bind(this, rootView);
    return rootView;
  }

  @OnClick(R.id.screenshot_start_button)
  void onStartButton() {
    getActivity().startService(new Intent(getActivity(), ScreenshotService.class));
  }

  @OnClick(R.id.screenshot_end_button)
  void onEndButton() {
    getActivity().stopService(new Intent(getActivity(), ScreenshotService.class));
  }
}