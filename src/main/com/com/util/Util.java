package com.util;

import com.bind.Bind;
import com.util.Profile;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	public static final String systemdir = System.getProperty("user.dir");
	String logger = "log";
	String handler = "log";
	String formatter = "";
//	handler.setFormatter(formatter)
//  logger.addHandler(handler)
//	logger.setLevel(logging.WARNING)

	public JSONObject server_info() {

//		Mem mem=null;
//		CpuInfo[] infos=null;
//		Sigar sigar = new Sigar();
//		try {
//			mem = sigar.getMem();
//			infos = sigar.getCpuInfoList();
//		}catch (Exception e){
//			System.out.println(e);
//		}

		JSONObject sysinf = new JSONObject();
		sysinf.put("hostname", "");

//		JSONObject cpusinf=new JSONObject();
//		JSONArray cpuspeed=new JSONArray();
//		for (int i=0;i<infos.length;i++){
//			cpuspeed.put(infos[i].getMhz());
//		}
//		cpusinf.put("num",infos.length);
//		cpusinf.put("speed",cpuspeed);
//		sysinf.put("cpu",cpusinf);
		sysinf.put("cpu", 1);

//		JSONObject totalcore=new JSONObject();
//		JSONArray core=new JSONArray();
//		int sum=0;
//		for (int i=0;i<infos.length;i++){
//			cpuspeed.put(infos[i].getTotalCores());
//			sum+=infos[i].getTotalCores();
//		}
//		totalcore.put("num",sum);
//		totalcore.put("each",core);
		sysinf.put("cpu_core", 4);

//		sysinf.put("memory",mem.getTotal());
		sysinf.put("memory", 8);
		sysinf.put("core_version", Bind.ver);
		sysinf.put("judger_version", Profile.ver);
		return sysinf;
	}

	public String get_token() {
//		token = os.environ.get("TOKEN")
//		if token:
//			return token
//    	else:
//			raise JudgeClientError("env 'TOKEN' not found")
		return null;
	}
//	token = hashlib.sha256(get_token().encode("utf-8")).hexdigest()

	public static String getSHA256StrJava(String str) {
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodeStr = byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodeStr;
	}

	private static String byte2Hex(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				//1得到一位的进行补0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}

	public static boolean deleteFolder(String url) {
		File file = new File(url);
		if (!file.exists()) {
			return false;
		}
		if (file.isFile()) {
			file.delete();
			return true;
		} else {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				//得到子文件或文件夹的绝对路径
				String root = files[i].getAbsolutePath();
				//System.out.println(root);
				deleteFolder(root);
			}
			file.delete();
			return true;
		}
	}

	public static String getMD5(String message) {
		String md5str = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] input = message.getBytes();
			byte[] buff = md.digest(input);
			md5str = bytesToHex(buff);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5str;
	}

	public static String bytesToHex(byte[] bytes) {
		StringBuffer md5str = new StringBuffer();
		// 把数组每一字节换成16进制连成md5字符串
		int digital;
		for (int i = 0; i < bytes.length; i++) {
			digital = bytes[i];

			if (digital < 0) {
				digital += 256;
			}
			if (digital < 16) {
				md5str.append("0");
			}
			md5str.append(Integer.toHexString(digital));
		}
		return md5str.toString().toUpperCase();
	}
}