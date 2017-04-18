package org.moe.googlemapsiosutils.protocol;


import apple.corelocation.struct.CLLocationCoordinate2D;
import org.moe.natj.general.ann.ByValue;
import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.Library;
import org.moe.natj.general.ann.Runtime;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCProtocolName;
import org.moe.natj.objc.ann.Selector;

@Generated
@Library("xcode/Pods/GoogleMaps/Maps/Frameworks/GoogleMaps.framework")
@Runtime(ObjCRuntime.class)
@ObjCProtocolName("GMUClusterItem")
public interface GMUClusterItem {
	@Generated
	@Selector("position")
	@ByValue
	CLLocationCoordinate2D position();
}