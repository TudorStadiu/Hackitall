<?php

    $con = mysqli_connect("localhost","freeprog_modify","cy@BAC2069","freeprog_mainDB");
    
    if($con==NULL){
        echo "error: could not load database";
        exit(0);
    } else{
        
        $sql = "SELECT * FROM Offers;";

        $result = $con->query($sql);
        
        if ($result->num_rows > 0){
            while($row = $result->fetch_assoc()) {
            
                echo $row["mag_name"] . "<br>" . $row["location"] . "<br>" . $row["description"] . "<br>" . $row["time"] . "<br>";
                $img = "uploads/" . $row["image"];
                echo "$img<br>";
                
            }
        }
    }

?>