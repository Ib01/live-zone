/***
	Copyright (c) 2009-10 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package ibsta.LiveZoneUITests;

import android.content.Intent;
import android.util.Log;

public class AppService extends WakefulIntentService {
	public AppService() {
		super("AppService");
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		
		
		Log.i("LiveZoneUITests", "in AppService.doWakefulWork");
		
		NotificationManager nm = new NotificationManager(this.getApplicationContext());
		nm.notify("AppService", "in AppService.doWakefulWork");
		
		
		ZoneAlertManager zoneAlertManager = new ZoneAlertManager(getApplicationContext());
		
		zoneAlertManager.getLocationAsync();
		
		
		/*File log=new File(Environment.getExternalStorageDirectory(),"AlarmLog.txt");

		try {
			BufferedWriter out=new BufferedWriter(new FileWriter(log.getAbsolutePath(),log.exists()));

			out.write(new Date().toString());
			out.write("\n");
			out.close();
		}
		catch (IOException e) {
			Log.e("AppService", "Exception appending to log file", e);
		}*/
	}
}











