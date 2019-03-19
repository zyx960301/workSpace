<%--
  Created by IntelliJ IDEA.
  User: 57473
  Date: 2018/9/29
  Time: 16:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h3>账户列表</h3>
<table border="1" cellpadding="10" cellspacing="0">
    <tr>
        <td>账号ID</td>
        <td>账号名称</td>
        <td>余额</td>
    </tr>
    <tr>
        <c:forEach items="${requestScope.accounts}" var="account">
    <tr>
        <td>${account.id}</td>
        <td>${account.name}</td>
        <td>${account.money}</td>
    </tr>
    </c:forEach>
    </tr>
</table>
</body>
</html>
