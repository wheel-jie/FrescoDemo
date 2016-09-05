package com.mango.supplier.fragment.completedorders;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.mango.supplier.App;
import com.mango.supplier.R;
import com.mango.supplier.activity.completedorder.CompletedOrderActivity;
import com.mango.supplier.activity.completedorder.OrderDetailsActivity;
import com.mango.supplier.adapter.completedorders.ComOrderlistAdapter;
import com.mango.supplier.bean.ComOrder;
import com.mango.supplier.utils.BaseUrl;
import com.mango.supplier.utils.CustomProgress;
import com.mango.supplier.utils.JsonUtil;
import com.mango.supplier.utils.SharedPreferencesUtils;
import com.mango.supplier.utils.UIUtils;
import com.mango.supplier.views.AutoListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class WeekComOrderFragment extends Fragment {

    private AutoListView mListView;

    private ComOrderlistAdapter adapter;

    private ListComOrder.Result.ComOrders list;

    private RelativeLayout mFail,mNetError,mNoproduct,mNoOrder;

    private PullToRefreshScrollView mPullToRefreshScrollView;

    private int mPage=1;

    public  ScrollView scrollView;

    private View viewT;
    private TextView mHintTv;
    int h=0;
    protected boolean isCreated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_week_com_order, container, false);
        initview(view);
        initData();
        setAdapter();
        setlistener();
        request(0);
        return view;
    }

    private void initview(View view) {
        this.mListView= (AutoListView) view.findViewById(R.id.lv_order_list);
        this.mFail= (RelativeLayout) view.findViewById(R.id.iv_error_fail);
        this.mNetError= (RelativeLayout) view.findViewById(R.id.iv_error_neterror);
        this.mNoproduct= (RelativeLayout) view.findViewById(R.id.no_product);
        this.mNoOrder= (RelativeLayout) view.findViewById(R.id.no_order);
        this.mPullToRefreshScrollView= (PullToRefreshScrollView) view.findViewById(R.id.psl_comorder_week);
        isCreated = true;
    }

    private void initData() {
        list=new ArrayListComOrder.Result.ComOrders();
        viewT=LayoutInflater.from(getActivity()).inflate(R.layout.list_head,null);
        mHintTv= (TextView) viewT.findViewById(R.id.tv_list_head);
        mHintTv.setText(下拉刷新...);
        mListView.addHeaderView(viewT);
    }

    private void setAdapter() {
        this.adapter=new ComOrderlistAdapter(getActivity(),list);
        this.mListView.setAdapter(adapter);
    }
    static int sh;
    @TargetApi(Build.VERSION_CODES.M)
    private void setlistener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                Intent intent=new Intent(getActivity(), OrderDetailsActivity.class);
                intent.putExtra(startSubOrderId,list.get(i-1).subOrderId);
                getActivity().startActivity(intent);
            }
        });
        scrollView=mPullToRefreshScrollView.getRefreshableView();
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (sh!=0){
                    if (i3sh){
                        if (i3-sh200){
                            sh=0;
                            CompletedOrderActivity.isShow(1);
                        }
                    }else {
                        if (sh-i3200){
                            sh=0;
                            CompletedOrderActivity.isShow(0);
                        }
                    }
                }else {
                    sh=i3;
                }
                if (i350){
                    CompletedOrderActivity.isShow(0);
                }
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }else{
                    scrollView.requestDisallowInterceptTouchEvent(false);
                }
                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE
                        if (view.getScrollY()100){
                            if (h!=0){
                                if (Math.abs(event.getY()-h)5) {
                                    if (event.getY()  h){
                                        CompletedOrderActivity.isShow(0);
                                    } else{
                                        CompletedOrderActivity.isShow(1);
                                    }
                                    h = (int) event.getY();
                                }
                            }else {
                                h = (int) event.getY();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP
                        h=0;
                        break;
                }
                return false;
            }
        });
        this.mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        initRefreshListView();
        this.mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2ScrollView() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBaseScrollView refreshView) {
                刷新
                mHintTv.setText(正在加载...);
                mPage=1;
                request(1);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBaseScrollView refreshView) {
                加载
                mPage++;
                request(2);
            }
        });
    }
    public void initRefreshListView() {
        ILoadingLayout Labels = mPullToRefreshScrollView.getLoadingLayoutProxy(true, false);
        Labels.setPullLabel();
        Labels.setRefreshingLabel();
        Labels.setLoadingDrawable(new Drawable() {
            @Override
            public void draw(Canvas canvas) {

            }

            @Override
            public void setAlpha(int i) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        });
        Labels.setReleaseLabel();
    }
    
      请求数据
     
    private void request(final int type) {
        String supplierId= SharedPreferencesUtils.getInfo(getActivity(),supplierId);
        if (TextUtils.isEmpty(supplierId)){
            Toast.makeText(getActivity(),登陆状态异常,Toast.LENGTH_SHORT).show();
            return;
        }
        if (type==0){
            CustomProgress.show(getActivity(), , true, null);
        }
        OkHttpUtils
                .get()
                .url(BaseUrl.COMPLETED_ORDER_LIST+supplierId=+supplierId+&timeTabNum=+2+&ps=10&pn=+mPage)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CustomProgress.dissmiss();
                        mNetError.setVisibility(View.VISIBLE);
                        mPullToRefreshScrollView.onRefreshComplete();
                        mHintTv.setText(下拉刷新...);
                        mPage--;
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        CustomProgress.dissmiss();
                        mPullToRefreshScrollView.onRefreshComplete();
                        mHintTv.setText(下拉刷新...);
                        ComOrder order = JsonUtil.parseJsonToBean(response, ComOrder.class);
                        Log.i(MIANINGOO,response);
                        if (00.equals(order.Code)){
                            UIUtils.Gone(mFail,mNetError,mNoproduct,mNoOrder);
                            ListComOrder.Result.ComOrders orders=order.result.list;
                            if (type==0type==1){
                                if (orders.size()==0){
                                    mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    mNoOrder.setVisibility(View.VISIBLE);
                                    return;
                                }
                                list.clear();
                                list.addAll(orders);
                                adapter.notifyDataSetChanged();
                            }else{
                                if (orders.size()==0){
                                    mPage--;
                                    return;
                                }
                                list.addAll(orders);
                                adapter.notifyDataSetChanged();
                            }
                        }else {
                            mPullToRefreshScrollView.onRefreshComplete();
                            mPage--;
                            mFail.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        CompletedOrderActivity.isShow(0);
        if (isVisibleToUser) {
            if (!isCreated) {
                return;
            }
            if (App.all ==true) {
                request(1);
            }
        }

    }
}
package com.mango.supplier.fragment.completedorders;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.mango.supplier.App;
import com.mango.supplier.R;
import com.mango.supplier.activity.completedorder.CompletedOrderActivity;
import com.mango.supplier.activity.completedorder.OrderDetailsActivity;
import com.mango.supplier.adapter.completedorders.ComOrderlistAdapter;
import com.mango.supplier.bean.ComOrder;
import com.mango.supplier.utils.BaseUrl;
import com.mango.supplier.utils.CustomProgress;
import com.mango.supplier.utils.JsonUtil;
import com.mango.supplier.utils.SharedPreferencesUtils;
import com.mango.supplier.utils.UIUtils;
import com.mango.supplier.views.AutoListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class WeekComOrderFragment extends Fragment {

    private AutoListView mListView;

    private ComOrderlistAdapter adapter;

    private ListComOrder.Result.ComOrders list;

    private RelativeLayout mFail,mNetError,mNoproduct,mNoOrder;

    private PullToRefreshScrollView mPullToRefreshScrollView;

    private int mPage=1;

    public  ScrollView scrollView;

    private View viewT;
    private TextView mHintTv;
    int h=0;
    protected boolean isCreated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_week_com_order, container, false);
        initview(view);
        initData();
        setAdapter();
        setlistener();
        request(0);
        return view;
    }

    private void initview(View view) {
        this.mListView= (AutoListView) view.findViewById(R.id.lv_order_list);
        this.mFail= (RelativeLayout) view.findViewById(R.id.iv_error_fail);
        this.mNetError= (RelativeLayout) view.findViewById(R.id.iv_error_neterror);
        this.mNoproduct= (RelativeLayout) view.findViewById(R.id.no_product);
        this.mNoOrder= (RelativeLayout) view.findViewById(R.id.no_order);
        this.mPullToRefreshScrollView= (PullToRefreshScrollView) view.findViewById(R.id.psl_comorder_week);
        isCreated = true;
    }

    private void initData() {
        list=new ArrayListComOrder.Result.ComOrders();
        viewT=LayoutInflater.from(getActivity()).inflate(R.layout.list_head,null);
        mHintTv= (TextView) viewT.findViewById(R.id.tv_list_head);
        mHintTv.setText(下拉刷新...);
        mListView.addHeaderView(viewT);
    }

    private void setAdapter() {
        this.adapter=new ComOrderlistAdapter(getActivity(),list);
        this.mListView.setAdapter(adapter);
    }
    static int sh;
    @TargetApi(Build.VERSION_CODES.M)
    private void setlistener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                Intent intent=new Intent(getActivity(), OrderDetailsActivity.class);
                intent.putExtra(startSubOrderId,list.get(i-1).subOrderId);
                getActivity().startActivity(intent);
            }
        });
        scrollView=mPullToRefreshScrollView.getRefreshableView();
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (sh!=0){
                    if (i3sh){
                        if (i3-sh200){
                            sh=0;
                            CompletedOrderActivity.isShow(1);
                        }
                    }else {
                        if (sh-i3200){
                            sh=0;
                            CompletedOrderActivity.isShow(0);
                        }
                    }
                }else {
                    sh=i3;
                }
                if (i350){
                    CompletedOrderActivity.isShow(0);
                }
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }else{
                    scrollView.requestDisallowInterceptTouchEvent(false);
                }
                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE
                        if (view.getScrollY()100){
                            if (h!=0){
                                if (Math.abs(event.getY()-h)5) {
                                    if (event.getY()  h){
                                        CompletedOrderActivity.isShow(0);
                                    } else{
                                        CompletedOrderActivity.isShow(1);
                                    }
                                    h = (int) event.getY();
                                }
                            }else {
                                h = (int) event.getY();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP
                        h=0;
                        break;
                }
                return false;
            }
        });
        this.mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        initRefreshListView();
        this.mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2ScrollView() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBaseScrollView refreshView) {
                刷新
                mHintTv.setText(正在加载...);
                mPage=1;
                request(1);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBaseScrollView refreshView) {
                加载
                mPage++;
                request(2);
            }
        });
    }
    public void initRefreshListView() {
        ILoadingLayout Labels = mPullToRefreshScrollView.getLoadingLayoutProxy(true, false);
        Labels.setPullLabel();
        Labels.setRefreshingLabel();
        Labels.setLoadingDrawable(new Drawable() {
            @Override
            public void draw(Canvas canvas) {

            }

            @Override
            public void setAlpha(int i) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        });
        Labels.setReleaseLabel();
    }
    
      请求数据
     
    private void request(final int type) {
        String supplierId= SharedPreferencesUtils.getInfo(getActivity(),supplierId);
        if (TextUtils.isEmpty(supplierId)){
            Toast.makeText(getActivity(),登陆状态异常,Toast.LENGTH_SHORT).show();
            return;
        }
        if (type==0){
            CustomProgress.show(getActivity(), , true, null);
        }
        OkHttpUtils
                .get()
                .url(BaseUrl.COMPLETED_ORDER_LIST+supplierId=+supplierId+&timeTabNum=+2+&ps=10&pn=+mPage)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CustomProgress.dissmiss();
                        mNetError.setVisibility(View.VISIBLE);
                        mPullToRefreshScrollView.onRefreshComplete();
                        mHintTv.setText(下拉刷新...);
                        mPage--;
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        CustomProgress.dissmiss();
                        mPullToRefreshScrollView.onRefreshComplete();
                        mHintTv.setText(下拉刷新...);
                        ComOrder order = JsonUtil.parseJsonToBean(response, ComOrder.class);
                        Log.i(MIANINGOO,response);
                        if (00.equals(order.Code)){
                            UIUtils.Gone(mFail,mNetError,mNoproduct,mNoOrder);
                            ListComOrder.Result.ComOrders orders=order.result.list;
                            if (type==0type==1){
                                if (orders.size()==0){
                                    mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    mNoOrder.setVisibility(View.VISIBLE);
                                    return;
                                }
                                list.clear();
                                list.addAll(orders);
                                adapter.notifyDataSetChanged();
                            }else{
                                if (orders.size()==0){
                                    mPage--;
                                    return;
                                }
                                list.addAll(orders);
                                adapter.notifyDataSetChanged();
                            }
                        }else {
                            mPullToRefreshScrollView.onRefreshComplete();
                            mPage--;
                            mFail.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        CompletedOrderActivity.isShow(0);
        if (isVisibleToUser) {
            if (!isCreated) {
                return;
            }
            if (App.all ==true) {
                request(1);
            }
        }

    }
}
