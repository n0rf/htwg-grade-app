package de.htwg.moc.htwg_grade_app.qis;

import java.util.List;

import android.os.AsyncTask;
import de.htwg.moc.htwg_grade_app.DegreeListActivity;
import de.htwg.moc.htwg_grade_app.dos.Degree;

public class QisRequest extends AsyncTask<String, String, Boolean> {

	//private ProgressDialog dialog;
	//private Context context;
	private DegreeListActivity activity;

	/**
	 * Default constructor.
	 */
	public QisRequest(DegreeListActivity activity) {
		this.activity = activity;
		//this.context = activity;
		//dialog = new ProgressDialog(context);
	}

	@Override
	protected Boolean doInBackground(String... strings) {
		if (strings.length == 2) {
			if (!"".equals(strings[0]) && !"".equals(strings[1])) {
				String user = strings[0];
				String password = strings[1];

				QisClient client = new QisClient(this, user, password);

				DegreeContent.DEGREES.clear();
				DegreeContent.DEGREE_LIST.clear();
				
				if (!client.fetchData()) {
					return false;
				}
				
				List<Degree> degrees = client.getDegrees();
				for (Degree degree : degrees) {
					DegreeContent.DEGREES.put(degree.getNumber(), degree);
					DegreeContent.DEGREE_LIST.add(degree);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(String... progressInfo) {
		// setProgressPercent(progress[0]);
		//this.dialog.setMessage(progressInfo[0]);
		//this.dialog.show();
		activity.showLoadProcedureInformation(progressInfo[0]);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// showDialog("Downloaded " + result + " bytes");
//		if (!result) {
//			this.dialog.setMessage("Result refresh.");
//			this.dialog.show();
//		}

//		if (dialog.isShowing()) {
//			dialog.dismiss();
//		}
		activity.finishedLoadProcedure(result);
	}
}
