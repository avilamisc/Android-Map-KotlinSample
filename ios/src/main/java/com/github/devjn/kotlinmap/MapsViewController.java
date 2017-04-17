package com.github.devjn.kotlinmap;


import com.github.devjn.kotlinmap.common.Consts;
import com.github.devjn.kotlinmap.ui.LocationManager;
import com.github.devjn.kotlinmap.ui.LocationManagerDelegate;

import apple.NSObject;
import apple.corelocation.CLLocation;
import apple.corelocation.struct.CLLocationCoordinate2D;
import apple.foundation.NSArray;
import apple.foundation.NSBundle;
import apple.foundation.NSCoder;
import apple.foundation.NSMethodSignature;
import apple.foundation.NSSet;
import apple.uikit.UIImage;
import apple.uikit.UISearchBar;
import apple.uikit.UIViewController;
import apple.uikit.enums.UISearchBarStyle;
import apple.uikit.protocol.UISearchBarDelegate;

import org.moe.googlemaps.GMSAddress;
import org.moe.googlemaps.GMSCameraPosition;
import org.moe.googlemaps.GMSGeocoder;
import org.moe.googlemaps.GMSMapView;
import org.moe.googlemaps.GMSMarker;
import org.moe.googlemaps.protocol.GMSMapViewDelegate;
import org.moe.natj.c.ann.FunctionPtr;
import org.moe.natj.general.NatJ;
import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.Mapped;
import org.moe.natj.general.ann.MappedReturn;
import org.moe.natj.general.ann.NInt;
import org.moe.natj.general.ann.NUInt;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.general.ann.Runtime;
import org.moe.natj.general.ptr.IntPtr;
import org.moe.natj.general.ptr.VoidPtr;
import org.moe.natj.objc.Class;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.SEL;
import org.moe.natj.objc.ann.ObjCClassBinding;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Property;
import org.moe.natj.objc.ann.Selector;
import org.moe.natj.objc.map.ObjCObjectMapper;

@Generated
@Runtime(ObjCRuntime.class)
@ObjCClassName("MapsViewController")
@RegisterOnStartup
public class MapsViewController extends UIViewController implements GMSMapViewDelegate, UISearchBarDelegate, LocationManagerDelegate {
	static {
		NatJ.register();
	}

    interface MapsViewControllerCallback {
        void onLocationPicked(CLLocationCoordinate2D coordinate, String address);
    }

    private MapsViewControllerCallback handler = null;

    private boolean located = false;
    private CLLocationCoordinate2D selectedCoordinate;
    private String address = "";

	protected MapsViewController(Pointer peer) {
		super(peer);
	}

	@Owned
	@Selector("alloc")
	public static native MapsViewController alloc();

	@Selector("init")
	public native MapsViewController init();

    @Selector("mapView")
    @Property
    public native GMSMapView getMapView();

    @Property
	@Selector("searchBar")
	public native UISearchBar getSearchBar();

    private GMSMarker marker;

    @Override
    public void viewDidLoad() {
        super.viewDidLoad();
        GMSCameraPosition camera = (GMSCameraPosition) GMSCameraPosition.cameraWithTargetZoom(selectedCoordinate, Consts.defaultZoom);

        getMapView().setCamera(camera);
        getMapView().settings().setCompassButton(true);

        // Creates a marker with current location
        marker = GMSMarker.alloc().init();
        marker.setTitle("My location");
        marker.setSnippet("Russia");
        marker.setMap(getMapView());

        getSearchBar().setBackgroundImage(UIImage.alloc().init());
        getSearchBar().setSearchBarStyle(UISearchBarStyle.Default);

        getSearchBar().setDelegate(this);
    }


    // UISearchBarDelegate

    @Override
    public boolean searchBarShouldBeginEditing(UISearchBar searchBar) {
        System.out.println("--- searchBarShouldBeginEditing");
        return true;
    }

    @Override
    public void searchBarSearchButtonClicked(UISearchBar searchBar) {
        getSearchBar().resignFirstResponder();

        // Do the search...
        System.out.println("--- searchBar.text: " + getSearchBar().text());
    }

    @Override
    public void viewWillAppear(boolean animated) {
        System.out.println("--- viewWillAppear");
        LocationManager.getSharedManager().setDelegate(this);
        getMapView().setDelegate(this);
    }

    @Selector("doneButtonPressed:")
    public void doneButtonPressed(NSObject sender) {
        handler.onLocationPicked(selectedCoordinate, address);
        navigationController().popViewControllerAnimated(true);
    }

    @Selector("handleLocationButton:")
    public void handleLocationButton(NSObject sender) {
        CLLocation location = LocationManager.getSharedManager().currentLocation();
        if (location != null) {
            CLLocationCoordinate2D coordinate = location.coordinate();
            GMSCameraPosition camera = (GMSCameraPosition) GMSCameraPosition.cameraWithTargetZoom(coordinate, Consts.defaultZoom);
            getMapView().animateToCameraPosition(camera);
        } else {
            System.out.println(LocationManager.LOCATION_WARNING);
        }
    }

    @Override
    @Selector("mapView:willMove:")
    public void mapViewWillMove(GMSMapView mapView, boolean gesture) {
        System.out.println("--- willMove");
    }

    @Override
    @Selector("mapView:idleAtCameraPosition:")
    public void mapViewIdleAtCameraPosition(GMSMapView mapView, GMSCameraPosition position) {
        // In our case position.target equals to projected center of the map
        selectedCoordinate = position.target();
        // TODO: turn on ActivityIndicator
        GMSGeocoder.geocoder().reverseGeocodeCoordinateCompletionHandler(selectedCoordinate, (response, error) -> {
            if (error != null) {
                handleUnknownAddress();
                return;
            }
            // TODO: turn off ActivityIndicator
            if (response != null) {
                handleGeocoderResponse(response.firstResult());
            }
        });
    }

    private void handleGeocoderResponse(GMSAddress address) {
        String addressText = address.thoroughfare();

        if (addressText == null || addressText.toLowerCase().contains("unnamed")) {
            handleUnknownAddress();
            return;
        }
        this.address = addressText;
        getSearchBar().setText(addressText);
    }

    private void handleUnknownAddress() {
        System.out.println("--- handleUnknownAddress");
        address = "Unknown address";
    }

    @Override
    public void didUpdateLocation(LocationManager manager, CLLocation location) {
        CLLocationCoordinate2D coordinate = location.coordinate();
        if (!located) {
            located = true;
        }
        marker.setPosition(coordinate);
    }

    @Override
    public void didUpdateState(LocationManager manager, LocationManager.State state) {

    }

    @Override
    public void didUpdateTrackingLocation(LocationManager manager, CLLocation location) {
        System.out.println("--- didUpdateTrackingLocation");
    }

    void pickLocation(CLLocationCoordinate2D coordinate, MapsViewControllerCallback handler) {
        selectedCoordinate = coordinate;
        this.handler = handler;
    }

/*	@Generated
	@Selector("setMapView:")
	public native void setMapView(IntPtr value);

	@Generated
	@Selector("setSearchBar:")
	public native void setSearchBar_unsafe(UISearchBar value);

	@Generated
	public void setSearchBar(UISearchBar value) {
		Object __old = getSearchBar();
		if (value != null) {
			org.moe.natj.objc.ObjCRuntime.associateObjCObject(this, value);
		}
		setSearchBar_unsafe(value);
		if (__old != null) {
			org.moe.natj.objc.ObjCRuntime.dissociateObjCObject(this, __old);
		}
	}*/

}