<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

2 <%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<script>
    function checkForm() {

        alert("提交")
        $("#Form").submit();


    }


</script>
<tr>
    <td>头像：</td>
    <td>
        <form action="<%=request.getContextPath() %>/video/uploadVideo.action" method="post"
              enctype="multipart/form-data">

            username: <input type="text" name="username"/><br>
            file: <input type="file" name="file"><br>
            file2: <input type="file" name="file2"><br>
            <input type="submit" value="上传文件">
        </form>

    </td>
</tr>