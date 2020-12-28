package com.example.ossdemo.common;

/**
 * OSS 常量类
 * @author Administrator
 *
 */
public class OSSConstant {

	//oss对外服务的访问域名
	public static final String OSS_ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com/";
	//访问身份验证中用到的用户标识
	public static final String OSS_ACCESSKEYID = "<your KEY>";
	//用户用于加密签名字符串和oss用来验证签名字符串的 密钥
	public static final String OSS_ACCESSKEYSECRET = "<your SECRET>";
	//oss的存储空间
	public static final String OSS_BUCKET = "cf-001";
	//阿里云OSS文件地址
	public static final String OSS_PIC_URL = "http://cf-001.oss-cn-shanghai.aliyuncs.com/";
}
