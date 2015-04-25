package com.example.wechatsample;

import android.content.Context;
import android.content.Intent;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

public class PlusActionProvider extends ActionProvider {

	private Context context;

	public PlusActionProvider(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public View onCreateActionView() {
		return null;
	}

	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		subMenu.clear();
		subMenu.add(context.getString(R.string.plus_group_chat))
				.setIcon(R.drawable.tasks_cccccc_64)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
                        context.startActivity(new Intent(context,PostStarEyeReuquestActivity.class));
						return true;
					}
				});
		subMenu.add(context.getString(R.string.plus_login))
				.setIcon(R.drawable.ofm_add_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
                        context.startActivity(new Intent(context,LoginActivity.class));
						return false;
					}
				});
		subMenu.add(context.getString(R.string.plus_register))
				.setIcon(R.drawable.ofm_add_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
                        context.startActivity(new Intent(context,RegisterActivity.class));
						return false;
					}
				});
        /*
		subMenu.add(context.getString(R.string.plus_setting))
				.setIcon(R.drawable.ofm_setting_icon)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return false;
					}
				});
		subMenu.add(context.getString(R.string.action_personal))
				.setIcon(R.drawable.user_cccccc_64)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						return false;
					}
				});*/
	}

	@Override
	public boolean hasSubMenu() {
		return true;
	}

}