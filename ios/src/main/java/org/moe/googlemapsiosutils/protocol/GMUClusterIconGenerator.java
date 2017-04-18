package org.moe.googlemapsiosutils.protocol;


import apple.uikit.UIImage;
import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.Library;
import org.moe.natj.general.ann.NUInt;
import org.moe.natj.general.ann.Runtime;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCProtocolName;
import org.moe.natj.objc.ann.Selector;

@Generated
@Library("xcode/Pods/GoogleMaps/Maps/Frameworks/GoogleMaps.framework")
@Runtime(ObjCRuntime.class)
@ObjCProtocolName("GMUClusterIconGenerator")
public interface GMUClusterIconGenerator {
	@Generated
	@Selector("iconForSize:")
	UIImage iconForSize(@NUInt long size);
}