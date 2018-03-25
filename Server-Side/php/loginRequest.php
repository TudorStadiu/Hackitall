<?php
    if(!isset($_GET["email"]) || !isset($_GET["passwd"])){
        echo "error: invalid input";
        exit(0);
    }
    
    $email = $_GET["email"];
    $passwd = $_GET["passwd"];
    
    $con = mysqli_connect("localhost","freeprog_modify","cy@BAC2069","freeprog_mainDB");
    
    if($con==NULL){
        echo "error: failed to connect";
        exit(0);
    } else{
        
        $email = mysqli_real_escape_string($con,$email);
        $passwd = mysqli_real_escape_string($con,$passwd);
        
        $sql = "SELECT * FROM Users_food WHERE email='$email';";
        
        $result = $con->query($sql);
        
        if ($result->num_rows > 0){
            
            $row = $result->fetch_assoc();
            
            if(password_verify($passwd,$row["passwd"])){
                echo "success";
                exit(0);
            } else{
                echo "error: invalid password";
                exit(0);
            }
            
        } else{
            echo "error: no such user";
            exit(0);
        }
        
    }
    
?>