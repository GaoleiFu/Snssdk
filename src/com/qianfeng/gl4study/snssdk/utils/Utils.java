package com.qianfeng.gl4study.snssdk.utils;

import android.graphics.*;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.qianfeng.gl4study.snssdk.tasks.ImageLoaderTask;
import com.qianfeng.gl4study.snssdk.tasks.UserAvatarTask;

/**
 * Created with IntelliJ IDEA.
 * I'm glad to share my knowledge with you all.
 * User:Gaolei
 * Date:2015/3/15
 * Email:pdsfgl@live.com
 */
public class Utils {

	/**
	 * 图片段子下载
	 * @param userImage
	 * @param avatarUrl
	 */
	public static void loaderImage(int width,ImageView userImage,String avatarUrl){
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
				if(width == -1){//头像下载
					UserAvatarTask imageLoaderTask = new UserAvatarTask(userImage);
					imageLoaderTask.execute(avatarUrl);
				}else {         //图片下载
					ImageLoaderTask imageLoaderTask = new ImageLoaderTask(width, userImage);
					imageLoaderTask.execute(avatarUrl);
				}
			}
		}
	}
	/**
	 * 图片段子设置
	 * @param width
	 * @param imageView
	 * @param bitmap
	 */
	public static void setImageToView(int width,ImageView imageView,Bitmap bitmap){
		if(imageView!=null) {
			int cWidth = width;
			float cHeight = (float) cWidth / bitmap.getWidth() * bitmap.getHeight();
			ViewGroup.LayoutParams params = imageView.getLayoutParams();
			params.height = (int) cHeight;
			params.width = cWidth;
			imageView.setLayoutParams(params);
			imageView.setImageBitmap(bitmap);
		}
	}
	/**
	 * 将图片截取为圆角图片
	 * @param bitmap 原图片
	 * @param ratio 截取比例，如果是8，则圆角半径是宽高的1/8，如果是2，则是圆形图片
	 * @return 圆角矩形图片
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float ratio) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, bitmap.getWidth()/ratio,
				bitmap.getHeight()/ratio, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}



}
