package de.htwg.moc.htwg_grade_app.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import de.htwg.moc.htwg_grade_app.R;
import de.htwg.moc.htwg_grade_app.dos.Grade;

public class GradeListAdapter extends SimpleAdapter {

	private List<Grade> m_grades;
	
	private int[] colors = new int[] { 0x00FFFFFF, 0x30009900, 0x300033FF };

	public GradeListAdapter(Context context, 
	        List<Map<String, String>> grades_details, List<Grade> grades,
	        int resource, 
	        String[] from, 
	        int[] to) {
	  super(context, grades_details, resource, from, to);
	  this.m_grades = (List<Grade>) grades;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  View view = super.getView(position, convertView, parent);

	  Grade grade = m_grades.get(position);
	  int color = 0xFF000000;
	  if (grade.isModul()) {
		  view.setBackgroundColor(colors[2]);
	  } else if (grade.isCertOnly()) {
		  view.setBackgroundColor(colors[1]);
		  color = 0xFF888888;
	  } else {
		  view.setBackgroundColor(colors[0]);
	  }
	  ((TextView) view.findViewById(R.id.grade_item_grade)).setTextColor(color);
	  return view;
	}

}
