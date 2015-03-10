package com.qianfeng.gl4study.snssdk.tasks;

import android.os.AsyncTask;
import com.qianfeng.gl4study.snssdk.utils.HttpTool;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * 获取商城的分类信息
 * Created with IntelliJ IDEA.
 * I'm glad to share my knowledge with you all.
 * User:Gaolei
 * Date:2015/2/5
 * Email:pdsfgl@live.com
 */
public class SnssdkTask extends AsyncTask<String ,Integer,JSONObject> {

	private TaskProcessor processor;
	private String  flag = "1";

	public SnssdkTask(TaskProcessor processor){
		this.processor = processor;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		JSONObject ret = null;
		if(params!=null&&params.length>1){
			String url = params[0];
			flag  = params[1];
			byte[] bytes = HttpTool.get(url);
			if(bytes!=null){
				try {
					String str = new String(bytes, "UTF-8");
					ret = new JSONObject(str);

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	@Override
	protected void onPostExecute(JSONObject jsonObject) {
		if(processor!=null){
			processor.processResult(jsonObject,flag);
		}
	}
}