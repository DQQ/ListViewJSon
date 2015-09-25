package com.demo.app.ui;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.demo.app.AppContext;
import com.demo.app.AppException;
import com.demo.app.R;
import com.demo.app.adapter.MainListViewAdapter;
import com.demo.app.bean.News;
import com.demo.app.bean.NewsList;
import com.demo.app.common.UIHelper;
import com.demo.app.widget.MyListView;

public class MainActivity extends Activity {
	private MyListView listview;
	private List<News> newsList;
	private AppContext appContext;// 全局Context
	private MainListViewAdapter listViewAdapter;
	private ProgressDialog selectorDialog;
	private Button bt_news, bt_forum, bt_cartype, bt_favour, bt_more;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appContext = (AppContext) getApplication();
		// 网络连接判断
		if (!appContext.isNetworkConnected())
			UIHelper.ToastMessage(this, R.string.network_not_connected);
		init();
		initData();
	}

	private void init() {
		bt_news = (Button) findViewById(R.id.bt_news);
		bt_forum = (Button) findViewById(R.id.bt_forum);
		bt_cartype = (Button) findViewById(R.id.bt_cartype);
		bt_favour = (Button) findViewById(R.id.bt_favour);
		bt_more = (Button) findViewById(R.id.bt_more);
		bt_news.setSelected(true);
		bt_news.setOnClickListener(onClick(bt_news));
		bt_forum.setOnClickListener(onClick(bt_forum));
		bt_cartype.setOnClickListener(onClick(bt_cartype));
		bt_favour.setOnClickListener(onClick(bt_favour));
		bt_more.setOnClickListener(onClick(bt_more));
		listview = (MyListView) findViewById(R.id.news_listview);
		selectorDialog = ProgressDialog.show(this, null, "正在加载，请稍候...", true,
				false);

	}

	private View.OnClickListener onClick(final Button btn) {
		return new View.OnClickListener() {
			public void onClick(View v) {

				bt_news.setSelected(false);
				bt_forum.setSelected(false);
				bt_cartype.setSelected(false);
				bt_favour.setSelected(false);
				bt_more.setSelected(false);
				if (btn == bt_news) {
					bt_news.setSelected(true);
				} else if (btn == bt_forum) {
					bt_forum.setSelected(true);
				} else if (btn == bt_cartype) {
					bt_cartype.setSelected(true);
				} else if (btn == bt_favour) {
					bt_favour.setSelected(true);
				} else if (btn == bt_more) {
					bt_more.setSelected(true);
				}
			}
		};
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			selectorDialog.cancel();
			if (msg.what == 1) {
				newsList = (List<News>) msg.obj;
				listViewAdapter = new MainListViewAdapter(MainActivity.this,
						newsList);
				listview.setAdapter(listViewAdapter);
			} else if (msg.what == -1) {
				UIHelper.ToastMessage(MainActivity.this, "没有数据");
			} else if (msg.what == -2) {
				UIHelper.ToastMessage(MainActivity.this,
						R.string.xml_parser_failed);
			}
		}
	};

	private void initData() {

		selectorDialog.show();
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				try {
					NewsList list = appContext.getNewsList();
					if (list.getNewsCount() > 0) {
						msg.what = 1;
						msg.obj = list.getNewslist();
						appContext.saveObject(list, "newslist_");
					} else {
						msg.what = -1;
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -2;
					msg.obj = e;
				}
				mHandler.sendMessage(msg);
			}
		}.start();
	}
}
