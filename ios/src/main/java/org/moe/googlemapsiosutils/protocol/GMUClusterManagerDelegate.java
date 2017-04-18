package org.moe.googlemapsiosutils.protocol;


import org.moe.googlemapsiosutils.GMUClusterManager;
import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.Library;
import org.moe.natj.general.ann.Mapped;
import org.moe.natj.general.ann.Runtime;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.IsOptional;
import org.moe.natj.objc.ann.ObjCProtocolName;
import org.moe.natj.objc.ann.Selector;
import org.moe.natj.objc.map.ObjCObjectMapper;

@Generated
@Library("xcode/Pods/GoogleMaps/Maps/Frameworks/GoogleMaps.framework")
@Runtime(ObjCRuntime.class)
@ObjCProtocolName("GMUClusterManagerDelegate")
public interface GMUClusterManagerDelegate {
	@Generated
	@IsOptional
	@Selector("clusterManager:didTapCluster:")
	default void clusterManagerDidTapCluster(GMUClusterManager clusterManager,
			@Mapped(ObjCObjectMapper.class) Object cluster) {
		throw new java.lang.UnsupportedOperationException();
	}

	@Generated
	@IsOptional
	@Selector("clusterManager:didTapClusterItem:")
	default void clusterManagerDidTapClusterItem(
			GMUClusterManager clusterManager,
			@Mapped(ObjCObjectMapper.class) Object clusterItem) {
		throw new java.lang.UnsupportedOperationException();
	}
}