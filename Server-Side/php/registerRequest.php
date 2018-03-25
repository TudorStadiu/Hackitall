<?php
   
   if(!isset($_GET["email"]) || !isset($_GET["passwd"]) || !isset($_GET["mag_name"]) || !isset($_GET["location"])){
        echo "error: invalid input";
        exit(0);
    }
    
    $email = $_GET["email"];
    $passwd = $_GET["passwd"];
    $mag_name = $_GET["mag_name"];
    $location = $_GET["location"];
    
    $con = mysqli_connect("localhost","freeprog_modify","cy@BAC2069","freeprog_mainDB");
    
    if($con==NULL){
        echo "error: failed to connect";
        exit(0);
    } else{
        
        $email = mysqli_real_escape_string($con,$email);
        $passwd = mysqli_real_escape_string($con,$passwd);
        $mag_name = mysqli_real_escape_string($con,$mag_name);
        $location = mysqli_real_escape_string($con,$location);
        
        $sql = "SELECT * FROM Users_food WHERE email='$email';";
        
        $result = $con->query($sql);
        
        if ($result->num_rows > 0){
            echo "error: email already exists";
            exit(0);
        } else{
            $hash = password_hash($passwd, PASSWORD_DEFAULT);
            $sql = "INSERT INTO Users_food (email, passwd, mag_name, location) VALUES ('$email','$hash', '$mag_name', '$location');";
            $result = $con->query($sql);
            if($result) {
                echo "success";
                exit(0);
            } else {
                echo "error : invalid sql statement";
                exit(0);
            }
        }
    }
   
?>