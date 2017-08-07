package com.exsun.meizi.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exsun.meizi.R;
import com.exsun.meizi.helper.Toasts;
import com.just.library.AgentWeb;
import com.just.library.AgentWebUtils;
import com.just.library.ChromeClientCallbackManager;
import com.yuyh.library.Base.BaseActivity;

import butterknife.Bind;

/**
 * Created by xiaokun on 2017/8/3.
 */

public class BaseWebActivity extends BaseActivity
{
    public static final String WEB_URL = "web_url";
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.container)
    LinearLayout container;
    private String url;

    protected AgentWeb mAgentWeb;

    @Override
    public void initData(Bundle bundle)
    {
        if (bundle == null)
        {
            return;
        }
        url = bundle.getString(WEB_URL);
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_base_web;
    }

    @Override
    protected void initPresenter()
    {

    }

    @Override
    public void initView()
    {
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        toolbar.setContentInsetStartWithNavigation(0);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.more));
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.refresh:
                        if (mAgentWeb != null)
                        {
                            mAgentWeb.getLoader().reload();
                        }
                        break;
                    case R.id.copy:
                        if (mAgentWeb != null)
                        {
                            toCopy(BaseWebActivity.this, mAgentWeb.getWebCreator().get().getUrl());
                        }
                        break;
                    case R.id.default_browser:
                        if (mAgentWeb != null)
                            openBrowser(mAgentWeb.getWebCreator().get().getUrl());
                        break;
                    default:

                        break;
                }
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    /**
     * 复制浏览器地址
     *
     * @param context
     * @param text
     */
    private void toCopy(Context context, String text)
    {
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
        Toasts.showSingleLong("成功复制链接：" + text);
    }

    /**
     * 外部浏览器打开地址
     *
     * @param targetUrl
     */
    private void openBrowser(String targetUrl)
    {
        if (!TextUtils.isEmpty(targetUrl) && targetUrl.startsWith("file://"))
        {
            AgentWebUtils.toastShowShort(this, targetUrl + " 该链接无法使用浏览器打开。");
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri url = Uri.parse(targetUrl);
        intent.setData(url);
        startActivity(intent);
    }

    @Override
    public void doBusiness(Context context)
    {
        mAgentWeb = AgentWeb.with(this)//传入Activity
                //传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
                .setAgentWebParent(container, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()// 使用默认进度条
                .setIndicatorColor(R.color.colorPrimary)
//                .defaultProgressBarColor()// 使用默认进度条颜色
                .setReceivedTitleCallback(mCallback)//设置 Web 页面的 title 回调
//                .setWebChromeClient(mWebChromeClient)
//                .setWebViewClient(mWebViewClient)
                .setSecutityType(AgentWeb.SecurityType.strict)
//                .setWebLayout(new WebLayout(this))
                .createAgentWeb()//
                .ready()
                .go(url);
    }

    @Override
    public void setStatusBar()
    {
//        super.setStatusBar();
    }

    private ChromeClientCallbackManager.ReceivedTitleCallback mCallback = new ChromeClientCallbackManager.ReceivedTitleCallback()
    {
        @Override
        public void onReceivedTitle(WebView view, String title)
        {
            if (toolbarTitle != null)
                toolbarTitle.setText(title);
        }
    };
}
