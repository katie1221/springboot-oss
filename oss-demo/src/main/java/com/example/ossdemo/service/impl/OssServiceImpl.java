package com.example.ossdemo.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectResult;
import com.example.ossdemo.common.OSSConstant;
import com.example.ossdemo.service.OssService;

@Service("OssService")
public class OssServiceImpl implements OssService {

	private String[] fileTypes = new String[]{"gif", "jpg", "jpeg", "png", "bmp"};
	
	/**
	 * 上传文件处理逻辑
	 */
	@Override
	public boolean uploadFile(String filepath, InputStream inputstream) {

		return upload(filepath, inputstream);
	}

	/**
	 * oss文件下载处理逻辑
	 */
	@Override
	public void downFile(HttpServletRequest request, HttpServletResponse response) {
		// 文件地址  http://xiaofang-001.oss-cn-shanghai.aliyuncs.com/file/20201216/093915477_23.docx
		//String oss_url = request.getParameter("url");
		String oss_url = "http://xiaofang-001.oss-cn-shanghai.aliyuncs.com/file/20201216/093915477_23.docx";

		// 获取域名后面的内容
		String oss_domain = OSSConstant.OSS_PIC_URL;
		String file_name = oss_url.replace(oss_domain, "");
		// 获取oss文件byte[]
		byte[] oss_byte = getOssFileByteArray(file_name);
		// 后缀名
		String fileExt = oss_url.substring(oss_url.lastIndexOf(".") + 1).toLowerCase();

		try {
			// 清空response
			response.reset();
			// 设置response的Header
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "." + fileExt);
			// response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(oss_byte);// 以流的形式下载文件。
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
	
	/**
	 * 上传文件
	 */
	public boolean upload(String filepath, InputStream inputstream) {
		boolean result = false;
		// 初始化配置参数
		String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;
		OSSClient ossClient = null;
		try {
			if (filepath != null && !"".equals(filepath.trim())) {
				// 创建ClientConfiguration实例，按照您的需要修改默认参数
				ClientConfiguration conf = new ClientConfiguration();
				// 开启支持CNAME选项
				conf.setSupportCname(true);
				ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);

				// 上传
				ossClient.putObject(OSS_BUCKET, filepath, inputstream);
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("文件上传异常");
		} finally {
			// 关闭client
			ossClient.shutdown();
		}
		return result;
	}

    /**
     * 列出oss bucket中的文件及目录
     *
     * @param dirname 目录名称,如： image/ , image/20201224/
     */
    public List<Map<String,Object>> list_file(String dirname) {
        // 初始化配置参数
    	String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;
        List<Map<String,Object>> fileList = new ArrayList<Map<String,Object>>();
        OSSClient ossClient = null;
        try {
            if (dirname != null && !"".equals(dirname.trim()) && dirname.endsWith("/")) {
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                // 开启支持CNAME选项
                conf.setSupportCname(true);
                ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);

                final int maxKeys = 200;
                String nextMarker = null;
                ObjectListing objectListing = null;

                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(OSS_BUCKET);
                listObjectsRequest.withMarker(nextMarker);
                listObjectsRequest.withMaxKeys(maxKeys);
                listObjectsRequest.withPrefix(dirname);
                objectListing = ossClient.listObjects(listObjectsRequest);
                List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (OSSObjectSummary s : sums) {
                    String key = s.getKey();
                    String lastmodifytime = sf.format(s.getLastModified());
                    System.out.println(key);
                    long size = s.getSize();
                    if (!key.equals(dirname)) {
                    	Map<String,Object> file = new HashMap<String, Object>();
                        file.put("lastmodifytime",lastmodifytime);//最新修改时间
                        file.put("filesize",size);//文件尺寸
                        String sub_filepath = key.substring((key.indexOf(dirname) + dirname.length()), key.length());
                        int count = appearNumber(sub_filepath, "/");
                        if (count == 1 && sub_filepath.endsWith("/")) { // 子目录
                        	file.put("is_dir",true);//是否目录
                            boolean hasfile = check_has_file(sums, dirname + sub_filepath);
                            file.put("has_file",hasfile);//是否含有子文件
                            file.put("is_photo",false);//是否图片
                            file.put("filetype","");//文件类型
                            String filename = sub_filepath.substring(0, sub_filepath.length() - 1);
                            file.put("filename",filename);//文件名
                            fileList.add(file);
                        } else if (count == 0) { // 文件
                        	file.put("is_dir",false);//是否目录
                        	file.put("has_file",false);//是否含有子文件
                            String fileExt = key.substring(key.lastIndexOf(".") + 1).toLowerCase();
                            file.put("is_photo",Arrays.<String>asList(fileTypes).contains(fileExt));//是否图片
                            file.put("fileExt",fileExt);//文件类型
                            file.put("filename",sub_filepath);//文件名
                            fileList.add(file);
                        }
                    }
                }
                nextMarker = objectListing.getNextMarker();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取文件列表异常");
        } finally {
            if (ossClient != null) {
                // 关闭client
                ossClient.shutdown();
            }
        }
        return fileList;
    }
	/**
	 * public int indexOf(int ch, int fromIndex) 返回在此字符串中第一次出现指定字符处的索引，从指定的索引开始搜索
	 * 
	 * @param srcText
	 * @param findText
	 * @return
	 */
	public static int appearNumber(String srcText, String findText) {
		int count = 0;
		int index = 0;
		while ((index = srcText.indexOf(findText, index)) != -1) {
			index = index + findText.length();
			count++;
		}
		return count;
	}
	
    private boolean check_has_file(List<OSSObjectSummary> sums, String dirname) {
        boolean result = false;
        for (OSSObjectSummary s : sums) {
            String path = s.getKey();
            if (!dirname.equals(path) && path.startsWith(dirname)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    /**
     * 检验oss bucket 某个目录下是否存在文件
     *
     * @param dirname 目录名称,如： image/ , image/20201224/
     */
    public boolean check_is_empty(String dirname) {
        boolean result = false;
        // 初始化配置参数
        String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;
        OSSClient ossClient = null;
        try {
            if (dirname != null && !"".equals(dirname.trim()) && dirname.endsWith("/")) {
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                // 开启支持CNAME选项
                conf.setSupportCname(true);
                ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);

                // 列举Object
                ObjectListing objectListing = ossClient.listObjects(OSS_BUCKET, dirname);
                List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
                if (sums.size() > 0) {
                    result = true;
                }
                System.out.println("检索目录：" + dirname + " 是否存在：" + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("校验文件异常");
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
        return result;
    }
    
    /**
     * 检验oss bucket中是否存在目录
     *
     * @param dirname 目录名称,如： image/ , image/20201224/
     */
    public boolean check_dir_exist(String dirname) {
        boolean result = false;
        // 初始化配置参数
        String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;
        OSSClient ossClient = null;
        try {
            if (dirname != null && !"".equals(dirname.trim()) && dirname.endsWith("/")) {
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                // 开启支持CNAME选项
                conf.setSupportCname(true);
                ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);

                // Object是否存在
                result = ossClient.doesObjectExist(OSS_BUCKET, dirname.trim());
                System.out.println("检索目录：" + dirname + " 是否存在：" + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件校验异常");
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
        return result;
    }
    

    /**
     * 在oss bucket中创建目录
     *
     * @param dirname 目录名称,如： image/ , image/20201224/
     */
    public boolean create_dir(String dirname) {
        boolean result = false;
        // 初始化配置参数
        String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;
        OSSClient ossClient = null;
        try {
            if (dirname != null && !"".equals(dirname.trim()) && dirname.endsWith("/")) {
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                // 开启支持CNAME选项
                conf.setSupportCname(true);
                ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);

                PutObjectResult r = ossClient.putObject(OSS_BUCKET, dirname, new ByteArrayInputStream(new byte[0]));
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("目录创建异常");
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
        return result;
    }
    
    /**
     * 上传文件
     */
    public boolean upload(String filepath, FileInputStream fileinputstream) {
        boolean result = false;
        // 初始化配置参数
        String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;
        OSSClient ossClient = null;
        try {
            if (filepath != null && !"".equals(filepath.trim())) {
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                // 开启支持CNAME选项
                conf.setSupportCname(true);
                ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);

                // 上传
                ossClient.putObject(OSS_BUCKET, filepath, fileinputstream);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传异常");
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
        return result;
    }
    /**
     * 上传文件
     */
    public boolean upload(String filepath, byte[] filecontent) {
        boolean result = false;
        // 初始化配置参数
        String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;
        OSSClient ossClient = null;
        try {
            if (filepath != null && !"".equals(filepath.trim())) {
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                // 开启支持CNAME选项
                conf.setSupportCname(true);
                ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);

                // 上传
                ossClient.putObject(OSS_BUCKET, filepath, new ByteArrayInputStream(filecontent));
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传异常");
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
        return result;
    }

    /**
     * 上传文件
     */
    public boolean upload(String filepath, ByteArrayInputStream bytestream) {
        boolean result = false;
        // 初始化配置参数
        String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;

        OSSClient ossClient = null;
        try {
            if (filepath != null && !"".equals(filepath.trim())) {
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                // 开启支持CNAME选项
                conf.setSupportCname(true);
                ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);

                // 上传
                ossClient.putObject(OSS_BUCKET, filepath, bytestream);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传异常");
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
        return result;
    }
    
    /**
     * 删除bucket中的文件
     */
    public boolean delete(String filename) {
        boolean result = false;
        // 初始化配置参数
        String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;
        OSSClient ossClient = null;
        try {
            if (filename != null && !"".equals(filename.trim())) {
                filename = filename.trim();
                String key = filename.substring(OSS_BUCKET.length() - 1, filename.length());
                // 创建ClientConfiguration实例，按照您的需要修改默认参数
                ClientConfiguration conf = new ClientConfiguration();
                // 开启支持CNAME选项
                conf.setSupportCname(true);
                ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);
                // 删除
                ossClient.deleteObject(OSS_BUCKET, key);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件删除异常");
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
        return result;
    }
    
	/**
	 * 获取oss文件byte[]
	 */
	public byte[] getOssFileByteArray(String filepath) {
		byte[] result = null;
		// 初始化配置参数
		String OSS_ENDPOINT = OSSConstant.OSS_ENDPOINT;
		String OSS_ACCESSKEYID = OSSConstant.OSS_ACCESSKEYID;
		String OSS_ACCESSKEYSECRET = OSSConstant.OSS_ACCESSKEYSECRET;
		String OSS_BUCKET = OSSConstant.OSS_BUCKET;
		OSSClient ossClient = null;
		try {
			if (filepath != null && !"".equals(filepath.trim())) {
				// 创建ClientConfiguration实例，按照您的需要修改默认参数
				ClientConfiguration conf = new ClientConfiguration();
				// 开启支持CNAME选项
				conf.setSupportCname(true);
				ossClient = new OSSClient(OSS_ENDPOINT, OSS_ACCESSKEYID, OSS_ACCESSKEYSECRET, conf);

				// 上传
				OSSObject ossObj = ossClient.getObject(OSS_BUCKET, filepath);
				if (ossObj != null) {
					InputStream is = ossObj.getObjectContent();
					result = InputStreamToByteArray(is);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("文件下载异常");
		} finally {
			// 关闭client
			ossClient.shutdown();
		}
		return result;
	}

	/**
	 * 1.图片转为字节数组 
	 * 图片到程序FileInputStream 
	 * 程序到数组 ByteArrayOutputStream
	 */
	public static byte[] InputStreamToByteArray(InputStream is) {
		// 1.创建源与目的的
		byte[] dest = null;// 在字节数组输出的时候是不需要源的。
		// 2.选择流，选择文件输入流
		ByteArrayOutputStream os = null;// 新增方法
		try {
			os = new ByteArrayOutputStream();
			// 3.操作,读文件
			byte[] flush = new byte[1024 * 10];// 10k，创建读取数据时的缓冲，每次读取的字节个数。
			int len = -1;// 接受长度；
			while ((len = is.read(flush)) != -1) {
				// 表示当还没有到文件的末尾时
				// 字符数组-->字符串，即是解码。
				os.write(flush, 0, len);// 将文件内容写出字节数组
			}
			os.flush();
			return os.toByteArray();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 4.释放资源
			try {
				if (is != null) {// 表示当文打开时，才需要通知操作系统关闭
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;

	}
	
	/**
     * 2.将字节数组还原为文件
     * 字节数组到程序ByteArrayInputStream
     * 程序到文件。FileOutputStream
     */
    public static void byteArrayToFile(byte[] src, String FilePath) {
        //1.创建源
        File dest = new File(FilePath);//该文件是不存在的，会在相应的路径下创建

        // 2.选择流
        FileOutputStream os = null;
        ByteArrayInputStream is = null;

        try {
            //3.操作
            os = new FileOutputStream(dest);
            is = new ByteArrayInputStream(src);
            //将内容写出
            byte[] flush = new byte[1024 * 10];//创建读取数据时的缓冲，每次读取的字节个数。
            int len = -1;//接受长度；
            while ((len = is.read(flush)) != -1) {
                //表示当还没有到文件的末尾时
                //字符数组-->字符串，即是解码。
                os.write(flush, 0, len);//

            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            //4.释放资源
            try {
                if (null != os) {
                    os.close();
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

}
