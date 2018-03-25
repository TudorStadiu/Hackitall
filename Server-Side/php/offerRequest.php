<?php
   
   if(!isset($_GET["mag_name"]) || !isset($_GET["location"]) || !isset($_GET["description"]) || !isset($_GET["time"])|| !isset($_GET["img"])){
        echo "error: invalid input";
        exit(0);
    }
    
    $mag_name = $_GET["mag_name"];
    $img = $_GET["img"];
    $location = $_GET["location"];
    $description = $_GET["description"];
    $time = $_GET["time"];
    
    $con = mysqli_connect("localhost","freeprog_modify","cy@BAC2069","freeprog_mainDB");
    
    if($con==NULL){
        echo "error: failed to connect";
        exit(0);
    } else{
        
        $mag_name = mysqli_real_escape_string($con,$mag_name);
        $location = mysqli_real_escape_string($con,$location);
        $description = mysqli_real_escape_string($con,$description);
        $time = mysqli_real_escape_string($con,$time);
        
        $date = date("Y-m-d H:i:s",strtotime($time));
        
        $sql = "INSERT INTO Offers (mag_name, image, location, description, time) VALUES ('$mag_name', '$img', '$location', '$description', '$date');";
        $result = $con->query($sql);
        echo "success";
        exit(0);
    }
   
?>