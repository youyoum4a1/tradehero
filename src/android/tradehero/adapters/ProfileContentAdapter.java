package android.tradehero.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.tradehero.activities.R;
import android.tradehero.utills.Util;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileContentAdapter extends BaseAdapter {

	private List<Item> items = new ArrayList<Item>();
	private LayoutInflater inflater;
	private Context ctx;

	public ProfileContentAdapter(Context context) {
		inflater = LayoutInflater.from(context);

		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.google_g));
		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.google_g));
		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.google_g));
		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.google_g));
		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.google_g));
		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.google_g));
		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.google_g));
		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.dbs));
		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.google_g));
		items.add(new Item("Alok",R.drawable.bhatia_img1,"vshd hggdjdkj hgdjjd hshj","3 min",R.drawable.facebook));

		ctx=context;

	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int i) {
		return items.get(i);
	}

	@Override
	public long getItemId(int i) {
		return items.get(i).drawableUserImg;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		View v = view;
		ImageView Userpicture,VenderImg;
		TextView name,content,time;

		if(v == null) 
		{
			v = inflater.inflate(R.layout.profile_item_list_screen, viewGroup, false);
			v.setTag(R.id.img_user, v.findViewById(R.id.img_user));
			v.setTag(R.id.txt_user_name, v.findViewById(R.id.txt_user_name));
			v.setTag(R.id.txt_user_content_name, v.findViewById(R.id.txt_user_content_name));
			v.setTag(R.id.txt_time_name, v.findViewById(R.id.txt_time_name));
			v.setTag(R.id.img_vender, v.findViewById(R.id.img_vender));
		}

		Userpicture = (ImageView)v.getTag(R.id.img_user);
		VenderImg =  (ImageView)v.getTag(R.id.img_vender);
		name = (TextView)v.getTag(R.id.txt_user_name);
		content = (TextView)v.getTag(R.id.txt_user_content_name);
		time = (TextView)v.getTag(R.id.txt_time_name);
		Item item = (Item)getItem(i);
		Userpicture.setImageBitmap(Util.getRoundedShape(BitmapFactory.decodeResource(ctx.getResources(), item.drawableUserImg)));
		VenderImg.setImageResource(item.drawablecompanyImg);
		name.setText(item.name);
		content.setText(item.content);
		time.setText(item.time);

		return v;
	}

	private class Item {
		final String name;
		final int drawableUserImg;
		final String content;
		final String time;
		final int drawablecompanyImg;


		Item(String name, int drawableUserImg,String content,String time,int drawablecompanyImg) {
			this.name = name;
			this.drawableUserImg = drawableUserImg;
			this.content = content;
			this.time = time;
			this.drawablecompanyImg = drawablecompanyImg;
		}
	}

}
