package com.rongchao.controller;

import java.io.*;
import java.util.List;

import com.rongchao.utils.JsonUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.rongchao.enums.VideoStatusEnum;
import com.rongchao.pojo.Bgm;
import com.rongchao.service.VideoService;
import com.rongchao.utils.RongchaoJSONResult;
import com.rongchao.utils.PagedResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@Controller
@RequestMapping("video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /*@Value("${FILE_SPACE}")*/
    private String FILE_SPACE = "/rongchao_videos_dev/mvc-bgm";

    @GetMapping("/showReportList")
    public String showReportList() {
        return "video/reportList";
    }

    @PostMapping("/reportList")
    @ResponseBody
    public PagedResult reportList(Integer page) {

        PagedResult result = videoService.queryReportList(page, 10);
        return result;
    }

    @PostMapping("/forbidVideo")
    @ResponseBody
    public RongchaoJSONResult forbidVideo(String videoId) {

        videoService.updateVideoStatus(videoId, VideoStatusEnum.FORBID.value);
        return RongchaoJSONResult.ok();
    }

    @GetMapping("/showBgmList")
    public String showBgmList() {
        return "video/bgmList";
    }

    @PostMapping("/queryBgmList")
    @ResponseBody
    public PagedResult queryBgmList(Integer page) {
        return videoService.queryBgmList(page, 10);
    }

    @GetMapping("/showAddBgm")
    public String login() {
        return "video/addBgm";
    }

    @GetMapping("/showUpload")
    public String showUpload() {
        return "video/addPic";
    }


    @PostMapping("/addBgm")
    @ResponseBody
    public RongchaoJSONResult addBgm(Bgm bgm, HttpServletRequest request) {
        String path = request.getServletContext().getRealPath("/upload");
        bgm.setPath(path);
        videoService.addBgm(bgm);
        return RongchaoJSONResult.ok();
    }

    @PostMapping("/delBgm")
    @ResponseBody
    public RongchaoJSONResult delBgm(String bgmId) {
        videoService.deleteBgm(bgmId);
        return RongchaoJSONResult.ok();
    }

    @PostMapping("/bgmUpload")
    @ResponseBody
    public RongchaoJSONResult bgmUpload(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws Exception {
        System.out.println("上传开始!");
        // 文件保存的命名空间
        String fileSpace = File.separator + "rongchao_videos_dev" + File.separator + "mvc-bgm";
//		String fileSpace = "C:" + File.separator + "rongchao_videos_dev" + File.separator + "mvc-bgm";

        //  /得到上传文件的保存目录，将上传的文件放在webRoot目录下（但是一般为了安全放在WEB-INF目录下，不允许外界直接访问，保证上传的安全）
        String path = request.getServletContext().getRealPath("/upload");

        // 保存到数据库中的相对路径
        String uploadPathDB = path + "bgm";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {

                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    // String finalPath = FILE_SPACE + uploadPathDB + File.separator + fileName;
                    System.out.println(path);
                    // 设置数据库保存的路径
                    uploadPathDB += (File.separator + fileName);

                    File outFile = new File(path);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    System.out.println("sdasdadadadssa" + path);
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return RongchaoJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RongchaoJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return RongchaoJSONResult.ok(uploadPathDB);
    }

    @CrossOrigin
    @RequestMapping(value = "/uploadVideo")
    @ResponseBody
    public RongchaoJSONResult uploadVideo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("////////");
        String path = request.getServletContext().getRealPath("/upload");
        File file = new File(path);

        //判断上传文件的保存目录是否存在
        if (!file.exists() && !file.isDirectory()) {
            System.out.println(path + "目录不存在，需要创建！");
            //创建目录
            file.mkdir();
        }
        //消息提示
        String message = "";
        String filename = "";
        try {
            //使用Apache文件上传组件处理文件上传步骤：
            //1.创建一个DiskFileItemFactory工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //2.创建一个文件上传解析器
            ServletFileUpload upload = new ServletFileUpload(factory);
            //解决中文乱码
            upload.setHeaderEncoding("UTF-8");
            //3.判断提交的数据普通表单的数据还是带文件上传的表单
                        /*if(!upload.isMultipartContent(request)){
                              //如果是表单数据普通表单,则按照传统方式获取数据
                               return  ;
                             }*/
            //4.使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
            List<FileItem> list = upload.parseRequest(request);
            for (FileItem item : list) {
                //如果fileItem中封装的是普通输入项的数据
                if (item.isFormField()) {
                    //获取字段名字
                    String name = item.getFieldName();
                    //解决普通输入项中中文乱码问题
                    String value = item.getString("UTF-8");//value = new String(value.getBytes("iso8859-1"),"UTF-8");
                    System.out.println(name + " = " + value);
                } else {    //如果表单中提交的是上传文件
                    //获得上传的文件名称
                   filename = item.getName();
                    System.out.println(filename);
                    if (filename == null || filename.trim().equals(" ")) {
                        continue;
                    }
                    //注意：不同的浏览器提交的文件名称是不一样的，有些浏览器提交的文件会带有路径，如“D:\\project\WebRoot\hello.jsp”，有一些是单纯的文件名：hello.jsp
                    //去掉获取到文件名中的路径名，保留单纯的文件名
                    filename = filename.substring(filename.lastIndexOf("\\") + 1);
                    //获取item中的上传文件的输入流
                    InputStream in = item.getInputStream();
                    //创建一个文件输入流
                    FileOutputStream out = new FileOutputStream(path + "\\" + filename);
                    //创建一个缓冲区
                    byte buffer[] = new byte[1024];
                    //判断输入流中的数据是否已经读取完毕的标志位
                    int len = 0;
                    //循环将输入流读入到缓冲区当中，（len = in.read(buffer)>0）就表示in里面还有数据存在
                    while ((len = in.read(buffer)) > 0) {
                        //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录（path+"\\"+filename）当中
                        out.write(buffer, 0, len);
                    }
                    //关闭输入流
                    in.close();
                    //关闭输出流
                    out.close();
                    //删除处理文件上传生成的临时文件
                    item.delete();
                    message = "文件上传成功!";


                }
            }

        } catch (Exception e) {
            message = "文件上传失败！";
            e.printStackTrace();
        }
        message = path+"\\"+filename;
        return RongchaoJSONResult.ok(message);
    }

    /**
     * 文件下载
     * 1.设置文件下载名
     * 2.
     */
    public RongchaoJSONResult downloadVideo(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        if (path.isEmpty()) {
            return RongchaoJSONResult.errorMsg("下载地址为空!");
        }else {
            //开始下载业务
            //文件名
            String fileName = path.substring(path.lastIndexOf("/"),path.length());
            //文件存放的路径
            String filePath = request.getServletContext().getRealPath("/upload")+fileName;
            //读到流中,
            InputStream fileInputStream = new FileInputStream(filePath);
            //设置输出格式
            response.reset();
            response.setContentType("bin");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            //循环读取流中字节(数据)
            byte[] b = new byte[1024];
            int len = 0;
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
           /* while (len = fileInputStream.read(b) != -1) {
                response.getOutputStream().write(b, 0, len);
            }*/

            bufferedInputStream.close();
            fileInputStream.close();
        }

        return null;
    }

}
