package com.xianglin.fellowvillager.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;
/**
 * 
 * 
 * @author chengshengli
 * @version v 1.0.0 2015/11/7 11:06 XLXZ Exp $
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter
{
	/**
	 * 切换Fragment页面集合
	 */
	private List<Fragment> fragments;
	
	public MyFragmentPagerAdapter(FragmentManager fm)
	{
		super(fm);
	}

	public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {  
        super(fm);  
        this.fragments = fragments;  
    }  
	
	@Override
	public Fragment getItem(int position)
	{
		if(fragments != null && fragments.size() > position)
		{
			return fragments.get(position);
		}else
		{
			return null;
		}
	}
	
	@Override
	public int getCount()
	{
		if(fragments != null)
		{
			return fragments.size();
		}else
		{
			return 0;
		}
	}
	
}
