package org.moe.googlemaps.struct;


import apple.corelocation.struct.CLLocationCoordinate2D;
import org.moe.natj.c.StructObject;
import org.moe.natj.c.ann.Structure;
import org.moe.natj.c.ann.StructureField;
import org.moe.natj.general.NatJ;
import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.ByValue;
import org.moe.natj.general.ann.Generated;

@Generated
@Structure()
public final class GMSVisibleRegion extends StructObject {
	static {
		NatJ.register();
	}
	private static long __natjCache;

	@Generated
	public GMSVisibleRegion() {
		super(GMSVisibleRegion.class);
	}

	@Generated
	protected GMSVisibleRegion(Pointer peer) {
		super(peer);
	}

	@Generated
	public GMSVisibleRegion(@ByValue CLLocationCoordinate2D nearLeft,
			@ByValue CLLocationCoordinate2D nearRight,
			@ByValue CLLocationCoordinate2D farLeft,
			@ByValue CLLocationCoordinate2D farRight) {
		super(GMSVisibleRegion.class);
		setNearLeft(nearLeft);
		setNearRight(nearRight);
		setFarLeft(farLeft);
		setFarRight(farRight);
	}

	@Generated
	@StructureField(order = 0, isGetter = true)
	@ByValue
	public native CLLocationCoordinate2D nearLeft();

	@Generated
	@StructureField(order = 0, isGetter = false)
	public native void setNearLeft(@ByValue CLLocationCoordinate2D value);

	@Generated
	@StructureField(order = 1, isGetter = true)
	@ByValue
	public native CLLocationCoordinate2D nearRight();

	@Generated
	@StructureField(order = 1, isGetter = false)
	public native void setNearRight(@ByValue CLLocationCoordinate2D value);

	@Generated
	@StructureField(order = 2, isGetter = true)
	@ByValue
	public native CLLocationCoordinate2D farLeft();

	@Generated
	@StructureField(order = 2, isGetter = false)
	public native void setFarLeft(@ByValue CLLocationCoordinate2D value);

	@Generated
	@StructureField(order = 3, isGetter = true)
	@ByValue
	public native CLLocationCoordinate2D farRight();

	@Generated
	@StructureField(order = 3, isGetter = false)
	public native void setFarRight(@ByValue CLLocationCoordinate2D value);
}