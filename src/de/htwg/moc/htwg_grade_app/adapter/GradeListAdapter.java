package de.htwg.moc.htwg_grade_app.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
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
	  if (grade.isModul()) {
		  view.setBackgroundColor(colors[2]);
	  } else if (grade.isCertOnly()) {
		  view.setBackgroundColor(colors[1]);
	  } else {
		  view.setBackgroundColor(colors[0]);
	  }
	  
	  //int colorPos = position % colors.length;
	  //view.setBackgroundColor(colors[colorPos]);
	  return view;
	}

}
