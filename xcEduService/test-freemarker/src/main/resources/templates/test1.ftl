<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
Hello${name}!
</br>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
        <td>日期</td>
    </tr>
    <#if stus ??></#if>
    <#list stus as stu >
        <tr>
            <td>${stu_index+1}</td>
            <td <#if stu.name == '小明'>style="background: aqua" </#if>>${stu.name}</td>
            <td>${stu.age}</td>
            <td <#if (stu.mondy) gt 300>style="background: crimson" </#if>>${stu.mondy}</td>
            <td>${stu.birthday?string("YYYY年MM月dd日")}</td>
        </tr>
    </#list>
</table></br>
姓名：${stuMap['stu1'].name!''}</br>
姓名：${stuMap.stu1.name}</br>
<#list stuMap?keys as k>
姓名：${stuMap[k].name}</br>
</#list>
</body>
</html>