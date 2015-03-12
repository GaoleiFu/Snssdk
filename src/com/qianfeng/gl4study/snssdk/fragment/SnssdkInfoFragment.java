package com.qianfeng.gl4study.snssdk.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.qianfeng.gl4study.snssdk.R;
import com.qianfeng.gl4study.snssdk.adapter.DiscussListAdapter;
import com.qianfeng.gl4study.snssdk.constant.Constant;
import com.qianfeng.gl4study.snssdk.model.Discuss;
import com.qianfeng.gl4study.snssdk.model.Snssdk;
import com.qianfeng.gl4study.snssdk.tasks.SnssdkTask;
import com.qianfeng.gl4study.snssdk.tasks.TaskProcessor;
import com.qianfeng.gl4study.snssdk.utils.FileCache;
import com.qianfeng.gl4study.snssdk.utils.ImageCache;
import com.qianfeng.gl4study.snssdk.utils.ImageLoader;
import com.qianfeng.gl4study.snssdk.view.MyListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * 显示段子详情及评论信息
 * Created with IntelliJ IDEA.
 * I'm glad to share my knowledge with you all.
 * User:Gaolei
 * Date:2015/3/11
 * Email:pdsfgl@live.com
 */
public class SnssdkInfoFragment extends Fragment implements TaskProcessor, View.OnClickListener {

	private LinkedList<Discuss> dataFresh;
	private LinkedList<Discuss> dataTop;
	private DiscussListAdapter adapterHot;
	private MyListView recyclerViewHot;
	private SnssdkTask discussTask;
	private MyListView recyclerViewFresh;
	private DiscussListAdapter adapterFresh;
	private View view;
	private TextView freshDiscussCount;
	private TextView hotDiscussCount;
	private Snssdk snssdk;
	private ImageView imgGood;
	private ImageView imgBad;
	private ImageView imgHot;
	private TextView txtGood;
	private TextView txtBad;
	private TextView txtHot;


	/**
	 * 初始化视图信息
	 */
	@Override
	public void onStart() {
		super.onStart();
		displaySnssdk(snssdk);
		displayDiscuss(snssdk);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.snssdk_info_fragment, container, false);
		Bundle arguments = getArguments();
		Serializable serializable = arguments.getSerializable("snssdk");
		dataFresh = new LinkedList<Discuss>();
		dataTop = new LinkedList<Discuss>();
		recyclerViewHot = (MyListView) view.findViewById(R.id.list_view_hot_discuss);
		recyclerViewFresh = (MyListView) view.findViewById(R.id.list_view_fresh_discuss);
		freshDiscussCount = (TextView) view.findViewById(R.id.txt_fresscuss);
		hotDiscussCount = (TextView) view.findViewById(R.id.txt_hot_discuss);
		ScrollView discussScrollView = (ScrollView)view.findViewById(R.id.snssdk_info_scroll_view);
		discussScrollView.scrollTo(0,0);
		if(null!=serializable&&serializable instanceof Snssdk){
			snssdk = (Snssdk)serializable;
		}
		return view;
	}


	/**
	 * 显示评论信息
	 * @param snssdk
	 */
	private void displayDiscuss(Snssdk snssdk){
		discussTask = new SnssdkTask(this);
		//主连接，段子Id，异步类型标记，返回评论数量，返回评论起点

		StringBuilder stringBuilder = new StringBuilder();
		String groupId="group_id="+snssdk.getGroupId();
		String count="&count=10";		//返回的新鲜评论数量
		String offset="&offset=0";    //返回的新鲜评论的起点
		stringBuilder.append(Constant.DISCUSS_CONTENT_LIST_URL).append(groupId).append(count).append(offset);

		discussTask.execute(stringBuilder.toString(),"1");
		adapterHot = new DiscussListAdapter(getActivity(), dataTop);
		adapterFresh = new DiscussListAdapter(getActivity(),dataFresh );
		recyclerViewHot.setAdapter(adapterHot);
		recyclerViewFresh.setAdapter(adapterFresh);
		Log.d("SnssdkInfoActivity", "displayDiscuss");
	}

	/**
	 * 显示段子信息
	 * @param snssdk
	 */
	private void displaySnssdk(Snssdk snssdk){

		TextView itemWord = (TextView) view.findViewById(R.id.item_fragment_word);
		//VideoView itemVideo = (VideoView) view.findViewById(R.id.item_fragment_video);
		ImageView itemImage = (ImageView) view.findViewById(R.id.item_fragment_image);
		ImageView itemIconUser = (ImageView) view.findViewById(R.id.item_fragment_user_icon);
		TextView itemUserName = (TextView) view.findViewById(R.id.item_fragment_user_name);
		loaderImage(itemIconUser,snssdk.getAuthorInformation().getAvatarUrl());
		itemUserName.setText(snssdk.getAuthorInformation().getName());

		Log.d("SnssdkInfoActivity","displaySnssdk");

		int snssdkType = snssdk.getSnssdkType();
		if(snssdkType == 1){
			String content = snssdk.getContent();
			if(null!=content) {
				itemWord.setText(content);
			}
			//	itemVideo.setVisibility(View.INVISIBLE);
			itemImage.setVisibility(View.INVISIBLE);
		}else if(snssdkType == 2){
			String content = snssdk.getContent();
			if(null!=content) {
				itemWord.setText(content);
			}
			loaderImage(itemImage,snssdk.getImageUrl());
			//		itemVideo.setVisibility(View.GONE);
		}else if(snssdkType == 3){
			//	itemVideo
		}

		//控件获取点击监听
		LinearLayout userInfo = (LinearLayout) view.findViewById(R.id.item_fragment_bar_user_ll);
		LinearLayout clickGood = (LinearLayout) view.findViewById(R.id.item_fragment_bar_good_ll);
		LinearLayout clickBad = (LinearLayout) view.findViewById(R.id.item_fragment_bar_bad_ll);
		LinearLayout clickHot = (LinearLayout) view.findViewById(R.id.item_fragment_bar_hot_ll);
		LinearLayout clikForward = (LinearLayout) view.findViewById(R.id.item_fragment_bar_forward_ll);

		//获取ImageView
		imgGood = (ImageView) view.findViewById(R.id.item_fragment_bar_good_img);
		imgBad = (ImageView) view.findViewById(R.id.item_fragment_bar_bad_img);
		imgHot = (ImageView) view.findViewById(R.id.item_fragment_bar_hot_img);
		//获取TextView
		txtGood = (TextView) view.findViewById(R.id.item_fragment_bar_good);
		txtBad = (TextView) view.findViewById(R.id.item_fragment_bar_bad);
		txtHot = (TextView) view.findViewById(R.id.item_fragment_bar_hot);

		userInfo.setOnClickListener(this);
		clickGood.setOnClickListener(this);
		clickBad.setOnClickListener(this);
		clickHot.setOnClickListener(this);
		clikForward.setOnClickListener(this);

	}

	/**
	 * 单击段子上的组件监听
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id){
		case R.id.item_fragment_bar_user_ll://点击用户
			break;
		case R.id.item_fragment_bar_good_ll://点击顶

			if(snssdk.getUserRepin()==1){
				Toast.makeText(getActivity(), "你已经踩了，做人不要矛盾哦", Toast.LENGTH_SHORT).show();
			}else {
				onClickBarImage();
				if (snssdk.getUserDigg() == 0) {
					snssdk.setDiggCount(snssdk.getDiggCount() + 1);
					imgGood.setImageResource(R.drawable.ic_bar_digg_pressed);
					snssdk.setUserDigg(1);
				} else {
					snssdk.setDiggCount(snssdk.getDiggCount() - 1);
					imgGood.setImageResource(R.drawable.ic_bar_digg_normal);
					snssdk.setUserDigg(0);
				}
				Log.d("MainActivity", "item_fragment_bar_good");
			}
			break;
		case R.id.item_fragment_bar_bad_ll://点击踩

			if(snssdk.getUserDigg() == 1){
				Toast.makeText(getActivity(),"你已经顶了，做人不要矛盾哦",Toast.LENGTH_SHORT).show();
			}else {
				onClickBarImage();
				if (snssdk.getUserRepin() == 0) {
					snssdk.setRepinCount(snssdk.getRepinCount() + 1);
					imgBad.setImageResource(R.drawable.ic_bar_bury_pressed);
					snssdk.setUserRepin(1);
				} else {
					snssdk.setRepinCount(snssdk.getRepinCount() - 1);
					imgBad.setImageResource(R.drawable.ic_bar_bury_normal);
					snssdk.setUserRepin(0);
				}
				Log.d("MainActivity", "item_fragment_bar_bad");
			}
			break;
		case R.id.item_fragment_bar_hot_ll://点击评论，跳转到分享页面
			break;
		}

	}

	/**
	 * 先将段子上的顶，踩图标修改成默认的，然后再在相应的位置设置正确的显示
	 */
	private void onClickBarImage(){

		//首先将所有图标设置成暗色，然后点击哪一个哪一个变色即可
		imgGood.setImageResource(R.drawable.ic_bar_digg_normal);
		imgBad.setImageResource(R.drawable.ic_bar_bury_normal);
	}

	/**
	 * 图片段子下载
	 * @param userImage
	 * @param avatarUrl
	 */
	private void loaderImage(ImageView userImage,String avatarUrl){
		userImage.setTag(avatarUrl);
		ImageCache imageCache = ImageCache.getInstance();
		Bitmap bitmap = imageCache.getImage(avatarUrl);
		if(bitmap!=null){
			userImage.setImageBitmap(bitmap);
		}else {
			FileCache fileCache = FileCache.getInstance();
			byte[] bytes = fileCache.getContent(avatarUrl);
			if(bytes!=null&&bytes.length>0){
				Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				userImage.setImageBitmap(bmp);
				imageCache.putImage(avatarUrl,bmp);
			}else {
				ImageLoader imageLoader = new ImageLoader(userImage);
				imageLoader.execute(avatarUrl);
			}
		}
	}

	/**
	 * 异步任务的回调
	 * @param result
	 * @param flag
	 */
	@Override
	public void processResult(JSONObject result, String flag) {

		Log.d("SnssdkInfoActivity","processResult");

		if(result!=null){      //评论列表
			try {
				String resultFlag = result.getString("message");
				if("success".equals(resultFlag)){
					JSONObject dataObject = result.getJSONObject("data");
					int tip = result.getInt("total_number");
					JSONArray dataJSONArray = dataObject.getJSONArray("top_comments");
					int type = Integer.parseInt(flag);
					for (int i = 0; i < dataJSONArray.length(); i++) {
						JSONObject jsonObject = dataJSONArray.getJSONObject(i);
						Discuss discuss = new Discuss();
						discuss.parseInformation(jsonObject, type);
						dataTop.add(discuss);
					}
					//最热评论数据添加完成，更新List
					if(dataTop.size()==0){
						hotDiscussCount.setVisibility(View.INVISIBLE);
					}else {
						hotDiscussCount.setText("热门评论(" + dataTop.size() + ")");
					}
					adapterHot.notifyDataSetChanged();
					dataJSONArray = dataObject.getJSONArray("recent_comments");
					for (int i = 0; i < dataJSONArray.length(); i++) {
						JSONObject jsonObject = dataJSONArray.getJSONObject(i);
						Discuss discuss = new Discuss();
						discuss.parseInformation(jsonObject, type);
						dataFresh.add(discuss);
					}
					//最新评论数据添加完成，更新List
					if (tip == 0) {
						freshDiscussCount.setVisibility(View.INVISIBLE);
					}else {
						freshDiscussCount.setText("新鲜评论("+tip+")");
					}
					adapterFresh.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}



}