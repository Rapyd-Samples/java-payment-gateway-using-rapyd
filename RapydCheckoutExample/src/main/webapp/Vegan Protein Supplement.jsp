<%-- 
    Document   : Vegan Protein Supplement.jsp
    Created on : 12 Dec 2022, 03:30:54
    Author     : Mduduzi Sibisi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vegan Protein Supplement</title>
</head>
<body>
    <div>
        <h1>Unflavored Vegan Protein Supplement</h1>
        <p>A vegan protein supplement that can be either taken with water as a shake or added to recipes.<br/> 
         Ideal for vegan athletes trying to build muscle. 
         Go from Vegan to Vegain with this pea and watermelon seed protein supplement! </p>
        <b>$10</b>
        <br>
        <form action="Purchase" method="POST">
            <input type="hidden" name="amount" value="10">
            <input type="submit" value="Purchase" name="submit">
        </form>
    </div>
</body>
</html>

