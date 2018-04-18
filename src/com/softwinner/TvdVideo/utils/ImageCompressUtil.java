package com.softwinner.TvdVideo.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片压缩工具类
 *
 * @author 丶Life_
 *
 */
public class ImageCompressUtil {
	public static long UPLOADIMG_MAX_SIZE = 524288;
	public static String _LOW_PROJECT_IMG_SRC = "/mnt/sdcard/BlueCollar/pic/";
	public static String _UP_PROJECT_IMG_SRC = "/storage/sdcard/BlueCollar/pic/";

	/**
	 * 通过降低图片的质量来压缩图片
	 *
	 * @param bitmap
	 *            要压缩的图片位图对象
	 * @param maxSize
	 *            压缩后图片大小的最大值,单位KB
	 * @return 压缩后的图片位图对象
	 */
	public static Bitmap compressByQuality(Bitmap bitmap, int maxSize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		bitmap.compress(CompressFormat.JPEG, quality, baos);
		System.out.println("图片压缩前大小：" + baos.toByteArray().length + "byte");
		boolean isCompressed = false;
		while (baos.toByteArray().length / 1024 > maxSize) {
			quality -= 10;
			baos.reset();
			bitmap.compress(CompressFormat.JPEG, quality, baos);
			System.out.println("质量压缩到原来的" + quality + "%时大小为：" + baos.toByteArray().length + "byte");
			isCompressed = true;
		}
		System.out.println("图片压缩后大小：" + baos.toByteArray().length + "byte");
		if (isCompressed) {
			Bitmap compressedBitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
			recycleBitmap(bitmap);
			return compressedBitmap;
		} else {
			return bitmap;
		}
	}

	/**
	 * 通过压缩图片的尺寸来压缩图片大小，仅仅做了缩小，如果图片本身小于目标大小，不做放大操作
	 *
	 * @param pathName
	 *            图片的完整路径
	 * @param targetWidth
	 *            缩放的目标宽度
	 * @param targetHeight
	 *            缩放的目标高度
	 * @return 缩放后的图片
	 */
	public static Bitmap compressBySize(String pathName, int targetWidth, int targetHeight) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
		// 得到图片的宽度、高度；
		int imgWidth = opts.outWidth;
		int imgHeight = opts.outHeight;
		// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
		int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
		int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
		if (widthRatio > 1 || heightRatio > 1) {
			if (widthRatio > heightRatio) {
				opts.inSampleSize = widthRatio;
			} else {
				opts.inSampleSize = heightRatio;
			}
		}
		// 设置好缩放比例后，加载图片进内容；
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(pathName, opts);
		return bitmap;
	}

	/**
	 * 通过压缩图片的尺寸来压缩图片大小
	 *
	 * @param bitmap
	 *            要压缩图片
	 * @param targetWidth
	 *            缩放的目标宽度
	 * @param targetHeight
	 *            缩放的目标高度
	 * @return 缩放后的图片
	 */
	public static Bitmap compressBySize(Bitmap bitmap, int targetWidth, int targetHeight) {
		if (bitmap != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, baos);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
			// 得到图片的宽度、高度；
			int imgWidth = opts.outWidth;
			int imgHeight = opts.outHeight;
			// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
			int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
			int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
			if (widthRatio > 1 || heightRatio > 1) {
				if (widthRatio > heightRatio) {
					opts.inSampleSize = widthRatio;
				} else {
					opts.inSampleSize = heightRatio;
				}
			}
			// 设置好缩放比例后，加载图片进内存；
			opts.inJustDecodeBounds = false;
			Bitmap compressedBitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
			recycleBitmap(bitmap);
			return compressedBitmap;
		}
		return bitmap;
	}

	/**
	 * Compress image by size, this will modify image width/height. Used to get
	 * thumbnail
	 *
	 * @param image
	 * @param pixelW
	 *            target pixel of width
	 * @param pixelH
	 *            target pixel of height
	 * @return
	 */
	public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, os);
		if (os.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			os.reset();// 重置baos即清空baos
			image.compress(CompressFormat.JPEG, 50, os);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
		float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		is = new ByteArrayInputStream(os.toByteArray());
		bitmap = BitmapFactory.decodeStream(is, null, newOpts);
		// 压缩好比例大小后再进行质量压缩
		// return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
		return bitmap;
	}

	/**
	 * 通过压缩图片的尺寸来压缩图片大小，通过读入流的方式，可以有效防止网络图片数据流形成位图对象时内存过大的问题；
	 *
	 * @param is
	 *            要压缩图片，以流的形式传入
	 * @param targetWidth
	 *            缩放的目标宽度
	 * @param targetHeight
	 *            缩放的目标高度
	 * @return 缩放后的图片
	 * @throws IOException
	 *             读输入流的时候发生异常
	 */
	public static Bitmap compressBySize(InputStream is, int targetWidth, int targetHeight) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int len = 0;
		while ((len = is.read(buff)) != -1) {
			baos.write(buff, 0, len);
		}

		byte[] data = baos.toByteArray();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		// 得到图片的宽度、高度；
		int imgWidth = opts.outWidth;
		int imgHeight = opts.outHeight;
		// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
		int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
		int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
		if (widthRatio > 1 || heightRatio > 1) {
			if (widthRatio > heightRatio) {
				opts.inSampleSize = widthRatio;
			} else {
				opts.inSampleSize = heightRatio;
			}
		}
		// 设置好缩放比例后，加载图片进内存；
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		return bitmap;
	}

	/**
	 * 旋转图片摆正显示
	 *
	 * @param srcPath
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotateBitmapByExif(String srcPath, Bitmap bitmap) {
		ExifInterface exif;
		Bitmap newBitmap = null;
		try {
			exif = new ExifInterface(srcPath);
			if (exif != null) { // 读取图片中相机方向信息
				int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				int digree = 0;
				switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					digree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					digree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					digree = 270;
					break;
				}
				if (digree != 0) {
					Matrix m = new Matrix();
					m.postRotate(digree);
					newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
					recycleBitmap(bitmap);
					return newBitmap;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 回收位图对象
	 *
	 * @param bitmap
	 */
	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			System.gc();
			bitmap = null;
		}
	}

	public String saveBitmapFile(Context ctx, Bitmap bitmap) {
		String tempPath = String.valueOf(System.currentTimeMillis());
		int sysVersion = Integer.parseInt(Build.VERSION.SDK);
		String savePath = ctx.getFilesDir().getPath();

		File file = new File(savePath + "/" + tempPath + ".jpg");// 将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			return file.getPath();
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		}
	}

	/**
	 * 修改Bitmap的长宽为原来的inSampleSize分之一
	 * 
	 * @param inSampleSize
	 * @return
	 */
	private BitmapFactory.Options getBitmapOption(int inSampleSize) {
		System.gc();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inSampleSize = inSampleSize;
		return options;
	}

	public static boolean isHasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	private void makeRootDirectory(String filePath) {
		File file = null;
		String newPath = null;
		String[] path = filePath.split("/");
		for (int i = 0; i < path.length; i++) {
			if (newPath == null) {
				newPath = path[i];
			} else {
				newPath = newPath + "/" + path[i];
			}
			file = new File(newPath);
			if (!file.exists()) {
				file.mkdir();
			}
		}
	}

	public String getGenPath() {
		boolean hasSdCard = ExistSDCard();
		if (hasSdCard) {
			Log.e("有无内存卡", "有");
			Log.e("有无内存卡", Environment.getExternalStorageDirectory().getPath());
			return Environment.getExternalStorageDirectory().getPath();
		} else {
			Log.e("有无内存卡", "无");
			Log.e("有无内存卡", Environment.getRootDirectory().getPath());
			return Environment.getRootDirectory().getPath();
		}
	}

	private boolean ExistSDCard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	public static String getAbsoluteImagePath(Context context, Uri uri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, proj, // Which
																		// columns
																		// to
																		// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

}