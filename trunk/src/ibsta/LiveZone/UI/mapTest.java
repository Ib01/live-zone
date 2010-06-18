package ibsta.LiveZone.UI;

import ibsta.LiveZone.R;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class mapTest extends MapActivity {
	List<Overlay> mapOverlays;
	Drawable drawable;
	HelloItemizedOverlay itemizedOverlay;
	LinearLayout linearLayout;
	MapView mapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// show layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapdialog);

		/*
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		mapOverlays = mapView.getOverlays();
		
		drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		itemizedOverlay = new HelloItemizedOverlay(drawable);

		Location l;
		
		GeoPoint point = new GeoPoint((int)(19240000 * 1e6), (int)(-99120000* 1e6));
		OverlayItem overlayitem = new OverlayItem(point, "", "");

		itemizedOverlay.addOverlay(overlayitem);
		
		mapOverlays.add(itemizedOverlay);*/

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
