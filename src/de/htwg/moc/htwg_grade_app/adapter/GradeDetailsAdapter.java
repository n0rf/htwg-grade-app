package de.htwg.moc.htwg_grade_app.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class GradeDetailsAdapter extends SimpleAdapter {

	//private int[] colors = new int[] { 0x00FFFFFF, 0xBBAAAAAA };

	public GradeDetailsAdapter(Context context, List<Map<String, String>> grades_details, int resource, String[] from,
			int[] to) {
		super(context, grades_details, resource, from, to);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

//		if (position % 2 == 0) {
//			view.setBackgroundColor(colors[0]);
//		} else {
//			view.setBackgroundColor(colors[1]);
//		}
		return view;
	}

}
