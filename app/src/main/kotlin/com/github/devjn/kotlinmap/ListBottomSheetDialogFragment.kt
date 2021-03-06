package com.github.devjn.kotlinmap

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.devjn.kotlinmap.common.PlaceClusterItem
import com.github.devjn.kotlinmap.common.PlacePoint
import com.github.devjn.kotlinmap.common.services.ResponseService
import com.github.devjn.kotlinmap.databinding.FragmentListBottomsheetBinding
import com.github.devjn.kotlinmap.databinding.ListItemCafeBinding
import com.github.devjn.kotlinmap.utils.SimpleDividerItemDecoration
import com.github.devjn.kotlinmap.utils.UIUtils
import com.minimize.android.rxrecycleradapter.RxDataSource
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

/**
 * Created by @author Jahongir on ${date}
 *
 *
 * ${file_name}
 */

class ListBottomSheetDialogFragment : BottomSheetDialogFragment() {
    val TAG = ListBottomSheetDialogFragment::class.java.kotlin.simpleName

    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private var listPlaces: List<PlaceClusterItem>? = null

    private lateinit var binding: FragmentListBottomsheetBinding
    private lateinit var mRecyclerView: RecyclerView
//    private  val mRecyclerAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    private lateinit var rxDataSource: RxDataSource<PlaceClusterItem>
    private val onClickSubject = PublishSubject.create<PlacePoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentListBottomsheetBinding>(inflater!!, R.layout.fragment_list_bottomsheet, container, false)
        mRecyclerView = binding.list
        mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        val mDividerItemDecoration = SimpleDividerItemDecoration(context, UIUtils.dp(16f), UIUtils.dp(16f))
        //                new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rxDataSource = RxDataSource(listPlaces)
        onClickSubject.subscribe { placePoint -> Log.i(TAG, "onClickSubject: " + placePoint) }
        onShow(lat, lng)
    }

    private fun onShow(lat: Double, lng: Double) {
        this.lat = lat
        this.lng = lng
        ResponseService.instance.getNearLocations(lat, lng, object : ResponseService.LocationResultListener {
            override fun onLocationResult(result: Collection<PlaceClusterItem>?) {
                Log.i(TAG, "places result: " + result)
                this@ListBottomSheetDialogFragment.listPlaces = ArrayList(result)
                rxDataSource.updateDataSet(listPlaces).updateAdapter()
                rxDataSource
                        .bindRecyclerView<ListItemCafeBinding>(mRecyclerView, R.layout.list_item_cafe)
                        .subscribe { viewHolder ->
                            val b = viewHolder.viewDataBinding
                            val place = viewHolder.item.clientObject
                            b.place = place
                            b.root.setOnClickListener { onClickSubject.onNext(place) }
                        }
                binding.progressBar.visibility = View.GONE
                Log.i(TAG, "list places: " + listPlaces)
            }
        })
    }

    val positionClicks: Observable<PlacePoint>
        get() = onClickSubject.hide()

    fun show(manager: FragmentManager, tag: String?, lat: Double, lng: Double) {
        this.lat = lat
        this.lng = lng
        super.show(manager, tag)
    }

    companion object {

        private val TAG = ListBottomSheetDialogFragment::class.java.simpleName

        internal fun newInstance(): ListBottomSheetDialogFragment {
            val fragment = ListBottomSheetDialogFragment()
            //        Bundle args = new Bundle();
            ////        args.putDouble("lat", lat);
            ////        args.putDouble("lng", lng);
            //        fragment.setArguments(args);
            return fragment
        }
    }
}
