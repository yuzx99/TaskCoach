package com.yuzx.taskcoach.share;

import java.util.HashMap;

import com.yuzx.taskcoach.R;

import cn.sharesdk.evernote.Evernote;

import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.WeiboActionListener;

import cn.sharesdk.sina.weibo.SinaWeibo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.Toast;

public class AuthActivity extends Activity 
implements WeiboActionListener,Callback{

	private Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_auth);
		
		handler = new Handler(this);
		AbstractWeibo[] weibos = AbstractWeibo.getWeiboList(this);
		for (AbstractWeibo weibo : weibos) {
			if (!weibo.isValid()) {
				continue;
			}

			CheckedTextView ctv = getView(weibo);
			if (ctv != null) {
				ctv.setChecked(true);
				String userName = weibo.getAuthedUserName();
				if (userName == null || userName.length() <= 0
						|| "null".equals(userName)) {
					// 如果平台已经授权却没有拿到帐号名称，则自动获取用户资料，以获取名称
					userName = getWeiboName(weibo);
					weibo.setWeiboActionListener(this);
					weibo.showUser(null);
				}
				ctv.setText(userName);
			}
		}
		
		CheckedTextView ctvSw = (CheckedTextView) findViewById(R.id.ctvSw);
		CheckedTextView ctvEn = (CheckedTextView) findViewById(R.id.ctvEn);
		
		ctvSw.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AbstractWeibo weibo = getWeibo(v.getId());
				CheckedTextView ctv = (CheckedTextView) v;
				if (weibo == null) {
					ctv.setChecked(false);
					ctv.setText(R.string.not_yet_authorized);
					return;
				}
				
				if (weibo.isValid()) {
					weibo.removeAccount();
					ctv.setChecked(false);
					ctv.setText(R.string.not_yet_authorized);
					return;
				}
				
				weibo.setWeiboActionListener(AuthActivity.this);
				weibo.showUser(null);
			}
		});
		
		ctvEn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AbstractWeibo weibo = getWeibo(v.getId());
				CheckedTextView ctv = (CheckedTextView) v;
				if (weibo == null) {
					ctv.setChecked(false);
					ctv.setText(R.string.not_yet_authorized);
					return;
				}
				
				if (weibo.isValid()) {
					weibo.removeAccount();
					ctv.setChecked(false);
					ctv.setText(R.string.not_yet_authorized);
					return;
				}
				
				weibo.setWeiboActionListener(AuthActivity.this);
				weibo.showUser(null);
			}
			
		});
	}

	public void onClick(View v) {
		
		AbstractWeibo weibo = getWeibo(v.getId());
		CheckedTextView ctv = (CheckedTextView) v;
		if (weibo == null) {
			ctv.setChecked(false);
			ctv.setText(R.string.not_yet_authorized);
			return;
		}
		
		if (weibo.isValid()) {
			weibo.removeAccount();
			ctv.setChecked(false);
			ctv.setText(R.string.not_yet_authorized);
			return;
		}
		
		weibo.setWeiboActionListener(this);
		weibo.showUser(null);
	}
	
	private AbstractWeibo getWeibo(int vid) {
		String name = null;
		switch (vid) {
		case R.id.ctvSw:
			name = SinaWeibo.NAME;
			break;
		case R.id.ctvEn:
			name = Evernote.NAME;
			break;
		}

		if (name != null) {
			return AbstractWeibo.getWeibo(this, name);
		}
		return null;
	}

	private CheckedTextView getView(AbstractWeibo weibo) {
		if (weibo == null) {
			return null;
		}

		String name = weibo.getName();
		if (name == null) {
			return null;
		}

		View v = null;
		if (SinaWeibo.NAME.equals(name)) {
			v = findViewById(R.id.ctvSw);
		}else if (Evernote.NAME.equals(name)) {
			v = findViewById(R.id.ctvEn);
		}

		if (v == null) {
			return null;
		}

		if (!(v instanceof CheckedTextView)) {
			return null;
		}

		return (CheckedTextView) v;
	}

	private String getWeiboName(AbstractWeibo weibo) {
		if (weibo == null) {
			return null;
		}

		String name = weibo.getName();
		if (name == null) {
			return null;
		}

		int res = 0;
		if (SinaWeibo.NAME.equals(name)) {
			res = R.string.sinaweibo;
		}

		else if (Evernote.NAME.equals(name)) {
			res = R.string.evernote;
		}

		if (res == 0) {
			return name;
		}

		return this.getResources().getString(res);
	}

	@Override
	public void onCancel(AbstractWeibo arg0, int arg1) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = arg1;
		msg.obj = arg0;
		handler.sendMessage(msg);
	}

	@Override
	public void onComplete(AbstractWeibo arg0, int arg1,
			HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
			Message msg = new Message();
			msg.arg1 = 1;
			msg.arg2 = arg1;
			msg.obj = arg2;
			handler.sendMessage(msg);
	}

	@Override
	public void onError(AbstractWeibo arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = arg1;
		msg.obj = arg0;
		handler.sendMessage(msg);
	}

	
	/** 
	 * 处理操作结果
	 * <p>
	 * 如果获取到用户的名称，则显示名称；否则如果已经授权，则显示
	 *平台名称
	 */
	public boolean handleMessage(Message msg) {
		AbstractWeibo weibo = (AbstractWeibo) msg.obj;
		String text = AbstractWeibo.actionToString(msg.arg2);
		switch (msg.arg1) {
			case 1: { // 成功
				text = weibo.getName() + " completed at " + text;
			}
			break;
			case 2: { // 失败
				text = weibo.getName() + " caught error at " + text;
			}
			break;
			case 3: { // 取消
				text = weibo.getName() + " canceled at " + text;
			}
			break;
		}
		
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		CheckedTextView ctv = getView(weibo);
		if (ctv != null) {
			ctv.setChecked(true);
			String userName = weibo.getAuthedUserName();
			if (userName == null || userName.length() <= 0
					|| "null".equals(userName)) {
				userName = getWeiboName(weibo);
			}
			ctv.setText(userName);
		}
		return false;
	}


}
