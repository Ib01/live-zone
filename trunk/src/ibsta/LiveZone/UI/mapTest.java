package ibsta.LiveZone.UI;

import ibsta.LiveZone.LocationManager;
import ibsta.LiveZone.R;
import ibsta.LiveZone.LocationManager.OnSearchCompleteListener;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class mapTest extends MapActivity {
	List<Overlay> mapOverlays;
	Drawable drawable;
	HelloItemizedOverlay itemizedOverlay;
	LinearLayout linearLayout;
	MapView mapView;
	LocationManager locationManager;
	
	
	private OnSearchCompleteListener listener = new OnSearchCompleteListener() {
		
		public void onSearchComplete(Location location)
		{
			locationManager.removeUpdates();
			
			mapView = (MapView) findViewById(R.id.mapview);
			mapView.setBuiltInZoomControls(true);
			
			mapOverlays = mapView.getOverlays();
					
			drawable = getResources().getDrawable(R.drawable.androidmarker);
			itemizedOverlay = new HelloItemizedOverlay(drawable);

			Double lat  = location.getLatitude()*1E6;
			Double lng  = location.getLongitude()*1E6;
			

			
			//GeoPoint point = new GeoPoint(lat.intValue(), lng.intValue());
			//GeoPoint point = new GeoPoint(-35239395, 149155421);
			//GeoPoint point = new GeoPoint((int)(-35.239395 * 1e6), (int)(149.155421 * 1e6));
			//GeoPoint point = new GeoPoint((int)(149.155421 * 1e6), (int)(-35.239395 * 1e6));
			//GeoPoint point = new GeoPoint(19240000, -99120000);
			
			GeoPoint point = new GeoPoint(35410000, 139460000);
			
			
			OverlayItem overlayitem = new OverlayItem(point, "", "");

			itemizedOverlay.addOverlay(overlayitem);
			
			mapOverlays.add(itemizedOverlay);

			//snow peas, corriander
		
		}
	};
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// show layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapdialog);
		
		/*locationManager = new LocationManager(this);
		locationManager.setOnSearchCompleteListener(listener);
		locationManager.getUpdates();
		*/
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		MapController mapController = mapView.getController();
		
		Double lat = 149.155421 * 1e6;
		Double lng = -35.239395 * 1e6;
		
		//GeoPoint point = new GeoPoint(lat.intValue(), lng.intValue());
		GeoPoint point = new GeoPoint(lng.intValue(), lat.intValue());
		
		mapController.setCenter(point);
		mapController.setZoom(2);
		
		
		
		
		
		
		/*mapOverlays = mapView.getOverlays();
				
		drawable = getResources().getDrawable(R.drawable.androidmarker);
		itemizedOverlay = new HelloItemizedOverlay(drawable);*/


		
		//GeoPoint point = new GeoPoint(lat.intValue(), lng.intValue());
		//GeoPoint point = new GeoPoint(-35239395, 149155421);
		//GeoPoint point = new GeoPoint((int)(-35.239395 * 1e6), (int)(149.155421 * 1e6));
		//GeoPoint point = new GeoPoint((int)(149.155421 * 1e6), (int)(-35.239395 * 1e6));
		//GeoPoint point = new GeoPoint(19240000, -99120000);
		
		/*GeoPoint point = new GeoPoint(19240000,-99120000);
		OverlayItem overlayitem = new OverlayItem(point, "", "");
		*/
		//GeoPoint point2 = new GeoPoint(35410000, 139460000);
		
		/*GeoPoint point2 = new GeoPoint(139460000, 35410000);
		OverlayItem overlayitem2 = new OverlayItem(point2, "", "");*/
		

		//itemizedOverlay.addOverlay(overlayitem);
		/*
		 * itemizedOverlay.addOverlay(overlayitem2);
		
		
		mapOverlays.add(itemizedOverlay);
		
*/

		
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
