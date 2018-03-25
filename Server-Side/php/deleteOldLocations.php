<?php

    function sameday($var1, $var2){
        if(!strcmp($var1[2],$var2[2]) && !strcmp($var1[1],$var2[1]) && !strcmp($var1[0],$var2[0]))
            return 1;
        return 0;
    }
    
    function lessthan2hours($var1, $var2){
        $diffH = intval($var1[0]) - intval($var2[0]);
        $diffM = intval($var1[1]) - intval($var2[1]);
        $diffS = intval($var1[2]) - intval($var2[2]);
        
        $totalDiff = $diffH * 3600 + $diffM * 60 + $diffS;
        
        if(abs($totalDiff) < 7200)
            return 1;
            
        return 0;
    }

    $con = mysqli_connect("localhost","freeprog_modify","cy@BAC2069","freeprog_mainDB");
    
    if($con==NULL){
        exit(0);
    } else{
        
        $sql = "SELECT * FROM Last_locations;";

        $result = $con->query($sql);
        
        if ($result->num_rows > 0){
            $currTime = date('Y-m-d H:i:s');;
            
            while($row = $result->fetch_assoc()) {
                
                $varTime =  $row["time"];

                $var1 = explode('-',explode(' ',($currTime))[0]);
                $var2 = explode('-',explode(' ',($varTime))[0]);
                
                if(sameday($var1, $var2) == 1){
                    $var12 = explode(':',explode(' ',($currTime))[1]);
                    $var22 = explode(':',explode(' ',($varTime))[1]);
                    if(lessthan2hours($var12, $var22) == 1)
                        continue;
                }

                $sql = "DELETE FROM Last_locations WHERE time = '$varTime'";
                $result2 = $con->query($sql);
            
                echo "Deleted: " .$varTime;
            }
        
        }
        
    }
?>