<?php
    if(!isset($_GET["id"])){
        echo "error: invalid input";
        exit(0);
    }
    
    $email = $_GET["id"];
    
    $con = mysqli_connect("localhost","freeprog_modify","cy@BAC2069","freeprog_mainDB");
    
    if($con==NULL){
        echo "error: failed to connect";
        exit(0);
    } else{
        
        $email = mysqli_real_escape_string($con,$email);
        
        $sql = "SELECT * FROM Users_food WHERE email='$email';";
        
        $result = $con->query($sql);
        
        if ($result->num_rows > 0){
            
            $row = $result->fetch_assoc();
            
            echo $row["mag_name"] . ":" . $row["location"];
            
        } else{
            echo "error: no such user";
            exit(0);
        }
        
    }
    
?>