package de.htwg.moc.htwg_grade_app.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class GradeDetailsAdapter extends SimpleAdapter {

	public GradeDetailsAdapter(Context context, List<Map<String, String>> grades_details, int resource, String[] from,
			int[] to) {
		super(context, grades_details, resource, from, to);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		return view;
	}

}
