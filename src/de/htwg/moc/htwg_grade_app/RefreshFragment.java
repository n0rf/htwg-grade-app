package de.htwg.moc.htwg_grade_app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class RefreshFragment extends Fragment {

	private View m_rootView = null;

	public RefreshFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		m_rootView = inflater.inflate(R.layout.fragment_refresh, container, false);

		m_rootView.findViewById(R.id.refresh_all_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((DegreeListActivity) getActivity()).refresh();
			}
		});

		return m_rootView;
	}
}
