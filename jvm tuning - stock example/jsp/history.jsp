<%-- 
 Copyright (c) 2013,2014 Scott Oaks. All rights reserved.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Stock Price History</title>
    </head>
    <body>
        <h1>Stock Price History for ${history.symbol}</h1>
        For period ${history.firstDate} to ${history.lastDate}
        <br>
        <br><strong>High: </strong>${history.highPrice}
        <br><strong>Low: </strong>${history.lowPrice}
        <br><strong>Average: </strong>${history.averagePrice}
        <br><strong>Standard Deviation: </strong>${history.stdDev}
    </body>
</html>
