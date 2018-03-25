<?php
    
    if(isset($_GET["pass"]))
        if(strcmp($_GET["pass"],"hack")){
            $con = mysqli_connect("localhost","freeprog_modify","cy@BAC2069","freeprog_mainDB");
    
            if($con==NULL){
                exit(0);
            } else{
                $sql = "TRUNCATE TABLE Offers;";
                $result = $con->query($sql);
            }
        }
?>