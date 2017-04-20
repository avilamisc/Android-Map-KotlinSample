package org.moe.googlemapsiosutils;


import apple.NSObject;
import apple.foundation.NSArray;
import apple.foundation.NSMethodSignature;
import apple.foundation.NSSet;
import org.moe.googlemapsiosutils.protocol.GMUClusterAlgorithm;
import org.moe.googlemapsiosutils.protocol.GMUClusterItem;
import org.moe.googlemapsiosutils.protocol.GMUClusterManagerDelegate;
import org.moe.googlemapsiosutils.protocol.GMUClusterRenderer;
import org.moe.natj.c.ann.FunctionPtr;
import org.moe.natj.general.NatJ;
import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.Generated;
import org.moe.natj.general.ann.Library;
import org.moe.natj.general.ann.Mapped;
import org.moe.natj.general.ann.MappedReturn;
import org.moe.natj.general.ann.NInt;
import org.moe.natj.general.ann.NUInt;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.Runtime;
import org.moe.natj.general.ptr.VoidPtr;
import org.moe.natj.objc.Class;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.SEL;
import org.moe.natj.objc.ann.ObjCClassBinding;
import org.moe.natj.objc.ann.Selector;
import org.moe.natj.objc.map.ObjCObjectMapper;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ann.ObjCClassName;

@ObjCClassName("GMUClusterManager") @RegisterOnStartup @Generated
@Library("xcode/Pods/GoogleMaps/Maps/Frameworks/GoogleMaps.framework")
@Runtime(ObjCRuntime.class)
public class GMUClusterManager extends NSObject {
	static {
		NatJ.register();
	}

	@Generated
	protected GMUClusterManager(Pointer peer) {
		super(peer);
	}

	@Generated
	@Selector("accessInstanceVariablesDirectly")
	public static native boolean accessInstanceVariablesDirectly();

	@Generated
	@Selector("addItem:")
	public native void addItem(
			@Mapped(ObjCObjectMapper.class) GMUClusterItem item);

	@Generated
	@Selector("addItems:")
	public native void addItems(NSArray<?> items);

	@Generated
	@Selector("algorithm")
	@MappedReturn(ObjCObjectMapper.class)
	public native GMUClusterAlgorithm algorithm();

	@Generated
	@Owned
	@Selector("alloc")
	public static native GMUClusterManager alloc();

	@Generated
	@Selector("allocWithZone:")
	@MappedReturn(ObjCObjectMapper.class)
	public static native Object allocWithZone(VoidPtr zone);

	@Generated
	@Selector("automaticallyNotifiesObserversForKey:")
	public static native boolean automaticallyNotifiesObserversForKey(String key);

	@Generated
	@Selector("cancelPreviousPerformRequestsWithTarget:")
	public static native void cancelPreviousPerformRequestsWithTarget(
			@Mapped(ObjCObjectMapper.class) Object aTarget);

	@Generated
	@Selector("cancelPreviousPerformRequestsWithTarget:selector:object:")
	public static native void cancelPreviousPerformRequestsWithTargetSelectorObject(
			@Mapped(ObjCObjectMapper.class) Object aTarget, SEL aSelector,
			@Mapped(ObjCObjectMapper.class) Object anArgument);

	@Generated
	@Selector("class")
	public static native Class class_objc_static();

	@Generated
	@Selector("classFallbacksForKeyedArchiver")
	public static native NSArray<String> classFallbacksForKeyedArchiver();

	@Generated
	@Selector("classForKeyedUnarchiver")
	public static native Class classForKeyedUnarchiver();

	@Generated
	@Selector("clearItems")
	public native void clearItems();

	@Generated
	@Selector("cluster")
	public native void cluster();

	@Generated
	@Selector("debugDescription")
	public static native String debugDescription_static();

	@Generated
	@Selector("delegate")
	@MappedReturn(ObjCObjectMapper.class)
	public native GMUClusterManagerDelegate delegate();

	@Generated
	@Selector("description")
	public static native String description_static();

	@Generated
	@Selector("hash")
	@NUInt
	public static native long hash_static();

	@Generated
	@Selector("init")
	public native GMUClusterManager init();

	@Generated
	@Selector("initWithMap:algorithm:renderer:")
	public native GMUClusterManager initWithMapAlgorithmRenderer(
			@Mapped(ObjCObjectMapper.class) Object mapView,
			@Mapped(ObjCObjectMapper.class) GMUClusterAlgorithm algorithm,
			@Mapped(ObjCObjectMapper.class) GMUClusterRenderer renderer);

	@Generated
	@Selector("initialize")
	public static native void initialize();

	@Generated
	@Selector("instanceMethodForSelector:")
	@FunctionPtr(name = "call_instanceMethodForSelector_ret")
	public static native NSObject.Function_instanceMethodForSelector_ret instanceMethodForSelector(
			SEL aSelector);

	@Generated
	@Selector("instanceMethodSignatureForSelector:")
	public static native NSMethodSignature instanceMethodSignatureForSelector(
			SEL aSelector);

	@Generated
	@Selector("instancesRespondToSelector:")
	public static native boolean instancesRespondToSelector(SEL aSelector);

	@Generated
	@Selector("isSubclassOfClass:")
	public static native boolean isSubclassOfClass(Class aClass);

	@Generated
	@Selector("keyPathsForValuesAffectingValueForKey:")
	public static native NSSet<String> keyPathsForValuesAffectingValueForKey(
			String key);

	@Generated
	@Selector("load")
	public static native void load_objc_static();

	@Generated
	@Selector("mapDelegate")
	@MappedReturn(ObjCObjectMapper.class)
	public native Object mapDelegate();

	@Generated
	@Owned
	@Selector("new")
	@MappedReturn(ObjCObjectMapper.class)
	public static native Object new_objc();

	@Generated
	@Selector("removeItem:")
	public native void removeItem(
			@Mapped(ObjCObjectMapper.class) GMUClusterItem item);

	@Generated
	@Selector("resolveClassMethod:")
	public static native boolean resolveClassMethod(SEL sel);

	@Generated
	@Selector("resolveInstanceMethod:")
	public static native boolean resolveInstanceMethod(SEL sel);

	@Generated
	@Selector("setDelegate:mapDelegate:")
	public native void setDelegateMapDelegate(
			@Mapped(ObjCObjectMapper.class) GMUClusterManagerDelegate delegate,
			@Mapped(ObjCObjectMapper.class) Object mapDelegate);

	@Generated
	@Selector("setVersion:")
	public static native void setVersion(@NInt long aVersion);

	@Generated
	@Selector("superclass")
	public static native Class superclass_static();

	@Generated
	@Selector("version")
	@NInt
	public static native long version_static();
}